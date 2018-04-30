package domainapp.dom.syncengine;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import static domainapp.dom.syncengine.SyncService.Scenario.LOCAL_NO_REMOTE_NO_STATUS;
import static domainapp.dom.syncengine.SyncService.Scenario.LOCAL_NO_REMOTE_STATUS;
import static domainapp.dom.syncengine.SyncService.Scenario.LOCAL_REMOTE_NO_STATUS;
import static domainapp.dom.syncengine.SyncService.Scenario.LOCAL_REMOTE_STATUS;
import static domainapp.dom.syncengine.SyncService.Scenario.NONE;
import static domainapp.dom.syncengine.SyncService.Scenario.NO_LOCAL_REMOTE_NO_STATUS;
import static domainapp.dom.syncengine.SyncService.Scenario.NO_LOCAL_REMOTE_STATUS;

@DomainService(nature = NatureOfService.DOMAIN)
public abstract class SyncService {

    /**
     * implements pattern decribed at https://unterwaditzer.net/2016/sync-algorithm.html
     * using two lists of entity instances (local and remote) and a status list
     *
     * @param localEntityList A list local instances of the entity
     * @param remoteEntityList A list remote instances of the entity
     * @param statusList The (local) status list of the entity
     * @return
     */
    @Programmatic
    public boolean sync(final List<SyncAble> localEntityList, final List<SyncAble> remoteEntityList, final List<SyncAbleStatusEntry> statusList){

        // handles all instances in local list
        for (SyncAble localInstance : Lists.newArrayList(localEntityList)){

            SyncAble remoteInstance = correspondingInstanceFrom(localInstance, remoteEntityList);
            Scenario scenario = NONE;

            if (remoteInstance!=null && statusListContains(localInstance, statusList)){
                scenario = LOCAL_REMOTE_STATUS;
            }
            if (remoteInstance==null && !statusListContains(localInstance, statusList)){
                scenario = LOCAL_NO_REMOTE_NO_STATUS;
            }
            if (remoteInstance==null && statusListContains(localInstance, statusList)){
                scenario = LOCAL_NO_REMOTE_STATUS;
            }
            if (remoteInstance!=null && !statusListContains(localInstance, statusList)){
                scenario = LOCAL_REMOTE_NO_STATUS;
            }

            switch (scenario){

            case LOCAL_REMOTE_STATUS:
                if (localETagHasChanged(localInstance, statusList) && remoteETagHasChanged(remoteInstance, statusList)){
                    handleConflictAndUpdateStatus(localInstance, remoteInstance, statusList);
                    break;
                } else {
                    if (localETagHasChanged(localInstance, statusList) && !remoteETagHasChanged(remoteInstance, statusList)) {
                        copyToRemoteAndUpdateStatus(localInstance, remoteInstance, statusList);
                        break;
                    } else {
                        if (!localETagHasChanged(localInstance, statusList) && remoteETagHasChanged(remoteInstance, statusList)) {
                            copyFromRemoteAndUpdateStatus(localInstance, remoteInstance, statusList);
                            break;
                        } else {
                            // no sync needed
                            break;
                        }
                    }
                }

            case LOCAL_NO_REMOTE_NO_STATUS:
                remoteEntityList.add(copyToRemoteAndAddStatus(localInstance, statusList));
                break;

            case LOCAL_NO_REMOTE_STATUS:
                removeLocalAndDeleteStatus(localInstance, statusList);
                localEntityList.remove(localInstance);
                break;

            case LOCAL_REMOTE_NO_STATUS:
                addStatusAndHandleConflictIfNeeded(localInstance, remoteInstance, statusList);
                break;

            case NONE:
            default:
                break;

            }

        }

        // handles those instances not in local list
        for (SyncAble remoteInstance : Lists.newArrayList(remoteEntityList)){

            SyncAble localInstance = correspondingInstanceFrom(remoteInstance, localEntityList);
            Scenario scenario = NONE;

            if (localInstance==null && !statusListContains(remoteInstance, statusList)){
                scenario = NO_LOCAL_REMOTE_NO_STATUS;
            }
            if (localInstance==null && statusListContains(remoteInstance, statusList)){
                scenario = NO_LOCAL_REMOTE_STATUS;
            }

            switch (scenario){

            case NO_LOCAL_REMOTE_NO_STATUS:
                localEntityList.add(copyFromRemoteAndAddStatus(remoteInstance, statusList));
                break;

            case NO_LOCAL_REMOTE_STATUS:
                removeRemoteAndDeleteStatus(remoteInstance, statusList);
                remoteEntityList.remove(remoteInstance);
                break;

            case NONE:
            default:
                break;

            }
        }

        // cleans status list (should not be needed)
        for (SyncAbleStatusEntry statusEntry : statusList){
            if (!entityListContains(statusEntry, (List) localEntityList) && !entityListContains(statusEntry, remoteEntityList)){
                statusEntry.remove();
            }
        }


        return true;
    }

    enum Scenario {
        LOCAL_REMOTE_STATUS,
        LOCAL_NO_REMOTE_NO_STATUS, // case: local created
        LOCAL_NO_REMOTE_STATUS, // case: remote deleted
        LOCAL_REMOTE_NO_STATUS, // case: local created and remote created but not in status list
        NO_LOCAL_REMOTE_NO_STATUS, // case: remote created
        NO_LOCAL_REMOTE_STATUS, // case: local deleted
        NONE

    }

    boolean statusListContains(final SyncAble entity, final List<SyncAbleStatusEntry> statusList){
        for (SyncAbleStatusEntry status : statusList){
            if (status.getUniqueId().equals(entity.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    boolean entityListContains(final SyncAbleStatusEntry statusEntry, final List<SyncAble> entityList){
        for (SyncAble instance : entityList){
            if (instance.getUniqueId().equals(statusEntry.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    boolean localETagHasChanged(final SyncAble localInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAbleStatusEntry statusEntry = statusEntryFor(localInstance, statusList);
        if (statusEntry==null) return false; // default behaviour
        if (statusEntry.getLocalETag().equals(localInstance.getETag())){
            return false;
        } else {
            return true;
        }
    }

    boolean remoteETagHasChanged(final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAbleStatusEntry statusEntry = statusEntryFor(remoteInstance, statusList);
        if (statusEntry==null) return false; // default behaviour
        if (statusEntry.getRemoteETag().equals(remoteInstance.getETag())){
            return false;
        } else {
            return true;
        }
    }

    SyncAble correspondingInstanceFrom(final SyncAble instance, final List<SyncAble> entityList){
        for (SyncAble listItem : entityList){
            if (listItem.getUniqueId().equals(instance.getUniqueId())){
                return listItem;
            }
        }
        return null;
    }

    SyncAbleStatusEntry statusEntryFor(final SyncAble localInstance, final List<SyncAbleStatusEntry> statusList){
        for (SyncAbleStatusEntry entry : statusList){
            if (entry.getUniqueId().equals(localInstance.getUniqueId())){
                return entry;
            }
        }
        return null;
    }

    boolean copyToRemoteAndUpdateStatus(final SyncAble localInstance, final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        copyState(localInstance, remoteInstance);
        boolean upsertDone = upsertRemote(remoteInstance);
        if (upsertDone) {
            statusEntryFor(localInstance, statusList).harmonizeETags(localInstance.getETag());
            return true;
        } else {
            return false;
        }
    }

    boolean copyFromRemoteAndUpdateStatus(final SyncAble localInstance, final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        copyState(remoteInstance, localInstance);
        upsertLocal(localInstance);
        statusEntryFor(localInstance, statusList).harmonizeETags(remoteInstance.getETag());
        return true;
    }

    SyncAble copyToRemoteAndAddStatus(final SyncAble localInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAble remoteInstance = newRemoteInstance();
        remoteInstance.setUniqueId(localInstance.getUniqueId());
        copyState(localInstance, remoteInstance);
        boolean upsertDone = upsertRemote(remoteInstance);
        addStatusEntry(remoteInstance, statusList);
        return remoteInstance;
    }

    boolean removeLocalAndDeleteStatus(final SyncAble localInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAbleStatusEntry entryToRemove = statusEntryFor(localInstance, statusList);
        removeLocal(localInstance);
        statusList.remove(entryToRemove);
        entryToRemove.remove();
        return true;
    }

    SyncAble copyFromRemoteAndAddStatus(final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAble localInstance = newLocalInstance();
        localInstance.setUniqueId(remoteInstance.getUniqueId());
        copyState(remoteInstance, localInstance);
        upsertLocal(localInstance);
        addStatusEntry(localInstance, statusList);
        return localInstance;
    }

    boolean removeRemoteAndDeleteStatus(final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAbleStatusEntry entryToRemove = statusEntryFor(remoteInstance, statusList);
        boolean removalDone = removeRemote(remoteInstance);
        if (removalDone) {
            statusList.remove(entryToRemove);
            entryToRemove.remove();
            return true;
        } else {
            return false;
        }
    }

    boolean addStatusAndHandleConflictIfNeeded(final SyncAble localInstance, final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        if (localInstance.getETag().equals(remoteInstance.getETag())){
            addStatusEntry(localInstance, statusList);
            return true;
        } else {
            boolean conflictResultOk = handleConflictAndAddStatus(localInstance, remoteInstance, statusList);
            if (conflictResultOk){
                return true;
            } else {
                return false;
            }
        }
    }

    boolean handleConflictAndAddStatus(final SyncAble localInstance, final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAble conflictResult = handleConflict(localInstance, remoteInstance);
        if (conflictResult!=null) {
            addStatusEntry(conflictResult, statusList);
            return true;
        } else {
            return false;
        }
    }

    boolean handleConflictAndUpdateStatus(final SyncAble localInstance, final SyncAble remoteInstance, final List<SyncAbleStatusEntry> statusList){
        SyncAble conflictResult = handleConflict(localInstance, remoteInstance);
        if (conflictResult!=null) {
            statusEntryFor(conflictResult, statusList).harmonizeETags(conflictResult.getETag());
            return true;
        } else {
            return false;
        }
    }

    public abstract SyncAble newLocalInstance();

    public abstract SyncAble newRemoteInstance();

    /**
     * Handles conflict: for instance 'localInstance prevails' or 'instance with latest timestamp' prevails
     * @param localInstance
     * @param remoteInstance
     * @return the local instance or null when fails
     */
    public abstract SyncAble handleConflict(final SyncAble localInstance, final SyncAble remoteInstance);

    /**
     * adds status entry for this to status list
     * @param statusList
     * @return status entry for this
     */
    public abstract SyncAbleStatusEntry addStatusEntry(final SyncAble syncAble, final List<SyncAbleStatusEntry> statusList);

    public abstract boolean removeLocal(final SyncAble syncAble);

    public abstract boolean removeRemote(final SyncAble syncAble);

    /**
     * updates or inserts a local instance
     * @param syncAble
     * @return true if succeeded; false otherwise
     */
    public abstract boolean upsertLocal(final SyncAble syncAble);

    /**
     * updates or inserts a remote instance
     * @param syncAble
     * @return true if succeeded; false otherwise
     */
    public abstract boolean upsertRemote(final SyncAble syncAble);

    /**
     * copies 'state' (normally all properties) to remote instance with the same unique id
     * @param syncAbleFrom instance to be copied from
     * @param syncAbleTo instance to be copied to
     * @return
     */
    public abstract SyncAble copyState(final SyncAble syncAbleFrom, final SyncAble syncAbleTo);

}
