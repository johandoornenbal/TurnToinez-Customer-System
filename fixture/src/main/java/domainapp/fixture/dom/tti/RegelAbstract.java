package domainapp.fixture.dom.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.Regel;
import domainapp.dom.bestellingen.RegelRepository;

public abstract class RegelAbstract extends FixtureScript {

    protected Regel createRegel(
            final Bestelling bestelling,
            final String artikel,
            final BigDecimal prijs,
            final String opmerking,
            final Integer volgorde,
            final ExecutionContext fixtureResults
    ){
        Regel regel = regelRepository.nieuweRegel(
                bestelling,
                artikel,
                prijs,
                opmerking,
                volgorde
        );
        return fixtureResults.addResult(this, regel);
    }

    @Inject
    protected RegelRepository regelRepository;
}
