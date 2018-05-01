package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
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

    @Inject ExcelService excelService;

    @Inject RepositoryService repositoryService;

    @Inject VerdienRegelRepository verdienRegelRepository;

}
