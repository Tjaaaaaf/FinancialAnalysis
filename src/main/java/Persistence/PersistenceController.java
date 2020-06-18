package Persistence;

import Models.DocumentWrapper.DocumentBuilder;
import Services.DomeinController;
import Models.Interfaces.IDocumentBuilder;
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

    public List<DocumentBuilder> getActiveDocumentBuilders() {
        return documentRepository.getActiveDocumentBuilders();
    }

    public ObservableList<String> getDocuments() {
        return documentRepository.getDocuments();
    }

}
