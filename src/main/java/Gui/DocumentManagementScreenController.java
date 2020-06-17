package Gui;

import Domain.DocumentWrapper.DocumentBuilder;
import Domain.DomeinController;
import Exceptions.DuplicateDocumentException;
import Exceptions.IllegalExtensionException;
import Utils.XmlUtil;
import javafx.scene.control.Alert.AlertType;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class DocumentManagementScreenController extends VBox {

    //region Properties
    private final DomeinController domeincontroller;
    private final FileChooser fileChooser;
    private final StartScreenController startScreenController;
    private SortedList<DocumentBuilder> sortedBuilders = new SortedList<>(FXCollections.observableArrayList());
    private final Comparator<DocumentBuilder> builderSorter = (builder1, builder2) -> builder1.getNameProperty().get().compareToIgnoreCase(builder2.getNameProperty().get());
    private final XmlUtil xmlUtil = new XmlUtil();
    private SettingsScreenController next;
    //endregion

    //region FXMLProperties
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
    //endregion

    //region Constructor
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

        configureFileChooser();
        setDefaultOrigin();

        buildGui();
    }
    //endregion

    //region BuildGUI
    private void buildGui() {
        Label label = new Label("Geen bestanden geselecteerd");
        label.setTextFill(Color.web("#d0dff2"));
        tvBestandenLijst.setPlaceholder(label);
        tcName.setCellValueFactory(celldata -> celldata.getValue().getNameProperty());

        tcSelect.setCellFactory(CheckBoxTableCell.forTableColumn(tcSelect));
        tcSelect.setCellValueFactory(celldata -> celldata.getValue().getSelectedProperty());

        tcDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tcDelete.setCellFactory(param -> fillDeleteButtonTableCells());

        fillTable();

        fixHeaders();
    }
    //endregion

    //region FXMLFunctions
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
            CustomAlert.showAlert("Fout bestandstype", "Fout bestandstype", "Het gekozen bestand is van een verkeerd bestandstype. Gelieve een bestand met extensie \".xbrl\" te selecteren.", this.getScene().getWindow(), AlertType.ERROR);
        } catch (DuplicateDocumentException ex) {
            CustomAlert.showAlert("Duplicaat bestand", "Duplicaat bestand", "Het gekozen bestand is al geselecteerd.", this.getScene().getWindow(), AlertType.ERROR);
        }
    }

    @FXML
    private void makeAnalysis(ActionEvent event) {
        if (!domeincontroller.getActiveDocumentBuilders().isEmpty()) {
            if (next == null) {
                next = new SettingsScreenController(domeincontroller, startScreenController, this);
            }
            startScreenController.setCenter(next);

            startScreenController.switchColorDocumentStep();
            startScreenController.switchColorSettingsStep();
        } else {
            CustomAlert.showAlert("Geen bestand geselecteerd", "Geen bestand geselecteerd", "Er is/zijn geen bestand(en) geselecteerd.", this.getScene().getWindow(), AlertType.ERROR);
        }
    }
    //endregion

    //region Functions
    private void configureFileChooser() {
        fileChooser.setTitle("Select xbrl file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XBRL", "*.xbrl")
        );
    }

    private void setDefaultOrigin() {
        String defaultOrigin = xmlUtil.getStringFromPreferences("defaultOrigin");
        if(defaultOrigin == null || defaultOrigin.equals("") || !Files.exists(Paths.get(defaultOrigin)))
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else
            fileChooser.setInitialDirectory(new File(defaultOrigin));
    }

    private void setOriginPreference(File file) {
        String path = file.getPath();
        int indexOflastSlash = 0;

        String slash = System.getProperty("os.name").startsWith("Windows") ? "\\" : "/";

        indexOflastSlash = path.lastIndexOf(slash);
        String directoryPath = path.substring(0, indexOflastSlash);
        xmlUtil.setStringFromPreferences("defaultOrigin", directoryPath);
    }

    private TableCell<DocumentBuilder, DocumentBuilder> fillDeleteButtonTableCells() {
        TableCell<DocumentBuilder, DocumentBuilder> temp = new TableCell<>() {
            private final Button btnDelete = new Button("x");

            @Override
            protected void updateItem(DocumentBuilder documentbuilder, boolean empty) {
                super.updateItem(documentbuilder, empty);

                if (documentbuilder == null) {
                    setGraphic(null);
                    return;
                }
                btnDelete.setPrefHeight(17);
                btnDelete.setMaxHeight(17);
                btnDelete.setMinHeight(17);
                btnDelete.setPadding(new Insets(0, 7, 0, 6));
                btnDelete.getStyleClass().add("deleteButton");
                setGraphic(btnDelete);
                btnDelete.setOnAction(
                        event -> {
                            domeincontroller.removeDocument(documentbuilder.getName());
                            fillTable();
                        }
                );
            }
        };
        temp.setPadding(new Insets(2, 0, 0, 3.5));
        return temp;
    }

    private void fillTable() {
        sortedBuilders = new SortedList<>(domeincontroller.getActiveDocumentBuilders(), builderSorter);
        tvBestandenLijst.setItems(sortedBuilders);
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
    //endregion®

}
