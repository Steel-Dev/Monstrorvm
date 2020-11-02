package com.github.steeldev.monstrorvm.util.items;

import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;

public class ItemAttributeInfo {
    public String attributeModName;
    public EquipmentSlot slot;
    public Attribute attributeMod;
    public double attributeValue;

    public ItemAttributeInfo(String attributeModName,
                             EquipmentSlot slot,
                             Attribute attributeMod,
                             double attributeValue) {
        this.attributeModName = attributeModName;
        this.slot = slot;
        this.attributeMod = attributeMod;
        this.attributeValue = attributeValue;
    }
}
