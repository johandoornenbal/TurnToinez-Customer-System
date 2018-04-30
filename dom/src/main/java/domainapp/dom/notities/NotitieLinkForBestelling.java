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

import domainapp.dom.bestellingen.Bestelling;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "tti"
)
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByBestelling", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.notities.NotitieLink "
                        + "WHERE bestelling == :bestelling ")
})
@DomainObject(editing = Editing.ENABLED)
public class NotitieLinkForBestelling extends NotitieLink {

    public NotitieLinkForBestelling(final Notitie notitie, final Bestelling bestelling){
        super(notitie);
        this.bestelling = bestelling;
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private Bestelling bestelling;

    @Override
    public Type getType(){
        return Bestelling.class;
    }

    @Override
    public Object linkedTo(){
        return getBestelling();
    }

}
