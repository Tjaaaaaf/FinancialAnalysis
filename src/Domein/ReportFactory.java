package Domein;

import Enums.PropertyName;
import Enums.ReportStyle;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

public class ReportFactory {

    private HSSFCellStyle BoldGreenCenter;
    private HSSFCellStyle Bold;
    private HSSFCellStyle BoldGrey;
    private HSSFCellStyle BoldYellow;
    private HSSFCellStyle BoldLightGrey;
    private HSSFCellStyle BoldBottom;
    private HSSFCellStyle BoldBottomTop;
    private HSSFCellStyle BoldBlue;
    private HSSFCellStyle BottomNormal;
    private HSSFCellStyle BoldTop;
    private HSSFFont fontBold;
    private HSSFCellStyle BoldNumber;
    private HSSFCellStyle BoldYellowNumber;
    private HSSFCellStyle BoldLightGreyNumber;
    private HSSFCellStyle BoldBottomNumber;
    private HSSFCellStyle BoldTopNumber;
    private HSSFCellStyle Number;
    private HSSFCellStyle BoldTopDouble;
    private HSSFCellStyle Grey;
    private HSSFCellStyle ProcentStyleBold;
    private HSSFCellStyle DoubleStyle;
    private HSSFCellStyle DoubleStyleBold;
    private HSSFCellStyle DoubleStyleBoldTop;
    private HSSFCellStyle ProcentStyle;
    private HSSFCellStyle ProcentStyleBoldBottom;

    private final List<DocumentWrapper> documents;

    private final HSSFSheet report;
    private final ReportStyle style;
    private final HSSFWorkbook workbook;

    private final HSSFDataFormat numberFormatter;
    private CellStyle NumberBottomTop;
    private CellStyle ProcentStyleBoldBottomTop;

    public ReportFactory(HSSFWorkbook workbook, HSSFSheet report, List<DocumentWrapper> documents, ReportStyle style, HSSFDataFormat numberFormatter) {
        this.workbook = workbook;
        this.report = report;
        this.documents = documents;
        this.style = style;
        this.numberFormatter = numberFormatter;

        makeStyles();
    }

    public void createHistoriekReport() {

        //ROW2
        Row row2 = report.createRow(1);
        Cell cellName = row2.createCell(0);
        cellName.setCellValue("NAAM");
        cellName.setCellStyle(BoldBottomTop);

        Cell cellPer = row2.createCell(1);
        cellPer.setCellValue("PER");
        cellPer.setCellStyle(BoldBottomTop);

        Cell cellDate = row2.createCell(2);
        cellDate.setCellValue("31/12");
        cellDate.setCellStyle(BoldBottomTop);

        row2.createCell(documents.size() + 2).setCellStyle(BoldTop);
        row2.createCell(documents.size() + 3).setCellStyle(BoldTop);

        for (int i = 0; i < documents.size(); i++) {
            Cell temp = row2.createCell(documents.size() + 4 + i);
            temp.setCellValue(documents.get(i).getYear());
            temp.setCellStyle(BoldGrey);
        }

        //ROW3
        Row row3 = report.createRow(2);
        Cell cellCompany = row3.createCell(0);
        cellCompany.setCellValue(documents.get(0).getBusiness().getName() + (style.equals(ReportStyle.HISTORIEKNV) ? " NV" : " BVBA"));
        cellCompany.setCellStyle(BoldGreenCenter);
        Cell temp = row3.createCell(2 + documents.size());
        temp.setCellValue("CORE NETTO WERK KAPITAAL");
        temp.setCellStyle(Bold);
        Cell temp2 = row3.createCell(3 + documents.size());
        temp2.setCellValue("voorraden+vorderingen-leveranciers");
        for (int i = 0; i < documents.size(); i++) {
            Cell temp3 = row3.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp3.setCellValue(Math.round(getCoreNettoWerkKapitaal(i)));
            temp3.setCellStyle(BoldNumber);
        }

        //ROW5
        Row row5 = report.createRow(4);
        Cell temp4 = row5.createCell(2 + documents.size());
        temp4.setCellValue("CAPITAL EMPLOYED");
        temp4.setCellStyle(BoldBottom);
        Cell temp5 = row5.createCell(3 + documents.size());
        temp5.setCellValue("netto werkkapitaal+vaste activa");
        temp5.setCellStyle(BottomNormal);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp6 = row5.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp6.setCellValue(Math.round(getCapitalEmployed(i)));
            temp6.setCellStyle(BoldBottomNumber);
        }

        //ROW6
        Row row6 = report.createRow(5);
        Cell temp7 = row6.createCell(0);
        temp7.setCellValue("BALANS ACTIVA");
        temp7.setCellStyle(BoldGrey);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp9 = row6.createCell(i + 1);
            temp9.setCellValue(documents.get(i).getYear());
            temp9.setCellStyle(BoldGrey);
        }

        //ROW7
        Row row7 = report.createRow(6);
        Cell temp10 = row7.createCell(2 + documents.size());
        temp10.setCellValue("EBIT MARGE");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp10.setCellStyle(BoldTop);
        } else {
            temp10.setCellStyle(Grey);
        }
        Cell temp11 = row7.createCell(3 + documents.size());
        temp11.setCellValue("EBIT");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp11.setCellStyle(BoldBottomTop);
        } else {
            temp11.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell temp12 = row7.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp12.setCellValue(getEBIT(i) / getOmzet(i));
                temp12.setCellStyle(ProcentStyleBold);
            } else {
                temp12.setCellValue("/");
                temp12.setCellStyle(Grey);
            }
        }

        //ROW8
        Row row8 = report.createRow(7);
        Cell temp13 = row8.createCell(0);
        temp13.setCellValue("VASTE ACTIVA");
        temp13.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp15 = row8.createCell(i + 1, CellType.NUMERIC);
            temp15.setCellValue(Math.round(getVasteActiva(i)));
            temp15.setCellStyle(BoldYellowNumber);
        }
        Cell temp16 = row8.createCell(3 + documents.size());
        temp16.setCellValue("OMZET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp16.setCellStyle(Bold);
        } else {
            temp16.setCellStyle(Grey);
        }

        //ROW9
        Row row9 = report.createRow(8);
        Cell temp17 = row9.createCell(0);
        temp17.setCellValue("IMMATERIELE (evt. Goodwill)");
        temp17.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp18 = row9.createCell(i + 1, CellType.NUMERIC);
            temp18.setCellValue(Math.round(Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BAImmaterieleVasteActiva))));
            temp18.setCellStyle(BoldNumber);
        }

        //ROW10
        Row row10 = report.createRow(9);
        Cell temp19 = row10.createCell(0);
        temp19.setCellValue("MATERIELE");
        temp19.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp20 = row10.createCell(i + 1, CellType.NUMERIC);
            temp20.setCellValue(Math.round(Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BAMaterieleVasteActiva))));
            temp20.setCellStyle(BoldNumber);
        }
        Cell temp21 = row10.createCell(2 + documents.size());
        temp21.setCellValue("EBITDA MARGE");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp21.setCellStyle(Bold);
        } else {
            temp21.setCellStyle(Grey);
        }
        Cell temp22 = row10.createCell(3 + documents.size());
        temp22.setCellValue("EBITDA");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp22.setCellStyle(BoldBottom);
        } else {
            temp22.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell temp23 = row10.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp23.setCellValue(getEBITDA(i) / getOmzet(i));
                temp23.setCellStyle(ProcentStyleBold);
            } else {
                temp23.setCellValue("/");
                temp23.setCellStyle(Grey);
            }
        }

        //ROW11
        Row row11 = report.createRow(10);
        Cell temp24 = row11.createCell(0);
        temp24.setCellValue("FINANCIELE");
        temp24.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp25 = row11.createCell(i + 1, CellType.NUMERIC);
            temp25.setCellValue(Math.round(Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BAFinancieleVasteActiva))));
            temp25.setCellStyle(BoldNumber);
        }
        Cell temp26 = row11.createCell(2 + documents.size());
        temp26.setCellValue("> 12-15%");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp26.setCellStyle(Bold);
        } else {
            temp26.setCellStyle(Grey);
        }
        Cell temp27 = row11.createCell(3 + documents.size());
        temp27.setCellValue("OMZET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp27.setCellStyle(Bold);
        } else {
            temp27.setCellStyle(Grey);
        }

        //ROW12
        Row row12 = report.createRow(11);
        Cell temp28 = row12.createCell(0);
        temp28.setCellValue("VLOTTENDE ACTIVA");
        temp28.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp29 = row12.createCell(i + 1, CellType.NUMERIC);
            temp29.setCellValue(Math.round(getVlottendeActiva(i)));
            temp29.setCellStyle(BoldYellowNumber);
        }

        //ROW13
        Row row13 = report.createRow(12);
        Cell temp30 = row13.createCell(0);
        temp30.setCellValue("VOORRADEN");
        temp30.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp31 = row13.createCell(i + 1, CellType.NUMERIC);
            temp31.setCellValue(Math.round(getVoorraden(i)));
            temp31.setCellStyle(BoldNumber);
        }
        Cell temp32 = row13.createCell(2 + documents.size());
        temp32.setCellValue("RENDEMENT EIGEN");
        temp32.setCellStyle(Bold);
        Cell temp33 = row13.createCell(3 + documents.size());
        temp33.setCellValue("NETTO WINST");
        temp33.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp34 = row13.createCell(documents.size() + 4 + i);
            temp34.setCellValue(getWinstVerliesBoekjaar(i) / getEigenVermogen(i));
            temp34.setCellStyle(ProcentStyleBold);
        }

        //ROW14
        Row row14 = report.createRow(13);
        Cell temp35 = row14.createCell(0);
        temp35.setCellValue("HANDELSVORDERINGEN");
        temp35.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp36 = row14.createCell(i + 1, CellType.NUMERIC);
            temp36.setCellValue(Math.round(getHandelsvorderingen(i)));
            temp36.setCellStyle(BoldNumber);
        }
        Cell temp37 = row14.createCell(2 + documents.size());
        temp37.setCellValue("VERMOGEN >?");
        temp37.setCellStyle(Bold);
        Cell temp38 = row14.createCell(3 + documents.size());
        temp38.setCellValue("EIGEN VERMOGEN");
        temp38.setCellStyle(Bold);

        //ROW15
        Row row15 = report.createRow(14);
        Cell temp39 = row15.createCell(0);
        temp39.setCellValue("ANDERE");
        temp39.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp40 = row15.createCell(i + 1, CellType.NUMERIC);
            temp40.setCellValue(Math.round(getAndereVorderingen(i)));
            temp40.setCellStyle(BoldNumber);
        }

        //ROW16
        Row row16 = report.createRow(15);
        Cell temp41 = row16.createCell(0);
        temp41.setCellValue("CASH");
        temp41.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp42 = row16.createCell(i + 1, CellType.NUMERIC);
            temp42.setCellValue(Math.round(getCash(i)));
            temp42.setCellStyle(BoldNumber);
        }
        Cell temp43 = row16.createCell(2 + documents.size());
        temp43.setCellValue("ROTATIE");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp43.setCellStyle(Bold);
        } else {
            temp43.setCellStyle(Grey);
        }

        Cell temp44 = row16.createCell(3 + documents.size());
        temp44.setCellValue("OMZET");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp44.setCellStyle(BoldBottom);
        } else {
            temp44.setCellStyle(Grey);
        }

        for (int i = 0; i < documents.size(); i++) {
            Cell temp45 = row16.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp45.setCellValue(getOmzet(i) / getCapitalEmployed(i));
                temp45.setCellStyle(DoubleStyleBold);
            } else {
                temp45.setCellValue("/");
                temp45.setCellStyle(Grey);
            }
        }

        //ROW17
        Row row17 = report.createRow(16);
        Cell temp46 = row17.createCell(0);
        temp46.setCellValue("TOTALE ACTIVA");
        temp46.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp47 = row17.createCell(i + 1, CellType.NUMERIC);
            temp47.setCellValue(Math.round(getTotaleActiva(i)));
            temp47.setCellStyle(BoldYellowNumber);
        }
        Cell temp48 = row17.createCell(3 + documents.size());
        temp48.setCellValue("CAPITAL EMPLOTYED");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp48.setCellStyle(Bold);
        } else {
            temp48.setCellStyle(Grey);
        }

        //ROW19
        Row row19 = report.createRow(18);
        Cell temp49 = row19.createCell(0);
        temp49.setCellValue("BALANS PASSIVA");
        temp49.setCellStyle(BoldGrey);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp50 = row19.createCell(i + 1);
            temp50.setCellValue(documents.get(i).getYear());
            temp50.setCellStyle(BoldGrey);
        }
        Cell temp51 = row19.createCell(2 + documents.size());
        temp51.setCellValue("RENDEMENT OP DE");
        temp51.setCellStyle(Bold);
        Cell temp52 = row19.createCell(3 + documents.size());
        temp52.setCellValue("EBIT");
        temp52.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp53 = row19.createCell(documents.size() + 4 + i);
            temp53.setCellValue(getEBIT(i) / getCapitalEmployed(i));
            temp53.setCellStyle(ProcentStyleBold);
        }

        //ROW20
        Row row20 = report.createRow(19);
        Cell temp54 = row20.createCell(2 + documents.size());
        temp54.setCellValue("INGEZETTE MIDDELEN (ROCE)");
        temp54.setCellStyle(Bold);
        Cell temp55 = row20.createCell(3 + documents.size());
        temp55.setCellValue("CAPITAL EMPLOYED");
        temp55.setCellStyle(Bold);

        //ROW21
        Row row21 = report.createRow(20);
        Cell temp56 = row21.createCell(0);
        temp56.setCellValue("EIGEN VERMOGEN");
        temp56.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp57 = row21.createCell(i + 1, CellType.NUMERIC);
            temp57.setCellValue(Math.round(getEigenVermogen(i)));
            temp57.setCellStyle(BoldYellowNumber);
        }
        Cell temp58 = row21.createCell(2 + documents.size());
        temp58.setCellValue(">WACC(10%?)");
        temp58.setCellStyle(BoldBottom);
        row21.createCell(3 + documents.size()).setCellStyle(BottomNormal);
        for (int i = 0; i < documents.size(); i++) {
            row21.createCell(documents.size() + 4 + i).setCellStyle(BottomNormal);
        }

        //ROW23
        Row row23 = report.createRow(22);
        Cell temp59 = row23.createCell(0);
        temp59.setCellValue("SCHULDEN");
        temp59.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp60 = row23.createCell(i + 1, CellType.NUMERIC);
            temp60.setCellValue(Math.round(getLangeTermijnFinancieleSchulden(i)
                    + getKorteTermijnSchulden(i)
                    + getProvisies(i)));
            temp60.setCellStyle(BoldYellowNumber);
        }
        Cell temp61 = row23.createCell(2 + documents.size());
        temp61.setCellValue("VOORRAADROTATIE");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp61.setCellStyle(BoldTop);
        } else {
            temp61.setCellStyle(Grey);
        }

        Cell temp62 = row23.createCell(3 + documents.size());
        temp62.setCellValue("VOORRADEN X 365");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp62.setCellStyle(BoldBottomTop);
        } else {
            temp62.setCellStyle(Grey);
        }

        for (int i = 0; i < documents.size(); i++) {
            Cell temp63 = row23.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp63.setCellValue(Math.round(getVoorraadrotatie(i)));
                temp63.setCellStyle(BoldTopNumber);
            } else {
                temp63.setCellValue("/");
                temp63.setCellStyle(Grey);
            }
        }

        //ROW24
        Row row24 = report.createRow(23);
        Cell temp64 = row24.createCell(0);
        temp64.setCellValue("PROVISIES");
        temp64.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
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

        //ROW25
        Row row25 = report.createRow(24);
        Cell temp67 = row25.createCell(0);
        temp67.setCellValue("LANGE TERMIJN SCHULDEN");
        temp67.setCellStyle(BoldLightGrey);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp68 = row25.createCell(i + 1, CellType.NUMERIC);
            temp68.setCellValue(Math.round(Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPSchuldenMeer1Jaar))));
            temp68.setCellStyle(BoldLightGreyNumber);
        }

        //ROW26
        Row row26 = report.createRow(25);
        Cell temp69 = row26.createCell(0);
        temp69.setCellValue("FINANCIELE");
        temp69.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
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

        for (int i = 0; i < documents.size(); i++) {
            Cell temp73 = row26.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp73.setCellValue(Math.round(getKlantenKrediet(i)));
                temp73.setCellStyle(BoldNumber);
            } else {
                temp73.setCellValue("/");
                temp73.setCellStyle(Grey);
            }
        }

        //ROW27
        Row row27 = report.createRow(26);
        Cell temp74 = row27.createCell(0);
        temp74.setCellValue("ANDERE");
        temp74.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp75 = row27.createCell(i + 1, CellType.NUMERIC);
            temp75.setCellValue(Math.round(Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPSchuldenMeer1JaarOverigeSchulden))));
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

        //ROW28
        Row row28 = report.createRow(27);
        Cell temp78 = row28.createCell(0);
        temp78.setCellValue("KORTE TERMIJN SCHULDEN");
        temp78.setCellStyle(BoldLightGrey);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp79 = row28.createCell(i + 1, CellType.NUMERIC);
            temp79.setCellValue(Math.round(getKorteTermijnSchulden(i)));
            temp79.setCellStyle(BoldLightGreyNumber);
        }

        //ROW29
        Row row29 = report.createRow(28);
        Cell temp80 = row29.createCell(0);
        temp80.setCellValue("FINANCIELE");
        temp80.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
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
        for (int i = 0; i < documents.size(); i++) {
            Cell temp84 = row29.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp84.setCellValue(Math.round(getLeveranciersKrediet(i)));
                temp84.setCellStyle(BoldNumber);
            } else {
                temp84.setCellValue("/");
                temp84.setCellStyle(Grey);
            }
        }

        //ROW30
        Row row30 = report.createRow(29);
        Cell temp85 = row30.createCell(0);
        temp85.setCellValue("LEVERANCIERS");
        temp85.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
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

        //ROW31
        Row row31 = report.createRow(30);
        Cell temp89 = row31.createCell(0);
        temp89.setCellValue("ANDERE");
        temp89.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp90 = row31.createCell(i + 1, CellType.NUMERIC);
            temp90.setCellValue(Math.round(getAndereSchuldenKorteTermijn(i)));
            temp90.setCellStyle(BoldNumber);
        }

        //ROW32
        Row row32 = report.createRow(31);
        Cell temp91 = row32.createCell(0);
        temp91.setCellValue("TOTALE PASSIVA");
        temp91.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
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
        for (int i = 0; i < documents.size(); i++) {
            Cell temp95 = row32.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp95.setCellValue(Math.round(getCashFlow(i)));
            temp95.setCellStyle(BoldNumber);
        }

        //ROW33
        Row row33 = report.createRow(32);
        Cell temp96 = row33.createCell(2 + documents.size());
        temp96.setCellValue("marge / omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp96.setCellStyle(Bold);
        } else {
            temp96.setCellStyle(Grey);
            row33.createCell(3 + documents.size()).setCellStyle(Grey);
        }

        for (int i = 0; i < documents.size(); i++) {
            Cell temp97 = row33.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp97.setCellValue(getCashFlow(i) / getOmzet(i));
                temp97.setCellStyle(ProcentStyleBold);
            } else {
                temp97.setCellValue("/");
                temp97.setCellStyle(Grey);
            }

        }

        //ROW35
        Row row35 = report.createRow(34);
        Cell temp98 = row35.createCell(0);
        temp98.setCellValue("RESULTATENREKENING");
        temp98.setCellStyle(BoldGrey);
        for (int i = 0; i < documents.size(); i++) {
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
        for (int i = 0; i < documents.size(); i++) {
            Cell temp102 = row35.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp102.setCellValue(Math.round(getCashFlow(i) - getInvesteringen(i)));
            temp102.setCellStyle(BoldNumber);
        }

        //ROW36
        Row row36 = report.createRow(35);
        Cell temp103 = row36.createCell(2 + documents.size());
        temp103.setCellValue("marge / omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp103.setCellStyle(Bold);
        } else {
            temp103.setCellStyle(Grey);
            row36.createCell(3 + documents.size()).setCellStyle(Grey);
        }

        for (int i = 0; i < documents.size(); i++) {
            Cell temp104 = row36.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp104.setCellValue((getCashFlow(i) - getInvesteringen(i)) / getOmzet(i));
                temp104.setCellStyle(ProcentStyleBold);
            } else {
                temp104.setCellValue("/");
                temp104.setCellStyle(Grey);
            }
        }

        //ROW37
        Row row37 = report.createRow(36);
        Cell temp105 = row37.createCell(0);
        temp105.setCellValue("BEDRIJFSOPBRENGSTEN");
        temp105.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp106 = row37.createCell(i + 1, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKBVBA)) {
                temp106.setCellValue("/");
            } else {
                temp106.setCellValue(Math.round(getOmzet(i)));
            }
            temp106.setCellStyle(BoldYellowNumber);
        }

        //ROW39
        Row row39 = report.createRow(38);
        Cell temp107 = row39.createCell(0);
        temp107.setCellValue("BEDRIJFSKOSTEN");
        temp107.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp108 = row39.createCell(i + 1, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKBVBA)) {
                temp108.setCellValue("/");
            } else {
                temp108.setCellValue(-Math.round(getBedrijfskosten(i)));
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

        for (int i = 0; i < documents.size(); i++) {
            Cell temp110 = row39.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp110.setCellValue(Math.round(getVoorraadrotatie(i) + getKlantenKrediet(i) - getLeveranciersKrediet(i)));
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

        //ROW41
        Row row41 = report.createRow(40);
        Cell temp111 = row41.createCell(0);
        temp111.setCellValue("AANKOPEN");
        if (style.equals(ReportStyle.HISTORIEKBVBA)) {
            temp111.setCellStyle(Grey);
        } else {
            temp111.setCellStyle(Bold);
        }
        for (int i = 0; i < documents.size(); i++) {
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
        for (int i = 0; i < documents.size(); i++) {
            Cell temp117 = row41.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp117.setCellValue(getVlottendeActiva(i) / getKorteTermijnSchulden(i));
            temp117.setCellStyle(DoubleStyleBoldTop);
        }

        //ROW42
        Row row42 = report.createRow(41);
        Cell temp1110 = row42.createCell(0);
        temp1110.setCellValue("BRUTOMARGE (toegevoegde waarde)");
        temp1110.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp112 = row42.createCell(i + 1, CellType.NUMERIC);
            temp112.setCellValue(Math.round(getBrutoMarge(i)));
            temp112.setCellStyle(BoldLightGreyNumber);
        }
        Cell temp120 = row42.createCell(2 + documents.size());
        temp120.setCellValue(">1");
        temp120.setCellStyle(Bold);

        Cell temp121 = row42.createCell(3 + documents.size());
        temp121.setCellValue("COURANTE PASSIVA");
        temp121.setCellStyle(Bold);

        //ROW43
        Row row43 = report.createRow(42);
        Cell temp113 = row43.createCell(0);
        temp113.setCellValue("PERSONEELSKOSTEN");
        temp113.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp114 = row43.createCell(i + 1, CellType.NUMERIC);
            temp114.setCellValue(-Math.round(getPersoneelskosten(i)));
            temp114.setCellStyle(BoldNumber);
        }

        //ROW44
        Row row44 = report.createRow(43);
        Cell temp118 = row44.createCell(0);
        temp118.setCellValue("ANDERE KOSTEN");
        temp118.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp119 = row44.createCell(i + 1, CellType.NUMERIC);
            temp119.setCellValue(-Math.round(getAndereKosten(i)));
            temp119.setCellStyle(BoldNumber);
        }
        Cell temp124 = row44.createCell(2 + documents.size());
        temp124.setCellValue("QUICK RATIO");
        temp124.setCellStyle(Bold);
        Cell temp125 = row44.createCell(3 + documents.size());
        temp125.setCellValue("COURANTE ACTIVA - VOORRAAD");
        temp125.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp126 = row44.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp126.setCellValue((getVlottendeActiva(i) - getVoorraden(i)) / getKorteTermijnSchulden(i));
            temp126.setCellStyle(DoubleStyleBold);
        }

        //ROW45
        Row row45 = report.createRow(44);
        Cell temp127 = row45.createCell(2 + documents.size());
        temp127.setCellValue(">0,7");
        temp127.setCellStyle(Bold);

        Cell temp128 = row45.createCell(3 + documents.size());
        temp128.setCellValue("COURANTE PASSIVA");
        temp128.setCellStyle(Bold);

        //ROW46
        Row row46 = report.createRow(45);
        Cell temp122 = row46.createCell(0);
        temp122.setCellValue("EBITDA");
        temp122.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp123 = row46.createCell(i + 1, CellType.NUMERIC);
            temp123.setCellValue(Math.round(getEBITDA(i)));
            temp123.setCellStyle(BoldYellowNumber);
        }

        //ROW48
        Row row48 = report.createRow(47);
        Cell temp129 = row48.createCell(0);
        temp129.setCellValue("AFSCHRIJVINGEN");
        temp129.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp130 = row48.createCell(i + 1, CellType.NUMERIC);
            temp130.setCellValue(-Math.round(getAfschrijvingen(i)));
            temp130.setCellStyle(BoldNumber);
        }
        Cell temp133 = row48.createCell(2 + documents.size());
        temp133.setCellValue("SOLVABILITEIT");
        temp133.setCellStyle(Bold);
        Cell temp134 = row48.createCell(3 + documents.size());
        temp134.setCellValue("EIGEN VERMOGEN");
        temp134.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp135 = row48.createCell(documents.size() + 4 + i);
            temp135.setCellValue(getEigenVermogen(i) / getTotalePassiva(i));
            temp135.setCellStyle(ProcentStyleBold);
        }

        //ROW49
        Row row49 = report.createRow(48);
        Cell temp131 = row49.createCell(0);
        temp131.setCellValue("WAARDEVERMINDERINGEN");
        temp131.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp132 = row49.createCell(i + 1, CellType.NUMERIC);
            temp132.setCellValue(-Math.round(getWaardeVermindering(i)));
            temp132.setCellStyle(BoldNumber);
        }
        Cell temp138 = row49.createCell(2 + documents.size());
        temp138.setCellValue(">25%");
        temp138.setCellStyle(Bold);

        Cell temp139 = row49.createCell(3 + documents.size());
        temp139.setCellValue("TOTALE PASSIVA");
        temp139.setCellStyle(Bold);

        //ROW51
        Row row51 = report.createRow(50);
        Cell temp136 = row51.createCell(0);
        temp136.setCellValue("BEDRIJFSWINST(EBIT)");
        temp136.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp137 = row51.createCell(i + 1, CellType.NUMERIC);
            temp137.setCellValue(Math.round(getEBIT(i)));
            temp137.setCellStyle(BoldYellowNumber);
        }
        Cell temp142 = row51.createCell(2 + documents.size());
        temp142.setCellValue("GEARING");
        temp142.setCellStyle(Bold);
        Cell temp143 = row51.createCell(3 + documents.size());
        temp143.setCellValue("NETTO FINANCIELE SCHULDEN");
        temp143.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp144 = row51.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp144.setCellValue((getKorteTermijnFinancieleSchulden(i) + getLangeTermijnFinancieleSchulden(i) - getCash(i)) / getEigenVermogen(i));
            temp144.setCellStyle(DoubleStyleBold);
        }

        //ROW52
        Row row52 = report.createRow(51);
        Cell temp1460 = row52.createCell(2 + documents.size());
        temp1460.setCellValue("<1");
        temp1460.setCellStyle(BoldBottom);
        Cell temp1461 = row52.createCell(3 + documents.size());
        temp1461.setCellValue("EIGEN VERMOGEN");
        temp1461.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            row52.createCell(documents.size() + 4 + i).setCellStyle(BottomNormal);
        }

        //ROW53
        Row row53 = report.createRow(52);
        Cell temp140 = row53.createCell(0);
        temp140.setCellValue("FINANCIELE RESULTATEN");
        temp140.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp141 = row53.createCell(i + 1, CellType.NUMERIC);
            temp141.setCellValue(Math.round(getFinancieleResultaten(i)));
            temp141.setCellStyle(BoldNumber);
        }

        //ROW54
        Row row54 = report.createRow(53);
        Cell temp145 = row54.createCell(0);
        temp145.setCellValue("UITZONDERLIJKE RESULTATEN");
        temp145.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp146 = row54.createCell(i + 1, CellType.NUMERIC);
            temp146.setCellValue(Math.round(getUitzonderlijkeResultaten(i)));
            temp146.setCellStyle(BoldNumber);
        }

        //ROW55
        Row row55 = report.createRow(54);
        Cell temp149 = row55.createCell(2 + documents.size());
        temp149.setCellValue("FINANCIELE LASTEN:");
        temp149.setCellStyle(BoldBottomTop);
        row55.createCell(3 + documents.size()).setCellStyle(BoldBottomTop);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp150 = row55.createCell(documents.size() + 4 + i);
            temp150.setCellValue(Math.round(getFinancieleKosten(i)));
            temp150.setCellStyle(BoldLightGreyNumber);
        }

        //ROW56
        Row row56 = report.createRow(55);
        Cell temp147 = row56.createCell(0);
        temp147.setCellValue("RESULTAAT VOOR BELASTINGEN");
        temp147.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp148 = row56.createCell(i + 1, CellType.NUMERIC);
            temp148.setCellValue(Math.round(getResultaatVoorBelastingen(i)));
            temp148.setCellStyle(BoldYellowNumber);
        }

        //ROW57
        Row row57 = report.createRow(56);
        Cell temp153 = row57.createCell(2 + documents.size());
        temp153.setCellValue("FINANCIERINGSLAST");
        temp153.setCellStyle(BoldTop);
        Cell temp154 = row57.createCell(3 + documents.size());
        temp154.setCellValue("SCHULDEN");
        temp154.setCellStyle(BoldBottomTop);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp155 = row57.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp155.setCellValue(getFinancieringsLast(i));
            temp155.setCellStyle(BoldTopDouble);
        }

        //ROW58
        Row row58 = report.createRow(57);
        Cell temp151 = row58.createCell(0);
        temp151.setCellValue("BELASTINGEN");
        temp151.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp152 = row58.createCell(i + 1, CellType.NUMERIC);
            temp152.setCellValue(-Math.round(getBelastingen(i)));
            temp152.setCellStyle(BoldNumber);
        }
        Cell temp158 = row58.createCell(2 + documents.size());
        temp158.setCellValue("<4");
        temp158.setCellStyle(Bold);

        Cell temp159 = row58.createCell(3 + documents.size());
        temp159.setCellValue("EBITDA");
        temp159.setCellStyle(Bold);

        //ROW60
        Row row60 = report.createRow(59);
        Cell temp156 = row60.createCell(0);
        temp156.setCellValue("NETTO WINST");
        temp156.setCellStyle(BoldYellow);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp157 = row60.createCell(i + 1, CellType.NUMERIC);
            temp157.setCellValue(Math.round(getWinstVerliesBoekjaar(i)));
            temp157.setCellStyle(BoldYellowNumber);
        }
        Cell temp160 = row60.createCell(2 + documents.size());
        temp160.setCellValue("INTRESTDEKKING");
        temp160.setCellStyle(Bold);
        Cell temp161 = row60.createCell(3 + documents.size());
        temp161.setCellValue("EBITDA");
        temp161.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp162 = row60.createCell(documents.size() + 4 + i, CellType.NUMERIC);
            temp162.setCellValue(Math.round(getEBITDA(i) / getFinancieleKosten(i)));
            temp162.setCellStyle(BoldNumber);
        }

        //ROW61
        Row row61 = report.createRow(60);
        Cell temp164 = row61.createCell(2 + documents.size());
        temp164.setCellValue(">1");
        temp164.setCellStyle(BoldBottom);
        Cell temp165 = row61.createCell(3 + documents.size());
        temp165.setCellValue("FINANCIELE LASTEN");
        temp165.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            row61.createCell(documents.size() + 4 + i).setCellStyle(BottomNormal);
        }

        //ROW63
        Row row63 = report.createRow(62);
        Cell temp163 = row63.createCell(0);
        temp163.setCellValue("EXTRA INFO");
        temp163.setCellStyle(Bold);

        //ROW64
        Row row64 = report.createRow(63);
        Cell temp169 = row64.createCell(2 + documents.size());
        temp169.setCellValue("Kostenstructuur");
        temp169.setCellStyle(BoldTop);
        row64.createCell(3 + documents.size()).setCellStyle(BoldTop);
        for (int i = 0; i < documents.size(); i++) {
            row64.createCell(documents.size() + 4 + i).setCellStyle(BoldTop);
        }

        //ROW65
        Row row65 = report.createRow(64);
        Cell temp166 = row65.createCell(0);
        temp166.setCellValue("Investeringen");
        temp166.setCellStyle(BoldBottomTop);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp167 = row65.createCell(i + 1, CellType.NUMERIC);
            temp167.setCellValue(Math.round(getInvesteringen(i)));
            temp167.setCellStyle(NumberBottomTop);
        }

        //ROW66
        Row row66 = report.createRow(65);
        Cell temp1660 = row66.createCell(0);
        temp1660.setCellValue("Investeringen/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp1660.setCellStyle(BoldBottomTop);
        } else {
            temp1660.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell temp168 = row66.createCell(i + 1, CellType.NUMERIC);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp168.setCellValue(getInvesteringen(i) / getOmzet(i));
                temp168.setCellStyle(ProcentStyleBoldBottomTop);
            } else {
                temp168.setCellValue("/");
                temp168.setCellStyle(Grey);
            }
        }
        Cell temp170 = row66.createCell(2 + documents.size());
        temp170.setCellValue("Aankopen/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp170.setCellStyle(Bold);
        } else {
            temp170.setCellStyle(Grey);
        }
        Cell temp171 = row66.createCell(3 + documents.size());
        temp171.setCellValue("AK/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp171.setCellStyle(Bold);
        } else {
            temp171.setCellStyle(Grey);
        }
        for (int i = 0; i < documents.size(); i++) {
            Cell temp172 = row66.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp172.setCellValue(getAankopen(i) / getOmzet(i));
                temp172.setCellStyle(ProcentStyleBold);
            } else {
                temp172.setCellValue("/");
                temp172.setCellStyle(Grey);
            }
        }

        //Row67
        Row row67 = report.createRow(66);
        Cell temp1661 = row67.createCell(0);
        temp1661.setCellValue("Investeringen/brutomarge");
        temp1661.setCellStyle(BoldBottomTop);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp168 = row67.createCell(i + 1, CellType.NUMERIC);
            temp168.setCellValue(getInvesteringen(i) / getBrutoMarge(i));
            temp168.setCellStyle(ProcentStyleBoldBottomTop);
        }

        //ROW68
        Row row68 = report.createRow(67);
        Cell temp173 = row68.createCell(2 + documents.size());
        temp173.setCellValue("Personeelskosten/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp173.setCellStyle(Bold);
        } else {
            temp173.setCellStyle(Grey);
        }

        Cell temp174 = row68.createCell(3 + documents.size());
        temp174.setCellValue("PK/omzet");
        if (style.equals(ReportStyle.HISTORIEKNV)) {
            temp174.setCellStyle(Bold);
        } else {
            temp174.setCellStyle(Grey);
        }

        for (int i = 0; i < documents.size(); i++) {
            Cell temp175 = row68.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp175.setCellValue(getPersoneelskosten(i) / getOmzet(i));
                temp175.setCellStyle(ProcentStyleBold);
            } else {
                temp175.setCellValue("/");
                temp175.setCellStyle(Grey);
            }
        }

        //ROW70
        Row row70 = report.createRow(69);
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

        for (int i = 0; i < documents.size(); i++) {
            Cell temp178 = row70.createCell(documents.size() + 4 + i);
            if (style.equals(ReportStyle.HISTORIEKNV)) {
                temp178.setCellValue(getAndereKosten(i) / getOmzet(i));
                temp178.setCellStyle(ProcentStyleBold);
            } else {
                temp178.setCellValue("/");
                temp178.setCellStyle(Grey);
            }
        }

        //ROW72
        Row row72 = report.createRow(71);
        Cell temp178 = row72.createCell(2 + documents.size());
        temp178.setCellValue("Personeelskosten/brutomarge");
        temp178.setCellStyle(Bold);
        Cell temp179 = row72.createCell(3 + documents.size());
        temp179.setCellValue("PK/brutomarge");
        temp179.setCellStyle(Bold);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp180 = row72.createCell(documents.size() + 4 + i);
            temp180.setCellValue(getPersoneelskosten(i) / getBrutoMarge(i));
            temp180.setCellStyle(ProcentStyleBold);
        }

        //ROW74
        Row row74 = report.createRow(73);
        Cell temp181 = row74.createCell(2 + documents.size());
        temp181.setCellValue("Andere kosten/brutomarge");
        temp181.setCellStyle(BoldBottom);
        Cell temp182 = row74.createCell(3 + documents.size());
        temp182.setCellValue("AK/brutomarge");
        temp182.setCellStyle(BoldBottom);
        for (int i = 0; i < documents.size(); i++) {
            Cell temp183 = row74.createCell(documents.size() + 4 + i);
            temp183.setCellValue(getAndereKosten(i) / getBrutoMarge(i));
            temp183.setCellStyle(ProcentStyleBoldBottom);
        }
    }

    public void createVergelijkingReport() {
        //ROW1
        Row row1 = report.createRow(0);
        row1.createCell(2).setCellValue("Naam");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempName = row1.createCell(i + 3);
            tempName.setCellValue(documents.get(i).getBusiness().getName());
        }

        //ROW2
        Row row2 = report.createRow(1);
        row2.createCell(2).setCellValue("Boekjaar");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempYear = row2.createCell(i + 3);
            tempYear.setCellValue(documents.get(i).getYear());
        }

        //ROW3
        Row row3 = report.createRow(2);
        row3.createCell(0).setCellValue("ACTIVA");

        //ROW4
        Row row4 = report.createRow(3);
        row4.createCell(1).setCellValue("vlottende activa");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempVA = row4.createCell(i + 3, CellType.NUMERIC);
            tempVA.setCellValue(Math.round(getVlottendeActiva(i)));
            tempVA.setCellStyle(Number);
        }

        //ROW5
        Row row5 = report.createRow(4);
        row5.createCell(1).setCellValue("liquide middelen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempLM = row5.createCell(i + 3, CellType.NUMERIC);
            tempLM.setCellValue(Math.round(getLiquideMiddelen(i)));
            tempLM.setCellStyle(Number);
        }

        //ROW6
        Row row6 = report.createRow(5);
        row6.createCell(1).setCellValue("totale activa");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempTA = row6.createCell(i + 3, CellType.NUMERIC);
            tempTA.setCellValue(Math.round(getTotaleActiva(i)));
            tempTA.setCellStyle(Number);
        }

        //ROW7
        Row row7 = report.createRow(6);
        row7.createCell(0).setCellValue("PASSIVA");

        //ROW8
        Row row8 = report.createRow(7);
        row8.createCell(1).setCellValue("eigen vermogen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempEV = row8.createCell(i + 3, CellType.NUMERIC);
            tempEV.setCellValue(Math.round(getEigenVermogen(i)));
            tempEV.setCellStyle(Number);
        }

        //ROW9
        Row row9 = report.createRow(8);
        row9.createCell(1).setCellValue("waarvan reserves");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempWR = row9.createCell(i + 3, CellType.NUMERIC);
            tempWR.setCellValue(Math.round(getReserves(i)));
            tempWR.setCellStyle(Number);
        }

        //ROW10
        Row row10 = report.createRow(9);
        row10.createCell(1).setCellValue("overgedragen winst");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempOW = row10.createCell(i + 3, CellType.NUMERIC);
            tempOW.setCellValue(Math.round(getOverdragenWinstVerlies(i)));
            tempOW.setCellStyle(Number);
        }

        //ROW11
        Row row11 = report.createRow(10);
        row11.createCell(1).setCellValue("schulden op korte termijn");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row11.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getKorteTermijnSchulden(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW12
        Row row12 = report.createRow(11);
        row12.createCell(0).setCellValue("RESULTATEN");

        //ROW13
        Row row13 = report.createRow(12);
        row13.createCell(1).setCellValue("omzet");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row13.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getOmzet(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW14
        Row row14 = report.createRow(13);
        row14.createCell(1).setCellValue("brutomarge (toegevoegde waarde)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row14.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getBrutoMarge(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW15
        Row row15 = report.createRow(14);
        row15.createCell(1).setCellValue("afschrijvingen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row15.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getAfschrijvingen(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW16
        Row row16 = report.createRow(15);
        row16.createCell(1).setCellValue("bedrijfswinst");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row16.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getBedrijfswinstVerlies(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW17
        Row row17 = report.createRow(16);
        row17.createCell(1).setCellValue("winst van boekjaar na belastingen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row17.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getWinstVerliesBoekjaar(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW18
        Row row18 = report.createRow(17);
        row18.createCell(0).setCellValue("FINANCIËLE RATIO'S");

        //ROW19
        Row row19 = report.createRow(18);
        row19.createCell(1).setCellValue("cashflow, of kasstroom : nettowinst + afschrijvingen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row19.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getWinstVerliesBoekjaar(i) + getAfschrijvingen(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW20
        Row row20 = report.createRow(19);
        row20.createCell(1).setCellValue("liquiditeitsratio: vlottende activa/schulden op korte termijn");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row20.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getVlottendeActiva(i) / getKorteTermijnSchulden(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW21
        Row row21 = report.createRow(20);
        row21.createCell(1).setCellValue("solvabiliteitsratio: schulden op korte termijn/totale activa");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row21.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getKorteTermijnSchulden(i) / getTotaleActiva(i));
            tempSKT.setCellStyle(ProcentStyle);
        }

        //ROW22
        Row row22 = report.createRow(21);
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
                tempSKT.setCellValue(getWinstVerliesBoekjaar(i) / getOmzet(i));
                tempSKT.setCellStyle(ProcentStyle);
            }
        }

        //ROW23
        Row row23 = report.createRow(22);
        row23.createCell(1).setCellValue("rentabiliteitsratio vh eigen vermogen: netto winst/eigen vermogen");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row23.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getWinstVerliesBoekjaar(i) / getEigenVermogen(i));
            tempSKT.setCellStyle(ProcentStyle);
        }

        //ROW24
        Row row24 = report.createRow(23);
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
                tempSKT.setCellValue(Math.round(getVoorraadrotatie(i) + getKlantenKrediet(i) - getLeveranciersKrediet(i)));
                tempSKT.setCellStyle(Number);
            }
        }

        //ROW25
        Row row25 = report.createRow(24);
        row25.createCell(0).setCellValue("PERSONEEL");

        //ROW26
        Row row26 = report.createRow(25);
        row26.createCell(1).setCellValue("gemiddeld aantal FTE (1003)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row26.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getGemiddeldeAantalFTE(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW27
        Row row27 = report.createRow(26);
        row27.createCell(1).setCellValue("gepresteerde uren (1013)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row27.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getGepresteerdeUren(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW28
        Row row28 = report.createRow(27);
        row28.createCell(1).setCellValue("personeelskosten (1023)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row28.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getSBPersoneelsKosten(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW30
        Row row30 = report.createRow(29);
        row30.createCell(1).setCellValue("gemiddeld aantal FTE uitzendkrachten (150)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row30.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getGemiddeldeAantalFTEUitzendkrachten(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW31
        Row row31 = report.createRow(30);
        row31.createCell(1).setCellValue("gepresteerde uren uitzendkrachten (151)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row31.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getGepresteerdeUrenUitzendkrachten(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW32
        Row row32 = report.createRow(31);
        row32.createCell(1).setCellValue("personeelskosten uitzendkrachten (152)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row32.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getPersoneelskostenUitzendkrachten(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW34
        Row row34 = report.createRow(33);
        row34.createCell(1).setCellValue("aantal werknemers op 31/12 (105/3)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row34.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalWerknemersOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW35
        Row row35 = report.createRow(34);
        row35.createCell(1).setCellValue("bedienden op 31/12 (134/3)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row35.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalBediendenOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW36
        Row row36 = report.createRow(35);
        row36.createCell(1).setCellValue("arbeiders op 31/12 (134/3)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row36.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalArbeiderssOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW37
        Row row37 = report.createRow(36);
        row37.createCell(0).setCellValue("PERSONEELRATIO'S");

        //ROW38
        Row row38 = report.createRow(37);
        row38.createCell(1).setCellValue("personeelskost/aantal FTE");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row38.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getSBPersoneelsKosten(i) / getGemiddeldeAantalFTE(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW39
        Row row39 = report.createRow(38);
        row39.createCell(1).setCellValue("personeelskost/gepresteerde uren");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row39.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getSBPersoneelsKosten(i) / getGepresteerdeUren(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW41
        Row row41 = report.createRow(40);
        row41.createCell(1).setCellValue("personeelskost uitzendkrachten/aantal FTE uitzendkrachten");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row41.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(Math.round(getPersoneelskostenUitzendkrachten(i) / getGemiddeldeAantalFTEUitzendkrachten(i)));
            tempSKT.setCellStyle(Number);
        }

        //ROW42
        Row row42 = report.createRow(41);
        row42.createCell(1).setCellValue("personeelskost uitzendkrachten/gepresteerde uren uitzendkrachten");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row42.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getPersoneelskostenUitzendkrachten(i) / getGepresteerdeUrenUitzendkrachten(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW44
        Row row44 = report.createRow(43);
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
                tempSKT.setCellValue(getOmzet(i) / (getGepresteerdeUrenUitzendkrachten(i) + getGepresteerdeUren(i)));
                tempSKT.setCellStyle(DoubleStyle);
            }
        }

        //ROW45
        Row row45 = report.createRow(44);
        row45.createCell(1).setCellValue("netto winst/totaal aantal gepresteerde uren (eigen + interim)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row45.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getWinstVerliesBoekjaar(i) / (getGepresteerdeUrenUitzendkrachten(i) + getGepresteerdeUren(i)));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW46
        Row row46 = report.createRow(45);
        row46.createCell(1).setCellValue("cashflow/totaal aantal gepresteerde uren (eigen + interim)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row46.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getCashFlow(i) / (getGepresteerdeUrenUitzendkrachten(i) + getGepresteerdeUren(i)));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW48
        Row row48 = report.createRow(47);
        row48.createCell(1).setCellValue("verhouding arbeiders/bedienden (31/12)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row48.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getAantalArbeiderssOpEindeBoekjaar(i) / getAantalBediendenOpEindeBoekjaar(i));
            tempSKT.setCellStyle(DoubleStyle);
        }

        //ROW49
        Row row49 = report.createRow(48);
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
                tempSKT.setCellValue(getOmzet(i) / getAantalBediendenOpEindeBoekjaar(i));
                tempSKT.setCellStyle(Number);
            }
        }

        //ROW50
        Row row50 = report.createRow(49);
        row50.createCell(1).setCellValue("netto winst/bediende (31/12)");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row50.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue(getWinstVerliesBoekjaar(i) / getAantalBediendenOpEindeBoekjaar(i));
            tempSKT.setCellStyle(Number);
        }

        //ROW51
        Row row51 = report.createRow(50);
        row51.createCell(1).setCellValue("netto winst bediende (31/12) per uur: netto winst/bediende (31/12)/1744");
        for (int i = 0; i < documents.size(); i++) {
            Cell tempSKT = row51.createCell(i + 3, CellType.NUMERIC);
            tempSKT.setCellValue((getWinstVerliesBoekjaar(i) / getAantalBediendenOpEindeBoekjaar(i)) / 1755);
            tempSKT.setCellStyle(DoubleStyle);
        }
    }

    private void makeStyles() {
        fontBold = workbook.createFont();
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

        Number = workbook.createCellStyle();
        Number.setDataFormat(numberFormatter.getFormat("### ### ##0"));

        BoldTopDouble = workbook.createCellStyle();
        BoldTopDouble.setFont(fontBold);
        BoldTopDouble.setBorderTop(BorderStyle.THIN);
        BoldTopDouble.setDataFormat(numberFormatter.getFormat("### ### ##0.00"));

        Grey = workbook.createCellStyle();
        Grey.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        Grey.setFont(fontBold);
        Grey.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        ProcentStyle = workbook.createCellStyle();
        ProcentStyle.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));

        ProcentStyleBold = workbook.createCellStyle();
        ProcentStyleBold.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));
        ProcentStyleBold.setFont(fontBold);

        ProcentStyleBoldBottom = workbook.createCellStyle();
        ProcentStyleBoldBottom.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));
        ProcentStyleBoldBottom.setFont(fontBold);
        ProcentStyleBoldBottom.setBorderBottom(BorderStyle.THIN);

        DoubleStyle = workbook.createCellStyle();
        DoubleStyle.setDataFormat(numberFormatter.getFormat("### ### ##0.00"));

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

        ProcentStyleBoldBottomTop = workbook.createCellStyle();
        ProcentStyleBoldBottomTop.setDataFormat(numberFormatter.getFormat("### ### ##0.00%"));
        ProcentStyleBoldBottomTop.setFont(fontBold);
        ProcentStyleBoldBottomTop.setBorderBottom(BorderStyle.THIN);
        ProcentStyleBoldBottomTop.setBorderTop(BorderStyle.THIN);
    }

    private double getAndereVorderingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAVorderingenHoogstens1JaarOverigeVorderingen));
    }

    private double getAantalWerknemersOpEindeBoekjaar(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBAantalWerknemersOpEindeBoekjaar));
    }

    private double getAantalBediendenOpEindeBoekjaar(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBAantalBediendenOpEindeBoekjaar));
    }

    private double getAantalArbeiderssOpEindeBoekjaar(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBAantalArbeidersOpEindeBoekjaar));
    }

    private double getPersoneelskostenUitzendkrachten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBPersoneelskostenUitzendkrachten));
    }

    private double getGepresteerdeUrenUitzendkrachten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBGepresteerdeUrenUitzendkrachten));
    }

    private double getGemiddeldeAantalFTEUitzendkrachten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBGemiddeldAantalFTEUitzendkrachten));
    }

    private double getSBPersoneelsKosten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBPersoneelskosten));
    }

    private double getGepresteerdeUren(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBGepresteerdeUren));
    }

    private double getGemiddeldeAantalFTE(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.SBGemiddeldeFTE));
    }

    private double getLiquideMiddelen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BALiquideMiddelen));
    }

    private double getTotaleActiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BATotaalActiva));
    }

    private double getReserves(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPReserves));
    }

    private double getOverdragenWinstVerlies(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPOvergedragenWinstVerlies));
    }

    private double getBedrijfsOpbrengsten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsopbrengsten));
    }

    private double getBedrijfswinstVerlies(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsWinstVerlies));
    }

    private double getWinstVerliesBoekjaar(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRWinstVerliesBoekjaar));
    }

    private double getProvisies(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPVoorzieningenUitgesteldeBelastingen));
    }

    private double getFinancieringsLast(int index) {
        return (getKorteTermijnFinancieleSchulden(index) + getLangeTermijnFinancieleSchulden(index)) / getEBITDA(index);
    }

    private double getCash(int index) {
        return getLiquideMiddelen(index) + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAOverlopendeRekeningen));
    }

    private double getVoorraadrotatie(int index) {
        return getVoorraden(index) / getOmzet(index) * 365;
    }

    private double getKlantenKrediet(int index) {
        return getHandelsvorderingen(index) / getOmzet(index) * 365;
    }

    private double getLeveranciersKrediet(int index) {
        return getLeveranciers(index) / getOmzet(index) * 365;
    }

    private double getCashFlow(int index) {
        return getWinstVerliesBoekjaar(index) + getAfschrijvingen(index);
    }

    private double getWaardeVermindering(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen));
    }

    private double getBelastingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBelastingenOpResultaat))
                - Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RROntrekkingenUitgesteldeBelastingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RROverboekingUitgesteldeBelastingen));
    }

    private double getAndereKosten(int index) {
        double value = Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenDienstenDiverseGoederen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenAndereBedrijfskosten))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten));
        if (Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten)) != Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten))) {
            value += Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten));
        }
        return value;
    }

    private double getPersoneelskosten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenBezoldigingenSocialeLastenPensioenen));
    }

    private double getAankopen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffen));
    }

    private double getKorteTermijnFinancieleSchulden(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden));
    }

    private double getLangeTermijnFinancieleSchulden(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPSchuldenMeer1JaarFinancieleSchulden));
    }

    private double getTotalePassiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPTotaalPassiva));
    }

    private double getKorteTermijnSchulden(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1Jaar)) + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPOverlopendeRekeningen));
    }

    private double getVlottendeActiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAVlottendeActiva));
    }

    private double getInvesteringen(int index) {
        //NV
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen))
                //BVBA
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLMVAMutatiesTijdensBoekjaarAanschaffingen))
                + Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.TLFVAMutatiesTijdensBoekjaarAanschaffingen));
    }

    private double getAfschrijvingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva));
    }

    private double getVasteActiva(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAVasteActiva));
    }

    private double getEigenVermogen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPEigenVermogen));
    }

    private double getLeveranciers(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenLeveranciers));
    }

    private double getHandelsvorderingen(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAVorderingenHoogstens1JaarHandelsvorderingen));
    }

    private double getVoorraden(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BAVoorradenBestellingenUitvoering));
    }

    private double getResultaatVoorBelastingen(int index) {
        return getEBITDA(index)
                - getAfschrijvingen(index)
                - getWaardeVermindering(index)
                + getFinancieleResultaten(index)
                + getUitzonderlijkeResultaten(index);
    }

    private double getUitzonderlijkeResultaten(int index) {
        double value = 0;
        if (Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten)) != Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten))) {
            value += Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten));
        }
        if (Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten)) != Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten))) {
            value -= Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRBedrijfskostenUitzonderlijkeKosten));
        }
        return value;
    }

    private double getOmzet(int index) {
        return getBedrijfsOpbrengsten(index);
    }

    private double getEBITDA(int index) {
        if (style.equals(ReportStyle.HISTORIEKBVBA)) {
            return getBrutoMarge(index) - getBedrijfskosten(index);
        } else if (style.equals(ReportStyle.HISTORIEKNV)) {
            return getOmzet(index) - getBedrijfskosten(index);
        }
        return 0;
    }

    private double getEBIT(int index) {
        return getEBITDA(index)
                - getAfschrijvingen(index)
                - getWaardeVermindering(index);
    }

    private double getFinancieleResultaten(int index) {
        double inkom = Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleOpbrengsten));
        if (inkom == 0) {
            inkom = Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleOpbrengstenRecurrent));
        }
        double uitgaand = getFinancieleKosten(index);
        if (uitgaand == 0) {
            uitgaand = Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleKostenRecurrent));
        }
        return inkom - uitgaand;
    }

    private double getFinancieleKosten(int index) {
        return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.RRFinancieleKosten));
    }

    private double getCoreNettoWerkKapitaal(int index) {
        return getVoorraden(index) + getHandelsvorderingen(index) - getLeveranciers(index);
    }

    private double getCapitalEmployed(int index) {
        return getCoreNettoWerkKapitaal(index) + getVasteActiva(index);
    }

    private double getBedrijfskosten(int index) {
        return getAankopen(index)
                + getPersoneelskosten(index)
                + getAndereKosten(index);
    }

    private double getBrutoMarge(int index) {
        if (style.equals(ReportStyle.HISTORIEKBVBA) || style.equals(ReportStyle.VERGELIJKINGBVBA)) {
            return Double.parseDouble(documents.get(index).getPropertiesMap().get(PropertyName.BVBABrutomarge));
        } else {
            return getBedrijfsOpbrengsten(index) - getAankopen(index);
        }
    }

    private double getAndereSchuldenKorteTermijn(int i) throws NumberFormatException {
        return Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1Jaar))
                + Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPOverlopendeRekeningen))
                - getLeveranciers(i)
                - Double.parseDouble(documents.get(i).getPropertiesMap().get(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden));
    }
}
