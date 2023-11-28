package ScreenControllers;

import Models.Enums.FileExtension;
import Models.Interfaces.IDocumentBuilder;
import Services.AlertService;
import Models.ErrorObject;
import Services.DomainController;
import Util.XmlUtil;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class DocumentManagementScreenController extends VBox {

    //region Properties
    private final DomainController domeincontroller;
    private final FileChooser fileChooser;
    private final StartScreenController startScreenController;
    private final Comparator<IDocumentBuilder> builderSorter = (builder1, builder2) -> builder1.getNameProperty().get().compareToIgnoreCase(builder2.getNameProperty().get());
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
    private TableView<IDocumentBuilder> tvBestandenLijst;
    @FXML
    private TableColumn<IDocumentBuilder, String> tcName;
    @FXML
    private TableColumn<IDocumentBuilder, Boolean> tcSelect;
    @FXML
    private TableColumn<IDocumentBuilder, IDocumentBuilder> tcDelete;
    //endregion

    //region Constructor
    public DocumentManagementScreenController(DomainController domeincontroller, StartScreenController startScreenController) {
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
        ErrorObject error = selectFiles();
        if (error != null)
            AlertService.showAlert(error.key, error.key, error.message, this.getScene().getWindow(), AlertType.ERROR);
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
            AlertService.showAlert("Geen bestand geselecteerd", "Geen bestand geselecteerd", "Er is/zijn geen bestand(en) geselecteerd.", this.getScene().getWindow(), AlertType.ERROR);
        }
    }
    //endregion

    //region Functions
    private ErrorObject selectFiles() {
        setDefaultOrigin();
        List<File> newFiles = fileChooser.showOpenMultipleDialog(Stage.getWindows().filtered(Window::isShowing).get(0));
        if (newFiles != null) {
            setOriginPreference(newFiles.get(0));
            for (File file : newFiles) {
                String name = file.getName();
                String fileExtensionString = name.substring(name.lastIndexOf("."));

                if (!fileExtensionString.equals(".xbrl") && !fileExtensionString.equals(".csv")) {
                    return new ErrorObject("Fout bestandstype", "Het gekozen bestand is van een verkeerd bestandstype. Gelieve een bestand met extensie \".xbrl\" te selecteren.");
                }

                FileExtension fileExtension = name.substring(name.lastIndexOf(".")).equals(".xbrl") ? FileExtension.XBRL : FileExtension.CSV;

                ErrorObject documentAdded = domeincontroller.addDocument(file, fileExtension);
                if (documentAdded != null) {
                    return documentAdded;
                }
            }
            fillTable();
        }
        return null;
    }

    private void configureFileChooser() {
        fileChooser.setTitle("Kies XBRL of CSV file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XBRL", "*.xbrl"),
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
    }

    private void setDefaultOrigin() {
        String defaultOrigin = xmlUtil.getStringFromPreferences("defaultOrigin");
        if (defaultOrigin == null || defaultOrigin.equals("") || !Files.exists(Paths.get(defaultOrigin)))
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else
            fileChooser.setInitialDirectory(new File(defaultOrigin));
    }

    private void setOriginPreference(File file) {
        String path = file.getPath();

        String slash = System.getProperty("os.name").startsWith("Windows") ? "\\" : "/";
        String directoryPath = path.substring(0, path.lastIndexOf(slash));
        xmlUtil.setStringFromPreferences("defaultOrigin", directoryPath);
    }

    private TableCell<IDocumentBuilder, IDocumentBuilder> fillDeleteButtonTableCells() {
        TableCell<IDocumentBuilder, IDocumentBuilder> temp = new TableCell<>() {
            private final Button btnDelete = new Button("x");

            @Override
            protected void updateItem(IDocumentBuilder documentbuilder, boolean empty) {
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
        SortedList<IDocumentBuilder> sortedBuilders = new SortedList<IDocumentBuilder>(domeincontroller.getActiveDocumentBuilders(), builderSorter);
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
    //endregion

}
