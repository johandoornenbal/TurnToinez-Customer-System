package domainapp.dom.syncengine.integtests;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import domainapp.dom.syncengine.SyncAble;
import domainapp.dom.syncengine.SyncAbleStatusEntry;
import domainapp.dom.syncengine.SyncService;
import domainapp.dom.syncengine.integtests.dom.StatusEntryRepository;
import domainapp.dom.syncengine.integtests.dom.SyncObjectLocal;
import domainapp.dom.syncengine.integtests.dom.SyncObjectLocalRepository;
import domainapp.dom.syncengine.integtests.dom.SyncObjectRemote;
import domainapp.dom.syncengine.integtests.dom.SyncObjectRemoteRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class SyncServiceForIntegTest extends SyncService {


    @Override
    public SyncAble copyState(final SyncAble syncAbleFrom, final SyncAble syncAbleTo) {
        if (syncAbleFrom.getClass().isAssignableFrom(SyncObjectLocal.class)){
            SyncObjectLocal localObj = (SyncObjectLocal) syncAbleFrom;
            SyncObjectRemote remoteObj = (SyncObjectRemote) syncAbleTo;
            remoteObj.setField(localObj.getField());
        } else {
            SyncObjectRemote remoteObj = (SyncObjectRemote) syncAbleFrom;
            SyncObjectLocal localObj = (SyncObjectLocal) syncAbleTo;
            localObj.setField(remoteObj.getField());
        }
        return syncAbleFrom;
    }

    @Override public SyncAble newLocalInstance() {
        return new SyncObjectLocal();
    }

    @Override public SyncAble newRemoteInstance() {
        return new SyncObjectRemote();
    }

    /**
     * Simple implementation: localInstance prevails
     * @param localInstance
     * @param remoteInstance
     * @return the local instance or null when fails
     */
    @Override
    public SyncAble handleConflict(final SyncAble localInstance, final SyncAble remoteInstance){
        copyState(localInstance, remoteInstance);
        upsertLocal(localInstance);
        boolean upsertResult = upsertRemote(remoteInstance);
        return upsertResult ? localInstance : null;
    }

    @Override
    @Programmatic
    public SyncAbleStatusEntry addStatusEntry(final SyncAble syncAble, final List<SyncAbleStatusEntry> statusList) {
        SyncAbleStatusEntry entry = statusEntryRepository.create(syncAble.getUniqueId(), syncAble.getETag(), syncAble.getETag());
        statusList.add(entry);
        return entry;
    }

    @Override
    public boolean removeLocal(final SyncAble syncAble) {
        localRepository.remove(localRepository.findByUniqueId(syncAble.getUniqueId()));
        return true;
    }

    @Override public boolean removeRemote(final SyncAble syncAble) {
        remoteRepository.remove(remoteRepository.findByUniqueId(syncAble.getUniqueId()));
        return true;
    }

    @Override public boolean upsertLocal(final SyncAble syncAble) {
        localRepository.findOrCreate(syncAble.getUniqueId(), syncAble.getETag()).setField(syncAble.getETag());
        return true;
    }

    @Override public boolean upsertRemote(final SyncAble syncAble) {
        remoteRepository.findOrCreate(syncAble.getUniqueId(), syncAble.getETag()).setField(syncAble.getETag());
        return true;
    }

    @Inject
    StatusEntryRepository statusEntryRepository;

    @Inject
    SyncObjectLocalRepository localRepository;

    @Inject
    SyncObjectRemoteRepository remoteRepository;
}
