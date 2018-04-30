package domainapp.dom.communicatie;

import java.util.Arrays;

import javax.inject.Inject;

import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.commons.config.IsisConfiguration;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.emailService.TtiEmailService;
import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.utils.HtmlUtils;

@DomainService(
        nature = NatureOfService.DOMAIN
)
@DomainServiceLayout(
        menuOrder = "1"
)
public class CommunicatieService {

    public void sendEmailWhenOrderToPaid(final Bestelling bestelling){

        // safeguard
        if (bestelling.getEmail()==null) return;

        final String onderwerp = "Bericht van TurnToInez - Je betaling is ontvangen";
        final String klantNaam = bestelling.getKlant().getNaam();
        final String emailWanneerBetaald = instellingenRepository.instellingen().getEmailWanneerBetaald();
        final String bericht = new StringBuilder()
                .append("Beste ")
                .append(klantNaam)
                .append(", \n\n")
                .append(emailWanneerBetaald)
                .toString();

        boolean result = emailService.send(
                Arrays.asList(bestelling.getEmail()),
                null,
                Arrays.asList(configuration.getString("isis.service.email.sender.address")),
                onderwerp,
                HtmlUtils.toHtml(bericht),
                bericht,
                null);
        if (result){
            messageService.informUser("E-mail is verstuurd");
            communicatieRepository.create(bestelling.getKlant(), onderwerp, clockService.now(), LocalTime.now(), bericht, configuration.getString("isis.service.email.sender.address"));

        } else {
            messageService.raiseError("E-mail is niet verstuurd");
        }

    }

    @Inject
    private TtiEmailService emailService;

    @Inject
    private IsisConfiguration configuration;

    @Inject
    private MessageService messageService;

    @Inject
    private CommunicatieRepository communicatieRepository;

    @Inject
    private ClockService clockService;

    @Inject
    private InstellingenRepository instellingenRepository;

}
