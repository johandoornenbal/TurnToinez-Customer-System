package domainapp.dom.facturen;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.commons.config.IsisConfiguration;

import domainapp.dom.emailService.TtiEmailService;
import domainapp.dom.utils.HtmlUtils;
import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class EmailViewModel {

    public EmailViewModel(){};

    public EmailViewModel(
            final Factuur factuur,
            final String tekst
    ){
        this.factuur = factuur;
        this.displEmailAddress =factuur.getKlant().getEmail();
        this.onderwerp = "Factuur voor uw bestelling bij Turn-To-Inez";
        this.tekst = tekst;
    }

    public EmailViewModel(
            final Factuur factuur,
            final String onderwerp,
            final String tekst,
            final String bcc
    ){
        this.factuur = factuur;
        this.displEmailAddress =factuur.getKlant().getEmail();
        this.onderwerp = onderwerp;
        this.tekst = tekst;
        this.bcc = bcc;
    }

    public String title(){
        return "Email";
    }

    public Factuur verstuurEmail(){
        if (getFactuur().getDraft()){
            getFactuur().setNummer(String.valueOf(numeratorRepository.findByName("factuurNummer").nextIncrementStr()));
            getFactuur().setDraft(false);
            getFactuur().createPdfDocumentAndrender();
        }
        boolean result = emailService.sendFactuur(factuur, onderwerp, HtmlUtils.formatString(getTekst()), getTekst(), getBcc()!=null ? getBcc() : configuration.getString("isis.service.email.sender.address"));
        if (result){
            messageService.informUser("E-mail is verstuurd");
        } else {
            messageService.raiseError("E-mail is niet verstuurd");
        }
        return getFactuur();
    }

    public EmailViewModel wijzigEmail(
            final String onderwerp,
            @ParameterLayout(multiLine = 15)
            final String tekst,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String bcc){
        return new EmailViewModel(getFactuur(), onderwerp, tekst, bcc);
    }

    public String default0WijzigEmail(final String onderwerp, final String tekst, final String bcc){
        return getOnderwerp();
    }

    public String default1WijzigEmail(final String onderwerp, final String tekst, final String bcc){
        return getTekst();
    }

    public String default2WijzigEmail(final String onderwerp, final String tekst, final String bcc){
        return configuration.getString("isis.service.email.sender.address");
    }

    @Getter @Setter
    @Property(hidden = Where.EVERYWHERE)
    private Factuur factuur;

    @Getter @Setter
    @PropertyLayout(named = "Email adres")
    private String displEmailAddress;

    @Getter @Setter
    private String onderwerp;

    @Getter @Setter
    @PropertyLayout(multiLine = 18)
    private String tekst;

    @Getter @Setter
    private String bcc;

    @Inject
    private TtiEmailService emailService;

    @Inject
    private NumeratorRepository numeratorRepository;

    @Inject
    private MessageService messageService;

    @Inject
    IsisConfiguration configuration;

}
