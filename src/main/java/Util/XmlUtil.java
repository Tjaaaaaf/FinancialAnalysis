package Util;

import StartUp.StartApplication;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import Services.AlertService;
import javafx.scene.control.Alert;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public final class XmlUtil {

    private Transformer transformer;

    public XmlUtil() {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<Node> asList(NodeList n) {
        return n.getLength() == 0
                ? Collections.emptyList() : new NodeListWrapper(n);
    }

    public static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {

        private final NodeList list;

        NodeListWrapper(NodeList l) {
            list = l;
        }

        @Override
        public Node get(int index) {
            return list.item(index);
        }

        @Override
        public int size() {
            return list.getLength();
        }
    }

    private void checkForSource() {
        String sourceDir = String.format("%s//.financialAnalysis", System.getProperty("user.home"));
        String sourceFile = String.format("%s//.financialAnalysis//preferences.xml", System.getProperty("user.home"));

        File directoryFile = new File(sourceDir);

        if (!directoryFile.exists()) {
            if (!directoryFile.mkdir())
                AlertService.showAlert("Fout",
                        "Instellingen map aanmaken mislukt", String.format("Het aanmaken van de map om de gebruikersinstellingen op te slaan is mislukt. (%s)", sourceDir),
                        StartApplication.getScene().getWindow(), Alert.AlertType.ERROR);
        }

        File file = new File(sourceFile);

        if (!file.exists()) {
            try {
                if(!file.createNewFile())
                    AlertService.showAlert("Fout",
                            "Instelligen bestand aanmaken mislukt", String.format("Het aanmaken van het bestand om de gebruikersinstellingen in op te slaan is mislukt. (%s)", sourceFile),
                            StartApplication.getScene().getWindow(), Alert.AlertType.ERROR);

                OutputStream output = Files.newOutputStream(Paths.get(sourceFile), StandardOpenOption.CREATE);
                String defaultString = "<preferences><defaultSource>C:\\</defaultSource><defaultOrigin>C:\\</defaultOrigin></preferences>";
                output.write(defaultString.getBytes());
                output.flush();
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getStringFromPreferences(String tagName) {
        try {
            Document doc = getPreferences();
            Node tag = doc.getElementsByTagName(tagName).item(0);
            if (tag != null) {
                return tag.getTextContent();
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public void setStringFromPreferences(String tagName, String content) {
        try {
            Document doc = getPreferences();
            Node tag = doc.getElementsByTagName(tagName).item(0);
            if (tag != null) {
                if (tag.hasChildNodes()) {
                    tag.getFirstChild().setTextContent(content);
                } else {
                    tag.appendChild(doc.createTextNode(content));
                }
            }
            sendDOMToFile(doc);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            Logger.getLogger(XmlUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendDOMToFile(Document doc) throws TransformerException, FileNotFoundException {
        transformer.transform(new DOMSource(doc),
                new StreamResult(new FileOutputStream(new File(String.format("%s//.financialAnalysis//preferences.xml", System.getProperty("user.home"))))));
    }

    private Document getPreferences() throws SAXException, ParserConfigurationException, IOException {
        checkForSource();
        String source = String.format("%s//.financialAnalysis//preferences.xml", System.getProperty("user.home"));
        File file = new File(source);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(file);
    }
}
