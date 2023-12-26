package Services;

import Models.DocumentWrapper;
import Models.Enums.PropertyName;
import Models.Enums.ReportStyle;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

public class HistoryReportService extends ReportService {

    protected HSSFCellStyle BoldGreenCenter;
    protected HSSFCellStyle BoldGreen;
    protected HSSFCellStyle BoldGreenNumber;
    protected HSSFCellStyle Bold;
    protected HSSFCellStyle BoldGrey;
    protected HSSFCellStyle BoldYellow;
    protected HSSFCellStyle BoldLightGrey;
    protected HSSFCellStyle BoldBottom;
    protected HSSFCellStyle BoldBottomTop;
    protected HSSFCellStyle BoldBlue;
    protected HSSFCellStyle BottomNormal;
    protected HSSFCellStyle BoldTop;
    protected HSSFCellStyle BoldNumber;
    protected HSSFCellStyle BoldYellowNumber;
    protected HSSFCellStyle BoldLightGreyNumber;
    protected HSSFCellStyle BoldBottomNumber;
    protected HSSFCellStyle BoldTopNumber;
    protected HSSFCellStyle BoldTopDouble;
    protected HSSFCellStyle PercentStyleBold;
    protected HSSFCellStyle DoubleStyleBold;
    protected HSSFCellStyle DoubleStyleBoldTop;
    protected HSSFCellStyle PercentStyleBoldBottom;
    protected HSSFCellStyle Grey;

    public HistoryReportService(
            HSSFWorkbook workbook, HSSFSheet reportSheet, HSSFSheet ratiosSheet,
            List<DocumentWrapper> documents, ReportStyle style, HSSFDataFormat numberFormatter) {
        super(workbook, reportSheet, ratiosSheet, documents, style, numberFormatter);
    }

    @Override
    public void create() {
        int rowNumber = 1;

        // ROW 2
        addCell(rowNumber, 0, "NAAM", BoldBottomTop);

        addCell(rowNumber, 1, "PER", BoldBottomTop);

        addCell(rowNumber, 2, "31/12", BoldBottomTop);

        addCell(rowNumber, documents.size() + 2, null, BoldTop);
        addCell(rowNumber, documents.size() + 3, null, BoldTop);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i, documents.get(i).getYear(), BoldGrey);
        }

        rowNumber++;

        // ROW 3
        addCell(rowNumber, 0,
                documents.get(0).getBusiness().getName() + (style.equals(ReportStyle.HISTORIEKNV) ? " NV" : " BVBA"),
                BoldGreenCenter);

        addCell(rowNumber, documents.size() + 2, "CORE NETTO WERK KAPITAAL", Bold);
        addCell(rowNumber, documents.size() + 3, "voorraden+vorderingen-leveranciers");

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i, Math.round(getCoreNettoWerkKapitaal(i)),
                    BoldNumber, CellType.NUMERIC);
        }

        rowNumber += 2;

        // ROW 5
        addCell(rowNumber, documents.size() + 2, "CAPITAL EMPLOYED", BoldBottom);
        addCell(rowNumber, documents.size() + 3, "netto werkkapitaal+vaste activa", BottomNormal);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i, Math.round(getCapitalEmployed(i)),
                    BoldBottomNumber, CellType.NUMERIC);
        }

        rowNumber++;

        // ROW 6
        addCell(rowNumber, 0, "BALANS ACTIVA", BoldGrey);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, documents.get(i).getYear(), BoldGrey);
        }

        rowNumber++;

        // ROW 7
        addCell(rowNumber, documents.size() + 2, "EBIT MARGE",
                style.equals(ReportStyle.HISTORIEKNV) ? BoldTop : Grey);

        addCell(rowNumber, documents.size() + 3, "EBIT",
                style.equals(ReportStyle.HISTORIEKNV) ? BoldBottomTop : Grey);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i,
                    style.equals(ReportStyle.HISTORIEKNV) ? getEBIT(i) / getBedrijfsOpbrengsten(i) : "/",
                    style.equals(ReportStyle.HISTORIEKNV) ? PercentStyleBold : Grey);
        }

        rowNumber++;

        // ROW 8
        addCell(rowNumber, 0, "VASTE ACTIVA", BoldYellow);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getVasteActiva(i)), BoldYellowNumber, CellType.NUMERIC);
        }

        addCell(rowNumber, documents.size() + 3, "Omzet", style.equals(ReportStyle.HISTORIEKNV) ? Bold : Grey);

        rowNumber++;

        // ROW 9
        addCell(rowNumber, 0, "IMMATERIELE (evt. Goodwill)", Bold);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(Double
                            .parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BAImmaterieleVasteActiva))),
                    BoldNumber, CellType.NUMERIC);
        }

        rowNumber++;

        // ROW 10
        addCell(rowNumber, 0, "MATERIELE", Bold);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(
                            Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BAMaterieleVasteActiva))),
                    BoldNumber, CellType.NUMERIC);
        }

        addCell(rowNumber, documents.size() + 2, "EBITDA MARGE",
                style.equals(ReportStyle.HISTORIEKNV) ? Bold : Grey);

        addCell(rowNumber, documents.size() + 3, "EBITDA",
                style.equals(ReportStyle.HISTORIEKNV) ? BoldBottom : Grey);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i,
                    style.equals(ReportStyle.HISTORIEKNV) ? getEBITDA(i) / getBedrijfsOpbrengsten(i) : "/",
                    style.equals(ReportStyle.HISTORIEKNV) ? PercentStyleBold : Grey);
        }

        rowNumber++;

        // ROW 11
        addCell(rowNumber, 0, "FINANCIELE", Bold);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(
                            Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BAFinancieleVasteActiva))),
                    BoldNumber, CellType.NUMERIC);
        }

        addCell(rowNumber, documents.size() + 2, "> 12-15%", style.equals(ReportStyle.HISTORIEKNV) ? Bold : Grey);

        addCell(rowNumber, documents.size() + 3, "OMZET", style.equals(ReportStyle.HISTORIEKNV) ? Bold : Grey);

        rowNumber++;

        // ROW 12
        addCell(rowNumber, 0, "VLOTTENDE ACTIVA", BoldYellow);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getVlottendeActiva(i)), BoldYellowNumber, CellType.NUMERIC);
        }

        rowNumber++;

        // ROW 13
        addCell(rowNumber, 0, "VOORRADEN", Bold);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getVoorradenEnBestellingenInUitvoering(i)),
                    BoldNumber, CellType.NUMERIC);
        }

        addCell(rowNumber, documents.size() + 2, "RENDEMENT EIGEN", Bold);

        addCell(rowNumber, documents.size() + 3, "NETTO WINST", BoldBottom);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i, getWinstVerliesBoekjaar(i) / getEigenVermogen(i),
                    PercentStyleBold);
        }

        rowNumber++;

        // ROW 14
        addCell(rowNumber, 0, "HANDELSVORDERINGEN", Bold);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getHandelsvorderingen(i)), BoldNumber, CellType.NUMERIC);
        }
        addCell(rowNumber, 2 + documents.size(), "VERMOGEN >?", Bold);
        addCell(rowNumber, 3 + documents.size(), "EIGEN VERMOGEN", Bold);

        rowNumber++;

        // ROW 15
        addCell(rowNumber, 0, "ANDERE", Bold);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getAndereVorderingen(i)), BoldNumber, CellType.NUMERIC);
        }

        rowNumber++;

        // ROW 16
        addCell(rowNumber, 0, "CASH", Bold);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getCash(i)), BoldNumber, CellType.NUMERIC);
        }
        addCell(rowNumber, 2 + documents.size(), "ROTATIE", style.equals(ReportStyle.HISTORIEKNV) ? Bold : Grey);

        addCell(rowNumber, 3 + documents.size(), "OMZET", style.equals(ReportStyle.HISTORIEKNV) ? BoldBottom : Grey);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i,
                    style.equals(ReportStyle.HISTORIEKNV) ? getBedrijfsOpbrengsten(i) / getCapitalEmployed(i) : "/",
                    style.equals(ReportStyle.HISTORIEKNV) ? DoubleStyleBold : Grey,
                    CellType.NUMERIC);
        }

        rowNumber++;

        // ROW 17
        addCell(rowNumber, 0, "TOTALE ACTIVA", BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getTotaleActiva(i)), BoldYellowNumber, CellType.NUMERIC);
        }

        addCell(rowNumber, 3 + documents.size(), "CAPITAL EMPLOTYED", style.equals(ReportStyle.HISTORIEKNV) ? Bold : Grey);

        rowNumber += 2;

        // ROW 19
        addCell(rowNumber, 0, "BALANS PASSIVA", BoldGrey);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, documents.get(i).getYear(), BoldGrey);
        }

        addCell(rowNumber, 2 + documents.size(), "RENDEMENT OP DE", Bold);
        addCell(rowNumber, 3 + documents.size(), "EBIT", BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i, getEBIT(i) / getCapitalEmployed(i), PercentStyleBold);
        }

        rowNumber++;

        // ROW 20
        addCell(rowNumber, 2 + documents.size(), "INGEZETTE MIDDELEN (ROCE)", Bold);
        addCell(rowNumber, 3 + documents.size(), "CAPITAL EMPLOYED", Bold);

        rowNumber++;

        // ROW 21
        addCell(rowNumber, 0, "EIGEN VERMOGEN", BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1, Math.round(getEigenVermogen(i)), BoldYellowNumber, CellType.NUMERIC);
        }

        addCell(rowNumber, 2 + documents.size(), ">WACC(10%?)", BoldBottom);
        addCell(rowNumber, 3 + documents.size(), null, BottomNormal);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i, null, BottomNormal);
        }

        rowNumber += 2;

        // ROW 23
        addCell(rowNumber, 0, "SCHULDEN", BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, i + 1,
                    Math.round(getLangeTermijnFinancieleSchulden(i) + getKorteTermijnSchulden(i) + getProvisies(i)),
                    BoldYellowNumber, CellType.NUMERIC);
        }

        addCell(rowNumber, 2 + documents.size(), "VOORRAADROTATIE",
                style.equals(ReportStyle.HISTORIEKNV) ? BoldTop : Grey);

        addCell(rowNumber, 3 + documents.size(), "VOORRADEN X 365",
                style.equals(ReportStyle.HISTORIEKNV) ? BoldBottomTop : Grey);

        for (int i = 0; i < documents.size(); i++) {
            addCell(rowNumber, documents.size() + 4 + i,
                    style.equals(ReportStyle.HISTORIEKNV) ? Math.round(getVoorraadrotatie(i)) : "/",
                    style.equals(ReportStyle.HISTORIEKNV) ? BoldTopNumber : Grey, CellType.NUMERIC);
        }

        rowNumber++;

        Row row24 = reportSheet.createRow(rowNumber);
        Cell temp64 = row24.createCell(0);
        temp64.setCellValue("PROVISIES");
        temp64.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp65 = row24.createCell(i + 1, CellType.NUMERIC);
            temp65.setCellValue(Math.round(getProvisies(i)));
            temp65.setCellStyle(BoldNumber);
        }

        Cell temp66 = row24.createCell(3 + documents.size());
        temp66.setCellValue("OMZET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp66.setCellStyle(Bold);
        } else {
            temp66.setCellStyle(Grey);
        }

        rowNumber++;

        Row row25 = reportSheet.createRow(rowNumber);
        Cell temp67 = row25.createCell(0);
        temp67.setCellValue("LANGE TERMIJN SCHULDEN");
        temp67.setCellStyle(BoldLightGrey);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp68 = row25.createCell(i + 1, CellType.NUMERIC);
            temp68.setCellValue(Math.round(
                    Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPSchuldenMeer1Jaar))));
            temp68.setCellStyle(BoldLightGreyNumber);
        }

        rowNumber++;

        Row row26 = reportSheet.createRow(rowNumber);
        Cell temp69 = row26.createCell(0);
        temp69.setCellValue("FINANCIELE");
        temp69.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp70 = row26.createCell(i + 1, CellType.NUMERIC);
            temp70.setCellValue(Math.round(getLangeTermijnFinancieleSchulden(i)));
            temp70.setCellStyle(BoldNumber);
        }

        Cell temp71 = row26.createCell(2 + documents.size());
        temp71.setCellValue("KLANTENKREDIET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp71.setCellStyle(Bold);
        } else {
            temp71.setCellStyle(Grey);
        }

        Cell temp72 = row26.createCell(3 + documents.size());
        temp72.setCellValue("VORDERINGEN X 365");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp72.setCellStyle(BoldBottom);
        } else {
            temp72.setCellStyle(Grey);
        }

        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp73 = row26.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp73.setCellValue(Math.round(getKlantenKrediet(i)));
                temp73.setCellStyle(BoldNumber);
            } else {
                temp73.setCellValue("/");
                temp73.setCellStyle(Grey);
            }
        }

        rowNumber++;

        Row row27 = reportSheet.createRow(rowNumber);
        Cell temp74 = row27.createCell(0);
        temp74.setCellValue("ANDERE");
        temp74.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp75 = row27.createCell(i + 1, CellType.NUMERIC);
            temp75.setCellValue(Math.round(Double.parseDouble(
                    documents.get(i).getPropertiesMap().get(PropertyName.BPSchuldenMeer1JaarOverigeSchulden))));
            temp75.setCellStyle(BoldNumber);
        }

        Cell temp76 = row27.createCell(2 + documents.size());
        temp76.setCellValue("DSO");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp76.setCellStyle(Bold);
        } else {
            temp76.setCellStyle(Grey);
        }

        Cell temp77 = row27.createCell(3 + documents.size());
        temp77.setCellValue("OMZET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp77.setCellStyle(Bold);
        } else {
            temp77.setCellStyle(Grey);
        }

        rowNumber++;

        Row row28 = reportSheet.createRow(rowNumber);
        Cell temp78 = row28.createCell(0);
        temp78.setCellValue("KORTE TERMIJN SCHULDEN");
        temp78.setCellStyle(BoldLightGrey);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp79 = row28.createCell(i + 1, CellType.NUMERIC);
            temp79.setCellValue(Math.round(getKorteTermijnSchulden(i)));
            temp79.setCellStyle(BoldLightGreyNumber);
        }

        rowNumber++;

        Row row29 = reportSheet.createRow(rowNumber);
        Cell temp80 = row29.createCell(0);
        temp80.setCellValue("FINANCIELE");
        temp80.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp81 = row29.createCell(i + 1, CellType.NUMERIC);
            temp81.setCellValue(Math.round(getKorteTermijnFinancieleSchulden(i)));
            temp81.setCellStyle(BoldNumber);
        }

        Cell temp82 = row29.createCell(2 + documents.size());
        temp82.setCellValue("LEVERANCIERSKREDIET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp82.setCellStyle(Bold);
        } else {
            temp82.setCellStyle(Grey);
        }

        Cell temp83 = row29.createCell(3 + documents.size());
        temp83.setCellValue("LEVERANCIERS X 365");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp83.setCellStyle(BoldBottom);
        } else {
            temp83.setCellStyle(Grey);
        }
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp84 = row29.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp84.setCellValue(Math.round(getLeveranciersKrediet(i)));
                temp84.setCellStyle(BoldNumber);
            } else {
                temp84.setCellValue("/");
                temp84.setCellStyle(Grey);
            }
        }

        rowNumber++;

        Row row30 = reportSheet.createRow(rowNumber);
        Cell temp85 = row30.createCell(0);
        temp85.setCellValue("LEVERANCIERS");
        temp85.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp86 = row30.createCell(i + 1, CellType.NUMERIC);
            temp86.setCellValue(Math.round(getLeveranciers(i)));
            temp86.setCellStyle(BoldNumber);
        }

        Cell temp87 = row30.createCell(2 + documents.size());
        temp87.setCellValue("DPO");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp87.setCellStyle(Bold);
        } else {
            temp87.setCellStyle(Grey);
        }

        Cell temp88 = row30.createCell(3 + documents.size());
        temp88.setCellValue("OMZET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp88.setCellStyle(Bold);
        } else {
            temp88.setCellStyle(Grey);
        }

        rowNumber++;

        Row row31 = reportSheet.createRow(rowNumber);
        Cell temp89 = row31.createCell(0);
        temp89.setCellValue("ANDERE");
        temp89.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp90 = row31.createCell(i + 1, CellType.NUMERIC);
            temp90.setCellValue(Math.round(getAndereSchuldenKorteTermijn(i)));
            temp90.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row32 = reportSheet.createRow(rowNumber);
        Cell temp91 = row32.createCell(0);
        temp91.setCellValue("TOTALE PASSIVA");
        temp91.setCellStyle(BoldYellow);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp92 = row32.createCell(i + 1, CellType.NUMERIC);
            temp92.setCellValue(Math.round(getTotalePassiva(i)));
            temp92.setCellStyle(BoldYellowNumber);
        }

        Cell temp93 = row32.createCell(2 + documents.size());
        temp93.setCellValue("CASH FLOW");
        temp93.setCellStyle(Bold);
        Cell temp94 = row32.createCell(3 + documents.size());
        temp94.setCellValue("NETTO WINST + AFSCHRIJVINGEN");
        temp94.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp95 = row32.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp95.setCellValue(Math.round(getCashFlow(i)));
            temp95.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row33 = reportSheet.createRow(rowNumber);
        Cell temp96 = row33.createCell(2 + documents.size());
        temp96.setCellValue("marge / omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp96.setCellStyle(Bold);
        } else {
            temp96.setCellStyle(Grey);
            row33.createCell(3 + documents.size()).setCellStyle(Grey);
        }

        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp97 = row33.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp97.setCellValue(getCashFlow(i) / getBedrijfsOpbrengsten(i));
                temp97.setCellStyle(PercentStyleBold);
            } else {
                temp97.setCellValue("/");
                temp97.setCellStyle(Grey);
            }

        }

        rowNumber += 2;

        Row row35 = reportSheet.createRow(rowNumber);
        Cell temp98 = row35.createCell(0);
        temp98.setCellValue("RESULTATENREKENING");
        temp98.setCellStyle(BoldGrey);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp99 = row35.createCell(i + 1);
            temp99.setCellValue(documents.get(i).getYear());
            temp99.setCellStyle(BoldGrey);
        }

        Cell temp100 = row35.createCell(2 + documents.size());
        temp100.setCellValue("FREE CASH FLOW");
        temp100.setCellStyle(Bold);
        Cell temp101 = row35.createCell(3 + documents.size());
        temp101.setCellValue("CASH FLOW - INVESTERINGEN");
        temp101.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp102 = row35.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp102.setCellValue(Math.round(getCashFlow(i) - getInvesteringen(i)));
            temp102.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row36 = reportSheet.createRow(rowNumber);
        Cell temp103 = row36.createCell(2 + documents.size());
        temp103.setCellValue("marge / omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp103.setCellStyle(Bold);
        } else {
            temp103.setCellStyle(Grey);
            row36.createCell(3 + documents.size()).setCellStyle(Grey);
        }

        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp104 = row36.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp104.setCellValue((getCashFlow(i) - getInvesteringen(i)) / getBedrijfsOpbrengsten(i));
                temp104.setCellStyle(PercentStyleBold);
            } else {
                temp104.setCellValue("/");
                temp104.setCellStyle(Grey);
            }
        }

        rowNumber++;

        Row row37 = reportSheet.createRow(rowNumber);
        Cell temp105 = row37.createCell(0);
        temp105.setCellValue("BEDRIJFSOPBRENGSTEN");
        temp105.setCellStyle(BoldYellow);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp106 = row37.createCell(i + 1, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKBVBA)) {
                temp106.setCellValue("/");
            } else {
                temp106.setCellValue(Math.round(getBedrijfsOpbrengsten(i)));
            }
            temp106.setCellStyle(BoldYellowNumber);
        }

        rowNumber += 2;

        Row omzet = reportSheet.createRow(rowNumber);
        Cell tempOmzet = omzet.createCell(0);
        tempOmzet.setCellValue("OMZET");
        tempOmzet.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell tempOmzetValue = omzet.createCell(1);
            tempOmzetValue.setCellValue(getBedrijfsOpbrengstenOmzet(i));
            tempOmzetValue.setCellStyle(BoldNumber);
        }

        rowNumber += 2;

        Row row39 = reportSheet.createRow(rowNumber);
        Cell temp107 = row39.createCell(0);
        temp107.setCellValue("BEDRIJFSKOSTEN");
        temp107.setCellStyle(BoldYellow);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp108 = row39.createCell(i + 1, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKBVBA)) {
                temp108.setCellValue("/");
            } else {
                temp108.setCellValue(-Math.round(getTotaleBedrijfskosten(i)));
            }
            temp108.setCellStyle(BoldYellowNumber);
        }

        Cell temp109 = row39.createCell(2 + documents.size());
        temp109.setCellValue("CASH CYCLE");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp109.setCellStyle(BoldBlue);
        } else {
            temp109.setCellStyle(Grey);
        }

        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp110 = row39.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp110.setCellValue(
                        Math.round(getVoorraadrotatie(i) + getKlantenKrediet(i) - getLeveranciersKrediet(i)));
                temp110.setCellStyle(BoldBlue);
            } else {
                temp110.setCellValue("/");
                temp110.setCellStyle(Grey);
            }

        }
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            row39.createCell(3 + documents.size()).setCellStyle(BoldBlue);
        } else {
            row39.createCell(3 + documents.size()).setCellStyle(Grey);
        }

        rowNumber += 2;

        Row row41 = reportSheet.createRow(rowNumber);
        Cell temp111 = row41.createCell(0);
        temp111.setCellValue("AANKOPEN");
        if (style.equals(ReportStyle.HISTORIEKBVBA)) {
            temp111.setCellStyle(Grey);
        } else {
            temp111.setCellStyle(Bold);
        }
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp112 = row41.createCell(i + 1, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKBVBA)) {
                temp112.setCellValue("/");
                temp112.setCellStyle(Grey);
            } else {
                temp112.setCellValue(-Math.round(getAankopen(i)));
                temp112.setCellStyle(BoldNumber);
            }
        }

        Cell temp115 = row41.createCell(2 + documents.size());
        temp115.setCellValue("CURRENT RATIO");
        temp115.setCellStyle(BoldTop);
        Cell temp116 = row41.createCell(3 + documents.size());
        temp116.setCellValue("COURANTE ACTIVA");
        temp116.setCellStyle(BoldBottomTop);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp117 = row41.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp117.setCellValue(getVlottendeActiva(i) / getKorteTermijnSchulden(i));
            temp117.setCellStyle(DoubleStyleBoldTop);
        }

        rowNumber++;

        Row row42 = reportSheet.createRow(rowNumber);
        Cell temp1110 = row42.createCell(0);
        temp1110.setCellValue("TOEGEVOEGDE WAARDE");
        temp1110.setCellStyle(BoldGreen);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp112 = row42.createCell(i + 1, CellType.NUMERIC);
            temp112.setCellValue(Math.round(getToegevoegdeWaarde(i)));
            temp112.setCellStyle(BoldGreenNumber);
        }

        Cell temp120 = row42.createCell(2 + documents.size());
        temp120.setCellValue(">1");
        temp120.setCellStyle(Bold);

        Cell temp121 = row42.createCell(3 + documents.size());
        temp121.setCellValue("COURANTE PASSIVA");
        temp121.setCellStyle(Bold);

        rowNumber += 2;

        Row rowDiensten = reportSheet.createRow(rowNumber);
        Cell tempDiensten = rowDiensten.createCell(0);
        tempDiensten.setCellValue("DIENSTEN EN DIVERSE GOEDEREN");
        tempDiensten.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp114 = rowDiensten.createCell(i + 1, CellType.NUMERIC);
            temp114.setCellValue(-Math.round(getDienstenEnDiverseGoederen(i)));
            temp114.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row rowBrutoMarge = reportSheet.createRow(rowNumber);
        Cell tempBrutoMarge = rowBrutoMarge.createCell(0);
        tempBrutoMarge.setCellValue("BRUTOMARGE");
        tempBrutoMarge.setCellStyle(BoldGreen);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp112 = rowBrutoMarge.createCell(i + 1, CellType.NUMERIC);
            temp112.setCellValue(Math.round(getBrutoMarge(i)));
            temp112.setCellStyle(BoldGreenNumber);
        }

        Cell temp124 = rowBrutoMarge.createCell(2 + documents.size());
        temp124.setCellValue("QUICK RATIO");
        temp124.setCellStyle(Bold);
        Cell temp125 = rowBrutoMarge.createCell(3 + documents.size());
        temp125.setCellValue("COURANTE ACTIVA - VOORRAAD");
        temp125.setCellStyle(BoldBottom);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp126 = rowBrutoMarge.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp126.setCellValue(
                    (getVlottendeActiva(i) - getVoorradenEnBestellingenInUitvoering(i)) / getKorteTermijnSchulden(i));
            temp126.setCellStyle(DoubleStyleBold);
        }

        rowNumber++;

        Row row45 = reportSheet.createRow(rowNumber);
        Cell temp127 = row45.createCell(2 + documents.size());
        temp127.setCellValue(">0,7");
        temp127.setCellStyle(Bold);

        Cell temp128 = row45.createCell(3 + documents.size());
        temp128.setCellValue("COURANTE PASSIVA");
        temp128.setCellStyle(Bold);

        rowNumber++;

        Row row43 = reportSheet.createRow(rowNumber);
        Cell temp113 = row43.createCell(0);
        temp113.setCellValue("PERSONEELSKOSTEN");
        temp113.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp114 = row43.createCell(i + 1, CellType.NUMERIC);
            temp114.setCellValue(-Math.round(getPersoneelskosten(i)));
            temp114.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row44 = reportSheet.createRow(rowNumber);
        Cell temp118 = row44.createCell(0);
        temp118.setCellValue("ANDERE KOSTEN");
        temp118.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp119 = row44.createCell(i + 1, CellType.NUMERIC);
            temp119.setCellValue(-Math.round(getAndereKosten(i) - getDienstenEnDiverseGoederen(i)));
            temp119.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row46 = reportSheet.createRow(rowNumber);
        Cell temp122 = row46.createCell(0);
        temp122.setCellValue("EBITDA");
        temp122.setCellStyle(BoldGreen);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp123 = row46.createCell(i + 1, CellType.NUMERIC);
            temp123.setCellValue(Math.round(getEBITDA(i)));
            temp123.setCellStyle(BoldGreenNumber);
        }

        Cell temp133 = row46.createCell(2 + documents.size());
        temp133.setCellValue("SOLVABILITEIT");
        temp133.setCellStyle(Bold);
        Cell temp134 = row46.createCell(3 + documents.size());
        temp134.setCellValue("EIGEN VERMOGEN");
        temp134.setCellStyle(BoldBottom);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp135 = row46.createCell(documents.size() + 4 + i);
            temp135.setCellValue(getEigenVermogen(i) / getTotalePassiva(i));
            temp135.setCellStyle(PercentStyleBold);
        }

        rowNumber++;

        Row rowPercentTotPas = reportSheet.createRow(rowNumber);
        Cell temp138 = rowPercentTotPas.createCell(2 + documents.size());
        temp138.setCellValue(">25%");
        temp138.setCellStyle(Bold);

        Cell temp139 = rowPercentTotPas.createCell(3 + documents.size());
        temp139.setCellValue("TOTALE PASSIVA");
        temp139.setCellStyle(Bold);

        rowNumber++;

        Row row48 = reportSheet.createRow(rowNumber);
        Cell temp129 = row48.createCell(0);
        temp129.setCellValue("AFSCHRIJVINGEN");
        temp129.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp130 = row48.createCell(i + 1, CellType.NUMERIC);
            temp130.setCellValue(-Math.round(getAfschrijvingen(i)));
            temp130.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row49 = reportSheet.createRow(rowNumber);
        Cell temp131 = row49.createCell(0);
        temp131.setCellValue("WAARDEVERMINDERINGEN");
        temp131.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp132 = row49.createCell(i + 1, CellType.NUMERIC);
            temp132.setCellValue(-Math.round(getWaardeVermindering(i)));
            temp132.setCellStyle(BoldNumber);
        }

        Cell temp142 = row49.createCell(2 + documents.size());
        temp142.setCellValue("GEARING");
        temp142.setCellStyle(Bold);
        Cell temp143 = row49.createCell(3 + documents.size());
        temp143.setCellValue("NETTO FINANCIELE SCHULDEN");
        temp143.setCellStyle(BoldBottom);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp144 = row49.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp144.setCellValue(
                    (getKorteTermijnFinancieleSchulden(i) + getLangeTermijnFinancieleSchulden(i) - getCash(i))
                            / getEigenVermogen(i));
            temp144.setCellStyle(DoubleStyleBold);
        }

        rowNumber++;

        Row row52 = reportSheet.createRow(rowNumber);
        Cell temp1460 = row52.createCell(2 + documents.size());
        temp1460.setCellValue("<1");
        temp1460.setCellStyle(BoldBottom);
        Cell temp1461 = row52.createCell(3 + documents.size());
        temp1461.setCellValue("EIGEN VERMOGEN");
        temp1461.setCellStyle(BoldBottom);
        for (
                int i = 0; i < documents.size(); i++) {
            row52.createCell(documents.size() + 4 + i).setCellStyle(BottomNormal);
        }

        rowNumber++;

        Row row51 = reportSheet.createRow(rowNumber);
        Cell temp136 = row51.createCell(0);
        temp136.setCellValue("BEDRIJFSWINST(EBIT)");
        temp136.setCellStyle(BoldYellow);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp137 = row51.createCell(i + 1, CellType.NUMERIC);
            temp137.setCellValue(Math.round(getEBIT(i)));
            temp137.setCellStyle(BoldYellowNumber);
        }

        rowNumber += 2;

        Row row53 = reportSheet.createRow(rowNumber);
        Cell temp140 = row53.createCell(0);
        temp140.setCellValue("FINANCIELE RESULTATEN");
        temp140.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp141 = row53.createCell(i + 1, CellType.NUMERIC);
            temp141.setCellValue(Math.round(getFinancieleResultaten(i)));
            temp141.setCellStyle(BoldNumber);
        }

        Cell temp149 = row53.createCell(2 + documents.size());
        temp149.setCellValue("FINANCIELE LASTEN:");
        temp149.setCellStyle(BoldBottomTop);
        row53.createCell(3 + documents.size()).

                setCellStyle(BoldBottomTop);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp150 = row53.createCell(documents.size() + 4 + i);
            temp150.setCellValue(Math.round(getFinancieleKosten(i)));
            temp150.setCellStyle(BoldLightGreyNumber);
        }

        rowNumber++;

        Row row54 = reportSheet.createRow(rowNumber);
        Cell temp145 = row54.createCell(0);
        temp145.setCellValue("UITZONDERLIJKE RESULTATEN");
        temp145.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp146 = row54.createCell(i + 1, CellType.NUMERIC);
            temp146.setCellValue(Math.round(getUitzonderlijkeResultaten(i)));
            temp146.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row57 = reportSheet.createRow(rowNumber);
        Cell temp153 = row57.createCell(2 + documents.size());
        temp153.setCellValue("FINANCIERINGSLAST");
        temp153.setCellStyle(BoldTop);
        Cell temp154 = row57.createCell(3 + documents.size());
        temp154.setCellValue("SCHULDEN");
        temp154.setCellStyle(BoldBottomTop);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp155 = row57.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp155.setCellValue(getFinancieringsLast(i));
            temp155.setCellStyle(BoldTopDouble);
        }

        rowNumber++;

        Row row56 = reportSheet.createRow(rowNumber);
        Cell temp147 = row56.createCell(0);
        temp147.setCellValue("RESULTAAT VOOR BELASTINGEN");
        temp147.setCellStyle(BoldYellow);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp148 = row56.createCell(i + 1, CellType.NUMERIC);
            temp148.setCellValue(Math.round(getResultaatVoorBelastingen(i)));
            temp148.setCellStyle(BoldYellowNumber);
        }

        Cell temp158 = row56.createCell(2 + documents.size());
        temp158.setCellValue("<4");
        temp158.setCellStyle(Bold);

        Cell temp159 = row56.createCell(3 + documents.size());
        temp159.setCellValue("EBITDA");
        temp159.setCellStyle(Bold);

        rowNumber += 2;

        Row row58 = reportSheet.createRow(rowNumber);
        Cell temp151 = row58.createCell(0);
        temp151.setCellValue("BELASTINGEN");
        temp151.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp152 = row58.createCell(i + 1, CellType.NUMERIC);
            temp152.setCellValue(-Math.round(getBelastingen(i)));
            temp152.setCellStyle(BoldNumber);
        }

        Cell temp160 = row58.createCell(2 + documents.size());
        temp160.setCellValue("INTRESTDEKKING");
        temp160.setCellStyle(Bold);
        Cell temp161 = row58.createCell(3 + documents.size());
        temp161.setCellValue("EBITDA");
        temp161.setCellStyle(BoldBottom);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp162 = row58.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp162.setCellValue(Math.round(getEBITDA(i) / getFinancieleKosten(i)));
            temp162.setCellStyle(BoldNumber);
        }

        rowNumber++;

        Row row61 = reportSheet.createRow(rowNumber);
        Cell temp164 = row61.createCell(2 + documents.size());
        temp164.setCellValue(">1");
        temp164.setCellStyle(BoldBottom);
        Cell temp165 = row61.createCell(3 + documents.size());
        temp165.setCellValue("FINANCIELE LASTEN");
        temp165.setCellStyle(BoldBottom);
        for (
                int i = 0; i < documents.size(); i++) {
            row61.createCell(documents.size() + 4 + i).setCellStyle(BottomNormal);
        }

        rowNumber++;

        Row row60 = reportSheet.createRow(rowNumber);
        Cell temp156 = row60.createCell(0);
        temp156.setCellValue("NETTO WINST");
        temp156.setCellStyle(BoldYellow);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp157 = row60.createCell(i + 1, CellType.NUMERIC);
            temp157.setCellValue(Math.round(getWinstVerliesBoekjaar(i)));
            temp157.setCellStyle(BoldYellowNumber);
        }

        rowNumber += 2;

        Row row64 = reportSheet.createRow(rowNumber);
        Cell temp169 = row64.createCell(2 + documents.size());
        temp169.setCellValue("Kostenstructuur");
        temp169.setCellStyle(BoldTop);
        row64.createCell(3 + documents.size()).

                setCellStyle(BoldTop);
        for (
                int i = 0; i < documents.size(); i++) {
            row64.createCell(documents.size() + 4 + i).setCellStyle(BoldTop);
        }

        rowNumber++;

        Row row63 = reportSheet.createRow(rowNumber);
        Cell temp163 = row63.createCell(0);
        temp163.setCellValue("EXTRA INFO");
        temp163.setCellStyle(Bold);

        rowNumber++;

        Row rowAankopenOmzet = reportSheet.createRow(rowNumber);
        Cell temp170 = rowAankopenOmzet.createCell(2 + documents.size());
        temp170.setCellValue("Aankopen/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp170.setCellStyle(Bold);
        } else {
            temp170.setCellStyle(Grey);
        }

        Cell temp171 = rowAankopenOmzet.createCell(3 + documents.size());
        temp171.setCellValue("AK/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp171.setCellStyle(Bold);
        } else {
            temp171.setCellStyle(Grey);
        }
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp172 = rowAankopenOmzet.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp172.setCellValue(getAankopen(i) / getBedrijfsOpbrengsten(i));
                temp172.setCellStyle(PercentStyleBold);
            } else {
                temp172.setCellValue("/");
                temp172.setCellStyle(Grey);
            }
        }

        rowNumber++;

        Row row65 = reportSheet.createRow(rowNumber);
        Cell temp166 = row65.createCell(0);
        temp166.setCellValue("Investeringen");
        temp166.setCellStyle(BoldBottomTop);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp167 = row65.createCell(i + 1, CellType.NUMERIC);
            temp167.setCellValue(Math.round(getInvesteringen(i)));
            temp167.setCellStyle(NumberBottomTop);
        }

        rowNumber++;

        Row row66 = reportSheet.createRow(rowNumber);
        Cell temp1660 = row66.createCell(0);
        temp1660.setCellValue("Investeringen/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp1660.setCellStyle(BoldBottomTop);
        } else {
            temp1660.setCellStyle(Grey);
        }
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp168 = row66.createCell(i + 1, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp168.setCellValue(getInvesteringen(i) / getBedrijfsOpbrengsten(i));
                temp168.setCellStyle(PercentStyleBoldBottomTop);
            } else {
                temp168.setCellValue("/");
                temp168.setCellStyle(Grey);
            }
        }

        Cell temp173 = row66.createCell(2 + documents.size());
        temp173.setCellValue("Personeelskosten/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp173.setCellStyle(Bold);
        } else {
            temp173.setCellStyle(Grey);
        }

        Cell temp174 = row66.createCell(3 + documents.size());
        temp174.setCellValue("PK/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp174.setCellStyle(Bold);
        } else {
            temp174.setCellStyle(Grey);
        }

        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp175 = row66.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp175.setCellValue(getPersoneelskosten(i) / getBedrijfsOpbrengsten(i));
                temp175.setCellStyle(PercentStyleBold);
            } else {
                temp175.setCellValue("/");
                temp175.setCellStyle(Grey);
            }
        }

        rowNumber++;

        Row row67 = reportSheet.createRow(rowNumber);
        Cell temp1661 = row67.createCell(0);
        temp1661.setCellValue("Investeringen/brutomarge");
        temp1661.setCellStyle(BoldBottomTop);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp168 = row67.createCell(i + 1, CellType.NUMERIC);
            temp168.setCellValue(getInvesteringen(i) / getBrutoMarge(i));
            temp168.setCellStyle(PercentStyleBoldBottomTop);
        }

        rowNumber++;

        Row row70 = reportSheet.createRow(rowNumber);
        Cell temp176 = row70.createCell(2 + documents.size());
        temp176.setCellValue("Andere kosten/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp176.setCellStyle(Bold);
        } else {
            temp176.setCellStyle(Grey);
        }

        Cell temp177 = row70.createCell(3 + documents.size());
        temp177.setCellValue("andere/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp177.setCellStyle(Bold);
        } else {
            temp177.setCellStyle(Grey);
        }

        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp178 = row70.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp178.setCellValue(getAndereKosten(i) / getBedrijfsOpbrengsten(i));
                temp178.setCellStyle(PercentStyleBold);
            } else {
                temp178.setCellValue("/");
                temp178.setCellStyle(Grey);
            }
        }

        rowNumber += 2;

        Row row72 = reportSheet.createRow(rowNumber);
        Cell temp178 = row72.createCell(2 + documents.size());
        temp178.setCellValue("Personeelskosten/brutomarge");
        temp178.setCellStyle(Bold);
        Cell temp179 = row72.createCell(3 + documents.size());
        temp179.setCellValue("PK/brutomarge");
        temp179.setCellStyle(Bold);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp180 = row72.createCell(documents.size() + 4 + i);
            temp180.setCellValue(getPersoneelskosten(i) / getBrutoMarge(i));
            temp180.setCellStyle(PercentStyleBold);
        }

        rowNumber += 2;

        Row row74 = reportSheet.createRow(rowNumber);
        Cell temp181 = row74.createCell(2 + documents.size());
        temp181.setCellValue("Andere kosten/brutomarge");
        temp181.setCellStyle(BoldBottom);
        Cell temp182 = row74.createCell(3 + documents.size());
        temp182.setCellValue("AK/brutomarge");
        temp182.setCellStyle(BoldBottom);
        for (
                int i = 0; i < documents.size(); i++) {
            Cell temp183 = row74.createCell(documents.size() + 4 + i);
            temp183.setCellValue(getAndereKosten(i) / getBrutoMarge(i));
            temp183.setCellStyle(PercentStyleBoldBottom);
        }

    }

    @Override
    void initializeStyles() {
        HSSFFont fontBold = workbook.createFont();
        fontBold.setBold(true);

        Bold = workbook.createCellStyle();
        Bold.setFont(fontBold);

        BoldNumber = workbook.createCellStyle();
        BoldNumber.setFont(fontBold);
        BoldNumber.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        BoldGreenCenter = workbook.createCellStyle();
        BoldGreenCenter.setFont(fontBold);
        BoldGreenCenter.setAlignment(HorizontalAlignment.CENTER);
        BoldGreenCenter.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        BoldGreenCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldGreenCenter.setBorderBottom(BorderStyle.THIN);
        BoldGreenCenter.setBorderTop(BorderStyle.THIN);

        BoldGreen = workbook.createCellStyle();
        BoldGreen.setFont(fontBold);
        BoldGreen.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        BoldGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldGreen.setBorderBottom(BorderStyle.THIN);
        BoldGreen.setBorderTop(BorderStyle.THIN);

        BoldGreenNumber = workbook.createCellStyle();
        BoldGreenNumber.setFont(fontBold);
        BoldGreenNumber.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        BoldGreenNumber.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldGreenNumber.setBorderBottom(BorderStyle.THIN);
        BoldGreenNumber.setBorderTop(BorderStyle.THIN);
        BoldGreenNumber.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        BoldGrey = workbook.createCellStyle();
        BoldGrey.setFont(fontBold);
        BoldGrey.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        BoldGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldGrey.setBorderBottom(BorderStyle.THIN);
        BoldGrey.setBorderTop(BorderStyle.THIN);

        BoldYellow = workbook.createCellStyle();
        BoldYellow.setFont(fontBold);
        BoldYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        BoldYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldYellow.setBorderBottom(BorderStyle.THIN);
        BoldYellow.setBorderTop(BorderStyle.THIN);

        BoldYellowNumber = workbook.createCellStyle();
        BoldYellowNumber.setFont(fontBold);
        BoldYellowNumber.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        BoldYellowNumber.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldYellowNumber.setBorderBottom(BorderStyle.THIN);
        BoldYellowNumber.setBorderTop(BorderStyle.THIN);
        BoldYellowNumber.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        BoldLightGrey = workbook.createCellStyle();
        BoldLightGrey.setFont(fontBold);
        BoldLightGrey.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        BoldLightGrey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldLightGrey.setBorderBottom(BorderStyle.THIN);
        BoldLightGrey.setBorderTop(BorderStyle.THIN);

        BoldLightGreyNumber = workbook.createCellStyle();
        BoldLightGreyNumber.setFont(fontBold);
        BoldLightGreyNumber.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        BoldLightGreyNumber.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldLightGreyNumber.setBorderBottom(BorderStyle.THIN);
        BoldLightGreyNumber.setBorderTop(BorderStyle.THIN);
        BoldLightGreyNumber.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        BoldBlue = workbook.createCellStyle();
        BoldBlue.setFont(fontBold);
        BoldBlue.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        BoldBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        BoldBlue.setBorderBottom(BorderStyle.THIN);
        BoldBlue.setBorderTop(BorderStyle.THIN);

        BoldBottom = workbook.createCellStyle();
        BoldBottom.setFont(fontBold);
        BoldBottom.setBorderBottom(BorderStyle.THIN);

        BoldBottomNumber = workbook.createCellStyle();
        BoldBottomNumber.setFont(fontBold);
        BoldBottomNumber.setBorderBottom(BorderStyle.THIN);
        BoldBottomNumber.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        BoldTop = workbook.createCellStyle();
        BoldTop.setFont(fontBold);
        BoldTop.setBorderTop(BorderStyle.THIN);

        BoldTopNumber = workbook.createCellStyle();
        BoldTopNumber.setFont(fontBold);
        BoldTopNumber.setBorderTop(BorderStyle.THIN);
        BoldTopNumber.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        BoldBottomTop = workbook.createCellStyle();
        BoldBottomTop.setFont(fontBold);
        BoldBottomTop.setBorderBottom(BorderStyle.THIN);
        BoldBottomTop.setBorderTop(BorderStyle.THIN);

        BottomNormal = workbook.createCellStyle();
        BottomNormal.setBorderBottom(BorderStyle.THIN);

        BoldTopDouble = workbook.createCellStyle();
        BoldTopDouble.setFont(fontBold);
        BoldTopDouble.setBorderTop(BorderStyle.THIN);
        BoldTopDouble.setDataFormat(numberFormatter.getFormat("### ### ##0.00"));

        Grey = workbook.createCellStyle();
        Grey.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        Grey.setFont(fontBold);
        Grey.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        PercentStyleBold = workbook.createCellStyle();
        PercentStyleBold.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));
        PercentStyleBold.setFont(fontBold);

        PercentStyleBoldBottom = workbook.createCellStyle();
        PercentStyleBoldBottom.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));
        PercentStyleBoldBottom.setFont(fontBold);
        PercentStyleBoldBottom.setBorderBottom(BorderStyle.THIN);

        DoubleStyleBold = workbook.createCellStyle();
        DoubleStyleBold.setDataFormat(numberFormatter.getFormat("### ### ##0.00"));
        DoubleStyleBold.setFont(fontBold);

        DoubleStyleBoldTop = workbook.createCellStyle();
        DoubleStyleBoldTop.setDataFormat(numberFormatter.getFormat("### ### ##0.00"));
        DoubleStyleBoldTop.setFont(fontBold);
        DoubleStyleBoldTop.setBorderTop(BorderStyle.THIN);

        NumberBottomTop = workbook.createCellStyle();
        NumberBottomTop.setFont(fontBold);
        NumberBottomTop.setBorderTop(BorderStyle.THIN);
        NumberBottomTop.setBorderBottom(BorderStyle.THIN);
        NumberBottomTop.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        PercentStyleBoldBottomTop = workbook.createCellStyle();
        PercentStyleBoldBottomTop.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));
        PercentStyleBoldBottomTop.setFont(fontBold);
        PercentStyleBoldBottomTop.setBorderBottom(BorderStyle.THIN);
        PercentStyleBoldBottomTop.setBorderTop(BorderStyle.THIN);
    }
}
