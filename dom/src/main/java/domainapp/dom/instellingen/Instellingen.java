package domainapp.dom.instellingen;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;

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
@Unique(name = "Instellingen_naam", members = { "naam" })
@DomainObject(
        editing = Editing.ENABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Instellingen {

    public Instellingen(){
        this.naam = "instellingen";
        this.laatsteBestellingVanSite = 930;
        this.boekingAfsluitDatum = new LocalDate(2016,12, 31);
    }

    @Title
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED)
    private String naam;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Integer laatsteBestellingVanSite;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate boekingAfsluitDatum;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @PropertyLayout(multiLine = 18)
    private String standaardEmailTekst;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @PropertyLayout(multiLine = 18)
    private String emailHandtekening;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @PropertyLayout(multiLine = 18)
    private String emailWanneerBetaald;

    @Getter @Setter
    @Column(allowsNull = "true", scale = 2)
    private BigDecimal basisPrijsPakje;

    @Getter @Setter
    @Column(allowsNull = "true", scale = 2)
    private BigDecimal basisKostenPakje;

    @Getter @Setter
    @Column(allowsNull = "true", scale = 2)
    private BigDecimal basisPrijsBroekje;

    @Getter @Setter
    @Column(allowsNull = "true", scale = 2)
    private BigDecimal basisKostenBroekje;

}
