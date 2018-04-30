package domainapp.integtests.tests.modules.tti;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.notities.NotitieRepository;
import domainapp.fixture.dom.tti.KlantenFixtures;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class NotitieRepositoryTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    NotitieRepository notitieRepository;

    public static class FinderTests extends NotitieRepositoryTest {

        @Before
        public void setUp() throws Exception {
            // given
            RecreateTtiTestFixtures tfs = new RecreateTtiTestFixtures();
            fixtureScripts.runFixtureScript(tfs, null);
            assertThat(notitieRepository.alleNotities().size()).isEqualTo(2);
        }

        @Test
        public void findBetween() throws Exception {

            // given
            Klant klantJohan = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);

            // when
            LocalDate startDate = new LocalDate(2017, 01, 01);
            LocalDate endDate = new LocalDate(2017, 01, 01);

            // then
            assertThat(notitieRepository.findByDatumBetween(startDate, endDate).size()).isEqualTo(1);
            assertThat(notitieRepository.findByDatumBetween(startDate, endDate).get(0).getDatum()).isEqualTo(startDate);
            assertThat(notitieRepository.findByDatumBetween(startDate, endDate).get(0).getHoortBij()).isEqualTo(klantJohan);

            // and when
            endDate = new LocalDate(2017, 02, 01);

            // then
            assertThat(notitieRepository.findByDatumBetween(startDate, endDate).size()).isEqualTo(2);

        }

        @Test
        public void findByNotitieContains() throws Exception {

            // given
            Klant klantJohan = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);

            // when
            String search = "OtiTi";

            // then
            assertThat(notitieRepository.findByNotitie(search).size()).isEqualTo(2);

            // and when
            search = "oole";
            // then
            assertThat(notitieRepository.findByNotitie(search).size()).isEqualTo(1);
            assertThat(notitieRepository.findByNotitie(search).get(0).getHoortBij()).isEqualTo(klantJohan);
        }

    }

    @Inject KlantRepository klantRepository;

}