package domainapp.dom.facturen;

import java.math.BigInteger;

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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
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
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.facturen.Numerator "
                        + "WHERE name == :name ")
})
@Unique(name = "Numerator_name_UNQ", members = { "name" })
@DomainObject(editing = Editing.DISABLED )
public class Numerator {

    @Column(allowsNull = "false")
    @Getter @Setter
    private String name;

    @Column(allowsNull = "true")
    @Getter @Setter
    private BigInteger lastIncrement;

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Numerator changeParameters(
            final BigInteger lastIncrement
    ) {
        setLastIncrement(lastIncrement);
        return this;
    }

    public BigInteger default0ChangeParameters() {
        return getLastIncrement();
    }

    @Programmatic
    public BigInteger nextIncrementStr() {
        return incrementCounter();
    }

    @Programmatic
    public BigInteger lastIncrementStr(){
        return getLastIncrement();
    }

    private BigInteger incrementCounter() {
        BigInteger last = getLastIncrement();
        if (last == null) {
            last = BigInteger.ZERO;
        }
        BigInteger next = last.add(BigInteger.ONE);
        setLastIncrement(next);
        return next;
    }

}
