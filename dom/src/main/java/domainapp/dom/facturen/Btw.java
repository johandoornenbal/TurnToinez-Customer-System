package domainapp.dom.facturen;

import java.math.BigDecimal;

import lombok.Getter;

public enum Btw {
    BTW_21_PERC(new BigDecimal("21.00"), new BigDecimal("1.21"));

    @Getter
    private BigDecimal percentage;

    @Getter
    private BigDecimal multiplier;

    Btw(final BigDecimal percentage, final BigDecimal multiplier){
        this.percentage = percentage;
        this.multiplier = multiplier;
    }
}
