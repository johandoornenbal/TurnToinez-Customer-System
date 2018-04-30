package domainapp.dom.notities;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import domainapp.dom.bestellingen.Bestelling;

@Mixin
public class Bestelling_notities {

    private final Bestelling bestelling;
    public Bestelling_notities(Bestelling bestelling) {
        this.bestelling = bestelling;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Notitie> notities() {
        List<Notitie> result = new ArrayList<>();
        for (NotitieLink link : notitieLinkRepository.findByBestelling(bestelling)){
            result.add(link.getNotitie());
        }
        return result;
    }

    @Inject
    NotitieLinkRepository notitieLinkRepository;

    @Inject
    ClockService clockService;

}
