package domainapp.dom.financieleadministratie.overzichten;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.JDOHelper;

import org.apache.wicket.util.string.StringValue;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;

@DomainService()
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.SECONDARY, named = "Overzichten")
public class OverzichtService {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob kwartaalOverzicht(final Integer jaar, final Integer kwartaal) {
        final String fileName =  "turntoinez kwartaaloverzicht " + StringValue.valueOf(jaar)+ "-Q" + StringValue.valueOf(kwartaal) + ".xlsx";
        WorksheetSpec spec = new WorksheetSpec(KwartaalOverzichtRegel.class, "overzicht");
        WorksheetContent worksheetContent = new WorksheetContent(overzichtRegels(jaar, kwartaal), spec);
        return excelService.toExcelPivot(worksheetContent, fileName);
    }

    public String validateKwartaalOverzicht(final Integer jaar, final Integer kwartaal){
        if (kwartaal<1 || kwartaal >4){
            return "Een kwartaal kan 1-4 zijn";
        }
        if (jaar < 2007 || jaar > 2999){
            return "Dat jaar zal niet kloppen waarschijnlijk ... (Kies 2007 - 2999)";
        }
        return null;
    }

    private List<KwartaalOverzichtRegel> overzichtRegels(final Integer jaar, final Integer kwartaal){
        LocalDate begindatum;
        LocalDate einddatum;
        switch (kwartaal){
        // defaults to first quarter
        case 2:
            begindatum = new LocalDate(jaar, 04,01);
            einddatum = new LocalDate(jaar, 06, 30);
            break;
        case 3:
            begindatum = new LocalDate(jaar, 07,01);
            einddatum = new LocalDate(jaar, 9, 30);
            break;
        case 4:
            begindatum = new LocalDate(jaar, 10,01);
            einddatum = new LocalDate(jaar, 12, 31);
            break;
        default:
            begindatum = new LocalDate(jaar, 01,01);
            einddatum = new LocalDate(jaar, 03, 31);
            break;
        }
        List<KwartaalOverzichtRegel> result = new ArrayList<>();
        for (Boeking boeking : boekingRepository.findByDatumBetween(begindatum, einddatum)){
            result.add(
                new KwartaalOverzichtRegel(
                    boeking.getDatum().toString("MM"),
                    boeking.getType().name(),
                    boeking.getBtw(),
                    boeking.getPrijsIncl(),
                    boeking.getPrijsExcl(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    JDOHelper.getObjectId(boeking).toString().split("\\[OID\\]")[0],
                    boeking.getOmschrijving(),
                    boeking.getPost().name(),
                    boeking.getKostenPost()!=null ? boeking.getKostenPost().getNaam() : "",
                    boeking.getDatum().toString("dd-MM-yyyy")
                )
            );
            result.add(
                new KwartaalOverzichtRegel(
                    boeking.getDatum().toString("MM"),
                    "saldo",
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    boeking.getType()==BoekingType.IN ? boeking.getBtw() : boeking.getBtw().negate(),
                    boeking.getType()==BoekingType.IN ? boeking.getPrijsIncl() : boeking.getPrijsIncl().negate(),
                    boeking.getType()==BoekingType.IN ? boeking.getPrijsExcl() : boeking.getPrijsExcl().negate(),
                    JDOHelper.getObjectId(boeking).toString().split("\\[OID\\]")[0],
                    boeking.getOmschrijving(),
                    boeking.getPost().name(),
                    boeking.getKostenPost()!=null ? boeking.getKostenPost().getNaam() : "",
                    boeking.getDatum().toString("dd-MM-yyyy")
                )
            );
        }
        return result.stream().sorted().collect(Collectors.toList());
    }

    public List<JaarOverzichtRegel> jaarOverzicht(final Integer jaar){
        List<JaarOverzichtRegel> result = new ArrayList<>();
        LocalDate startJaar = new LocalDate(jaar, 01, 01);
        LocalDate eindJaar = new LocalDate(jaar, 12, 31);
        for (Boeking boeking : boekingRepository.findByDatumBetween(startJaar, eindJaar)){
            JaarOverzichtRegel helper = null;
            if (result.stream().filter(x->x.getMaand().equals(maandNaam(boeking.getDatum().getMonthOfYear()))).collect(Collectors.toList()).size()>0){
                helper =  result.stream().filter(x->x.getMaand().equals(maandNaam(boeking.getDatum().getMonthOfYear()))).collect(Collectors.toList()).get(0);
            }
            if (helper==null){
                result.add(
                    new JaarOverzichtRegel(
                        maandNaam(boeking.getDatum().getMonthOfYear()),
                        boeking.getType()==BoekingType.IN ? boeking.getBtw() : BigDecimal.ZERO,
                        boeking.getType()==BoekingType.IN ? boeking.getPrijsIncl() : BigDecimal.ZERO,
                        boeking.getType()==BoekingType.IN ? boeking.getPrijsExcl() : BigDecimal.ZERO,
                        boeking.getType()==BoekingType.UIT ? boeking.getBtw() : BigDecimal.ZERO,
                        boeking.getType()==BoekingType.UIT ? boeking.getPrijsIncl() : BigDecimal.ZERO,
                        boeking.getType()==BoekingType.UIT ? boeking.getPrijsExcl() : BigDecimal.ZERO,
                        String.valueOf(jaar),
                        boeking.getDatum().getMonthOfYear()
                    )
                );
            } else {
                helper.add(
                    boeking.getType()==BoekingType.IN ? boeking.getBtw() : BigDecimal.ZERO,
                    boeking.getType()==BoekingType.IN ? boeking.getPrijsIncl() : BigDecimal.ZERO,
                    boeking.getType()==BoekingType.IN ? boeking.getPrijsExcl() : BigDecimal.ZERO,
                    boeking.getType()==BoekingType.UIT ? boeking.getBtw() : BigDecimal.ZERO,
                    boeking.getType()==BoekingType.UIT ? boeking.getPrijsIncl() : BigDecimal.ZERO,
                    boeking.getType()==BoekingType.UIT ? boeking.getPrijsExcl() : BigDecimal.ZERO
                );
            }
        }
        return result.stream().sorted().collect(Collectors.toList());
    }

    public Integer default0JaarOverzicht(){
        return clockService.now().getYear();
    }

    private String maandNaam(final int maandNummer){
        switch (maandNummer){
        case 1:
            return "01 Januari";
        case 2:
            return "02 Februari";
        case 3:
            return "03 Maart";
        case 4:
            return "04 April";
        case 5:
            return "05 Mei";
        case 6:
            return "06 Juni";
        case 7:
            return "07 Juli";
        case 8:
            return "08 Augustus";
        case 9:
            return "09 September";
        case 10:
            return "10 Oktober";
        case 11:
            return "11 November";
        case 12:
            return "12 December";
        default:
            return "Onbekend";
        }
    }

    public List<Bestelling> bestellingenOverzicht(final LocalDate vanaf){

        return bestellingRepository.gemaakteBestellingenVanaf(vanaf);

    }

    public LocalDate default0BestellingenOverzicht(final LocalDate vanaf){
        return LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1).minusMonths(1);
    }

    @Inject ExcelService excelService;

    @Inject BoekingRepository boekingRepository;

    @Inject ClockService clockService;

    @Inject BestellingRepository bestellingRepository;

}
