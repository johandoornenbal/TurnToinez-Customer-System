package domainapp.dom.document.spiimplementation;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.services.ClassNameServiceAbstract;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.spi.AttachmentAdvisorClassNameService;

@DomainService(nature = NatureOfService.DOMAIN)
public class AttachmentAdvisorClassNameServiceForTti extends ClassNameServiceAbstract<AttachmentAdvisor> implements
        AttachmentAdvisorClassNameService {

    public AttachmentAdvisorClassNameServiceForTti() {
        super(AttachmentAdvisor.class, "domainapp.dom");
    }

    @Override
    public List<ClassNameViewModel> attachmentAdvisorClassNames() {
        return this.classNames();
    }

}
