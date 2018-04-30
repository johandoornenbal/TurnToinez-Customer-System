package domainapp.dom.facturen;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import domainapp.dom.instellingen.Instellingen;
import domainapp.dom.instellingen.InstellingenRepository;
import domainapp.dom.klanten.Klant;
import domainapp.dom.utils.HtmlUtils;
import static org.assertj.core.api.Assertions.assertThat;

public class EmailViewModelTest {

    @Test
    public void formatStringTest() {

        // given
        Klant klant = new Klant();
        klant.setNaam("Some Name");

        InstellingenRepository instellingenRepository = new InstellingenRepository() {
            Instellingen instellingen = new Instellingen() {
                @Override
                public String getStandaardEmailTekst() {
                    return "This is standard\r\nnew line";
                }
            };

            @Override
            public List<Instellingen> listAll(){
                return Arrays.asList(instellingen);
            }
        };

        Factuur factuur = new Factuur();
        factuur.setKlant(klant);
        factuur.instellingenRepository = instellingenRepository;
        String stringToParse = factuur.defaultTextToBeOverridden();
        EmailViewModel vm = new EmailViewModel();
        vm.setFactuur(factuur);
        vm.setTekst(stringToParse);

        // when
        String formattedString = HtmlUtils.formatString(stringToParse);

        // then
        assertThat(formattedString).isEqualTo("<html><body>Beste Some Name,\r<br>\r<br>This is standard\r<br>new line</body></html>");
    }

}