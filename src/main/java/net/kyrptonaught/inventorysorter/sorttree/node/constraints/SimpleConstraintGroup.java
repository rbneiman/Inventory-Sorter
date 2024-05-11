package net.kyrptonaught.inventorysorter.sorttree.node.constraints;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.sorttree.node.SortNodeData;
import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleConstraintGroup implements DataConstraintGroup{
    private static final Logger logger = InventorySorterMod.logger;

    private final String name;
    private final ArrayList<DataConstraint> nodeConstraints;

    SimpleConstraintGroup(String name){
        this.name = name;
        this.nodeConstraints = new ArrayList<>();
    }

    public Collection<SortTreeNode> getSatisfyingNodes(ItemStack itemStack){
        ArrayList<SortTreeNode> nodesOut = new ArrayList<>();
        for(DataConstraint constraint : nodeConstraints){
            if(constraint.satisfiesConstraint(itemStack)){
                nodesOut.add(constraint.getNode());
            }
        }
        return nodesOut;
    }

    public Collection<SortTreeNode> filterSatisfyingNodes(Collection<SortTreeNode> nodes, ItemStack itemStack){
        ArrayList<SortTreeNode> nodesOut = new ArrayList<>();
        for(SortTreeNode node : nodes){
            SortNodeData data = node.getData();
            if(!data.hasConstraint(name)){
                nodesOut.add(node);
                continue;
            }

            if(data.getConstraint(name).satisfiesConstraint(itemStack)){
                nodesOut.add(node);
            }
        }
        return nodesOut;
    }

    public void addNode(SortTreeNode node){
        SortNodeData data = node.getData();
        if(!data.hasConstraint(name)){
            return;
        }
        nodeConstraints.add(data.getConstraint(name));
    }

//    public boolean satisfiesConstraint(SortTreeNode node, ItemStack itemStack){
//        Optional<SortNodeData> data = node.getData();
//        if(data.isEmpty()){
//            logger.warn("Empty data for node {}", node);
//            return false;
//        }
//        return satisfyer.satisfiesConstraint(node, itemStack);
//    }
}
