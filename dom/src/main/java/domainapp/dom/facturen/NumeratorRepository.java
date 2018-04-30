package domainapp.dom.facturen;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = Numerator.class,
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        named = "Instellingen",
        menuOrder = "100"
)
public class NumeratorRepository {

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Numerator findOrCreateNumerator() {
        Numerator numerator = findByName("factuurNummer");
        if (numerator==null){
            numerator = new Numerator();
            numerator.setName("factuurNummer");
            serviceRegistry.injectServicesInto(numerator);
            repositoryService.persist(numerator);
        }
        return numerator;
    }

    @Programmatic
    public Numerator findByName(final String name) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Numerator.class,
                        "findByName",
                        "name", name)
        );
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

    @Inject
    private ClockService clockService;
}
