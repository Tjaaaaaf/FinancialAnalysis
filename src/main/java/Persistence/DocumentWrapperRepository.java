package Persistence;

import Models.DocumentWrapper;
import Models.Enums.FileExtension;
import Models.ErrorObject;
import Models.Interfaces.IDocumentBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DocumentWrapperRepository {

    private final HashMap<String, IDocumentBuilder> documentMap;
    private final ObservableList<String> documentNames;
    private DocumentBuilder builder;

    public DocumentWrapperRepository() {
        this.documentMap = new HashMap<>();
        this.documentNames = FXCollections.observableArrayList();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            this.builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DocumentWrapperRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ErrorObject addDocument(File file, FileExtension fileExtension) {
        try {
            if (documentMap.containsKey(file.getName())) {
                return new ErrorObject("Duplicaat bestand", "Het gekozen bestand is al geselecteerd.");
            }

            DocumentWrapper.DocumentBuilder documentBuilder = null;

            switch (fileExtension) {
                case XBRL:
                    Document document = builder.parse(file);
                    document.getDocumentElement().normalize();
                    documentBuilder = makeNewDocumentBuilder(document, file.getName(), fileExtension);
                    break;
                case CSV:
                    HashMap<String, String> csvValues = new HashMap<>();
                    try (Scanner scanner = new Scanner(file)) {
                        while (scanner.hasNextLine()) {
                            String nextLine = scanner.nextLine();
                            if (nextLine.contains(",")) {
                                String[] rowValues = nextLine.split(",");
                                csvValues.put(rowValues[0].substring(1, rowValues[0].length() - 1), rowValues[1].substring(1, rowValues[1].length() - 1));
                            }
                        }
                    }
                    documentBuilder = makeNewDocumentBuilder(csvValues, file.getName(), fileExtension);
                    break;
            }

            if (documentBuilder == null) {
                return new ErrorObject("Er ging iets fout!", "Er ging iets fout bij het verwerken van het bestand.");
            }

            documentMap.put(file.getName(), documentBuilder);

            documentNames.add(file.getName());

            return null;
        } catch (SAXException | IOException ex) {
            Logger.getLogger(DocumentWrapperRepository.class.getName()).log(Level.SEVERE, null, ex);
            return new ErrorObject("Er ging iets fout!", "Er ging iets fout bij het inlezen van het bestand.");
        }
    }

    private static DocumentWrapper.DocumentBuilder makeNewDocumentBuilder(Document document, String fileName, FileExtension fileExtension) {
        try {
            int year;
            if (document.getElementsByTagName("xbrli:instant").item(0) != null) {
                year = Integer.parseInt(document.getElementsByTagName("xbrli:instant").item(0).getTextContent().substring(0, 4));
            } else {
                year = Integer.parseInt(document.getElementsByTagName("instant").item(0).getTextContent().substring(0, 4));
            }
            DocumentWrapper.DocumentBuilder docBuilder = new DocumentWrapper.DocumentBuilder(document, fileName, year, fileExtension);
            if (docBuilder.extractBusiness() || !docBuilder.extractCurrentTimePeriods()) {
                return null;
            }
            return docBuilder;
        } catch (NumberFormatException | DOMException ex) {
            Logger.getLogger(DocumentWrapperRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static DocumentWrapper.DocumentBuilder makeNewDocumentBuilder(Map<String, String> csvValues, String fileName, FileExtension fileExtension) {
        try {
            int year = Integer.parseInt(csvValues.get("Accounting period start date").substring(0, 4));
            DocumentWrapper.DocumentBuilder docBuilder = new DocumentWrapper.DocumentBuilder(csvValues, fileName, year, fileExtension);
            if (docBuilder.extractBusiness()) {
                return null;
            }
            return docBuilder;
        } catch (NumberFormatException ex) {
            Logger.getLogger(DocumentWrapperRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<IDocumentBuilder> getActiveDocumentBuilders() {
        return documentMap.values().stream().filter(IDocumentBuilder::isSelected).collect(Collectors.toList());
    }

    public ObservableList<String> getDocuments() {
        return FXCollections.unmodifiableObservableList(documentNames);
    }

    public IDocumentBuilder getDocumentBuilderByName(String documentName) {
        return documentMap.get(documentName);
    }

    public void removeDocument(String documentName) {
        documentMap.remove(documentName);
        documentNames.remove(documentName);
    }

}
