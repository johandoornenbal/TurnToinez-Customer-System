package domainapp.fixture.dom.tti;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.facturen.Numerator;
import domainapp.dom.facturen.NumeratorRepository;

public class NumeratorFixture extends FixtureScript {

    @Inject
    protected NumeratorRepository numeratorRepository;

    @Override protected void execute(final ExecutionContext executionContext) {
        Numerator numerator = numeratorRepository.findOrCreateNumerator();
    }
}
