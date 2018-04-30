package domainapp.integtests.tests.modules.mailchimpintegration;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;

import domainapp.dom.mailchimpintegration.module.impl.MailChimpList;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpListRepository;
import domainapp.fixture.dom.tti.TtiTearDown;
import domainapp.integtests.tests.DomainAppIntegTest;

public class MailChimpListRepository_test extends DomainAppIntegTest {


    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    MailChimpListRepository mailChimpListRepository;

    MailChimpList list;

    @Before
    public void setUp() throws Exception {
        // given
        fixtureScripts.runFixtureScript(new TtiTearDown(), null);
        list = mailChimpListRepository.findOrCreate("list1", "List 1");
    }

    @Test
    public void find_by_name_contains_works() throws Exception {

        // given
        // when
        // then
        Assertions.assertThat(mailChimpListRepository.findByNameContains("").size()).isEqualTo(1);
        Assertions.assertThat(mailChimpListRepository.findByNameContains(" ").size()).isEqualTo(1);
        Assertions.assertThat(mailChimpListRepository.findByNameContains("I").size()).isEqualTo(1);
        Assertions.assertThat(mailChimpListRepository.findByNameContains("Ist").size()).isEqualTo(1);
        Assertions.assertThat(mailChimpListRepository.findByNameContains("Ist1").size()).isEqualTo(0);

    }


}