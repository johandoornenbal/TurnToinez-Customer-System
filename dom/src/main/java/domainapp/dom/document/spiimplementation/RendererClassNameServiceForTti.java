package domainapp.dom.document.spiimplementation;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.DocumentNature;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.services.ClassNameServiceAbstract;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.spi.RendererClassNameService;

@DomainService(nature = NatureOfService.DOMAIN)
public class RendererClassNameServiceForTti extends ClassNameServiceAbstract<Renderer> implements
        RendererClassNameService {

    private static final String PACKAGE_PREFIX = "domainapp.dom";

    public RendererClassNameServiceForTti() {
        super(Renderer.class, PACKAGE_PREFIX);
    }

    @Programmatic
    public List<ClassNameViewModel> renderClassNamesFor(
            final DocumentNature inputNature,
            final DocumentNature outputNature) {
        if(inputNature == null || outputNature == null){
            return Lists.newArrayList();
        }
        return classNames(x -> inputNature.canActAsInputTo(x) && outputNature.canActAsOutputTo(x));
    }

    @Override
    public Class<Renderer> asClass(final String className) {
        return super.asClass(className);
    }

}
