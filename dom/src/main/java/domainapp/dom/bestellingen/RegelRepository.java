package domainapp.dom.bestellingen;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = Regel.class,
        nature = NatureOfService.DOMAIN
)
public class RegelRepository {

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<Regel> listAll() {
        return repositoryService.allInstances(Regel.class);
    }

    @Programmatic
    public List<Regel> findByBestelling(
            final Bestelling bestelling
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Regel.class,
                        "findByBestelling",
                        "bestelling", bestelling));
    }

    @Programmatic
    public List<Regel> findByRegelContains(
            final String searchString
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Regel.class,
                        "findByRegelContains",
                        "searchString", searchString));
    }

    @Programmatic
    public Regel nieuweRegel(final Bestelling bestelling,
            final String artikel,
            final BigDecimal prijs,
            final String opmerking,
            final Integer volgorde) {
        final Regel regel = new Regel();
        regel.setBestelling(bestelling);
        regel.setArtikel(artikel);
        regel.setPrijs(prijs);
        regel.setOpmerking(opmerking);
        regel.setVolgorde(volgorde);
        serviceRegistry.injectServicesInto(regel);
        repositoryService.persist(regel);
        return regel;
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

    @Inject
    private ClockService clockService;
}
