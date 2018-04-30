package domainapp.integtests.tests.modules.mailchimpintegration;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;

import domainapp.dom.mailchimpintegration.module.impl.MailChimpList;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpListMemberLinkRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpListRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMember;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMemberRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpServiceImplementation;
import domainapp.fixture.dom.tti.TtiTearDown;
import domainapp.integtests.tests.DomainAppIntegTest;

public class MailChimpServiceImplementation_test extends DomainAppIntegTest {


    @Inject
    FixtureScripts fixtureScripts;

    @Inject MailChimpServiceImplementation mailChimpServiceImplementation;

    @Inject
    MailChimpListRepository mailChimpListRepository;

    @Inject
    MailChimpMemberRepository mailChimpMemberRepository;

    @Inject
    MailChimpListMemberLinkRepository mailChimpListMemberLinkRepository;

    MailChimpList list;

    MailChimpMember member;

    @Before
    public void setUp() throws Exception {
        // given
        fixtureScripts.runFixtureScript(new TtiTearDown(), null);
        list = mailChimpListRepository.findOrCreate("list1", "List 1");
        member = mailChimpMemberRepository.create("123", "firstName", "lastName", "email@test.com");
        mailChimpListMemberLinkRepository.findOrCreateLocal(list, member);
    }

    @Test
    public void deleteListLocal_works() throws Exception {

        // given
        Assertions.assertThat(mailChimpListRepository.listAll()).contains(list);
        Assertions.assertThat(list.getMembers()).contains(member);
        Assertions.assertThat(mailChimpListRepository.listAll()).isNotEmpty();
        // when
        mailChimpServiceImplementation.deleteListLocal(list);
        // then
        Assertions.assertThat(mailChimpListRepository.listAll()).isEmpty();
        Assertions.assertThat(mailChimpListRepository.listAll()).isEmpty();

    }


}