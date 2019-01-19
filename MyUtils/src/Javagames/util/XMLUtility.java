package Javagames.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class XMLUtility {
    //xml文件解析工具包
    //parseDocument()方法接受一个InputStream或一个Reader，返回一个Document对象，包含了内存中的整个XML文件
    //getElement()和getAllElement()方法返回具有给定标记名称的元素
    //getElement()只是在一个层次深度上查找该标记，而getAllElement()返回拥有给定名称的任何标记而不管它们在文档中位于何处
    public static Document parseDocument(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(inputStream));
        return document;
    }

    public static Document parseDocument(Reader reader)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(reader));
        return document;
    }

    //Element 接口表示 HTML 或 XML 文档中的一个元素
    public static List<Element> getAllElements(
            Element element, String tagName) {
        ArrayList<Element> elements = new ArrayList<Element>();
        //NodeList getElementsByTagName(String name)以文档顺序返回
        // 具有给定标记名称的所有后代 Elements 的 NodeList
        NodeList nodes = element.getElementsByTagName(tagName);
        for(int i=0;i<nodes.getLength();i++) {
            elements.add((Element) nodes.item(i));
        }
        return elements;
    }

    public static List<Element> getElements(
            Element element, String tagName) {
        ArrayList<Element> elements = new ArrayList<Element>();
        //NodeList getChildNodes()包含此节点的所有子节点的 NodeList。
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            //static final short ELEMENT_NODE该节点为 Element。
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();
                if (nodeName != null && nodeName.equals(tagName)) {
                    elements.add((Element) node);
                }
            }
        }
        return elements;
    }
}
