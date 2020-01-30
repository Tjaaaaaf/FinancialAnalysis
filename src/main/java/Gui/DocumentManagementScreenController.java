package Gui;

import Domein.DocumentWrapper.DocumentBuilder;
import Domein.DomeinController;
import Exceptions.DuplicateDocumentException;
import Exceptions.IllegalExtensionException;
import Utils.XmlUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class DocumentManagementScreenController extends VBox {

    private final DomeinController domeincontroller;
    private final FileChooser fileChooser;
    private final StartScreenController startScreenController;
    private SortedList<DocumentBuilder> sortedBuilders = new SortedList<>(FXCollections.observableArrayList());
    private final Comparator<DocumentBuilder> builderSorter = (builder1, builder2) -> builder1.getNameProperty().get().compareToIgnoreCase(builder2.getNameProperty().get());
    private final XmlUtil xmlUtil = new XmlUtil();

    @FXML
    private HBox hbButtonBox;
    @FXML
    private Button btnKiesBestand;
    @FXML
    private Button btnAnalyse;
    @FXML
    private VBox vbScherm;
    @FXML
    private TableView<DocumentBuilder> tvBestandenLijst;
    @FXML
    private TableColumn<DocumentBuilder, String> tcName;
    @FXML
    private TableColumn<DocumentBuilder, Boolean> tcSelect;
    @FXML
    private TableColumn<DocumentBuilder, DocumentBuilder> tcDelete;

    public DocumentManagementScreenController(DomeinController domeincontroller, StartScreenController startScreenController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DocumentManagementScreen.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.domeincontroller = domeincontroller;
        this.startScreenController = startScreenController;
        this.fileChooser = new FileChooser();
        fileChooser.setTitle("Select xbrl file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XBRL", "*.xbrl")
        );
        setDefaultOrigin();

        buildGui();
    }

    private void setDefaultOrigin() {
        String defaultOrigin = xmlUtil.getStringFromPreferences("defaultOrigin");
        fileChooser.setInitialDirectory(defaultOrigin.equals("") ? new File(System.getProperty("user.home")) : new File(defaultOrigin));
    }

    private void buildGui() {
        Label label = new Label("Geen bestanden geselecteerd");
        label.setTextFill(Color.web("#d0dff2"));
        tvBestandenLijst.setPlaceholder(label);
        tcName.setCellValueFactory(celldata -> celldata.getValue().getNameProperty());

        tcSelect.setCellFactory(CheckBoxTableCell.forTableColumn(tcSelect));
        tcSelect.setCellValueFactory(celldata -> celldata.getValue().getSelectedProperty());

        tcDelete.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        tcDelete.setCellFactory(param -> new TableCell<DocumentBuilder, DocumentBuilder>() {
            private final Button btnDelete = new Button("x");

            @Override
            protected void updateItem(DocumentBuilder documentbuilder, boolean empty) {
                super.updateItem(documentbuilder, empty);

                if (documentbuilder == null) {
                    setGraphic(null);
                    return;
                }
                btnDelete.setPrefHeight(18);
                btnDelete.setMaxHeight(18);
                btnDelete.setMinHeight(18);
                btnDelete.setPadding(new Insets(0,7,0,7));
                btnDelete.setStyle("-fx-background-color: RED; -fx-text-fill: WHITE");
                setGraphic(btnDelete);
                btnDelete.setOnAction(
                        event -> {
                            domeincontroller.removeDocument(documentbuilder.getName());
                            fillTable();
                        }
                );
            }
        });
        fillTable();

        fixHeaders();
    }

    @FXML
    private void chooseDocument(ActionEvent event) {
        try {
            setDefaultOrigin();
            List<File> newFiles = fileChooser.showOpenMultipleDialog(Stage.getWindows().filtered(window -> window.isShowing()).get(0));
            if (newFiles != null) {
                setOriginPreference(newFiles.get(0));

                newFiles.stream().forEach(file -> {
                    String name = file.getName();

                    if (!name.substring(name.lastIndexOf(".")).equals(".xbrl")) {
                        throw new IllegalExtensionException();
                    }

                    String addedName = domeincontroller.addDocument(file);
                    if (addedName == null) {
                        throw new DuplicateDocumentException();
                    }
                });
                fillTable();
            }
        } catch (IllegalExtensionException ex) {
            ErrorAlert.showAlert("Fout bestandstype", "Fout bestandstype", "Het gekozen bestand is van een verkeerd bestandstype. Gelieve een bestand met extensie \".xbrl\" te selecteren.");
        } catch (DuplicateDocumentException ex) {
            ErrorAlert.showAlert("Duplicaat bestand", "Duplicaat bestand", "Het gekozen bestand is al geselecteerd.");
        }
    }

    private void setOriginPreference(File file) {
        String path = file.getPath();
        int indexOflastSlash = 0;

        String slash = System.getProperty("os.name").startsWith("Windows") ? "\\" : "/";

        indexOflastSlash = path.lastIndexOf(slash);
        String directoryPath = path.substring(0, indexOflastSlash);
        xmlUtil.setStringFromPreferences("defaultOrigin", directoryPath);
    }

    private void fillTable() {
        sortedBuilders = new SortedList<>(domeincontroller.getDocumentBuilders(), builderSorter);
        tvBestandenLijst.setItems(sortedBuilders);
    }

    @FXML
    private void makeAnalysis(ActionEvent event) {
        if (!domeincontroller.getDocumentBuilders().isEmpty()) {
            startScreenController.setCenter(new ChooseReportStyleScreenController(domeincontroller, startScreenController));
        } else {
            showFailureAlert();
        }
    }

    private void showFailureAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Geen bestand geselecteerd");
        alert.setHeaderText("Actie niet uitgevoerd");
        alert.setContentText("De actie is niet uitgevoerd omdat er geen bestand(en) is/zijn geselecteerd zijn.");
        alert.showAndWait();
    }

    private void fixHeaders() {
        tvBestandenLijst.getColumns().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                change.next();
                if (change.wasReplaced()) {
                    tvBestandenLijst.getColumns().clear();
                    tvBestandenLijst.getColumns().addAll(tcName, tcSelect, tcDelete);
                }
            }
        });
    }

}
