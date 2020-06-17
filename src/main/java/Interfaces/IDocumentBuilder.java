package Interfaces;

import Domain.Business;
import Domain.DocumentWrapper;
import Enums.PropertyName;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public interface IDocumentBuilder {

    String getName();

    int getYear();

    Business getBusiness();

    SimpleStringProperty getNameProperty();

    BooleanProperty getSelectedProperty();

    DocumentWrapper build();

    IDocumentBuilder removeProperty(PropertyName propertyName);

    //BALANS NA WINSTVERDELING
    //ACTIVA
    //OPRICHTINGSKOSTEN
    IDocumentBuilder addBAOprichtingskosten();

    //VASTE ACTIVA
    IDocumentBuilder addBAVasteActiva();

    //IMMATERIËLE ACTIVA
    IDocumentBuilder addBAImmaterieleVasteActiva();

    //MATERIËLE ACTIVA
    IDocumentBuilder addBAMaterieleVasteActiva();

    IDocumentBuilder addBATerreinenGebouwen();

    IDocumentBuilder addBAInstallatiesMachinesUitrusting();

    IDocumentBuilder addBAMeubilairRollendMaterieel();

    IDocumentBuilder addBALeasingSoortgelijkeRechten();

    IDocumentBuilder addBAOverigeMaterieleVasteActiva();

    IDocumentBuilder addBAActivaAanbouwVooruitbetalingen();

    //FINANCIËLE ACTIVA
    IDocumentBuilder addBAFinancieleVasteActiva();

    IDocumentBuilder addBAVerbondenOndernemingen();

    IDocumentBuilder addBAVerbondenOndernemingenDeelnemingen();

    IDocumentBuilder addBAVerbondenOndernemingenVorderingen();

    IDocumentBuilder addBAOndernemingenDeelnemingsverhouding();

    IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingDeelnemingen();

    IDocumentBuilder addBAOndernemingenDeelnemingsverhoudingVorderingen();

    IDocumentBuilder addBAAndereFinancieleVasteActiva();

    IDocumentBuilder addBAAndereFinancieleVasteActivaAandelen();

    IDocumentBuilder addBAAndereFinancieleVasteActivaVorderingenBorgtochtenContanten();

    //VLOTTENDE ACTIVA
    IDocumentBuilder addBAVlottendeActiva();

    IDocumentBuilder addBAVorderingenMeer1Jaar();

    IDocumentBuilder addBAVorderingenMeer1JaarHandelsvorderingen();

    IDocumentBuilder addBAVorderingenMeer1JaarOverigeVorderingen();

    IDocumentBuilder addBAVoorradenBestellingenUitvoering();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorraden();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGrondHulpstoffen();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGoederenBewerking();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenGereedProduct();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenHandelsgoederen();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenOnroerendeGoederenVerkoop();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringVoorradenVooruitbetalingen();

    IDocumentBuilder addBAVoorradenBestellingenUitvoeringBestellingenUitvoer();

    IDocumentBuilder addBAVorderingenHoogstens1Jaar();

    IDocumentBuilder addBAVorderingenHoogstens1JaarHandelsvorderingen();

    IDocumentBuilder addBAVorderingenHoogstens1JaarOverigeVorderingen();

    IDocumentBuilder addBAGeldBeleggingen();

    IDocumentBuilder addBAGeldBeleggingenEigenAandelen();

    IDocumentBuilder addBAGeldBeleggingenOverigeBeleggingen();

    IDocumentBuilder addBALiquideMiddelen();

    IDocumentBuilder addBAOverlopendeRekeningen();

    IDocumentBuilder addBATotaalActiva();

    //PASSIVA
    //EIGEN VERMOGEN
    IDocumentBuilder addBPEigenVermogen();

    IDocumentBuilder addBPKapitaal();

    IDocumentBuilder addBPKapitaalGeplaatst();

    IDocumentBuilder addBPKapitaalNietOpgevraagd();

    IDocumentBuilder addBPUitgiftepremies();

    IDocumentBuilder addBPHerwaarderingsmeerwaarden();

    IDocumentBuilder addBPReserves();

    IDocumentBuilder addBPReservesWettelijkeReserve();

    IDocumentBuilder addBPReservesOnbeschikbareReserves();

    IDocumentBuilder addBPReservesOnbeschikbareReservesEigenAandelen();

    IDocumentBuilder addBPReservesOnbeschikbareReservesAndere();

    IDocumentBuilder addBPReservesBelastingvrijeReserves();

    IDocumentBuilder addBPReservesBeschikbareReserves();

    IDocumentBuilder addBPOvergedragenWinstVerlies();

    IDocumentBuilder addBPKapitaalSubsidies();

    IDocumentBuilder addBPVoorschotVennotenVerdelingNettoActief();

    //VOORZIENINGEN EN UITGESTELDE BELASTINGEN
    IDocumentBuilder addBPVoorzieningenUitgesteldeBelastingen();

    IDocumentBuilder addBPVoorzieningenRisicosKosten();

    IDocumentBuilder addBPVoorzieningenRisicosKostenPensioenenSoortelijkeVerplichtingen();

    IDocumentBuilder addBPVoorzieningenRisicosKostenFiscaleLasten();

    IDocumentBuilder addBPVoorzieningenRisicosKostenGroteHerstellingsOnderhoudswerken();

    IDocumentBuilder addBPVoorzieningenRisicosKostenMilieuverplichtingen();

    IDocumentBuilder addBPVoorzieningenRisicosKostenOverige();

    IDocumentBuilder addBPUitgesteldeBelastingen();

    IDocumentBuilder addBPSchuldenMeer1Jaar();

    IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchulden();

    IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenAchtergesteldeLeningen();

    IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenNietAchtergesteldeObligatieleningen();

    IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenLeasingschuldenSoortgelijkeSchulden();

    IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenKredietinstellingen();

    IDocumentBuilder addBPSchuldenMeer1JaarFinancieleSchuldenOverigeLeningen();

    IDocumentBuilder addBPSchuldenMeer1JaarHandelsschulden();

    IDocumentBuilder addBPSchuldenMeer1JaarHandelsschuldenTeBetalenWissels();

    IDocumentBuilder addBPSchuldenMeer1JaarOntvangenVooruitbetalingenBestellingen();

    IDocumentBuilder addBPSchuldenMeer1JaarOverigeSchulden();

    IDocumentBuilder addBPSchuldenHoogstens1Jaar();

    IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenMeer1JaarBinnenJaarVervallen();

    IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchulden();

    IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenKredietinstellingen();

    IDocumentBuilder addBPSchuldenHoogstens1JaarFinancieleSchuldenOverigeLeningen();

    IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschulden();

    IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenLeveranciers();

    IDocumentBuilder addBPSchuldenHoogstens1JaarHandelsschuldenTeBetalenWissels();

    IDocumentBuilder addBPSchuldenHoogstens1JaarOntvangenVooruitbetalingenBestellingen();

    IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLasten();

    IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBelastingen();

    IDocumentBuilder addBPSchuldenHoogstens1JaarSchuldenBelastingenBezoldigingenSocialeLastenBezoldigingenSocialeLasten();

    IDocumentBuilder addBPSchuldenHoogstens1JaarOverigeSchulden();

    IDocumentBuilder addBPOverlopendeRekeningen();

    IDocumentBuilder addBPTotaalPassiva();

    //RESULATATENREKENING
    //BEDRIJFOPBRENGST
    IDocumentBuilder addRRBedrijfsopbrengsten();

    IDocumentBuilder addRRBedrijfsopbrengstenOmzet();

    IDocumentBuilder addRRBedrijfsopbrengstenToenameAfnameVoorraadGoederenBewerkingGereedProductBestellingenUitvoering();

    IDocumentBuilder addRRBedrijfsopbrengstenGeproduceerdeVasteActiva();

    IDocumentBuilder addRRBedrijfsopbrengstenAndereBedrijfsopbrengsten();

    IDocumentBuilder addRRBedrijfsopbrengstenNietRecurrenteBedrijfsopbrengsten();

    //BEDRIJFSKOSTEN
    IDocumentBuilder addRRBedrijfskosten();

    IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffen();

    IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenAankopen();

    IDocumentBuilder addRRBedrijfskostenHandelsgoederenGrondHulpstoffenVoorraadAfnameToename();

    IDocumentBuilder addRRBedrijfskostenDienstenDiverseGoederen();

    IDocumentBuilder addRRBedrijfskostenBezoldigingenSocialeLastenPensioenen();

    IDocumentBuilder addRRBedrijfskostenAfschrijvingenWaardeverminderingenOprichtingskostenImmaterieleMaterieleVasteActiva();

    IDocumentBuilder addRRBedrijfskostenWaardeverminderingenVoorradenBestellingenUitvoeringHandelsvorderingenToevoegingenTerugnemingen();

    IDocumentBuilder addRRBedrijfskostenVoorzieningenRisicosKostenToevoegingenBestedingenTerugnemingen();

    IDocumentBuilder addRRBedrijfskostenAndereBedrijfskosten();

    IDocumentBuilder addRRBedrijfskostenHerstructurerngskostenGeactiveerdeBedrijfskosten();

    IDocumentBuilder addRRBedrijfskostenNietRecurrenteBedrijfskosten();

    IDocumentBuilder addRRBedrijfskostenUitzonderlijkeKosten();

    IDocumentBuilder addRRBedrijfsopbrengstenUitzonderlijkeOpbrengsten();

    //BEDRIJFSWINSTVERLIES
    IDocumentBuilder addRRBedrijfsWinstVerlies();

    //FINANCIËLE OPBRENGSTEN
    IDocumentBuilder addRRFinancieleOpbrengsten();

    IDocumentBuilder addRRFinancieleOpbrengstenRecurrent();

    IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenFinancieleVasteActiva();

    IDocumentBuilder addRRFinancieleOpbrengstenRecurrentOpbrengstenVlottendeActiva();

    IDocumentBuilder addRRFinancieleOpbrengstenRecurrentAndereFinancieleOpbrengsten();

    IDocumentBuilder addRRFinancieleOpbrengstenNietRecurrent();

    //FINANCIËLE KOSTEN
    IDocumentBuilder addRRFinancieleKosten();

    IDocumentBuilder addRRFinancieleKostenRecurrent();

    IDocumentBuilder addRRFinancieleKostenRecurrentKostenSchulden();

    IDocumentBuilder addRRFinancieleKostenRecurrentWaardeverminderingenVlottendeActivaAndereVoorradenBestellingenUitvoeringHandelvorderingenToevoegingenTerugnemingen();

    IDocumentBuilder addRRFinancieleKostenRecurrentAndereFinancieleKosten();

    IDocumentBuilder addRRFinancieleKostenNietRecurrent();

    //ANDERE
    IDocumentBuilder addRRWinstVerliesBoekjaarVoorBelastingen();

    IDocumentBuilder addRROntrekkingenUitgesteldeBelastingen();

    IDocumentBuilder addRROverboekingUitgesteldeBelastingen();

    IDocumentBuilder addRRBelastingenOpResultaat();

    IDocumentBuilder addRRBelastingenOpResultaatBelastingen();

    IDocumentBuilder addRRBelastingenOpResultaatRegulariseringBelastingenTerugnemingVoorzieningenBelastingen();

    IDocumentBuilder addRRWinstVerliesBoekjaar();

    IDocumentBuilder addRROntrekkingBelastingvrijeReserves();

    IDocumentBuilder addRROverboekingBelastingvrijeReserves();

    IDocumentBuilder addRRTeBestemmenWinstVerliesBoekjaar();

    IDocumentBuilder addTLMVAMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLIMVAMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLMVAConcessiesOctrooienLicentiesKnowhowMerkenSoortgelijkeRechtenMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLFVAMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLMVATerreinenEnGebouwenMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLMVAInstallatiesMachinesUitrustingMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLMVAMeubilairRollendMaterieelMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLMVAOverigeMaterieleActivaMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLFVAOndernemingenDeelnemingsverhoudingMutatiesTijdensBoekjaarAanschaffingen();

    IDocumentBuilder addTLFVAAndereOndernemingenMutatiesTijdensBoekjaarAanschaffingen();

    //SOCIALE BALANS
    IDocumentBuilder addSBGemiddeldeFTE();

    IDocumentBuilder addSBGepresteerdeUren();

    IDocumentBuilder addSBGemiddeldAantalFTEUitzendkrachten();

    IDocumentBuilder addSBPersoneelskosten();

    IDocumentBuilder addSBGepresteerdeUrenUitzendkrachten();

    IDocumentBuilder addSBPersoneelskostenUitzendkrachten();

    IDocumentBuilder addSBAantalWerknemersOpEindeBoekjaar();

    IDocumentBuilder addSBAantalBediendenOpEindeBoekjaar();

    IDocumentBuilder addSBAantalArbeidersOpEindeBoekjaar();

    IDocumentBuilder addBVBABrutomarge();
}
