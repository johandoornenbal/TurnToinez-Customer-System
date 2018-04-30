package domainapp.fixture.dom.tti;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.notities.Notitie;

public class NotitieFixtures extends NotitieAbstract {

    @Override protected void execute(final ExecutionContext executionContext) {
        Klant klantJohan = klantRepository.findUnique("johan@filternet.nl");
        Notitie notitieJohan = createNotitieForKlant(
                klantJohan,
                new LocalDate(2017, 01, 01),
                "Een hele coole notitie op klant Johan",
                executionContext
        );

        Klant klantMietje = klantRepository.findUnique("mietje@gmail.com");
        Bestelling bestelling = klantMietje.getBestellingen().first();
        Notitie notitieBestellingMietje = createNotitieForBestelling(
                bestelling,
                new LocalDate(2017,02,01),
                "Een voorbeeld van een notitie op de eerste bestelling van Mietje",
                executionContext
        );

    }

    @Inject KlantRepository klantRepository;
}
