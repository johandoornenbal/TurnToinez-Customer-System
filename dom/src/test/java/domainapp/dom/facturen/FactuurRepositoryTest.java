package domainapp.dom.facturen;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.Regel;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class FactuurRepositoryTest {

    public static class FactuurCalculation extends FactuurRepositoryTest {

        FactuurRepository factuurRepository = new FactuurRepository();

        @Test
        public void test_een_regel() throws Exception {

            // given
            Regel r1 = new Regel();
            r1.setPrijs(new BigDecimal("1.21"));
            Bestelling bestelling = new Bestelling(){
                @Override
                public SortedSet<Regel> getRegels() { return new TreeSet<>(Arrays.asList(r1)); }
            };

            // when
            FactuurRepository.FactuurCalculation calculation = factuurRepository.calculate(bestelling);

            // then
            assertThat(calculation.totaalExclBtw).isEqualTo(new BigDecimal("1.00"));
            assertThat(calculation.totaalInclBtw).isEqualTo(new BigDecimal("1.21"));
            assertThat(calculation.totaalBtw).isEqualTo(new BigDecimal("0.21"));

        }

        @Test
        public void test_meerdere_regels() throws Exception {

            // given
            Regel r1 = new Regel();
            r1.setPrijs(new BigDecimal("1.21"));
            Regel r2 = new Regel();
            r2.setPrijs(new BigDecimal("12.10"));
            Bestelling bestelling = new Bestelling(){
                @Override
                public SortedSet<Regel> getRegels() { return new TreeSet<>(Arrays.asList(r1, r2)); }
            };

            // when
            FactuurRepository.FactuurCalculation calculation = factuurRepository.calculate(bestelling);

            // then
            assertThat(calculation.totaalExclBtw).isEqualTo(new BigDecimal("11.00"));
            assertThat(calculation.totaalInclBtw).isEqualTo(new BigDecimal("13.31"));
            assertThat(calculation.totaalBtw).isEqualTo(new BigDecimal("2.31"));

        }

    }

}