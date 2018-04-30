package domainapp.dom.facturen;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = FactuurRegel.class,
        nature = NatureOfService.DOMAIN
)
public class FactuurRegelRepository {

    @Programmatic
    public FactuurRegel nieuweRegel(
            final Factuur factuur,
            final String artikel,
            final BigDecimal prijsIncl,
            final Integer volgorde) {
        final FactuurRegel factuurRegel = new FactuurRegel();
        factuurRegel.setFactuur(factuur);
        factuurRegel.setArtikel(artikel);
        factuurRegel.setPrijsInclBtw(prijsIncl);
        factuurRegel.setVolgorde(volgorde);
        serviceRegistry.injectServicesInto(factuurRegel);
        repositoryService.persist(factuurRegel);
        return factuurRegel;
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

    @Inject
    private ClockService clockService;
}
