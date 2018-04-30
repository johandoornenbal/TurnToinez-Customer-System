package domainapp.fixture.scenarios;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

import domainapp.dom.document.attachmentadvisor.ForFactuurAttachToSelf;
import domainapp.dom.document.renderermodelfactory.XDocReportModelOfFactuur;
import domainapp.dom.facturen.Factuur;
import lombok.Getter;

public class DocumentTypeAndTemplatesApplicableForFactuurFixture extends DocumentTemplateFSAbstract {

    // applicable to Factuur.class
    public static final String DOC_TYPE_REF_XDOCREPORT_PDF = "XDOCREPORT-PDF";

    @Getter
    DocumentTemplate fmkTemplate;

    @Getter
    DocumentTemplate siTemplate;

    @Getter
    DocumentTemplate xdpTemplate;

    @Getter
    DocumentTemplate xddTemplate;

    @Override
    protected void execute(final FixtureScript.ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new RenderingStrategiesFixture());
        final RenderingStrategy fmkRenderingStrategy = renderingStrategyRepository.findByReference(RenderingStrategiesFixture.REF_FMK);
        final RenderingStrategy xdpRenderingStrategy = renderingStrategyRepository.findByReference(RenderingStrategiesFixture.REF_XDP);

        final String atPath = "/";

        //
        // template for xdocreport (PDF)
        //
        final LocalDate now = clockService.now();

        final DocumentType docTypeForXDocReportPdf =
                upsertType(DOC_TYPE_REF_XDOCREPORT_PDF, "Factuur PDF", executionContext);

        xdpTemplate = upsertDocumentBlobTemplate(
                docTypeForXDocReportPdf, now, atPath,
                ".pdf",
                false,
                new Blob(
                        docTypeForXDocReportPdf.getName() + ".docx",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        loadResourceBytes("factuur_template.docx")
                ), xdpRenderingStrategy,
                "${factuur.nummer} - ${factuur.datum}", fmkRenderingStrategy,
                executionContext);

        mixin(DocumentTemplate._applicable.class, xdpTemplate).applicable(
                Factuur.class,
                XDocReportModelOfFactuur.class,
                ForFactuurAttachToSelf.class);
    }

    private static byte[] loadResourceBytes(final String resourceName) {
        final URL templateUrl = Resources
                .getResource(XDocReportModelOfFactuur.class, resourceName);
        try {
            return Resources.toByteArray(templateUrl);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read resource URL '%s'", templateUrl));
        }
    }

    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    private ClockService clockService;

}
