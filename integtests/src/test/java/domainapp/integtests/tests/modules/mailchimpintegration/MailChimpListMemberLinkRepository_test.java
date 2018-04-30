package domainapp.integtests.tests.modules.mailchimpintegration;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.mailchimpintegration.module.impl.MailChimpList;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpListMemberLinkRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpListRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMember;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMemberRepository;
import domainapp.fixture.dom.tti.TtiTearDown;
import domainapp.integtests.tests.DomainAppIntegTest;

public class MailChimpListMemberLinkRepository_test extends DomainAppIntegTest {


    @Inject
    FixtureScripts fixtureScripts;

    @Inject TransactionService transactionService;

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
        member = mailChimpMemberRepository.findOrCreateFromRemote("1", list, "", "last name", "test@email.com", "some status");
    }

    @Test
    public void findUnique_works() throws Exception {

        // given
        mailChimpListMemberLinkRepository.findOrCreateFromRemote(list, member, "some status");
        transactionService.nextTransaction();
        // when, then
        Assertions.assertThat(mailChimpListMemberLinkRepository.findUnique(list, member)).isNotNull();

    }


}