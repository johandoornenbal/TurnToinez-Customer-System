package domainapp.dom.uren;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = Uren.class,
        nature = NatureOfService.DOMAIN
)
public class UrenRepository {

    @Programmatic
    public Uren findUnique(final LocalDate datum) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Uren.class,
                        "findUnique",
                        "datum", datum));
    }

    @Programmatic
    public List<Uren> findByDatumBetween(
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Uren.class,
                        "findByDatumBetween",
                        "startDate", startDate, "endDate", endDate));
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Uren schrijfUren(
            final LocalDate datum,
            final int aantalUren
    ) {
        Uren uren = findUnique(datum);
        if (uren==null) {
            uren = create(datum, aantalUren);
        } else {
            uren.setUren(aantalUren);
        }
        return uren;
    }

    public LocalDate default0SchrijfUren(){
        return clockService.now();
    }

    public int default1SchrijfUren(){
        return 6;
    }

    private Uren create(
            final LocalDate datum,
            final int aantalUren){
        Uren uren = new Uren(datum, aantalUren);
        serviceRegistry.injectServicesInto(uren);
        repositoryService.persist(uren);
        return uren;
    }

    // for import API
    @Programmatic
    private Uren create(
            final LocalDate datum,
            final int aantalUren,
            final int historicId){
        Uren nwUren = create(datum, aantalUren);
        nwUren.setHistoricId(historicId);
        return nwUren;
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

    @Inject ClockService clockService;
}
