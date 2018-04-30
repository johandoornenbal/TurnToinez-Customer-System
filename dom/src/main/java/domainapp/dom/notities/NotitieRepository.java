package domainapp.dom.notities;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

@DomainService(
        repositoryFor = Notitie.class,
        nature = NatureOfService.DOMAIN
)
public class NotitieRepository {

    public List<Notitie> findByDatumBetween(
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Notitie.class,
                        "findByDatumBetween",
                        "startDate", startDate, "endDate", endDate));
    }

    public Notitie create(
            final LocalDate datum,
            final String notitie,
            final Blob foto1,
            final Blob foto2){
        Notitie nwNotitie = new Notitie(datum, notitie, foto1, foto2);
        serviceRegistry.injectServicesInto(nwNotitie);
        repositoryService.persist(nwNotitie);
        return nwNotitie;
    }

    public List<Notitie> findByNotitie(final String zoek) {
        return repositoryService.allMatches(
            new QueryDefault<Notitie>(
                    Notitie.class,
                    "findByNotitieContains",
                    "notitie",
                    zoek
            )
        );
    }

    @Programmatic
    public List<Notitie> alleNotities(){
        return repositoryService.allInstances(Notitie.class);
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

}
