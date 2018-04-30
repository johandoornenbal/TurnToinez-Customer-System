package domainapp.integtests.tests.modules.tti;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.document.dom.impl.docs.DocumentRepository;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.facturen.Factuur;
import domainapp.dom.facturen.FactuurRepository;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.fixture.dom.tti.KlantenFixtures;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class FactuurRepositoryTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    FactuurRepository factuurRepository;

    @Inject
    KlantRepository klantRepository;

    @Inject
    DocumentRepository documentRepository;

    @Before
    public void setUp() throws Exception {
        // given
        RecreateTtiTestFixtures fs = new RecreateTtiTestFixtures();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();
        assertThat(factuurRepository.alleFacturen().size()).isEqualTo(3);
        assertThat(documentRepository.allDocuments().size()).isEqualTo(1); // 1 template, 0 documents
    }

    @Test
    public void findByNummer() throws Exception {
        //given
        Factuur factuur = factuurRepository.alleFacturen().get(0);
        factuur.setNummer("1"); // had before - wrap(factuur).downloadfactuur() which caused concurrency issue, because a new thread is started
        //when
        factuur = wrap(factuurRepository).vindFactuurOpNummer("1");
        //then
        assertThat(factuur.getNummer()).isEqualTo("1");
    }

    @Test
    public void findByBestelling() throws Exception {
        //given
        Factuur factuur = factuurRepository.alleFacturen().get(0);
        Bestelling bestelling = factuur.getBestelling();
        //when
        List<Factuur> results = factuurRepository.findByBestelling(bestelling);
        //then
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(factuur);
    }

    @Test
    public void findByBestellingAndNotVervallen() throws Exception {
        //given
        Factuur factuur = factuurRepository.alleFacturen().get(0);
        Bestelling bestelling = factuur.getBestelling();
        //when
        Factuur result = factuurRepository.findByBestellingAndNotVervallen(bestelling);
        //then
        assertThat(result).isEqualTo(factuur);

        // and when
        factuur.setVervallen(true);
        // then
        assertThat(factuurRepository.findByBestellingAndNotVervallen(bestelling)).isNull();
    }

    @Test
    public void findByKlant() throws Exception {
        //given
        Klant johan = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);
        // when
        List<Factuur> results = factuurRepository.findByKlant(johan);
        // then
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0).getKlant()).isEqualTo(johan);
        assertThat(results.get(1).getKlant()).isEqualTo(johan);
    }

    @Test
    public void create() throws Exception {
        //given
        Klant klant = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);
        Bestelling bestelling = klant.nieuweBestelling(null, null, null, null, null);
        bestelling.setDatumBestelling(new LocalDate(2000,01,01));

        //when
        Factuur nieuweFactuur = factuurRepository.createFactuur(bestelling);
        //then
        assertThat(factuurRepository.alleFacturen().size()).isEqualTo(4);
        assertThat(nieuweFactuur.getKlant()).isEqualTo(klant);
        assertThat(nieuweFactuur.getBestelling()).isEqualTo(bestelling);
    }

}