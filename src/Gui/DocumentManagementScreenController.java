package Gui;

import Domein.DocumentWrapper.DocumentBuilder;
import Domein.DomeinController;
import Exceptions.DuplicateDocumentException;
import Exceptions.IllegalExtensionException;
import Utils.XmlUtil;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private Button btnRemove;
    @FXML
    private VBox vbScherm;
    @FXML
    private TableView<DocumentBuilder> tvBestandenLijst;
    @FXML
    private TableColumn<DocumentBuilder, String> tcName;
    @FXML
    private TableColumn<DocumentBuilder, Boolean> tcSelect;

    public DocumentManagementScreenController(DomeinController domeincontroller, StartScreenController startScreenController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DocumentManagementScreen.fxml"));
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
        label.setTextFill(Color.web("#dadada"));
        tvBestandenLijst.setPlaceholder(label);
        tcName.setCellValueFactory(celldata -> celldata.getValue().getNameProperty());
        tcSelect.setCellValueFactory(celldata -> celldata.getValue().getSelectedProperty());
        tcSelect.setCellFactory(CheckBoxTableCell.forTableColumn(tcSelect));
        fillTable();
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
        int indexOflastSlash = path.lastIndexOf('\\');
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

    @FXML
    private void removeDocuments(ActionEvent event) {
        if (!domeincontroller.getDocumentBuilders().isEmpty()) {
            domeincontroller.removeDocuments();
            fillTable();
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

}
