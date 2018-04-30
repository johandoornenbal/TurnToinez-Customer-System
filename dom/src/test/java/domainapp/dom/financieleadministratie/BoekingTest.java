package domainapp.dom.financieleadministratie;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import domainapp.dom.instellingen.Instellingen;
import domainapp.dom.instellingen.InstellingenRepository;

public class BoekingTest {

    Boeking boeking = new Boeking();

    @Test
    public void calculateFromPrijsIncl() throws Exception {

        // given
        boeking.setPrijsIncl(new BigDecimal("1.82"));
        boeking.setPrijsExcl(new BigDecimal("100"));
        boeking.setBtw(new BigDecimal("100"));

        // when
        boeking.calculateAmounts();

        // then
        Assertions.assertThat(boeking.getPrijsExcl()).isEqualTo(new BigDecimal("1.50"));
        Assertions.assertThat(boeking.getBtw()).isEqualTo(new BigDecimal("0.32"));
    }

    @Test
    public void calculateFromPrijsExcl() throws Exception {

        // given
        boeking.setPrijsExcl(new BigDecimal("1.50"));
        boeking.setBtw(new BigDecimal("100"));

        // when
        boeking.calculateAmounts();

        // then
        Assertions.assertThat(boeking.getPrijsIncl()).isEqualTo(new BigDecimal("1.82"));
        Assertions.assertThat(boeking.getBtw()).isEqualTo(new BigDecimal("0.32"));
    }

    @Test
    public void calculateFromBtw() throws Exception {

        // given
        boeking.setBtw(new BigDecimal("0.32"));

        // when
        boeking.calculateAmounts();

        // then
        Assertions.assertThat(boeking.getPrijsIncl()).isEqualTo(new BigDecimal("1.84"));
        Assertions.assertThat(boeking.getPrijsExcl()).isEqualTo(new BigDecimal("1.52"));
    }

    @Test
    public void boekingAfsluitDatum_werkt() throws Exception {

        Instellingen instellingen;
        LocalDate endDate = new LocalDate(2016,12,31);

        instellingen = new Instellingen(){
            @Override
            public LocalDate getBoekingAfsluitDatum(){
                return endDate;
            }
        };

        InstellingenRepository instellingenRepository = new InstellingenRepository(){
            @Override
            public Instellingen instellingen() {
                return instellingen;
            }
        };

        BoekingRepository boekingRepository = new BoekingRepository(){
            @Override
            public String validate(final Boolean berekenBtw, final BigDecimal prijsExcl, final BigDecimal btw){
                return null;
            }
        };

        // given
        Boeking boeking = new Boeking();
        boeking.instellingenRepository = instellingenRepository;
        boeking.boekingRepository = boekingRepository;
        // when
        boeking.setDatum(endDate);
        // then
        Assertions.assertThat(
                boeking.validateNieuweOnkosten(endDate, null, null,null,null, null,false, false)
        ).isEqualTo("De datum mag niet in een afgesloten periode liggen");
        Assertions.assertThat(
                boeking.validateWijzig(endDate, null, new BigDecimal("10.00"),null,null, null,false, false)
        ).isEqualTo("De datum mag niet in een afgesloten periode liggen");
        Assertions.assertThat(
                boeking.validateBereken()
        ).isEqualTo("De datum mag niet in een afgesloten periode liggen");

        // and when
        LocalDate date = new LocalDate(2017,01,01);
        boeking.setDatum(date);
        // then
        Assertions.assertThat(
                boeking.validateNieuweOnkosten(date, null, null,null,null, null,false, false)
        ).isNull();
        Assertions.assertThat(
                boeking.validateWijzig(date, null, new BigDecimal("10.00"),null,null, null,false, false)
        ).isNull();
        Assertions.assertThat(
                boeking.validateBereken()
        ).isNull();

    }

}