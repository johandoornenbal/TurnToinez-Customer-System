package domainapp.integtests.tests.modules.tti;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.bestellingen.Regel;
import domainapp.dom.bestellingen.RegelRepository;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class RegelRepositoryTest extends DomainAppIntegTest {


    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    RegelRepository regelRepository;

    @Before
    public void setUp() throws Exception {
        // given
        RecreateTtiTestFixtures fs = new RecreateTtiTestFixtures();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();
    }

    @Test
    public void findByRegelContains() throws Exception {
        // given
        // when
        List<Regel> results = regelRepository.findByRegelContains("AANbieding");
        // then
        assertThat(results.size()).isEqualTo(1);
        // and when
        results = regelRepository.findByRegelContains("pAkj");
        // then
        assertThat(results.size()).isEqualTo(6);
    }

}