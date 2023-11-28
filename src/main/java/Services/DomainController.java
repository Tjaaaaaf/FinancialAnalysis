package Services;

import Models.Enums.FileExtension;
import Models.ErrorObject;
import Models.Interfaces.IDocumentBuilder;
import Persistence.PersistenceController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;

public class DomainController {

    private final PersistenceController persistenceController;

    public DomainController() {
        this.persistenceController = new PersistenceController();
    }

    public ErrorObject addDocument(File file, FileExtension fileExtension) {
        return persistenceController.addDocument(file, fileExtension);
    }

    public void removeDocument(String documentName) {
        persistenceController.removeDocument(documentName);
    }

    public IDocumentBuilder getDocumentBuilder(String name) {
        return persistenceController.getDocumentBuilder(name);
    }

    public ObservableList<IDocumentBuilder> getActiveDocumentBuilders() {
        return FXCollections.observableArrayList(persistenceController.getActiveDocumentBuilders());
    }

    public ObservableList<String> getDocuments() {
        return persistenceController.getDocuments();
    }
}
