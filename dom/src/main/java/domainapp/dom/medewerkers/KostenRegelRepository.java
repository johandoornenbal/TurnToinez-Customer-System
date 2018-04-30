package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = KostenRegel.class
)
public class KostenRegelRepository {

    @Programmatic
    public List<KostenRegel> listAll() {
        return repositoryService.allInstances(KostenRegel.class);
    }

    @Programmatic
    public KostenRegel create(
            final LocalDate datum,
            final String onderwerp,
            final BigDecimal bedrag,
            final String aantekening,
            final Medewerker medewerker
            ) {
        KostenRegel regel = new KostenRegel();
        regel.setDatum(datum);
        regel.setOnderwerp(onderwerp);
        regel.setBedrag(bedrag);
        regel.setAantekening(aantekening);
        regel.setMedewerker(medewerker);
        serviceRegistry2.injectServicesInto(regel);
        repositoryService.persist(regel);
        return regel;
    }

    @Programmatic
    public List<KostenRegel> findByDatumBetweenAndMedewerker(
            final LocalDate startDate,
            final LocalDate endDate,
            final Medewerker medewerker
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        KostenRegel.class,
                        "findByDatumBetweenAndMedewerker",
                        "startDate", startDate, "endDate", endDate, "medewerker", medewerker));
    }

    @Programmatic
    public List<KostenRegel> findByMedewerker(final Medewerker medewerker){
        return repositoryService.allMatches(
                new QueryDefault<>(
                        KostenRegel.class,
                        "findByMedewerker",
                        "medewerker", medewerker
                )
        );
    }

    @Programmatic
    public void remove(final KostenRegel kostenRegel){
        repositoryService.remove(kostenRegel);
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry2;

}
