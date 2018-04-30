package domainapp.integtests.tests.modules.tti;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.communicatie.Communicatie;
import domainapp.dom.communicatie.CommunicatieRepository;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.fixture.dom.tti.KlantenFixtures;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class CommunicatieRepositoryTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    KlantRepository klantRepository;

    @Inject
    CommunicatieRepository communicatieRepository;

    @Before
    public void setUp() throws Exception {
        // given
        RecreateTtiTestFixtures fs = new RecreateTtiTestFixtures();
        fixtureScripts.runFixtureScript(fs, null);
    }


    @Test
    public void create() throws Exception {
        //given
        Klant klant = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);
        LocalDate datum = new LocalDate(2016, 01,01);
        LocalTime tijd = new LocalTime(21, 03);

        //when
        Communicatie newCommunicatie = communicatieRepository.create(klant, "titel", datum, tijd, "some text", klant.getEmail());

        //then
        assertThat(communicatieRepository.alleCommunicaties().size()).isEqualTo(1);
        assertThat(newCommunicatie.getKlant()).isEqualTo(klant);
        assertThat(newCommunicatie.getTitel()).isEqualTo("titel");
        assertThat(newCommunicatie.getTekst()).isEqualTo("some text");
        assertThat(newCommunicatie.getDatum()).isEqualTo(datum);
        assertThat(newCommunicatie.getLocalTime()).isEqualTo(tijd);
        assertThat(newCommunicatie.getTijd()).isEqualTo("21:03");
        assertThat(newCommunicatie.getAfzender()).isEqualTo("johan@filternet.nl");

    }

    public void findUnique() throws Exception {

        //given
        Klant klant = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);
        LocalDate datum = new LocalDate(2016, 01,01);
        LocalTime tijd = new LocalTime(21, 03);

        //when
        Communicatie newCommunicatie = communicatieRepository.create(klant, "titel", datum, tijd, "some text", klant.getEmail());

        //then
        assertThat(communicatieRepository.findUnique(klant, datum, tijd)).isEqualTo(newCommunicatie);

    }

    @Test
    public void laatst_ontvangen_sorts_by_date_descending() throws Exception {

        //given
        Klant klant = klantRepository.findUnique(KlantenFixtures.EMAIL_JOHAN);
        LocalDate datum1 = LocalDate.now().minusDays(1);
        LocalDate datumVoorDatum1 = LocalDate.now().minusDays(2);
        LocalTime tijd1 = new LocalTime(21, 03);
        LocalTime tijdVoorTijd1 = new LocalTime(21, 02);

        //when
        Communicatie com1 = communicatieRepository.create(klant, "titel", datum1, tijd1, "some text", klant.getEmail());
        Communicatie com2 = communicatieRepository.create(klant, "titel", datumVoorDatum1, tijd1, "some text", klant.getEmail());
        Communicatie com3 = communicatieRepository.create(klant, "titel", datum1, tijdVoorTijd1, "some text", klant.getEmail());

        //then
        List<Communicatie> result = communicatieRepository.laatstOntvangen();
        assertThat(result.get(0)).isEqualTo(com1);
        assertThat(result.get(1)).isEqualTo(com3);
        assertThat(result.get(2)).isEqualTo(com2);

    }

}