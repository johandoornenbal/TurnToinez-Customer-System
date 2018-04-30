package domainapp.dom.document.renderermodelfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.isisaddons.module.xdocreport.dom.service.XDocReportModel;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import domainapp.dom.facturen.Factuur;
import domainapp.dom.facturen.FactuurRegel;
import domainapp.dom.klanten.Klant;
import lombok.Getter;

public class XDocReportModelOfFactuur extends RendererModelFactoryAbstract<Factuur> {

    public XDocReportModelOfFactuur() {
        super(Factuur.class);
    }

    @Override
    protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final Factuur factuur) {
        List<FactuurRegel.FactuurRegelDto> factuurRegels = new ArrayList<>();
        for (FactuurRegel regel : factuur.getFactuurRegels()){
            factuurRegels.add(new FactuurRegel.FactuurRegelDto(regel));
        }
        return new DataModel(factuur, factuurRegels);
    }

    public static class DataModel implements XDocReportModel {

        // for freemarker

        // org.incode.module.docrendering.xdocreport.dom.impl.AbstractFieldsMetadataClassSerializer#process
        // is called recursively and not very efficient. Using Dto's speeds up the process significantly
        @Getter
        private final Factuur.FactuurDto factuur;

        @Getter
        private final List<FactuurRegel.FactuurRegelDto> factuurRegels;

        @Getter
        private final String factuurDatum;

        @Getter
        private final Klant.KlantDto klant;

        public DataModel(final Factuur factuur, final List<FactuurRegel.FactuurRegelDto> factuurRegels) {
            this.factuur = new Factuur.FactuurDto(factuur);
            this.factuurRegels = factuurRegels;
            this.factuurDatum = factuur.getDatum().toString("dd-MM-yyyy");
            this.klant = new Klant.KlantDto(factuur.getKlant());
            if (klant.getEmail()==null){
                klant.setEmail("");
            }
            if (klant.getStraat()==null){
                klant.setStraat("");
            }
            if (klant.getPostcode()==null){
                klant.setPostcode("");
            }
            if (klant.getPlaats()==null){
                klant.setPlaats("");
            }
        }

        // for XDocReport
        @Override
        public Map<String, Data> getContextData() {
            return ImmutableMap.of(
                    "factuur", Data.object(factuur),
                    "factuurRegels", Data.list(factuurRegels, FactuurRegel.FactuurRegelDto.class),
                    "factuurDatum", Data.object(factuurDatum),
                    "klant", Data.object(klant)
            );
        }

    }

}
