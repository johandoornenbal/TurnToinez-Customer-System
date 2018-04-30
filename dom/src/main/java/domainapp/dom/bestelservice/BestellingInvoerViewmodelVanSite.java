package domainapp.dom.bestelservice;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.siteservice.FormulierVanSite;
import domainapp.dom.siteservice.FormulierVanSiteRepository;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bestellingInvoerViewmodelVanSite")
@XmlType(
        name = "",
        propOrder = {
                "naamTurnster",
                "leeftijd",
                "kledingmaat",
                "lengte",
                "postuur",
                "naamKlant",
                "email",
                "straat",
                "postcode",
                "plaats",
                "telefoon1",
                "telefoon2",
                "aantekeningen",
                "klantRepository",
                "bestellingRepository",
                "pakje",
                "prijsPakje",
                "broekje",
                "prijsBroekje",
                "elastiek",
                "prijsElastiek",
                "verzendkosten",
                "opmerkingen",
                "prijsInfo",
                "formulierVanSite",
                "formulierVanSiteRepository"
        }
)
public class BestellingInvoerViewmodelVanSite {

    public BestellingInvoerViewmodelVanSite() {
    }

    public BestellingInvoerViewmodelVanSite(
            final String naamTurnsterInv,
            final String leeftijdInv,
            final String kledingmaatInv,
            final String lengteInv,
            final Postuur postuurInv,
            final String naamKlant,
            final String straat,
            final String postcode,
            final String plaats,
            final String email,
            final String telefoon,
            final String vragen,
            final String pakje,
            final String mouwtje,
            final Boolean broekje,
            final String kleurBroekje,
            final Boolean elastiek,
            final String opmerkingen,
            final String prijsInfo,
            final int formulierVanSite
    ) {
        this.naamTurnster = naamTurnsterInv;
        this.leeftijd = leeftijdInv;
        this.kledingmaat = kledingmaatInv;
        this.lengte = lengteInv;
        this.postuur = postuurInv;
        this.naamKlant = naamKlant;
        this.straat = straat;
        this.postcode = postcode;
        this.plaats = plaats;
        this.email = email;
        this.telefoon1 = telefoon;
        this.aantekeningen = vragen;
        this.pakje = mouwtje!=null ? "Turnpakje ".concat(pakje).concat(" ").concat(mouwtje).concat(" mouwtje") : pakje;
        this.broekje = broekje ? "Broekje ".concat(kleurBroekje) : null;
        this.elastiek = elastiek ? "Bijpassend haar elastiek" : null;
        this.opmerkingen = opmerkingen;
        this.prijsInfo = prijsInfo;
        this.prijsPakje = BigDecimal.ZERO;
        this.prijsBroekje = BigDecimal.ZERO;
        this.prijsElastiek = BigDecimal.ZERO;
        this.verzendkosten = BigDecimal.ZERO;
        this.formulierVanSite = formulierVanSite;
    }

    public String title() {
        return "Nieuwe bestelling en klant";
    }

    // turnster

    @Getter @Setter
    @XmlElement(required = false)
    private String naamTurnster;

    @Getter @Setter
    @XmlElement(required = false)
    private String leeftijd;

    @Getter @Setter
    @XmlElement(required = false)
    private String kledingmaat;

    @Getter @Setter
    @XmlElement(required = false)
    private String lengte;

    @Getter @Setter
    @XmlElement(required = false)
    private Postuur postuur;

    // Bestelling

    @Getter @Setter
    @XmlElement(required = false)
    private String pakje;

    @Getter @Setter
    @XmlElement(required = false)
    private BigDecimal prijsPakje;

    @Getter @Setter
    @XmlElement(required = false)
    private String broekje;

    @Getter @Setter
    @XmlElement(required = false)
    private BigDecimal prijsBroekje;

    @Getter @Setter
    @XmlElement(required = false)
    private String elastiek;

    @Getter @Setter
    @XmlElement(required = false)
    private BigDecimal prijsElastiek;

    @Getter @Setter
    @XmlElement(required = false)
    private BigDecimal verzendkosten;

    @Getter @Setter
    @XmlElement(required = false)
    @PropertyLayout(multiLine = 5)
    private String opmerkingen;

    // Prijs Info Site

    @Getter @Setter
    @XmlElement(required = false)
    @PropertyLayout(multiLine = 8)
    @Property(editing = Editing.DISABLED)
    private String prijsInfo;

    // Klant

    @Getter @Setter
    @XmlElement(required = true)
    private String naamKlant;

    @Getter @Setter
    @XmlElement(required = false)
    private String straat;

    @Getter @Setter
    @XmlElement(required = false)
    private String postcode;

    @Getter @Setter
    @XmlElement(required = false)
    private String plaats;

    @Getter @Setter
    @XmlElement(required = false)
    private String email;

    @Getter @Setter
    @XmlElement(required = false)
    private String telefoon1;

    @Getter @Setter
    @XmlElement(required = false)
    private String telefoon2;

    @Getter @Setter
    @XmlElement(required = false)
    @PropertyLayout(multiLine = 5)
    private String aantekeningen;

    // Formulier
    @Getter @Setter
    @XmlElement(required = true)
    private int formulierVanSite;

    // Acties

    public Bestelling maakBestellingEnKlant(){
        Klant nieuweKLant = getNaamKlant()!=null ?
                klantRepository.nieuweKlant(
                getNaamKlant(),getStraat(), getPostcode(), getPlaats(),
                getEmail(), getTelefoon1(), getTelefoon2(), getAantekeningen())
                :
                null;
        Bestelling nieuweBestelling = bestellingRepository.nieuweBestelling(
                nieuweKLant,
                getNaamTurnster(),
                getLeeftijd(),
                getKledingmaat(),
                getLengte(),
                getPostuur()
        );
        boolean opmVerwerkt = false;
        if (getPakje()!=null){
            nieuweBestelling.nieuweRegel(getPakje(), getPrijsPakje(), getOpmerkingen(), 1);
            opmVerwerkt=true;
        }
        if (getBroekje()!=null){
            nieuweBestelling.nieuweRegel(getBroekje(), getPrijsBroekje(), opmVerwerkt?null:getOpmerkingen(), 2);
            opmVerwerkt=true;
        }
        if (getElastiek()!=null){
            nieuweBestelling.nieuweRegel(getElastiek(), getPrijsElastiek(), opmVerwerkt?null:getOpmerkingen(), 3);
        }
        if (getVerzendkosten()!=null){
            nieuweBestelling.nieuweRegel("Verzendkosten", getVerzendkosten(), null, 4);
        }
        FormulierVanSite formulier = formulierVanSiteRepository.findByNummer(formulierVanSite);
        formulier.setVerwerkt(true);
        return nieuweBestelling;
    }

    public BestellingInvoerViewmodelVanSite bestellingWijzigen(
            @Parameter(optionality = Optionality.OPTIONAL)
            final String pakje,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsPakje,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String broekje,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsBroekje,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String elastiek,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsElastiek,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal verzendkosten,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 5)
            final String opmerkingen
    ){
        setPakje(pakje);
        setPrijsPakje(prijsPakje);
        setBroekje(broekje);
        setPrijsBroekje(prijsBroekje);
        setElastiek(elastiek);
        setPrijsElastiek(prijsElastiek);
        setVerzendkosten(verzendkosten);
        setOpmerkingen(opmerkingen);
        return this;
    }

    public String default0BestellingWijzigen(){
          return getPakje();
    }

    public BigDecimal default1BestellingWijzigen(){
        return getPrijsPakje();
    }

    public String default2BestellingWijzigen(){
        return getBroekje();
    }

    public BigDecimal default3BestellingWijzigen(){
        return getPrijsBroekje();
    }

    public String default4BestellingWijzigen(){
        return getElastiek();
    }

    public BigDecimal default5BestellingWijzigen(){
        return getPrijsElastiek();
    }

    public BigDecimal default6BestellingWijzigen(){
        return getVerzendkosten();
    }

    public String default7BestellingWijzigen(){
        return getOpmerkingen();
    }

    public BestellingInvoerViewmodelVanSite turnsterGegevensWijzigen(
            @Parameter(optionality = Optionality.OPTIONAL)
            final String naamTurnster,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String leeftijd,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String kledingmaat,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String lengte,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Postuur postuur
    ){
        setNaamTurnster(naamTurnster);
        setLeeftijd(leeftijd);
        setKledingmaat(kledingmaat);
        setLengte(lengte);
        setPostuur(postuur);
        return this;
    }

    public String default0TurnsterGegevensWijzigen(){
        return getNaamTurnster();
    }

    public String default1TurnsterGegevensWijzigen(){
        return getLeeftijd();
    }

    public String default2TurnsterGegevensWijzigen(){
        return getKledingmaat();
    }

    public String default3TurnsterGegevensWijzigen(){
        return getLengte();
    }

    public Postuur default4TurnsterGegevensWijzigen(){
        return getPostuur();
    }

    public BestellingInvoerViewmodelVanSite klantGegevensWijzigen(
            final String naamKlant,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String email,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String straat,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String postcode,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String plaats,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String telefoon1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String telefoon2,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 5)
            final String aantekeningen
    ) {
        setNaamKlant(naamKlant);
        setEmail(email);
        setStraat(straat);
        setPostcode(postcode);
        setPlaats(plaats);
        setTelefoon1(telefoon1);
        setTelefoon2(telefoon2);
        setAantekeningen(aantekeningen);
        return this;
    }

    public String default0KlantGegevensWijzigen() {
        return getNaamKlant();
    }

    public String default1KlantGegevensWijzigen() {
        return getEmail();
    }

    public String default2KlantGegevensWijzigen() {
        return getStraat();
    }

    public String default3KlantGegevensWijzigen() {
        return getPostcode();
    }

    public String default4KlantGegevensWijzigen() {
        return getPlaats();
    }

    public String default5KlantGegevensWijzigen() {
        return getTelefoon1();
    }

    public String default6KlantGegevensWijzigen() {
        return getTelefoon2();
    }

    public String default7KlantGegevensWijzigen() {
        return getAantekeningen();
    }

    @Inject
    KlantRepository klantRepository;

    @Inject
    BestellingRepository bestellingRepository;

    @Inject
    FormulierVanSiteRepository formulierVanSiteRepository;

}
