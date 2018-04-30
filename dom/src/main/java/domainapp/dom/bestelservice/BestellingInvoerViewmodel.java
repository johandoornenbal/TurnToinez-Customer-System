package domainapp.dom.bestelservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bestellingInvoerViewmodel")
@XmlType(
        name = "",
        propOrder = {
                "naamTurnster",
                "leeftijd",
                "kledingmaat",
                "lengte",
                "postuur",
                "regels",
                "naamKlant",
                "email",
                "straat",
                "postcode",
                "plaats",
                "telefoon1",
                "telefoon2",
                "aantekeningen",
                "klantRepository",
                "bestellingRepository"
        }
)
public class BestellingInvoerViewmodel {

    public BestellingInvoerViewmodel() {
    }

    public BestellingInvoerViewmodel(
            final String naamTurnsterInv,
            final String leeftijdInv,
            final String kledingmaatInv,
            final String lengteInv,
            final Postuur postuurInv
    ) {
        this.naamTurnster = naamTurnsterInv;
        this.leeftijd = leeftijdInv;
        this.kledingmaat = kledingmaatInv;
        this.lengte = lengteInv;
        this.postuur = postuurInv;
    }

    public BestellingInvoerViewmodel(
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
            final String vragen
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
    }

    public String title() {
        return "Nieuwe bestelling en klant";
    }

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

    @XmlElementWrapper
    @XmlElement(name = "regelViewmodel")
    @Getter @Setter
    private List<RegelViewmodel> regels = new ArrayList<>();

    // KLANT

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

    public Bestelling maakBestelling(){
        Klant nieuweKLant = getNaamKlant()!=null && getEmail()!=null ?
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
        for (RegelViewmodel regel : getRegels()){
            nieuweBestelling.nieuweRegel(regel.getArtikel(), regel.getPrijs(), regel.getOpmerking(), regel.getVolgorde());
        }
        return nieuweBestelling;
    }

    public String disableMaakBestelling(){
        if (this.getNaamKlant()==null){
            return "Er is nog geen klant";
        }
        return null;
    }

    public BestellingInvoerViewmodel nieuwArtikel(
            final String artikel,
            final BigDecimal prijs,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 5)
            final String opmerking,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Integer volgorde) {
        RegelViewmodel regel = new RegelViewmodel(artikel, prijs, opmerking, volgorde);
        regels.add(regel);
        return this;
    }

    public BestellingInvoerViewmodel verwijderLaatsteArtikel() {
        Integer last = regels.size();
        if (last > 0) {
            regels.remove(last - 1);
        }
        return this;
    }

    public BestellingInvoerViewmodel nieuweKlantInvoeren(
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

    public String default0NieuweKlantInvoeren() {
        return getNaamKlant();
    }

    public String default1NieuweKlantInvoeren() {
        return getEmail();
    }

    public String default2NieuweKlantInvoeren() {
        return getStraat();
    }

    public String default3NieuweKlantInvoeren() {
        return getPostcode();
    }

    public String default4NieuweKlantInvoeren() {
        return getPlaats();
    }

    public String default5NieuweKlantInvoeren() {
        return getTelefoon1();
    }

    public String default6NieuweKlantInvoeren() {
        return getTelefoon2();
    }

    public String default7NieuweKlantInvoeren() {
        return getAantekeningen();
    }

    public boolean hideNieuweKlantInvoeren() {
        if (getNaamKlant()!=null || getEmail()!=null){
            return true;
        }
        return false;
    }

    public BestellingInvoerViewmodel wijzigKlantGegevens(
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

    public String default0WijzigKlantGegevens() {
        return getNaamKlant();
    }

    public String default1WijzigKlantGegevens() {
        return getEmail();
    }

    public String default2WijzigKlantGegevens() {
        return getStraat();
    }

    public String default3WijzigKlantGegevens() {
        return getPostcode();
    }

    public String default4WijzigKlantGegevens() {
        return getPlaats();
    }

    public String default5WijzigKlantGegevens() {
        return getTelefoon1();
    }

    public String default6WijzigKlantGegevens() {
        return getTelefoon2();
    }

    public String default7WijzigKlantGegevens() {
        return getAantekeningen();
    }

    public boolean hideWijzigKlantGegevens() {
        if (getNaamKlant()!=null || getEmail()!=null){
            return false;
        }
        return true;
    }

    public BestellingInvoerViewmodel zoekBestaandeKlant(final Klant klant){
        setNaamKlant(klant.getNaam());
        setEmail(klant.getEmail());
        setStraat(klant.getStraat());
        setPostcode(klant.getPostcode());
        setPlaats(klant.getPlaats());
        setTelefoon1(klant.getTelefoon1());
        setTelefoon2(klant.getTelefoon2());
        setAantekeningen(klant.getAantekeningen());
        return this;
    }

    @Inject
    KlantRepository klantRepository;

    @Inject
    BestellingRepository bestellingRepository;

}
