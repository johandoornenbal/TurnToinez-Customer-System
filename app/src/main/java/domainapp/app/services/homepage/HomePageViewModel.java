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
package domainapp.app.services.homepage;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.HomePage;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.i18n.TranslatableString;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.bestellingen.Status;
import domainapp.dom.bestelservice.BestellingInvoerViewmodel;
import domainapp.dom.bestelservice.Bestelservice;
import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;
import domainapp.dom.financieleadministratie.KostenPost;
import domainapp.dom.financieleadministratie.KostenPostRepository;
import domainapp.dom.siteservice.FormulierVanSite;
import domainapp.dom.siteservice.SiteService;
import domainapp.dom.uren.Uren;
import domainapp.dom.uren.UrenRepository;

@DomainObject(nature = Nature.VIEW_MODEL, auditing = Auditing.DISABLED)
public class HomePageViewModel {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("{num} betaalde bestellingen", "num", getBetaald().size());
    }
    //endregion

    @HomePage
    @CollectionLayout(named = "Betaald (werklijst)")
    public List<Bestelling> getBetaald() {
        return bestellingRepository.zoekOpStatus(Status.BETAALD);
    }

    @HomePage
    @CollectionLayout(named = "Nog niet betaald")
    public List<Bestelling> getOffertes() {
        return bestellingRepository.zoekOpStatus(Status.BESTELLING);
    }

    @HomePage
    @CollectionLayout(named = "Klaar")
    public List<Bestelling> getKlaar() {
        return bestellingRepository.laatstGemaakteBestellingen();
    }

    @HomePage
    public List<Bestelling> getKlad() {
        return bestellingRepository.zoekOpStatus(Status.KLAD);
    }

    public List<FormulierVanSite> haalBestellingenOpVanWebsite(){
        return siteService.haalBestellingenOp();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Boeking nieuweOnkostenBoeken(
            final LocalDate datum,
            final String omschrijving,
            final BigDecimal prijsIncl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijsExcl,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal btw,
            final KostenPost kostenPost,
            @Parameter(optionality = Optionality.OPTIONAL)
            final boolean berekenBtw,
            @Parameter(optionality = Optionality.OPTIONAL)
            final boolean laterBijwerken
    ){
        BoekingPost post = kostenPost.getPost();
        Boeking newBoeking = boekingRepository.create(datum, BoekingType.UIT, omschrijving, prijsIncl, prijsExcl, btw, post, kostenPost, null, laterBijwerken);
        return berekenBtw ? newBoeking.bereken() : newBoeking;
    }

    public LocalDate default0NieuweOnkostenBoeken(
    ){
        return clockService.now();
    }

    public boolean default6NieuweOnkostenBoeken(){
        return true;
    }

    public List<KostenPost> choices5NieuweOnkostenBoeken(){
        return kostenPostRepository.listAll();
    }

    public String validateNieuweOnkostenBoeken(
            final LocalDate datum,
            final String omschrijving,
            final BigDecimal prijsIncl,
            final BigDecimal prijsExcl,
            final BigDecimal btw,
            final KostenPost kostenPost,
            final boolean berekenBtw,
            final boolean laterBijwerken
    ){
        return boekingRepository.validate(berekenBtw, prijsExcl, btw);
    }

    public BestellingInvoerViewmodel nieuweBestellingEnKlant(
            @Parameter(optionality = Optionality.OPTIONAL)
            final String naamTurnster,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String leeftijd,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String kledingmaat,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String lengte,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Postuur postuur,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String artikel1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijs1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String opmerking1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String artikel2,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijs2,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String opmerking2,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String artikel3,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijs3,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String opmerking3

    ){
        return bestelservice.nieuweBestellingEnKlant(naamTurnster, leeftijd, kledingmaat, lengte,
                postuur, artikel1, prijs1, opmerking1, artikel2, prijs2, opmerking2, artikel3, prijs3, opmerking3);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Uren schrijfUren(final LocalDate datum, final int aantalUren){
        return urenRepository.schrijfUren(datum, aantalUren);
    }

    public LocalDate default0SchrijfUren(){
        return urenRepository.default0SchrijfUren();
    }

    public int default1SchrijfUren(){
        return urenRepository.default1SchrijfUren();
    }

    @javax.inject.Inject
    BestellingRepository bestellingRepository;

    @javax.inject.Inject SiteService siteService;

    @javax.inject.Inject BoekingRepository boekingRepository;

    @javax.inject.Inject KostenPostRepository kostenPostRepository;

    @javax.inject.Inject ClockService clockService;

    @javax.inject.Inject Bestelservice bestelservice;

    @javax.inject.Inject UrenRepository urenRepository;

}
