package domainapp.dom.utils;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import domainapp.dom.facturen.Btw;

public class CalcUtilsTest {

    final BigDecimal amountGross = new BigDecimal("1.21");
    final BigDecimal amountNet = new BigDecimal("1.00");
    final BigDecimal vat = new BigDecimal("0.21");

    @Test
    public void netFromGross() throws Exception {
        // given
        CalcUtils utils = new CalcUtils();
        // when, then
        Assertions.assertThat(utils.netFromGross(amountGross, Btw.BTW_21_PERC)).isEqualTo(amountNet);
    }

    @Test
    public void vatFromGross() throws Exception {
        // given
        CalcUtils utils = new CalcUtils();
        // when, then
        Assertions.assertThat(utils.vatFromGross(amountGross, Btw.BTW_21_PERC)).isEqualTo(vat);
    }

    @Test
    public void grossFromNet() throws Exception {
        // given
        CalcUtils utils = new CalcUtils();
        // when, then
        Assertions.assertThat(utils.grossFromNet(amountNet, Btw.BTW_21_PERC)).isEqualTo(amountGross);
    }

    @Test
    public void vatFromNet() throws Exception {
        // given
        CalcUtils utils = new CalcUtils();
        // when, then
        Assertions.assertThat(utils.vatFromNet(amountNet, Btw.BTW_21_PERC)).isEqualTo(vat);
    }

}