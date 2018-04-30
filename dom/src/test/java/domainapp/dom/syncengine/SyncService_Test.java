package domainapp.dom.syncengine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class SyncService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    SyncAbleStatusEntryForTest mockEntry;

    SyncService syncService;

    @Before
    public void setUp(){
        syncService = new SyncServiceForTest();
    }

    @Test
    public void sync_works_when_no_sync_needed_and_statuslist_cleans_up() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        entry.setLocalETag("123");
        entry.setRemoteETag("123");
        statusList.add(entry);
        statusList.add(mockEntry);

        List<SyncAble> localEntityList = new ArrayList<>();
        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");
        localEntityList.add(localInstance);

        List<SyncAble> remoteEntityList = new ArrayList<>();
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setUniqueId("1");
        remoteInstance.setField("123");
        remoteEntityList.add(remoteInstance);

        Assertions.assertThat(statusList.size()).isEqualTo(2);

        // expect removal of second mock entry
        context.checking(new Expectations(){{

            allowing(mockEntry).getUniqueId();
            will(returnValue("2"));
            oneOf(mockEntry).remove();

        }});

        // when
        syncService.sync(localEntityList, remoteEntityList, statusList);

        // then nothing has changed for first entry
        Assertions.assertThat(statusList.get(0)).isEqualTo(entry);
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("123");
        Assertions.assertThat(localEntityList.size()).isEqualTo(1);
        Assertions.assertThat(remoteEntityList.size()).isEqualTo(1);

    }

    /**
     * This test asserts that the 3 lists used as input for sync method are updated
     * Note however, that when implementing the service the persistance (local and remote) should be done overriding the
     * abstract methods (upsertLocal, upsertRemote, removeLocal, removeRemote, addStatusEntry). Although the in-memory lists are updated, they are not (explicitly) meant to be used
     * for persistence after syncing ...
     */
    @Test
    public void sync_works_with_local_and_remote_changes(){

        // local change
        SyncAbleForTest inst1Loc = new SyncAbleForTest("1", "123");
        SyncAbleForTest inst1Rem = new SyncAbleForTest("1", "xxx");
        SyncAbleStatusEntry entry1 = new SyncAbleStatusEntryForTest("1","xxx", "xxx");

        // remote change
        SyncAbleForTest inst2Loc = new SyncAbleForTest("2", "xxx");
        SyncAbleForTest inst2Rem = new SyncAbleForTest("2", "234");
        SyncAbleStatusEntry entry2 = new SyncAbleStatusEntryForTest("2","xxx", "xxx");

        // local add
        SyncAbleForTest inst3Loc = new SyncAbleForTest("3", "345");

        // remote add
        SyncAbleForTest inst4Rem = new SyncAbleForTest("4", "456");

        // setup lists
        List<SyncAble> localEntityList = new ArrayList<>();  // needed because adding elements during test; Arrays.asList returns fixed size list
        localEntityList.addAll(Arrays.asList(inst1Loc, inst2Loc, inst3Loc));
        Assertions.assertThat(localEntityList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(localEntityList.get(0).getETag()).isEqualTo("123");
        Assertions.assertThat(localEntityList.get(1).getUniqueId()).isEqualTo("2");
        Assertions.assertThat(localEntityList.get(1).getETag()).isEqualTo("xxx");
        Assertions.assertThat(localEntityList.get(2).getUniqueId()).isEqualTo("3");
        Assertions.assertThat(localEntityList.get(2).getETag()).isEqualTo("345");

        List<SyncAble> remoteEntityList = new ArrayList<>(); // needed because adding elements during test; Arrays.asList returns fixed size list
        remoteEntityList.addAll(Arrays.asList(inst1Rem, inst2Rem, inst4Rem));
        Assertions.assertThat(remoteEntityList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(remoteEntityList.get(0).getETag()).isEqualTo("xxx");
        Assertions.assertThat(remoteEntityList.get(1).getUniqueId()).isEqualTo("2");
        Assertions.assertThat(remoteEntityList.get(1).getETag()).isEqualTo("234");
        Assertions.assertThat(remoteEntityList.get(2).getUniqueId()).isEqualTo("4");
        Assertions.assertThat(remoteEntityList.get(2).getETag()).isEqualTo("456");

        List<SyncAbleStatusEntry> statusList = new ArrayList<>(); // needed because adding elements during test; Arrays.asList returns fixed size list
        statusList.addAll(Arrays.asList(entry1, entry2));
        Assertions.assertThat(statusList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("xxx");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("xxx");
        Assertions.assertThat(statusList.get(1).getUniqueId()).isEqualTo("2");
        Assertions.assertThat(statusList.get(1).getLocalETag()).isEqualTo("xxx");
        Assertions.assertThat(statusList.get(1).getRemoteETag()).isEqualTo("xxx");

        // when
        syncService.sync(localEntityList, remoteEntityList, statusList);

        // then
        Assertions.assertThat(localEntityList.size()).isEqualTo(4);
        Assertions.assertThat(localEntityList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(localEntityList.get(0).getETag()).isEqualTo("123");
        Assertions.assertThat(localEntityList.get(1).getUniqueId()).isEqualTo("2");
        Assertions.assertThat(localEntityList.get(1).getETag()).isEqualTo("234");

        Assertions.assertThat(remoteEntityList.size()).isEqualTo(4);
        Assertions.assertThat(remoteEntityList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(remoteEntityList.get(0).getETag()).isEqualTo("123");
        Assertions.assertThat(remoteEntityList.get(1).getUniqueId()).isEqualTo("2");
        Assertions.assertThat(remoteEntityList.get(1).getETag()).isEqualTo("234");

        Assertions.assertThat(statusList.size()).isEqualTo(4);
        Assertions.assertThat(statusList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(1).getUniqueId()).isEqualTo("2");
        Assertions.assertThat(statusList.get(1).getLocalETag()).isEqualTo("234");
        Assertions.assertThat(statusList.get(1).getRemoteETag()).isEqualTo("234");

    }

    @Test
    public void statusListContains() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        statusList.add(entry);

        SyncAbleForTest instanceToBeFound = new SyncAbleForTest();
        instanceToBeFound.setUniqueId("1");

        SyncAbleForTest instanceNotToBeFound = new SyncAbleForTest();
        instanceNotToBeFound.setUniqueId("0");

        // when, then
        Assertions.assertThat(syncService.statusListContains(instanceToBeFound, statusList)).isTrue();
        Assertions.assertThat(syncService.statusListContains(instanceNotToBeFound, statusList)).isFalse();

    }

    @Test
    public void entityListContains() throws Exception {

        // given
        List<SyncAble> entityList = new ArrayList<>();
        SyncAbleForTest entityInstance = new SyncAbleForTest();
        entityInstance.setUniqueId("1");
        entityList.add(entityInstance);

        SyncAbleStatusEntryForTest entryToBeFound = new SyncAbleStatusEntryForTest();
        entryToBeFound.setUniqueId("1");

        SyncAbleStatusEntryForTest entryNotToBeFound = new SyncAbleStatusEntryForTest();
        entryNotToBeFound.setUniqueId("0");

        // when, then
        Assertions.assertThat(syncService.entityListContains(entryToBeFound, entityList)).isTrue();
        Assertions.assertThat(syncService.entityListContains(entryNotToBeFound, entityList)).isFalse();

    }

    @Test
    public void localETagHasChanged() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        entry.setLocalETag("123");
        statusList.add(entry);

        SyncAbleForTest instance = new SyncAbleForTest();
        instance.setUniqueId("1");
        instance.setField("1234");

        // when, then
        Assertions.assertThat(syncService.localETagHasChanged(instance, statusList)).isTrue();

        // and when
        instance.setField("123");
        // then
        Assertions.assertThat(syncService.localETagHasChanged(instance, statusList)).isFalse();

    }

    @Test
    public void remoteETagHasChanged() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        entry.setRemoteETag("123");
        statusList.add(entry);

        SyncAbleForTest instance = new SyncAbleForTest();
        instance.setUniqueId("1");
        instance.setField("1234");

        // when, then
        Assertions.assertThat(syncService.remoteETagHasChanged(instance, statusList)).isTrue();

        // and when
        instance.setField("123");
        // then
        Assertions.assertThat(syncService.remoteETagHasChanged(instance, statusList)).isFalse();

    }

    @Test
    public void correspondingInstanceFrom() throws Exception {

        // given
        List<SyncAble> entityList = new ArrayList<>();
        SyncAbleForTest entityInstanceInList = new SyncAbleForTest();
        entityInstanceInList.setUniqueId("1");
        entityList.add(entityInstanceInList);

        SyncAbleForTest entityInstanceSearchedFor = new SyncAbleForTest();
        entityInstanceSearchedFor.setUniqueId("1");

        // when
        SyncAbleForTest result = (SyncAbleForTest) syncService.correspondingInstanceFrom(entityInstanceSearchedFor, entityList);
        // then
        Assertions.assertThat(result).isEqualTo(entityInstanceInList);

        // and when
        entityInstanceSearchedFor.setUniqueId("2");
        result = (SyncAbleForTest) syncService.correspondingInstanceFrom(entityInstanceSearchedFor, entityList);
        // then
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void statusEntryFor() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        entry.setRemoteETag("123");
        entry.setLocalETag("456");
        statusList.add(entry);

        SyncAbleForTest instance = new SyncAbleForTest();
        instance.setUniqueId("1");

        // when
        SyncAbleStatusEntryForTest statusEntryForInstanceFound = (SyncAbleStatusEntryForTest) syncService.statusEntryFor(instance, statusList);

        // then
        Assertions.assertThat(statusEntryForInstanceFound.getUniqueId()).isEqualTo(entry.getUniqueId());
        Assertions.assertThat(statusEntryForInstanceFound.getLocalETag()).isEqualTo(entry.getLocalETag());
        Assertions.assertThat(statusEntryForInstanceFound.getRemoteETag()).isEqualTo(entry.getRemoteETag());
    }

    @Test
    public void copyToRemoteAndUpdateStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        statusList.add(entry);

        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setUniqueId("1");
        remoteInstance.setField("456");

        // when
        syncService.copyToRemoteAndUpdateStatus(localInstance, remoteInstance, statusList);

        // then
        Assertions.assertThat(entry.getLocalETag()).isEqualTo("123");
        Assertions.assertThat(entry.getRemoteETag()).isEqualTo("123");

    }

    @Test
    public void copyFromRemoteAndUpdateStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        statusList.add(entry);

        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setUniqueId("1");
        remoteInstance.setField("456");

        // when
        syncService.copyFromRemoteAndUpdateStatus(localInstance, remoteInstance, statusList);

        // then
        Assertions.assertThat(entry.getLocalETag()).isEqualTo("456");
        Assertions.assertThat(entry.getRemoteETag()).isEqualTo("456");

    }

    @Test
    public void copyToRemoteAndAddStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();

        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");

        // when
        syncService.copyToRemoteAndAddStatus(localInstance, statusList);

        // then
        Assertions.assertThat(statusList).isNotEmpty();
        Assertions.assertThat(statusList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("123");

    }



    @Test
    public void removeLocalAndDeleteStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        statusList.add(mockEntry);

        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockEntry).getUniqueId();
            will(returnValue("1"));
            oneOf(mockEntry).remove();
        }});

        // when
        syncService.removeLocalAndDeleteStatus(localInstance, statusList);

    }

    @Test
    public void copyFromRemoteAndAddStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();

        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setUniqueId("1");
        remoteInstance.setField("456");

        // when
        syncService.copyFromRemoteAndAddStatus(remoteInstance, statusList);

        // then
        Assertions.assertThat(statusList).isNotEmpty();
        Assertions.assertThat(statusList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("456");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("456");

    }

    @Test
    public void removeRemoteAndDeleteStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        statusList.add(mockEntry);

        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setUniqueId("2");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockEntry).getUniqueId();
            will(returnValue("2"));
            oneOf(mockEntry).remove();
        }});

        // when
        syncService.removeLocalAndDeleteStatus(remoteInstance, statusList);

    }

    @Test
    public void addStatusAndHandleConflictIfNeeded_works_when_no_conflict() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setField("123");

        // when
        syncService.addStatusAndHandleConflictIfNeeded(localInstance, remoteInstance, statusList);

        // then
        Assertions.assertThat(statusList).isNotEmpty();
        Assertions.assertThat(statusList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("123");

    }

    @Test
    public void addStatusAndHandleConflictIfNeeded_works_when_conflict() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setField("456");
        Assertions.assertThat(remoteInstance.getETag()).isEqualTo("456");

        // when
        syncService.addStatusAndHandleConflictIfNeeded(localInstance, remoteInstance, statusList);

        // then
        Assertions.assertThat(statusList).isNotEmpty();
        Assertions.assertThat(statusList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("123");
        Assertions.assertThat(remoteInstance.getETag()).isEqualTo("123");

    }

    @Test
    public void handleConflictAndAddStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setField("456");
        Assertions.assertThat(remoteInstance.getETag()).isEqualTo("456");

        // when
        syncService.handleConflictAndAddStatus(localInstance, remoteInstance, statusList);

        // then
        Assertions.assertThat(statusList).isNotEmpty();
        Assertions.assertThat(statusList.get(0).getUniqueId()).isEqualTo("1");
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("123");
        Assertions.assertThat(remoteInstance.getETag()).isEqualTo("123");

    }

    @Test
    public void handleConflictAndUpdateStatus() throws Exception {

        // given
        List<SyncAbleStatusEntry> statusList = new ArrayList<>();
        SyncAbleStatusEntryForTest entry = new SyncAbleStatusEntryForTest();
        entry.setUniqueId("1");
        statusList.add(entry);
        Assertions.assertThat(statusList.size()).isEqualTo(1);
        Assertions.assertThat(statusList.get(0).getLocalETag()).isNull();


        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setUniqueId("1");
        localInstance.setField("123");
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setField("456");
        Assertions.assertThat(remoteInstance.getETag()).isEqualTo("456");

        // when
        syncService.handleConflictAndUpdateStatus(localInstance, remoteInstance, statusList);

        // then
        Assertions.assertThat(statusList.size()).isEqualTo(1);
        Assertions.assertThat(statusList.get(0).getLocalETag()).isEqualTo("123");
        Assertions.assertThat(statusList.get(0).getRemoteETag()).isEqualTo("123");

    }

    @Test
    public void handleConflict() throws Exception {

        // given
        SyncAbleForTest localInstance = new SyncAbleForTest();
        localInstance.setField("123");
        SyncAbleForTest remoteInstance = new SyncAbleForTest();
        remoteInstance.setField("456");
        Assertions.assertThat(remoteInstance.getETag()).isEqualTo("456");

        // when
        syncService.handleConflict(localInstance, remoteInstance);

        // then
        Assertions.assertThat(remoteInstance.getETag()).isEqualTo("123");

    }

}