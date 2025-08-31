package com.jcardozo.easyspends.enums;

public enum PackagingUnit {
    KG(1, "KILOGRAM"),
    UN(2, "UNIT"),
    L(3, "LITER");

    private final int id;
    private final String name;

    PackagingUnit(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
