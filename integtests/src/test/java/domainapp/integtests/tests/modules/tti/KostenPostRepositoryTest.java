package domainapp.integtests.tests.modules.tti;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.KostenPost;
import domainapp.dom.financieleadministratie.KostenPostRepository;
import domainapp.fixture.dom.tti.KostenPostFixtures;
import domainapp.fixture.dom.tti.TtiTearDown;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class KostenPostRepositoryTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    KostenPostRepository kostenPostRepository;

    @Before
    public void setUp() throws Exception {
        // given
        TtiTearDown tfs = new TtiTearDown();
        fixtureScripts.runFixtureScript(tfs, null);
        transactionService.nextTransaction();
        KostenPostFixtures kfs = new KostenPostFixtures();
        fixtureScripts.runFixtureScript(kfs, null);

        assertThat(kostenPostRepository.listAll().size()).isEqualTo(10);
    }

    @Test
    public void findByNaamContains() throws Exception {
        // given
        String namePart = "IvErs";
        // when
        List<KostenPost> found = kostenPostRepository.findByNaamContains(namePart);
        // then
        assertThat(found.size()).isEqualTo(2);
        assertThat(found.get(0).getNaam()).isEqualTo("diversen inkoop");
        assertThat(found.get(0).getPost()).isEqualTo(BoekingPost.INKOOP);
        assertThat(found.get(1).getNaam()).isEqualTo("diversen onkosten");
        assertThat(found.get(1).getPost()).isEqualTo(BoekingPost.ONKOSTEN_ALGEMEEN);
    }

    @Test
    public void findUnique() throws Exception {
        // given
        String name = "diversen inkoop";
        BoekingPost post = BoekingPost.INKOOP;
        // when
        KostenPost kostenPost = kostenPostRepository.findByNaamAndPost(name, post);
        // then
        assertThat(kostenPost.getNaam()).isEqualTo(name);
        assertThat(kostenPost.getPost()).isEqualTo(post);
    }

    @Test
    public void findOrCreate() throws Exception {
        // given
        String name = "nieuwe inkoop post";
        BoekingPost post = BoekingPost.INKOOP;
        // when
        KostenPost kostenPost = kostenPostRepository.findOrCreate(name, post);
        // then
        assertThat(kostenPostRepository.listAll().size()).isEqualTo(11);
        assertThat(kostenPost.getNaam()).isEqualTo(name);
        assertThat(kostenPost.getPost()).isEqualTo(post);

        // and when again is idempotent
        kostenPost = kostenPostRepository.findOrCreate(name, post);

        // then still
        assertThat(kostenPostRepository.listAll().size()).isEqualTo(11);
        assertThat(kostenPost.getNaam()).isEqualTo(name);
        assertThat(kostenPost.getPost()).isEqualTo(post);

    }

    @Test
    public void validateCreate() {

        // given
        BoekingPost boekingPost = BoekingPost.VERKOOP;

        // when
        String msg = kostenPostRepository.validateCreate(boekingPost);

        //
        assertThat(msg).isEqualTo("Een kostenpost kan niet verkoop zijn");

    }

}