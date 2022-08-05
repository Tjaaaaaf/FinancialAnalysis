package Models;

import Models.Enums.FileExtension;
import Models.Enums.PropertyName;
import Models.Interfaces.IDocumentBuilder;
import Models.Interfaces.IDocumentWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Util.XmlUtil.asList;

public class DocumentWrapper implements IDocumentWrapper {

    private final String name;
    private final SimpleStringProperty nameProperty;
    private final int year;
    private final Business business;
    private final SimpleBooleanProperty selectedProperty;
    private final Map<PropertyName, String> properties;

    private DocumentWrapper(DocumentBuilder documentBuilder) {
        this.name = documentBuilder.name;
        this.year = documentBuilder.year;
        this.business = documentBuilder.business;
        this.properties = documentBuilder.properties;
        this.nameProperty = documentBuilder.nameProperty;
        this.selectedProperty = documentBuilder.selectedProperty;
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
    public SimpleStringProperty getNameProperty() {
        return nameProperty;
    }

    @Override
    public SimpleBooleanProperty getSelectedProperty() {
        return selectedProperty;
    }

    @Override
    public Map<PropertyName, String> getPropertiesMap() {
        return properties;
    }

    public static class DocumentBuilder implements IDocumentBuilder {

        private final String name;
        private final SimpleStringProperty nameProperty;
        private final int year;
        private Business business;
        private final Map<PropertyName, String> properties;
        private SimpleBooleanProperty selectedProperty;
        private Document document = null;
        private Map<String, String> csvValues = null;
        private ArrayList<String> currentTimePeriods;
        private final FileExtension fileExtension;

        public DocumentBuilder(Document document, String fileName, int year, FileExtension fileExtension) {
            this.document = document;
            this.name = fileName;
            this.year = year;
            this.nameProperty = new SimpleStringProperty(fileName);
            this.selectedProperty = new SimpleBooleanProperty(true);
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
            this.nameProperty = new SimpleStringProperty(fileName);
            this.selectedProperty = new SimpleBooleanProperty(true);
            this.properties = new HashMap<>();
            for (PropertyName propname : PropertyName.values()) {
                properties.put(propname, "0");
            }
            this.fileExtension = fileExtension;
        }

        public boolean extractBusiness() {
            try {
                switch (fileExtension) {
                    case XBRL:
                        this.business = new Business(
                                this.document.getElementsByTagName("pfs-gcd:EntityCurrentLegalName").item(0).getTextContent(),
                                this.document.getElementsByTagName("pfs-gcd:IdentifierValue").item(0).getTextContent(),
                                this.document.getElementsByTagName("pfs-gcd:Street").item(0).getTextContent(),
                                this.document.getElementsByTagName("pfs-gcd:Number").item(0).getTextContent(),
                                this.document.getElementsByTagName("pfs-gcd:Box").item(0) == null ? ""
                                        : this.document.getElementsByTagName("pfs-gcd:Box").item(0).getTextContent(),
                                this.document.getElementsByTagName("pfs-gcd:PostalCodeCity").item(0).getChildNodes().item(0) == null ? ""
                                        : this.document.getElementsByTagName("pfs-gcd:PostalCodeCity").item(0).getTextContent(),
                                this.document.getElementsByTagName("pfs-gcd:CountryCode").item(0).getChildNodes().item(0) == null ? ""
                                        : this.document.getElementsByTagName("pfs-gcd:CountryCode").item(0).getTextContent());
                        return true;
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
                        return true;
                    default:
                        return false;
                }
            } catch (NullPointerException ex) {
                return false;
            }
        }

        public boolean extractCurrentTimePeriods() {
            try {
                ArrayList<String> currentTimePeriods = new ArrayList<>();

                int index = 0;
                boolean currentYearFound = false, currentDurationFound = false;
                while (index < this.document.getElementsByTagName("xbrli:context").getLength()) {
                    Node e = this.document.getElementsByTagName("xbrli:context").item(index);
                    if (e.getChildNodes().item(3).getChildNodes().getLength() == 3 && !currentYearFound) {
                        currentTimePeriods.add(e.getAttributes().getNamedItem("id").getTextContent());
                        currentYearFound = true;
                    }

                    if (e.getChildNodes().item(3).getChildNodes().getLength() == 5 && !currentDurationFound) {
                        currentTimePeriods.add(e.getAttributes().getNamedItem("id").getTextContent());
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

        public void setSelectedProperty(SimpleBooleanProperty selectedProperty) {
            this.selectedProperty = selectedProperty;
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
        public SimpleStringProperty getNameProperty() {
            return nameProperty;
        }

        @Override
        public SimpleBooleanProperty getSelectedProperty() {
            return selectedProperty;
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

        @Override
        public IDocumentBuilder addBAOprichtingskosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAOprichtingskosten, getStringFromCSVValues("20"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAOprichtingskosten, getStringFromXBRL("FormationExpenses"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVasteActiva, getStringFromCSVValues("21/28"));
                    break;
                case XBRL:
                    String vasteActiva = getStringFromXBRL("FixedAssetsFormationExpensesExcluded");
                    if (vasteActiva.equals("0")) {
                        vasteActiva = getStringFromXBRL("FixedAssets");
                    }
                    properties.replace(PropertyName.BAVasteActiva, vasteActiva);
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAImmaterieleVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAImmaterieleVasteActiva, getStringFromCSVValues("21"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAImmaterieleVasteActiva, getStringFromXBRL("IntangibleFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAMaterieleVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAMaterieleVasteActiva, getStringFromCSVValues("22/27"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAMaterieleVasteActiva, getStringFromXBRL("TangibleFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBATerreinenGebouwen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BATerreinenGebouwen, getStringFromCSVValues("22"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BATerreinenGebouwen, getStringFromXBRL("LandBuildings"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAInstallatiesMachinesUitrusting() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAInstallatiesMachinesUitrusting, getStringFromCSVValues("23"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAInstallatiesMachinesUitrusting,
                            getStringFromXBRL("PlantMachineryEquipment"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAMeubilairRollendMaterieel() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAMeubilairRollendMaterieel, getStringFromCSVValues("24"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAMeubilairRollendMaterieel, getStringFromXBRL("FurnitureVehicles"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBALeasingSoortgelijkeRechten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BALeasingSoortgelijkeRechten, getStringFromCSVValues("25"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BALeasingSoortgelijkeRechten, getStringFromXBRL("LeasingSimilarRights"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAOverigeMaterieleVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAOverigeMaterieleVasteActiva, getStringFromCSVValues("26"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAOverigeMaterieleVasteActiva, getStringFromXBRL("OtherTangibleAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAActivaAanbouwVooruitbetalingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAActivaAanbouwVooruitbetalingen, getStringFromCSVValues("27"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAActivaAanbouwVooruitbetalingen,
                            getStringFromXBRL("AssetsUnderConstructionAdvancePayments"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAFinancieleVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAFinancieleVasteActiva, getStringFromCSVValues("28"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAFinancieleVasteActiva, getStringFromXBRL("FinancialFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVerbondenOndernemingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVerbondenOndernemingen, getStringFromCSVValues("280/1"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVerbondenOndernemingen,
                            getStringFromXBRL("ParticipatingInterestsAffiliatedEnterprises"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVerbondenOndernemingenDeelnemingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVerbondenOndernemingenDeelnemingen, getStringFromCSVValues("280"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVerbondenOndernemingenDeelnemingen,
                            getStringFromXBRL("ParticipatingInterestsAmountsReceivableAffiliatedEnterprises"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVerbondenOndernemingenVorderingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVerbondenOndernemingenVorderingen, getStringFromCSVValues("281"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVerbondenOndernemingenVorderingen,
                            getStringFromXBRL("OtherAmountsReceivableAffiliatedEnterprises"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhouding() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAOndernemingenDeelnemingsverhouding, getStringFromCSVValues("282/3"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAOndernemingenDeelnemingsverhouding, getStringFromXBRL(
                            "ParticipatingInterestsAmountsReceivableOtherEnterprisesLinkedParticipatingInterestsAssociatedEnterprisesExcluded"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingDeelnemingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAOndernemingenDeelnemingsverhoudingDeelnemingen, getStringFromCSVValues("282"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAOndernemingenDeelnemingsverhoudingDeelnemingen, getStringFromXBRL(
                            "ParticipatingInterestsOtherEnterprisesLinkedParticipatingInterestsAssociatedEnterprisesExcluded"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingVorderingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAOndernemingenDeelnemingsverhoudingVorderingen, getStringFromCSVValues("283"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAOndernemingenDeelnemingsverhoudingVorderingen,
                            getStringFromXBRL("SubordinatedAmountsReceivableEnterprisesLinkedByParticipatingInterests"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAAndereFinancieleVasteActiva, getStringFromCSVValues("284/8"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAAndereFinancieleVasteActiva, getStringFromXBRL("OtherFinancialAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActivaAandelen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAAndereFinancieleVasteActivaAandelen, getStringFromCSVValues("284"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAAndereFinancieleVasteActivaAandelen,
                            getStringFromXBRL("OtherFinancialAssetsParticipatingInterestsShares"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten, getStringFromCSVValues("285/8"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten,
                            getStringFromXBRL("OtherFinancialAssetsAmountsReceivableCashGuarantees"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVlottendeActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVlottendeActiva, getStringFromCSVValues("29/58"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVlottendeActiva, getStringFromXBRL("CurrentsAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVorderingenMeer1Jaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVorderingenMeer1Jaar, getStringFromCSVValues("29"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVorderingenMeer1Jaar, getStringFromXBRL("AmountsReceivableMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVorderingenMeer1JaarHandelsvorderingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVorderingenMeer1JaarHandelsvorderingen, getStringFromCSVValues("290"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVorderingenMeer1JaarHandelsvorderingen,
                            getStringFromXBRL("TradeDebtorsMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVorderingenMeer1JaarOverigeVorderingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVorderingenMeer1JaarOverigeVorderingen, getStringFromCSVValues("291"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVorderingenMeer1JaarOverigeVorderingen,
                            getStringFromXBRL("OtherAmountsReceivableMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoering() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoering, getStringFromCSVValues("3"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoering,
                            getStringFromXBRL("StocksContractsProgress"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorraden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorraden, getStringFromCSVValues("30/36"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorraden, getStringFromXBRL("Stocks"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen, getStringFromCSVValues("30/31"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen,
                            getStringFromXBRL("StockRawMaterialsConsumables"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking, getStringFromCSVValues("32"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking,
                            getStringFromXBRL("StockWorkProgress"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGereedProduct() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGereedProduct, getStringFromCSVValues("33"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGereedProduct,
                            getStringFromXBRL("StockFinishedGoods"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen, getStringFromCSVValues("34"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen,
                            getStringFromXBRL("StockGoodsPurchasedResale"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop, getStringFromCSVValues("35"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop,
                            getStringFromXBRL("StockImmovablePropertyIntendedSale"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen, getStringFromCSVValues("36"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen,
                            getStringFromXBRL("AdvancePaymentsPurchasesStocks"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringBestellingenUitvoer() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringBestellingenUitvoer, getStringFromCSVValues("37"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringBestellingenUitvoer,
                            getStringFromXBRL("ContractsProgress"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1Jaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVorderingenHoogstens1Jaar, getStringFromCSVValues("40/41"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVorderingenHoogstens1Jaar,
                            getStringFromXBRL("AmountsReceivableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1JaarHandelsvorderingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVorderingenHoogstens1JaarHandelsvorderingen, getStringFromCSVValues("40"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVorderingenHoogstens1JaarHandelsvorderingen,
                            getStringFromXBRL("TradeDebtorsWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1JaarOverigeVorderingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAVorderingenHoogstens1JaarOverigeVorderingen, getStringFromCSVValues("41"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAVorderingenHoogstens1JaarOverigeVorderingen,
                            getStringFromXBRL("OtherAmountsReceivableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAGeldBeleggingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAGeldBeleggingen, getStringFromCSVValues("50/53"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAGeldBeleggingen, getStringFromXBRL("CurrentInvestments"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAGeldBeleggingenEigenAandelen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAGeldBeleggingenEigenAandelen, getStringFromCSVValues("50"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAGeldBeleggingenEigenAandelen, getStringFromXBRL("OwnShares"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAGeldBeleggingenOverigeBeleggingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAGeldBeleggingenOverigeBeleggingen, getStringFromCSVValues("51/53"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAGeldBeleggingenOverigeBeleggingen,
                            getStringFromXBRL("OtherCurrentInvestments"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBALiquideMiddelen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BALiquideMiddelen, getStringFromCSVValues("54/58"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BALiquideMiddelen, getStringFromXBRL("CashBankHand"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBAOverlopendeRekeningen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BAOverlopendeRekeningen, getStringFromCSVValues("490/1"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BAOverlopendeRekeningen, getStringFromXBRL("DeferredChargesAccruedIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBATotaalActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BATotaalActiva, getStringFromCSVValues("20/58"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BATotaalActiva, getStringFromXBRL("Assets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPEigenVermogen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPEigenVermogen, getStringFromCSVValues("10/15"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPEigenVermogen, getStringFromXBRL("Equity"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPKapitaal() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPKapitaal, getStringFromCSVValues("10"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPKapitaal, getStringFromXBRL("Capital"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPKapitaalGeplaatst() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPKapitaalGeplaatst, getStringFromCSVValues("100"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPKapitaalGeplaatst, getStringFromXBRL("IssuedCapital"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPKapitaalNietOpgevraagd() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPKapitaalNietOpgevraagd, getStringFromCSVValues("101"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPKapitaalNietOpgevraagd, getStringFromXBRL("UncalledCapital"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPUitgiftepremies() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPUitgiftepremies, getStringFromCSVValues("1100/10"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPUitgiftepremies, getStringFromXBRL("SharePremiumAccount"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPHerwaarderingsmeerwaarden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPHerwaarderingsmeerwaarden, getStringFromCSVValues("12"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPHerwaarderingsmeerwaarden, getStringFromXBRL("RevaluationSurpluses"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPReserves() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPReserves, getStringFromCSVValues("13"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPReserves, getStringFromXBRL("Reserves"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPReservesWettelijkeReserve() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPReservesWettelijkeReserve, getStringFromCSVValues("130"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPReservesWettelijkeReserve,
                            getStringFromXBRL("DifferentCategoriesSharesValue"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReserves() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPReservesOnbeschikbareReserves, getStringFromCSVValues("130/1"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPReservesOnbeschikbareReserves, getStringFromXBRL("ReservesNotAvailable"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReservesEigenAandelen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPReservesOnbeschikbareReservesEigenAandelen, getStringFromCSVValues("1312"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPReservesOnbeschikbareReservesEigenAandelen,
                            getStringFromXBRL("ReservesNotAvailableOwnSharesHeld"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReservesAndere() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPReservesOnbeschikbareReservesAndere, getStringFromCSVValues("1319"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPReservesOnbeschikbareReservesAndere,
                            getStringFromXBRL("OtherReservesNotAvailable"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPReservesBelastingvrijeReserves() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPReservesBelastingvrijeReserves, getStringFromCSVValues("132"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPReservesBelastingvrijeReserves, getStringFromXBRL("UntaxedReserves"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPReservesBeschikbareReserves() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPReservesBeschikbareReserves, getStringFromCSVValues("133"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPReservesBeschikbareReserves, getStringFromXBRL("AvailableReserves"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPOvergedragenWinstVerlies() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPOvergedragenWinstVerlies, getStringFromCSVValues("14"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPOvergedragenWinstVerlies, getStringFromXBRL("AccumulatedProfitsLosses"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPKapitaalSubsidies() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPKapitaalSubsidies, getStringFromCSVValues("15"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPKapitaalSubsidies, getStringFromXBRL("InvestmentGrants"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorschotVennotenVerdelingNettoActief() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorschotVennotenVerdelingNettoActief, getStringFromCSVValues("19"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorschotVennotenVerdelingNettoActief,
                            getStringFromXBRL("AdvanceAssociatesSharingOutAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenUitgesteldeBelastingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorzieningenUitgesteldeBelastingen, getStringFromCSVValues("16"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorzieningenUitgesteldeBelastingen,
                            getStringFromXBRL("ProvisionsDeferredTaxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKosten, getStringFromCSVValues("160/5"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKosten,
                            getStringFromXBRL("ProvisionLiabilitiesCharges"));

            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen, getStringFromCSVValues("160"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen,
                            getStringFromXBRL("ProvisionsPensionsSimilarObligations"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenFiscaleLasten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenFiscaleLasten, getStringFromCSVValues("161"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenFiscaleLasten,
                            getStringFromXBRL("ProvisionsTaxation"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken, getStringFromCSVValues("162"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken,
                            getStringFromXBRL("ProvisionsMajorRepairsMaintenance"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenMilieuverplichtingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenMilieuverplichtingen, getStringFromCSVValues("163"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenMilieuverplichtingen,
                            getStringFromXBRL("ProvisionsOtherLiabilitiesCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenOverige() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenOverige, getStringFromCSVValues("164/5"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPVoorzieningenRisicosKostenOverige, getStringFromXBRL("DeferredTaxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchulden, getStringFromCSVValues("17/49"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchulden, getStringFromXBRL("AmountsPayable"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1Jaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1Jaar, getStringFromCSVValues("17"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1Jaar, getStringFromXBRL("AmountsPayableMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchulden, getStringFromCSVValues("170/4"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchulden,
                            getStringFromXBRL("FinancialDebtsRemainingTermMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen, getStringFromCSVValues("170"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen,
                            getStringFromXBRL("SubordinatedLoansRemainingTermMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen, getStringFromCSVValues("171"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen,
                            getStringFromXBRL("UnsubordinatedDebenturesRemainingTermMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden, getStringFromCSVValues("172"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden,
                            getStringFromXBRL("LeasingSimilarObligationsRemainingTermMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen, getStringFromCSVValues("173"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen,
                            getStringFromXBRL("AmountsPayableMoreOneYearCreditInstitutions"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen, getStringFromCSVValues("174"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen,
                            getStringFromXBRL("OtherLoansRemainingTermMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarHandelsschulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarHandelsschulden, getStringFromCSVValues("175"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarHandelsschulden,
                            getStringFromXBRL("TradeDebtsPayableMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels, getStringFromCSVValues("1751"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels,
                            getStringFromXBRL("BillExchangeMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen, getStringFromCSVValues("176"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen,
                            getStringFromXBRL("AdvancesReceivedContractsProgressWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarOverigeSchulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarOverigeSchulden, getStringFromCSVValues("178/9"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenMeer1JaarOverigeSchulden,
                            getStringFromXBRL("OtherAmountsPayableMoreOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1Jaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1Jaar, getStringFromCSVValues("42/48"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1Jaar, getStringFromXBRL("AmountsPayableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen, getStringFromCSVValues("42"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen,
                            getStringFromXBRL("CurrentPortionAmountsPayableMoreOneYearFallingDueWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden, getStringFromCSVValues("43"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden,
                            getStringFromXBRL("FinancialDebtsPayableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen, getStringFromCSVValues("430/8"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen,
                            getStringFromXBRL("AmountsPayableWithinOneYearCreditInstitutions"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen, getStringFromCSVValues("439"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen,
                            getStringFromXBRL("OtherLoansPayableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschulden, getStringFromCSVValues("44"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschulden,
                            getStringFromXBRL("TradeDebtsPayableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenLeveranciers() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenLeveranciers, getStringFromCSVValues("440/4"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenLeveranciers,
                            getStringFromXBRL("SuppliersInvoicesToReceiveWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels, getStringFromCSVValues("441"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels,
                            getStringFromXBRL("BillExchangePayableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen, getStringFromCSVValues("46"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen,
                            getStringFromXBRL("AdvancesReceivedContractsProgressWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten, getStringFromCSVValues("45"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten,
                            getStringFromXBRL("TaxesRemunerationSocialSecurity"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen, getStringFromCSVValues("450/3"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen,
                            getStringFromXBRL("Taxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten, getStringFromCSVValues("454/9"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten,
                            getStringFromXBRL("RemunerationSocialSecurity"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarOverigeSchulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarOverigeSchulden, getStringFromCSVValues("47/48"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPSchuldenHoogstens1JaarOverigeSchulden,
                            getStringFromXBRL("OtherAmountsPayableWithinOneYear"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPOverlopendeRekeningen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPOverlopendeRekeningen, getStringFromCSVValues("492/3"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPOverlopendeRekeningen, getStringFromXBRL("AccruedChargesDeferredIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBPTotaalPassiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BPTotaalPassiva, getStringFromCSVValues("10/49"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BPTotaalPassiva, getStringFromXBRL("EquityLiabilities"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengsten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsopbrengsten, getStringFromCSVValues("70/76A"));
                    break;
                case XBRL:
                    String bedrijfsOpbrengsten = getStringFromXBRL("OperatingIncomeNonRecurringOperatingIncomeIncluded");
                    if (bedrijfsOpbrengsten.equals("0")) {
                        bedrijfsOpbrengsten = getStringFromXBRL("OperatingIncome");
                    }
                    properties.replace(PropertyName.RRBedrijfsopbrengsten, bedrijfsOpbrengsten);
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenOmzet() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenOmzet, getStringFromCSVValues("70"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenOmzet, getStringFromXBRL("Turnover"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering, getStringFromCSVValues("71"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.RRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering,
                            getStringFromXBRL("IncreaseDecreaseStocksWorkContractsProgress"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenGeproduceerdeVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenGeproduceerdeVasteActiva, getStringFromCSVValues("72"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenGeproduceerdeVasteActiva,
                            getStringFromXBRL("OwnConstructionCapitalised"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenAndereBedrijfsopbrengsten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenAndereBedrijfsopbrengsten, getStringFromCSVValues("74"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenAndereBedrijfsopbrengsten,
                            getStringFromXBRL("OtherOperatingIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten, getStringFromCSVValues("76A"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten,
                            getStringFromXBRL("NonRecurringOperatingIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskosten, getStringFromCSVValues("60/66A"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskosten,
                            getStringFromXBRL("OperatingChargesNonRecurringOperatingChargesIncluded"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffen, getStringFromCSVValues("60"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffen,
                            getStringFromXBRL("RawMaterialsConsumables"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen, getStringFromCSVValues("600/8"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen,
                            getStringFromXBRL("PurchasesRawMaterialsConsumables"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename, getStringFromCSVValues("609"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename,
                            getStringFromXBRL("IncreaseDecreaseStocks"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenDienstenDiverseGoederen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenDienstenDiverseGoederen, getStringFromCSVValues("61"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenDienstenDiverseGoederen,
                            getStringFromXBRL("ServicesOtherGoods"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenBezoldigingenSocialeLastenPensioenen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenBezoldigingenSocialeLastenPensioenen, getStringFromCSVValues("62"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenBezoldigingenSocialeLastenPensioenen,
                            getStringFromXBRL("RemunerationSocialSecurityPensions"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva, getStringFromCSVValues("630"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.RRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva,
                            getStringFromXBRL(
                                    "DepreciationOtherAmountsWrittenDownFormationExpensesIntangibleTangibleFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen, getStringFromCSVValues("631/4"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.RRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen,
                            getStringFromXBRL("AmountsWrittenDownStocksContractsProgressTradeDebtorsAppropriationsWriteBacks"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen, getStringFromCSVValues("635/8"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.RRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen,
                            getStringFromXBRL("ProvisionsRisksChargesAppropriationsWriteBacks"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenAndereBedrijfskosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenAndereBedrijfskosten, getStringFromCSVValues("640/8"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenAndereBedrijfskosten,
                            getStringFromXBRL("MiscellaneousOperatingCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenHerstructureringskostenGeactiveerdeBedrijfskosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenHerstructurerngskostenGeactiveerdeBedrijfskosten, getStringFromCSVValues("649"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenHerstructurerngskostenGeactiveerdeBedrijfskosten,
                            getStringFromXBRL("OperatingChargesCarriedAssetsRestructuringCosts"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenNietRecurrenteBedrijfskosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten, getStringFromCSVValues("66A"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten,
                            getStringFromXBRL("NonRecurringOperatingCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfskostenUitzonderlijkeKosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfskostenUitzonderlijkeKosten, getStringFromCSVValues("66"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfskostenUitzonderlijkeKosten,
                            getStringFromXBRL("ExtraordinaryCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenUitzonderlijkeOpbrengsten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten, getStringFromCSVValues("76"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten,
                            getStringFromXBRL("ExtraordinaryIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBedrijfsWinstVerlies() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBedrijfsWinstVerlies, getStringFromCSVValues("9901"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBedrijfsWinstVerlies, getStringFromXBRL("OperatingProfitLoss"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengsten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleOpbrengsten, getStringFromCSVValues("75/76B"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleOpbrengsten,
                            getStringFromXBRL("FinancialIncomeNonRecurringFinancialIncomeIncluded"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrent() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrent, getStringFromCSVValues("75"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrent, getStringFromXBRL("FinancialIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva, getStringFromCSVValues("750"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva,
                            getStringFromXBRL("IncomeFinancialFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva, getStringFromCSVValues("751"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva,
                            getStringFromXBRL("IncomeCurrentAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten, getStringFromCSVValues("752/9"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten,
                            getStringFromXBRL("OtherFinancialIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenNietRecurrent() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenNietRecurrent, getStringFromCSVValues("76B"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleOpbrengstenNietRecurrent,
                            getStringFromXBRL("NonRecurringFinancialIncome"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleKosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleKosten, getStringFromCSVValues("65/66B"));
                    break;
                case XBRL:
                    String financieleKosten = getStringFromXBRL("FinancialChargesNonRecurringFinancialChargesIncluded");
                    if (financieleKosten.equals("0")) {
                        financieleKosten = getStringFromXBRL("FinancialCharges");
                    }
                    properties.replace(PropertyName.RRFinancieleKosten, financieleKosten);
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrent() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleKostenRecurrent, getStringFromCSVValues("65"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleKostenRecurrent, getStringFromXBRL("FinancialCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentKostenSchulden() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleKostenRecurrentKostenSchulden, getStringFromCSVValues("650"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleKostenRecurrentKostenSchulden,
                            getStringFromXBRL("DebtCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen, getStringFromCSVValues("651"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.RRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen,
                            getStringFromXBRL("ProvisionsRisksChargesAppropriationsWriteBacks"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentAndereFinancieleKosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleKostenRecurrentAndereFinancieleKosten, getStringFromCSVValues("652/9"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleKostenRecurrentAndereFinancieleKosten,
                            getStringFromXBRL("OtherFinancialCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRFinancieleKostenNietRecurrent() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRFinancieleKostenNietRecurrent, getStringFromCSVValues("66B"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRFinancieleKostenNietRecurrent,
                            getStringFromXBRL("NonRecurringFinancialCharges"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRWinstVerliesBoekjaarVoorBelastingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRWinstVerliesBoekjaarVoorBelastingen, getStringFromCSVValues("9903"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRWinstVerliesBoekjaarVoorBelastingen,
                            getStringFromXBRL("GainLossBeforeTaxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRROntrekkingenUitgesteldeBelastingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RROntrekkingenUitgesteldeBelastingen, getStringFromCSVValues("780"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RROntrekkingenUitgesteldeBelastingen,
                            getStringFromXBRL("TransferFromDeferredTaxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRROverboekingUitgesteldeBelastingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RROverboekingUitgesteldeBelastingen, getStringFromCSVValues("680"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RROverboekingUitgesteldeBelastingen,
                            getStringFromXBRL("TransferToDeferredTaxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBelastingenOpResultaat() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBelastingenOpResultaat, getStringFromCSVValues("67/77"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBelastingenOpResultaat, getStringFromXBRL("IncomeTaxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBelastingenOpResultaatBelastingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBelastingenOpResultaatBelastingen, getStringFromCSVValues("670/3"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRBelastingenOpResultaatBelastingen,
                            getStringFromXBRL("BelgianForeignIncomeTaxes"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen, getStringFromCSVValues("77"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.RRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen,
                            getStringFromXBRL("AdjustmentIncomeTaxesWriteBackTaxProvisions"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRWinstVerliesBoekjaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRWinstVerliesBoekjaar, getStringFromCSVValues("9904"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRWinstVerliesBoekjaar, getStringFromXBRL("GainLossPeriod"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRROntrekkingBelastingvrijeReserves() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RROntrekkingBelastingvrijeReserves, getStringFromCSVValues("789"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RROntrekkingBelastingvrijeReserves,
                            getStringFromXBRL("TransferFromUntaxedReserves"));

            }
            return this;
        }

        @Override
        public IDocumentBuilder addRROverboekingBelastingvrijeReserves() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RROverboekingBelastingvrijeReserves, getStringFromCSVValues("689"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RROverboekingBelastingvrijeReserves,
                            getStringFromXBRL("TransferToUntaxedReserves"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addRRTeBestemmenWinstVerliesBoekjaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.RRTeBestemmenWinstVerliesBoekjaar, getStringFromCSVValues("9905"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.RRTeBestemmenWinstVerliesBoekjaar,
                            getStringFromXBRL("GainLossToBeAppropriated"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8022"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.TLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL(
                                    "ConcessionsPatentsLicencesSimilarRightsAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLIMVAMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8029"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("IntangibleFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLMVAMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLMVAMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8169"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLMVAMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("TangibleFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLFVAMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLFVAMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8365"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLFVAMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("FinancialFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8161"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("LandBuildingsAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8162"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("PlantMachineryEquipmentAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8163"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("FurnitureVehiclesAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8165"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("OtherTangibleFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8362"));
                    break;
                case XBRL:
                    properties.replace(
                            PropertyName.TLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL(
                                    "ParticipatingInterestsSharesEnterprisesLinkedParticipatingInterestAcquisitions"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addTLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.TLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen, getStringFromCSVValues("8363"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.TLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen,
                            getStringFromXBRL("OtherParticipatingInterestsSharesAcquisitions"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBGemiddeldeFTE() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBGemiddeldeFTE, getStringFromCSVValues("9087"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBGemiddeldeFTE,
                            getStringFromXBRL("AverageNumberEmployeesPersonnelRegisterTotalFullTimeEquivalents"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBGepresteerdeUren() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBGepresteerdeUren, getStringFromCSVValues("9088"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBGepresteerdeUren, getStringFromXBRL("NumberHoursActuallyWorkedTotal"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBGemiddeldAantalFTEUitzendkrachten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBGemiddeldAantalFTEUitzendkrachten, getStringFromCSVValues("9097"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBGemiddeldAantalFTEUitzendkrachten,
                            getStringFromXBRL("HiredTemporaryStaffAverageNumberPersonsEmployed"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBPersoneelskosten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBPersoneelskosten, getStringFromCSVValues("1023"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBPersoneelskosten, getStringFromXBRL("PersonnelCostsTotal"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBGepresteerdeUrenUitzendkrachten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBGepresteerdeUrenUitzendkrachten, getStringFromCSVValues("9098"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBGepresteerdeUrenUitzendkrachten,
                            getStringFromXBRL("HiredTemporaryStaffNumbersHoursActuallyWorked"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBPersoneelskostenUitzendkrachten() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBPersoneelskostenUitzendkrachten, getStringFromCSVValues("617"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBPersoneelskostenUitzendkrachten,
                            getStringFromXBRL("HiredTemporaryStaffCostsEnterprise"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBAantalWerknemersOpEindeBoekjaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBAantalWerknemersOpEindeBoekjaar, getStringFromCSVValues("1053"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBAantalWerknemersOpEindeBoekjaar, getStringFromXBRL(
                            "NumberEmployeesPersonnelRegisterClosingDateFinancialYearTotalFullTimeEquivalents"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBAantalBediendenOpEindeBoekjaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBAantalBediendenOpEindeBoekjaar, getStringFromCSVValues("1343"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBAantalBediendenOpEindeBoekjaar, getStringFromXBRL(
                            "NumberEmployeesPersonnelRegisterClosingDateFinancialYearEmployeesTotalFullTimeEquivalents"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addSBAantalArbeidersOpEindeBoekjaar() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.SBAantalArbeidersOpEindeBoekjaar, getStringFromCSVValues("1323"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.SBAantalArbeidersOpEindeBoekjaar, getStringFromXBRL(
                            "NumberEmployeesPersonnelRegisterClosingDateFinancialYearWorkersTotalFullTimeEquivalents"));
            }
            return this;
        }

        @Override
        public IDocumentBuilder addBVBABrutomarge() {
            switch (fileExtension) {
                case CSV:
                    properties.replace(PropertyName.BVBABrutomarge, getStringFromCSVValues("9900"));
                    break;
                case XBRL:
                    properties.replace(PropertyName.BVBABrutomarge, getStringFromXBRL("GrossOperatingMargin"));
            }
            return this;
        }
    }
}
