package domainapp.dom.utils;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class HtmlUtilsTest {

    @Test
    public void fromHtml() throws Exception {

        // given
        String html = "This is<br>a new line &quot; &nbsp;&lt;&gt;&amp;";
        // when
        String result = HtmlUtils.fromHtml(html);
        // then
        Assertions.assertThat(result).isEqualTo("This is\na new line \"  <>&");

    }

}