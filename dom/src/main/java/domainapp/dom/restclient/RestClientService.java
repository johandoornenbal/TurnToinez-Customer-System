package domainapp.dom.restclient;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.glassfish.jersey.client.ClientProperties;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.commons.config.IsisConfiguration;

/**
 * Generic REST client
 */

@DomainService(nature = NatureOfService.DOMAIN)
public class RestClientService {

    /**
     *  A generic method to call a REST api - with optional body as dto class instance
     *
     * @param dto
     * @param href
     * @param method
     * @param body      dto object (can be of any class that Gson is able to handle)
     * @return          dto object - that can be casted to class of param dto
     */
    @Programmatic
    public <T> Object callRestApi(final Class<T> dto, final String href, final String method, final Object body) throws Exception {
        Gson gson = new Gson();
        String bodyStr = gson.toJson(body);
        return callRestApi(dto, href, method, bodyStr);
    }


    /**
     *  A generic method to call a REST api - with optional body as String class instance
     *
     * @param dto       class of dto used
     * @param href
     * @param method
     * @param body      json string of the payload
     * @return          dto object - that can be casted to class of param dto
     */
    @Programmatic
    public <T> Object callRestApi(final Class<T> dto, final String href, final String method, final String body){
        Gson gson = new Gson();
        Object result = null;
        try {

            result = gson.fromJson(callRestApi(href, method, body), dto);

        } catch (Exception e){

            // callRestApi may return an array instead of an object;
            List<T> resultList = new ArrayList<>();

            List<JsonElement> jsonObjList = gson.fromJson(callRestApi(href, method, body), new TypeToken<ArrayList<JsonElement>>() {
            }.getType());

            for (JsonElement json : jsonObjList) {
                resultList.add(gson.fromJson(json, dto));
            }

            return resultList;

        }
        return result;
    }

    String callRestApi(final String href, final String method, final String body) {
        try {
            javax.ws.rs.client.Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, getConnectTimeOut());
            client.property(ClientProperties.READ_TIMEOUT,    getReadTimeOut());
            String resourceString = href;
            WebTarget webResource2 = client.target(resourceString);
            Invocation.Builder invocationBuilder =
                    webResource2
                            .request(MediaType.APPLICATION_JSON);
            Response response;
            switch (method){

            case HttpMethod.GET:
                response = invocationBuilder.get();
                if (response.getStatus() != 200) {
                    throw new RuntimeException("" + response.getStatus());
                }
                return response.readEntity(String.class);

            case HttpMethod.POST:
                response = invocationBuilder.post(Entity.json(body));
                if (response.getStatus() != 200) {
                    throw new RuntimeException("" + response.getStatus() + " " + response.readEntity(String.class));
                }
                return response.readEntity(String.class);

            case HttpMethod.DELETE:
                response = invocationBuilder.delete();
                if (response.getStatus() != 204) {
                    throw new RuntimeException("" + response.getStatus());
                } else {
                    return "deleted";
                }

            default:
                return null;
            }
        } catch (Exception e) {
            messageService.warnUser(e.getMessage());
            return null;
        }
    }

    Integer getConnectTimeOut() {
        String timeoutStr = this.configuration.getString("isis.service.restservice.connect-timeout");
        return timeoutStr!=null ? Integer.valueOf(timeoutStr) : 2000;
    }

    Integer getReadTimeOut() {
        String timeoutStr = this.configuration.getString("isis.service.restservice.read-timeout");
        return timeoutStr!=null ? Integer.valueOf(timeoutStr) : 2000;
    }

    @Inject private MessageService messageService;

    @Inject private IsisConfiguration configuration;

}
