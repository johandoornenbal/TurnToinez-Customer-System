package domainapp.dom.mailchimpintegration;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import domainapp.dom.mailchimpintegration.module.impl.MailChimpList;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMember;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMemberRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        named = "MailChimpMenu",
        menuOrder = "105"
)
public class MailChimpMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<MailChimpList> alleLijsten(){
        return ttiMailChimpService.getAllLists();
    }

    @MemberOrder(sequence = "2")
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public List<MailChimpList> synchroniseerLijsten(){
        return ttiMailChimpService.syncMailChimpLists();
    }

    @MemberOrder(sequence = "3")
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public MailChimpList synchroniseerLeden(final MailChimpList lijst){
        return ttiMailChimpService.syncMembers(lijst);
    }

    @MemberOrder(sequence = "5")
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public MailChimpList nieuweLijst(final String naam, final String standaardEmailOnderwerp){
        return ttiMailChimpService.createList(naam, standaardEmailOnderwerp);
    }

    @MemberOrder(sequence = "6")
    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public List<MailChimpList> verwijderLijst(final MailChimpList lijst){
        ttiMailChimpService.deleteList(lijst);
        return ttiMailChimpService.getAllLists();
    }

    @MemberOrder(sequence = "4")
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public MailChimpList werkKlantenBijOpLijst(final MailChimpList lijst, final LocalDate laatsteBestellingNa, final LocalDate laatsteBestellingVoor){
        return ttiMailChimpService.customersWithLastOrderInPeriod(lijst, laatsteBestellingNa, laatsteBestellingVoor);
    }

    public String validateWerkKlantenBijOpLijst(final MailChimpList list, final LocalDate laatsteBestellingNa, final LocalDate laatsteBestellingVoor){
        if (laatsteBestellingVoor.isBefore(laatsteBestellingNa)){
            return "Geen goede periode: einde ligt voor het begin";
        }
        return null;
    }

    @MemberOrder(sequence = "7")
    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<MailChimpMember> alleLeden(){
        return mailChimpMemberRepository.listAll();
    }

    @MemberOrder(sequence = "8")
    @Action(semantics = SemanticsOf.SAFE)
    public  List<MailChimpMember> ledenZonderLijst(){
        return ttiMailChimpService.findOrphanedMembers();
    }

    @MemberOrder(sequence = "9")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void verwijderLedenZonderLijst(){
        ttiMailChimpService.removeAllOrphanedMembers();
    }

    @Inject
    TtiMailChimpService ttiMailChimpService;

    @Inject
    MailChimpMemberRepository mailChimpMemberRepository;
}
