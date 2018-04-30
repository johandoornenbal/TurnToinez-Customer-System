package domainapp.dom.klanten;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = MapsView.class,
        nature = NatureOfService.DOMAIN
)
public class MapsViewRepository {

    @Programmatic
    public MapsView findByKlant(final Klant klant) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        MapsView.class,
                        "findByKlant",
                        "klant", klant));
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public MapsView nieuweMapsView(
            final Klant klant
    ) {
        MapsView view = findByKlant(klant);
        if (view==null) {
            view = create(klant);
        }
        return view;
    }

    private MapsView create(
            final Klant klant){
        MapsView view = new MapsView(klant);
        serviceRegistry.injectServicesInto(view);
        repositoryService.persist(view);
        return view;
    }

    @Programmatic
    public void remove(final MapsView view){
        repositoryService.remove(view);
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;
}
