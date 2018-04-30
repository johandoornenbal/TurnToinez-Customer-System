package domainapp.dom.financieleadministratie;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.facturen.Factuur;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Boeking.class
)
public class BoekingRepository {

    @PostConstruct
    public void postConstruct() {
        eventBusService.register(this);
    }

    @Programmatic
    public List<Boeking> listAll() {
        return repositoryService.allInstances(Boeking.class);
    }

    @Programmatic
    public Boeking create(
            final LocalDate datum,
            final BoekingType type,
            final String omschrijving,
            final BigDecimal prijsIncl,
            final BigDecimal prijsExcl,
            final BigDecimal btw,
            final BoekingPost boekingPost,
            final KostenPost kostenPost,
            final Factuur factuur,
            final boolean laterBijwerken
            ) {
        String timestamp = clockService.nowAsLocalDateTime().toString();
        return create(
                datum, type, omschrijving, prijsIncl, prijsExcl, btw, boekingPost, kostenPost, factuur,
                laterBijwerken, timestamp
        );
    }

    @Programmatic
    public Boeking create(
            final LocalDate datum,
            final BoekingType type,
            final String omschrijving,
            final BigDecimal prijsIncl,
            final BigDecimal prijsExcl,
            final BigDecimal btw,
            final BoekingPost boekingPost,
            final KostenPost kostenPost,
            final Factuur factuur,
            final boolean laterBijwerken,
            final String timestamp
    ) {
        final Boeking boeking = new Boeking(
                datum,
                timestamp,
                type,
                omschrijving,
                prijsIncl,
                prijsExcl,
                btw,
                boekingPost,
                kostenPost,
                factuur,
                laterBijwerken
        );
        serviceRegistry2.injectServicesInto(boeking);
        repositoryService.persist(boeking);
        return boeking;
    }

    @Programmatic
    public List<Boeking> laterBijwerken() {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        Boeking.class,
                        "findLaterBijwerken"));
    }

    @Programmatic
    public List<Boeking> findByDatumBetween(
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        Boeking.class,
                        "findByDatumBetween",
                        "startDate", startDate, "endDate", endDate));
    }

    @Programmatic
    public List<Boeking> findByTypeAndDatumBetween(
            final BoekingType type,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        Boeking.class,
                        "findByTypeAndDatumBetween",
                        "type", type, "startDate", startDate, "endDate", endDate));
    }

    @Programmatic
    public Boeking findByFactuur(final Factuur factuur){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Boeking.class,
                        "findByFactuur",
                        "factuur", factuur
                )
        );
    }

    @Programmatic
    public String validate(final Boolean berekenBtw, final BigDecimal prijsExcl, final BigDecimal btw){
        if (!berekenBtw){
            if (prijsExcl==null){
                return "Vul de prijs exclusief btw in, of vink berekenBtw aan";
            }
            if (btw==null){
                return "Vul de btw in, of vink berekenBtw aan";
            }
        }
        return null;
    }

    @Programmatic
    public void remove(final Boeking boeking){
        repositoryService.remove(boeking);
    }

    @Subscribe
    @Programmatic
    public void onBestellingBetaald(final Bestelling.BestellingBetaaldEvent e){
        if (e.getEventPhase()== AbstractDomainEvent.Phase.EXECUTED) {
            Factuur factuur = e.getSource().getFactuur();
            if (factuur != null) {
                create(
                        clockService.now(),
                        BoekingType.IN,
                        "Bestelling",
                        factuur.getBedragInclBtw(),
                        factuur.getBedragExclBtw(),
                        factuur.getBtw(),
                        BoekingPost.VERKOOP,
                        null,
                        factuur,
                        false
                );
            }
            messageService.informUser("Factuur boeking gemaakt voor bedrag ".concat(e.getSource().getFactuur().getBedragInclBtw().toString()));
        }
    }

    @Subscribe
    @Programmatic
    public void onFactuurVervallen(final Factuur.FactuurVervallenEvent e){

            Factuur factuur = e.getSource();
            Boeking factuurBoeking = findByFactuur(factuur);
            if (factuurBoeking != null) {
                create(
                        clockService.now(),
                        BoekingType.UIT,
                        "Niet betaalde factuur",
                        factuur.getBedragInclBtw(),
                        factuur.getBedragExclBtw(),
                        factuur.getBtw(),
                        BoekingPost.ONKOSTEN_ALGEMEEN,
                        null,
                        factuur,
                        false
                );
                messageService.informUser("Factuur boeking gecorrigeerd voor bedrag ".concat(e.getSource().getBedragInclBtw().toString()));
            }

    }

    @Programmatic
    public Boeking findByUniqueHistoricId(final Integer historicId) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Boeking.class,
                        "findByUniqueHistoricId",
                        "historicId", historicId));
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry2;

    @Inject
    private ClockService clockService;

    @Inject
    private MessageService messageService;

    @Inject
    private EventBusService eventBusService;

}
