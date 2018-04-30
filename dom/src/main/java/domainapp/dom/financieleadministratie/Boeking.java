package domainapp.dom.financieleadministratie;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

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
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.base.dom.utils.TitleBuilder;

import domainapp.dom.facturen.Btw;
import domainapp.dom.facturen.Factuur;
import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.utils.CalcUtils;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "financieleadministratie",
        table = "Boeking"
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
                        + "FROM domainapp.dom.financieleadministratie.Boeking "),
        @Query(
                name = "findByDatumBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.financieleadministratie.Boeking "
                        + "WHERE :startDate <= datum  "
                        + "   && datum      <= :endDate "
                        + "ORDER BY datum DESC "),
        @Query(
                name = "findByTypeAndDatumBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.financieleadministratie.Boeking "
                        + "WHERE :startDate <= datum  "
                        + "   && datum      <= :endDate "
                        + " && type == :type "
                        + "ORDER BY datum DESC "),
        @Query(
                name = "findByFactuur", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.financieleadministratie.Boeking "
                        + "WHERE factuur == :factuur"),
        @javax.jdo.annotations.Query(
                name = "findByUniqueHistoricId", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.financieleadministratie.Boeking "
                        + "WHERE historicId == :historicId "),
        @javax.jdo.annotations.Query(
                name = "findLaterBijwerken", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.financieleadministratie.Boeking "
                        + "WHERE laterBijwerken == true ")
})
@Unique(name = "Boeking_timestamp_UNQ", members = { "timestamp" })
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Boeking implements Comparable<Boeking> {

    public Boeking(){}

    public Boeking(
            final LocalDate datum,
            final String timestamp,
            final BoekingType type,
            final String omschrijving,
            final BigDecimal prijsIncl,
            final BigDecimal prijsExcl,
            final BigDecimal btw,
            final BoekingPost boekingPost,
            final KostenPost kostenPost,
            final Factuur factuur,
            final boolean laterBijwerken
            ){
        this.datum = datum;
        this.timestamp = timestamp;
        this.type = type;
        this.omschrijving = omschrijving;
        this.prijsIncl = prijsIncl;
        this.prijsExcl = prijsExcl;
        this.btw = btw;
        this.post = boekingPost;
        this.kostenPost = kostenPost;
        this.factuur = factuur;
        this.laterBijwerken = laterBijwerken;
    }

    public String title(){
        return TitleBuilder
                .start()
                .withName(getDatum().toString("dd-MM-yyyy"))
                .withName(" - ")
                .withName(getType())
                .withName(" - ")
                .withName(getOmschrijving())
                .toString();
    }

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private LocalDate datum;

    @Column(allowsNull = "false")
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private String timestamp;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private BoekingType type;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String omschrijving;

    @Column(allowsNull = "true", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal prijsIncl;

    @Column(allowsNull = "true", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal prijsExcl;

    @Column(allowsNull = "true", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal btw;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private BoekingPost post;

    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private KostenPost kostenPost;

    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Factuur factuur;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private boolean laterBijwerken;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private Integer historicId;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.ALL_TABLES)
    private String gemigreerdePost;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Boeking werkBij(
            final BigDecimal prijsIncl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsExcl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal btw,
            final boolean berekenBtw){
        setPrijsIncl(prijsIncl);
        setPrijsExcl(prijsExcl);
        setBtw(btw);
        setLaterBijwerken(false);
        return berekenBtw ? this.bereken() : this;
    }

    public BigDecimal default0WerkBij(){
        return getPrijsIncl();
    }

    public BigDecimal default1WerkBij(){
        return getPrijsExcl();
    }

    public BigDecimal default2WerkBij(){
        return getBtw();
    }

    public String validateWerkBij(
            final BigDecimal prijsIncl,
            final BigDecimal prijsExcl,
            final BigDecimal btw,
            final boolean berekenBtw){
        return boekingRepository.validate(berekenBtw, prijsExcl, btw);
    }

    public boolean hideWerkBij(){
        return !isLaterBijwerken();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public List<Boeking> verwijder(){
        LocalDate dateBoeking = getDatum();
        boekingRepository.remove(this);
        return boekingRepository.findByDatumBetween(dateBoeking.minusMonths(1), clockService.now());
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION,
            named = "Andere boekingen van hetzelfde type (+/- 2 mnd.)")
    @CollectionLayout(paged = 200)
    public List<Boeking> getAndereBoekingen(){
        return boekingRepository.findByTypeAndDatumBetween(getType(), getDatum().minusMonths(2), getDatum().plusMonths(2))
                .stream()
                .filter(x->x.getTimestamp()!=getTimestamp())
                .collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Boeking nieuweOnkosten(
            final LocalDate datum,
            final String omschrijving,
            final BigDecimal prijsIncl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsExcl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal btw,
            final KostenPost kostenPost,
            @Parameter(optionality = Optionality.OPTIONAL)
            final boolean berekenBtw,
            @Parameter(optionality = Optionality.OPTIONAL)
            final boolean laterBijwerken
    ){
        BoekingPost post = kostenPost.getPost();
        Boeking newBoeking = boekingRepository.create(datum, BoekingType.UIT, omschrijving, prijsIncl, prijsExcl, btw, post, kostenPost, null, laterBijwerken);
        return berekenBtw ? newBoeking.bereken() : newBoeking;
    }

    public LocalDate default0NieuweOnkosten(
    ){
         return clockService.now();
    }

    public boolean default6NieuweOnkosten(){
        return true;
    }

    public List<KostenPost> choices5NieuweOnkosten(){
        return kostenPostRepository.listAll();
    }

    public String validateNieuweOnkosten(
            final LocalDate datum,
            final String omschrijving,
            final BigDecimal prijsIncl,
            final BigDecimal prijsExcl,
            final BigDecimal btw,
            final KostenPost kostenPost,
            final boolean berekenBtw,
            final boolean laterBijwerken
    ){
        if (validateDate(datum)!=null){
            return validateDate(datum);
        }
        return boekingRepository.validate(berekenBtw, prijsExcl, btw);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Boeking wijzig(
            final LocalDate datum,
            final String omschrijving,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsIncl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsExcl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal btw,
            final KostenPost kostenPost,
            @Parameter(optionality = Optionality.OPTIONAL)
            final boolean laterBijwerken,
            @Parameter(optionality = Optionality.OPTIONAL)
            final boolean berekenBtw
    ){
         setDatum(datum);
         setOmschrijving(omschrijving);
         setPrijsIncl(prijsIncl);
         setPrijsExcl(prijsExcl);
         setBtw(btw);
         setKostenPost(kostenPost);
         setLaterBijwerken(laterBijwerken);
         return berekenBtw ? this.bereken() : this;
    }

    public LocalDate default0Wijzig(){
        return getDatum();
    }

    public String default1Wijzig(){
        return getOmschrijving();
    }

    public BigDecimal default2Wijzig(){
        return getPrijsIncl();
    }

    public BigDecimal default3Wijzig(){
        return getPrijsExcl();
    }

    public BigDecimal default4Wijzig(){
        return getBtw();
    }

    public KostenPost default5Wijzig(){
        return getKostenPost();
    }

    public boolean default6Wijzig(){
        return isLaterBijwerken();
    }

    public boolean default7Wijzig(){
        return true;
    }

    public String validateWijzig(
            final LocalDate datum,
            final String omschrijving,
            final BigDecimal prijsIncl,
            final BigDecimal prijsExcl,
            final BigDecimal btw,
            final KostenPost kostenPost,
            final boolean laterBijwerken,
            final boolean berekenBtw){
        if (prijsIncl==null && prijsExcl==null && btw==null){
            return "Er moet een prijs en/of btw ingevuld worden.";
        }
        if (validateDate(datum)!=null){
            return validateDate(datum);
        }
        return boekingRepository.validate(berekenBtw, prijsIncl, btw);
    }

    String validateDate(final LocalDate date){
        if (date.isBefore(instellingenRepository.instellingen().getBoekingAfsluitDatum().plusDays(1))){
            return "De datum mag niet in een afgesloten periode liggen";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Boeking bereken(){
        calculateAmounts();
        return this;
    }

    public String validateBereken(){
        return validateDate(getDatum());
    }

    void calculateAmounts(){
        if (getPrijsIncl()!=null){
            setPrijsExcl(CalcUtils.netFromGross(getPrijsIncl(), Btw.BTW_21_PERC));
            setBtw(CalcUtils.vatFromGross(getPrijsIncl(), Btw.BTW_21_PERC));
        } else {
            if (getPrijsExcl() != null){
                setPrijsIncl(CalcUtils.grossFromNet(getPrijsExcl(), Btw.BTW_21_PERC));
                setBtw(CalcUtils.vatFromNet(getPrijsExcl(), Btw.BTW_21_PERC));
            } else {
                if (getBtw() != null) {
                    setPrijsExcl(getBtw().multiply(new BigDecimal("100").divide(Btw.BTW_21_PERC.getPercentage(), MathContext.DECIMAL64)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    setPrijsIncl(getPrijsExcl().add(getBtw()).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }
        }
    }

    public String getNummer(){
        return JDOHelper.getObjectId(this).toString().split("\\[OID]")[0];
    }

    //region > compareTo, toString
    @Override
    public int compareTo(final Boeking other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "timestamp", "datum");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "timestamp", "post", "prijsIncl");
    }
    //endregion

    @Inject
    BoekingRepository boekingRepository;

    @Inject
    private ClockService clockService;

    @Inject
    private KostenPostRepository kostenPostRepository;

    @Inject
    InstellingenRepository instellingenRepository;

}
