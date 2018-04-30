package domainapp.dom.siteservice;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import domainapp.dom.bestelservice.BestellingInvoerViewmodelVanSite;
import domainapp.dom.bestellingen.Postuur;
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
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findNietVerwerkt", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.siteservice.FormulierVanSite "
                        + "WHERE verwerkt != true "),
        @javax.jdo.annotations.Query(
                name = "findVerwerkt", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.siteservice.FormulierVanSite "
                        + "WHERE verwerkt == true "),
        @javax.jdo.annotations.Query(
                name = "findByNummer", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.siteservice.FormulierVanSite "
                        + "WHERE nummer == :nummer ")

})
@Unique(name = "FormulierVanSite_nummer", members = { "nummer" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class FormulierVanSite {

    public FormulierVanSite(){}

    public FormulierVanSite(
            final int nummer,
            final String turnster,
            final String leeftijd,
            final String kledingmaat,
            final String lengte,
            final String postuur,
            final String pakje,
            final String mouwtje,
            final String broekje,
            final String kleurbroekje,
            final String elastiek,
            final String opmerkingen,
            final String naam,
            final String straat,
            final String postcode,
            final String plaats,
            final String email,
            final String telefoon,
            final String vragen,
            final String datum,
            final String pakjeId,
            final String prijsInfo
    ){
        this.nummer = nummer;
        this.turnster = turnster;
        this.leeftijd = leeftijd;
        this.kledingmaat = kledingmaat;
        this.lengte = lengte;
        this.postuur = postuur;
        this.pakje = pakje;
        this.mouwtje = mouwtje;
        this.broekje = broekje;
        this.kleurbroekje = kleurbroekje;
        this.elastiek = elastiek;
        this.opmerkingen = opmerkingen;
        this.naam = naam;
        this.straat = straat;
        this.postcode = postcode;
        this.plaats = plaats;
        this.email = email;
        this.telefoon = telefoon;
        this.vragen = vragen;
        this.datum = datum;
        this.pakjeId = pakjeId;
        this.prijsInfo = prijsInfo;
    }

    public String title(){
        return "Formulier nr. " + getNummer();
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private Integer nummer;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String turnster;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String leeftijd;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String kledingmaat;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String lengte;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String postuur;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String pakje;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String mouwtje;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String broekje;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String kleurbroekje;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String elastiek;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @PropertyLayout(multiLine = 5)
    @Property(hidden = Where.ALL_TABLES)
    private String opmerkingen;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String naam;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String straat;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String postcode;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String plaats;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String telefoon;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String email;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @PropertyLayout(multiLine = 5)
    @Property(hidden = Where.ALL_TABLES)
    private String vragen;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String datum;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String pakjeId;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @Property(editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @PropertyLayout(multiLine = 12)
    private String prijsInfo;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private Boolean verwerkt;

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public BestellingInvoerViewmodelVanSite maakBestelling(){
        return new BestellingInvoerViewmodelVanSite(
                getTurnster(),
                getLeeftijd(),
                getKledingmaat(),
                getLengte(),
                !getPostuur().equals("") ? Postuur.valueOf(getPostuur().toUpperCase()) : null,
                getNaam(),
                getStraat(),
                getPostcode(),
                getPlaats(),
                getEmail(),
                getTelefoon(),
                getVragen(),
                getPakje(),
                getMouwtje(),
                getBroekje().equals("ja") ? true : false,
                getKleurbroekje(),
                getElastiek().equals("ja") ? true : false,
                getOpmerkingen(),
                getPrijsInfo(),
                getNummer());
    }

    @Action(semantics = SemanticsOf.SAFE, invokeOn = InvokeOn.COLLECTION_ONLY)
    public FormulierVanSite open(){
        return this;
    }

    @Action (semantics = SemanticsOf.IDEMPOTENT, invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public List<FormulierVanSite> gooiWeg(){
        this.setVerwerkt(true);
        return formulierVanSiteRepository.nietVerwerkteBestellingenVanSite();
    }

    @Inject
    private FormulierVanSiteRepository formulierVanSiteRepository;
}
