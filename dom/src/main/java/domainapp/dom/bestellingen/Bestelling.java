package domainapp.dom.bestellingen;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.validation.constraints.Digits;

import org.joda.time.LocalDate;

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
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.base.dom.utils.TitleBuilder;

import domainapp.dom.communicatie.CommunicatieService;
import domainapp.dom.facturen.Factuur;
import domainapp.dom.facturen.FactuurRepository;
import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.klanten.Klant;
import domainapp.dom.medewerkers.Medewerker;
import domainapp.dom.medewerkers.VerdienRegel;
import domainapp.dom.medewerkers.VerdienRegelRepository;
import domainapp.dom.notities.NotitieLink;
import domainapp.dom.notities.NotitieLinkRepository;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "tti"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByKlant", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.bestellingen.Bestelling "
                        + "WHERE klant == :klant "),
        @Query(
                name = "findByNaamTurnsterContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.bestellingen.Bestelling "
                        + "WHERE naamTurnster.toLowerCase().indexOf(:naamTurnster.toLowerCase()) >= 0 "
                        + "ORDER BY datumBestelling"),
        @Query(
                name = "zoekOpStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.bestellingen.Bestelling "
                        + "WHERE status == :status ORDER BY datumBestelling DESC"),
        @javax.jdo.annotations.Query(
                name = "findByUniqueHistoricId", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.bestellingen.Bestelling "
                        + "WHERE historicId == :historicId ")
})
@DomainObject()
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Bestelling implements Comparable<Bestelling> {

    public String title(){
        String dateString = getDatumBestelling()!=null ? getDatumBestelling().toString("dd-MM-yyyy") : null;
        return TitleBuilder
                .start()
                .withParent(getKlant())
                .withName(getNaamTurnster()!=null ? "(" + getNaamTurnster() + (") ") : null)
                .withName(dateString)
                .toString();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public String getSamenvatting(){
        String summary = new String();
        boolean first = true;
        for (Regel regel : getRegels()){
            if (first){
                first=false;
            } else {
                summary = summary.concat(" | ");
            }
            summary = summary.concat(regel.getArtikel());
        }
        return summary;
    }

    @Column(allowsNull = "true")
    @Property(editing = Editing.DISABLED, hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Klant klant;

    @Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private Status status;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private String naamTurnster;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String leeftijd;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String kledingmaat;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String lengte;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED, hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Postuur postuur;

    @Column(allowsNull = "false")
    @Property(hidden = Where.ALL_TABLES, editing = Editing.DISABLED)
    @Getter @Setter
    private String timestamp;

    @Column(allowsNull = "true")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private LocalDate datumBestelling;

    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES, editing = Editing.DISABLED)
    @Getter @Setter
    private LocalDate datumBetaald;

    @Column(allowsNull = "true")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private LocalDate datumKlaar;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private Medewerker gemaaktDoor;

    public boolean hideGemaaktDoor(){
        return getStatus()!=Status.KLAAR;
    }

    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Integer historicId;

    @Getter @Setter
    @Persistent(mappedBy = "bestelling", dependentElement = "true")
    @CollectionLayout(sortedBy = Regel.class)
    private SortedSet<Regel> regels = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Bestelling nieuweRegel(
            final String artikel,
            final BigDecimal prijs,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 5)
            final String opmerking,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Integer volgorde
    ){
        regelRepository.nieuweRegel(this, artikel, prijs, opmerking, volgorde);
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Bestelling wijzigKlant(final Klant klant){
        setKlant(klant);
        for (Factuur factuur : factuurRepository.findByBestelling(this)){
            factuur.setKlant(klant);
        }
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public void verwijder(){
        Factuur factuur = getFactuur();
        if (factuur!=null){
            factuur.laatVervallen();
        }
        for (NotitieLink notitieLink : notitieLinkRepository.findByBestelling(this)){
            notitieLink.remove();
        }
        bestellingRepository.remove(this);
    }

    public String disableVerwijder(){
        if (getStatus() == Status.KLAAR){
            return "Een bestelling met status klaar kan niet verwijderd worden";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Factuur besteld(){
        if (getStatus()==Status.KLAD && getKlant()!=null){
            setStatus(Status.BESTELLING);
            setDatumBestelling(clockService.now());
            return createFactuurForBestelling();
        }
        return null;
    }

    public String disableBesteld(){
        if (getKlant()==null){
            return "Er is nog geen klant.";
        }
        if (getStatus()!=Status.KLAD){
            return "Werkt alleen als de status van de bestelling KLAD is.";
        }
        return null;
    }

    public static class BestellingBetaaldEvent extends ActionDomainEvent<Bestelling> {}
    @Action(semantics = SemanticsOf.IDEMPOTENT, domainEvent = Bestelling.BestellingBetaaldEvent.class)
    public Bestelling betaald(final Boolean stuurEmail) throws IllegalArgumentException {
        if (getStatus()==Status.BESTELLING){
            setStatus(Status.BETAALD);
            setDatumBetaald(clockService.now());
            if (getFactuur()!=null) {
                getFactuur().setDatumBetaald(clockService.now());
                if (stuurEmail) {
                    communicatieService.sendEmailWhenOrderToPaid(this);
                }
            } else {
                throw new IllegalArgumentException("FOUT: geen factuur gevonden voor deze bestelling.");
            }

        }
        return  this;
    }

    public Boolean default0Betaald(){
        return true;
    }

    public String disableBetaald(){
        if (getStatus()!=Status.BESTELLING){
            return "Werkt alleen als de status van de bestelling BESTELLING is.";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Bestelling klaar(
            final Medewerker maker,
            @Nullable
            final String omschrijving,
            @Nullable
            @Digits(integer = 10, fraction = 2)
            final BigDecimal verkoopPrijs,
            @Nullable
            @Digits(integer = 10, fraction = 2)
            final BigDecimal kosten,
            @Nullable
            final Integer percentage,
            @Nullable
            final String aantekening
    ){
        if (getStatus()==Status.BETAALD){
            setStatus(Status.KLAAR);
            setGemaaktDoor(maker);
            setDatumKlaar(clockService.now());
        }
        if (maker != Medewerker.INEZ) {
            VerdienRegel verdienRegel = verdienRegelRepository.create(
                    clockService.now(),
                    omschrijving,
                    verkoopPrijs,
                    kosten,
                    percentage,
                    aantekening,
                    maker == Medewerker.INEZ_EN_GRIETJE ? Medewerker.GRIETJE : maker
            );
            verdienRegel.setBestelling(this);
        } else {
            messageService.informUser("Geen verdienste aangemaakt voor " + maker.name()) ;
        }
        return  this;
    }

    public Medewerker default0Klaar(){
        return Medewerker.INEZ;
    }

    public String default1Klaar(){
        return getSamenvatting().replace("| Verzendkosten", "");
    }

    public BigDecimal default2Klaar(){
        if (getSamenvatting().toLowerCase().contains("pak") && getSamenvatting().toLowerCase().contains("broek")){
            return instellingenRepository.instellingen().getBasisPrijsPakje().add(instellingenRepository.instellingen().getBasisPrijsBroekje());
        }
        if (getSamenvatting().toLowerCase().contains("pak")){
            return instellingenRepository.instellingen().getBasisPrijsPakje();
        }
        if (getSamenvatting().toLowerCase().contains("broek")){
            return instellingenRepository.instellingen().getBasisPrijsBroekje();
        }
        return null;
    }

    public BigDecimal default3Klaar(){
        if (getSamenvatting().toLowerCase().contains("pak") && getSamenvatting().toLowerCase().contains("broek")){
            return instellingenRepository.instellingen().getBasisKostenPakje().add(instellingenRepository.instellingen().getBasisKostenBroekje());
        }
        if (getSamenvatting().toLowerCase().contains("pak")){
            return instellingenRepository.instellingen().getBasisKostenPakje();
        }
        if (getSamenvatting().toLowerCase().contains("broek")){
            return instellingenRepository.instellingen().getBasisKostenBroekje();
        }
        return null;
    }

    public List<Integer> choices4Klaar(){
        return Arrays.asList(20,40,60,80);
    }

    public String validateKlaar(
            final Medewerker maker,
            final String omschrijving,
            final BigDecimal verkoopPrijs,
            final BigDecimal kosten,
            final Integer percentage,
            final String aantekening
    ){
        if (maker!=Medewerker.INEZ){
            if (!verdiensteIsCompleetVoor(omschrijving, verkoopPrijs, kosten, percentage)){
                return "Verdienste moet compleet worden ingevuld: omschrijving, verkoopprijs, kosten en percentage";
            }
        }
        return null;
    }

    private boolean verdiensteIsCompleetVoor(
            final String omschrijving,
            final BigDecimal verkoopPrijs,
            final BigDecimal kosten,
            final Integer percentage) {
        return verkoopPrijs != null && omschrijving != null && kosten != null && percentage != null;
    }


    public String disableKlaar(){
        if (getStatus()!=Status.BETAALD){
            return "Werkt alleen als de status van de bestelling BETAALD is.";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Factuur maakFactuur(){
        return createFactuurForBestelling();
    }

    public boolean hideMaakFactuur(){
        if (getFactuur()==null && getStatus()!=Status.KLAD){
            return false;
        }
        return true;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal getTotaalBedrag(){
        BigDecimal result = BigDecimal.ZERO;
        for (Regel regel : getRegels()){
            result = result.add(regel.getPrijs());
        }
        return result.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Programmatic
    public Factuur createFactuurForBestelling(){
        return factuurRepository.createFactuur(this);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Factuur getFactuur()  {
        return factuurRepository.findByBestellingAndNotVervallen(this);
    }

    //region > compareTo, toString
    @Override
    public int compareTo(final Bestelling other) {
        return org.apache.isis.applib.util.ObjectContracts
                .compare(this, other, "timestamp");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "klant", "timestamp");
    }
    //endregion

    @PropertyLayout(hidden = Where.ALL_TABLES)
    public String getEmail(){
        return getKlant() == null ? null : getKlant().getEmail();
    }

    @PropertyLayout(hidden = Where.ALL_TABLES)
    public String getStraat(){
        return getKlant() == null ? null : getKlant().getStraat();
    }

    @PropertyLayout(hidden = Where.ALL_TABLES)
    public String getPostcode(){
        return getKlant() == null ? null : getKlant().getPostcode();
    }

    @PropertyLayout(hidden = Where.ALL_TABLES)
    public String getPlaats(){
        return getKlant() == null ? null : getKlant().getPlaats();
    }

    @PropertyLayout(hidden = Where.ALL_TABLES)
    public String getTelefoon1(){
        return getKlant() == null ? null : getKlant().getTelefoon1();
    }

    @PropertyLayout(hidden = Where.ALL_TABLES)
    public String getTelefoon2(){
        return getKlant() == null ? null : getKlant().getTelefoon2();
    }

    @Inject
    private RegelRepository regelRepository;

    @Inject
    private ClockService clockService;

    @Inject
    private FactuurRepository factuurRepository;

    @Inject
    private BestellingRepository bestellingRepository;

    @Inject
    private CommunicatieService communicatieService;

    @Inject
    private NotitieLinkRepository notitieLinkRepository;

    @Inject
    VerdienRegelRepository verdienRegelRepository;

    @Inject
    InstellingenRepository instellingenRepository;

    @Inject
    MessageService messageService;

}
