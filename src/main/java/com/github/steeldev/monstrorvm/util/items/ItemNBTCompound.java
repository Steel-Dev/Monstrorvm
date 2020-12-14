package com.github.steeldev.monstrorvm.util.items;


import java.util.List;

public class ItemNBTCompound {
    public String compoundKey;
    public ItemNBTType compoundType;
    public Object compoundValue;

    public ItemNBTCompound(String compoundKey,
                           Object compoundValue) {
        this.compoundKey = compoundKey;
        this.compoundValue = compoundValue;
        if (compoundValue instanceof Boolean)
            this.compoundType = ItemNBTType.BOOLEAN;
        else if (compoundValue instanceof Integer)
            this.compoundType = ItemNBTType.INTEGER;
        else if (compoundValue instanceof Float)
            this.compoundType = ItemNBTType.FLOAT;
        else if (compoundValue instanceof String)
            this.compoundType = ItemNBTType.STRING;
        else if (compoundValue instanceof Double)
            this.compoundType = ItemNBTType.DOUBLE;
    }

    public ItemNBTCompound(String compoundKey,
                           boolean compoundValue) {
        this.compoundKey = compoundKey;
        this.compoundValue = compoundValue;
        this.compoundType = ItemNBTType.BOOLEAN;
    }

    public ItemNBTCompound(String compoundKey,
                           int compoundValue) {
        this.compoundKey = compoundKey;
        this.compoundValue = compoundValue;
        this.compoundType = ItemNBTType.INTEGER;
    }

    public ItemNBTCompound(String compoundKey,
                           float compoundValue) {
        this.compoundKey = compoundKey;
        this.compoundValue = compoundValue;
        this.compoundType = ItemNBTType.FLOAT;
    }

    public ItemNBTCompound(String compoundKey,
                           String compoundValue) {
        this.compoundKey = compoundKey;
        this.compoundValue = compoundValue;
        this.compoundType = ItemNBTType.STRING;
    }

    public ItemNBTCompound(String compoundKey,
                           List<String> compoundValue) {
        this.compoundKey = compoundKey;
        this.compoundValue = compoundValue;
        this.compoundType = ItemNBTType.STRING;
    }

    public ItemNBTCompound(String compoundKey,
                           double compoundValue) {
        this.compoundKey = compoundKey;
        this.compoundValue = compoundValue;
        this.compoundType = ItemNBTType.DOUBLE;
    }
}
