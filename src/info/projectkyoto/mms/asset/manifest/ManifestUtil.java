/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.projectkyoto.mms.asset.manifest;

import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;

/**
 *
 * @author kobayasi
 */
public class ManifestUtil {

    public static Manifest parseXml(InputStream is) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(is);
            Manifest manifest = new Manifest();
            Element root = dom.getDocumentElement();
            Node serialNode = root.getAttributes().getNamedItem("serialNo");
            if (serialNode == null) {
                throw new IllegalArgumentException("serialNo is null.");
            }
            manifest.setSerialNo(Integer.parseInt(serialNode.getNodeValue()));
            Node versionNode = root.getAttributes().getNamedItem("version");
            if (versionNode == null) {
                throw new IllegalArgumentException("version is null.");
            }
            manifest.setVersion(versionNode.getNodeValue());
            NodeList assetsNodeList = root.getChildNodes();
            for (int i = 0; i < assetsNodeList.getLength(); i++) {
                Node productNode = assetsNodeList.item(i);
                if (productNode.getNodeName().equals("product")) {
                    ProductNode assets = new ProductNode();
                    Node nameNode = productNode.getAttributes().getNamedItem("name");
                    if (nameNode == null) {
                        assets.setLanguage("");
                    } else {
                        assets.setName(nameNode.getNodeValue());
                    }
                    Node localeNode = productNode.getAttributes().getNamedItem("language");
                    if (localeNode == null) {
                        assets.setLanguage("default");
                    } else {
                        assets.setLanguage(localeNode.getNodeValue());
                    }
                    Node developerNameNode = productNode.getAttributes().getNamedItem("developerName");
                    if (developerNameNode == null) {
                        throw new IllegalArgumentException("developer name is null.");
                    } else {
                        assets.setDeveloperName(developerNameNode.getNodeValue());
                    }
                    NodeList childNodeList = productNode.getChildNodes();
                    for (int i2 = 0; i2 < childNodeList.getLength(); i2++) {
                        Node childNode = childNodeList.item(i2);
                        if (childNode.getNodeName().equals("description")) {
                            assets.setDescription(childNode.getTextContent());
                        } else if (childNode.getNodeName().equals("file")) {
                            FileNode asset = parseFile(childNode);
                            assets.getFileMap().put(asset.getPath(), asset);
                        }
                    }
                    manifest.getProductMap().put(assets.getLanguage(), assets);
                }
            }
            return manifest;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static FileNode parseFile(Node node) {
        FileNode fileNode = new FileNode();
        Node typeNode = node.getAttributes().getNamedItem("type");
        if (typeNode == null) {
            fileNode.setType(null);
        } else {
            fileNode.setType(typeNode.getNodeValue());
        }
        Node pathNode = node.getAttributes().getNamedItem("path");
        if (pathNode == null) {
            throw new IllegalArgumentException("invalid path");
        } else {
            fileNode.setPath(pathNode.getNodeValue());
        }
        Node nameNode = node.getAttributes().getNamedItem("name");
        if (nameNode == null) {
            throw new IllegalArgumentException("invalid name");
        } else {
            fileNode.setName(nameNode.getNodeValue());
        }
        NodeList childNodeList = node.getChildNodes();
        for (int i2 = 0; i2 < childNodeList.getLength(); i2++) {
            Node childNode = childNodeList.item(i2);
            if (childNode.getNodeName().equals("description")) {
                fileNode.setDescription(childNode.getTextContent());
                break;
            }
        }
        return fileNode;
    }

    public static String escape(String content) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '<') {
                buffer.append("&lt;");
            } else if (c == '>') {
                buffer.append("&gt;");
            } else if (c == '&') {
                buffer.append("&amp;");
            } else if (c == '"') {
                buffer.append("&quot;");
            } else if (c == '\'') {
                buffer.append("&apos;");
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
    public static Document manifest2xml(Manifest manifest) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element manifestElement = doc.createElement("manifest");
            manifestElement.setAttribute("version", manifest.getVersion());
            manifestElement.setAttribute("serialNo", Integer.toString(manifest.getSerialNo()));
            doc.appendChild(manifestElement);
            
            //asset
            for(ProductNode productNode : manifest.getProductMap().values()) {
                Element assetsElement = doc.createElement("product");
                manifestElement.appendChild(assetsElement);
                assetsElement.setAttribute("language", productNode.getLanguage());
                assetsElement.setAttribute("developerName", productNode.getDeveloperName());
                assetsElement.setAttribute("name", productNode.getName());
                Element descriptionElement = doc.createElement("description");
                assetsElement.appendChild(descriptionElement);
                descriptionElement.appendChild(doc.createTextNode(escape(productNode.getDescription())));
                for(FileNode fileNode : productNode.getFileMap().values()) {
                    Element assetElement = doc.createElement("file");
                    assetsElement.appendChild(assetElement);
                    if (fileNode.getType() != null) {
                        assetElement.setAttribute("type", fileNode.getType());
                    }
                    assetElement.setAttribute("path", fileNode.getPath());
                    assetElement.setAttribute("name", fileNode.getName());
                    Element descriptionElement2 = doc.createElement("description");
                    assetElement.appendChild(descriptionElement2);
                    descriptionElement2.appendChild(doc.createTextNode(escape(fileNode.getDescription())));
                }
            }
            return doc;
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public static void printDoc(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(System.out));
        } catch(Exception ex) {
            new RuntimeException(ex);
        }
    }
}
