package domainapp.dom.websitemaintenance.faq;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import domainapp.dom.utils.HtmlUtils;
import domainapp.dom.websitemaintenance.WebsiteLanguage;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Faq.class
)
public class FaqRepository {

    Comparator<Faq> bySortOrderAsc =
            (Faq o1, Faq o2)->o1.getSortOrder().compareTo(o2.getSortOrder());

    @Programmatic
    public List<Faq> listAll() {
        return repositoryService.allInstances(Faq.class)
                .stream().sorted(bySortOrderAsc).collect(Collectors.toList());
    }

    public List<Faq> autoComplete(@MinLength(3) final String search) {
        return listAll();
    }


    @Programmatic
    public Faq findByRemoteId(
            final Integer remoteId
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Faq.class,
                        "findByRemoteId",
                        "remoteId", remoteId));
    }

    @Programmatic
    public List<Faq> findByLanguage(
            final WebsiteLanguage language
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Faq.class,
                        "findByLanguage",
                        "language", language));
    }

    @Programmatic
    public List<Faq> findOthersByLanguage(
            final Faq me){
            return findByLanguage(me.getLanguage())
                    .stream()
                    .filter(x->!x.equals(me))
                    .collect(Collectors.toList());
    }

    @Programmatic
    public Faq create(
            final Integer remoteId,
            final Integer sortOrder,
            final WebsiteLanguage language,
            final String question,
            final String answer) {
        final Faq faq = new Faq(remoteId, sortOrder, language, question, answer);
        serviceRegistry.injectServicesInto(faq);
        repositoryService.persistAndFlush(faq);
        return faq;
    }

    @Programmatic
    public Faq findOrCreate(
            final Integer remoteId,
            final Integer sortOrder,
            final WebsiteLanguage language,
            final String question,
            final String answer
    ) {
        Faq faq = findByRemoteId(remoteId);
        if (faq == null) {
            faq = create(remoteId, sortOrder, language, HtmlUtils.fromHtml(question), HtmlUtils.fromHtml(answer));
        }
        return faq;
    }

    @Programmatic
    public void rearrangeSortOrder(final WebsiteLanguage language){
        List<Faq> faqListSorted = findByLanguage(language).stream().sorted(bySortOrderAsc).collect(Collectors.toList());
        int i = 1;
        for (Faq faq : faqListSorted){
            faq.setSortOrder(i);
            i = i + 2; // this leaves room for inserts before running this function
        }
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;
}
