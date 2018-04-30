package domainapp.dom.financieleadministratie.overzichten;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.isisaddons.module.excel.dom.PivotColumn;
import org.isisaddons.module.excel.dom.PivotRow;
import org.isisaddons.module.excel.dom.PivotValue;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class KwartaalOverzichtRegel implements Comparable<KwartaalOverzichtRegel>{

    public KwartaalOverzichtRegel(
            final String maand,
            final String inUit,
            final BigDecimal btw,
            final BigDecimal incl,
            final BigDecimal excl,
            final BigDecimal btwSaldo,
            final BigDecimal inclSaldo,
            final BigDecimal exclSaldo,
            final String id,
            final String omschrijving,
            final String post,
            final String kostenPost,
            final String datum){
        this.maand = maand;
        this.inUit = inUit;
        this.btw = btw;
        this.incl = incl;
        this.excl = excl;
        this.btwSaldo = btwSaldo;
        this.inclSaldo = inclSaldo;
        this.exclSaldo = exclSaldo;
        this.id = id;
        this.omschrijving = omschrijving;
        this.post = post;
        this.kostenPost = kostenPost;
        this.datum = datum;
    }

    @PivotRow
    @Getter @Setter
    private String maand;

    @PivotColumn(order = 1)
    @Getter @Setter
    private String inUit;

    @PivotValue(order = 1)
    @Getter @Setter
    private BigDecimal btw;

    @PivotValue(order = 2)
    @Getter @Setter
    private BigDecimal excl;

    @PivotValue(order = 3)
    @Getter @Setter
    private BigDecimal incl;

    @PivotValue(order = 4)
    @Getter @Setter
    private BigDecimal btwSaldo;

    @PivotValue(order = 5)
    @Getter @Setter
    private BigDecimal inclSaldo;

    @PivotValue(order = 6)
    @Getter @Setter
    private BigDecimal exclSaldo;

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String omschrijving;

    @Getter @Setter
    private String post;

    @Getter @Setter
    private String kostenPost;

    @Getter @Setter
    private String datum;

    @Override
    public int compareTo(final KwartaalOverzichtRegel o) {
        return getMaand().compareTo(o.getMaand());
    }
}
