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

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Digits;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;
import domainapp.dom.financieleadministratie.KostenPost;
import domainapp.dom.financieleadministratie.KostenPostRepository;
import domainapp.dom.medewerkers.KostenRegel;
import domainapp.dom.medewerkers.KostenRegelRepository;
import domainapp.dom.medewerkers.Medewerker;
import domainapp.dom.medewerkers.MedewerkerService;
import domainapp.dom.medewerkers.Saldo;
import domainapp.dom.medewerkers.VerdienRegel;
import domainapp.dom.medewerkers.VerdienRegelRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Financiën",
        menuOrder = "10"
)
public class FinancienMenu {

    @MemberOrder(sequence = "1")
    public Saldo overzichtGrietje(){
        return new Saldo(Medewerker.GRIETJE);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public VerdienRegel nieuweVerdiensteGrietje(
            final LocalDate datum,
            final String onderwerp,
            @Digits(integer = 10, fraction = 2)
            final BigDecimal verkoopprijs,
            @Digits(integer = 10, fraction = 2)
            final BigDecimal kosten,
            final Integer percentage,
            @Nullable
            final String aantekening
    ){
        return verdienRegelRepository.create(datum, onderwerp, verkoopprijs, kosten, percentage, aantekening, Medewerker.GRIETJE);
    }

    public LocalDate default0NieuweVerdiensteGrietje(){
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "3")
    public KostenRegel nieuweKostenGrietje(
            final LocalDate datum,
            final String onderwerp,
            @Digits(integer = 10, fraction = 2)
            final BigDecimal bedrag,
            @Nullable
            final String aantekening
    ){
        return kostenRegelRepository.create(datum, onderwerp, bedrag, aantekening, Medewerker.GRIETJE);
    }

    public LocalDate default0NieuweKostenGrietje(){
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "4")
    public List<Boeking> vindBoekingen(
            final LocalDate startDatum,
            final LocalDate eindDatum
    ){
        return boekingRepository.findByDatumBetween(startDatum, eindDatum);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "5")
    public List<Boeking> vindBoekingenOmBijTeWerken(){
        return boekingRepository.laterBijwerken().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "6")
    public Boeking nieuweOnkosten(
            final LocalDate datum,
            final String omschrijving,
            @Digits(integer = 10, fraction = 2)
            final BigDecimal prijsIncl,
            @Parameter(optionality = Optionality.OPTIONAL)
            @Digits(integer = 10, fraction = 2)
            final BigDecimal prijsExcl,
            @Parameter(optionality = Optionality.OPTIONAL)
            @Digits(integer = 10, fraction = 2)
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

    public LocalDate default0NieuweOnkosten(
    ){
        return clockService.now();
    }

    public boolean default6NieuweOnkosten(){
        return true;
    }

    public List<KostenPost> choices5NieuweOnkosten(){
        return kostenPostRepository.listAll();
    }

    public String validateNieuweOnkosten(
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

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "7")
    public List<Boeking> alleBoekingen(){
        return boekingRepository.listAll();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "8")
    public Saldo importVerdiensteGrietje(final Blob sheet){
        medewerkerService.importVerdienste(sheet, Medewerker.GRIETJE);
        return new Saldo(Medewerker.GRIETJE);
    }

    @Inject
    private BoekingRepository boekingRepository;

    @Inject
    private KostenPostRepository kostenPostRepository;

    @Inject
    private ClockService clockService;

    @Inject
    VerdienRegelRepository verdienRegelRepository;

    @Inject
    KostenRegelRepository kostenRegelRepository;

    @Inject
    BestellingRepository bestellingRepository;

    @Inject
    MedewerkerService medewerkerService;


}
