package Services;

import Models.Enums.PropertyName;
import Models.Enums.ReportStyle;
import Models.DocumentWrapper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

public abstract class ReportService {

    protected final List<DocumentWrapper> documents;

    protected final HSSFSheet reportSheet;
    protected final HSSFSheet ratiosSheet;
    protected final ReportStyle style;
    protected final HSSFWorkbook workbook;

    protected final HSSFDataFormat numberFormatter;
    protected CellStyle NumberBottomTop;
    protected CellStyle PercentStyleBoldBottomTop;

    public ReportService(
            HSSFWorkbook workbook, HSSFSheet reportSheet, HSSFSheet ratiosSheet,
            List<DocumentWrapper> documents, ReportStyle style, HSSFDataFormat numberFormatter) {
        this.workbook = workbook;
        this.reportSheet = reportSheet;
        this.ratiosSheet = ratiosSheet;
        this.documents = documents;
        this.style = style;
        this.numberFormatter = numberFormatter;

        initializeStyles();
    }

    public abstract void create();
    abstract void initializeStyles();

    protected <T> void addCell(int rowNumber, int cellNumber, T value) {
        addCell(rowNumber, cellNumber, value, null, null);
    }

    protected <T> void addCell(int rowNumber, int cellNumber, T value, HSSFCellStyle style) {
        addCell(rowNumber, cellNumber, value, style, null);
    }

    protected <T> void addCell(int rowNumber, int cellNumber, T value, HSSFCellStyle style, CellType cellType) {
        Row row = reportSheet.getRow(rowNumber) == null
                ? reportSheet.createRow(rowNumber)
                : reportSheet.getRow(rowNumber);

        Cell cell = cellType == null
                ? row.createCell(cellNumber)
                : row.createCell(cellNumber, cellType);

        if (value != null) {
            if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else {
                cell.setCellValue((String) value);
            }
        }

        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    protected double getAndereVorderingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.BAVorderingenHoogstens1JaarOverigeVorderingen));
    }

    protected double getAantalWerknemersOpEindeBoekjaar(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.SBAantalWerknemersOpEindeBoekjaar));
    }

    protected double getAantalBediendenOpEindeBoekjaar(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.SBAantalBediendenOpEindeBoekjaar));
    }

    protected double getAantalArbeiderssOpEindeBoekjaar(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.SBAantalArbeidersOpEindeBoekjaar));
    }

    protected double getPersoneelskostenUitzendkrachten(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.SBPersoneelskostenUitzendkrachten));
    }

    protected double getGepresteerdeUrenUitzendkrachten(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.SBGepresteerdeUrenUitzendkrachten));
    }

    protected double getGemiddeldeAantalFTEUitzendkrachten(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.SBGemiddeldAantalFTEUitzendkrachten));
    }

    protected double getSBPersoneelsKosten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBPersoneelskosten));
    }

    protected double getGepresteerdeUren(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBGepresteerdeUren));
    }

    protected double getGemiddeldeAantalFTE(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBGemiddeldeFTE));
    }

    protected double getLiquideMiddelen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BALiquideMiddelen));
    }

    protected double getTotaleActiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BATotaalActiva));
    }

    protected double getReserves(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPReserves));
    }

    protected double getOverdragenWinstVerlies(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPOvergedragenWinstVerlies));
    }

    protected double getBedrijfsOpbrengsten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsopbrengsten));
    }

    protected double getBedrijfsOpbrengstenOmzet(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsopbrengstenOmzet));
    }

    protected double getBedrijfswinstVerlies(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsWinstVerlies));
    }

    protected double getWinstVerliesBoekjaar(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRWinstVerliesBoekjaar));
    }

    protected double getProvisies(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.BPVoorzieningenUitgesteldeBelastingen));
    }

    protected double getFinancieringsLast(int index) {
        return (getKorteTermijnFinancieleSchulden(index) + getLangeTermijnFinancieleSchulden(index)) / getEBITDA(index);
    }

    protected double getCash(int index) {
        return getLiquideMiddelen(index)
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAOverlopendeRekeningen));
    }

    protected double getVoorraadrotatie(int index) {
        return getVoorradenEnBestellingenInUitvoering(index) / getBedrijfsOpbrengsten(index) * 365;
    }

    protected double getKlantenKrediet(int index) {
        return getHandelsvorderingen(index) / getBedrijfsOpbrengsten(index) * 365;
    }

    protected double getLeveranciersKrediet(int index) {
        return getLeveranciers(index) / getBedrijfsOpbrengsten(index) * 365;
    }

    protected double getCashFlow(int index) {
        return getWinstVerliesBoekjaar(index) + getAfschrijvingen(index);
    }

    protected double getWaardeVermindering(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(
                PropertyName.RRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen));
    }

    protected double getBelastingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBelastingenOpResultaat))
                - Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.RROntrekkingenUitgesteldeBelastingen))
                + Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.RROverboekingUitgesteldeBelastingen));
    }

    protected double getDienstenEnDiverseGoederen(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenDienstenDiverseGoederen));
    }

    protected double getAndereKosten(int index) {
        double value = Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenDienstenDiverseGoederen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(
                PropertyName.RRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen))
                + Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenAndereBedrijfskosten))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten));
        if (Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten)) != Double
                .parseDouble(documents.get(index).getPropertiesMap()
                        .get(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten))) {
            value += Double.parseDouble(
                    documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten));
        }
        return value;
    }

    protected double getPersoneelskosten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfskostenBezoldigingenSocialeLastenPensioenen));
    }

    protected double getAankopen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffen));
    }

    protected double getKorteTermijnFinancieleSchulden(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden));
    }

    protected double getLangeTermijnFinancieleSchulden(int index) {
        return Double.parseDouble(
                documents.get(index).getPropertiesMap().get(PropertyName.BPSchuldenMeer1JaarFinancieleSchulden));
    }

    protected double getTotalePassiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPTotaalPassiva));
    }

    protected double getKorteTermijnSchulden(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1Jaar))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPOverlopendeRekeningen));
    }

    protected double getVlottendeActiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAVlottendeActiva));
    }

    protected double getInvesteringen(int index) {
        // NV
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(
                PropertyName.TLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen))
                // BVBA
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLMVAMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.TLFVAMutatiesTijdensBoekjaarAanschaffingen));
    }

    protected double getAfschrijvingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(
                PropertyName.RRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva));
    }

    protected double getVasteActiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAVasteActiva));
    }

    protected double getEigenVermogen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPEigenVermogen));
    }

    protected double getLeveranciers(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenLeveranciers));
    }

    protected double getHandelsvorderingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.BAVorderingenHoogstens1JaarHandelsvorderingen));
    }

    protected double getResultaatVoorBelastingen(int index) {
        return getEBITDA(index) - getAfschrijvingen(index) - getWaardeVermindering(index)
                + getFinancieleResultaten(index) + getUitzonderlijkeResultaten(index);
    }

    protected double getRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen));
    }

    protected double getUitzonderlijkeResultaten(int index) {
        double value = 0;
        if (Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten)) != Double
                .parseDouble(documents.get(index).getPropertiesMap()
                        .get(PropertyName.RRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten))) {
            value += Double.parseDouble(documents.get(index).getPropertiesMap()
                    .get(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten));
        }
        if (Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten)) != Double
                .parseDouble(documents.get(index).getPropertiesMap()
                        .get(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten))) {
            value -= Double.parseDouble(
                    documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten));
        }
        return value;
    }

    protected double getEBITDA(int index) {
        if (style.equals(ReportStyle.HISTORIEKBVBA) || style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            return getBrutoMarge(index) - getBedrijfskostenVoorBerekeningen(index);
        } else if (style.equals(ReportStyle.HISTORIEKNV) || style.equals(ReportStyle.VERGELIJKINGNV)) {
            return getBedrijfsOpbrengsten(index) - getBedrijfskostenVoorBerekeningen(index);
        }
        return 0;
    }

    protected double getEBIT(int index) {
        return getEBITDA(index) - getAfschrijvingen(index) - getWaardeVermindering(index);
    }

    protected double getFinancieleResultaten(int index) {
        double inkom = Double
                .parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleOpbrengsten));
        if (inkom == 0) {
            inkom = Double.parseDouble(
                    documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleOpbrengstenRecurrent));
        }
        double uitgaand = getFinancieleKosten(index);
        if (uitgaand == 0) {
            uitgaand = Double
                    .parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleKostenRecurrent));
        }
        return inkom - uitgaand;
    }

    protected double getFinancieleKosten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleKosten));
    }

    protected double getCoreNettoWerkKapitaal(int index) {
        return getVoorradenEnBestellingenInUitvoering(index) + getHandelsvorderingen(index) - getLeveranciers(index);
    }

    protected double getCapitalEmployed(int index) {
        return getCoreNettoWerkKapitaal(index) + getVasteActiva(index);
    }

    protected double getBedrijfskostenVoorBerekeningen(int index) {
        return getAankopen(index) + getPersoneelskosten(index) + getAndereKosten(index);
    }

    protected double getTotaleBedrijfskosten(int index) {
        return Double.parseDouble((documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskosten)));
    }

    protected double getBrutoMarge(int index) {
        if (style.equals(ReportStyle.HISTORIEKBVBA) || style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BVBABrutomarge));
        } else {
            return getBedrijfsOpbrengsten(index) - getNietRecurenteBedrijfsopbrengsten(index)
                    - getRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen(index)
                    - getBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename(index)
                    - getDienstenEnDiverseGoederen(index);
        }
    }

    protected double getBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename));
    }

    protected double getNietRecurenteBedrijfsopbrengsten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap()
                .get(PropertyName.RRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten));
    }

    protected double getToegevoegdeWaarde(int index) {
        return getBedrijfsOpbrengstenOmzet(index) - getAankopen(index);
    }

    protected double getAndereSchuldenKorteTermijn(int i) throws NumberFormatException {
        return Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1Jaar))
                + Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPOverlopendeRekeningen))
                - getLeveranciers(i) - Double.parseDouble(documents.get(i).getPropertiesMap()
                .get(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden));
    }

    protected double getVoorradenEnBestellingenInUitvoering(int i) {
        return Double.parseDouble(documents.get(i).getPropertiesMap()
                .get(PropertyName.BAVoorradenBestellingenUitvoering));
    }

    protected double getTotaleSchulden(int i) {
        return Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPSchulden));
    }

    protected double getNettoWerkkapitaal(int i) {
        return getVoorradenEnBestellingenInUitvoering(i) + getHandelsvorderingen(i) - getLeveranciers(i);
    }

    protected double getZScoreAltman(int i) {
        double x1 = getNettoWerkkapitaal(i) / getTotaleActiva(i) * 0.717;
        double x2 = getWinstVerliesBoekjaar(i) / getTotaleActiva(i) * 0.847;
        double x3 = getEBIT(i) / getTotaleActiva(i) * 3.107;
        double x4 = getEigenVermogen(i) / getTotaleSchulden(i) * 0.42;
        double x5 = getBedrijfsOpbrengstenOmzet(i) / getTotaleActiva(i) * 0.998;

        return x1 + x2 + x3 + x4 + x5;
    }
}
