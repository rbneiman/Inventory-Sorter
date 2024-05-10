package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class SimpleConstraint implements DataConstraint{
    private final Satisfyer satisfyer;
    private final ArrayList<SortTreeNode> nodes;

    SimpleConstraint(Satisfyer satisfyer){
        this.satisfyer = satisfyer;
        this.nodes = new ArrayList<>();
    }

    public Iterable<SortTreeNode> getSatisfyingNodes(ItemStack itemStack){
        ArrayList<SortTreeNode> nodesOut = new ArrayList<>();
        for(SortTreeNode node : nodes){
            if(satisfyer.satisfiesConstraint(node, itemStack)){
                nodesOut.add(node);
            }
        }
        return nodesOut;
    }

    public Iterable<SortTreeNode> filterSatisfyingNodes(Iterable<SortTreeNode> nodes, ItemStack itemStack){
        ArrayList<SortTreeNode> nodesOut = new ArrayList<>();
        for(SortTreeNode node : nodes){
            if(satisfyer.satisfiesConstraint(node, itemStack)){
                nodesOut.add(node);
            }
        }
        return nodesOut;
    }

    public void addNode(SortTreeNode node){
        nodes.add(node);
    }

    public boolean satisfiesConstraint(SortTreeNode node, ItemStack itemStack){
        return satisfyer.satisfiesConstraint(node, itemStack);
    }

    public interface Satisfyer{
        boolean satisfiesConstraint(SortTreeNode node, ItemStack itemStack);
    }
}
