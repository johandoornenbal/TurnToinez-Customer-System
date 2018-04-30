package domainapp.dom.utils;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void firstNameOf() throws Exception {

        // given
        String name;
        // when
        name = "Johan Doornenbal";
        // then
        Assertions.assertThat(StringUtils.firstNameOf(name)).isEqualTo("Johan");

        // and when
        name = "Johan";
        // then
        Assertions.assertThat(StringUtils.firstNameOf(name)).isEqualTo("");

        // and when
        name = "Johannes Tjerk Koenraad Doornenbal";
        // then
        Assertions.assertThat(StringUtils.firstNameOf(name)).isEqualTo("Johannes");

    }

    @Test
    public void lastNameOf() throws Exception {

        // given
        String name;
        // when
        name = "Johan Doornenbal";
        // then
        Assertions.assertThat(StringUtils.lastNameOf(name)).isEqualTo("Doornenbal");

        // and when
        name = "Johan";
        // then
        Assertions.assertThat(StringUtils.lastNameOf(name)).isEqualTo("Johan");

        // and when
        name = "Johannes Tjerk Koenraad Doornenbal";
        // then
        Assertions.assertThat(StringUtils.lastNameOf(name)).isEqualTo("Tjerk Koenraad Doornenbal");

    }

}