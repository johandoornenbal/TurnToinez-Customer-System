package domainapp.dom.document.spiimplementation;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.spi.ApplicationTenancyService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class ApplicationTenancyServiceForTti implements ApplicationTenancyService {
    @Override
    public String atPathFor(final Object domainObject) {
        return "/";
    }
}
