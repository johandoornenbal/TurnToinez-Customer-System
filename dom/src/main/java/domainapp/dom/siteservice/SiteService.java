package domainapp.dom.siteservice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.glassfish.jersey.client.ClientProperties;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.commons.config.IsisConfiguration;

import domainapp.dom.instellingen.Instellingen;
import domainapp.dom.instellingen.InstellingenRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Van Website",
        menuOrder = "100"
)
public class SiteService {

    protected String getApiBaseUrl() {
        return this.configuration.getString("isis.service.ttiapi.base-url");
    }

    protected String getApiSecret() {
        return this.configuration.getString("isis.service.ttiapi.secret");
    }

    public List<FormulierVanSite> haalBestellingenOp() {

        Gson gson = new Gson();
        Instellingen instellingen = instellingenRepository.instellingen();
        Integer laatsteNummer = instellingen.getLaatsteBestellingVanSite();
        List<FormulierVanSite> formulieren = formulierVanSiteRepository.nietVerwerkteBestellingenVanSite();

        try {
            javax.ws.rs.client.Client client = ClientBuilder.newClient();
            client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
            client.property(ClientProperties.READ_TIMEOUT,    1000);
            String resourceString = getApiBaseUrl().concat("?laatste_nummer=")
                    .concat(laatsteNummer.toString())
                    .concat("&")
                    .concat(getApiSecret());
            WebTarget webResource2 = client.target(resourceString);
            Invocation.Builder invocationBuilder =
                    webResource2.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();
            if (response.getStatus() != 200) {
                throw new RuntimeException("" + response.getStatus());
            }
            List<SiteResponse> results = gson.fromJson(response.readEntity(String.class), new TypeToken<ArrayList<SiteResponse>>() {}.getType());
            if (results==null){return formulieren;}
            for (SiteResponse res : results){
                PrijsInfo prijsInfo = null;
                try {
                    if (res.getPakjeId()!=null) {
                        Gson gsonPrijs = new Gson();
                        String resourceStringPrijs = getApiBaseUrl()
                                .concat("?pakjeId=")
                                .concat(res.getPakjeId());
                        webResource2 = client.target(resourceStringPrijs);
                        invocationBuilder =
                                webResource2.request(MediaType.APPLICATION_JSON);
                        response = invocationBuilder.get();
                        if (response.getStatus() != 200) {
                            throw new RuntimeException("" + response.getStatus());
                        }
                        prijsInfo = gsonPrijs.fromJson(response.readEntity(String.class), PrijsInfo.class);
                    }
                } catch (Exception e) {
                    if (e.getMessage()!=null) {
                        messageService.warnUser(e.getMessage());
                    }
                }
                formulieren.add(formulierVanSiteRepository.create(
                        res.getNummer(),
                        res.getTurnster(),
                        res.getLeeftijd(),
                        res.getKledingmaat(),
                        res.getLengte(),
                        res.getPostuur(),
                        res.getPakje(),
                        res.getMouwtje(),
                        res.getBroekje(),
                        res.getKleurbroekje(),
                        res.getElastiek(),
                        res.getOpmerkingen(),
                        res.getNaam(),
                        res.getStraat(),
                        res.getPostcode(),
                        res.getPlaats(),
                        res.getEmail(),
                        res.getTelefoon(),
                        res.getVragen(),
                        res.getDatum(),
                        res.getPakjeId(),
                        prijsInfo !=null ? prijsInfo.toString() : ""
                ));
                instellingen.setLaatsteBestellingVanSite(res.getNummer());
            }
            return formulieren;

        } catch (Exception e) {
            if (e.getMessage()!=null) {
                messageService.warnUser(e.getMessage());
            } else {
                messageService.warnUser("Het ophalen van bestellingen gaat niet goed. Waarschijnlijk een verkeerde bestelling aanwezig. Vraag hulp van je lieve man ... ;-)");
            }
            return formulieren;
        }

    }

    @Inject
    private MessageService messageService;

    @Inject
    private InstellingenRepository instellingenRepository;

    @Inject
    private FormulierVanSiteRepository formulierVanSiteRepository;

    @Inject
    IsisConfiguration configuration;

}
