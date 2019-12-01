package Persistence;

import Domein.DocumentWrapper;
import Interfaces.IDocumentBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DocumentWrapperRepository {

    private HashMap<String, IDocumentBuilder> documentMap;
    private ObservableList<String> documentNames;
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;

    public DocumentWrapperRepository() {
        this.documentMap = new HashMap<>();
        this.documentNames = FXCollections.observableArrayList();
        try {
            this.factory = DocumentBuilderFactory.newInstance();
            this.builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DocumentWrapperRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String addDocument(File file) {
        try {
            if (documentMap.containsKey(file.getName())) {
                return null;
            }

            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            documentMap.put(file.getName(), makeNewDocumentBuilder(document, file));

            documentNames.add(file.getName());

            return file.getName();
        } catch (SAXException | IOException ex) {
            Logger.getLogger(DocumentWrapperRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static DocumentWrapper.DocumentBuilder makeNewDocumentBuilder(Document document, File file) {
        try {
            int year = 0;
            if (document.getElementsByTagName("xbrli:instant").item(0) != null) {
                year = Integer.parseInt(document.getElementsByTagName("xbrli:instant").item(0).getTextContent().substring(0, 4));
            } else {
                year = Integer.parseInt(document.getElementsByTagName("instant").item(0).getTextContent().substring(0, 4));
            }
            return new DocumentWrapper.DocumentBuilder(document, file.getName(), year);
        } catch (NumberFormatException | DOMException ex) {
            Logger.getLogger(DocumentWrapperRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<DocumentWrapper.DocumentBuilder> getDocumentBuilders() {
        List<DocumentWrapper.DocumentBuilder> documentBuilders = new ArrayList<>();
        documentBuilders.addAll((Collection<DocumentWrapper.DocumentBuilder>) (Object) documentMap.values());
        return documentBuilders;
    }

    public ObservableList<String> getDocuments() {
        return FXCollections.unmodifiableObservableList(documentNames);
    }

    public IDocumentBuilder getDocumentBuilderByName(String documentName) {
        IDocumentBuilder documentBuilder = documentMap.get(documentName);
        if (documentBuilder != null) {
            return documentBuilder;
        }
        return null;
    }

    public void removeDocuments() {
        List<String> names = new ArrayList<>();
        documentMap.values().stream().forEach(builder -> {
            if (builder.getSelectedProperty().get()) {
                names.add(builder.getNameProperty().get());
            }
        });
        Set namesSet = new HashSet<String>();
        namesSet.addAll(names);
        documentMap.keySet().removeAll(namesSet);
        documentNames.removeAll(names);
    }

}
