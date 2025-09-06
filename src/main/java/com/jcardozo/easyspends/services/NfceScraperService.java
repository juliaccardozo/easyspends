package com.jcardozo.easyspends.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcardozo.easyspends.entities.Product;
import com.jcardozo.easyspends.entities.Purchase;
import com.jcardozo.easyspends.entities.PurchaseItem;
import com.jcardozo.easyspends.enums.PackagingUnit;
import com.jcardozo.easyspends.exceptions.NfceProcessingException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class NfceScraperService {

    public Purchase extract(String receiptJson) {
        String url = extractUrl(receiptJson)
                .orElseThrow(() -> new NfceProcessingException("Invoice URL not found in JSON."));

        try {
            Document doc = Jsoup.connect(url).get();

            Purchase purchase = parsePurchaseContent(doc.getElementById("conteudo"));
            parsePurchaseInfo(doc.getElementById("infos"), purchase);

            return purchase;
        } catch (IOException e) {
            throw new NfceProcessingException("Error connecting to invoice URL", e);
        }
    }

    private Optional<String> extractUrl(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return Optional.ofNullable(jsonObject.get("url")).map(JsonElement::getAsString);
    }

    private Purchase parsePurchaseContent(Element nfceElement) {
        if (nfceElement == null) {
            throw new NfceProcessingException("Invoice content section not found.");
        }

        Purchase purchase = new Purchase();

        purchase.setStablishmentName(safeText(nfceElement, "u20"));

        purchase.setCnpj(safeClassText(nfceElement, "text", 0, "00000000000000")
                .replaceAll("\\D", ""));

        Element totalElement = nfceElement.getElementById("totalNota");
        if (totalElement == null) {
            throw new NfceProcessingException("Invoice total section not found.");
        }

        Elements lines = totalElement.getElementsByAttributeValue("id", "linhaTotal");
        for (Element linha : lines) {
            String label = linha.getElementsByTag("label").text().trim();
            String value = linha.getElementsByTag("span").text().trim();

            if (label.startsWith("Qtd. total de itens")) {
                purchase.setTotalItensQuantity(toInteger(value));
            } else if (label.startsWith("Valor total R$")) {
                purchase.setTotalValue(toBigDecimal(value));
            } else if (label.startsWith("Descontos R$")) {
                purchase.setDiscount(toBigDecimal(value));
            } else if (label.startsWith("Valor a pagar R$")) {
                purchase.setTotalPayment(toBigDecimal(value));
            }
        }

        Elements receiptProducts = nfceElement.getElementsByTag("tr");
        if (receiptProducts.isEmpty()) {
            throw new NfceProcessingException("No products found in invoice.");
        }

        List<PurchaseItem> items = new ArrayList<>();
        for (Element item : receiptProducts) {
            items.add(parseItem(item, purchase));
        }

        purchase.setItems(items);
        return purchase;
    }

    private PurchaseItem parseItem(Element item, Purchase purchase) {
        PurchaseItem purchaseItem = new PurchaseItem();
        Product product = new Product();

        product.setName(safeClassText(item, "txtTit2", 0, "UNKNOWN"));
        product.setCode(safeClassText(item, "RCod", 0, "0").replaceAll("\\D", ""));
        product.setPackagingUnit(resolvePackagingUnit(safeClassText(item, "RUN", 0, "UNDEFINED")));

        purchaseItem.setPurchase(purchase);
        purchaseItem.setProduct(product);
        purchaseItem.setQuantity(toBigDecimal(safeClassText(item, "Rqtd", 0, "0")));
        purchaseItem.setUnitPrice(toBigDecimal(safeClassText(item, "RvlUnit", 0, "0")));
        purchaseItem.setTotalPrice(toBigDecimal(safeClassText(item, "valor", 0, "0")));

        return purchaseItem;
    }

    private void parsePurchaseInfo(Element infoElement, Purchase purchase) {
        if (infoElement == null) {
            throw new NfceProcessingException("Invoice info section not found.");
        }

        String accessKey = Optional.of(infoElement.getElementsByClass("chave"))
                .map(Elements::text).filter(s -> !s.isBlank())
                .orElseThrow(() -> new NfceProcessingException("Access key not found in invoice."));
        purchase.setAccessKey(accessKey);

        Elements liElements = infoElement.getElementsByTag("li");
        if (!liElements.isEmpty() && liElements.get(0).childNodeSize() > 11) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            String rawDate = liElements.get(0).childNode(11).toString().split(" - ")[0];
            LocalDateTime issueDate = LocalDateTime.parse(rawDate, formatter);

            purchase.setIssueDate(issueDate);
        } else {
            purchase.setIssueDate(LocalDateTime.now());
        }
    }

    private String safeText(Element parent, String id) {
        return Optional.ofNullable(parent.getElementById(id))
                .map(Element::text)
                .orElse("UNKNOWN");
    }

    private String safeClassText(Element parent, String className, int index, String defaultValue) {
        Elements elements = parent.getElementsByClass(className);
        if (elements.size() > index) {
            return elements.get(index).text();
        }
        return defaultValue;
    }

    private String resolvePackagingUnit(String text) {
        try {
            return PackagingUnit.valueOf(text.split(": ")[1]).getName();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private BigDecimal toBigDecimal(String text) {
        if (text == null || text.isBlank()) return BigDecimal.ZERO;
        try {
            String clean = text.replaceAll("[^\\d,]", "").replace(",", ".");
            return new BigDecimal(clean);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Integer toInteger(String text) {
        if (text == null || text.isBlank()) return 0;
        try {
            String clean = text.replaceAll("[^\\d,]", "").replace(",", ".");
            return Integer.parseInt(clean);
        } catch (Exception e) {
            return 0;
        }
    }
}
