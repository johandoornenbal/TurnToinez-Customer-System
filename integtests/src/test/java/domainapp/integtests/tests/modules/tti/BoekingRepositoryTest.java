package domainapp.integtests.tests.modules.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.facturen.Factuur;
import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.fixture.dom.tti.BoekingenFixtures;
import domainapp.fixture.dom.tti.KlantenFixtures;
import domainapp.fixture.dom.tti.KostenPostFixtures;
import domainapp.fixture.dom.tti.TtiTearDown;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class BoekingRepositoryTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    BoekingRepository boekingRepository;

    public static class NormalFinderTests extends BoekingRepositoryTest {

        @Before
        public void setUp() throws Exception {
            // given
            TtiTearDown tfs = new TtiTearDown();
            fixtureScripts.runFixtureScript(tfs, null);
            transactionService.nextTransaction();
            KostenPostFixtures kfs = new KostenPostFixtures();
            fixtureScripts.runFixtureScript(kfs, null);
            transactionService.nextTransaction();
            BoekingenFixtures bfs = new BoekingenFixtures();
            fixtureScripts.runFixtureScript(bfs, null);

            assertThat(boekingRepository.listAll().size()).isEqualTo(2);
        }

        @Test
        public void findBetween() throws Exception {

            // given
            LocalDate startDate = new LocalDate(2017, 01, 01);
            LocalDate endDate = new LocalDate(2017, 01, 01);

            // when, then
            assertThat(boekingRepository.findByDatumBetween(startDate, endDate).size()).isEqualTo(1);

            // and when
            endDate = new LocalDate(2017, 02, 02);

            // then
            assertThat(boekingRepository.findByDatumBetween(startDate, endDate).size()).isEqualTo(2);

            // and when
            startDate = new LocalDate(2017, 02, 03);

            // then
            assertThat(boekingRepository.findByDatumBetween(startDate, endDate).size()).isEqualTo(0);

        }

        @Test
        public void findByTypeAndBetween() throws Exception {

            // given
            LocalDate startDate = new LocalDate(2017, 01, 01);
            LocalDate endDate = new LocalDate(2017, 01, 01);

            // when, then
            assertThat(boekingRepository.findByTypeAndDatumBetween(BoekingType.UIT, startDate, endDate).size()).isEqualTo(1);

        }

    }

    public static class FactuurBoekingen extends BoekingRepositoryTest {

        @Inject KlantRepository klantRepository;

        @Before
        public void setUp() throws Exception {
            // given
            RecreateTtiTestFixtures fs = new RecreateTtiTestFixtures();
            fixtureScripts.runFixtureScript(fs, null);
            transactionService.nextTransaction();
        }

        @Test
        public void testFindByFactuur() throws Exception {

            // given
            Klant jan = klantRepository.findUnique(KlantenFixtures.EMAIL_JAN);
            Bestelling bestelling = wrap(jan).nieuweBestelling("dochter", null, null, null, null);
            Factuur factuur = wrap(bestelling).besteld();
            transactionService.nextTransaction();
            Boeking boeking = boekingRepository.create(
                    new LocalDate(2017,01,01),
                    BoekingType.IN,
                    "testboeking",
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BoekingPost.VERKOOP,
                    null,
                    factuur,
                    false
                    );
            transactionService.nextTransaction();

            // when
            Boeking result = boekingRepository.findByFactuur(factuur);

            // then
            assertThat(result).isEqualTo(boeking);
            assertThat(boeking.getFactuur()).isEqualTo(factuur);
        }

    }

    public static class LaterBijwerken extends BoekingRepositoryTest {

        @Test
        public void later_bijwerken_werkt(){

            // given
            getFixtureClock().setDate(2017,01,01);
            Boeking boeking = boekingRepository.create(new LocalDate(2017,01,01),  BoekingType.UIT,"test", null, null, BigDecimal.ZERO, BoekingPost.ONKOSTEN_ALGEMEEN, null, null, true);
            transactionService.nextTransaction();
            // when, then
            assertThat(boekingRepository.laterBijwerken().size()).isEqualTo(1);
            assertThat(boekingRepository.laterBijwerken().get(0)).isEqualTo(boeking);

        }

    }

}