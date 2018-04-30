package domainapp.dom.document.renderermodelfactory;

import java.util.ArrayList;
import java.util.List;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import domainapp.dom.facturen.Factuur;
import domainapp.dom.facturen.FactuurRegel;
import lombok.Value;

public class FreemarkerModelOfFactuur extends RendererModelFactoryAbstract<Factuur> {

    public FreemarkerModelOfFactuur() {
        super(Factuur.class);
    }

    @Override protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final Factuur factuur) {
        List<FactuurRegel.FactuurRegelDto> factuurRegels = new ArrayList<>();
        for (FactuurRegel regel : factuur.getFactuurRegels()){
            factuurRegels.add(new FactuurRegel.FactuurRegelDto(regel));
        }
        return new DataModel(new Factuur.FactuurDto(factuur), factuurRegels);
    }

    @Value
    public static class DataModel {
        Factuur.FactuurDto factuur;
        List<FactuurRegel.FactuurRegelDto> factuurRegels;
    }

}

