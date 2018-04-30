package domainapp.dom.notities;

import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.services.repository.RepositoryService;

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
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME
)
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByNotitie", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.notities.NotitieLink "
                        + "WHERE notitie == :notitie ")
})
@DomainObject(editing = Editing.ENABLED)
public abstract class NotitieLink {

    public NotitieLink(final Notitie notitie){
        this.notitie = notitie;
    }

    public String title(){return "NotitieLink"; }

    @Getter @Setter
    @Column(allowsNull = "false")
    private Notitie notitie;

    public abstract Type getType();

    public abstract Object linkedTo();

    public void remove(){
        repositoryService.removeAndFlush(this);
    }

    @Inject
    private RepositoryService repositoryService;

}
