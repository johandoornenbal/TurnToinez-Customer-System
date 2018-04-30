package domainapp.fixture.dom.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.bestellingen.Regel;
import domainapp.dom.bestellingen.RegelRepository;
import domainapp.dom.bestellingen.Status;

public abstract class KlantAbstract extends FixtureScript {

    protected Klant createKlant(
            final String naam,
            final String straat,
            final String postcode,
            final String plaats,
            final String email,
            final String naamTurnster,
            final String leeftijd,
            final String kledingmaat,
            final String lengte,
            final Postuur postuur,
            final String artikel,
            final BigDecimal prijs,
            final String opmerking,
            final Status status,
            final LocalDate datumBestelling,
            final LocalDate datumBetaald,
            final LocalDate datumKlaar,
            final ExecutionContext fixtureResults
    ){
        Klant klant = klantRepository.nieuweKlant(
                naam,
                straat,
                postcode,
                plaats,
                email,null,null,null
        );
        if (naamTurnster!=null) {
            Bestelling bestelling = bestellingRepository.nieuweBestelling(
                    klant,
                    naamTurnster,
                    leeftijd,
                    kledingmaat,
                    lengte,
                    postuur
            );
            bestelling.setStatus(status);
            bestelling.setDatumBestelling(datumBestelling);
            bestelling.setDatumBetaald(datumBetaald);
            bestelling.setDatumKlaar(datumKlaar);
            Regel regel = regelRepository.nieuweRegel(
                    bestelling,
                    artikel,
                    prijs,
                    opmerking,
                    1
            );
        }
        return fixtureResults.addResult(this, klant);
    }

    @Inject
    protected KlantRepository klantRepository;

    @Inject
    protected BestellingRepository bestellingRepository;

    @Inject
    protected RegelRepository regelRepository;
}
