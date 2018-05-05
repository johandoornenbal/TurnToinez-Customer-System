package domainapp.dom.medewerkers;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@ViewModel
@Builder
@Getter @Setter
public class SaldoTotaalRegel {

    @MemberOrder(sequence = "1")
    private String datum;

    @MemberOrder(sequence = "2")
    private String totalen;

    @MemberOrder(sequence = "3")
    @Column(scale = 2)
    private BigDecimal bedrag;

}
