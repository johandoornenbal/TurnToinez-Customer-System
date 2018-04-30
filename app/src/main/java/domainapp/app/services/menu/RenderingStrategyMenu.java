package domainapp.app.services.menu;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.spi.RendererClassNameService;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Documenten",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "77.3")
public class RenderingStrategyMenu  {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, hidden = Where.EVERYWHERE)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "1")
    public RenderingStrategy newRenderingStrategy(
            @ParameterLayout(named = "Reference")
            final String reference,
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "Input nature")
            final DocumentNature inputNature,
            @ParameterLayout(named = "Output nature")
            final DocumentNature outputNature,
            @ParameterLayout(named = "Renderer class name")
            final ClassNameViewModel classViewModel) {

        final Class<? extends Renderer> rendererClass =
                rendererClassNameService.asClass(classViewModel.getFullyQualifiedClassName());
        return renderingStrategyRepository.create(reference, name, inputNature, outputNature , rendererClass);
    }

    public List<ClassNameViewModel> choices4NewRenderingStrategy(
            final String reference,
            final String name,
            final DocumentNature inputNature,
            final DocumentNature outputNature
    ) {
        return rendererClassNameService.renderClassNamesFor(inputNature, outputNature);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    @MemberOrder(sequence = "2")
    public List<RenderingStrategy> allRenderingStrategies() {
        return renderingStrategyRepository.allStrategies();
    }

    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;

    @Inject
    private RendererClassNameService rendererClassNameService;


}