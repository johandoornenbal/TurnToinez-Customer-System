package domainapp.dom.syncengine;

import java.util.List;

public class SyncServiceForTest extends SyncService {

    public SyncAble copyState(final SyncAble syncAble, final SyncAble syncAbleRemote) {
        SyncAbleForTest syncAbleForTest = (SyncAbleForTest) syncAble;
        SyncAbleForTest syncAbleForTestRemote = (SyncAbleForTest) syncAbleRemote;
        syncAbleForTestRemote.setField(syncAbleForTest.getField());
        return syncAble;
    }

    @Override public SyncAble newLocalInstance() {
        return new SyncAbleForTest();
    }

    @Override public SyncAble newRemoteInstance() {
        return new SyncAbleForTest();
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
    public SyncAbleStatusEntry addStatusEntry(final SyncAble syncAble, final List<SyncAbleStatusEntry> statusList) {
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId(syncAble.getUniqueId());
        entry.setLocalETag(syncAble.getETag());
        entry.setRemoteETag(syncAble.getETag());
        statusList.add(entry);
        return entry;
    }

    @Override public boolean removeLocal(final SyncAble syncAble) {
        return true;
    }

    @Override public boolean removeRemote(final SyncAble syncAble) {
        return true;
    }

    @Override public boolean upsertLocal(final SyncAble syncAble) {
        return true;
    }

    @Override public boolean upsertRemote(final SyncAble syncAble) {
        return true;
    }

}
