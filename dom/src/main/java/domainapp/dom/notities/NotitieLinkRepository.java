package domainapp.dom.notities;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.klanten.Klant;

@DomainService(
        repositoryFor = NotitieLink.class,
        nature = NatureOfService.DOMAIN
)
public class NotitieLinkRepository {

    public NotitieLinkForKlant createLinkForKlant(
            final Notitie notitie,
            final Klant klant){
        NotitieLinkForKlant nwlink = new NotitieLinkForKlant(notitie, klant);
        serviceRegistry.injectServicesInto(nwlink);
        repositoryService.persist(nwlink);
        return nwlink;
    }

    public NotitieLinkForBestelling createLinkForBestelling(
            final Notitie notitie,
            final Bestelling bestelling){
        NotitieLinkForBestelling nwlink = new NotitieLinkForBestelling(notitie, bestelling);
        serviceRegistry.injectServicesInto(nwlink);
        repositoryService.persist(nwlink);
        return nwlink;
    }

    public NotitieLink findByNotitie(final Notitie notitie){
        return repositoryService.uniqueMatch(
            new QueryDefault<>(
                    NotitieLink.class,
                    "findByNotitie",
                    "notitie",
                    notitie)
        );
    }

    public List<NotitieLinkForKlant> findByKlant(final Klant klant) {
        return repositoryService.allMatches(
            new QueryDefault<>(
                    NotitieLinkForKlant.class,
                    "findByKlant",
                    "klant",
                    klant)
        );
    }

    public List<NotitieLinkForBestelling> findByBestelling(final Bestelling bestelling) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        NotitieLinkForBestelling.class,
                        "findByBestelling",
                        "bestelling",
                        bestelling)
        );
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

}
