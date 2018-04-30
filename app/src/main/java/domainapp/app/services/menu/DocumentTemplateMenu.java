package domainapp.app.services.menu;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.types.DocumentType;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Documenten",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "77.4")
public class DocumentTemplateMenu  {

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, hidden = Where.EVERYWHERE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "1")
    public DocumentTemplate newTextTemplate(
            final DocumentType type,
            @ParameterLayout(named = "Date", describedAs = "Date that this template comes into effect")
            final LocalDate date,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name", describedAs = "Optional, will defaults to the name of selected document type")
            final String name,
            @Parameter(maxLength = DocumentAbstract.MimeTypeType.Meta.MAX_LEN, mustSatisfy = DocumentAbstract.MimeTypeType.Meta.Specification.class)
            @ParameterLayout(named = "MIME type")
            final String mimeType,
            @Parameter(maxLength = DocumentTemplate.FileSuffixType.Meta.MAX_LEN)
            @ParameterLayout(named = "File suffix", describedAs = "The file suffix for any documents created from this template")
            final String fileSuffix,
            @ParameterLayout(named = "Text", multiLine = DocumentModule.Constants.TEXT_MULTILINE)
            final String templateText,
            @ParameterLayout(named = "Content Rendering Strategy")
            final RenderingStrategy contentRenderingStrategy,
            @Parameter(maxLength = DocumentTemplate.NameTextType.Meta.MAX_LEN)
            @ParameterLayout(named = "Subject text")
            final String subjectText,
            @ParameterLayout(named = "Subject rendering strategy")
            final RenderingStrategy subjectRenderingStrategy,
            @ParameterLayout(named = "Preview only?")
            final boolean previewOnly) {

        final String documentName = name != null? name : type.getName();
        return documentTemplateRepository.createText(
                type, date, "/", fileSuffix, previewOnly, documentName, mimeType, templateText,
                contentRenderingStrategy,
                subjectText, subjectRenderingStrategy);
    }

    public String default3NewTextTemplate() {
        return "text/html";
    }
    public String default4NewTextTemplate() {
        return "html";
    }

    public TranslatableString validateNewTextTemplate(
            final DocumentType proposedType,
            final LocalDate proposedDate,
            final String name,
            final String mimeType,
            final String fileSuffix,
            final String templateText,
            final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy,
            final boolean previewOnly) {

        final DocumentSort documentSort = DocumentSort.TEXT;

        return validateNewTemplate(proposedType, proposedDate, contentRenderingStrategy,
                documentSort);
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, hidden = Where.EVERYWHERE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "2")
    public DocumentTemplate newClobTemplate(
            final DocumentType type,
            @ParameterLayout(named = "Date", describedAs = "Date that this template comes into effect")
            final LocalDate date,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name", describedAs = "Optional, will default to the file name of the uploaded Clob")
            final String name,
            @Parameter(maxLength = DocumentTemplate.FileSuffixType.Meta.MAX_LEN)
            @ParameterLayout(named = "File suffix", describedAs = "The file suffix for any documents created from this template")
            final String fileSuffix,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Clob clob,
            final RenderingStrategy contentRenderingStrategy,
            @Parameter(maxLength = DocumentTemplate.NameTextType.Meta.MAX_LEN)
            @ParameterLayout(named = "Subject text")
            final String subjectText,
            @ParameterLayout(named = "Subject rendering strategy")
            final RenderingStrategy subjectRenderingStrategy,
            @ParameterLayout(named = "Preview only?")
            final boolean previewOnly) {

        final DocumentTemplate template = documentTemplateRepository.createClob(
                type, date, "/", fileSuffix, previewOnly, clob, contentRenderingStrategy, subjectText,
                subjectRenderingStrategy);
        if(name != null) {
            template.setName(name);
        }
        return template;
    }

    public TranslatableString validateNewClobTemplate(
            final DocumentType proposedType,
            final LocalDate proposedDate,
            final String name,
            final String fileSuffix,
            final Clob clob,
            final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy,
            final boolean previewOnly) {

        final DocumentSort documentSort = DocumentSort.CLOB;

        return validateNewTemplate(
                proposedType, proposedDate, contentRenderingStrategy,
                documentSort);

    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, hidden = Where.EVERYWHERE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "3")
    public DocumentTemplate newBlobTemplate(
            final DocumentType type,
            @ParameterLayout(named = "Date", describedAs = "Date that this template comes into effect")
            final LocalDate date,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name", describedAs = "Optional, will default to the file name of the uploaded Blob")
            final String name,
            @Parameter(maxLength = DocumentTemplate.FileSuffixType.Meta.MAX_LEN)
            @ParameterLayout(named = "File suffix", describedAs = "The file suffix for any documents created from this template")
            final String fileSuffix,
            final Blob blob,
            final RenderingStrategy contentRenderingStrategy,
            @Parameter(maxLength = DocumentTemplate.NameTextType.Meta.MAX_LEN)
            @ParameterLayout(named = "Subject text")
            final String subjectText,
            @ParameterLayout(named = "Subject rendering strategy")
            final RenderingStrategy subjectRenderingStrategy,
            @ParameterLayout(named = "Preview only?")
            final boolean previewOnly) {

        final DocumentTemplate template = documentTemplateRepository.createBlob(
                type, date, "/", fileSuffix, previewOnly, blob,
                contentRenderingStrategy, subjectText, subjectRenderingStrategy);
        if(name != null) {
            template.setName(name);
        }
        return template;
    }

    public TranslatableString validateNewBlobTemplate(
            final DocumentType proposedType,
            final LocalDate proposedDate,
            final String name,
            final String fileSuffix,
            final Blob blob,
            final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy,
            final boolean previewOnly) {

        final DocumentSort documentSort = DocumentSort.BLOB;

        return validateNewTemplate(
                proposedType, proposedDate, contentRenderingStrategy, documentSort);
    }

    private TranslatableString validateNewTemplate(
            final DocumentType proposedType,
            final LocalDate proposedDate,
            final RenderingStrategy proposedRenderingStrategy,
            final DocumentSort documentSort) {
        TranslatableString translatableString = documentTemplateRepository.validateApplicationTenancyAndDate(
                proposedType, "/", proposedDate, null);
        if(translatableString != null) {
            return translatableString;
        }

        translatableString = documentTemplateRepository.validateSortAndRenderingStrategyInputNature(documentSort,
                proposedRenderingStrategy);
        if(translatableString != null) {
            return translatableString;
        }

        return null;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    @MemberOrder(sequence = "4")
    public List<DocumentTemplate> allDocumentTemplates() {
        return documentTemplateRepository.allTemplates();
    }

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;

}