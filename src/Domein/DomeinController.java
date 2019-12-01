package Domein;

import Interfaces.IDocumentBuilder;
import Domein.DocumentWrapper.DocumentBuilder;
import Persistence.PersistenceController;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DomeinController {

    private final PersistenceController persistenceController;

    public DomeinController() {
        this.persistenceController = new PersistenceController(this);
    }

    public String addDocument(File file) {
        return persistenceController.addDocument(file);
    }

    public void removeDocuments() {
        persistenceController.removeDocuments();
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
