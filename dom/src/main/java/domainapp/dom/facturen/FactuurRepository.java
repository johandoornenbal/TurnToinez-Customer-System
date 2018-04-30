package domainapp.dom.facturen;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.services.DocumentCreatorService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.Regel;
import domainapp.dom.klanten.Klant;

@DomainService(
        repositoryFor = Factuur.class,
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Financiën",
        menuOrder = "100"
)
public class FactuurRepository {

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public java.util.List<Factuur> alleFacturen() {
        return repositoryService.allInstances(Factuur.class);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Factuur vindFactuurOpNummer(
            final String nummer
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Factuur.class,
                        "vindFactuurOpNummer",
                        "nummer", nummer));
    }

    @Programmatic
    public List<Factuur> findByBestelling(
            final Bestelling bestelling
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Factuur.class,
                        "findByBestelling",
                        "bestelling", bestelling));
    }

    @Programmatic
    public List<Factuur> findByKlant(
            final Klant klant
    ) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        Factuur.class,
                        "findByKlant",
                        "klant", klant));
    }

    @Programmatic
    public Factuur create(final Bestelling bestelling) {

        final Factuur factuur = new Factuur();
        serviceRegistry2.injectServicesInto(factuur);

        final UUID uuid = UUID.randomUUID();

        // maak de regels
        for (Regel regel : bestelling.getRegels()){
            BigDecimal prijsInclBtw = regel.getPrijs();
            factuur.createRegel(
                    regel.getArtikel(),
                    prijsInclBtw,
                    regel.getVolgorde()!=null ? regel.getVolgorde() : 0);
        }
        FactuurCalculation calculation = calculate(bestelling);
        factuur.setNummer(uuid.toString());
        factuur.setBestelling(bestelling);
        factuur.setKlant(bestelling.getKlant());
        factuur.setDatum(bestelling.getDatumBestelling());
        factuur.setBedragInclBtw(calculation.totaalInclBtw);
        factuur.setBedragExclBtw(calculation.totaalExclBtw);
        factuur.setBtw(calculation.totaalBtw);
        factuur.setDraft(true);
        repositoryService.persist(factuur);
        return factuur;
    }

    @Programmatic
    public Factuur createAndAlsoCreateDocument(final Bestelling bestelling, final boolean andRender){
        Factuur factuur = create(bestelling);
        DocumentTemplate template = documentTemplateRepository.findFirstByTypeAndApplicableToAtPath(documentTypeRepository.findByReference("XDOCREPORT-PDF"),"/");
        Document doc = documentCreatorService.createDocumentAndAttachPaperclips(factuur, template);
        if (andRender){
            doc.render(template, factuur);
        }
        return factuur;
    }

    @Programmatic
    FactuurCalculation calculate(Bestelling bestelling){
        FactuurCalculation calculation = new FactuurCalculation();
        calculation.calculate(bestelling);
        return  calculation;
    }

    @Programmatic
    public Factuur createFactuur(final Bestelling bestelling) {
        if (findByBestellingAndNotVervallen(bestelling)!=null){
            throw new IllegalArgumentException("FOUT: Er is al een factuur gevonden voor deze bestelling.");
        } else {
            return create(bestelling);
        }
    }

    @Programmatic
    public Factuur findByBestellingAndNotVervallen(final Bestelling bestelling) throws IllegalArgumentException {
        List<Factuur> results = new ArrayList<>();
        for (Factuur factuur : findByBestelling(bestelling)){
            if (factuur.getVervallen()==null){
                results.add(factuur);
            }
        }

        if (results.size()>1) throw new IllegalArgumentException("FOUT: Meer dan een factuur gevonden voor deze bestelling.");

        return results.size()==1 ? results.get(0) : null;
    }

    @Programmatic
    public void remove(final Factuur factuur) {
        repositoryService.remove(factuur);
    }

    class FactuurCalculation {

        FactuurCalculation(){
            this.totaalInclBtw = BigDecimal.ZERO;
            this.totaalExclBtw = BigDecimal.ZERO;
            this.totaalBtw = BigDecimal.ZERO;
        }

        BigDecimal totaalInclBtw;

        BigDecimal totaalBtw;

        BigDecimal totaalExclBtw;

        void calculate(Bestelling bestelling){
            for (Regel regel : bestelling.getRegels()){
                BigDecimal prijsInclBtw = regel.getPrijs();
                totaalInclBtw = totaalInclBtw.add(prijsInclBtw);
            }
            totaalExclBtw = totaalInclBtw.divide(Btw.BTW_21_PERC.getMultiplier(), MathContext.DECIMAL64).setScale(2, BigDecimal.ROUND_HALF_UP);
            totaalBtw = totaalInclBtw.subtract(totaalExclBtw);
        }
    }

    @Programmatic
    public Factuur findByUniqueHistoricId(final Integer historicId) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Factuur.class,
                        "findByUniqueHistoricId",
                        "historicId", historicId));
    }

    @Inject RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject NumeratorRepository numeratorRepository;

    @Inject DocumentTemplateRepository documentTemplateRepository;

    @Inject DocumentCreatorService documentCreatorService;

    @Inject DocumentTypeRepository documentTypeRepository;

    @Inject ClockService clockService;

    @Inject PaperclipRepository paperclipRepository;

    @Inject DocumentRepository documentRepository;
}
