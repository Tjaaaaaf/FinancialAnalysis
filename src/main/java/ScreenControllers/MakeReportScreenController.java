package ScreenControllers;

import Services.DomeinController;
import Util.DocumentComparator;
import Models.ErrorObject;
import Services.AlertService;
import Models.DocumentWrapper;
import Services.ReportService;
import Models.Enums.ReportStyle;
import Util.XmlUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MakeReportScreenController extends VBox {

    // region Properties
    private final DomeinController domeinController;
    private final DirectoryChooser directoryChooser;
    private final StartScreenController startScreenController;
    private final ReportStyle style;
    private final HSSFWorkbook workbook;
    private final List<DocumentWrapper> documents;
    private final SettingsScreenController settingsScreenController;
    private File directoryFile;
    private HSSFSheet report;
    private XmlUtil xmlUtil = new XmlUtil();
    // endregion

    // region FXMLProperties
    @FXML
    private TextField tfLocation;
    @FXML
    private Button btnLocation;
    @FXML
    private TextField tfName;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnMake;
    // endregion

    // region Constructor
    public MakeReportScreenController(DomeinController domeinController, StartScreenController startScreenController,
            ReportStyle style, SettingsScreenController settingsScreenController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MakeReportScreen.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.domeinController = domeinController;
        this.startScreenController = startScreenController;
        this.settingsScreenController = settingsScreenController;
        this.directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory");
        this.style = style;
        this.documents = new ArrayList<>();
        this.workbook = new HSSFWorkbook();

        buildGui();
    }
    // endregion

    // region BuildGUI
    private void buildGui() {
        String defaultSource = xmlUtil.getStringFromPreferences("defaultSource");
        if (defaultSource != null) {
            directoryFile = new File(defaultSource);
            tfLocation.setText(defaultSource);
        } else {
            directoryFile = null;
            tfLocation.setText("");
        }
    }
    // endregion

    // region FXMLFunctions
    @FXML
    private void back(ActionEvent event) {
        startScreenController.setCenter(settingsScreenController);
        startScreenController.switchColorSettingsStep();
        startScreenController.switchColorSaveStep();
    }

    @FXML
    private void chooseLocation(ActionEvent event) {
        directoryFile = directoryChooser.showDialog(Stage.getWindows().filtered(window -> window.isShowing()).get(0));
        if (directoryFile != null) {
            tfLocation.setText(directoryFile.getPath());
            xmlUtil.setStringFromPreferences("defaultSource", directoryFile.getPath());
        }
    }

    @FXML
    private void makeSheet(ActionEvent event) {
        ErrorObject error = checkInput();
        if (error != null)
            AlertService.showAlert(error.key, error.key, error.message, this.getScene().getWindow(), AlertType.ERROR);
        else {
            report = workbook.createSheet(tfName.getText());
            error = writeReport();
            if (error != null)
                AlertService.showAlert(error.key, error.key, error.message, this.getScene().getWindow(),
                        AlertType.ERROR);
            else {
                startScreenController
                        .setCenter(new DocumentManagementScreenController(domeinController, startScreenController));
                startScreenController.switchColorSaveStep();
                startScreenController.switchColorDocumentStep();
            }
        }
    }

    private ErrorObject checkInput() {
        if (tfName.getText().toCharArray().length >= 30) {
            return new ErrorObject("Verkeerde input", "Naam mag niet langer zijn dan 31 karakters.");
        }
        if (tfName.getText().toCharArray().length == 0) {
            return new ErrorObject("Verkeerde input", "Naam mag niet leeg zijn.");
        }
        File test = new File(tfLocation.getText());
        if (!test.isDirectory()) {
            return new ErrorObject("Verkeerde input", "Locatie is niet geldig.");
        }
        if (!test.exists()) {
            return new ErrorObject("Verkeerde input", "Locatie is niet geldig.");
        }
        File alreadyExists = new File(test.getPath() + String.format("\\%s.xls", tfName.getText()));
        if (alreadyExists.exists()) {
            return new ErrorObject("Verkeerde input",
                    String.format("Er bestaat al een overzicht met de naam \"%s\" op de locatie \"%s\".",
                            tfName.getText(), tfLocation.getText()));
        }
        return null;
    }

    @FXML
    private void checkForEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            makeSheet(new ActionEvent());
        }
    }
    // endregion

    // region Functions
    private ErrorObject writeReport() {
        try {
            String slash = System.getProperty("os.name").startsWith("Windows") ? "\\" : "/";
            File file = new File(tfLocation.getText() + slash + tfName.getText() + ".xls");

            try (FileOutputStream out = new FileOutputStream(file)) {
                prepareDocuments();
                createReport();

                workbook.write(out);
            }
            workbook.close();

            AlertService.showAlert("Aanmaken gelukt", "Aanmaken excel bestand gelukt.", "Bestand is aangemaakt.",
                    this.getScene().getWindow(), AlertType.INFORMATION);
        } catch (FileNotFoundException ex) {
            return new ErrorObject("Verkeerde input", "Het pad klopt niet");
        } catch (IOException ex) {
            return new ErrorObject("Verkeerde input", "Er is een fout opgetreden");
        }
        return null;
    }

    private void createReport() {

        ReportService reportService = new ReportService(workbook, report, documents, style,
                workbook.createDataFormat());

        if (!style.equals(ReportStyle.VERGELIJKINGNV) && !style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            reportService.createHistoriekReport();
        } else {
            reportService.createVergelijkingReport();
        }

        for (int i = 0; i < 2 * documents.size() + 7; i++) {
            report.autoSizeColumn(i);
        }
    }

    private void prepareDocuments() {
        if (!style.equals(ReportStyle.VERGELIJKINGNV) && !style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            compileHistoriek();
        } else {
            compileVergelijking();
        }
        sortDocuments();
    }

    private void sortDocuments() {
        Collections.sort(documents, new DocumentComparator());
    }

    private void compileVergelijking() {
        domeinController.getActiveDocumentBuilders().stream().forEach(doc -> {
            documents.add(doc.addBAVlottendeActiva()
                    .addBALiquideMiddelen()
                    .addBATotaalActiva()
                    .addBPEigenVermogen()
                    .addBPReserves()
                    .addBPOvergedragenWinstVerlies()
                    .addBPSchuldenHoogstens1Jaar()
                    .addRRBedrijfsopbrengsten()
                    .addRRBedrijfsopbrengstenOmzet()
                    .addRRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva()
                    .addRRBedrijfsWinstVerlies()
                    .addRRWinstVerliesBoekjaar()
                    .addRRBedrijfskostenHandelsgoederenGrondHulpstoffen()
                    .addRRBedrijfskostenBezoldigingenSocialeLastenPensioenen()
                    .addRRBedrijfskostenDienstenDiverseGoederen()
                    .addRRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen()
                    .addRRBedrijfskostenAndereBedrijfskosten()
                    .addRRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen()
                    .addRRFinancieleKosten()
                    .addRRFinancieleKostenRecurrent()
                    .addRRFinancieleKostenNietRecurrent()
                    .addRRFinancieleOpbrengsten()
                    .addRRFinancieleOpbrengstenRecurrent()
                    .addRRBedrijfskostenNietRecurrenteBedrijfskosten()
                    .addRRBedrijfskostenUitzonderlijkeKosten()
                    .addRRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten()
                    .addRRBedrijfsopbrengstenUitzonderlijkeOpbrengsten()
                    .addRRBelastingenOpResultaat()
                    .addRROntrekkingenUitgesteldeBelastingen()
                    .addRROverboekingUitgesteldeBelastingen()
                    .addBAVoorradenBestellingenUitvoering()
                    .addBAVorderingenHoogstens1JaarHandelsvorderingen()
                    .addBPSchuldenHoogstens1JaarHandelsschuldenLeveranciers()
                    .addSBGemiddeldeFTE()
                    .addSBGepresteerdeUren()
                    .addSBGemiddeldAantalFTEUitzendkrachten()
                    .addSBPersoneelskosten()
                    .addSBGepresteerdeUrenUitzendkrachten()
                    .addSBPersoneelskostenUitzendkrachten()
                    .addSBAantalWerknemersOpEindeBoekjaar()
                    .addSBAantalBediendenOpEindeBoekjaar()
                    .addSBAantalArbeidersOpEindeBoekjaar()
                    .addBVBABrutomarge()
                    .addRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen()
                    .addRRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename()
                    .addBPSchulden()
                    .build());
        });
    }

    private void compileHistoriek() {
        domeinController.getActiveDocumentBuilders().stream().forEach(doc -> {
            documents.add(doc.addBAVasteActiva()
                    .addBAImmaterieleVasteActiva()
                    .addBAMaterieleVasteActiva()
                    .addBAFinancieleVasteActiva()
                    .addBAVlottendeActiva()
                    .addBAVoorradenBestellingenUitvoering()
                    .addBAVorderingenHoogstens1JaarHandelsvorderingen()
                    .addBAVorderingenHoogstens1JaarOverigeVorderingen()
                    .addBALiquideMiddelen()
                    .addBAOverlopendeRekeningen()
                    .addBATotaalActiva()
                    .addBPEigenVermogen()
                    .addBPVoorzieningenUitgesteldeBelastingen()
                    .addBPSchuldenMeer1Jaar()
                    .addBPSchuldenMeer1JaarFinancieleSchulden()
                    .addBPSchuldenHoogstens1JaarHandelsschuldenLeveranciers()
                    .addBPSchuldenMeer1JaarOverigeSchulden()
                    .addBPTotaalPassiva()
                    .addRRBedrijfsopbrengsten()
                    .addRRBedrijfsopbrengstenOmzet()
                    .addRRBedrijfskosten()
                    .addRRWinstVerliesBoekjaar()
                    .addRRBedrijfskostenHandelsgoederenGrondHulpstoffen()
                    .addRRBedrijfskostenBezoldigingenSocialeLastenPensioenen()
                    .addRRBedrijfskostenDienstenDiverseGoederen()
                    .addRRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen()
                    .addRRBedrijfskostenAndereBedrijfskosten()
                    .addRRBedrijfskostenNietRecurrenteBedrijfskosten()
                    .addRRBedrijfskostenUitzonderlijkeKosten()
                    .addRRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva()
                    .addRRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen()
                    .addRRBedrijfsWinstVerlies()
                    .addRRBelastingenOpResultaat()
                    .addRROntrekkingenUitgesteldeBelastingen()
                    .addRROverboekingUitgesteldeBelastingen()
                    .addBPOverlopendeRekeningen()
                    .addBPSchuldenHoogstens1Jaar()
                    .addBPSchuldenHoogstens1JaarFinancieleSchulden()
                    .addRRFinancieleOpbrengsten()
                    .addRRFinancieleKosten()
                    .addRRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten()
                    .addRRBedrijfsopbrengstenUitzonderlijkeOpbrengsten()
                    .addRRFinancieleKostenRecurrent()
                    .addRRFinancieleOpbrengstenRecurrent()
                    .addTLMVAMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLIMVAMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLFVAMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen()
                    .addTLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen()
                    .addBAOndernemingenDeelnemingsverhoudingDeelnemingen()
                    .addBVBABrutomarge()
                    .addRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen()
                    .addRRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename()
                    .build());
        });
    }
    // endregion
}
