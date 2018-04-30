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

import domainapp.dom.bestellingen.Bestelling;

@Mixin
public class Bestelling_nieuweNotitie {

    private final Bestelling bestelling;

    public Bestelling_nieuweNotitie(Bestelling bestelling) {
        this.bestelling = bestelling;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Bestelling $$(
            @ParameterLayout(multiLine = 8)
            final String notitie,
            @Parameter(optionality= Optionality.OPTIONAL)
            final Blob foto1,
            @Parameter(optionality= Optionality.OPTIONAL)
            final Blob foto2) {
        Notitie nwNotitie = notitieRepository.create(clockService.now(), notitie, foto1, foto2);
        notitieLinkRepository.createLinkForBestelling(nwNotitie, bestelling);
        return bestelling;
    }

    @Inject
    NotitieRepository notitieRepository;

    @Inject
    NotitieLinkRepository notitieLinkRepository;

    @Inject
    ClockService clockService;

}
