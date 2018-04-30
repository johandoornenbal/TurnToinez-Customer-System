package domainapp.dom.communicatie;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;

import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.klanten.Klant;

@Mixin
public class Klant_nieuweEmail {

    private final Klant klant;

    public Klant_nieuweEmail(Klant klant) {
        this.klant = klant;
    }

    @Action()
    public EmailCommunicatieViewmodel nieuweEmail(
            final String onderwerp,
            @ParameterLayout(multiLine = 10)
            final String tekst) {
        return new EmailCommunicatieViewmodel(onderwerp, tekst, klant, false);
    }

    public String default1NieuweEmail(final String onderwerp, final String tekst){
        return "\n\n".concat(instellingenRepository.instellingen().getEmailHandtekening());
    }

    @Inject InstellingenRepository instellingenRepository;

}
