package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SimpleConstraint implements DataConstraint{
    private final String name;
    private final SortTreeNode node;
    private final String rValue;
    private final Satisfyer satisfyer;

    public SimpleConstraint(String name, @NotNull SortTreeNode node, @NotNull String rValue, Satisfyer satisfyer){
        this.name = name;
        this.node = node;
        this.rValue = rValue;
        this.satisfyer = satisfyer;
    }

    @Override
    public SortTreeNode getNode() {
        return null;
    }

    public boolean satisfiesConstraint(ItemStack itemStack) {
        return satisfyer.satisfies(itemStack, rValue);
    }

    @Override
    public DataConstraintGroup constructGroup() {
        return new SimpleConstraintGroup(name);
    }

    public interface Satisfyer{

        boolean satisfies(ItemStack stack, String rValue);
    }

    public static void register(String name, Satisfyer satisfyer){
        DataConstraints.registerConstraint(name, (n,r)->new SimpleConstraint(name, n,r, satisfyer));
    }
}
