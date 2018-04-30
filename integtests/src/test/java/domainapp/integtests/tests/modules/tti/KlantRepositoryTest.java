package domainapp.integtests.tests.modules.tti;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import domainapp.fixture.dom.tti.KlantenFixtures;
import domainapp.fixture.scenarios.RecreateTtiTestFixtures;
import domainapp.integtests.tests.DomainAppIntegTest;
import static org.assertj.core.api.Assertions.assertThat;

public class KlantRepositoryTest extends DomainAppIntegTest {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    KlantRepository klantRepository;

    @Before
    public void setUp() throws Exception {
        // given
        RecreateTtiTestFixtures fs = new RecreateTtiTestFixtures();
        fixtureScripts.runFixtureScript(fs, null);
        transactionService.nextTransaction();

        assertThat(klantRepository.alleKlanten().size()).isEqualTo(5);
    }

    @Test
    public void zoekKlant() throws Exception {
        // given
        String plaatsPart = "leEuw";
        // when
        List<Klant> found = wrap(klantRepository).zoekKlant(plaatsPart);
        // then
        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0).getPlaats()).isEqualTo("Leeuwarden");

        // and given
        String twoTimesFoundInSameKlant = "mie";
        // when
        found = wrap(klantRepository).zoekKlant(twoTimesFoundInSameKlant);
        // then still
        assertThat(found.size()).isEqualTo(1);

    }

    @Test
    public void findByNaam() throws Exception {
        // given
        String namePart = "door";
        // when
        List<Klant> found = klantRepository.findByNaam(namePart);
        // then
        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0).getNaam()).isEqualTo("Johan Doornenbal");
    }

    @Test
    public void findByEmail() throws Exception {
        // given
        String emailPart = "n@fiLterNet.n";
        // when
        List<Klant> found = klantRepository.findByEmail(emailPart);
        // then
        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0).getEmail()).isEqualTo("johan@filternet.nl");
    }

    @Test
    public void findByPlaats() throws Exception {
        // given
        String plaatsPartCaseAltered = "wArDe";
        // when
        List<Klant> found = klantRepository.findByPlaats(plaatsPartCaseAltered);
        // then
        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0).getPlaats()).isEqualTo("Leeuwarden");
    }

    @Test
    public void findUnique() throws Exception {
        // given
        String emailToBeFound = "JOHan@filternet.nl";
        // when
        Klant johan = klantRepository.findUnique(emailToBeFound);
        // then
        assertThat(johan.getEmail()).isEqualTo("johan@filternet.nl");

        // and given
        String emailNotToBeFound = "JOHan@filternet.n";
        // when
        johan = klantRepository.findUnique(emailNotToBeFound);
        // then
        assertThat(johan).isNull();
    }

    @Test
    public void nieuweKlant() throws Exception {

        Klant klant;

        // given
        assertThat(klantRepository.alleKlanten().size()).isEqualTo(5);
        // when
        klant = wrap(klantRepository).nieuweKlant("test", "test", "test", "test", "test", "test", "test", "test");
        // then
        assertThat(klantRepository.alleKlanten().size()).isEqualTo(6);
        assertThat(klant.getNaam()).isEqualTo("test");
        // and when again (idempotent)
        klant = wrap(klantRepository).nieuweKlant("test123", "test", "test", "test", "test", "test", "test", "test");
        // then still
        assertThat(klantRepository.alleKlanten().size()).isEqualTo(6);
        // but klant properties are updated
        assertThat(klant.getNaam()).isEqualTo("test123");

    }

    @Test
    public void validateWijzigKlant() throws Exception {

        //given
        Klant mietje = klantRepository.findUnique(KlantenFixtures.EMAIL_MIETJE);
        //when
        //then
        assertThat(mietje.validateWijzigKlant("Mietje", "joHAn@Filternet.nl")).isEqualTo("Er is al een klant met dit email adres");

    }

}