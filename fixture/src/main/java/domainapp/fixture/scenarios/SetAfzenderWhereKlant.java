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

package domainapp.fixture.scenarios;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.dom.communicatie.Communicatie;
import domainapp.dom.communicatie.CommunicatieRepository;

public class SetAfzenderWhereKlant extends FixtureScript {

    public SetAfzenderWhereKlant(){
        withDiscoverability(Discoverability.DISCOVERABLE);
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        for (Communicatie communicatie : communicatieRepository.alleCommunicaties()){
            if (communicatie.getAfzender().equals("klant") || communicatie.getAfzender()==null){
                if (communicatie.getKlant().getEmail()!=null) {
                    communicatie.setAfzender(communicatie.getKlant().getEmail());
                }
            }
        }

    }

    @Inject CommunicatieRepository communicatieRepository;
}
