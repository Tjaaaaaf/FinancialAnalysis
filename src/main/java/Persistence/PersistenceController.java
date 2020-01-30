package Persistence;

import Domein.DocumentWrapper.DocumentBuilder;
import Domein.DomeinController;
import Interfaces.IDocumentBuilder;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;

public class PersistenceController {

    private final DomeinController domeinController;
    private final DocumentWrapperRepository documentRepository;

    public PersistenceController(DomeinController domeincontroller) {
        this.domeinController = domeincontroller;
        this.documentRepository = new DocumentWrapperRepository();
    }

    public String addDocument(File file) {
        return documentRepository.addDocument(file);
    }

    public void removeDocument(String documentName) {
        documentRepository.removeDocument(documentName);
    }

    public IDocumentBuilder getDocumentBuilder(String name) {
        return documentRepository.getDocumentBuilderByName(name);
    }

    public List<DocumentBuilder> getDocumentBuilders() {
        return documentRepository.getDocumentBuilders();
    }

    public ObservableList<String> getDocuments() {
        return documentRepository.getDocuments();
    }

}
