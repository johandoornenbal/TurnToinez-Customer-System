package domainapp.dom.facturen;

import java.math.BigDecimal;
import java.util.Comparator;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

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
                name = "findByFactuur", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.facturen.FactuurRegel "
                        + "WHERE factuur == :factuur ")
})
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class FactuurRegel implements Comparable<FactuurRegel>, Comparator<FactuurRegel> {

    public String title(){
        return TitleBuilder
                .start()
                .withParent(getFactuur())
                .withName(getVolgorde())
                .toString();
    }

    @Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED, hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Factuur factuur;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private int volgorde;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String artikel;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal prijsInclBtw;

    @Getter
    public static class FactuurRegelDto {

        public FactuurRegelDto(final FactuurRegel regel){
             this.artikel = regel.getArtikel();
             this.prijsInclBtw = regel.getPrijsInclBtw();
        }

        private String artikel;
        private BigDecimal prijsInclBtw;

    }

    //region > compareTo, toString
    @Override
    public int compareTo(final FactuurRegel other) {
        return org.apache.isis.applib.util.ObjectContracts
                .compare(this, other, "factuur", "volgorde", "artikel", "prijsInclBtw");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "factuur", "artikel", "volgorde");
    }

    @Override public int compare(final FactuurRegel o1, final FactuurRegel o2) {
        return o1.compareTo(o2);
    }
    //endregion

}
