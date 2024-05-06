package net.kyrptonaught.inventorysorter;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SortCases {
    static boolean registered = false;
    public enum SortType {
        NAME, CATEGORY, MOD, ID;

        public String getTranslationKey() {
            return "key." + InventorySorterMod.MOD_ID + ".sorttype." + this.toString().toLowerCase();
        }
    }

    static String getStringForSort(ItemStack stack, SortType sortType) {
        Item item = stack.getItem();
        String itemName = specialCases(stack);
        switch (sortType) {
            case CATEGORY -> {
                ItemGroup group = getFirstItemGroup(stack);
                return (group != null ? group.getDisplayName().getString() : "zzz") + itemName;
            }
            case MOD -> {

                return Registries.ITEM.getId(item).getNamespace() + itemName;
            }
            case NAME -> {
                if (stack.contains(DataComponentTypes.CUSTOM_NAME)) return stack.getName() + itemName;
            }
        }


        return itemName;
    }

    private static ItemGroup getFirstItemGroup(ItemStack stack) {
        List<ItemGroup> groups = ItemGroups.getGroups();
        for (ItemGroup group : groups) {
            if (group.contains(new ItemStack(stack.getItem())))
                return group;

        }
        return null;
    }

    private static String specialCases(ItemStack stack) {
        Item item = stack.getItem();
//        DynamicRegistryManager registryManager = stack.getHolder().getRegistryManager();
//        NbtElement element = stack.encode(registryManager);
//
//        if (stack.contains(DataComponentTypes.CUSTOM_DATA)){
//            NbtComponent tag = stack.get(DataComponentTypes.CUSTOM_DATA);
//            if(tag != null && tag.contains("SkullOwner")){
//                return playerHeadCase(stack);
//            }
//        }
        if(item instanceof PlayerHeadItem playerHeadItem){
            return playerHeadCase(stack, playerHeadItem);
        }
        if (stack.getCount() != stack.getMaxCount())
            return stackSize(stack);
        if (item instanceof EnchantedBookItem)
            return enchantedBookNameCase(stack);
        if (item instanceof ToolItem)
            return toolDuribilityCase(stack);
        return item.toString();
    }

    private static String playerHeadCase(ItemStack stack, PlayerHeadItem headItem) {
//        NbtCompound tag = stack.getNbt();
//        NbtCompound skullOwner = component.get(Registries.ENTITY_TYPE.getCodec().fieldOf("id"););
//        String ownerName = skullOwner.getString("Name");

        String ownerName = headItem.getName(stack).getString();

        // this is duplicated logic, so we should probably refactor
        String count = "";
        if (stack.getCount() != stack.getMaxCount()) {
            count = Integer.toString(stack.getCount());
        }

        return stack.getItem().toString() + " " + ownerName + count;
    }

    private static String stackSize(ItemStack stack) {
        return stack.getItem().toString() + stack.getCount();
    }

    private static String enchantedBookNameCase(ItemStack stack) {
//        NbtList enchants = EnchantedBookItem.getEnchantmentNbt(stack);
        ItemEnchantmentsComponent enchantmentsComponent = stack.getEnchantments();
        Set<RegistryEntry<Enchantment>> enchantments = enchantmentsComponent.getEnchantments();

        List<String> names = new ArrayList<>();
        StringBuilder enchantNames = new StringBuilder();

        for(RegistryEntry<Enchantment> entry : enchantments){
            Enchantment enchantment = entry.value();
            names.add(enchantment.getName(enchantmentsComponent.getLevel(enchantment)).getString());
        }

//        for (int i = 0; i < enchantments.size(); i++) {
//            NbtCompound enchantTag = enchants.getCompound(i);
//            Identifier enchantID = Identifier.tryParse(enchantTag.getString("id"));
//            if (enchantID == null) continue;
//            Enchantment enchant = Registries.ENCHANTMENT.get(enchantID);
//            if (enchant == null) continue;
//            names.add(enchant.getName(enchantTag.getInt("lvl")).getString());
//        }
        Collections.sort(names);
        for (String enchant : names) {
            enchantNames.append(enchant).append(" ");
        }
        return stack.getItem().toString() + " " + enchantmentsComponent.getSize() + " " + enchantNames;
    }

    private static String toolDuribilityCase(ItemStack stack) {
        return stack.getItem().toString() + stack.getDamage();
    }
}
