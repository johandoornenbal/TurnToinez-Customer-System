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
package domainapp.dom.syncengine.integtests.dom;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import domainapp.dom.syncengine.SyncAble;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "sync"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByUniqueId", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.dom.syncengine.integtests.dom.SyncObjectLocal "
                        + "WHERE uniqueId == :uniqueId ")
})
@javax.jdo.annotations.Unique(name="ObjectLocal_uniqueId_UNQ", members = {"uniqueId"})
@DomainObject
public class SyncObjectLocal implements SyncAble {

    @Getter @Setter
    @Column(allowsNull = "false")
    private String uniqueId;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String field;

    @Override
    public String getETag() {
        return getField();
    }

}
