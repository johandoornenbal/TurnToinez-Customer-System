package domainapp.dom.mailchimpintegration;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import domainapp.dom.klanten.Klant;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMember;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMemberRepository;

@Mixin
public class Klant_mailChimpMember {

    private final Klant klant;

    public Klant_mailChimpMember(Klant klant) {
        this.klant = klant;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, hidden = Where.ALL_TABLES)
    public MailChimpMember mailChimpMember() {
        return klant.getEmail()!=null ? mailChimpMemberRepository.findByEmail(klant.getEmail()) : null;
    }

    @Inject MailChimpMemberRepository mailChimpMemberRepository;

}
