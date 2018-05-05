package domainapp.integtests.tests.modules.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.Status;
import domainapp.dom.bestelservice.BestellingInvoerViewmodelVanSite;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.dom.medewerkers.Medewerker;
import domainapp.dom.siteservice.FormulierVanSite;
import domainapp.dom.siteservice.FormulierVanSiteRepository;
import domainapp.fixture.dom.tti.KlantenFixtures;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class BestellingScenarioTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    KlantRepository klantRepository;

    @Inject
    FormulierVanSiteRepository formulierVanSiteRepository;

    @Before
    public void setup() {
        // given
        RecreateTtiTestFixtures fs = new RecreateTtiTestFixtures();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();
    }

    @Test
    public void fullScenarioTest(){
        createBestelling();
        statusToBesteld();
        statusToBetaald();
        statusToKlaar();
    }

    Bestelling bestelling;
    Klant jan;

    private void createBestelling() {

        // given
        jan = klantRepository.findUnique(KlantenFixtures.EMAIL_JAN);

        // when
        bestelling = wrap(jan).nieuweBestelling("dochter", null, null, null, null);
        wrap(bestelling).nieuweRegel("pakje", new BigDecimal("30.00"), null, 1);
        wrap(bestelling).nieuweRegel("broekje", new BigDecimal("10.50"), null, 3);
        wrap(bestelling).nieuweRegel("wokkel", new BigDecimal("5.75"), null, 2);
        transactionService.nextTransaction();
        // then
        assertThat(bestelling.getStatus()).isEqualTo(Status.KLAD);
        assertThat(bestelling.getRegels().size()).isEqualTo(3);
        assertThat(bestelling.getRegels().first().getArtikel()).isEqualTo("pakje");
        assertThat(bestelling.getRegels().last().getArtikel()).isEqualTo("broekje");
    }

    private void statusToBesteld() {
        // given
        getFixtureClock().setDate(2000,01,01);
        // when
        wrap(bestelling).besteld();
        // then
        assertThat(bestelling.getStatus()).isEqualTo(Status.BESTELLING);
        assertThat(bestelling.getDatumBestelling()).isEqualTo(new LocalDate(2000,01,01));
        assertThat(bestelling.getFactuur().getKlant()).isEqualTo(jan);
        assertThat(bestelling.getFactuur().getBedragInclBtw()).isEqualTo(new BigDecimal("46.25"));
        assertThat(bestelling.getFactuur().getFactuurRegels().size()).isEqualTo(3);
        assertThat(bestelling.getFactuur().getDatum()).isEqualTo(bestelling.getDatumBestelling());

    }

    private void statusToBetaald() {
        // given
        getFixtureClock().setDate(2000,01,02);
        // when
        wrap(bestelling).betaald(false);
        // then
        assertThat(bestelling.getStatus()).isEqualTo(Status.BETAALD);
        assertThat(bestelling.getDatumBestelling()).isEqualTo(new LocalDate(2000,01,01));
        assertThat(bestelling.getDatumBetaald()).isEqualTo(new LocalDate(2000,01,02));
    }

    private void statusToKlaar() {

        // given
        getFixtureClock().setDate(2000,01,04);
        // when
        wrap(bestelling).klaar(Medewerker.INEZ, null, null, null, null, null);
        // then
        assertThat(bestelling.getStatus()).isEqualTo(Status.KLAAR);
        assertThat(bestelling.getDatumBestelling()).isEqualTo(new LocalDate(2000,01,01));
        assertThat(bestelling.getDatumBetaald()).isEqualTo(new LocalDate(2000,01,02));
        assertThat(bestelling.getDatumKlaar()).isEqualTo(new LocalDate(2000,01,04));
        assertThat(bestelling.getGemaaktDoor()).isEqualTo(Medewerker.INEZ);

    }

    @Test
    public void formulierVanSiteToBestellingAndKlantTest(){

        // given
        FormulierVanSite formulierVanSite =
                formulierVanSiteRepository.create(
                        123,
                        "turnster",
                        "leeftijd 11",
                        "maat 123",
                        "lengte 111",
                        "gemiddeld",
                        "Naam pakje",
                        "lang",
                        "ja",
                        "kleur broekje",
                        "ja",
                        "opmerkingen",
                        "Klant Naam",
                        "Straat naam",
                        "1234AB",
                        "Plaatsnaam",
                        "email@adres.com",
                        "telnummer 12345678",
                        "vragen",
                        "2017-01-01",
                        "123",
                        "prijsinformatie"
                );

        // when
        BestellingInvoerViewmodelVanSite bestellingInvoerViewmodelVanSite = wrap(formulierVanSite).maakBestelling();
        // then
        assertThat(formulierVanSite.getVerwerkt()).isEqualTo(false);

        // and when
        wrap(bestellingInvoerViewmodelVanSite).maakBestellingEnKlant();
        // then
        assertThat(formulierVanSite.getVerwerkt()).isEqualTo(true);
        Klant nwKlant = klantRepository.findByEmail("email@adres.com").get(0);
        assertThat(nwKlant.getNaam()).isEqualTo("Klant Naam");
        assertThat(nwKlant.getBestellingen().size()).isEqualTo(1);
        assertThat(nwKlant.getBestellingen().first().getNaamTurnster()).isEqualTo("turnster");

    }

}
