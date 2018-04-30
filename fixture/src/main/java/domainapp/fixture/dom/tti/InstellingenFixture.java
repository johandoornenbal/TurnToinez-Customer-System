package domainapp.fixture.dom.tti;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.instellingen.Instellingen;
import domainapp.dom.instellingen.InstellingenRepository;

public class InstellingenFixture extends FixtureScript {

    protected Instellingen createInstellingen(
            final String emailHandtekening,
            final String emailWanneerBetaald,
            final ExecutionContext fixtureResults
    ){
        Instellingen instellingen = instellingenRepository.instellingen();
        instellingen.setEmailHandtekening(emailHandtekening);
        instellingen.setEmailWanneerBetaald(emailWanneerBetaald);
        return fixtureResults.addResult(this, instellingen);
    }

    @Inject
    protected InstellingenRepository instellingenRepository;

    @Override protected void execute(final ExecutionContext executionContext) {
        createInstellingen(
                "\n\n\n\n\n\n Met vriendelijke groet,\nInez Doornenbal\n\n06 57622989\n058 2661357\nwww.turntoinez.nl\n",
                "Ik heb je betaling ontvangen. Bedankt. \n\nOp mijn facebook (https://www.facebook.com/turnpakjes) kun je kijken wanneer het pakje wordt verstuurd. \n\n Met vriendelijke groet,\nInez Doornenbal\n\n06 57622989\n058 2661357\nwww.turntoinez.nl\n",
                executionContext);
    }
}
