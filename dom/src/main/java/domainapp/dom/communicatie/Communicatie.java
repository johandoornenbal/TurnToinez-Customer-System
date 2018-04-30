package domainapp.dom.communicatie;

import java.util.Comparator;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.utils.TitleBuilder;

import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.klanten.Klant;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "tti"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.VERSION_NUMBER,
        column="version")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findUnique", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.communicatie.Communicatie "
                    + "WHERE klant == :klant && datum == :datum && localTime == :localTime "),
    @javax.jdo.annotations.Query(
            name = "findByDateInterval", language = "JDOQL",
            value = "SELECT "
                    + "FROM domainapp.dom.communicatie.Communicatie "
                    + "WHERE datum >= :begin && datum <= :end ")

})
@javax.jdo.annotations.Unique(name="Communicatie_UNQ", members = {"klant", "datum", "localTime"})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Communicatie implements Comparable<Communicatie>{

    public Communicatie(){}

    public Communicatie(
            final Klant klant,
            final String titel,
            final LocalDate datum,
            final LocalTime tijd,
            final String tekst,
            final String afzender
    )  {
        this.klant = klant;
        this.titel = titel;
        this.datum = datum;
        this.localTime = tijd;
        this.tekst = tekst;
        this.afzender = afzender;
    }

    public static final int MAX_LENGTH = 8192;

    public String title(){
        return TitleBuilder.start()
                .withName(getDatum().toString("dd-MM-yyyy"))
                .withName(getTitel())
                .toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private Klant klant;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String titel;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate datum;

    @Getter @Setter
    @Column(allowsNull = "false")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private LocalTime localTime;

    @Getter @Setter
    @Column(allowsNull = "true", length = MAX_LENGTH)
    @PropertyLayout(multiLine = 30, hidden = Where.ALL_TABLES)
    private String tekst;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String afzender;

    @Action(semantics = SemanticsOf.SAFE)
    public String getSamenvatting(){
        return getTekst().length() < 50 ? getTekst() : getTekst().substring(0,50).concat(" ....");
    }

    @Action(semantics = SemanticsOf.SAFE)
    public String getTijd(){
        return getLocalTime().toString("HH:mm");
    }

    @Action()
    public EmailCommunicatieViewmodel beantwoordt(
            @ParameterLayout(multiLine = 10)
            final String tekst) {
        String titel = "RE: ".concat(klant.getCommunicaties().last().getTitel());
        return new EmailCommunicatieViewmodel(titel, tekst, klant, false);
    }

    public String default0Beantwoordt(final String tekst){
        return  "\n\n"
            .concat(instellingenRepository.instellingen().getEmailHandtekening())
            .concat("\n\n-------------------------------\n\nOp ")
            .concat(getDatum().toString("dd-MM-yyyy"))
            .concat(" ")
            .concat(getTijd())
            .concat(" schreef ")
            .concat(getAfzender()!=null ? getAfzender() : "")
            .concat("\n\n")
            .concat(getTekst());
    }

    @Override
    public int compareTo(final Communicatie other) {
        return org.apache.isis.applib.util.ObjectContracts
                .compare(this, other, "klant", "datum", "localTime");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "klant", "datum", "localTime");
    }
    //endregion

    public static class CommunicatieComparator implements Comparator<Communicatie> {
        @Override
        public int compare(final Communicatie o1, final Communicatie o2) {
            return o2.compareTo(o1);
        }
    };

    @Inject InstellingenRepository instellingenRepository;
}
