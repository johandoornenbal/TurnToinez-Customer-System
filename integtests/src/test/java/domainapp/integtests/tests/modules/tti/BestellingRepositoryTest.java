package domainapp.integtests.tests.modules.tti;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.bestellingen.Status;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.fixture.dom.tti.KlantenFixtures;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class BestellingRepositoryTest extends DomainAppIntegTest {


    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    BestellingRepository bestellingRepository;

    @Inject
    KlantRepository klantRepository;

    @Before
    public void setUp() throws Exception {
        // given
        RecreateTtiTestFixtures fs = new RecreateTtiTestFixtures();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();
    }

    @Test
    public void findByKlant() throws Exception {
        // given
        Klant johan = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);
        // when
        List<Bestelling> results = bestellingRepository.findByKlant(johan);
        // then
        assertThat(results.size()).isEqualTo(3);
    }

    @Test
    public void zoekOpStatus() throws Exception {
        // given
        // when
        List<Bestelling> result = bestellingRepository.zoekOpStatus(Status.BESTELLING);
        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void zoekOpNaamTurnster() throws Exception {
        // given
        // when
        List<Bestelling> result = bestellingRepository.findByNaamTurnster("neChie");
        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getNaamTurnster()).isEqualTo("Annechien");
    }

    @Test
    public void nieuweBestelling() throws Exception {
        // given
        getFixtureClock().setDate(2000,01,01);
        getFixtureClock().setTime(0, 0);
        assertThat(bestellingRepository.listAll().size()).isEqualTo(6);

        // when
        Bestelling blancoBestelling = bestellingRepository.nieuweBestelling();

        // then
        assertThat(blancoBestelling.getStatus()).isEqualTo(Status.KLAD);
        assertThat(blancoBestelling.getTimestamp()).isEqualTo("2000-01-01T00:00:00.000");
        assertThat(bestellingRepository.listAll().size()).isEqualTo(7);

        // and given
        Klant johan = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);

        // when
        Bestelling bestellingMetKlant = bestellingRepository
                .nieuweBestelling(
                        johan,
                        "Turnster",
                        "10 jaar",
                        "maat 140",
                        "135 cm",
                        Postuur.GEMIDDELD
                );
        // then
        assertThat(bestellingRepository.listAll().size()).isEqualTo(8);
        assertThat(bestellingMetKlant.getKlant()).isEqualTo(johan);
        assertThat(bestellingMetKlant.getNaamTurnster()).isEqualTo("Turnster");
        assertThat(bestellingMetKlant.getLeeftijd()).isEqualTo("10 jaar");
        assertThat(bestellingMetKlant.getKledingmaat()).isEqualTo("maat 140");
        assertThat(bestellingMetKlant.getLengte()).isEqualTo("135 cm");
        assertThat(bestellingMetKlant.getPostuur()).isEqualTo(Postuur.GEMIDDELD);
    }

    @Test
    public void wijzig_klant_works(){

        // given
        Bestelling bestellingForMietje = bestellingRepository.findByKlant(klantRepository.findUnique(KlantenFixtures.EMAIL_MIETJE)).get(1);
        assertThat(bestellingForMietje.getFactuur()).isNotNull();

        // when
        bestellingForMietje.wijzigKlant(klantRepository.findUnique(KlantenFixtures.EMAIL_JAN));

        // then
        assertThat(bestellingForMietje.getKlant()).isEqualTo(klantRepository.findUnique(KlantenFixtures.EMAIL_JAN));
        assertThat(bestellingForMietje.getFactuur().getKlant()).isEqualTo(klantRepository.findUnique(KlantenFixtures.EMAIL_JAN));

    }

    @Test
    public void zoek_works(){
        // given, when, then
        assertThat(bestellingRepository.zoek("kLeIn").size()).isEqualTo(2);
    }

}