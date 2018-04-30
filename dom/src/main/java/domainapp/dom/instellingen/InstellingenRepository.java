package domainapp.dom.instellingen;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = Instellingen.class,
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        named = "Instellingen"
)
public class InstellingenRepository {

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Instellingen instellingen() {
        if (listAll().size()>0){
            return listAll().get(0);
        } else {
            final Instellingen instellingen = new Instellingen();
            serviceRegistry.injectServicesInto(instellingen);
            repositoryService.persist(instellingen);
            return instellingen;
        }
    }

    @Programmatic
    public List<Instellingen> listAll(){
        return repositoryService.allInstances(Instellingen.class);
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

}
