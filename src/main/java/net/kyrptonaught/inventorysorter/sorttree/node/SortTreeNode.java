package net.kyrptonaught.inventorysorter.sorttree.node;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.sorttree.SortTreeVisitor;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class SortTreeNode {
    private static final Logger logger = InventorySorterMod.logger;

    public final int index;
    public final String name;
    public final HashMap<String, String> attributes;
    public final ArrayList<SortTreeNode> children;
    private SortNodeData data;

    public SortTreeNode(int index, String name, Attributes attributes){
        this.index = index;
        this.name = name;
        this.data = null;

        children = new ArrayList<>();

        this.attributes = new HashMap<>();
        for(int i=0; i<attributes.getLength(); ++i){
            String attribName = attributes.getQName(i);
            String value = attributes.getValue(i);
            if(attribName.equals("data")){
                if(data == null){
                    data = new SortNodeData(this, value);
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

    public Optional<SortNodeData> getData(){
        return Optional.ofNullable(data);
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
