package domainapp.dom.facturen;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.services.DocumentCreatorService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.klanten.Klant;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
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
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.facturen.Factuur "),
        @Query(
                name = "vindFactuurOpNummer", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.facturen.Factuur "
                        + "WHERE nummer == :nummer "),
        @Query(
                name = "findByBestelling", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.facturen.Factuur "
                        + "WHERE bestelling == :bestelling "),
        @Query(
                name = "findByKlant", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.facturen.Factuur "
                        + "WHERE klant == :klant "),
        @javax.jdo.annotations.Query(
                name = "findByUniqueHistoricId", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.facturen.Factuur "
                        + "WHERE historicId == :historicId ")
})
@Unique(name = "Factuur_nummer_UNQ", members = { "nummer" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Factuur implements Comparable<Factuur> {

    public Factuur(){}

    public Factuur(
            final String nummer,
            final Bestelling bestelling,
            final LocalDate datum,
            final BigDecimal bedragInclBtw,
            final BigDecimal bedragExclBtw,
            final BigDecimal btw
    ){
        this.nummer = nummer;
        this.bestelling = bestelling;
        this.klant = bestelling.getKlant();
        this.datum = datum;
        this.bedragInclBtw = bedragInclBtw;
        this.bedragExclBtw = bedragExclBtw;
        this.btw = btw;
    }

    public String title(){
        return getDraft() ? "Tijdelijke factuur" : TitleBuilder
                .start()
                .withName("Factuur nr: ")
                .withName(nummer)
                .toString();
    }

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String nummer;

    @Column(allowsNull = "true")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Bestelling bestelling;

    @Column(allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Klant klant;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private LocalDate datum;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private LocalDate datumBetaald;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private Boolean vervallen;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private Boolean verzonden;

    @Column(allowsNull = "true")
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private Boolean draft;

    @Column(allowsNull = "false", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal bedragInclBtw;

    @Column(allowsNull = "false", scale = 2)
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private BigDecimal bedragExclBtw;

    @Column(allowsNull = "false", scale = 2)
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private BigDecimal btw;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private Integer historicId;

    @Getter @Setter
    @Persistent(mappedBy = "factuur", dependentElement = "true")
    private SortedSet<FactuurRegel> factuurRegels = new TreeSet<>();

    @Action(semantics = SemanticsOf.SAFE)
    public EmailViewModel verstuurViaEmail(
            @ParameterLayout(multiLine = 18)
            final String tekst
    ){
        return new EmailViewModel(this, tekst);
    }

    public String default0VerstuurViaEmail(final String tekst){
        return defaultTextToBeOverridden();
    }

    public String disableVerstuurViaEmail(){
        if (getHistoricId()!=null){
            return "Dit is een geimporteerde factuur en kan niet worden gemaild";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadFactuur(){
        if (getDraft()){
            setNummer(String.valueOf(numeratorRepository.findByName("factuurNummer").nextIncrementStr()));
            setDraft(false);
            createPdfDocumentAndrender();
        }
        return paperclipRepository.findByAttachedTo(this).get(0).getDocument().getBlob();
    }

    public String disableDownloadFactuur(){
        if (getHistoricId()!=null){
            return "Dit is een geimporteerde factuur en kan niet worden gedownload";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Bestelling verwijder(){
        Bestelling bestelling = getBestelling();
        factuurRepository.remove(this);
        return bestelling;
    }

    public String disableVerwijder(){
        if (!getDraft()){
            return "Deze factuur heeft al een nummer en kan niet worden verwijderd";
        }
        if (getHistoricId()!=null){
            return "Dit is een geimporteerde factuur en kan niet worden verwijderd";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Factuur nietBetaald(){
        laatVervallen();
        return this;
    }

    public String disableNietBetaald(){
        if (getDraft()){
            return "Deze factuur heeft nog geen nummer en kan worden verwijderd";
        }
        if (getVervallen()!=null && getVervallen()){
            return "De factuur is vervallen";
        }
        return null;
    }

    @Programmatic
    public Factuur createPdfDocumentAndrender(){
        DocumentTemplate template = documentTemplateRepository.findFirstByTypeAndApplicableToAtPath(documentTypeRepository.findByReference("XDOCREPORT-PDF"),"/");
        Document doc = documentCreatorService.createDocumentAndAttachPaperclips(this, template);
        doc.render(template, this);
        return this;
    }

    @Programmatic
    public String defaultTextToBeOverridden(){
        String defaultTekst = new String();
        defaultTekst = defaultTekst.concat("Beste ").concat(getKlant().getNaam()).concat(",\r\n\r\n");
        if (instellingenRepository.listAll().size()>0 && instellingenRepository.listAll().get(0).getStandaardEmailTekst()!=null){
            defaultTekst = defaultTekst.concat(instellingenRepository.listAll().get(0).getStandaardEmailTekst());
        } else {
            defaultTekst = defaultTekst.concat(defaultEmailTekst());
        }
        return defaultTekst;
    }

    @Programmatic
    private String defaultEmailTekst(){
        String defaultTekst = new String();
        defaultTekst = defaultTekst.concat("Bedankt voor de bestelling.\r\n\r\n");
        defaultTekst = defaultTekst.concat("Bijgevoegd vindt u de factuur. Wanneer de betaling is ontvangen stuur ik het zo spoedig mogelijk op.\r\n\r\n");
        defaultTekst = defaultTekst.concat("Met vriendelijke groet,\r\n\r\nInez Doornenbal\r\n058 2661357");
        defaultTekst = defaultTekst.concat("\r\n\r\n\r\nP.s. Je kunt op mijn facebook (http://www.facebook.com/turnpakjes) zien wanneer ik de bestelling heb verstuurd.");
        return defaultTekst;
    }

    @Programmatic
    public Factuur createRegel(
            final String artikel,
            final BigDecimal prijsIncl,
            final Integer volgorde){
        factuurRegelRepository.nieuweRegel(this, artikel, prijsIncl, volgorde);
        return this;
    }

    public static class FactuurVervallenEvent extends ActionDomainEvent<Factuur>{}
    @Programmatic
    public void laatVervallen() {
        setBestelling(null);
        setVervallen(true);
        FactuurVervallenEvent event = new FactuurVervallenEvent();
        event.setSource(this);
        eventBusService.post(event);
    }

    @Getter
    public static class FactuurDto {

        public FactuurDto(final Factuur factuur){
            this.nummer = factuur.getNummer();
            this.bedragInclBtw = factuur.getBedragInclBtw();
            this.bedragExclBtw = factuur.getBedragExclBtw();
            this.btw = factuur.getBtw();
            this.datum = factuur.getDatum().toString("dd-MM-yyyy");
        }

        String nummer;

        String datum;

        BigDecimal bedragInclBtw;

        BigDecimal bedragExclBtw;

        BigDecimal btw;

    }

    //region > compareTo, toString
    @Override
    public int compareTo(final Factuur other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "nummer");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "nummer");
    }
    //endregion

    @Inject
    private FactuurRegelRepository factuurRegelRepository;

    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    private NumeratorRepository numeratorRepository;

    @Inject
    private FactuurRepository factuurRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;

    @Inject
    private DocumentTypeRepository documentTypeRepository;

    @Inject
    private DocumentCreatorService documentCreatorService;

    @Inject
    private EventBusService eventBusService;

    @Inject
    InstellingenRepository instellingenRepository;

}
