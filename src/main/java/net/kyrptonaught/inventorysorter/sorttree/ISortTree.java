package net.kyrptonaught.inventorysorter.sorttree;

import net.kyrptonaught.inventorysorter.interfaces.SortableContainer;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public interface ISortTree {

    void sortContainer(SortableContainer sortableContainer);

    Comparator<ItemStack> getComparator();
}
