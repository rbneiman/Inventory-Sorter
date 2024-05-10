package net.kyrptonaught.inventorysorter.sorttree;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.interfaces.SortableContainer;
import net.kyrptonaught.inventorysorter.sorttree.node.SortNodeGroup;
import net.kyrptonaught.inventorysorter.sorttree.node.SortTreeNode;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SortTree implements ISortTree{
    private static final Logger logger = InventorySorterMod.logger;

    private static SortTree INSTANCE;

    private SortTreeNode rootNode;
    private final ArrayList<SortTreeNode> nodes;
    private final HashMap<String, SortNodeGroup> idGroupMap;
//    private final HashMap<String, SortTreeNode> idNodeMap;

    private SortTree(){
        rootNode = null;
        nodes = new ArrayList<>();
        idGroupMap = new HashMap<>();
    }

    public static @NotNull SortTree fromInputStream(InputStream inputStream)
            throws IOException, ParserConfigurationException, SAXException {
        return TreeParser.fromInputStream(inputStream);
    }

    private SortKey getSortKey(ItemStack stack){
        String id = stack.getItem().toString();
        SortNodeGroup sortGroup = idGroupMap.get(id);
        return new SortKey(sortGroup, stack);
    }


    private int componentCompare(SortKey a, SortKey b){
        // TODO compare item data
        return 0;
    }

    private int compareSortKeys(SortKey a, SortKey b) {
        boolean validA = a.nodeGroup != null;
        boolean validB = b.nodeGroup != null;

        if(!validA){
            logger.debug("Invalid key, id '{}'", a.stack().getItem().toString());
        }
        if(!validB){
            logger.debug("Invalid key, id '{}'", b.stack().getItem().toString());
        }

        if(validA && validB){
            if(a.nodeGroup.equals(b.nodeGroup)){
                return a.nodeGroup.compareGroupNodes(a.stack,b.stack);
            }
            return a.nodeGroup.groupIndex.compareTo(b.nodeGroup.groupIndex);
        }else if(validA){
            return 1;
        }else if(validB){
            return -1;
        }else{
            return a.stack().getItem().toString().compareTo(b.stack.getItem().toString());
        }

    }

    @Override
    public void sortContainer(SortableContainer sortableContainer) {
    }

    @Override
    public Comparator<ItemStack> getComparator() {
        return Comparator.comparing(this::getSortKey, this::compareSortKeys);
    }

    public static void setInstance(SortTree tree){
        INSTANCE = tree;
    }

    public static Optional<ISortTree> getInstance(){
        return Optional.ofNullable(INSTANCE);
    }

    private record SortKey(SortNodeGroup nodeGroup, ItemStack stack) {
    }

    private static class TreeParser extends DefaultHandler {
        private final Stack<SortTreeNode> nodeStack;
        private final SortTree tree;
        private int nodeIndex;
        boolean foundRoot;

        private TreeParser(SortTree tree){
            this.nodeStack = new Stack<>();
            this.foundRoot = false;
            this.tree = tree;
            nodeIndex = 0;
        }

        private void doVisitors(){
            SortTreeVisitor idVisitor = new SortTreeVisitor.IDVisitor(tree.idGroupMap);
            tree.rootNode.accept(idVisitor);
        }

        public static SortTree fromInputStream(InputStream inputStream)
                throws IOException, ParserConfigurationException, SAXException {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            SortTree sortTree = new SortTree();
            TreeParser treeParser = new TreeParser(sortTree);
            xmlReader.setContentHandler(treeParser);
            xmlReader.parse(new InputSource(inputStream));
            treeParser.doVisitors();
            return sortTree;
        }

        /**
         * Receive notification of the start of an element.
         *
         * <p>By default, do nothing.  Application writers may override this
         * method in a subclass to take specific actions at the start of
         * each element (such as allocating a new tree node or writing
         * output to a file).</p>
         *
         * @param uri The Namespace URI, or the empty string if the
         *        element has no Namespace URI or if Namespace
         *        processing is not being performed.
         * @param localName The local name (without prefix), or the
         *        empty string if Namespace processing is not being
         *        performed.
         * @param qName The qualified name (with prefix), or the
         *        empty string if qualified names are not available.
         * @param attributes The attributes attached to the element.  If
         *        there are no attributes, it shall be an empty
         *        Attributes object.
         * @throws org.xml.sax.SAXException Any SAX exception, possibly
         *            wrapping another exception.
         * @see org.xml.sax.ContentHandler#startElement
         */
        @Override
        public void startElement (String uri, String localName,
                                  String qName, Attributes attributes)
                throws SAXException {

            SortTreeNode node = new SortTreeNode(nodeIndex, qName, attributes);
            ++nodeIndex;

            if(!this.foundRoot){
                tree.rootNode = node;
                this.foundRoot = true;
            }
            tree.nodes.add(node);

            if(!nodeStack.empty()){
                nodeStack.peek().addChild(node);
            }
            nodeStack.push(node);
        }

        /**
         * Receive notification of the end of an element.
         *
         * <p>By default, do nothing.  Application writers may override this
         * method in a subclass to take specific actions at the end of
         * each element (such as finalising a tree node or writing
         * output to a file).</p>
         *
         * @param uri The Namespace URI, or the empty string if the
         *        element has no Namespace URI or if Namespace
         *        processing is not being performed.
         * @param localName The local name (without prefix), or the
         *        empty string if Namespace processing is not being
         *        performed.
         * @param qName The qualified name (with prefix), or the
         *        empty string if qualified names are not available.
         * @throws org.xml.sax.SAXException Any SAX exception, possibly
         *            wrapping another exception.
         * @see org.xml.sax.ContentHandler#endElement
         */
        @Override
        public void endElement (String uri, String localName, String qName)
                throws SAXException {
            SortTreeNode node = nodeStack.pop();
            node.finalizeNode();
        }


    }
}
