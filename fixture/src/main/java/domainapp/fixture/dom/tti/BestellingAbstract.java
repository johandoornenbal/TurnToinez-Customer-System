package domainapp.fixture.dom.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.klanten.Klant;
import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;

public abstract class BestellingAbstract extends FixtureScript {

    protected Bestelling createBestelling(
            final Klant klant,
            final String naamTurnster,
            final String leeftijd,
            final String kledingmaat,
            final String lengte,
            final Postuur postuur,
            final String art1,
            final BigDecimal prijs1,
            final String opm1,
            final String art2,
            final BigDecimal prijs2,
            final String opm2,
            final ExecutionContext fixtureResults
    ){
        Bestelling bestelling = bestellingRepository.nieuweBestelling(
                klant,
                naamTurnster,
                leeftijd,
                kledingmaat,
                lengte,
                postuur
        );
        bestelling.nieuweRegel(art1, prijs1, opm1, 1);
        bestelling.nieuweRegel(art2, prijs2, opm2, 2);
        return fixtureResults.addResult(this, bestelling);
    }

    @Inject
    protected BestellingRepository bestellingRepository;

}
