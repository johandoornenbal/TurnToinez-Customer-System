package domainapp.dom.notities;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import domainapp.dom.klanten.Klant;

@Mixin
public class Klant_nieuweNotitie {

    private final Klant klant;

    public Klant_nieuweNotitie(Klant klant) {
        this.klant = klant;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Klant $$(
            @ParameterLayout(multiLine = 8)
            final String notitie,
            @Parameter(optionality= Optionality.OPTIONAL)
            final Blob foto1,
            @Parameter(optionality= Optionality.OPTIONAL)
            final Blob foto2) {
        Notitie nwNotitie = notitieRepository.create(clockService.now(), notitie, foto1, foto2);
        notitieLinkRepository.createLinkForKlant(nwNotitie, klant);
        return klant;
    }

    @Inject
    NotitieRepository notitieRepository;

    @Inject
    NotitieLinkRepository notitieLinkRepository;

    @Inject
    ClockService clockService;

}
