package domainapp.dom.financieleadministratie;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = KostenPost.class
)
public class KostenPostRepository {

    public List<KostenPost> autoComplete(final String search){
        return listAll();
    }

    @Programmatic
    public List<KostenPost> listAll() {
        return repositoryService.allInstances(KostenPost.class);
    }

    @Programmatic
    public KostenPost findByNaamAndPost(
            final String naam,
            final BoekingPost boekingPost
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        KostenPost.class,
                        "findByNaamAndPost",
                        "naam", naam, "post", boekingPost));
    }

    @Programmatic
    public List<KostenPost> findByNaamContains(
            final String naam
    ) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        KostenPost.class,
                        "findByNaamContains",
                        "naam", naam));
    }

    @Programmatic
    public KostenPost create(final String naam, final BoekingPost boekingPost) {
        final KostenPost kostenPost = new KostenPost();
        kostenPost.setNaam(naam);
        kostenPost.setPost(boekingPost);
        serviceRegistry2.injectServicesInto(kostenPost);
        repositoryService.persist(kostenPost);
        return kostenPost;
    }

    @Programmatic
    public String validateCreate(final BoekingPost boekingPost){
        return boekingPost==BoekingPost.VERKOOP ? "Een kostenpost kan niet verkoop zijn" : null;
    }

    @Programmatic
    public KostenPost findOrCreate(
            final String naam,
            final BoekingPost boekingPost
    ) {
        KostenPost kostenPost = findByNaamAndPost(naam, boekingPost);
        if (kostenPost == null) {
            kostenPost = create(naam, boekingPost);
        }
        return kostenPost;
    }

    public void remove(final KostenPost kostenPost){
        repositoryService.remove(kostenPost);
    }

    @javax.inject.Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry2;
}
