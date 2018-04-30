/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.integtests.tests.modules.syncengine;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import domainapp.dom.syncengine.SyncAble;
import domainapp.dom.syncengine.SyncAbleStatusEntry;
import domainapp.dom.syncengine.integtests.SyncServiceForIntegTest;
import domainapp.dom.syncengine.integtests.dom.StatusEntry;
import domainapp.dom.syncengine.integtests.dom.StatusEntryRepository;
import domainapp.dom.syncengine.integtests.dom.SyncObjectLocal;
import domainapp.dom.syncengine.integtests.dom.SyncObjectLocalRepository;
import domainapp.dom.syncengine.integtests.dom.SyncObjectRemote;
import domainapp.dom.syncengine.integtests.dom.SyncObjectRemoteRepository;
import domainapp.integtests.tests.DomainAppIntegTest;


public class SyncEngine_IntegTest extends DomainAppIntegTest {

    @Inject
    private IsisJdoSupport isisJdoSupport;

    SyncObjectLocal objLocAndRem;
    SyncObjectLocal objLocalOnly;
    SyncObjectLocal objLocRemDiv;

    SyncObjectRemote objRemAndLoc;
    SyncObjectRemote objRemOnly;
    SyncObjectRemote objRemLocDiv;

    @Before
    public void setUp() throws Exception {
        isisJdoSupport.executeUpdate("delete from \"sync\".\"StatusEntry\"");
        isisJdoSupport.executeUpdate("delete from \"sync\".\"SyncObjectLocal\"");
        isisJdoSupport.executeUpdate("delete from \"sync\".\"SyncObjectRemote\"");

        objLocAndRem = localRepository.create("1", "123");
        objLocalOnly = localRepository.create("2", "234");
        objLocRemDiv = localRepository.create("4", "456");

        objRemAndLoc = remoteRepository.create("1", "123");
        objRemOnly = remoteRepository.create("3", "345");
        objRemLocDiv = remoteRepository.create("4", "XXX");
    }

    @Test
    public void setup_is_correct() {

        // assert setup correct
        Assertions.assertThat(objLocAndRem.getField()).isEqualTo("123");
        Assertions.assertThat(objLocAndRem.getETag()).isEqualTo("123");
        Assertions.assertThat(objLocalOnly.getField()).isEqualTo("234");
        Assertions.assertThat(objLocalOnly.getETag()).isEqualTo("234");
        Assertions.assertThat(objLocRemDiv.getField()).isEqualTo("456");
        Assertions.assertThat(objLocRemDiv.getETag()).isEqualTo("456");
        Assertions.assertThat(objRemAndLoc.getField()).isEqualTo("123");
        Assertions.assertThat(objRemAndLoc.getETag()).isEqualTo("123");
        Assertions.assertThat(objRemOnly.getField()).isEqualTo("345");
        Assertions.assertThat(objRemOnly.getETag()).isEqualTo("345");
        Assertions.assertThat(objRemLocDiv.getField()).isEqualTo("XXX");
        Assertions.assertThat(objRemLocDiv.getETag()).isEqualTo("XXX");

    }

    @Test
    public void sync_with_empty_status_list_works() throws Exception {

        // given
        List<SyncAble> localEntitiesBefore = (List) localRepository.listAll();
        List<SyncAble> remoteEntitiesBefore = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusListBefore = (List) statusEntryRepository.listAll();

        // when
        syncServiceForIntegTest.sync(localEntitiesBefore, remoteEntitiesBefore, statusListBefore);

        // then
        List<SyncAbleStatusEntry> statusListAfter = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusListAfter.size()).isEqualTo(4);

        SyncAbleStatusEntry entry1 = statusEntryRepository.findByUniqueId("1");
        SyncAbleStatusEntry entry2 = statusEntryRepository.findByUniqueId("2");
        SyncAbleStatusEntry entry3 = statusEntryRepository.findByUniqueId("3");
        SyncAbleStatusEntry entry4 = statusEntryRepository.findByUniqueId("4");

        Assertions.assertThat(entry1.getLocalETag()).isEqualTo("123");
        Assertions.assertThat(entry1.getLocalETag()).isEqualTo(objLocAndRem.getETag());
        Assertions.assertThat(entry1.getRemoteETag()).isEqualTo(objRemAndLoc.getETag());
        Assertions.assertThat(entry2.getLocalETag()).isEqualTo("234");
        Assertions.assertThat(entry2.getLocalETag()).isEqualTo(objLocalOnly.getETag());
        Assertions.assertThat(entry2.getRemoteETag()).isEqualTo(objLocalOnly.getETag());
        Assertions.assertThat(entry3.getLocalETag()).isEqualTo("345");
        Assertions.assertThat(entry3.getLocalETag()).isEqualTo(objRemOnly.getETag());
        Assertions.assertThat(entry3.getRemoteETag()).isEqualTo(objRemOnly.getETag());
        Assertions.assertThat(entry4.getLocalETag()).isEqualTo("456");
        Assertions.assertThat(entry4.getLocalETag()).isEqualTo(objLocRemDiv.getETag());
        Assertions.assertThat(entry4.getRemoteETag()).isEqualTo(objLocRemDiv.getETag());


        List<SyncObjectLocal> localEntitiesAfter = localRepository.listAll();
        Assertions.assertThat(localEntitiesAfter.size()).isEqualTo(4);

        SyncObjectLocal loc1 = localRepository.findByUniqueId("1");
        SyncObjectLocal loc2 = localRepository.findByUniqueId("2");
        SyncObjectLocal loc3 = localRepository.findByUniqueId("3");
        SyncObjectLocal loc4 = localRepository.findByUniqueId("4");

        List<SyncObjectRemote> remoteEntitiesAfter = remoteRepository.listAll();
        Assertions.assertThat(remoteEntitiesAfter.size()).isEqualTo(4);

        SyncObjectRemote rem1 = remoteRepository.findByUniqueId("1");
        SyncObjectRemote rem2 = remoteRepository.findByUniqueId("2");
        SyncObjectRemote rem3 = remoteRepository.findByUniqueId("3");
        SyncObjectRemote rem4 = remoteRepository.findByUniqueId("4");

        Assertions.assertThat(loc1.getField()).isEqualTo("123");
        Assertions.assertThat(rem1.getField()).isEqualTo("123");
        Assertions.assertThat(loc2.getField()).isEqualTo("234");
        Assertions.assertThat(rem2.getField()).isEqualTo("234");
        Assertions.assertThat(loc3.getField()).isEqualTo("345");
        Assertions.assertThat(rem3.getField()).isEqualTo("345");
        Assertions.assertThat(loc4.getField()).isEqualTo("456");
        Assertions.assertThat(rem4.getField()).isEqualTo("456");
    }

    @Test
    public void sync_with_local_change_works() throws Exception {

        // given
        StatusEntry entry1 = statusEntryRepository.create("1", "123", "123"); // entry before local change
        objLocAndRem.setField("111"); // change to local instance
        Assertions.assertThat(objLocAndRem.getETag()).isEqualTo("111");


        List<SyncAble> localEntities = (List) localRepository.listAll();
        List<SyncAble> remoteEntities = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusList = (List) statusEntryRepository.listAll();

        // when
        syncServiceForIntegTest.sync(localEntities, remoteEntities, statusList);

        // then
        Assertions.assertThat(objRemAndLoc.getField()).isEqualTo(objLocAndRem.getETag());
        Assertions.assertThat(objRemAndLoc.getField()).isEqualTo("111");
        Assertions.assertThat(objRemAndLoc.getETag()).isEqualTo("111");
        Assertions.assertThat(entry1.getLocalETag()).isEqualTo(objLocAndRem.getETag());
        Assertions.assertThat(entry1.getRemoteETag()).isEqualTo(objLocAndRem.getETag());

    }

    @Test
    public void sync_with_remote_change_works() throws Exception {

        // given
        StatusEntry entry1 = statusEntryRepository.create("1", "123", "123"); // entry before remote change
        objRemAndLoc.setField("111"); // change to remote instance
        Assertions.assertThat(objRemAndLoc.getETag()).isEqualTo("111");


        List<SyncAble> localEntities = (List) localRepository.listAll();
        List<SyncAble> remoteEntities = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusList = (List) statusEntryRepository.listAll();

        // when
        syncServiceForIntegTest.sync(localEntities, remoteEntities, statusList);

        // then
        Assertions.assertThat(objLocAndRem.getField()).isEqualTo(objRemAndLoc.getETag());
        Assertions.assertThat(objLocAndRem.getField()).isEqualTo("111");
        Assertions.assertThat(objLocAndRem.getETag()).isEqualTo("111");
        Assertions.assertThat(entry1.getLocalETag()).isEqualTo(objRemAndLoc.getETag());
        Assertions.assertThat(entry1.getRemoteETag()).isEqualTo(objRemAndLoc.getETag());

    }

    @Test
    public void sync_with_local_delete_works() throws Exception {

        // given
        StatusEntry entry1 = statusEntryRepository.create("1", "123", "123"); // entry before local change
        localRepository.remove(objLocAndRem);

        List<SyncAble> localEntities = (List) localRepository.listAll();
        List<SyncAble> remoteEntities = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusList = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusList).contains(entry1);

        // when
        syncServiceForIntegTest.sync(localEntities, remoteEntities, statusList);

        // then
        List<SyncAble> localEntitiesAfter = (List) localRepository.listAll();
        List<SyncAble> remoteEntitiesAfter = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusListAfter = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusListAfter).doesNotContain(entry1);
        Assertions.assertThat(localEntitiesAfter.size()).isEqualTo(3);
        Assertions.assertThat(remoteEntitiesAfter.size()).isEqualTo(3);
        Assertions.assertThat(localRepository.findByUniqueId("1")).isNull();
        Assertions.assertThat(remoteRepository.findByUniqueId("1")).isNull();

    }

    @Test
    public void sync_with_remote_delete_works() throws Exception {
        // given
        StatusEntry entry1 = statusEntryRepository.create("1", "123", "123"); // entry before local change
        remoteRepository.remove(objRemAndLoc);

        List<SyncAble> localEntities = (List) localRepository.listAll();
        List<SyncAble> remoteEntities = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusList = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusList).contains(entry1);

        // when
        syncServiceForIntegTest.sync(localEntities, remoteEntities, statusList);

        // then
        List<SyncAble> localEntitiesAfter = (List) localRepository.listAll();
        List<SyncAble> remoteEntitiesAfter = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusListAfter = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusListAfter).doesNotContain(entry1);
        Assertions.assertThat(localEntitiesAfter.size()).isEqualTo(3);
        Assertions.assertThat(remoteEntitiesAfter.size()).isEqualTo(3);
        Assertions.assertThat(localRepository.findByUniqueId("1")).isNull();
        Assertions.assertThat(remoteRepository.findByUniqueId("1")).isNull();
    }

    @Test
    public void sync_with_conflict_works_local_instance_prevails_impl() throws Exception {

        // given
        StatusEntry entry1 = statusEntryRepository.create("1", "123", "123"); // entry before local change
        objLocAndRem.setField("111");
        Assertions.assertThat(objLocAndRem.getETag()).isEqualTo("111");
        objRemAndLoc.setField("222");
        Assertions.assertThat(objRemAndLoc.getETag()).isEqualTo("222");

        List<SyncAble> localEntities = (List) localRepository.listAll();
        List<SyncAble> remoteEntities = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusList = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusList).contains(entry1);

        // when
        syncServiceForIntegTest.sync(localEntities, remoteEntities, statusList);

        // then
        List<SyncAble> localEntitiesAfter = (List) localRepository.listAll();
        List<SyncAble> remoteEntitiesAfter = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusListAfter = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusListAfter).contains(entry1);
        Assertions.assertThat(localEntitiesAfter).contains(objLocAndRem);
        Assertions.assertThat(remoteEntitiesAfter).contains(objRemAndLoc);
        Assertions.assertThat(entry1.getLocalETag()).isEqualTo("111");
        Assertions.assertThat(entry1.getRemoteETag()).isEqualTo("111");
        Assertions.assertThat(objLocAndRem.getField()).isEqualTo("111");
        Assertions.assertThat(objRemAndLoc.getField()).isEqualTo("111");

    }

    @Test
    public void status_list_cleans_itself() throws Exception {
        // given
        StatusEntry entrySuperflous = statusEntryRepository.create("5", "bla", "bla");

        List<SyncAble> localEntities = (List) localRepository.listAll();
        List<SyncAble> remoteEntities = (List) remoteRepository.listAll();
        List<SyncAbleStatusEntry> statusList = (List) statusEntryRepository.listAll();

        Assertions.assertThat(statusList).contains(entrySuperflous);

        // when
        syncServiceForIntegTest.sync(localEntities, remoteEntities, statusList);

        // then
        List<SyncAbleStatusEntry> statusListAfter = (List) statusEntryRepository.listAll();
        Assertions.assertThat(statusListAfter).doesNotContain(entrySuperflous);
    }

    @Inject
    SyncServiceForIntegTest syncServiceForIntegTest;

    @Inject
    SyncObjectLocalRepository localRepository;

    @Inject
    SyncObjectRemoteRepository remoteRepository;

    @Inject
    StatusEntryRepository statusEntryRepository;

}