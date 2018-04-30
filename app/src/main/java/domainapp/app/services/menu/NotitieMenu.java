package domainapp.app.services.menu;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import domainapp.dom.notities.Notitie;
import domainapp.dom.notities.NotitieRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(named = "Notities", menuOrder = "90")
public class NotitieMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public List<Notitie> zoekNotitie(final String zoek){
        return notitieRepository.findByNotitie(zoek);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Notitie> zoekNotitieOpDatum(
            final LocalDate startdate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate){
        return notitieRepository.findByDatumBetween(startdate, endDate!=null ? endDate : clockService.now());
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public List<Notitie> alleNotities(){
        return notitieRepository.alleNotities();
    }

    @Inject
    NotitieRepository notitieRepository;

    @Inject ClockService clockService;

}
