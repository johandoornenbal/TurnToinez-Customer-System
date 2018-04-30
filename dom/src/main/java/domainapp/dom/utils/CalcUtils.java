package domainapp.dom.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import domainapp.dom.facturen.Btw;

public class CalcUtils {

    public static BigDecimal netFromGross(final BigDecimal amountGross, final Btw btw){
        return amountGross.divide(btw.getMultiplier(), MathContext.DECIMAL64).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal vatFromGross(final BigDecimal amountGross, final Btw btw){
        return amountGross.subtract(amountGross.divide(btw.getMultiplier(), MathContext.DECIMAL64).setScale(2, RoundingMode.HALF_UP));
    }

    public static BigDecimal grossFromNet(final BigDecimal amountNet, final Btw btw){
        return amountNet.multiply(btw.getMultiplier()).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal vatFromNet(final BigDecimal amountNet, final Btw btw){
        return amountNet.multiply(btw.getPercentage()).divide(new BigDecimal("100"), MathContext.DECIMAL64).setScale(2, RoundingMode.HALF_UP);
    }

}
