package Persistence;

import Models.Enums.FileExtension;
import Models.ErrorObject;
import Models.Interfaces.IDocumentBuilder;
import javafx.collections.ObservableList;
import java.io.File;
import java.util.List;

public class PersistenceController {

    private final DocumentWrapperRepository documentRepository;

    public PersistenceController() {
        this.documentRepository = new DocumentWrapperRepository();
    }

    public ErrorObject addDocument(File file, FileExtension fileExtension) {
        return documentRepository.addDocument(file, fileExtension);
    }

    public void removeDocument(String documentName) {
        documentRepository.removeDocument(documentName);
    }

    public IDocumentBuilder getDocumentBuilder(String name) {
        return documentRepository.getDocumentBuilderByName(name);
    }

    public List<IDocumentBuilder> getActiveDocumentBuilders() {
        return documentRepository.getActiveDocumentBuilders();
    }

    public ObservableList<String> getDocuments() {
        return documentRepository.getDocuments();
    }

}
