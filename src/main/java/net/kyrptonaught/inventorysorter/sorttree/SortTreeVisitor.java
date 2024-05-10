package net.kyrptonaught.inventorysorter.sorttree;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.sorttree.node.SortNodeGroup;
import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public abstract class SortTreeVisitor {
    private static final Logger logger = InventorySorterMod.logger;

    public void startVisit(SortTreeNode node){

    }

    public void endVisit(SortTreeNode node){

    }

    public static class IDVisitor extends SortTreeVisitor{
        private final HashMap<String, SortNodeGroup> idGroupMap;
//        private final HashMap<String, SortTreeNode> nodeMap;
        private int groupIndex;

        public IDVisitor(HashMap<String, SortNodeGroup> idGroupMap){
            this.idGroupMap = idGroupMap;
            groupIndex = 0;
        }

        public void startVisit(SortTreeNode node){
            if(!node.children.isEmpty() || !node.attributes.containsKey("id")) return;
            String id = node.attributes.get("id");

            SortNodeGroup group;
            if(idGroupMap.containsKey(id)){
//                logger.debug("Ignoring duplicate id in sort tree: {}", id);
//                return;
                group = idGroupMap.get(id);
            }else{
                group = new SortNodeGroup(groupIndex, id);
                idGroupMap.put(id, group);
                ++groupIndex;
            }

            group.addNode(node);
        }
    }
}
