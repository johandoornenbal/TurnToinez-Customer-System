package domainapp.dom.websitemaintenance.faq;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.dom.utils.HtmlUtils;
import domainapp.dom.websitemaintenance.WebsiteLanguage;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "website"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByRemoteId", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.websitemaintenance.faq.Faq "
                        + "WHERE remoteId == :remoteId "),
        @Query(
                name = "findByLanguage", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.websitemaintenance.faq.Faq "
                        + "WHERE language == :language "
                        + "ORDER BY sortOrder ASC")
})
@Unique(name = "Faq_remoteId_UNQ", members = { "remoteId" })
@DomainObject(
        editing = Editing.DISABLED,
        autoCompleteRepository = FaqRepository.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Faq implements Comparable<Faq> {

    public Faq(final Integer remoteId, final Integer sortOrder, final WebsiteLanguage language, final String question, final String answer) {
        this.remoteId = remoteId;
        this.sortOrder = sortOrder;
        this.language = language;
        this.question = question;
        this.answer = answer;
    }

    public String title(){
        return getQuestion().length()>30 ? getSortOrder().toString() + " - " + getQuestion().substring(0,30) : getSortOrder().toString() + " - " + getQuestion();
    }

    @Column(allowsNull = "false")
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Integer remoteId;

    @Column(allowsNull = "false")
    @PropertyLayout(named = "Volgorde")
    @Getter @Setter
    private Integer sortOrder;

    @Column(allowsNull = "false")
    @PropertyLayout(named = "Taal")
    @Getter @Setter
    private WebsiteLanguage language;

    @Column(allowsNull = "false")
    @PropertyLayout(named = "Vraag", multiLine = 3)
    @Getter
    private String question;

    public void setQuestion(final String q){
        this.question = HtmlUtils.fromHtml(q);
    }

    @Column(allowsNull = "false")
    @PropertyLayout(named = "Antwoord", multiLine = 5)
    @Getter
    private String answer;

    public void setAnswer(final String a){
        this.answer = HtmlUtils.fromHtml(a);
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.ALL_TABLES)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, named = "vorige", hidden = Where.ALL_TABLES)
    public Faq getPrevious(){
        Comparator<Faq> bySortOrderDesc =
                (Faq o1, Faq o2)->o2.getSortOrder().compareTo(o1.getSortOrder());
        List<Faq> othersBeforeMe = faqRepository.findOthersByLanguage(this)
                .stream()
                .filter(x->this.getSortOrder()!=null && x.getSortOrder()<this.getSortOrder())
                .sorted(bySortOrderDesc)
                .collect(Collectors.toList());
        return othersBeforeMe.isEmpty() ? null : othersBeforeMe.get(0);
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.ALL_TABLES)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION, named = "volgende", hidden = Where.ALL_TABLES)
    public Faq getNext(){
        List<Faq> othersAfterMe = faqRepository.findOthersByLanguage(this)
                .stream()
                .filter(x->this.getSortOrder()!=null && x.getSortOrder()>this.getSortOrder())
                .collect(Collectors.toList());
        return othersAfterMe.isEmpty() ? null : othersAfterMe.get(0);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public  List<Faq> getAndereVeelgesteldeVragen(){
        return faqRepository.findOthersByLanguage(this);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "Nieuw")
    public Faq create(
            @ParameterLayout(multiLine = 3)
            final String question,
            @ParameterLayout(multiLine = 5)
            final String answer,
            @ParameterLayout(named = "Plaats na")
            final Faq placeAfter,
            @ParameterLayout(named = "Taal")
            final WebsiteLanguage language
    ){
        Faq nwFaq = faqRepository.create(0, placeAfter.getSortOrder() + 1, language, HtmlUtils.toHtml(question), HtmlUtils.toHtml(answer));
        nwFaq.setRemoteId(faqService.addOnRemote(nwFaq));
        faqRepository.rearrangeSortOrder(this.getLanguage());
        faqService.syncWithRemote();
        return nwFaq;
    }

    public WebsiteLanguage default3Create(){
        return this.getLanguage();
    }

    public List<Faq> choices2Create(){
        return faqRepository.findByLanguage(this.getLanguage());
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(named = "Verwijder")
    public List<Faq> remove(){
        WebsiteLanguage useLang = this.getLanguage();
        faqService.deleteOnRemote(this);
        repositoryService.removeAndFlush(this);
        faqRepository.rearrangeSortOrder(useLang);
        faqService.syncWithRemote();
        return faqRepository.listAll();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Werk bij")
    public Faq update(
            @ParameterLayout(multiLine = 3)
            final String question,
            @ParameterLayout(multiLine = 5)
            final String answer,
            @Nullable
            @ParameterLayout(named = "Plaats na")
            final Faq placeAfter){
        setQuestion(HtmlUtils.toHtml(question));
        setAnswer(HtmlUtils.toHtml(answer));
        if (placeAfter!=null) {
            setSortOrder(placeAfter.getSortOrder() + 1);
        }
        faqService.updateOnRemote(this);
        faqRepository.rearrangeSortOrder(this.getLanguage());
        faqService.syncWithRemote();
        return this;
    }

    public String default0Update(){
        return getQuestion();
    }

    public String default1Update(){
        return getAnswer();
    }

    public List<Faq> choices2Update(){
        return faqRepository.findOthersByLanguage(this);
    }

    @ActionLayout(named = "Naar boven")
    public Faq moveUp(){
        setSortOrder(getSortOrder() - 3); // the previous has -2. To shift in above we use -3
        faqRepository.rearrangeSortOrder(this.getLanguage());
        faqService.updateOnRemote(this);
        faqService.syncWithRemote();
        return this;
    }

    @ActionLayout(named = "Naar beneden")
    public Faq moveDown(){
        setSortOrder(getSortOrder() + 3); // the next has + 2. To shift in underneath we use +3
        faqRepository.rearrangeSortOrder(this.getLanguage());
        faqService.updateOnRemote(this);
        faqService.syncWithRemote();
        return this;
    }


    //region > compareTo, toString
    @Override
    public int compareTo(final Faq other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "remoteId");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "remoteId");
    }
    //endregion

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private FaqService faqService;

    @Inject
    private FaqRepository faqRepository;

}
