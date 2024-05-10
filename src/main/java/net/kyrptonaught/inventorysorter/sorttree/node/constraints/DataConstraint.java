package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.item.ItemStack;

public interface DataConstraint {
    enum ComparisonType{
        EQUALITY,
        INEQUALITY_LT,
        INEQUALITY_LEQ,
        INEQUALITY_GT,
        INEQUALITY_GEQ,
        CONTAINS,
        NOT_CONTAINS,
    }

    Iterable<SortTreeNode> getSatisfyingNodes(ItemStack itemStack);
    Iterable<SortTreeNode> filterSatisfyingNodes(Iterable<SortTreeNode> nodes, ItemStack itemStack);
    boolean satisfiesConstraint(SortTreeNode node, ItemStack itemStack);
    void addNode(SortTreeNode node);
}
