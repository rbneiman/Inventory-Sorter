package net.kyrptonaught.inventorysorter.sorttree.node;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class SortNodeGroup {
    public final Integer groupIndex;
    public final String sharedId;
    public final ArrayList<SortTreeNode> nodes;

    public SortNodeGroup(Integer groupIndex, String sharedId) {
        this.groupIndex = groupIndex;
        this.sharedId = sharedId;
        this.nodes = new ArrayList<>();
    }

    public void addNode(SortTreeNode node) {
        nodes.add(node);
    }

    public int compareGroupNodes(ItemStack a, ItemStack b){
        // a.getComponents().get(DataComponentTypes.POTION_CONTENTS).toString()

        return 0;
    }
}
