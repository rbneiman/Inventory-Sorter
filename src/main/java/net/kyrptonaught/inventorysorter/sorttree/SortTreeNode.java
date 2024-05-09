package net.kyrptonaught.inventorysorter.sorttree;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.HashMap;

public class SortTreeNode {
    public final String name;
    public final HashMap<String, String> attributes;
    public final ArrayList<SortTreeNode> children;
    private boolean finalized;

    public SortTreeNode(String name, Attributes attributes){
        this.name = name;

        children = new ArrayList<>();
        this.finalized = false;

        this.attributes = new HashMap<>();
        for(int i=0; i<attributes.getLength(); ++i){
            String attribName = attributes.getQName(i);
            String value = attributes.getValue(i);
            this.attributes.put(attribName, value);
        }
    }

    public void finalizeNode(){
        finalized = true;
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
