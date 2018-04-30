package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

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

}
