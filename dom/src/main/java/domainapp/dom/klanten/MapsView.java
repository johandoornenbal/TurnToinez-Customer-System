package domainapp.dom.klanten;

import java.util.Arrays;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.SemanticsOf;

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
                name = "findByKlant", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.klanten.MapsView "
                        + "WHERE klant == :klant ")
})
@DomainObject(editing = Editing.DISABLED, auditing = Auditing.DISABLED)
public class MapsView {

    public MapsView(final Klant klant){
        this.klant = klant;
    }

    public String title(){return getKlant().getNaam().concat(" op Google maps");}

    @Getter @Setter
    @Column(allowsNull = "false")
    private Klant klant;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Klant> getGoogleMaps(){
        return Arrays.asList(getKlant(), getKlant()); // hack to force a collection layout ... 2x same obj
    }

}
