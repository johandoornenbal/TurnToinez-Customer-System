package domainapp.dom.mailchimpintegration;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.dom.mailchimpintegration.module.impl.MailChimpList;

@Mixin
public class Lijst_werkKlantenBij {

    private final MailChimpList mailChimpList;

    public Lijst_werkKlantenBij(MailChimpList mailChimpList) {
        this.mailChimpList = mailChimpList;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public MailChimpList werkKlantenBij(final LocalDate laatsteBestellingNa, final LocalDate laatsteBestellingVoor) {
        return ttiMailChimpService.customersWithLastOrderInPeriod(mailChimpList, laatsteBestellingNa, laatsteBestellingVoor);
    }

    @Inject TtiMailChimpService ttiMailChimpService;

}
