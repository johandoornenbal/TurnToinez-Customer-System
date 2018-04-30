package domainapp.dom.notities;

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
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.value.Blob;

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
        @Query(
                name = "findByNotitieContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.notities.Notitie "
                        + "WHERE notitie.toLowerCase().indexOf(:notitie.toLowerCase()) >= 0 "),
        @Query(
                name = "findByDatumBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.notities.Notitie "
                        + "WHERE :startDate <= datum  "
                        + "   && datum      <= :endDate "
                        + "ORDER BY datum DESC ")
})
@DomainObject(editing = Editing.ENABLED)
public class Notitie {

    public Notitie(){}

    public Notitie(final LocalDate datum, final String notitie, final Blob foto1, final Blob foto2){
        this.datum = datum;
        this.notitie = notitie;
        this.foto1 = foto1;
        this.foto2 = foto2;
    }

    public String title(){return "Notitie ".concat(getDatum().toString("dd-MM-yyyy")).concat(": ");}

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate datum;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(multiLine = 8)
    private String notitie;

    @Getter @Setter
    @javax.jdo.annotations.Persistent(defaultFetchGroup="false", columns = {
            @javax.jdo.annotations.Column(name = "foto1_naam"),
            @javax.jdo.annotations.Column(name = "foto1_mimetype"),
            @javax.jdo.annotations.Column(name = "foto1_bytes", jdbcType = "BLOB", sqlType = "LONGVARBINARY")
    })
    @Property(optionality= Optionality.OPTIONAL)
    private Blob foto1;

    @Getter @Setter
    @javax.jdo.annotations.Persistent(defaultFetchGroup="false", columns = {
            @javax.jdo.annotations.Column(name = "foto2_naam"),
            @javax.jdo.annotations.Column(name = "foto2_mimetype"),
            @javax.jdo.annotations.Column(name = "foto2_bytes", jdbcType = "BLOB", sqlType = "LONGVARBINARY")
    })
    @Property(optionality= Optionality.OPTIONAL)
    private Blob foto2;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, hidden = Where.PARENTED_TABLES)
    public Object getHoortBij(){
        return notitieLinkRepository.findByNotitie(this).linkedTo();
    }

    @Inject
    NotitieLinkRepository notitieLinkRepository;

}
