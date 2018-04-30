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
        repositoryFor = SyncObjectLocal.class
)
public class SyncObjectLocalRepository {

    @Programmatic
    public List<SyncObjectLocal> listAll() {
        return repositoryService.allInstances(SyncObjectLocal.class);
    }

    @Programmatic
    public SyncObjectLocal findByUniqueId(
            final String uniqueId
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        SyncObjectLocal.class,
                        "findByUniqueId",
                        "uniqueId", uniqueId));
    }

    @Programmatic
    public SyncObjectLocal create(final String uniqueId, final String field) {
        final SyncObjectLocal syncObjectLocal = new SyncObjectLocal();
        serviceRegistry2.injectServicesInto(syncObjectLocal);
        syncObjectLocal.setUniqueId(uniqueId);
        syncObjectLocal.setField(field);
        repositoryService.persistAndFlush(syncObjectLocal);
        return syncObjectLocal;
    }

    @Programmatic
    public SyncObjectLocal findOrCreate(
            final String uniqueId,
            final String field
    ) {
        SyncObjectLocal syncObjectLocal = findByUniqueId(uniqueId);
        if (syncObjectLocal == null) {
            syncObjectLocal = create(uniqueId, field);
        }
        return syncObjectLocal;
    }

    @Programmatic
    public void remove(final SyncObjectLocal syncAble) {
        repositoryService.removeAndFlush(syncAble);
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry2;

}
