package domainapp.dom.siteservice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = FormulierVanSite.class,
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Van Website"
)
public class FormulierVanSiteRepository {

    @Programmatic
    public FormulierVanSite create(
            final int nummer,
            final String turnster,
            final String leeftijd,
            final String kledingmaat,
            final String lengte,
            final String postuur,
            final String pakje,
            final String mouwtje,
            final String broekje,
            final String kleurbroekje,
            final String elastiek,
            final String opmerkingen,
            final String naam,
            final String straat,
            final String postcode,
            final String plaats,
            final String email,
            final String telefoon,
            final String vragen,
            final String datum,
            final String pakjeId,
            final String prijsInfo
    ) {
        final FormulierVanSite formulier = new FormulierVanSite(
                nummer, turnster, leeftijd, kledingmaat, lengte, postuur, pakje,
                mouwtje, broekje, kleurbroekje, elastiek, opmerkingen, naam, straat,
                postcode, plaats, email, telefoon, vragen, datum, pakjeId, prijsInfo
        );
        formulier.setVerwerkt(false);
        serviceRegistry.injectServicesInto(formulier);
        repositoryService.persistAndFlush(formulier);
        return formulier;
    }

    @Programmatic
    public List<FormulierVanSite> listAll(){
        return repositoryService.allInstances(FormulierVanSite.class);
    }

    @Programmatic
    public List<FormulierVanSite> nietVerwerkteBestellingenVanSite() {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        FormulierVanSite.class,
                        "findNietVerwerkt")
        );
    }

    public List<FormulierVanSite> verwerkteBestellingenVanSite() {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        FormulierVanSite.class,
                        "findVerwerkt")
        );
    }

    @Programmatic
    public FormulierVanSite findByNummer(final int nummer){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        FormulierVanSite.class,
                        "findByNummer",
                        "nummer",
                        nummer
                )
        );
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;


}
