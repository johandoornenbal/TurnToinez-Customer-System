package domainapp.dom.bestellingen;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import domainapp.dom.klanten.Klant;

public class BestellingTest {

    Bestelling bestelling = new Bestelling();

    @Test
    public void disableBesteld() throws Exception {
        // given
        // when
        String msg = bestelling.disableBesteld();
        // then
        Assertions.assertThat(msg).isEqualTo("Er is nog geen klant.");

        // and given
        Klant klant = new Klant();
        bestelling.setKlant(klant);
        // when
        msg = bestelling.disableBesteld();
        // then
        Assertions.assertThat(msg).isEqualTo("Werkt alleen als de status van de bestelling KLAD is.");

        // and given
        bestelling.setStatus(Status.KLAD);
        // when
        msg = bestelling.disableBesteld();
        // then
        Assertions.assertThat(msg).isNull();
    }

    @Test
    public void disableBetaald() throws Exception {
        // given
        // when
        String msg = bestelling.disableBetaald();
        // then
        Assertions.assertThat(msg).isEqualTo("Werkt alleen als de status van de bestelling BESTELLING is.");

        // and given
        bestelling.setStatus(Status.BESTELLING);
        // when
        msg = bestelling.disableBetaald();
        // then
        Assertions.assertThat(msg).isNull();
    }

    @Test
    public void disableKlaar() throws Exception {
        // given
        // when
        String msg = bestelling.disableKlaar();
        // then
        Assertions.assertThat(msg).isEqualTo("Werkt alleen als de status van de bestelling BETAALD is.");
        // and given
        bestelling.setStatus(Status.BETAALD);
        // when
        msg = bestelling.disableKlaar();
        // then
        Assertions.assertThat(msg).isNull();
    }

}