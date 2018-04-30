package domainapp.app.services.api;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.dom.communicatie.CommunicatieRepository;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;

@DomainService(nature = NatureOfService.VIEW_REST_ONLY)
public class Api {

    @Action(semantics = SemanticsOf.IDEMPOTENT, publishing = Publishing.DISABLED)
    public void consumeEmail(
            final String emailAddress,
            final String title,
            final LocalDate date,
            final String time,
            final String body){
        Klant klant = klantRepository.findByEmail(emailAddress).size()>0 ? klantRepository.findByEmail(emailAddress).get(0) : null;
        if (klant!=null){
            communicatieRepository.findOrCreate(klant, title, date, LocalTime.parse(time), body);
        }
    }

    @Inject KlantRepository klantRepository;

    @Inject CommunicatieRepository communicatieRepository;

}
