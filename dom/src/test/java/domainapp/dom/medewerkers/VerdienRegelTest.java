package domainapp.dom.medewerkers;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class VerdienRegelTest {

    VerdienRegel regel;

    @Before
    public void setUp(){
        regel = new VerdienRegel();
        regel.setKosten(new BigDecimal("0.15"));
        regel.setPrijs(new BigDecimal("1.21"));
        regel.setPercentage(50);
    }

    @Test
    public void getBtw() throws Exception {
        Assertions.assertThat(regel.getBtw()).isEqualTo(new BigDecimal("0.21"));
    }

    @Test
    public void getPrijsExBtw() throws Exception {
        Assertions.assertThat(regel.getPrijsExBtw()).isEqualTo(new BigDecimal("1.00"));
    }

    @Test
    public void getMarge() throws Exception {
        Assertions.assertThat(regel.getMarge()).isEqualTo(new BigDecimal("0.85"));
    }

    @Test
    public void calcVerdienste() throws Exception {
        // when
        regel.calcVerdienste();
        Assertions.assertThat(regel.getVerdienste()).isEqualTo(new BigDecimal("0.43"));
    }

}