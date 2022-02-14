package Models;

import java.util.ArrayList;
import java.util.List;

public class Business {
    
    private String name;
    private String ondernemingsnummer;
    private String adres;
    private String nr;
    private String bus;
    private String postcode;
    private String landcode;
    private List<DocumentWrapper> jaarrekeningen;
    
    public Business(String name, String ondernemingsnummer, String adres, String nr, String bus, String postcode, String landcode) {
        setName(name);
        setOndernemingsnummer(ondernemingsnummer);
        setAdres(adres);
        setNr(nr);
        setBus(bus);
        setPostcode(postcode);
        setLandcode(landcode);
        this.jaarrekeningen = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOndernemingsnummer() {
        return ondernemingsnummer;
    }

    public void setOndernemingsnummer(String ondernemingsnummer) {
        this.ondernemingsnummer = ondernemingsnummer;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLandcode() {
        return landcode;
    }

    public void setLandcode(String landcode) {
        this.landcode = landcode;
    }
    
    public List<DocumentWrapper> getJaarrekeningen(){
        return jaarrekeningen;
    }
    
    public void addJaarrekening(DocumentWrapper document){
        jaarrekeningen.add(document);
    }
}
