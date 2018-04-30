package domainapp.fixture.dom.tti;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.klanten.Klant;
import domainapp.dom.notities.Notitie;
import domainapp.dom.notities.NotitieLinkRepository;
import domainapp.dom.notities.NotitieRepository;

public abstract class NotitieAbstract extends FixtureScript {

    protected Notitie createNotitieForKlant(
            final Klant klant,
            final LocalDate datum,
            final String notitieString,
            final ExecutionContext fixtureResults
    ){
        Notitie notitie = notitieRepository.create(
                datum,
                notitieString,
                null, null
        );
        notitieLinkRepository.createLinkForKlant(notitie, klant);
        return fixtureResults.addResult(this, notitie);
    }

    protected Notitie createNotitieForBestelling(
            final Bestelling bestelling,
            final LocalDate datum,
            final String notitieString,
            final ExecutionContext fixtureResults
    ){
        Notitie notitie = notitieRepository.create(
                datum,
                notitieString,
                null, null
        );
        notitieLinkRepository.createLinkForBestelling(notitie, bestelling);
        return fixtureResults.addResult(this, notitie);
    }

    @Inject
    protected NotitieRepository notitieRepository;

    @Inject
    protected NotitieLinkRepository notitieLinkRepository;
}
