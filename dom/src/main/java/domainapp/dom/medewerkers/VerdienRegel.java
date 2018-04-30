package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

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
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.facturen.Btw;
import domainapp.dom.utils.CalcUtils;
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
                        + "FROM domainapp.dom.medewerkers.VerdienRegel "
                        + "WHERE :startDate <= datum  "
                        + "   && datum      <= :endDate "
                        + " && medewerker == :medewerker "
                        + "ORDER BY datum DESC "),
        @Query(
                name = "findByMedewerker", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.medewerkers.VerdienRegel "
                        + "WHERE medewerker == :medewerker"),
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class VerdienRegel {

    public String title(){
        return "Verdienste";
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate datum;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String onderwerp;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BigDecimal prijs;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BigDecimal kosten;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Integer percentage;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BigDecimal verdienste;

    @Getter @Setter
    @Column(allowsNull = "false")
    private Medewerker medewerker;

    @Getter @Setter
    @Column(allowsNull = "true", length = 255)
    private String aantekening;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Bestelling bestelling;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public VerdienRegel wijzig(
            final LocalDate datum,
            final String onderwerp,
            final BigDecimal prijs,
            final BigDecimal kosten,
            final Integer percentage,
            @Nullable
            final String aantekening
    ){
        setDatum(datum);
        setOnderwerp(onderwerp);
        setPrijs(prijs);
        setKosten(kosten);
        setPercentage(percentage);
        setAantekening(aantekening);
        calcVerdienste();
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
        return getPrijs();
    }

    public BigDecimal default3Wijzig(){
        return getKosten();
    }

    public Integer default4Wijzig(){
        return getPercentage();
    }

    public String default5Wijzig(){
        return getAantekening();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public VerdienRegel gekoppeldAan(@Nullable final Bestelling bestelling){
        setBestelling(bestelling);
        return this;
    }

    public Bestelling default0GekoppeldAan(){
        return getBestelling();
    }

    public List<Bestelling> choices0GekoppeldAan(
            final Bestelling bestelling){
        return bestellingRepository.laatstGemaakteBestellingen();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public BigDecimal getBtw(){
        return CalcUtils.vatFromGross(getPrijs(), Btw.BTW_21_PERC);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public BigDecimal getPrijsExBtw(){
        return CalcUtils.netFromGross(getPrijs(), Btw.BTW_21_PERC);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public BigDecimal getMarge(){
        return getPrijsExBtw().subtract(getKosten());
    }

    @Programmatic
    public void calcVerdienste(){
        setVerdienste(getPercentage()!=null ? getMarge().multiply(BigDecimal.valueOf(getPercentage()).divide(new BigDecimal("100"), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
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

    @Inject
    RepositoryService repositoryService;

    @Inject
    BestellingRepository bestellingRepository;

}
