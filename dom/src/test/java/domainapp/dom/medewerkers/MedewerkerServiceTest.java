package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class MedewerkerServiceTest {

    List<VerdienRegel> verdienRegels;
    List<KostenRegel> kostenRegels;

    @Before
    public void setUp(){
        VerdienRegel vr1 = new VerdienRegel();
        vr1.setVerdienste(new BigDecimal("12.34"));
        VerdienRegel vr2 = new VerdienRegel();
        vr2.setVerdienste(new BigDecimal("00.06"));
        KostenRegel kr1 = new KostenRegel();
        kr1.setBedrag(new BigDecimal("1.00"));
        KostenRegel kr2 = new KostenRegel();
        kr2.setBedrag(new BigDecimal("0.39"));
        KostenRegel kr3 = new KostenRegel();
        kr3.setBedrag(new BigDecimal("-0.10"));
        verdienRegels = Arrays.asList(vr1, vr2);
        kostenRegels = Arrays.asList(kr1, kr2, kr3);
    }

    @Test
    public void totaalVerdienste() throws Exception {
        MedewerkerService service = new MedewerkerService();
        Assertions.assertThat(service.totaalVerdienste(verdienRegels)).isEqualTo(new BigDecimal("12.40"));
    }

    @Test
    public void totaalKosten() throws Exception {
        MedewerkerService service = new MedewerkerService();
        Assertions.assertThat(service.totaalKosten(kostenRegels)).isEqualTo(new BigDecimal("1.29"));
    }

    @Test
    public void saldo() throws Exception {
        MedewerkerService service = new MedewerkerService();
        Assertions.assertThat(service.saldo(verdienRegels, kostenRegels)).isEqualTo(new BigDecimal("11.11"));
    }

}