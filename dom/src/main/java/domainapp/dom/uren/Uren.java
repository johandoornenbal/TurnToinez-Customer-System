package domainapp.dom.uren;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

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
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.uren.Uren "
                        + "WHERE datum == :datum "),
        @Query(
                name = "findByDatumBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.uren.Uren "
                        + "WHERE :startDate <= datum  "
                        + "   && datum      <= :endDate "
                        + "ORDER BY datum DESC ")
})
@javax.jdo.annotations.Unique(name="Uren_UNQ", members = {"datum"})
@DomainObject(editing = Editing.ENABLED)
public class Uren {

    public Uren(){}

    public Uren(final LocalDate datum, final int uren){
        this.datum = datum;
        this.uren = uren;
    }

    public String title(){return "Uren ".concat(getDatum().toString("dd-MM-yyyy")).concat(": ").concat(String.valueOf(getUren()));}

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate datum;

    @Getter @Setter
    @Column(allowsNull = "false")
    private int uren;

    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    private Integer historicId;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Uren schrijfUren(final LocalDate datum, final int aantalUren){
        return urenRepository.schrijfUren(datum, aantalUren);
    }

    public LocalDate default0SchrijfUren(){
        return urenRepository.default0SchrijfUren();
    }

    public int default1SchrijfUren(){
        return urenRepository.default1SchrijfUren();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, named = "Andere uren boekingen")
    public List<Uren> getAndereUren(){
        return urenRepository.findByDatumBetween(getDatum().minusMonths(1), getDatum().plusMonths(1))
                .stream()
                .filter(x->x.getDatum()!=getDatum())
                .collect(Collectors.toList());
    }

    @Inject
    UrenRepository urenRepository;

}
