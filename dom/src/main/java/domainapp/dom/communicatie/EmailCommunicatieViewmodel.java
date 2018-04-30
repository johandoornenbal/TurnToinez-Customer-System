package domainapp.dom.communicatie;

import java.util.Arrays;

import javax.inject.Inject;

import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.commons.config.IsisConfiguration;

import domainapp.dom.emailService.TtiEmailService;
import domainapp.dom.klanten.Klant;
import domainapp.dom.utils.HtmlUtils;
import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class EmailCommunicatieViewmodel {

    public EmailCommunicatieViewmodel(){}

    public EmailCommunicatieViewmodel(final String onderwerp, final String tekst, final Klant klant, boolean isAntwoord){
        Communicatie lastcom = klant.getCommunicaties().isEmpty() ? null : klant.getCommunicaties().last();
        this.onderwerp = onderwerp;
        this.aan = klant;
        this.email = klant.getEmail();
        this.tekst = isAntwoord ?
                tekst
                .concat("\n\n-------------------------------\n\nOp ")
                .concat(lastcom.getDatum().toString("dd-MM-yyyy"))
                .concat(" ")
                .concat(lastcom.getTijd())
                .concat(" schreef ")
                .concat(klant.getEmail())
                .concat("\n\n")
                .concat(lastcom.getTekst())
                :
                tekst;
    }

    public String title(){
        return "Uitgaande mail";
    }

    @Getter @Setter
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private Klant aan;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private String onderwerp;

    @Getter @Setter
    @PropertyLayout(multiLine = 10)
    private String tekst;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public EmailCommunicatieViewmodel wijzigEmail(
            final String onderwerp,
            @ParameterLayout(multiLine = 15)
            final String tekst){
        return new EmailCommunicatieViewmodel(onderwerp, tekst, getAan(), false);
    }

    public String default0WijzigEmail(){
        return getOnderwerp();
    }

    public String default1WijzigEmail(){
        return getTekst();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Klant verstuur(){

        boolean result = emailService.send(
                Arrays.asList(getEmail()),
                null,
                Arrays.asList(configuration.getString("isis.service.email.sender.address")),
                getOnderwerp(),
                HtmlUtils.formatString(getTekst()),
                getTekst(),
                null);

        if (result){
            messageService.informUser("E-mail is verstuurd");
            communicatieRepository.create(getAan(), getOnderwerp(), clockService.now(), LocalTime.now(), getTekst(), configuration.getString("isis.service.email.sender.address"));

        } else {
            messageService.raiseError("E-mail is niet verstuurd");
        }

        return getAan();
    }

    public Klant annuleer(){
        return getAan();
    }

    @Inject private TtiEmailService emailService;

    @Inject private IsisConfiguration configuration;

    @Inject private MessageService messageService;

    @Inject private CommunicatieRepository communicatieRepository;

    @Inject private ClockService clockService;

}
