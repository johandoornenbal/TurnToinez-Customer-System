package domainapp.dom.bestelservice;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;

import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "artikel",
        "prijs",
        "opmerking",
        "volgorde"
})
@XmlRootElement(name = "regelViewmodel")
public class RegelViewmodel {

    public RegelViewmodel(){}

    public RegelViewmodel(
            final String artikelInv,
            final BigDecimal prijsInv,
            final String opmerkingInv,
            final Integer volgordeInv
    ){
        this.artikel = artikelInv;
        this.prijs = prijsInv;
        this.opmerking = opmerkingInv;
        this.volgorde = volgordeInv;
    }

    @Getter @Setter
    @XmlElement(required = true)
    @Property(editing = Editing.DISABLED)
    private String artikel;

    @Getter @Setter
    @Column(scale = 2)
    @XmlElement(required = true)
    @Property(editing = Editing.DISABLED)
    private BigDecimal prijs;

    @Getter @Setter
    @XmlElement(required = false)
    @Property(editing = Editing.DISABLED)
    private String opmerking;

    @Getter @Setter
    @XmlElement(required = false)
    @Property(editing = Editing.DISABLED)
    private Integer volgorde;

}
