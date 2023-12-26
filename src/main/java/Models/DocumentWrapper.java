package Models;

import Models.Enums.FileExtension;
import Models.Enums.PropertyName;
import Models.Interfaces.IDocumentBuilder;
import Models.Interfaces.IDocumentWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Util.XmlUtil.asList;

public class DocumentWrapper implements IDocumentWrapper {

    private final int year;
    private final Business business;
    private final Map<PropertyName, String> properties;

    private DocumentWrapper(DocumentBuilder documentBuilder) {
        this.year = documentBuilder.year;
        this.business = documentBuilder.business;
        this.properties = documentBuilder.properties;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public Business getBusiness() {
        return business;
    }

    @Override
    public Map<PropertyName, String> getPropertiesMap() {
        return properties;
    }

    public static class DocumentBuilder implements IDocumentBuilder {

        private final String name;
        private final int year;
        private Business business;
        private final Map<PropertyName, String> properties;
        private final boolean selected;
        private Document document = null;
        private Map<String, String> csvValues = null;
        private ArrayList<String> currentTimePeriods;
        private final FileExtension fileExtension;

        public DocumentBuilder(Document document, String fileName, int year, FileExtension fileExtension) {
            this.document = document;
            this.name = fileName;
            this.year = year;
            this.selected = true;
            this.properties = new HashMap<>();
            for (PropertyName propName : PropertyName.values()) {
                properties.put(propName, "0");
            }
            this.fileExtension = fileExtension;
        }

        public DocumentBuilder(Map<String, String> csvValues, String fileName, int year, FileExtension fileExtension) {
            this.csvValues = csvValues;
            this.name = fileName;
            this.year = year;
            this.selected = true;
            this.properties = new HashMap<>();
            for (PropertyName propname : PropertyName.values()) {
                properties.put(propname, "0");
            }
            this.fileExtension = fileExtension;
        }

        private String getTextContentOfTag(String tag) {
            Node tagNode = this.document.getElementsByTagName(tag).item(0);
            return tagNode == null ? "" : tagNode.getTextContent();
        }

        public boolean extractBusiness() {
            try {
                switch (fileExtension) {
                    case XBRL:
                        this.business = new Business(
                                getTextContentOfTag("pfs-gcd:EntityCurrentLegalName"),
                                getTextContentOfTag("pfs-gcd:IdentifierValue"),
                                getTextContentOfTag("pfs-gcd:Street"),
                                getTextContentOfTag("pfs-gcd:Number"),
                                getTextContentOfTag("pfs-gcd:Box"),
                                getTextContentOfTag("pfs-gcd:PostalCodeCity"),
                                getTextContentOfTag("pfs-gcd:CountryCode"));
                        return false;
                    case CSV:
                        this.business = new Business(
                                csvValues.get("Entity name"),
                                csvValues.get("Entity number"),
                                csvValues.get("Entity address street"),
                                csvValues.get("Entity address number"),
                                csvValues.get("Entity address box") == null ? "" : csvValues.get("Entity address box"),
                                csvValues.get("Entity postal code"),
                                csvValues.get("Entity country")
                        );
                        return false;
                    default:
                        return true;
                }
            } catch (NullPointerException ex) {
                return true;
            }
        }

        public boolean extractCurrentTimePeriods() {
            try {
                ArrayList<String> currentTimePeriods = new ArrayList<>();

                int index = 0;
                boolean currentYearFound = false, currentDurationFound = false;
                NodeList contextList = this.document.getElementsByTagName("xbrli:context");
                while (index < contextList.getLength()) {
                    Node e = contextList.item(index);
                    int numberOfChildren = e.getChildNodes().item(3).getChildNodes().getLength();
                    String idAttribute = e.getAttributes().getNamedItem("id").getTextContent();

                    if ((numberOfChildren == 3 && !currentYearFound) || (numberOfChildren == 5 && !currentDurationFound)) {
                        currentTimePeriods.add(idAttribute);
                        if (numberOfChildren == 3)
                            currentYearFound = true;
                        else
                            currentDurationFound = true;
                    }

                    index++;
                }
                this.currentTimePeriods = currentTimePeriods;
                return true;
            } catch (NullPointerException ex) {
                return false;
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getYear() {
            return year;
        }

        @Override
        public Business getBusiness() {
            return business;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        private String getStringFromXBRL(String tagName) {
            List<Node> elements = asList(document.getElementsByTagName(String.format("pfs:%s", tagName)));
            if (elements.isEmpty()) {
                elements = asList(document.getElementsByTagName(String.format("c:%s", tagName)));
            }
            for (Node node : elements) {
                String currentRef = node.getAttributes().getNamedItem("contextRef").getTextContent();
                if (currentTimePeriods.contains(currentRef)) {
                    return node.getTextContent();
                }
            }
            return "0";
        }

        private String getStringFromCSVValues(String key) {
            String value = csvValues.get(key);
            return value == null ? "0" : value;
        }

        @Override
        public DocumentWrapper build() {
            return new DocumentWrapper(this);
        }

        private IDocumentBuilder addProperty(PropertyName propertyName, String CSVKey, String XBRLKey) {
            switch (fileExtension) {
                case CSV:
                    properties.replace(propertyName, getStringFromCSVValues(CSVKey));
                    break;
                case XBRL:
                    properties.replace(propertyName, getStringFromXBRL(XBRLKey));
            }
            return this;
        }

        private IDocumentBuilder addProperty(PropertyName propertyName, String CSVKey, String XBRLKey, String XBRLKeyBackup) {
            switch (fileExtension) {
                case CSV:
                    properties.replace(propertyName, getStringFromCSVValues(CSVKey));
                    break;
                case XBRL:
                    String stringFromXBRL = getStringFromXBRL(XBRLKey);
                    if (stringFromXBRL.equals("0")) {
                        stringFromXBRL = getStringFromXBRL(XBRLKeyBackup);
                    }
                    properties.replace(propertyName, stringFromXBRL);
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAOprichtingskosten() {
            return addProperty(PropertyName.BAOprichtingskosten, "20", "FormationExpenses");
        }

        @Override
        public IDocumentBuilder addBAVasteActiva() {
            return addProperty(PropertyName.BAVasteActiva, "21/28", "FixedAssetsFormationExpensesExcluded", "FixedAssets");
        }

        @Override
        public IDocumentBuilder addBAImmaterieleVasteActiva() {
            return addProperty(PropertyName.BAImmaterieleVasteActiva, "21", "IntangibleFixedAssets");
        }

        @Override
        public IDocumentBuilder addBAMaterieleVasteActiva() {
            return addProperty(PropertyName.BAMaterieleVasteActiva, "22/27", "TangibleFixedAssets");
        }

        @Override
        public IDocumentBuilder addBATerreinenGebouwen() {
            return addProperty(PropertyName.BATerreinenGebouwen, "22", "LandBuildings");
        }

        @Override
        public IDocumentBuilder addBAInstallatiesMachinesUitrusting() {
            return addProperty(PropertyName.BAInstallatiesMachinesUitrusting, "23", "PlantMachineryEquipment");
        }

        @Override
        public IDocumentBuilder addBAMeubilairRollendMaterieel() {
            return addProperty(PropertyName.BAMeubilairRollendMaterieel, "24", "FurnitureVehicles");
        }

        @Override
        public IDocumentBuilder addBALeasingSoortgelijkeRechten() {
            return addProperty(PropertyName.BALeasingSoortgelijkeRechten, "25", "LeasingSimilarRights");
        }

        @Override
        public IDocumentBuilder addBAOverigeMaterieleVasteActiva() {
            return addProperty(PropertyName.BAOverigeMaterieleVasteActiva, "26", "OtherTangibleAssets");
        }

        @Override
        public IDocumentBuilder addBAActivaAanbouwVooruitbetalingen() {
            return addProperty(PropertyName.BAActivaAanbouwVooruitbetalingen, "27", "AssetsUnderConstructionAdvancePayments");
        }

        @Override
        public IDocumentBuilder addBAFinancieleVasteActiva() {
            return addProperty(PropertyName.BAFinancieleVasteActiva, "28", "FinancialFixedAssets");
        }

        @Override
        public IDocumentBuilder addBAVerbondenOndernemingen() {
            return addProperty(PropertyName.BAVerbondenOndernemingen, "280/1", "ParticipatingInterestsAffiliatedEnterprises");
        }

        @Override
        public IDocumentBuilder addBAVerbondenOndernemingenDeelnemingen() {
            return addProperty(PropertyName.BAVerbondenOndernemingenDeelnemingen, "280", "ParticipatingInterestsAmountsReceivableAffiliatedEnterprises");
        }

        @Override
        public IDocumentBuilder addBAVerbondenOndernemingenVorderingen() {
            return addProperty(PropertyName.BAVerbondenOndernemingenVorderingen, "281", "OtherAmountsReceivableAffiliatedEnterprises");
        }

        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhouding() {
            return addProperty(PropertyName.BAOndernemingenDeelnemingsverhouding, "282/3",
                    "ParticipatingInterestsAmountsReceivableOtherEnterprisesLinkedParticipatingInterestsAssociatedEnterprisesExcluded");
        }

        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingDeelnemingen() {
            return addProperty(PropertyName.BAOndernemingenDeelnemingsverhoudingDeelnemingen, "282",
                    "ParticipatingInterestsOtherEnterprisesLinkedParticipatingInterestsAssociatedEnterprisesExcluded");
        }

        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingVorderingen() {
            return addProperty(PropertyName.BAOndernemingenDeelnemingsverhoudingVorderingen, "283", "SubordinatedAmountsReceivableEnterprisesLinkedByParticipatingInterests");
        }

        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActiva() {
            return addProperty(PropertyName.BAAndereFinancieleVasteActiva, "284/8", "OtherFinancialAssets");
        }

        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActivaAandelen() {
            return addProperty(PropertyName.BAAndereFinancieleVasteActivaAandelen, "284", "OtherFinancialAssetsParticipatingInterestsShares");
        }

        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten() {
            return addProperty(PropertyName.BAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten, "285/8", "OtherFinancialAssetsAmountsReceivableCashGuarantees");
        }

        @Override
        public IDocumentBuilder addBAVlottendeActiva() {
            return addProperty(PropertyName.BAVlottendeActiva, "29/58", "CurrentsAssets");
        }

        @Override
        public IDocumentBuilder addBAVorderingenMeer1Jaar() {
            return addProperty(PropertyName.BAVorderingenMeer1Jaar, "29", "AmountsReceivableMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBAVorderingenMeer1JaarHandelsvorderingen() {
            return addProperty(PropertyName.BAVorderingenMeer1JaarHandelsvorderingen, "290", "TradeDebtorsMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBAVorderingenMeer1JaarOverigeVorderingen() {
            return addProperty(PropertyName.BAVorderingenMeer1JaarOverigeVorderingen, "291", "OtherAmountsReceivableMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoering() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoering, "3", "StocksContractsProgress");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorraden() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringVoorraden, "30/36", "Stocks");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen, "30/31", "StockRawMaterialsConsumables");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking, "32", "StockWorkProgress");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGereedProduct() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGereedProduct, "33", "StockFinishedGoods");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen, "34", "StockGoodsPurchasedResale");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop, "35", "StockImmovablePropertyIntendedSale");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen, "36", "AdvancePaymentsPurchasesStocks");
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringBestellingenUitvoer() {
            return addProperty(PropertyName.BAVoorradenBestellingenUitvoeringBestellingenUitvoer, "37", "ContractsProgress");
        }

        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1Jaar() {
            return addProperty(PropertyName.BAVorderingenHoogstens1Jaar, "40/41", "AmountsReceivableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1JaarHandelsvorderingen() {
            return addProperty(PropertyName.BAVorderingenHoogstens1JaarHandelsvorderingen, "40", "TradeDebtorsWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1JaarOverigeVorderingen() {
            return addProperty(PropertyName.BAVorderingenHoogstens1JaarOverigeVorderingen, "41", "OtherAmountsReceivableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBAGeldBeleggingen() {
            return addProperty(PropertyName.BAGeldBeleggingen, "50/53", "CurrentInvestments");
        }

        @Override
        public IDocumentBuilder addBAGeldBeleggingenEigenAandelen() {
            return addProperty(PropertyName.BAGeldBeleggingenEigenAandelen, "50", "OwnShares");
        }

        @Override
        public IDocumentBuilder addBAGeldBeleggingenOverigeBeleggingen() {
            return addProperty(PropertyName.BAGeldBeleggingenOverigeBeleggingen, "51/53", "OtherCurrentInvestments");
        }

        @Override
        public IDocumentBuilder addBALiquideMiddelen() {
            return addProperty(PropertyName.BALiquideMiddelen, "54/58", "CashBankHand");
        }

        @Override
        public IDocumentBuilder addBAOverlopendeRekeningen() {
            return addProperty(PropertyName.BAOverlopendeRekeningen, "490/1", "DeferredChargesAccruedIncome");
        }

        @Override
        public IDocumentBuilder addBATotaalActiva() {
            return addProperty(PropertyName.BATotaalActiva, "20/58", "Assets");
        }

        @Override
        public IDocumentBuilder addBPEigenVermogen() {
            return addProperty(PropertyName.BPEigenVermogen, "10/15", "Equity");
        }

        @Override
        public IDocumentBuilder addBPKapitaal() {
            return addProperty(PropertyName.BPKapitaal, "10", "Capital");
        }

        @Override
        public IDocumentBuilder addBPKapitaalGeplaatst() {
            return addProperty(PropertyName.BPKapitaalGeplaatst, "100", "IssuedCapital");
        }

        @Override
        public IDocumentBuilder addBPKapitaalNietOpgevraagd() {
            return addProperty(PropertyName.BPKapitaalNietOpgevraagd, "101", "UncalledCapital");
        }

        @Override
        public IDocumentBuilder addBPUitgiftepremies() {
            return addProperty(PropertyName.BPUitgiftepremies, "1100/10", "SharePremiumAccount");
        }

        @Override
        public IDocumentBuilder addBPHerwaarderingsmeerwaarden() {
            return addProperty(PropertyName.BPHerwaarderingsmeerwaarden, "12", "RevaluationSurpluses");
        }

        @Override
        public IDocumentBuilder addBPReserves() {
            return addProperty(PropertyName.BPReserves, "13", "Reserves");
        }

        @Override
        public IDocumentBuilder addBPReservesWettelijkeReserve() {
            return addProperty(PropertyName.BPReservesWettelijkeReserve, "130", "DifferentCategoriesSharesValue");
        }

        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReserves() {
            return addProperty(PropertyName.BPReservesOnbeschikbareReserves, "130/1", "ReservesNotAvailable");
        }

        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReservesEigenAandelen() {
            return addProperty(PropertyName.BPReservesOnbeschikbareReservesEigenAandelen, "1312", "ReservesNotAvailableOwnSharesHeld");
        }

        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReservesAndere() {
            return addProperty(PropertyName.BPReservesOnbeschikbareReservesAndere, "1319", "OtherReservesNotAvailable");
        }

        @Override
        public IDocumentBuilder addBPReservesBelastingvrijeReserves() {
            return addProperty(PropertyName.BPReservesBelastingvrijeReserves, "132", "UntaxedReserves");
        }

        @Override
        public IDocumentBuilder addBPReservesBeschikbareReserves() {
            return addProperty(PropertyName.BPReservesBeschikbareReserves, "133", "AvailableReserves");
        }

        @Override
        public IDocumentBuilder addBPOvergedragenWinstVerlies() {
            return addProperty(PropertyName.BPOvergedragenWinstVerlies, "14", "AccumulatedProfitsLosses");
        }

        @Override
        public IDocumentBuilder addBPKapitaalSubsidies() {
            return addProperty(PropertyName.BPKapitaalSubsidies, "15", "InvestmentGrants");
        }

        @Override
        public IDocumentBuilder addBPVoorschotVennotenVerdelingNettoActief() {
            return addProperty(PropertyName.BPVoorschotVennotenVerdelingNettoActief, "19", "AdvanceAssociatesSharingOutAssets");
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenUitgesteldeBelastingen() {
            return addProperty(PropertyName.BPVoorzieningenUitgesteldeBelastingen, "16", "ProvisionsDeferredTaxes");
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKosten() {
            return addProperty(PropertyName.BPVoorzieningenRisicosKosten, "160/5", "ProvisionLiabilitiesCharges");
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen() {
            return addProperty(PropertyName.BPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen, "160", "ProvisionsPensionsSimilarObligations");
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenFiscaleLasten() {
            return addProperty(PropertyName.BPVoorzieningenRisicosKostenFiscaleLasten, "161", "ProvisionsTaxation");
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken() {
            return addProperty(PropertyName.BPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken, "162", "ProvisionsMajorRepairsMaintenance");
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenMilieuverplichtingen() {
            return addProperty(PropertyName.BPVoorzieningenRisicosKostenMilieuverplichtingen, "163", "ProvisionsOtherLiabilitiesCharges");
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenOverige() {
            return addProperty(PropertyName.BPVoorzieningenRisicosKostenOverige, "164/5", "DeferredTaxes");
        }

        @Override
        public IDocumentBuilder addBPSchulden() {
            return addProperty(PropertyName.BPSchulden, "17/49", "AmountsPayable");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1Jaar() {
            return addProperty(PropertyName.BPSchuldenMeer1Jaar, "17", "AmountsPayableMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchulden() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarFinancieleSchulden, "170/4", "FinancialDebtsRemainingTermMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen, "170", "SubordinatedLoansRemainingTermMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen, "171", "UnsubordinatedDebenturesRemainingTermMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden, "172", "LeasingSimilarObligationsRemainingTermMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen, "173", "AmountsPayableMoreOneYearCreditInstitutions");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen, "174", "OtherLoansRemainingTermMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarHandelsschulden() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarHandelsschulden, "175", "TradeDebtsPayableMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels, "1751", "BillExchangeMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen, "176", "AdvancesReceivedContractsProgressWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarOverigeSchulden() {
            return addProperty(PropertyName.BPSchuldenMeer1JaarOverigeSchulden, "178/9", "OtherAmountsPayableMoreOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1Jaar() {
            return addProperty(PropertyName.BPSchuldenHoogstens1Jaar, "42/48", "AmountsPayableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen, "42", "CurrentPortionAmountsPayableMoreOneYearFallingDueWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchulden() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden, "43", "FinancialDebtsPayableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen, "430/8", "AmountsPayableWithinOneYearCreditInstitutions");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen, "439", "OtherLoansPayableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschulden() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarHandelsschulden, "44", "TradeDebtsPayableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenLeveranciers() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenLeveranciers, "440/4", "SuppliersInvoicesToReceiveWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels, "441", "BillExchangePayableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen, "46", "AdvancesReceivedContractsProgressWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten, "45", "TaxesRemunerationSocialSecurity");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen, "450/3", "Taxes");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten, "454/9", "RemunerationSocialSecurity");
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarOverigeSchulden() {
            return addProperty(PropertyName.BPSchuldenHoogstens1JaarOverigeSchulden, "47/48", "OtherAmountsPayableWithinOneYear");
        }

        @Override
        public IDocumentBuilder addBPOverlopendeRekeningen() {
            return addProperty(PropertyName.BPOverlopendeRekeningen, "492/3", "AccruedChargesDeferredIncome");
        }

        @Override
        public IDocumentBuilder addBPTotaalPassiva() {
            return addProperty(PropertyName.BPTotaalPassiva, "10/49", "EquityLiabilities");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengsten() {
            return addProperty(PropertyName.RRBedrijfsopbrengsten, "70/76A", "OperatingIncomeNonRecurringOperatingIncomeIncluded", "OperatingIncome");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenOmzet() {
            return addProperty(PropertyName.RRBedrijfsopbrengstenOmzet, "70", "Turnover");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering() {
            return addProperty(PropertyName.RRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering, "71", "IncreaseDecreaseStocksWorkContractsProgress");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenGeproduceerdeVasteActiva() {
            return addProperty(PropertyName.RRBedrijfsopbrengstenGeproduceerdeVasteActiva, "72", "OwnConstructionCapitalised");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenAndereBedrijfsopbrengsten() {
            return addProperty(PropertyName.RRBedrijfsopbrengstenAndereBedrijfsopbrengsten, "74", "OtherOperatingIncome");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten() {
            return addProperty(PropertyName.RRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten, "76A", "NonRecurringOperatingIncome");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskosten() {
            return addProperty(PropertyName.RRBedrijfskosten, "60/66A", "OperatingChargesNonRecurringOperatingChargesIncluded");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffen() {
            return addProperty(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffen, "60", "RawMaterialsConsumables");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen() {
            return addProperty(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen, "600/8", "PurchasesRawMaterialsConsumables");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename() {
            return addProperty(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename, "609", "IncreaseDecreaseStocks");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenDienstenDiverseGoederen() {
            return addProperty(PropertyName.RRBedrijfskostenDienstenDiverseGoederen, "61", "ServicesOtherGoods");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenBezoldigingenSocialeLastenPensioenen() {
            return addProperty(PropertyName.RRBedrijfskostenBezoldigingenSocialeLastenPensioenen, "62", "RemunerationSocialSecurityPensions");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva() {
            return addProperty(PropertyName.RRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva, "630", "DepreciationOtherAmountsWrittenDownFormationExpensesIntangibleTangibleFixedAssets");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen() {
            return addProperty(PropertyName.RRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen, "631/4", "AmountsWrittenDownStocksContractsProgressTradeDebtorsAppropriationsWriteBacks");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen() {
            return addProperty(PropertyName.RRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen, "635/8", "ProvisionsRisksChargesAppropriationsWriteBacks");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenAndereBedrijfskosten() {
            return addProperty(PropertyName.RRBedrijfskostenAndereBedrijfskosten, "640/8", "MiscellaneousOperatingCharges");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHerstructureringskostenGeactiveerdeBedrijfskosten() {
            return addProperty(PropertyName.RRBedrijfskostenHerstructurerngskostenGeactiveerdeBedrijfskosten, "649", "OperatingChargesCarriedAssetsRestructuringCosts");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenNietRecurrenteBedrijfskosten() {
            return addProperty(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten, "66A", "NonRecurringOperatingCharges");
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenUitzonderlijkeKosten() {
            return addProperty(PropertyName.RRBedrijfskostenUitzonderlijkeKosten, "66", "ExtraordinaryCharges");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenUitzonderlijkeOpbrengsten() {
            return addProperty(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten, "76", "ExtraordinaryIncome");
        }

        @Override
        public IDocumentBuilder addRRBedrijfsWinstVerlies() {
            return addProperty(PropertyName.RRBedrijfsWinstVerlies, "9901", "OperatingProfitLoss");
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengsten() {
            return addProperty(PropertyName.RRFinancieleOpbrengsten, "75/76B", "FinancialIncomeNonRecurringFinancialIncomeIncluded");
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrent() {
            return addProperty(PropertyName.RRFinancieleOpbrengstenRecurrent, "75", "FinancialIncome");
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva() {
            return addProperty(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva, "750", "IncomeFinancialFixedAssets");
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva() {
            return addProperty(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva, "751", "IncomeCurrentAssets");
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten() {
            return addProperty(PropertyName.RRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten, "752/9", "OtherFinancialIncome");
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenNietRecurrent() {
            return addProperty(PropertyName.RRFinancieleOpbrengstenNietRecurrent, "76B", "NonRecurringFinancialIncome");
        }

        @Override
        public IDocumentBuilder addRRFinancieleKosten() {
            return addProperty(PropertyName.RRFinancieleKosten, "65/66B", "FinancialChargesNonRecurringFinancialChargesIncluded", "FinancialCharges");
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrent() {
            return addProperty(PropertyName.RRFinancieleKostenRecurrent, "65", "FinancialCharges");
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentKostenSchulden() {
            return addProperty(PropertyName.RRFinancieleKostenRecurrentKostenSchulden, "650", "DebtCharges");
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen() {
            return addProperty(PropertyName.RRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen, "651", "ProvisionsRisksChargesAppropriationsWriteBacks");
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentAndereFinancieleKosten() {
            return addProperty(PropertyName.RRFinancieleKostenRecurrentAndereFinancieleKosten, "652/9", "OtherFinancialCharges");
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenNietRecurrent() {
            return addProperty(PropertyName.RRFinancieleKostenNietRecurrent, "66B", "NonRecurringFinancialCharges");
        }

        @Override
        public IDocumentBuilder addRRWinstVerliesBoekjaarVoorBelastingen() {
            return addProperty(PropertyName.RRWinstVerliesBoekjaarVoorBelastingen, "9903", "GainLossBeforeTaxes");
        }

        @Override
        public IDocumentBuilder addRROntrekkingenUitgesteldeBelastingen() {
            return addProperty(PropertyName.RROntrekkingenUitgesteldeBelastingen, "780", "TransferFromDeferredTaxes");
        }

        @Override
        public IDocumentBuilder addRROverboekingUitgesteldeBelastingen() {
            return addProperty(PropertyName.RROverboekingUitgesteldeBelastingen, "680", "TransferToDeferredTaxes");
        }

        @Override
        public IDocumentBuilder addRRBelastingenOpResultaat() {
            return addProperty(PropertyName.RRBelastingenOpResultaat, "67/77", "IncomeTaxes");
        }

        @Override
        public IDocumentBuilder addRRBelastingenOpResultaatBelastingen() {
            return addProperty(PropertyName.RRBelastingenOpResultaatBelastingen, "670/3", "BelgianForeignIncomeTaxes");
        }

        @Override
        public IDocumentBuilder addRRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen() {
            return addProperty(PropertyName.RRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen, "77", "AdjustmentIncomeTaxesWriteBackTaxProvisions");
        }

        @Override
        public IDocumentBuilder addRRWinstVerliesBoekjaar() {
            return addProperty(PropertyName.RRWinstVerliesBoekjaar, "9904", "GainLossPeriod");
        }

        @Override
        public IDocumentBuilder addRROntrekkingBelastingvrijeReserves() {
            return addProperty(PropertyName.RROntrekkingBelastingvrijeReserves, "789", "TransferFromUntaxedReserves");
        }

        @Override
        public IDocumentBuilder addRROverboekingBelastingvrijeReserves() {
            return addProperty(PropertyName.RROverboekingBelastingvrijeReserves, "689", "TransferToUntaxedReserves");
        }

        @Override
        public IDocumentBuilder addRRTeBestemmenWinstVerliesBoekjaar() {
            return addProperty(PropertyName.RRTeBestemmenWinstVerliesBoekjaar, "9905", "GainLossToBeAppropriated");
        }

        @Override
        public IDocumentBuilder addTLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen, "8022",
                    "ConcessionsPatentsLicencesSimilarRightsAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLIMVAMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen, "8029", "IntangibleFixedAssetsAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLMVAMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLMVAMutatiesTijdensBoekjaarAanschaffingen, "8169", "TangibleFixedAssetsAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLFVAMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLFVAMutatiesTijdensBoekjaarAanschaffingen, "8365", "FinancialFixedAssetsAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen, "8161", "LandBuildingsAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen, "8162", "PlantMachineryEquipmentAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen, "8163", "FurnitureVehiclesAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen, "8165", "OtherTangibleFixedAssetsAcquisitionIncludingProducedFixedAssets");
        }

        @Override
        public IDocumentBuilder addTLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen, "8362", "ParticipatingInterestsSharesEnterprisesLinkedParticipatingInterestAcquisitions");
        }

        @Override
        public IDocumentBuilder addTLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen() {
            return addProperty(PropertyName.TLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen, "8363", "OtherParticipatingInterestsSharesAcquisitions");
        }

        @Override
        public IDocumentBuilder addSBGemiddeldeFTE() {
            return addProperty(PropertyName.SBGemiddeldeFTE, "9087", "AverageNumberEmployeesPersonnelRegisterTotalFullTimeEquivalents");
        }

        @Override
        public IDocumentBuilder addSBGepresteerdeUren() {
            return addProperty(PropertyName.SBGepresteerdeUren, "9088", "NumberHoursActuallyWorkedTotal");
        }

        @Override
        public IDocumentBuilder addSBGemiddeldAantalFTEUitzendkrachten() {
            return addProperty(PropertyName.SBGemiddeldAantalFTEUitzendkrachten, "9097", "HiredTemporaryStaffAverageNumberPersonsEmployed");
        }

        @Override
        public IDocumentBuilder addSBPersoneelskosten() {
            return addProperty(PropertyName.SBPersoneelskosten, "1023", "PersonnelCostsTotal");
        }

        @Override
        public IDocumentBuilder addSBGepresteerdeUrenUitzendkrachten() {
            return addProperty(PropertyName.SBGepresteerdeUrenUitzendkrachten, "9098", "HiredTemporaryStaffNumbersHoursActuallyWorked");
        }

        @Override
        public IDocumentBuilder addSBPersoneelskostenUitzendkrachten() {
            return addProperty(PropertyName.SBPersoneelskostenUitzendkrachten, "617", "HiredTemporaryStaffCostsEnterprise");
        }

        @Override
        public IDocumentBuilder addSBAantalWerknemersOpEindeBoekjaar() {
            return addProperty(PropertyName.SBAantalWerknemersOpEindeBoekjaar, "1053",
                    "NumberEmployeesPersonnelRegisterClosingDateFinancialYearTotalFullTimeEquivalents");
        }

        @Override
        public IDocumentBuilder addSBAantalBediendenOpEindeBoekjaar() {
            return addProperty(PropertyName.SBAantalBediendenOpEindeBoekjaar, "1343",
                    "NumberEmployeesPersonnelRegisterClosingDateFinancialYearEmployeesTotalFullTimeEquivalents");
        }

        @Override
        public IDocumentBuilder addSBAantalArbeidersOpEindeBoekjaar() {
            return addProperty(PropertyName.SBAantalArbeidersOpEindeBoekjaar, "1323",
                    "NumberEmployeesPersonnelRegisterClosingDateFinancialYearWorkersTotalFullTimeEquivalents");
        }

        @Override
        public IDocumentBuilder addBVBABrutomarge() {
            return addProperty(PropertyName.BVBABrutomarge, "9900", "GrossOperatingMargin");
        }
    }
}
