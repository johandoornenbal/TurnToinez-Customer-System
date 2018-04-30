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
package domainapp.integtests.bootstrap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import domainapp.app.DomainAppAppModule;
import domainapp.dom.DomainAppDomainModule;
import domainapp.dom.syncengine.integtests.dom.StatusEntryRepository;
import domainapp.dom.syncengine.integtests.dom.SyncObjectLocalRepository;
import domainapp.dom.syncengine.integtests.dom.SyncObjectRemoteRepository;
import domainapp.fixture.DomainAppFixtureModule;

/**
 * Bootstrap the application.
 */
public class DomainAppAppManifestForIntegTests implements AppManifest {

    /**
     * Load all services and entities found in (the packages and subpackages within) these modules
     */
    @Override
    public List<Class<?>> getModules() {
        return Arrays.asList(
                DomainAppDomainModule.class,  // domain (entities and repositories)
                DomainAppFixtureModule.class, // fixtures
                DomainAppAppModule.class      // home page service etc
        );
    }

    /**
     * Registration of classes that are somehow are not detected when running with jetty-runner
     */
    @Override
    public List<Class<?>> getAdditionalServices() {
        return Arrays.asList(
                SyncObjectLocalRepository.class,
                SyncObjectRemoteRepository.class,
                StatusEntryRepository.class);
    }

    /**
     * Use shiro for authentication.
     *
     * <p>
     *     NB: this is ignored for integration tests, which always use "bypass".
     * </p>
     */
    @Override
    public String getAuthenticationMechanism() {
        return "bypass";
    }

    /**
     * Use shiro for authorization.
     *
     * <p>
     *     NB: this is ignored for integration tests, which always use "bypass".
     * </p>
     */
    @Override
    public String getAuthorizationMechanism() {
        return "bypass";
    }

    /**
     * No fixtures.
     */
    @Override
    public List<Class<? extends FixtureScript>> getFixtures() {
        return Collections.emptyList();
    }

    /**
     * No overrides.
     */
    @Override
    public Map<String, String> getConfigurationProperties() {
        HashMap<String,String> props = Maps.newHashMap();
        /*TODO: seems not to work; so I left entry in isis.properties*/
        props.put("isis.reflector.facets.include", "org.isisaddons.metamodel.paraname8.NamedFacetOnParameterParaname8Factory");
        return props;
    }

}
