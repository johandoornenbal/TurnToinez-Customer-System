package domainapp.dom.restclient;

import com.google.gson.Gson;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RestClientServiceTest {

    @Test
    public void callRestApi() throws Exception {

        // given
        RestClientService service = new RestClientService(){
            @Override
            String callRestApi(final String href, final String method, final String body) {
                return "{\"test\" : \"json\"}";
            }
        };

        Gson gson = new Gson();
        String jsonResult = service.callRestApi(null,null,null);
        Assertions.assertThat(jsonResult).isEqualTo("{\"test\" : \"json\"}");
        TestDto resultDto = gson.fromJson(jsonResult, TestDto.class);
        Assertions.assertThat(resultDto).isNotNull();

        // when
        TestDto result = (TestDto) service.callRestApi(TestDto.class, null, null, null);

        // then
        Assertions.assertThat(result.getTest()).isEqualTo("json");

    }

    @Test
    public void callRestApi_with_body_as_dto_object() throws Exception {

        // given
        RestClientService service = new RestClientService(){
            @Override
            String callRestApi(final String href, final String method, final String body) {
                return "{\"test\" : \"json\"}";
            }
        };

        class SomeClass{
        }

        // when
        TestDto result = (TestDto) service.callRestApi(TestDto.class, null, null, new Object());
        // then
        Assertions.assertThat(result.getTest()).isEqualTo("json");

    }

}