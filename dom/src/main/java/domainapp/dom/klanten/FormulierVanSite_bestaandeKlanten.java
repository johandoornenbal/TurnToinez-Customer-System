package domainapp.dom.klanten;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.dom.siteservice.FormulierVanSite;

@Mixin
public class FormulierVanSite_bestaandeKlanten {

    private final FormulierVanSite formulierVanSite;

    public FormulierVanSite_bestaandeKlanten(FormulierVanSite formulierVanSite) {
        this.formulierVanSite = formulierVanSite;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Klant> bestaandeKlanten() {
        return formulierVanSite.getEmail()!=null && formulierVanSite.getEmail().length()>4 ? klantRepository.findByEmail(formulierVanSite.getEmail()) : new ArrayList<>();
    }

    @Inject
    KlantRepository klantRepository;

}
