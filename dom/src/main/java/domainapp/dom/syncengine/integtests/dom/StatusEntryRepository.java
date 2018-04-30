package domainapp.dom.syncengine.integtests.dom;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = StatusEntry.class
)
public class StatusEntryRepository {

    @Programmatic
    public List<StatusEntry> listAll() {
        return repositoryService.allInstances(StatusEntry.class);
    }

    @Programmatic
    public StatusEntry findByUniqueId(
            final String uniqueId
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        StatusEntry.class,
                        "findByUniqueId",
                        "uniqueId", uniqueId));
    }

    @Programmatic
    public StatusEntry create(final String uniqueId, final String localETag, final String remoteETag) {
        final StatusEntry statusEntry = new StatusEntry();
        serviceRegistry2.injectServicesInto(statusEntry);
        statusEntry.setUniqueId(uniqueId);
        statusEntry.setLocalETag(localETag);
        statusEntry.setRemoteETag(remoteETag);
        repositoryService.persistAndFlush(statusEntry);
        return statusEntry;
    }

    @Programmatic
    public StatusEntry findOrCreate(
            final String uniqueId,
            final String localETag,
            final String remoteETag
    ) {
        StatusEntry statusEntry = findByUniqueId(uniqueId);
        if (statusEntry == null) {
            statusEntry = create(uniqueId, localETag, remoteETag);
        }
        return statusEntry;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry2;
}
