package Domain;

import Enums.PropertyName;
import Interfaces.IDocumentBuilder;
import Interfaces.IDocumentWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Utils.XmlUtil.asList;

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
        
        private String name;
        private final SimpleStringProperty nameProperty;
        private int year;
        private Business business;
        private Map<PropertyName, String> properties;
        private SimpleBooleanProperty selectedProperty;
        private final Document document;
        
        public DocumentBuilder(Document document, String fileName, int year) {
            this.document = document;
            this.name = fileName;
            this.year = year;
            this.nameProperty = new SimpleStringProperty(fileName);
            this.selectedProperty = new SimpleBooleanProperty(true);
            this.properties = new HashMap<>();
            for (PropertyName propname : PropertyName.values()) {
                properties.put(propname, "0");
            }
            this.business = extractBusiness(document);
        }

        private Business extractBusiness(Document document) {
            return new Business(document.getElementsByTagName("pfs-gcd:EntityCurrentLegalName").item(0).getTextContent(),
                    document.getElementsByTagName("pfs-gcd:IdentifierValue").item(0).getTextContent(),
                    document.getElementsByTagName("pfs-gcd:Street").item(0).getTextContent(),
                    document.getElementsByTagName("pfs-gcd:Number").item(0).getTextContent(),
                    document.getElementsByTagName("pfs-gcd:Box").item(0) == null ? "" : document.getElementsByTagName("pfs-gcd:Box").item(0).getTextContent(),
                    document.getElementsByTagName("pfs-gcd:PostalCodeCity").item(0).getChildNodes().item(0) == null ? "" : document.getElementsByTagName("pfs-gcd:PostalCodeCity").item(0).getTextContent(),
                    document.getElementsByTagName("pfs-gcd:CountryCode").item(0).getChildNodes().item(0) == null ? "" : document.getElementsByTagName("pfs-gcd:CountryCode").item(0).getTextContent());
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
                if (currentRef.equals("CurrentDuration") || currentRef.equals("CurrentInstant") || currentRef.equals("c12") || currentRef.equals("c10")) {
                    return node.getTextContent();
                }
            }
            return "0";
        }
        
        @Override
        public DocumentWrapper build() {
            return new DocumentWrapper(this);
        }
        
        @Override
        public IDocumentBuilder removeProperty(PropertyName propertyName) {
            if (properties.keySet().contains(propertyName)) {
                properties.remove(propertyName);
            }
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAOprichtingskosten() {
            properties.replace(PropertyName.BAOprichtingskosten, getStringFromXBRL("FormationExpenses"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVasteActiva() {
            String vasteActiva = getStringFromXBRL("FixedAssetsFormationExpensesExcluded");
            if (vasteActiva.equals("0")) {
                vasteActiva = getStringFromXBRL("FixedAssets");
            }
            properties.replace(PropertyName.BAVasteActiva, vasteActiva);
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAImmaterieleVasteActiva() {
            properties.replace(PropertyName.BAImmaterieleVasteActiva, getStringFromXBRL("IntangibleFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAMaterieleVasteActiva() {
            properties.replace(PropertyName.BAMaterieleVasteActiva, getStringFromXBRL("TangibleFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBATerreinenGebouwen() {
            properties.replace(PropertyName.BATerreinenGebouwen, getStringFromXBRL("LandBuildings"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAInstallatiesMachinesUitrusting() {
            properties.replace(PropertyName.BAInstallatiesMachinesUitrusting, getStringFromXBRL("PlantMachineryEquipment"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAMeubilairRollendMaterieel() {
            properties.replace(PropertyName.BAMeubilairRollendMaterieel, getStringFromXBRL("FurnitureVehicles"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBALeasingSoortgelijkeRechten() {
            properties.replace(PropertyName.BALeasingSoortgelijkeRechten, getStringFromXBRL("LeasingSimilarRights"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAOverigeMaterieleVasteActiva() {
            properties.replace(PropertyName.BAOverigeMaterieleVasteActiva, getStringFromXBRL("OtherTangibleAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAActivaAanbouwVooruitbetalingen() {
            properties.replace(PropertyName.BAActivaAanbouwVooruitbetalingen, getStringFromXBRL("AssetsUnderConstructionAdvancePayments"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAFinancieleVasteActiva() {
            properties.replace(PropertyName.BAFinancieleVasteActiva, getStringFromXBRL("FinancialFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVerbondenOndernemingen() {
            properties.replace(PropertyName.BAVerbondenOndernemingen, getStringFromXBRL("ParticipatingInterestsAffiliatedEnterprises"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVerbondenOndernemingenDeelnemingen() {
            properties.replace(PropertyName.BAVerbondenOndernemingenDeelnemingen, getStringFromXBRL("ParticipatingInterestsAmountsReceivableAffiliatedEnterprises"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVerbondenOndernemingenVorderingen() {
            properties.replace(PropertyName.BAVerbondenOndernemingenVorderingen, getStringFromXBRL("OtherAmountsReceivableAffiliatedEnterprises"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhouding() {
            properties.replace(PropertyName.BAOndernemingenDeelnemingsverhouding, getStringFromXBRL("ParticipatingInterestsAmountsReceivableOtherEnterprisesLinkedParticipatingInterestsAssociatedEnterprisesExcluded"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingDeelnemingen() {
            properties.replace(PropertyName.BAOndernemingenDeelnemingsverhoudingDeelnemingen, getStringFromXBRL("ParticipatingInterestsOtherEnterprisesLinkedParticipatingInterestsAssociatedEnterprisesExcluded"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingVorderingen() {
            properties.replace(PropertyName.BAOndernemingenDeelnemingsverhoudingVorderingen, getStringFromXBRL("SubordinatedAmountsReceivableEnterprisesLinkedByParticipatingInterests"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActiva() {
            properties.replace(PropertyName.BAAndereFinancieleVasteActiva, getStringFromXBRL("OtherFinancialAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActivaAandelen() {
            properties.replace(PropertyName.BAAndereFinancieleVasteActivaAandelen, getStringFromXBRL("OtherFinancialAssetsParticipatingInterestsShares"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten() {
            properties.replace(PropertyName.BAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten, getStringFromXBRL("OtherFinancialAssetsAmountsReceivableCashGuarantees"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVlottendeActiva() {
            properties.replace(PropertyName.BAVlottendeActiva, getStringFromXBRL("CurrentsAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVorderingenMeer1Jaar() {
            properties.replace(PropertyName.BAVorderingenMeer1Jaar, getStringFromXBRL("AmountsReceivableMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVorderingenMeer1JaarHandelsvorderingen() {
            properties.replace(PropertyName.BAVorderingenMeer1JaarHandelsvorderingen, getStringFromXBRL("TradeDebtorsMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVorderingenMeer1JaarOverigeVorderingen() {
            properties.replace(PropertyName.BAVorderingenMeer1JaarOverigeVorderingen, getStringFromXBRL("OtherAmountsReceivableMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoering() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoering, getStringFromXBRL("StocksContractsProgress"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorraden() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorraden, getStringFromXBRL("Stocks"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen, getStringFromXBRL("StockRawMaterialsConsumables"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking, getStringFromXBRL("StockWorkProgress"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGereedProduct() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenGereedProduct, getStringFromXBRL("StockFinishedGoods"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen, getStringFromXBRL("StockGoodsPurchasedResale"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop, getStringFromXBRL("StockImmovablePropertyIntendedSale"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen, getStringFromXBRL("AdvancePaymentsPurchasesStocks"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVoorradenBestellingenUitvoeringBestellingenUitvoer() {
            properties.replace(PropertyName.BAVoorradenBestellingenUitvoeringBestellingenUitvoer, getStringFromXBRL("ContractsProgress"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1Jaar() {
            properties.replace(PropertyName.BAVorderingenHoogstens1Jaar, getStringFromXBRL("AmountsReceivableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1JaarHandelsvorderingen() {
            properties.replace(PropertyName.BAVorderingenHoogstens1JaarHandelsvorderingen, getStringFromXBRL("TradeDebtorsWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAVorderingenHoogstens1JaarOverigeVorderingen() {
            properties.replace(PropertyName.BAVorderingenHoogstens1JaarOverigeVorderingen, getStringFromXBRL("OtherAmountsReceivableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAGeldBeleggingen() {
            properties.replace(PropertyName.BAGeldBeleggingen, getStringFromXBRL("CurrentInvestments"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAGeldBeleggingenEigenAandelen() {
            properties.replace(PropertyName.BAGeldBeleggingenEigenAandelen, getStringFromXBRL("OwnShares"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAGeldBeleggingenOverigeBeleggingen() {
            properties.replace(PropertyName.BAGeldBeleggingenOverigeBeleggingen, getStringFromXBRL("OtherCurrentInvestments"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBALiquideMiddelen() {
            properties.replace(PropertyName.BALiquideMiddelen, getStringFromXBRL("CashBankHand"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBAOverlopendeRekeningen() {
            properties.replace(PropertyName.BAOverlopendeRekeningen, getStringFromXBRL("DeferredChargesAccruedIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBATotaalActiva() {
            properties.replace(PropertyName.BATotaalActiva, getStringFromXBRL("Assets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPEigenVermogen() {
            properties.replace(PropertyName.BPEigenVermogen, getStringFromXBRL("Equity"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPKapitaal() {
            properties.replace(PropertyName.BPKapitaal, getStringFromXBRL("Capital"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPKapitaalGeplaatst() {
            properties.replace(PropertyName.BPKapitaalGeplaatst, getStringFromXBRL("IssuedCapital"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPKapitaalNietOpgevraagd() {
            properties.replace(PropertyName.BPKapitaalNietOpgevraagd, getStringFromXBRL("UncalledCapital"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPUitgiftepremies() {
            properties.replace(PropertyName.BPUitgiftepremies, getStringFromXBRL("SharePremiumAccount"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPHerwaarderingsmeerwaarden() {
            properties.replace(PropertyName.BPHerwaarderingsmeerwaarden, getStringFromXBRL("RevaluationSurpluses"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPReserves() {
            properties.replace(PropertyName.BPReserves, getStringFromXBRL("Reserves"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPReservesWettelijkeReserve() {
            properties.replace(PropertyName.BPReservesWettelijkeReserve, getStringFromXBRL("DifferentCategoriesSharesValue"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReserves() {
            properties.replace(PropertyName.BPReservesOnbeschikbareReserves, getStringFromXBRL("ReservesNotAvailable"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReservesEigenAandelen() {
            properties.replace(PropertyName.BPReservesOnbeschikbareReservesEigenAandelen, getStringFromXBRL("ReservesNotAvailableOwnSharesHeld"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPReservesOnbeschikbareReservesAndere() {
            properties.replace(PropertyName.BPReservesOnbeschikbareReservesAndere, getStringFromXBRL("OtherReservesNotAvailable"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPReservesBelastingvrijeReserves() {
            properties.replace(PropertyName.BPReservesBelastingvrijeReserves, getStringFromXBRL("UntaxedReserves"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPReservesBeschikbareReserves() {
            properties.replace(PropertyName.BPReservesBeschikbareReserves, getStringFromXBRL("AvailableReserves"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPOvergedragenWinstVerlies() {
            properties.replace(PropertyName.BPOvergedragenWinstVerlies, getStringFromXBRL("AccumulatedProfitsLosses"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPKapitaalSubsidies() {
            properties.replace(PropertyName.BPKapitaalSubsidies, getStringFromXBRL("InvestmentGrants"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorschotVennotenVerdelingNettoActief() {
            properties.replace(PropertyName.BPVoorschotVennotenVerdelingNettoActief, getStringFromXBRL("AdvanceAssociatesSharingOutAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorzieningenUitgesteldeBelastingen() {
            properties.replace(PropertyName.BPVoorzieningenUitgesteldeBelastingen, getStringFromXBRL("ProvisionsDeferredTaxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKosten() {
            properties.replace(PropertyName.BPVoorzieningenRisicosKosten, getStringFromXBRL("ProvisionLiabilitiesCharges"));
            
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen() {
            properties.replace(PropertyName.BPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen, getStringFromXBRL("ProvisionsPensionsSimilarObligations"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenFiscaleLasten() {
            properties.replace(PropertyName.BPVoorzieningenRisicosKostenFiscaleLasten, getStringFromXBRL("ProvisionsTaxation"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken() {
            properties.replace(PropertyName.BPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken, getStringFromXBRL("ProvisionsMajorRepairsMaintenance"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenMilieuverplichtingen() {
            properties.replace(PropertyName.BPVoorzieningenRisicosKostenMilieuverplichtingen, getStringFromXBRL("ProvisionsOtherLiabilitiesCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPVoorzieningenRisicosKostenOverige() {
            properties.replace(PropertyName.BPVoorzieningenRisicosKostenOverige, getStringFromXBRL("DeferredTaxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPUitgesteldeBelastingen() {
            properties.replace(PropertyName.BPUitgesteldeBelastingen, getStringFromXBRL("AmountsPayable"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1Jaar() {
            properties.replace(PropertyName.BPSchuldenMeer1Jaar, getStringFromXBRL("AmountsPayableMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchulden() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchulden, getStringFromXBRL("FinancialDebtsRemainingTermMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen, getStringFromXBRL("SubordinatedLoansRemainingTermMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen, getStringFromXBRL("UnsubordinatedDebenturesRemainingTermMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden, getStringFromXBRL("LeasingSimilarObligationsRemainingTermMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen, getStringFromXBRL("AmountsPayableMoreOneYearCreditInstitutions"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen, getStringFromXBRL("OtherLoansRemainingTermMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarHandelsschulden() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarHandelsschulden, getStringFromXBRL("TradeDebtsPayableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels, getStringFromXBRL("BillExchangeMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen, getStringFromXBRL("AdvancesReceivedContractsProgressWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenMeer1JaarOverigeSchulden() {
            properties.replace(PropertyName.BPSchuldenMeer1JaarOverigeSchulden, getStringFromXBRL("OtherAmountsPayableMoreOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1Jaar() {
            properties.replace(PropertyName.BPSchuldenHoogstens1Jaar, getStringFromXBRL("AmountsPayableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen, getStringFromXBRL("CurrentPortionAmountsPayableMoreOneYearFallingDueWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchulden() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchulden, getStringFromXBRL("FinancialDebtsPayableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen, getStringFromXBRL("AmountsPayableWithinOneYearCreditInstitutions"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen, getStringFromXBRL("OtherLoansPayableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschulden() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschulden, getStringFromXBRL("TradeDebtsPayableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenLeveranciers() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenLeveranciers, getStringFromXBRL("SuppliersInvoicesToReceiveWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels, getStringFromXBRL("BillExchangePayableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen, getStringFromXBRL("AdvancesReceivedContractsProgressWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten, getStringFromXBRL("TaxesRemunerationSocialSecurity"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen, getStringFromXBRL("Taxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten, getStringFromXBRL("RemunerationSocialSecurity"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPSchuldenHoogstens1JaarOverigeSchulden() {
            properties.replace(PropertyName.BPSchuldenHoogstens1JaarOverigeSchulden, getStringFromXBRL("OtherAmountsPayableWithinOneYear"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPOverlopendeRekeningen() {
            properties.replace(PropertyName.BPOverlopendeRekeningen, getStringFromXBRL("AccruedChargesDeferredIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBPTotaalPassiva() {
            properties.replace(PropertyName.BPTotaalPassiva, getStringFromXBRL("EquityLiabilities"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsopbrengsten() {
            String bedrijfsOpbrengsten = getStringFromXBRL("OperatingIncomeNonRecurringOperatingIncomeIncluded");
            if (bedrijfsOpbrengsten.equals("0")) {
                bedrijfsOpbrengsten = getStringFromXBRL("OperatingIncome");
            }
            properties.replace(PropertyName.RRBedrijfsopbrengsten, bedrijfsOpbrengsten);
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenOmzet() {
            properties.replace(PropertyName.RRBedrijfsopbrengstenOmzet, getStringFromXBRL("Turnover"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering() {
            properties.replace(PropertyName.RRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering, getStringFromXBRL("IncreaseDecreaseStocksWorkContractsProgress"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenGeproduceerdeVasteActiva() {
            properties.replace(PropertyName.RRBedrijfsopbrengstenGeproduceerdeVasteActiva, getStringFromXBRL("OwnConstructionCapitalised"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenAndereBedrijfsopbrengsten() {
            properties.replace(PropertyName.RRBedrijfsopbrengstenAndereBedrijfsopbrengsten, getStringFromXBRL("OtherOperatingIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten() {
            properties.replace(PropertyName.RRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten, getStringFromXBRL("NonRecurringOperatingIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskosten() {
            properties.replace(PropertyName.RRBedrijfskosten, getStringFromXBRL("OperatingChargesNonRecurringOperatingChargesIncluded"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffen() {
            properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffen, getStringFromXBRL("RawMaterialsConsumables"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen() {
            properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen, getStringFromXBRL("PurchasesRawMaterialsConsumables"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename() {
            properties.replace(PropertyName.RRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename, getStringFromXBRL("IncreaseDecreaseStocks"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenDienstenDiverseGoederen() {
            properties.replace(PropertyName.RRBedrijfskostenDienstenDiverseGoederen, getStringFromXBRL("ServicesOtherGoods"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenBezoldigingenSocialeLastenPensioenen() {
            properties.replace(PropertyName.RRBedrijfskostenBezoldigingenSocialeLastenPensioenen, getStringFromXBRL("RemunerationSocialSecurityPensions"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva() {
            properties.replace(PropertyName.RRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva, getStringFromXBRL("DepreciationOtherAmountsWrittenDownFormationExpensesIntangibleTangibleFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen() {
            properties.replace(PropertyName.RRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen, getStringFromXBRL("AmountsWrittenDownStocksContractsProgressTradeDebtorsAppropriationsWriteBacks"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen() {
            properties.replace(PropertyName.RRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen, getStringFromXBRL("ProvisionsRisksChargesAppropriationsWriteBacks"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenAndereBedrijfskosten() {
            properties.replace(PropertyName.RRBedrijfskostenAndereBedrijfskosten, getStringFromXBRL("MiscellaneousOperatingCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenHerstructurerngskostenGeactiveerdeBedrijfskosten() {
            properties.replace(PropertyName.RRBedrijfskostenHerstructurerngskostenGeactiveerdeBedrijfskosten, getStringFromXBRL("OperatingChargesCarriedAssetsRestructuringCosts"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenNietRecurrenteBedrijfskosten() {
            properties.replace(PropertyName.RRBedrijfskostenNietRecurrenteBedrijfskosten, getStringFromXBRL("NonRecurringOperatingCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfskostenUitzonderlijkeKosten() {
            properties.replace(PropertyName.RRBedrijfskostenUitzonderlijkeKosten, getStringFromXBRL("ExtraordinaryCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsopbrengstenUitzonderlijkeOpbrengsten() {
            properties.replace(PropertyName.RRBedrijfsopbrengstenUitzonderlijkeOpbrengsten, getStringFromXBRL("ExtraordinaryIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBedrijfsWinstVerlies() {
            properties.replace(PropertyName.RRBedrijfsWinstVerlies, getStringFromXBRL("OperatingProfitLoss"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleOpbrengsten() {
            properties.replace(PropertyName.RRFinancieleOpbrengsten, getStringFromXBRL("FinancialIncomeNonRecurringFinancialIncomeIncluded"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrent() {
            properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrent, getStringFromXBRL("FinancialIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva() {
            properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva, getStringFromXBRL("IncomeFinancialFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva() {
            properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva, getStringFromXBRL("IncomeCurrentAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten() {
            properties.replace(PropertyName.RRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten, getStringFromXBRL("OtherFinancialIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleOpbrengstenNietRecurrent() {
            properties.replace(PropertyName.RRFinancieleOpbrengstenNietRecurrent, getStringFromXBRL("NonRecurringFinancialIncome"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleKosten() {
            String financieleKosten = getStringFromXBRL("FinancialChargesNonRecurringFinancialChargesIncluded");
            if (financieleKosten.equals("0")) {
                financieleKosten = getStringFromXBRL("FinancialCharges");
            }
            properties.replace(PropertyName.RRFinancieleKosten, financieleKosten);
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrent() {
            properties.replace(PropertyName.RRFinancieleKostenRecurrent, getStringFromXBRL("FinancialCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentKostenSchulden() {
            properties.replace(PropertyName.RRFinancieleKostenRecurrentKostenSchulden, getStringFromXBRL("DebtCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen() {
            properties.replace(PropertyName.RRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen, getStringFromXBRL("ProvisionsRisksChargesAppropriationsWriteBacks"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleKostenRecurrentAndereFinancieleKosten() {
            properties.replace(PropertyName.RRFinancieleKostenRecurrentAndereFinancieleKosten, getStringFromXBRL("OtherFinancialCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRFinancieleKostenNietRecurrent() {
            properties.replace(PropertyName.RRFinancieleKostenNietRecurrent, getStringFromXBRL("NonRecurringFinancialCharges"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRWinstVerliesBoekjaarVoorBelastingen() {
            properties.replace(PropertyName.RRWinstVerliesBoekjaarVoorBelastingen, getStringFromXBRL("GainLossBeforeTaxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRROntrekkingenUitgesteldeBelastingen() {
            properties.replace(PropertyName.RROntrekkingenUitgesteldeBelastingen, getStringFromXBRL("TransferFromDeferredTaxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRROverboekingUitgesteldeBelastingen() {
            properties.replace(PropertyName.RROverboekingUitgesteldeBelastingen, getStringFromXBRL("TransferToDeferredTaxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBelastingenOpResultaat() {
            properties.replace(PropertyName.RRBelastingenOpResultaat, getStringFromXBRL("IncomeTaxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBelastingenOpResultaatBelastingen() {
            properties.replace(PropertyName.RRBelastingenOpResultaatBelastingen, getStringFromXBRL("BelgianForeignIncomeTaxes"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen() {
            properties.replace(PropertyName.RRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen, getStringFromXBRL("AdjustmentIncomeTaxesWriteBackTaxProvisions"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRWinstVerliesBoekjaar() {
            properties.replace(PropertyName.RRWinstVerliesBoekjaar, getStringFromXBRL("GainLossPeriod"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRROntrekkingBelastingvrijeReserves() {
            properties.replace(PropertyName.RROntrekkingBelastingvrijeReserves, getStringFromXBRL("TransferFromUntaxedReserves"));
            
            return this;
        }
        
        @Override
        public IDocumentBuilder addRROverboekingBelastingvrijeReserves() {
            properties.replace(PropertyName.RROverboekingBelastingvrijeReserves, getStringFromXBRL("TransferToUntaxedReserves"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addRRTeBestemmenWinstVerliesBoekjaar() {
            properties.replace(PropertyName.RRTeBestemmenWinstVerliesBoekjaar, getStringFromXBRL("GainLossToBeAppropriated"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("ConcessionsPatentsLicencesSimilarRightsAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLIMVAMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLIMVAMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("IntangibleFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLMVAMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLMVAMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("TangibleFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLFVAMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLFVAMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("FinancialFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("LandBuildingsAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("PlantMachineryEquipmentAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("FurnitureVehiclesAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("OtherTangibleFixedAssetsAcquisitionIncludingProducedFixedAssets"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("ParticipatingInterestsSharesEnterprisesLinkedParticipatingInterestAcquisitions"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addTLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen() {
            properties.replace(PropertyName.TLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen, getStringFromXBRL("OtherParticipatingInterestsSharesAcquisitions"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBGemiddeldeFTE() {
            properties.replace(PropertyName.SBGemiddeldeFTE, getStringFromXBRL("AverageNumberEmployeesPersonnelRegisterTotalFullTimeEquivalents"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBGepresteerdeUren() {
            properties.replace(PropertyName.SBGepresteerdeUren, getStringFromXBRL("NumberHoursActuallyWorkedTotal"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBGemiddeldAantalFTEUitzendkrachten() {
            properties.replace(PropertyName.SBGemiddeldAantalFTEUitzendkrachten, getStringFromXBRL("HiredTemporaryStaffAverageNumberPersonsEmployed"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBPersoneelskosten() {
            properties.replace(PropertyName.SBPersoneelskosten, getStringFromXBRL("PersonnelCostsTotal"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBGepresteerdeUrenUitzendkrachten() {
            properties.replace(PropertyName.SBGepresteerdeUrenUitzendkrachten, getStringFromXBRL("HiredTemporaryStaffNumbersHoursActuallyWorked"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBPersoneelskostenUitzendkrachten() {
            properties.replace(PropertyName.SBPersoneelskostenUitzendkrachten, getStringFromXBRL("HiredTemporaryStaffCostsEnterprise"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBAantalWerknemersOpEindeBoekjaar() {
            properties.replace(PropertyName.SBAantalWerknemersOpEindeBoekjaar, getStringFromXBRL("NumberEmployeesPersonnelRegisterClosingDateFinancialYearTotalFullTimeEquivalents"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBAantalBediendenOpEindeBoekjaar() {
            properties.replace(PropertyName.SBAantalBediendenOpEindeBoekjaar, getStringFromXBRL("NumberEmployeesPersonnelRegisterClosingDateFinancialYearEmployeesTotalFullTimeEquivalents"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addSBAantalArbeidersOpEindeBoekjaar() {
            properties.replace(PropertyName.SBAantalArbeidersOpEindeBoekjaar, getStringFromXBRL("NumberEmployeesPersonnelRegisterClosingDateFinancialYearWorkersTotalFullTimeEquivalents"));
            return this;
        }
        
        @Override
        public IDocumentBuilder addBVBABrutomarge() {
            properties.replace(PropertyName.BVBABrutomarge, getStringFromXBRL("GrossOperatingMargin"));
            return this;
        }
    }
}
