package domainapp.dom.communicatie;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;

import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.klanten.Klant;

@Mixin
public class Klant_beantwoordt {

    private final Klant klant;

    public Klant_beantwoordt(Klant klant) {
        this.klant = klant;
    }

    @Action()
    public EmailCommunicatieViewmodel beantwoordt(
        @ParameterLayout(multiLine = 10)
        final String tekst) {
            String titel = "RE: ".concat(klant.getCommunicaties().last().getTitel());
            return new EmailCommunicatieViewmodel(titel, tekst, klant, true);
    }


    public String disableBeantwoordt(){
        return klant.getCommunicaties().isEmpty() ? "geen communicaties" : null;
    }

    public String default0Beantwoordt(final String tekst){
        return "\n\n".concat(instellingenRepository.instellingen().getEmailHandtekening());
    }

    @Inject InstellingenRepository instellingenRepository;

}
