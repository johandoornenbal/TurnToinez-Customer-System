package domainapp.dom.medewerkers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.jdo.annotations.Column;
import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@ViewModel
@Builder
public class SaldoTotaalRegel {

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String datum;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String totalen;

    @Getter
    @MemberOrder(sequence = "3")
    @Column(scale = 2)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal bedrag;

    public void setBedrag(final BigDecimal bedrag){
        this.bedrag = bedrag.setScale(2, RoundingMode.HALF_UP);
    }

}
