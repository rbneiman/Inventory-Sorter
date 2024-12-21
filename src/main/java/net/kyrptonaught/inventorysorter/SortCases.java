package net.kyrptonaught.inventorysorter;

import net.kyrptonaught.inventorysorter.sorttree.ISortTree;
import net.kyrptonaught.inventorysorter.sorttree.SortTree;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;

public class SortCases {
    private static final Logger logger = InventorySorterMod.logger;

    public enum SortType {
        NAME, CATEGORY, MOD, ID, TREE;

        public String getTranslationKey() {
            return "key." + InventorySorterMod.MOD_ID + ".sorttype." + this.toString().toLowerCase();
        }
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

        if(item instanceof PlayerHeadItem playerHeadItem){
            return playerHeadCase(stack, playerHeadItem);
        }
        if (stack.getCount() != stack.getMaxCount())
            return stackSize(stack);
        if (stack.isEnchantable())
            return enchantedBookNameCase(stack);
        if (stack.isDamageable())
            return toolDuribilityCase(stack);
        return item.toString();
    }

    private static String playerHeadCase(ItemStack stack, PlayerHeadItem headItem) {
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
        ItemEnchantmentsComponent enchantmentsComponent = stack.getEnchantments();
        Set<RegistryEntry<Enchantment>> enchantments = enchantmentsComponent.getEnchantments();

        List<String> names = new ArrayList<>();
        StringBuilder enchantNames = new StringBuilder();

        for(RegistryEntry<Enchantment> entry : enchantments){
//            Enchantment enchantment = entry.value();
            names.add(Enchantment.getName(entry, enchantmentsComponent.getLevel(entry)).getString());
        }

        Collections.sort(names);
        for (String enchant : names) {
            enchantNames.append(enchant).append(" ");
        }
        return stack.getItem().toString() + " " + enchantmentsComponent.getSize() + " " + enchantNames;
    }

    private static String toolDuribilityCase(ItemStack stack) {
        return stack.getItem().toString() + stack.getDamage();
    }

    public static Comparator<ItemStack> getComparatorForSortType(SortType sortType){
        if(sortType == SortType.TREE){
            Optional<ISortTree> instance = SortTree.getInstance();
            if(instance.isPresent()){
                return instance.get().getComparator();
            }else{
                logger.warn("Tried to get comparator for sort tree, but no instance available. Defaulting to SortType.ID.");
                sortType = SortType.ID;
            }

        }

        Function<ItemStack, String> sortStringGetter = null;
        switch (sortType){
            case ID -> {
                sortStringGetter = SortCases::specialCases;
            }
            case NAME -> {
                sortStringGetter =  stack -> {
                    String itemName = specialCases(stack);
                    if (stack.contains(DataComponentTypes.CUSTOM_NAME)) return stack.getName() + itemName;
                    else return itemName;
                };
            }
            case CATEGORY -> {
                sortStringGetter =  stack -> {
                    String itemName = specialCases(stack);
                    ItemGroup group = getFirstItemGroup(stack);
                    return (group != null ? group.getDisplayName().getString() : "zzz") + itemName;
                };
            }
            case MOD -> {
                sortStringGetter =  stack -> {
                    Item item = stack.getItem();
                    String itemName = specialCases(stack);
                    return Registries.ITEM.getId(item).getNamespace() + itemName;
                };
            }
        }
        return Comparator.comparing(sortStringGetter);
    }

}
