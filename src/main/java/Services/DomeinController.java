package Services;

import Models.DocumentWrapper.DocumentBuilder;
import Models.Interfaces.IDocumentBuilder;
import Persistence.PersistenceController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class DomeinController {

    private final PersistenceController persistenceController;

    public DomeinController() {
        this.persistenceController = new PersistenceController();
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

    public ObservableList<DocumentBuilder> getActiveDocumentBuilders() {
        return FXCollections.observableArrayList(persistenceController.getActiveDocumentBuilders());
    }

    public ObservableList<String> getDocuments() {
        return persistenceController.getDocuments();
    }
}
