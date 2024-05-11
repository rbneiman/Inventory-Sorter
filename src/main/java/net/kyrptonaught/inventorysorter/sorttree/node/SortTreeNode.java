package net.kyrptonaught.inventorysorter.sorttree.node;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.sorttree.SortTreeVisitor;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.HashMap;

public class SortTreeNode {
    private static final Logger logger = InventorySorterMod.logger;

    public final int index;
    public final String name;
    public final HashMap<String, String> attributes;
    public final ArrayList<SortTreeNode> children;
    private final SortNodeData data;

    public SortTreeNode(int index, String name, Attributes attributes){
        this.index = index;
        this.name = name;
        this.data = new SortNodeData(this);

        children = new ArrayList<>();
        boolean hasData = false;
        this.attributes = new HashMap<>();
        for(int i=0; i<attributes.getLength(); ++i){
            String attribName = attributes.getQName(i);
            String value = attributes.getValue(i);
            if(attribName.equals("data")){
                if(!hasData){
                    boolean res = data.parseDataStr(value);
                    if(!res){
                        logger.warn("Failed to parse data string {}", value);
                    }
                    hasData = true;
                }else{
                    logger.warn("SortNode {} has multiple data attributes. Ignoring...", name);
                }
            }
            this.attributes.put(attribName, value);
        }
    }

    public void finalizeNode(){

    }

    public void addChild(SortTreeNode child){
        children.add(child);
    }

    public void accept(SortTreeVisitor visitor){
        visitor.startVisit(this);
        for(SortTreeNode child : children){
            child.accept(visitor);
        }
        visitor.endVisit(this);
    }

    public SortNodeData getData(){
        return data;
    }

    @Override
    public String toString(){
        return name;
    }

    private static class Attrib{
        public final String key;
        public final String value;

        public Attrib(String key, String value){
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString(){
            return key + "='" + value + "'";
        }
    }
}
