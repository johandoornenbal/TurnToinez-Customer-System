package domainapp.fixture.dom.tti;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import domainapp.dom.klanten.Klant;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.bestellingen.Status;

public class KlantenFixtures extends KlantAbstract{

    public static String EMAIL_JOHAN = "johan@filternet.nl";
    public static String EMAIL_MIETJE = "mietje@gmail.com";
    public static String EMAIL_TIEKE = "t.turnstra@tester.com";
    public static String EMAIL_JAN = "j.turnstra@hotmail.com";

    @Override protected void execute(final ExecutionContext executionContext) {
        Klant klant1 = createKlant(
                "Johan Doornenbal",
                "Bongastate 11",
                "8926 PJ",
                "Leeuwarden",
                EMAIL_JOHAN,
                "Jildou",
                "13",
                "164",
                "120 cm",
                Postuur.GEMIDDELD,
                "Pakje Krul",
                new BigDecimal("65.50"),
                null,
                Status.KLAD,
                new LocalDate("2016-10-02"),
                new LocalDate("2016-10-04"),
                null,
                executionContext);
        Klant klant2 = createKlant(
                "Mietje van der Meulen",
                "NieuweStraat 101",
                "1234 AB",
                "Amsterdam",
                EMAIL_MIETJE,
                "Miep",
                "10 bijna 11",
                "140 denk ik",
                "1.15 m",
                Postuur.STEVIG,
                "Pakje Snow",
                new BigDecimal("20.50"),
                "Niet te klein maken",
                Status.KLAD,
                new LocalDate("2016-10-02"),
                null,
                null,
                executionContext);
        Klant klant3 = createKlant(
                "Tieke Turnstra",
                "Kerkstraat 10b",
                "4567 CD",
                "Ergens",
                EMAIL_TIEKE,
                null, null, null, null,null, null, null,null,
                null, null,null, null,
                executionContext);
        Klant klant4 = createKlant("Jan Turnstra",
                "Schoolstraat 12",
                "5432 EF",
                "Ergensanders",
                EMAIL_JAN,
                null, null, null, null, null, null, null,null,
                null, null,null, null,
                executionContext);
        Klant klant5 = createKlant("Piet Zondermail",
                null,null,"Wommels",null,
                null,null, null, null,null,null,
                null,null,null,null,null,null,
                executionContext);

    }
}
