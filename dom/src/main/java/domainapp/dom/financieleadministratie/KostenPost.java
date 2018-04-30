package domainapp.dom.financieleadministratie;

import java.util.List;

import javax.inject.Inject;
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
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "financieleadministratie",
        table = "KostenPost"
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
                        + "FROM domainapp.dom.financieleadministratie.KostenPost "),
        @Query(
                name = "findByNaamContains", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.financieleadministratie.KostenPost "
                        + "WHERE naam.toLowerCase().indexOf(:naam.toLowerCase()) >= 0 "),
        @Query(
                name = "findByNaamAndPost", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.financieleadministratie.KostenPost "
                        + "WHERE naam == :naam && post == :post ")
})
@Unique(name = "KostenPost_naam_post_UNQ", members = { "naam", "post" })
@DomainObject(
        editing = Editing.DISABLED,
        autoCompleteRepository = KostenPostRepository.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class KostenPost implements Comparable<KostenPost> {

    public String title() {
        return TitleBuilder
                .start()
                .withName(getNaam())
                .withName(getPost().name().toLowerCase())
                .toString();
    }

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private String naam;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private BoekingPost post;

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public List<KostenPost> verwijder(){
        kostenPostRepository.remove(this);
        return kostenPostRepository.listAll();
    }

    //region > compareTo, toString
    @Override
    public int compareTo(final KostenPost other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "naam", "post");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "naam", "post");
    }
    //endregion

    @Inject
    private KostenPostRepository kostenPostRepository;

}
