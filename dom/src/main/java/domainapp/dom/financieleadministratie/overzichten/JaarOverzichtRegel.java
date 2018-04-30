package domainapp.dom.financieleadministratie.overzichten;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "jaarOverzichtRegel")
@XmlType(
        name = "",
        propOrder = {
                "maand",
                "btwIn",
                "inclIn",
                "exclIn",
                "btwUit",
                "inclUit",
                "exclUit",
                "jaar",
                "maandNr",
                "boekingRepository"
        }
)
@DomainObject(editing = Editing.DISABLED)
public class JaarOverzichtRegel implements Comparable<JaarOverzichtRegel>{

    public JaarOverzichtRegel(){}

    public JaarOverzichtRegel(
            final String maandString,
            final BigDecimal btwIn,
            final BigDecimal inclIn,
            final BigDecimal exclIn,
            final BigDecimal btwUit,
            final BigDecimal inclUit,
            final BigDecimal exclUit,
            final String jaar,
            final int maandNr){
        this.maand = maandString;
        this.btwIn = btwIn;
        this.inclIn = inclIn;
        this.exclIn = exclIn;
        this.btwUit = btwUit;
        this.inclUit = inclUit;
        this.exclUit = exclUit;
        this.jaar = jaar;
        this.maandNr = maandNr;
    }

    public String title(){
        return getMaand().concat(" ").concat(String.valueOf(getJaar()));
    }

    @Getter @Setter
    private String maand;

    @Getter @Setter
    private BigDecimal btwIn;

    @Getter @Setter
    private BigDecimal exclIn;

    @Getter @Setter
    private BigDecimal inclIn;

    @Getter @Setter
    private BigDecimal btwUit;

    @Getter @Setter
    private BigDecimal inclUit;

    @Getter @Setter
    private BigDecimal exclUit;

    @Getter @Setter
    private String jaar;

    @Getter @Setter
    @Property(hidden = Where.EVERYWHERE)
    private int maandNr;

    public BigDecimal getResultaatInclusief(){
        return getInclIn().subtract(getInclUit());
    }

    public BigDecimal getResultaatExclusief(){
        return getExclIn().subtract(getExclUit());
    }

    public BigDecimal getBtwSaldo(){
        return getBtwIn().subtract(getBtwUit());
    }

    public List<Boeking> getBoekingenUit(){
        LocalDate startDate = new LocalDate(Integer.valueOf(getJaar()), getMaandNr(), 01);
        LocalDate endDate = startDate.dayOfMonth().withMaximumValue();
        return boekingRepository.findByDatumBetween(
                startDate,
                endDate
        ).stream().filter(x->x.getType()== BoekingType.UIT).sorted().collect(Collectors.toList());
    }

    public List<Boeking> getBoekingenIn(){
        LocalDate startDate = new LocalDate(Integer.valueOf(getJaar()), getMaandNr(), 01);
        LocalDate endDate = startDate.dayOfMonth().withMaximumValue();
        return boekingRepository.findByDatumBetween(
                startDate,
                endDate
        ).stream().filter(x->x.getType()== BoekingType.IN).sorted().collect(Collectors.toList());
    }

    @Programmatic
    public JaarOverzichtRegel add(
            final BigDecimal addBtwIn,
            final BigDecimal addInclIn,
            final BigDecimal addExclIn,
            final BigDecimal addBtwUit,
            final BigDecimal addInclUit,
            final BigDecimal addExclUit
    ){
        setBtwIn(getBtwIn().add(addBtwIn));
        setInclIn(getInclIn().add(addInclIn));
        setExclIn(getExclIn().add(addExclIn));
        setBtwUit(getBtwUit().add(addBtwUit));
        setInclUit(getInclUit().add(addInclUit));
        setExclUit(getExclUit().add(addExclUit));
        return this;
    }

    @Override
    public int compareTo(final JaarOverzichtRegel o) {
        return getMaand().compareTo(o.getMaand());
    }

    @Inject
    BoekingRepository boekingRepository;
}
