package domainapp.fixture.dom.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.facturen.Factuur;
import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;
import domainapp.dom.financieleadministratie.KostenPost;

public abstract class BoekingAbstract extends FixtureScript {

    protected Boeking createBoeking(
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
            final String timestamp,
            final ExecutionContext fixtureResults
    ){
        Boeking boeking = boekingRepository.create(
                datum,
                type,
                omschrijving,
                prijsIncl,
                prijsExcl,
                btw,
                boekingPost,
                kostenPost,
                factuur,
                laterBijwerken,
                timestamp
        );
        return fixtureResults.addResult(this, boeking);
    }

    @Inject
    protected BoekingRepository boekingRepository;
}
