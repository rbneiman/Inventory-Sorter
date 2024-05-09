package net.kyrptonaught.inventorysorter.sorttree;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public abstract class SortTreeVisitor {
    private static final Logger logger = InventorySorterMod.logger;

    public void startVisit(SortTreeNode node){

    }

    public void endVisit(SortTreeNode node){

    }

    public static class IDVisitor extends SortTreeVisitor{
        private final HashMap<String, Integer> indexMap;
        private final HashMap<String, SortTreeNode> nodeMap;
        private int index;

        public IDVisitor(HashMap<String, Integer> indexMap, HashMap<String, SortTreeNode> nodeMap){
            this.indexMap = indexMap;
            this.nodeMap = nodeMap;
            index = 0;
        }

        public void startVisit(SortTreeNode node){
            if(!node.children.isEmpty() || !node.attributes.containsKey("id")) return;
            String id = node.attributes.get("id");

            if(nodeMap.containsKey(id)){
                logger.debug("Ignoring duplicate id in sort tree: {}", id);
                return;
            }

            indexMap.put(id, index);
            nodeMap.put(id,node);
            index++;
        }
    }
}
