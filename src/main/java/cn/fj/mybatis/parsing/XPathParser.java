package cn.fj.mybatis.parsing;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import cn.fj.mybatis.bulider.BuilderException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import java.io.Reader;

public class XPathParser {

    private XPath xPath;

    private Document document;

    public XPathParser(Reader reader) {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        this.xPath = xPathFactory.newXPath();
        this.document = createDocument(new InputSource(reader));
    }

    public Node evalNode(String expression){
        return this.evalNode(expression,document);
    }

    public Node evalNode(String expression,Object root){
        return (Node) evaluate(expression,root,XPathConstants.NODE);
    }

    public Object evaluate(String expression, Object root, QName returnType){
        try {
            return xPath.evaluate(expression,root, returnType);
        } catch (XPathExpressionException e) {
            throw new BuilderException("Error evaluating XPath.  Cause: " + e, e);
        }
    }

    private Document createDocument(InputSource inputSource){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setValidating(false);

            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            return documentBuilder.parse(inputSource);
        } catch (Exception e) {
            throw new BuilderException("Error creating document instance.  Cause: " + e, e);
        }
    }
}
