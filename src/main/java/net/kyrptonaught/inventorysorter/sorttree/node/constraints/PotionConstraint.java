package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.sorttree.node.SortNodeData;
import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PotionConstraint implements DataConstraint{ //, DataConstraints.DataConstraintMaker{
    public final static String NAME = "Potion";
    private final String potionName;
    private final SortTreeNode node;

    public PotionConstraint(@NotNull SortTreeNode node, @NotNull String potionName) {
        this.node = node;
        this.potionName = potionName;
    }

    public boolean satisfiesConstraint(ItemStack itemStack) {
        PotionContentsComponent contents = itemStack.getComponents().get(DataComponentTypes.POTION_CONTENTS);
        if(contents == null || contents.potion().isEmpty()){
            return false;
        }
        String itemPotionStr = contents.potion().get().getIdAsString();

        return potionName.equals(itemPotionStr);
    }

    @Override
    public DataConstraintGroup constructGroup() {
        return new SimpleConstraintGroup(NAME);
    }

    public SortTreeNode getNode(){
        return node;
    }

//    @Override
//    public DataConstraint construct(String rValue) {
//        return new SimpleConstraint(this);
//    }

}
