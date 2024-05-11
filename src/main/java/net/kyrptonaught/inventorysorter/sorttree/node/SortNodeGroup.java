package net.kyrptonaught.inventorysorter.sorttree.node;

import net.kyrptonaught.inventorysorter.sorttree.node.constraints.DataConstraint;
import net.kyrptonaught.inventorysorter.sorttree.node.constraints.DataConstraintGroup;
import net.minecraft.item.ItemStack;

import java.util.*;

public class SortNodeGroup {
    public final Integer groupIndex;
    public final String sharedId;
    public final ArrayList<SortTreeNode> nodes;
    public final HashMap<String, DataConstraintGroup> constraintGroups;

    public SortNodeGroup(Integer groupIndex, String sharedId) {
        this.groupIndex = groupIndex;
        this.sharedId = sharedId;
        this.nodes = new ArrayList<>();
        this.constraintGroups = new HashMap<>();
    }

    public void addNode(SortTreeNode node) {
        nodes.add(node);

        for(Map.Entry<String, DataConstraint> entry: node.getData().getConstraintMap().entrySet()){
            String name = entry.getKey();
            DataConstraint constraint = entry.getValue();
            if(constraintGroups.containsKey(name)){
                constraintGroups.get(name).addNode(node);
            }else{
                DataConstraintGroup group = constraint.constructGroup();
                group.addNode(node);
                constraintGroups.put(name, group);
            }
        }

    }

    private Optional<SortTreeNode> findSatisfyingNode(ItemStack itemStack){
        if(constraintGroups.isEmpty()){ // no constraints, any stack should satisfy
            return Optional.ofNullable(nodes.getFirst());
        }
        Iterator<Map.Entry<String, DataConstraintGroup>> entryIt = constraintGroups.entrySet().iterator();
        Collection<SortTreeNode> nodes = entryIt.next().getValue().getSatisfyingNodes(itemStack);
        while (entryIt.hasNext() && nodes.size() > 1){
            nodes = entryIt.next().getValue().filterSatisfyingNodes(nodes, itemStack);
        }

        if(nodes.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(nodes.iterator().next());
        }
    }

    public int compareGroupNodes(ItemStack a, ItemStack b){
        Optional<SortTreeNode> nodeA = findSatisfyingNode(a);
        Optional<SortTreeNode> nodeB = findSatisfyingNode(b);

        if(nodeA.isPresent() && nodeB.isPresent()){
            return Integer.compare(nodeA.get().index, nodeB.get().index);
        }else if(nodeA.isPresent()){
            return 1;
        }else if(nodeB.isPresent()){
            return -1;
        }else{
            return 0;
        }
    }
}
