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
package domainapp.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.mixins.DocumentTemplateForAtPathService;
import org.incode.module.document.dom.services.DocumentCreatorService;

import domainapp.app.services.api.Api;
import domainapp.dom.DomainAppDomainModule;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.document.DocumentExportService;
import domainapp.dom.facturen.FactuurRepository;
import domainapp.dom.financieleadministratie.overzichten.OverzichtService;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.mailchimpintegration.TtiMailChimpService;
import domainapp.dom.mailchimpintegration.module.api.MailChimpService;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpListMemberLinkRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpListRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpMemberRepository;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpRestApiService;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpServiceImplementation;
import domainapp.dom.medewerkers.KostenRegelRepository;
import domainapp.dom.medewerkers.MedewerkerService;
import domainapp.dom.medewerkers.VerdienRegelRepository;
import domainapp.dom.websitemaintenance.faq.FaqRepository;
import domainapp.dom.websitemaintenance.faq.FaqService;
import domainapp.fixture.DomainAppFixtureModule;

/**
 * Bootstrap the application.
 */
public class DomainAppAppManifest implements AppManifest {

    /**
     * Load all services and entities found in (the packages and subpackages within) these modules
     */
    @Override
    public List<Class<?>> getModules() {
        return Arrays.asList(
                DomainAppDomainModule.class,  // domain (entities and repositories)
                DomainAppFixtureModule.class, // fixtures
                DomainAppAppModule.class,      // home page service etc
                org.isisaddons.module.audit.AuditModule.class,
                org.isisaddons.module.command.CommandModule.class,
                org.incode.module.document.dom.DocumentModule.class,
                org.incode.module.docrendering.xdocreport.dom.XDocReportDocRenderingModule.class,
                org.incode.module.docrendering.stringinterpolator.dom.StringInterpolatorDocRenderingModule.class,
                org.incode.module.docrendering.freemarker.dom.FreemarkerDocRenderingModule.class,
                org.isisaddons.module.stringinterpolator.StringInterpolatorModule.class,
                org.isisaddons.module.freemarker.dom.FreeMarkerModule.class,
                org.isisaddons.module.xdocreport.dom.service.XDocReportService.class,
                org.isisaddons.module.excel.ExcelModule.class,
                org.isisaddons.wicket.gmap3.cpt.applib.Gmap3ApplibModule.class,
                org.isisaddons.wicket.gmap3.cpt.service.Gmap3ServiceModule.class
        );
    }

    /**
     * Registration of classes that are somehow are not detected when running with jetty-runner
     */
    @Override
    public List<Class<?>> getAdditionalServices() {
        return Arrays.asList(
                KlantRepository.class,
                BestellingRepository.class,
                DocumentTemplateForAtPathService.class,
                DocumentTemplateRepository.class,
                DocumentTypeRepository.class,
                DocumentCreatorService.class,
                PaperclipRepository.class,
                OverzichtService.class,
                MailChimpService.class,
                MailChimpListRepository.class,
                MailChimpMemberRepository.class,
                MailChimpListMemberLinkRepository.class,
                MailChimpRestApiService.class,
                MailChimpServiceImplementation.class,
                FaqRepository.class,
                FaqService.class,
                TtiMailChimpService.class,
                Api.class,
                MedewerkerService.class,
                VerdienRegelRepository.class,
                KostenRegelRepository.class,
                DocumentExportService.class,
                FactuurRepository.class);
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
