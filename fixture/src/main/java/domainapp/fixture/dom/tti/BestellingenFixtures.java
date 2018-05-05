package domainapp.fixture.dom.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.medewerkers.Medewerker;

public class BestellingenFixtures extends BestellingAbstract {

    @Override protected void execute(final ExecutionContext executionContext) {
        Klant klantJohan = klantRepository.findUnique("johan@filternet.nl");
        Bestelling bestellingJohan = createBestelling(
                klantJohan,
                "Lieuwkje",
                "14 jaar",
                "164",
                "134 (vorige maand nog gemeten)",
                Postuur.TENGER,
                "Pakje Vlok",
                new BigDecimal("45.50"),
                "moet lekker zitten",
                "Broekje velours zwart",
                new BigDecimal("10.00"),
                null,
                executionContext
        );
        bestellingJohan.besteld();

        Klant klantMietje = klantRepository.findUnique("mietje@gmail.com");
        Bestelling bestellingMietje = createBestelling(
                klantMietje,
                "Annechien",
                "net 9",
                "122",
                "101 cm",
                Postuur.STEVIG,
                "Pakje Floor",
                new BigDecimal("35.40"),
                "Moet niet te klein hoor",
                "Broekje velours zwart",
                new BigDecimal("10"),
                "Graag de aanbieding",
                executionContext
        );
        bestellingMietje.besteld();
        bestellingMietje.betaald(false);

        Bestelling legeBestelling = createBestelling(
                null,
                "Lisa",
                "6",
                "geen idee",
                null,
                null,
                "Pakje Frozen Elsa",
                new BigDecimal("65.00"),
                null, "Wokkel", new BigDecimal("5.25"),null,
                executionContext
        );

        Bestelling bestellingJohan2 = createBestelling(
                klantJohan,
                "Alberdientje",
                "8 jaar",
                "116",
                "99 (vorige jaar dan)",
                Postuur.STEVIG,
                "Pakje Oranje",
                new BigDecimal("15.50"),
                "Graag oranje",
                "Broekje velours zwart",
                new BigDecimal("10.00"),
                null,
                executionContext
        );
        bestellingJohan2.besteld();
        bestellingJohan2.betaald(false);
        bestellingJohan2.klaar(Medewerker.INEZ, null, null, null, null, null);
    }

    @Inject KlantRepository klantRepository;
}
