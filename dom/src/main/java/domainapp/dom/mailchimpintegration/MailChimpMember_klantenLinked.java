package domainapp.dom.mailchimpintegration;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMember;

@Mixin
public class MailChimpMember_klantenLinked {

    private final MailChimpMember mailChimpMember;

    public MailChimpMember_klantenLinked(MailChimpMember mailChimpMember) {
        this.mailChimpMember = mailChimpMember;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Klant> klantenLinked() {
        return klantRepository.findByEmail(mailChimpMember.getEmailAddress());
    }

    @Inject KlantRepository klantRepository;

}
