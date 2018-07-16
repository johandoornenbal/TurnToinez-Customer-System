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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import domainapp.dom.document.DocumentExportService;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.KostenPost;
import domainapp.dom.financieleadministratie.KostenPostRepository;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        named = "Instellingen"
)
public class InstellingenMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public KostenPost nieuweKostenPost(final String naam, final BoekingPost boekingPost){
        return kostenPostRepository.findOrCreate(naam, boekingPost);
    }

    public String validate1NieuweKostenPost(final BoekingPost boekingPost){
        return kostenPostRepository.validateCreate(boekingPost);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<KostenPost> alleKostenPosten(){
        return kostenPostRepository.listAll();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @CollectionLayout(defaultView = "Map")
    public List<Klant> zoekKlantLocaties(){
        for (Klant klant : klantRepository.alleKlanten()){
            if (klant.getLocation()==null) {
                klant.zetPinOpGoogleMaps();
            }
        }
        return klantRepository.alleKlanten();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void exportBlobs(final LocalDate before, final boolean onlyExportNoDelete){
        documentExportService.exportDocumentBlobs(before, onlyExportNoDelete);
    }

    public String validateExportBlobs(final LocalDate before, final boolean onlyExportNoDelete){
        LocalDate threshold = clockService.now().minusMonths(6);
        if (!onlyExportNoDelete && before.isAfter(threshold)){
            return "Kies een datum die langer dan een half jaar geleden is";
        }
        return null;
    }

    public LocalDate default0ExportBlobs(){
        return clockService.now().minusMonths(6);
    }

    @Inject
    public KlantRepository klantRepository;

    @Inject
    private KostenPostRepository kostenPostRepository;

    @Inject
    DocumentExportService documentExportService;

    @Inject
    ClockService clockService;

}
