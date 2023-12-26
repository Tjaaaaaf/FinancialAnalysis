package Services;

import Models.DocumentWrapper;
import Models.Enums.ReportStyle;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

public class CompareReportService extends ReportService {

    protected HSSFCellStyle Number;
    protected HSSFCellStyle GreyNumber;
    protected HSSFCellStyle RedNumber;
    protected HSSFCellStyle GreenNumber;
    protected HSSFCellStyle DoubleStyle;
    protected HSSFCellStyle PercentStyle;
    protected HSSFCellStyle Grey;

    public CompareReportService(
            HSSFWorkbook workbook, HSSFSheet reportSheet, HSSFSheet ratiosSheet,
            List<DocumentWrapper> documents, ReportStyle style, HSSFDataFormat numberFormatter) {
        super(workbook, reportSheet, ratiosSheet, documents, style, numberFormatter);
    }

    @Override
    public void create() {
        int rowNumber = 0;

        // ROW1
        Row row1 = reportSheet.createRow(0);
        row1.createCell(2).setCellValue("Naam");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempName = row1.createCell(i + 3);
            tempName.setCellValue(documents.get(i).getBusiness().getName());
        }

        rowNumber++;

        // ROW2
        Row row2 = reportSheet.createRow(rowNumber);
        row2.createCell(2).setCellValue("Boekjaar");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempYear = row2.createCell(i + 3);
            tempYear.setCellValue(documents.get(i).getYear());
        }

        rowNumber++;

        // ROW3
        Row row3 = reportSheet.createRow(rowNumber);
        row3.createCell(0).setCellValue("ACTIVA");

        rowNumber++;

        // ROW4
        Row row4 = reportSheet.createRow(rowNumber);
        row4.createCell(1).setCellValue("vlottende activa");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempVA = row4.createCell(i + 3, CellType.NUMERIC);
            tempVA.setCellValue(Math.round(getVlottendeActiva(i)));
            tempVA.setCellStyle(Number);
        }

        rowNumber++;

        addCell(rowNumber, 1, "voorraden en bestellingen in uitvoering", null);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 3, Math.round(getVoorradenEnBestellingenInUitvoering(i)), Number, CellType.NUMERIC);
        }

        rowNumber++;

        addCell(rowNumber, 1, "handelsvorderingen", null);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 3, Math.round(getHandelsvorderingen(i)), Number, CellType.NUMERIC);
        }

        rowNumber++;

        // ROW5
        Row row5 = reportSheet.createRow(rowNumber);
        row5.createCell(1).setCellValue("liquide middelen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempLM = row5.createCell(i + 3, CellType.NUMERIC);
            tempLM.setCellValue(Math.round(getLiquideMiddelen(i)));
            tempLM.setCellStyle(Number);
        }

        rowNumber++;

        // ROW6
        Row row6 = reportSheet.createRow(rowNumber);
        row6.createCell(1).setCellValue("totale activa");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempTA = row6.createCell(i + 3, CellType.NUMERIC);
            tempTA.setCellValue(Math.round(getTotaleActiva(i)));
            tempTA.setCellStyle(Number);
        }

        rowNumber++;

        // ROW7
        Row row7 = reportSheet.createRow(rowNumber);
        row7.createCell(0).setCellValue("PASSIVA");

        rowNumber++;

        // ROW8
        Row row8 = reportSheet.createRow(rowNumber);
        row8.createCell(1).setCellValue("eigen vermogen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempEV = row8.createCell(i + 3, CellType.NUMERIC);
            tempEV.setCellValue(Math.round(getEigenVermogen(i)));
            tempEV.setCellStyle(Number);
        }

        rowNumber++;

        // ROW9
        Row row9 = reportSheet.createRow(rowNumber);
        row9.createCell(1).setCellValue("waarvan reserves");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempWR = row9.createCell(i + 3, CellType.NUMERIC);
            tempWR.setCellValue(Math.round(getReserves(i)));
            tempWR.setCellStyle(Number);
        }

        rowNumber++;

        Row row10 = reportSheet.createRow(rowNumber);
        row10.createCell(1).setCellValue("overgedragen winst");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempOW = row10.createCell(i + 3, CellType.NUMERIC);
            tempOW.setCellValue(Math.round(getOverdragenWinstVerlies(i)));
            tempOW.setCellStyle(Number);
        }

        rowNumber++;

        addCell(rowNumber, 1, "totale schulden", null);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 3, Math.round(getTotaleSchulden(i)), Number, CellType.NUMERIC);
        }

        rowNumber++;

        Row row11 = reportSheet.createRow(rowNumber);
        row11.createCell(1).setCellValue("schulden op korte termijn");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row11.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getKorteTermijnSchulden(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        addCell(rowNumber, 1, "Leveranciersschulden", null);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 3, Math.round(getLeveranciers(i)), Number, CellType.NUMERIC);
        }

        rowNumber++;

        Row row12 = reportSheet.createRow(rowNumber);
        row12.createCell(0).setCellValue("RESULTATEN");

        rowNumber++;

        Row row13 = reportSheet.createRow(rowNumber);
        row13.createCell(1).setCellValue("bedrijfsopbrengsten");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row13.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getBedrijfsOpbrengsten(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row rowOmzet = reportSheet.createRow(rowNumber);
        rowOmzet.createCell(1).setCellValue("omzet");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = rowOmzet.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getBedrijfsOpbrengstenOmzet(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row14 = reportSheet.createRow(rowNumber);
        row14.createCell(1).setCellValue("toegevoegde waarde");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row14.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getToegevoegdeWaarde(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row rowBrutomarge = reportSheet.createRow(rowNumber);
        rowBrutomarge.createCell(1).setCellValue("brutomarge");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = rowBrutomarge.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getBrutoMarge(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row rowEbitda = reportSheet.createRow(rowNumber);
        rowEbitda.createCell(1).setCellValue("ebitda");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = rowEbitda.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getEBITDA(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row15 = reportSheet.createRow(rowNumber);
        row15.createCell(1).setCellValue("afschrijvingen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row15.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getAfschrijvingen(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row16 = reportSheet.createRow(rowNumber);
        row16.createCell(1).setCellValue("bedrijfswinst (ebit)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row16.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getEBIT(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row17 = reportSheet.createRow(rowNumber);
        row17.createCell(1).setCellValue("winst van boekjaar na belastingen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row17.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getWinstVerliesBoekjaar(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row18 = reportSheet.createRow(rowNumber);
        row18.createCell(0).setCellValue("FINANCIËLE RATIO'S");

        rowNumber++;

        Row row19 = reportSheet.createRow(rowNumber);
        row19.createCell(1).setCellValue("cashflow, of kasstroom : nettowinst + afschrijvingen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row19.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getWinstVerliesBoekjaar(i) + getAfschrijvingen(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row20 = reportSheet.createRow(rowNumber);
        row20.createCell(1).setCellValue("liquiditeitsratio: vlottende activa/schulden op korte termijn (>1)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row20.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getVlottendeActiva(i) / getKorteTermijnSchulden(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row21 = reportSheet.createRow(rowNumber);
        row21.createCell(1).setCellValue("solvabiliteitsratio: schulden op korte termijn/totale activa");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row21.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getKorteTermijnSchulden(i) / getTotaleActiva(i));
            tempSKT.setCellStyle(PercentStyle);
        }

        rowNumber++;

        Row rowBedrijfswinst = reportSheet.createRow(rowNumber);
        Cell cellBedrijfswinst = rowBedrijfswinst.createCell(1);
        cellBedrijfswinst.setCellValue("bedrijfswinst/omzet");
        if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            cellBedrijfswinst.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = rowBedrijfswinst.createCell(i + 3, CellType.NUMERIC);
            if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
                tempSKT.setCellValue("/");
                tempSKT.setCellStyle(Grey);
            } else {
                tempSKT.setCellValue(getBedrijfswinstVerlies(i) / getBedrijfsOpbrengstenOmzet(i));
                tempSKT.setCellStyle(PercentStyle);
            }
        }

        rowNumber++;

        Row row22 = reportSheet.createRow(rowNumber);
        Cell cellNWO = row22.createCell(1);
        cellNWO.setCellValue("netto winst/omzet");
        if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            cellNWO.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row22.createCell(i + 3, CellType.NUMERIC);
            if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
                tempSKT.setCellValue("/");
                tempSKT.setCellStyle(Grey);
            } else {
                tempSKT.setCellValue(getWinstVerliesBoekjaar(i) / getBedrijfsOpbrengstenOmzet(i));
                tempSKT.setCellStyle(PercentStyle);
            }
        }

        rowNumber++;

        Row row23 = reportSheet.createRow(rowNumber);
        row23.createCell(1).setCellValue("rentabiliteitsratio vh eigen vermogen: netto winst/eigen vermogen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row23.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getWinstVerliesBoekjaar(i) / getEigenVermogen(i));
            tempSKT.setCellStyle(PercentStyle);
        }

        rowNumber++;

        Row row24 = reportSheet.createRow(rowNumber);
        Cell cellCCC = row24.createCell(1);
        if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            cellCCC.setCellStyle(Grey);
        }
        cellCCC.setCellValue("cash conversion cycle");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row24.createCell(i + 3, CellType.NUMERIC);
            if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
                tempSKT.setCellValue("/");
                tempSKT.setCellStyle(Grey);
            } else {
                tempSKT.setCellValue(
                        Math.round(getVoorraadrotatie(i) + getKlantenKrediet(i) - getLeveranciersKrediet(i)));
                tempSKT.setCellStyle(Number);
            }
        }

        rowNumber++;

        addCell(rowNumber, 1, "netto werkkapitaal (voorraden + vorderingen - leveranciers)", null);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 3,
                    Math.round(
                            getNettoWerkkapitaal(i)),
                    Number, CellType.NUMERIC);
        }

        rowNumber += 2;

        addCell(rowNumber, 1, "Z score Altman (1,80 > < 2,99)", null);

        for (int i = 0; i < documents.size(); i++) {
            HSSFCellStyle style;
            if (getZScoreAltman(i) >= 2.99) {
                style = GreenNumber;
            } else {
                if (getZScoreAltman(i) <= 1.80) {
                    style = RedNumber;
                } else {
                    style = GreyNumber;
                }
            }
            addCell(rowNumber, i + 3, getZScoreAltman(i), style, CellType.NUMERIC);
        }

        rowNumber++;

        Row row25 = reportSheet.createRow(rowNumber);
        row25.createCell(0).setCellValue("PERSONEEL");

        rowNumber++;

        Row row26 = reportSheet.createRow(rowNumber);
        row26.createCell(1).setCellValue("gemiddeld aantal FTE (1003)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row26.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getGemiddeldeAantalFTE(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row27 = reportSheet.createRow(rowNumber);
        row27.createCell(1).setCellValue("gepresteerde uren (1013)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row27.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getGepresteerdeUren(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row28 = reportSheet.createRow(rowNumber);
        row28.createCell(1).setCellValue("personeelskosten (1023)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row28.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getSBPersoneelsKosten(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber += 2;

        Row row30 = reportSheet.createRow(rowNumber);
        row30.createCell(1).setCellValue("gemiddeld aantal FTE uitzendkrachten (150)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row30.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getGemiddeldeAantalFTEUitzendkrachten(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row31 = reportSheet.createRow(rowNumber);
        row31.createCell(1).setCellValue("gepresteerde uren uitzendkrachten (151)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row31.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getGepresteerdeUrenUitzendkrachten(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row32 = reportSheet.createRow(rowNumber);
        row32.createCell(1).setCellValue("personeelskosten uitzendkrachten (152)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row32.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getPersoneelskostenUitzendkrachten(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber += 2;

        Row row34 = reportSheet.createRow(rowNumber);
        row34.createCell(1).setCellValue("aantal werknemers op 31/12 (105/3)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row34.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalWerknemersOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row35 = reportSheet.createRow(rowNumber);
        row35.createCell(1).setCellValue("bedienden op 31/12 (134/3)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row35.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalBediendenOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row36 = reportSheet.createRow(rowNumber);
        row36.createCell(1).setCellValue("arbeiders op 31/12 (134/3)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row36.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalArbeiderssOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row37 = reportSheet.createRow(rowNumber);
        row37.createCell(0).setCellValue("PERSONEELRATIO'S");

        rowNumber++;

        Row row38 = reportSheet.createRow(rowNumber);
        row38.createCell(1).setCellValue("personeelskost/aantal FTE");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row38.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getSBPersoneelsKosten(i) / getGemiddeldeAantalFTE(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row39 = reportSheet.createRow(rowNumber);
        row39.createCell(1).setCellValue("personeelskost/gepresteerde uren");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row39.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getSBPersoneelsKosten(i) / getGepresteerdeUren(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber += 2;

        Row row41 = reportSheet.createRow(rowNumber);
        row41.createCell(1).setCellValue("personeelskost uitzendkrachten/aantal FTE uitzendkrachten");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row41.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(
                    Math.round(getPersoneelskostenUitzendkrachten(i) / getGemiddeldeAantalFTEUitzendkrachten(i)));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row42 = reportSheet.createRow(rowNumber);
        row42.createCell(1).setCellValue("personeelskost uitzendkrachten/gepresteerde uren uitzendkrachten");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row42.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getPersoneelskostenUitzendkrachten(i) / getGepresteerdeUrenUitzendkrachten(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber += 2;

        Row row44 = reportSheet.createRow(rowNumber);
        Cell cellOTAGU = row44.createCell(1);
        cellOTAGU.setCellValue("omzet/totaal aantal gepresteerde uren (eigen + interim)");
        if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            cellOTAGU.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row44.createCell(i + 3, CellType.NUMERIC);
            if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
                tempSKT.setCellValue("/");
                tempSKT.setCellStyle(Grey);
            } else {
                tempSKT.setCellValue(
                        getBedrijfsOpbrengstenOmzet(i)
                                / (getGepresteerdeUrenUitzendkrachten(i) + getGepresteerdeUren(i)));
                tempSKT.setCellStyle(DoubleStyle);
            }
        }

        rowNumber++;

        Row row45 = reportSheet.createRow(rowNumber);
        row45.createCell(1).setCellValue("netto winst/totaal aantal gepresteerde uren (eigen + interim)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row45.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(
                    getWinstVerliesBoekjaar(i) / (getGepresteerdeUrenUitzendkrachten(i) + getGepresteerdeUren(i)));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row46 = reportSheet.createRow(rowNumber);
        row46.createCell(1).setCellValue("cashflow/totaal aantal gepresteerde uren (eigen + interim)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row46.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getCashFlow(i) / (getGepresteerdeUrenUitzendkrachten(i) + getGepresteerdeUren(i)));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber += 2;

        Row row48 = reportSheet.createRow(rowNumber);
        row48.createCell(1).setCellValue("verhouding arbeiders/bedienden (31/12)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row48.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalArbeiderssOpEindeBoekjaar(i) / getAantalBediendenOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        rowNumber++;

        Row row49 = reportSheet.createRow(rowNumber);
        Cell cellOB = row49.createCell(1);
        cellOB.setCellValue("omzet/bediende (31/12)");
        if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            cellOB.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row49.createCell(i + 3, CellType.NUMERIC);
            if (style.equals(ReportStyle.VERGELIJKINGBVBA)) {
                tempSKT.setCellValue("/");
                tempSKT.setCellStyle(Grey);
            } else {
                tempSKT.setCellValue(getBedrijfsOpbrengstenOmzet(i) / getAantalBediendenOpEindeBoekjaar(i));
                tempSKT.setCellStyle(Number);
            }
        }

        rowNumber++;

        Row row50 = reportSheet.createRow(rowNumber);
        row50.createCell(1).setCellValue("netto winst/bediende (31/12)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row50.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getWinstVerliesBoekjaar(i) / getAantalBediendenOpEindeBoekjaar(i));
            tempSKT.setCellStyle(Number);
        }

        rowNumber++;

        Row row51 = reportSheet.createRow(rowNumber);
        row51.createCell(1).setCellValue("netto winst bediende (31/12) per uur: netto winst/bediende (31/12)/1744");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row51.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue((getWinstVerliesBoekjaar(i) / getAantalBediendenOpEindeBoekjaar(i)) / 1744);
            tempSKT.setCellStyle(DoubleStyle);
        }
    }

    @Override
    void initializeStyles() {
        HSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true);

        Grey = workbook.createCellStyle();
        Grey.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        Grey.setFont(fontBold);
        Grey.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Number = workbook.createCellStyle();
        Number.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        GreyNumber = workbook.createCellStyle();
        GreyNumber.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        GreyNumber.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        GreyNumber.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        RedNumber = workbook.createCellStyle();
        RedNumber.setFillForegroundColor(IndexedColors.RED.getIndex());
        RedNumber.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        RedNumber.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        GreenNumber = workbook.createCellStyle();
        GreenNumber.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        GreenNumber.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        GreenNumber.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        PercentStyle = workbook.createCellStyle();
        PercentStyle.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));

        DoubleStyle = workbook.createCellStyle();
        DoubleStyle.setDataFormat(numberFormatter.getFormat("### ### ##0.00"));
    }
}
