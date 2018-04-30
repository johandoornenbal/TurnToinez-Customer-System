package domainapp.dom.document.renderermodelfactory;

import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import domainapp.dom.facturen.Factuur;
import lombok.Getter;

public class StringInterpolatorRootOfFactuur extends RendererModelFactoryAbstract<Factuur> {

    public StringInterpolatorRootOfFactuur() {
        super(Factuur.class);
    }

    @Override
    protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final Factuur factuur) {
        return new DataModel(new Factuur.FactuurDto(factuur));
    }

    public static class DataModel extends StringInterpolatorService.Root {
        @Getter
        private final Factuur.FactuurDto factuur;

        public DataModel(final Factuur.FactuurDto factuur) {
            super(factuur);
            this.factuur = factuur;
        }
    }

}
