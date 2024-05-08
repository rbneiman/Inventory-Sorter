package net.kyrptonaught.inventorysorter.sorttree;

import net.kyrptonaught.inventorysorter.InventorySorterMod;
import net.kyrptonaught.inventorysorter.interfaces.SortableContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SortTree implements ISortTree{
    private static final Logger logger = InventorySorterMod.logger;

    private static SortTree INSTANCE;

    private SortTreeNode rootNode;
    private final ArrayList<SortTreeNode> nodes;
    private final HashMap<String, Integer> simpleItemMapping;

    private SortTree(){
        rootNode = null;
        nodes = new ArrayList<>();
        simpleItemMapping = new HashMap<>();
    }

    public static SortTree fromInputStream(InputStream inputStream)
            throws IOException, ParserConfigurationException, SAXException {
        return TreeParser.fromInputStream(inputStream);
    }

    private int getSimpleItemSlot(Item item){
        return 0;
    }


    private int compareItems(Item a, Item b) {
        return 0;
    }

    @Override
    public void sortContainer(SortableContainer sortableContainer) {
    }

    @Override
    public Comparator<ItemStack> getComparator() {
        return null;
    }

    public static void setInstance(SortTree tree){
        INSTANCE = tree;
    }

    public static Optional<ISortTree> getInstance(){
        return Optional.ofNullable(INSTANCE);
    }

    private static class TreeParser extends DefaultHandler {
        private final SortTree tree;

        private TreeParser(SortTree tree){
            this.tree = tree;
        }

        public static SortTree fromInputStream(InputStream inputStream)
                throws IOException, ParserConfigurationException, SAXException {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            SortTree sortTree = new SortTree();
            xmlReader.setContentHandler(new TreeParser(sortTree));
            xmlReader.parse(new InputSource(inputStream));
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
            // no op
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
            // no op
        }


    }
}
