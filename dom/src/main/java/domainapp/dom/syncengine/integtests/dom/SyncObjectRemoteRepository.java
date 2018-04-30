package domainapp.dom.syncengine.integtests.dom;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = SyncObjectRemote.class
)
public class SyncObjectRemoteRepository {

    @Programmatic
    public List<SyncObjectRemote> listAll() {
        return repositoryService.allInstances(SyncObjectRemote.class);
    }

    @Programmatic
    public SyncObjectRemote findByUniqueId(
            final String uniqueId
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        SyncObjectRemote.class,
                        "findByUniqueId",
                        "uniqueId", uniqueId));
    }

    @Programmatic
    public SyncObjectRemote create(final String uniqueId, final String field) {
        final SyncObjectRemote syncObjectRemote = new SyncObjectRemote();
        serviceRegistry2.injectServicesInto(syncObjectRemote);
        syncObjectRemote.setUniqueId(uniqueId);
        syncObjectRemote.setField(field);
        repositoryService.persistAndFlush(syncObjectRemote);
        return syncObjectRemote;
    }

    @Programmatic
    public SyncObjectRemote findOrCreate(
            final String uniqueId,
            final String field
    ) {
        SyncObjectRemote syncObjectRemote = findByUniqueId(uniqueId);
        if (syncObjectRemote == null) {
            syncObjectRemote = create(uniqueId, field);
        }
        return syncObjectRemote;
    }

    @Programmatic
    public void remove(final SyncObjectRemote syncAble) {
        repositoryService.removeAndFlush(syncAble);
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry2;
}
