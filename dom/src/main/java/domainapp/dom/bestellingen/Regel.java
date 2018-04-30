package domainapp.dom.bestellingen;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;

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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.utils.TitleBuilder;

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
                name = "findByBestelling", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.bestellingen.Regel "
                        + "WHERE bestelling == :bestelling "),
        @Query(
                name = "findByRegelContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.bestellingen.Regel "
                        + "WHERE artikel.toLowerCase().indexOf(:searchString.toLowerCase()) >= 0 "
                        + "|| opmerking.toLowerCase().indexOf(:searchString.toLowerCase()) >= 0 ")
})
@DomainObject()
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Regel implements Comparable<Regel>, Comparator<Regel> {

    public String title(){
        return TitleBuilder
                .start()
                .withParent(getBestelling())
                .withName(getVolgorde())
                .toString();
    }

    @Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED, hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Bestelling bestelling;

    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private Integer volgorde;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String artikel;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal prijs;

    @Getter @Setter
    @Column(allowsNull = "true", length = 2048)
    @PropertyLayout(multiLine = 5)
    private String opmerking;

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Bestelling verwijderRegel(){
        Bestelling bestelling = getBestelling();
        repositoryService.removeAndFlush(this);
        return bestelling;
    }

    public String disableVerwijderRegel(){
        if (Arrays.asList(Status.BETAALD, Status.KLAAR).contains(getBestelling().getStatus())){
            return String.format("Je kunt de regel niet verwijderen want de status is %s", getBestelling().getStatus());
        }
        if (getBestelling().getFactuur()!= null){
            return "Verwijder eerst de factuur op de bestelling";
        }
        return null;
    }

    //region > compareTo, toString
    @Override
    public int compareTo(final Regel other) {
        return org.apache.isis.applib.util.ObjectContracts
                .compare(this, other, "bestelling", "volgorde", "artikel", "prijs", "opmerking");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "bestelling", "artikel", "volgorde");
    }

    @Override public int compare(final Regel o1, final Regel o2) {
        return o1.compareTo(o2);
    }
    //endregion

    @Inject RepositoryService repositoryService;

}
