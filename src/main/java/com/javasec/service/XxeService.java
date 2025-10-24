package com.javasec.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * XXE漏洞服务类
 */
@Service
public class XxeService {

    /**
     * 不安全的XML解析 - 存在XXE漏洞
     */
    public String parseXmlUnsafe(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return "请提供XML内容";
        }

        try {
            System.out.println("解析XML内容: " + xmlContent);

            // 危险：未禁用外部实体解析
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 注意：这里故意不设置安全配置，存在XXE漏洞
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));

            StringBuilder result = new StringBuilder();
            result.append("XML解析成功！\n\n");
            result.append("解析结果:\n");
            result.append("----------------------------------------\n");
            
            // 递归解析XML节点
            parseNode(document.getDocumentElement(), result, 0);
            
            return result.toString();

        } catch (Exception e) {
            return "XML解析失败: " + e.getMessage() + "\n\n堆栈信息:\n" + getStackTrace(e);
        }
    }

    /**
     * 安全的XML解析 - 禁用外部实体
     */
    public String parseXmlSafe(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return "请提供XML内容";
        }

        try {
            System.out.println("安全解析XML内容: " + xmlContent);

            // 安全：禁用外部实体解析
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            // 禁用DTD处理
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // 禁用外部通用实体
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            // 禁用外部参数实体
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 禁用外部DTD
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // 设置为false以禁用XML包含
            factory.setXIncludeAware(false);
            // 禁用扩展实体引用
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));

            StringBuilder result = new StringBuilder();
            result.append("安全XML解析成功！\n\n");
            result.append("解析结果:\n");
            result.append("----------------------------------------\n");
            
            // 递归解析XML节点
            parseNode(document.getDocumentElement(), result, 0);
            
            return result.toString();

        } catch (Exception e) {
            return "安全XML解析失败: " + e.getMessage();
        }
    }

    /**
     * 递归解析XML节点
     */
    private void parseNode(Node node, StringBuilder result, int depth) {
        String indent = "  ".repeat(depth);
        
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            result.append(indent).append("元素: ").append(element.getTagName());
            
            // 获取属性
            if (element.hasAttributes()) {
                result.append(" [属性: ");
                for (int i = 0; i < element.getAttributes().getLength(); i++) {
                    Node attr = element.getAttributes().item(i);
                    result.append(attr.getNodeName()).append("=").append(attr.getNodeValue());
                    if (i < element.getAttributes().getLength() - 1) {
                        result.append(", ");
                    }
                }
                result.append("]");
            }
            result.append("\n");
            
            // 获取文本内容
            String textContent = element.getTextContent();
            if (textContent != null && !textContent.trim().isEmpty()) {
                // 检查是否只有文本内容（没有子元素）
                boolean hasChildElements = false;
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        hasChildElements = true;
                        break;
                    }
                }
                
                if (!hasChildElements) {
                    result.append(indent).append("  内容: ").append(textContent.trim()).append("\n");
                }
            }
            
            // 递归处理子节点
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    parseNode(child, result, depth + 1);
                }
            }
        }
    }

    /**
     * 获取异常堆栈信息
     */
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 创建示例XML
     */
    public String createSampleXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<user>\n" +
               "    <name>张三</name>\n" +
               "    <age>25</age>\n" +
               "    <email>zhangsan@example.com</email>\n" +
               "</user>";
    }

    /**
     * 创建XXE攻击载荷示例
     */
    public String createXxePayload() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<!DOCTYPE root [\n" +
               "<!ENTITY xxe SYSTEM \"file:///etc/passwd\">\n" +
               "]>\n" +
               "<root>\n" +
               "    <data>&xxe;</data>\n" +
               "</root>";
    }
}