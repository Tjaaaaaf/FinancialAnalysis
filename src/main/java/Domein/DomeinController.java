package Domein;

import Domein.DocumentWrapper.DocumentBuilder;
import Interfaces.IDocumentBuilder;
import Persistence.PersistenceController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class DomeinController {

    private final PersistenceController persistenceController;

    public DomeinController() {
        this.persistenceController = new PersistenceController(this);
    }

    public String addDocument(File file) {
        return persistenceController.addDocument(file);
    }

    public void removeDocument(String documentName) {
        persistenceController.removeDocument(documentName);
    }

    public IDocumentBuilder getDocumentBuilder(String name) {
        return persistenceController.getDocumentBuilder(name);
    }

    public ObservableList<DocumentBuilder> getDocumentBuilders() {
        return FXCollections.observableArrayList(persistenceController.getDocumentBuilders());
    }

    public ObservableList<String> getDocuments() {
        return persistenceController.getDocuments();
    }
}
