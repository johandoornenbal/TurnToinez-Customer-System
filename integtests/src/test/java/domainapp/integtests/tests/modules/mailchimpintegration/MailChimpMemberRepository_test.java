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

public class MailChimpMemberRepository_test extends DomainAppIntegTest {


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
    }

    @Test
    public void findOrCreate_works() throws Exception {

        MailChimpMember memberFound;

        // given
        list = mailChimpListRepository.findOrCreate("list1", "List 1");
        member = mailChimpMemberRepository.create("1", "", "last name", "test@email.com");
        mailChimpListMemberLinkRepository.findOrCreateFromRemote(list, member, "some status");
        // when
        memberFound = mailChimpMemberRepository.findOrCreateFromRemote("1", list, "tralala", "tralala", "test@email.com", "tralala");
        // then
        Assertions.assertThat(memberFound).isNotNull();
        Assertions.assertThat(memberFound).isEqualTo(member);
        Assertions.assertThat(mailChimpMemberRepository.listAll().size()).isEqualTo(1);
        Assertions.assertThat(mailChimpListMemberLinkRepository.listAll().size()).isEqualTo(1);

        // and when
        MailChimpList list2 = mailChimpListRepository.findOrCreate("list2", "List 2");
        memberFound = mailChimpMemberRepository.findOrCreateFromRemote("1", list2, "tralala", "tralala", "test@email.com", "tralala");
        // then
        Assertions.assertThat(memberFound).isEqualTo(member);
        Assertions.assertThat(mailChimpMemberRepository.listAll().size()).isEqualTo(1);
        Assertions.assertThat(mailChimpListMemberLinkRepository.listAll().size()).isEqualTo(2);

        // and when
        memberFound = mailChimpMemberRepository.findOrCreateFromRemote("2", list2, "tralala", "tralala", "tralala", "tralala");
        // then
        Assertions.assertThat(memberFound).isNotEqualTo(member);
        Assertions.assertThat(mailChimpMemberRepository.listAll().size()).isEqualTo(2);
        Assertions.assertThat(mailChimpListMemberLinkRepository.listAll().size()).isEqualTo(3);

    }

}