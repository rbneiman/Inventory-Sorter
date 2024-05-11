package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.sorttree.node.SortNodeData;
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

    SortTreeNode getNode();
    boolean satisfiesConstraint(ItemStack itemStack);
    DataConstraintGroup constructGroup();
}
