package domainapp.dom.communicatie;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.dom.klanten.Klant;

@DomainService(
        repositoryFor = CommunicatieRepository.class
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Communicatie",
        menuOrder = "80"
)
public class CommunicatieRepository {

    @Programmatic
    public Communicatie create(
            final Klant klant,
            final String titel,
            final LocalDate datum,
            final LocalTime tijd,
            final String tekst,
            final String afzender
    ){
        Communicatie communicatie = new Communicatie(
                klant,
                titel,
                datum,
                tijd,
                tekst,
                afzender
        );
        serviceRegistry.injectServicesInto(communicatie);
        repositoryService.persist(communicatie);
        return communicatie;
    }

    @Programmatic
    public Communicatie findOrCreate(
            final Klant klant,
            final String titel,
            final LocalDate datum,
            final LocalTime tijd,
            final String tekst
    ){
        return findUnique(klant, datum, tijd)!=null ? findUnique(klant, datum, tijd) : create(klant, titel, datum, tijd, tekst, klant.getEmail());
    }

    @Programmatic
    public Communicatie findUnique(
            final Klant klant,
            final LocalDate datum,
            final LocalTime tijd
    ){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Communicatie.class,
                        "findUnique",
                        "klant", klant, "datum", datum, "localTime", tijd));
    }

    @Programmatic
    public List<Communicatie> alleCommunicaties(){
                return repositoryService.allInstances(Communicatie.class);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Communicatie> laatstOntvangen(){
        LocalDate now = LocalDate.now();
        List<Communicatie> result = repositoryService.allMatches(
                new QueryDefault<>(
                        Communicatie.class,
                        "findByDateInterval",
                        "begin", now.minusDays(5), "end", now))
                .stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        return result;
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

}
