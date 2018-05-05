package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

@DomainService(nature = NatureOfService.DOMAIN)
public class MedewerkerService {

    @Programmatic
    public BigDecimal totaalVerdienste(final List<VerdienRegel> verdienRegels){
        BigDecimal result = BigDecimal.ZERO;
        for (VerdienRegel verdienRegel : verdienRegels){
            result = result.add(verdienRegel.getVerdienste());
        }
        return result;
    }

    @Programmatic
    public BigDecimal totaalKosten(final List<KostenRegel> kostenRegels){
        BigDecimal result = BigDecimal.ZERO;
        for (KostenRegel kostenRegel : kostenRegels){
            result = result.add(kostenRegel.getBedrag());
        }
        return result;
    }

    @Programmatic
    public BigDecimal saldo(final List<VerdienRegel> verdienRegels, final List<KostenRegel> kostenRegels){
        return totaalVerdienste(verdienRegels).subtract(totaalKosten(kostenRegels));
    }

    @Programmatic
    public void importVerdienste(final Blob sheet, final Medewerker medewerker){

        List<VerdienRegel> regels = excelService.fromExcel(sheet, VerdienRegel.class, "inkomsten", Mode.RELAXED);
        regels.forEach(x->{
            if (x.getKosten()==null) x.setKosten(BigDecimal.ZERO);
            if (x.getDatum()!=null && verdienRegelRepository.findByDatumAndPrijsAndOnderwerpAndPercentageAndKostenAndMedewerker(
                    x.getDatum(),
                    x.getPrijs(),
                    x.getOnderwerp(),
                    x.getPercentage(),
                    x.getKosten(),
                    x.getMedewerker()
            ) == null) {
                x.setMedewerker(medewerker);
                x.calcVerdienste();
                repositoryService.persistAndFlush(x);
            }
        });

    }

    @Programmatic
    public Blob exportNaarExcel(final Medewerker medewerker){

        final String currentDate = clockService.now().toString("dd-MM-yyyy");
        final List<VerdienRegel> verdienRegels = verdienRegelRepository.findByMedewerker(medewerker);
        final List<KostenRegel> kostenRegels = kostenRegelRepository.findByMedewerker(medewerker);
        final BigDecimal totaalVerdienste = totaalVerdienste(verdienRegels);

        List<SaldoTotaalRegel> totalen = new ArrayList<>();

        totalen.add(SaldoTotaalRegel.builder().datum(currentDate).build());
        totalen.add(
                SaldoTotaalRegel.builder().
                        totalen("Totaal verdienste").
                        bedrag(totaalVerdienste.setScale(2, RoundingMode.HALF_UP)).build()
        );

        final BigDecimal totaalKosten = totaalKosten(kostenRegels);
        totalen.add(
                SaldoTotaalRegel.builder().
                        totalen("Totaal kosten").
                        bedrag(totaalKosten.setScale(2, RoundingMode.HALF_UP)).build()
        );
        totalen.add(
                SaldoTotaalRegel.builder().
                        totalen("Saldo").
                        bedrag(totaalVerdienste.subtract(totaalKosten).setScale(2, RoundingMode.HALF_UP)).build()
        );

        WorksheetSpec specTotalen = new WorksheetSpec(SaldoTotaalRegel.class, "totalen");
        WorksheetContent contentTotalen = new WorksheetContent(totalen, specTotalen);

        WorksheetSpec specVerdienste = new WorksheetSpec(VerdienRegel.class, "verdienste");
        WorksheetContent contentVerdienste = new WorksheetContent(verdienRegels, specVerdienste);

        WorksheetSpec specKosten = new WorksheetSpec(KostenRegel.class, "kosten");
        WorksheetContent contentKosten = new WorksheetContent(kostenRegels, specKosten);

        return excelService.toExcel(Arrays.asList(contentTotalen, contentVerdienste, contentKosten), "Saldo " + medewerker.name() + " - " + currentDate + ".xlsx");
    }

    @Inject ExcelService excelService;

    @Inject RepositoryService repositoryService;

    @Inject VerdienRegelRepository verdienRegelRepository;

    @Inject KostenRegelRepository kostenRegelRepository;

    @Inject ClockService clockService;

}
