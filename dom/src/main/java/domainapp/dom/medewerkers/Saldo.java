package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Digits;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import lombok.Getter;
import lombok.Setter;

@ViewModel
public class Saldo {

    public String title(){
        return  "Saldo " + getMedewerker().name();
    }

    public Saldo(){}

    public Saldo(final Medewerker medewerker){
        this.medewerker = medewerker;
    }

    @Getter @Setter
    private Medewerker medewerker;

    @Action(semantics = SemanticsOf.SAFE)
    public BigDecimal getSaldo(){
        return medewerkerService.saldo(getVerdienste(), getKosten());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public BigDecimal getTotaalVerdienste(){
        return medewerkerService.totaalVerdienste(getVerdienste());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public BigDecimal getTotaalKosten(){
        return medewerkerService.totaalKosten(getKosten());
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<VerdienRegel> getVerdienste(){
        return verdienRegelRepository.findByMedewerker(medewerker);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<KostenRegel> getKosten(){
        return kostenRegelRepository.findByMedewerker(medewerker);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public VerdienRegel nieuweVerdienste(
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
        return verdienRegelRepository.create(datum, onderwerp, verkoopprijs, kosten, percentage, aantekening,  getMedewerker());
    }

    public LocalDate default0NieuweVerdienste(){
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "2")
    public KostenRegel nieuweKosten(
            final LocalDate datum,
            final String onderwerp,
            @Digits(integer = 10, fraction = 2)
            final BigDecimal bedrag,
            @Nullable
            final String aantekening
    ){
        return kostenRegelRepository.create(datum, onderwerp, bedrag, aantekening, getMedewerker());
    }

    public LocalDate default0NieuweKosten(){
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public Blob exportNaarExcel(){
        return medewerkerService.exportNaarExcel(getMedewerker());
    }

    @Inject
    VerdienRegelRepository verdienRegelRepository;

    @Inject
    KostenRegelRepository kostenRegelRepository;

    @Inject
    MedewerkerService medewerkerService;

    @Inject
    ClockService clockService;



}
