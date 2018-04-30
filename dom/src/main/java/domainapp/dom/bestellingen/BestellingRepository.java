package domainapp.dom.bestellingen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;

@DomainService(
        repositoryFor = Bestelling.class
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Bestellingen",
        menuOrder = "10"
)
public class BestellingRepository {

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<Bestelling> listAll() {
        return repositoryService.allInstances(Bestelling.class);
    }

    @Programmatic
    public List<Bestelling> findByKlant(
            final Klant klant
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Bestelling.class,
                        "findByKlant",
                        "klant", klant));
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Bestelling> zoek(final String zoek){
        final List<Bestelling> results = new ArrayList<>();
        results.addAll(findByNaamTurnster(zoek));
        for (Regel regel : regelRepository.findByRegelContains(zoek)){
            if (!results.contains(regel.getBestelling())){
                results.add(regel.getBestelling());
            }
        }
        for (Klant klant : klantRepository.zoekKlant(zoek)){
            for (Bestelling bestelling : klant.getBestellingen()){
                if (!results.contains(bestelling)){
                    results.add(bestelling);
                }
            }
        }
        return results.stream().sorted().collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Bestelling> zoekOpStatus(final Status status) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Bestelling.class,
                        "zoekOpStatus",
                        "status", status));
    }

    @Programmatic
    public List<Bestelling> findByNaamTurnster(
            final String naamTurnster
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Bestelling.class,
                        "findByNaamTurnsterContains",
                        "naamTurnster", naamTurnster));
    }

    @Programmatic
    public Bestelling nieuweBestelling() {
        final Bestelling bestelling = new Bestelling();
        bestelling.setTimestamp(clockService.nowAsLocalDateTime().toString());
        bestelling.setStatus(Status.KLAD);
        serviceRegistry.injectServicesInto(bestelling);
        repositoryService.persist(bestelling);
        return bestelling;
    }

    @Programmatic
    public Bestelling nieuweBestelling(
            final Klant klant,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String naamTurnster,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String leeftijd,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String kledingmaat,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String lengte,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Postuur postuur) {
        Bestelling bestelling = nieuweBestelling();
        bestelling.setKlant(klant);
        bestelling.setNaamTurnster(naamTurnster);
        bestelling.setLeeftijd(leeftijd);
        bestelling.setKledingmaat(kledingmaat);
        bestelling.setLengte(lengte);
        if (postuur!=null){
            bestelling.setPostuur(postuur);
        }
        return bestelling;
    }

    @Programmatic
    public Bestelling findByUniqueHistoricId(final Integer historicId) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Bestelling.class,
                        "findByUniqueHistoricId",
                        "historicId", historicId));
    }

    @Programmatic
    public void remove(final Bestelling bestelling) {
        repositoryService.remove(bestelling);
    }

    @Programmatic
    public List<Bestelling> laatstGemaakteBestellingen() {
        LocalDate begin = LocalDate.now().minusDays(21);
        List<Bestelling> result = zoekOpStatus(Status.KLAAR);
        return result.stream().filter(x->x.getDatumKlaar().isAfter(begin))
                .sorted(
                        (x1, x2)->(
                            Integer.compare(
                                    x2.getDatumKlaar().getYear()*366+x2.getDatumKlaar().getMonthOfYear()*31+x2.getDatumKlaar().getDayOfMonth(),
                                    Integer.valueOf(x1.getDatumKlaar().getYear()*366+x1.getDatumKlaar().getMonthOfYear()*31+x1.getDatumKlaar().getDayOfMonth())
                            )
                    )
                )
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Bestelling> gemaakteBestellingenVanaf(final LocalDate dateFrom) {
        List<Bestelling> result = zoekOpStatus(Status.KLAAR);
        return result.stream().filter(x->x.getDatumKlaar().isAfter(dateFrom.minusDays(1)))
                .sorted(
                        (x1, x2)->(
                                Integer.compare(
                                        x2.getDatumKlaar().getYear()*366+x2.getDatumKlaar().getMonthOfYear()*31+x2.getDatumKlaar().getDayOfMonth(),
                                        Integer.valueOf(x1.getDatumKlaar().getYear()*366+x1.getDatumKlaar().getMonthOfYear()*31+x1.getDatumKlaar().getDayOfMonth())
                                )
                        )
                )
                .sorted((x1, x2)->(x1.getGemaaktDoor().compareTo(x2.getGemaaktDoor())))
                .collect(Collectors.toList());
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

    @Inject
    private ClockService clockService;

    @Inject
    RegelRepository regelRepository;

    @Inject
    KlantRepository klantRepository;

}
