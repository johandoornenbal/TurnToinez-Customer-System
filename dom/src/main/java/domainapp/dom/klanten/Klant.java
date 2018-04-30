package domainapp.dom.klanten;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.wicket.gmap3.cpt.applib.Locatable;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.isisaddons.wicket.gmap3.cpt.service.LocationLookupService;

import org.incode.module.base.dom.utils.TitleBuilder;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.communicatie.Communicatie;
import domainapp.dom.communicatie.CommunicatieRepository;
import domainapp.dom.facturen.Factuur;
import domainapp.dom.facturen.FactuurRepository;
import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.mailchimpintegration.module.api.IMailChimpParty;
import domainapp.dom.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "tti"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.VERSION_NUMBER,
        column="version")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByNaamContains", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.klanten.Klant "
                    + "WHERE naam.toLowerCase().indexOf(:naam.toLowerCase()) >= 0 "),
    @javax.jdo.annotations.Query(
            name = "findByEmailContains", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.klanten.Klant "
                    + "WHERE email.toLowerCase().indexOf(:email.toLowerCase()) >= 0 "),
    @javax.jdo.annotations.Query(
            name = "findUnique", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.klanten.Klant "
                    + "WHERE uniqueId == :uniqueId.toLowerCase() "),
    @javax.jdo.annotations.Query(
            name = "findByUniqueHistoricId", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.klanten.Klant "
                    + "WHERE historicId == :historicId "),
    @javax.jdo.annotations.Query(
            name = "findByMogelijkDubbel", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.klanten.Klant "
                    + "WHERE mogelijkDubbel == :mogelijkDubbel "),
    @javax.jdo.annotations.Query(
            name = "findByPlaatsContains", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.klanten.Klant "
                    + "WHERE plaats.toLowerCase().indexOf(:plaats.toLowerCase()) >= 0 "),
    @javax.jdo.annotations.Query(
            name = "findByKlantObjectContains", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.klanten.Klant "
                    + "WHERE plaats.toLowerCase().indexOf(:searchString.toLowerCase()) >= 0 "
                    + "|| postcode.toLowerCase().indexOf(:searchString.toLowerCase()) >= 0 "
                    + "|| naam.toLowerCase().indexOf(:searchString.toLowerCase()) >= 0 "
                    + "|| email.toLowerCase().indexOf(:searchString.toLowerCase()) >= 0 "
                    + "|| aantekeningen.toLowerCase().indexOf(:searchString.toLowerCase()) >= 0 ")

})
@javax.jdo.annotations.Unique(name="Klant_UNQ", members = {"uniqueId"})
@DomainObject(
        editing = Editing.DISABLED, autoCompleteRepository = KlantRepository.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Klant implements Locatable, Comparable<Klant>, IMailChimpParty {

    public Klant(){}

    public Klant(
            final String naam,
            final String straat,
            final String postcode,
            final String plaats,
            final String email,
            final String telefoon1,
            final String telefoon2,
            final String aantekeningen
    )  {
        this.naam = naam;
        this.straat = straat;
        this.postcode = postcode;
        this.plaats = plaats;
        this.email = email;
        this.telefoon1 = telefoon1;
        this.telefoon2 = telefoon2;
        this.aantekeningen = aantekeningen;
    }

    public String title(){
        return TitleBuilder.start()
                .withName(getNaam())
                .toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private String naam;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String straat;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String postcode;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String plaats;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String email;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String telefoon1;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String telefoon2;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @PropertyLayout(multiLine = 5)
    private String aantekeningen;

    @Persistent
    @Getter @Setter
    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    private Location location;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Klant> getOpDeKaart(){
        return Arrays.asList(this,this); // hack to force a collection layout ... 2x same obj
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private String uniqueId;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private Integer historicId;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private Boolean mogelijkDubbel;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private Boolean stuurGeenMailing;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Klant aanUit(){
        setStuurGeenMailing(getStuurGeenMailing()==null || !getStuurGeenMailing() ? true : false);
        return this;
    }

    @Getter @Setter
    @Persistent(mappedBy = "klant", dependentElement = "true")
    private SortedSet<Bestelling> bestellingen = new TreeSet<>();

    @Getter @Setter
    @Persistent(mappedBy = "klant", dependentElement = "true")
    @CollectionLayout(sortedBy = Communicatie.CommunicatieComparator.class)
    private SortedSet<Communicatie> communicaties = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Bestelling nieuweBestelling(
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
        return bestellingRepository.nieuweBestelling(
                this, naamTurnster, leeftijd, kledingmaat, lengte, postuur);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Factuur> getFacturen(){
                return factuurRepository.findByKlant(this);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Klant wijzigKlant(
            final String naam,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String email
    ){
        setNaam(naam);
        setEmail(email);
        if (email!=null && !email.equals("")){
            setUniqueId(getEmail().toLowerCase());
        } else {
            setUniqueId(UUID.randomUUID().toString());
        }
        return this;
    }

    public String default0WijzigKlant(){
        return getNaam();
    }

    public String default1WijzigKlant(){
        return getEmail();
    }

    public String validateWijzigKlant(final String naam, final String email){
        if (email!=null) {
            Klant found = klantRepository.findUnique(email);
            if (email != null && found != null) {
                if (!found.equals(this)) {
                    return "Er is al een klant met dit email adres";
                }
            }
        }
        return null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Klant wijzigTelefoon(
            @Parameter(optionality = Optionality.OPTIONAL)
            final String telefoon1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String telefoon2
    ){
        setTelefoon1(telefoon1);
        setTelefoon2(telefoon2);
        return this;
    }

    public String default0WijzigTelefoon(){
        return getTelefoon1();
    }

    public String default1WijzigTelefoon(){
        return getTelefoon2();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Klant wijzigAdres(
            @Parameter(optionality = Optionality.OPTIONAL)
            final String straat,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String postcode,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String plaats
    ){
        setStraat(straat);
        setPostcode(postcode);
        setPlaats(plaats);
        zetPinOpGoogleMaps();
        return this;
    }

    public String default0WijzigAdres(){
        return getStraat();
    }

    public String default1WijzigAdres(){
        return getPostcode();
    }

    public String default2WijzigAdres(){
        return getPlaats();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Klant wijzigAantekening(
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 5)
            final String aantekeningen
    ){
        setAantekeningen(aantekeningen);
        return this;
    }

    public String default0WijzigAantekening(){
        return getAantekeningen();
    }

    @Programmatic
    public Klant zetPinOpGoogleMaps() {
        if (locationLookupService != null) {
            String zoekstring = new String("");
            if (getStraat()!=null){
                zoekstring = zoekstring.concat(getStraat());
            }
            if (getPostcode()!=null){
                zoekstring = zoekstring.concat(", ").concat(getPostcode());
            }
            if (getPlaats()!=null){
                zoekstring = zoekstring.concat(", ").concat(getPlaats());
            }
            setLocation(locationLookupService.lookup(zoekstring));
        }
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public MapsView zetPinOpGoogleMaps(final String adresgegevens){
        setLocation(locationLookupService.lookup(adresgegevens));
        return mapsViewRepository.nieuweMapsView(this);
    }

    public String default0ZetPinOpGoogleMaps(){
        String zoekstring = new String("");
        if (getStraat()!=null){
            zoekstring = zoekstring.concat(getStraat());
        }
        if (getPostcode()!=null){
            zoekstring = zoekstring.concat(", ").concat(getPostcode());
        }
        if (getPlaats()!=null){
            zoekstring = zoekstring.concat(", ").concat(getPlaats());
        }
        return zoekstring;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void verwijder(){
        klantRepository.remove(this);
    }

    public boolean hideVerwijder(){
        if (getBestellingen().size()<1 && getFacturen().size()<1){
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final Klant other) {
        return org.apache.isis.applib.util.ObjectContracts
                .compare(this, other, "uniqueId");
    }

    // Voor MailChimp integratie

    @Programmatic
    @Override
    public String getFirstName() {
        return StringUtils.firstNameOf(getNaam());
    }

    @Programmatic
    @Override
    public String getLastName() {
        return StringUtils.lastNameOf(getNaam());
    }

    @Programmatic
    @Override
    public String getEmailAddress() {
        return getEmail();
    }

    @Programmatic
    @Override
    public Boolean excludeFromLists() {
        return getStuurGeenMailing();
    }

    @Getter
    @Setter
    public static class KlantDto {

        public KlantDto(final Klant klant){

            this.naam = klant.getNaam();
            this.email = klant.getEmail();
            this.straat = klant.getStraat();
            this.postcode = klant.getPostcode();
            this.plaats = klant.getPlaats();
        }

        private String naam;

        private String email;

        private String straat;

        private String postcode;

        private String plaats;

    }

    @Inject
    BestellingRepository bestellingRepository;

    @Inject
    FactuurRepository factuurRepository;

    @Inject
    KlantRepository klantRepository;

    @Inject
    LocationLookupService locationLookupService;

    @Inject
    MapsViewRepository mapsViewRepository;

    @Inject
    CommunicatieRepository communicatieRepository;

    @Inject
    private InstellingenRepository instellingenRepository;

}
