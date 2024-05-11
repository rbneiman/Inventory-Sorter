package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface DataConstraintGroup {

    Collection<SortTreeNode> getSatisfyingNodes(ItemStack itemStack);
    Collection<SortTreeNode> filterSatisfyingNodes(Collection<SortTreeNode> nodes, ItemStack itemStack);
    void addNode(SortTreeNode node);
}
