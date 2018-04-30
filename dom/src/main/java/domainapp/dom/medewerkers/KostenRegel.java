package domainapp.dom.medewerkers;

import java.math.BigDecimal;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "financieleadministratie"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByDatumBetweenAndMedewerker", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.medewerkers.KostenRegel "
                        + "WHERE :startDate <= datum  "
                        + "   && datum      <= :endDate "
                        + " && medewerker == :medewerker "
                        + "ORDER BY datum DESC "),
        @Query(
                name = "findByMedewerker", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.medewerkers.KostenRegel "
                        + "WHERE medewerker == :medewerker"),
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class KostenRegel {

    public String title(){
        return "Kosten";
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate datum;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String onderwerp;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BigDecimal bedrag;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Medewerker medewerker;

    @Getter @Setter
    @Column(allowsNull = "true", length = 255)
    private String aantekening;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public KostenRegel wijzig(
            final LocalDate datum,
            final String onderwerp,
            final BigDecimal bedrag,
            @Nullable
            final String aantekening){
        setDatum(datum);
        setOnderwerp(onderwerp);
        setBedrag(bedrag);
        setAantekening(aantekening);
        repositoryService.persistAndFlush(this);
        return this;
    }

    public LocalDate default0Wijzig(){
        return getDatum();
    }

    public String default1Wijzig(){
        return getOnderwerp();
    }

    public BigDecimal default2Wijzig(){
        return getBedrag();
    }

    public String default3Wijzig(){
        return getAantekening();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Saldo verwijder(){
        Medewerker medewerker = getMedewerker();
        repositoryService.removeAndFlush(this);
        return new Saldo(medewerker);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Saldo saldo(){
        return new Saldo(getMedewerker());
    }

    @Inject RepositoryService repositoryService;

}
