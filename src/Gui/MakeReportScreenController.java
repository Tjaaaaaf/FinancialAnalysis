package Gui;

import Domein.DocumentComparator;
import Domein.DocumentWrapper;
import Domein.DomeinController;
import Interfaces.IDocumentBuilder;
import Domein.ReportFactory;
import Enums.ReportStyle;
import Utils.XmlUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class MakeReportScreenController extends VBox {

    private final DomeinController domeinController;
    private final DirectoryChooser directoryChooser;
    private final StartScreenController startScreenController;
    private final ReportStyle style;
    private final HSSFWorkbook workbook;
    private final List<DocumentWrapper> documents;
    private File directoryFile;
    private HSSFSheet report;
    private XmlUtil xmlUtil = new XmlUtil();

    @FXML
    private TextField tfLocatie;
    @FXML
    private Button btnLocatie;
    @FXML
    private TextField tfName;
    @FXML
    private Label lblFout;
    @FXML
    private Button btnTerug;
    @FXML
    private Button btnMaak;

    public MakeReportScreenController(DomeinController domeinController, StartScreenController startScreenController, ReportStyle style) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeReportScreen.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.domeinController = domeinController;
        this.startScreenController = startScreenController;
        this.directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select directory");
        this.style = style;
        this.documents = new ArrayList<>();
        this.workbook = new HSSFWorkbook();

        buildGui();
    }

    private void buildGui() {
        lblFout.setVisible(false);
        String defaultSource = xmlUtil.getStringFromPreferences("defaultSource");
        if (defaultSource != null) {
            directoryFile = new File(defaultSource);
            tfLocatie.setText(defaultSource);
        } else {
            directoryFile = null;
            tfLocatie.setText("");
        }

    }

    @FXML
    private void goToPreviousScreen(ActionEvent event) {
        startScreenController.setCenter(new ChooseReportStyleScreenController(domeinController, startScreenController));
    }

    @FXML
    private void kiesLocatie(ActionEvent event) {
        directoryFile = directoryChooser.showDialog(Stage.getWindows().filtered(window -> window.isShowing()).get(0));
        if (directoryFile != null) {
            tfLocatie.setText(directoryFile.getPath());
            xmlUtil.setStringFromPreferences("defaultSource", directoryFile.getPath());
        }
    }

    @FXML
    private void maakReport(ActionEvent event) {
        try {
            if (tfName.getText().toCharArray().length >= 30) {
                throw new IllegalArgumentException("Naam mag niet langer zijn dan 31 karakters");
            }
            if (tfName.getText().toCharArray().length == 0) {
                throw new IllegalArgumentException("Naam mag niet leeg zijn");
            }
            File test = new File(tfLocatie.getText());
            if (!test.isDirectory()) {
                throw new IllegalArgumentException("Locatie is niet geldig");
            }
            if (!test.exists()) {
                throw new IllegalArgumentException("Locatie is niet geldig");
            }

            report = workbook.createSheet(tfName.getText());

            writeReport();
        } catch (IllegalArgumentException ex) {
            lblFout.setVisible(true);
            lblFout.setText(ex.getMessage());
        }
    }

    private void writeReport() throws IllegalArgumentException {
        try {
            File file = new File(tfLocatie.getText() + "\\" + tfName.getText() + ".xls");
            FileOutputStream out = new FileOutputStream(file);
            prepareDocuments();
            createReport();

            workbook.write(out);
            out.close();
            workbook.close();

            showSuccessAlert();

            startScreenController.setCenter(new DocumentManagementScreenController(domeinController, startScreenController));
        } catch (FileNotFoundException ex) {
            throw new IllegalArgumentException("Het pad klopt niet");
        } catch (IOException ex) {
            throw new IllegalArgumentException("Er is een fout opgetreden");
        }
    }

    private void createReport() {

        ReportFactory reportFactory = new ReportFactory(workbook, report, documents, style, workbook.createDataFormat());

        if (!style.equals(ReportStyle.VERGELIJKINGNV) && !style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            reportFactory.createHistoriekReport();
        } else {
            reportFactory.createVergelijkingReport();
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

    private void showSuccessAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Aanmaken gelukt");
        alert.setHeaderText("Aanmaken excel bestand gelukt.");
        alert.setContentText("Bestand is aangemaakt.");
        alert.showAndWait();
    }

    private void compileVergelijking() {
        domeinController.getDocumentBuilders().stream().forEach(doc -> {
            documents.add(doc.addBAVlottendeActiva()
                    .addBALiquideMiddelen()
                    .addBATotaalActiva()
                    .addBPEigenVermogen()
                    .addBPReserves()
                    .addBPOvergedragenWinstVerlies()
                    .addBPSchuldenHoogstens1Jaar()
                    .addRRBedrijfsopbrengsten()
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
                    .addBPSchuldenMeer1JaarHandelsschuldenLeveranciers()
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
                    .build());
        });
    }

    private void compileHistoriek() {
        domeinController.getDocumentBuilders().stream().forEach(doc -> {
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
                    .addBPSchuldenMeer1JaarHandelsschuldenLeveranciers()
                    .addBPSchuldenMeer1JaarOverigeSchulden()
                    .addBPTotaalPassiva()
                    .addRRBedrijfsopbrengsten()
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
                    .build());
        });
    }

    private void checkForEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            maakReport(new ActionEvent());
        }
    }
}
