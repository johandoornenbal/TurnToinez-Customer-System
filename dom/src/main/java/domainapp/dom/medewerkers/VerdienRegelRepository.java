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
        repositoryFor = VerdienRegel.class
)
public class VerdienRegelRepository {

    @Programmatic
    public List<VerdienRegel> listAll() {
        return repositoryService.allInstances(VerdienRegel.class);
    }

    @Programmatic
    public VerdienRegel create(
            final LocalDate datum,
            final String onderwerp,
            final BigDecimal verkoopprijs,
            final BigDecimal kosten,
            final Integer percentage,
            final String aantekening,
            final Medewerker medewerker
            ) {
        VerdienRegel regel = new VerdienRegel();
        regel.setDatum(datum);
        regel.setOnderwerp(onderwerp);
        regel.setPrijs(verkoopprijs);
        regel.setKosten(kosten);
        regel.setPercentage(percentage);
        regel.setAantekening(aantekening);
        regel.setMedewerker(medewerker);
        regel.calcVerdienste();
        serviceRegistry2.injectServicesInto(regel);
        repositoryService.persist(regel);
        return regel;
    }

    @Programmatic
    public List<VerdienRegel> findByDatumBetweenAndMedewerker(
            final LocalDate startDate,
            final LocalDate endDate,
            final Medewerker medewerker
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        VerdienRegel.class,
                        "findByDatumBetweenAndMedewerker",
                        "startDate", startDate, "endDate", endDate, "medewerker", medewerker));
    }

    @Programmatic
    public List<VerdienRegel> findByMedewerker(final Medewerker medewerker){
        return repositoryService.allMatches(
                new QueryDefault<>(
                        VerdienRegel.class,
                        "findByMedewerker",
                        "medewerker", medewerker
                )
        );
    }

    public VerdienRegel findByDatumAndPrijsAndOnderwerpAndPercentageAndKostenAndMedewerker(final LocalDate datum, final BigDecimal prijs, final String onderwerp, final Integer percentage, final BigDecimal kosten, final Medewerker medewerker) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        VerdienRegel.class,
                        "findByDatumAndPrijsAndOnderwerpAndPercentageAndKostenAndMedewerker",
                        "datum", datum,
                        "prijs", prijs,
                        "onderwerp", onderwerp,
                        "percentage", percentage,
                        "kosten", kosten,
                        "medewerker", medewerker
                )
        );
    }

    @Programmatic
    public void remove(final VerdienRegel verdienRegel){
        repositoryService.remove(verdienRegel);
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry2;

}
