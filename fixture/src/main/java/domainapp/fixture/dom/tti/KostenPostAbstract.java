package domainapp.fixture.dom.tti;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.KostenPost;
import domainapp.dom.financieleadministratie.KostenPostRepository;

public abstract class KostenPostAbstract extends FixtureScript {

    protected KostenPost createKostenPost(
            final String naam,
            final BoekingPost boekingPost,
            final ExecutionContext fixtureResults
    ){
        KostenPost post = kostenPostRepository.create(
                naam,
                boekingPost
        );
        return fixtureResults.addResult(this, post);
    }

    @Inject
    protected KostenPostRepository kostenPostRepository;
}
