package domainapp.dom.document.attachmentadvisor;

import java.util.Collections;
import java.util.List;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAbstract;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import domainapp.dom.facturen.Factuur;
import lombok.Value;

public class ForFactuurAttachToSelf extends AttachmentAdvisorAbstract<Factuur> {

    public ForFactuurAttachToSelf() {
        super(Factuur.class);
    }

    @Override
    protected List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final Factuur factuur,
            final Document createdDocument) {
        return Collections.singletonList(new AttachmentAdvisor.PaperclipSpec(null, factuur,createdDocument));
    }

    @Value
    public static class DataModel {
        Factuur factuur;
    }

}
