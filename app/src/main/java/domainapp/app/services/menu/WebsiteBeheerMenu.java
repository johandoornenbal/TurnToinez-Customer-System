/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
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
package domainapp.app.services.menu;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.core.commons.config.IsisConfiguration;

import domainapp.dom.restclient.RestClientService;
import domainapp.dom.websitemaintenance.ApiCheckDto;
import domainapp.dom.websitemaintenance.WebsiteLanguage;
import domainapp.dom.websitemaintenance.faq.Faq;
import domainapp.dom.websitemaintenance.faq.FaqRepository;
import domainapp.dom.websitemaintenance.faq.FaqService;
import domainapp.dom.websitemaintenance.faq.FaqVm;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Website Beheer",
        menuOrder = "110"
)
public class WebsiteBeheerMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public List<Faq> veelGesteldeVragen(final WebsiteLanguage language){
        return faqRepository.findByLanguage(language);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Faq> sync(){
        return faqService.syncWithRemote();
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<FaqVm> veelGesteldeVragenRemote() throws Exception {
        String secret = this.configuration.getString("isis.service.ttiapi.secret");
        List<FaqVm> listResult = (List<FaqVm>) restClientService.callRestApi(FaqVm.class, "http://www.turntoinez.nl/api/backendApi.php/faq?" + secret, HttpMethod.GET, "");
        return listResult;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public ApiCheckDto checkApi() throws Exception {
        String secret = this.configuration.getString("isis.service.ttiapi.secret");
        return (ApiCheckDto) restClientService.callRestApi(ApiCheckDto.class, "http://www.turntoinez.nl/api?" + secret, HttpMethod.GET, "");
    }

    @Inject
    private FaqRepository faqRepository;

    @Inject
    private RestClientService restClientService;

    @Inject
    private IsisConfiguration configuration;

    @Inject
    private FaqService faqService;


}
