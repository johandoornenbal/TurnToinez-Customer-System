package domainapp.dom.notities;

import java.lang.reflect.Type;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Query;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import domainapp.dom.klanten.Klant;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "tti"
)
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByKlant", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.notities.NotitieLink "
                        + "WHERE klant == :klant ")
})
@DomainObject(editing = Editing.ENABLED)
public class NotitieLinkForKlant extends NotitieLink {

    public NotitieLinkForKlant(final Notitie notitie, final Klant klant){
        super(notitie);
        this.klant = klant;
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private Klant klant;

    @Override
    public Type getType(){
        return Klant.class;
    }

    @Override
    public Object linkedTo(){
        return getKlant();
    }

}
