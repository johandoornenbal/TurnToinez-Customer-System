package domainapp.webapp;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;

public class MyMailProcessor implements Processor{

    @Override
    public void process(Exchange exchange) throws Exception {

        String portNumber = PropertiesCache.getInstance().getProperty("rest.port");
        String secret = PropertiesCache.getInstance().getProperty("rest.authorization");

        Client client = ClientBuilder.newBuilder().newClient();
        WebTarget webTarget = client.target("http://localhost:" + portNumber + "/restful/services/domainapp.app.services.api.Api/actions/consumeEmail/invoke");

        final Invocation.Builder invocationBuilder = webTarget.request();
        invocationBuilder.accept("application/json;profile=urn:org.apache.isis/v1");
        invocationBuilder.header("Authorization", secret);
        final Invocation invocation = invocationBuilder.buildPut(Entity.json(exchange.getIn().getBody()));

        final Response response = invocation.invoke();

        Message message = new DefaultMessage();
        Gson gson = new Gson();
        String email = gson.fromJson(String.valueOf(exchange.getIn().getBody()), MyMailTransformer.Payload.class).getEmailAddress().getValue();
        message.setBody("Processed: " + email + " \nResponse: " + response.getStatus() + " -- " + response.getStatusInfo().getReasonPhrase());
        exchange.setOut(message);

    }



}
