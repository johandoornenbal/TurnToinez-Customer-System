package domainapp.dom.websitemaintenance.faq;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.commons.config.IsisConfiguration;

import domainapp.dom.restclient.RestClientService;
import domainapp.dom.websitemaintenance.ApiResult;

@DomainService(nature = NatureOfService.DOMAIN)
public class FaqService {

    private String secret(){
        return configuration.getString("isis.service.ttiapi.secret");
    }

    @Programmatic
    public List<Faq> syncWithRemote(){

        // get all from remote
        List<FaqVm> remoteList = getAllFromRemote();

        // get all local
        List<Faq> localList = faqRepository.listAll();

        // add or update on remote that are found local
        for (Faq faq : localList){
            boolean foundRemote = false;
            for (FaqVm vm : remoteList){
                if (faq.getRemoteId().equals(vm.getId())){
                    foundRemote = true;
                    break;
                }
            }
            if (foundRemote) {
                updateOnRemote(faq);
            } else {
                addOnRemote(faq);
            }
        }

        // delete faq on remote not found local unless local is empty
        if (!localList.isEmpty()){
            for (FaqVm vm : remoteList) {
                boolean found = false;
                for (Faq faq : localList) {
                    if (vm.getId().equals(faq.getRemoteId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    deleteOnRemote(vm);
                }
            }
        }

        // again get all from remote
        remoteList = getAllFromRemote();

        // find or create from result
        for (FaqVm result : remoteList){
            faqRepository.findOrCreate(result.getId(), result.getSortorder(), result.getLanguage(), result.getQuestion(), result.getAnswer());
        }

        return faqRepository.listAll();
    }

    private List<FaqVm> getAllFromRemote(){
        return (List<FaqVm>) restClientService.callRestApi(FaqVm.class, "http://www.turntoinez.nl/api/backendApi.php/faq?" + secret(), HttpMethod.GET, "");
    }

    @Programmatic
    public void deleteOnRemote(final Faq faq){
        ApiResult result = (ApiResult) restClientService.callRestApi(ApiResult.class, "http://www.turntoinez.nl/api/backendApi.php/faq/delete/" + faq.getRemoteId() + "?" + secret(), HttpMethod.POST, "");
        messageService.informUser("affected: " + result.getAffected() + " inserted: " + result.getInserted());
    }

    private void deleteOnRemote(final FaqVm faqVm){
        ApiResult result = (ApiResult) restClientService.callRestApi(ApiResult.class, "http://www.turntoinez.nl/api/backendApi.php/faq/delete/" + faqVm.getId() + "?" + secret(), HttpMethod.POST, "");
        messageService.informUser("affected: " + result.getAffected() + " inserted: " + result.getInserted());
    }

    @Programmatic
    public void updateOnRemote(final Faq faq){
        FaqVm payload = new FaqVm(faq.getRemoteId(), faq.getSortOrder(), faq.getLanguage(), faq.getQuestion(), faq.getAnswer());
        try {
            restClientService.callRestApi(ApiResult.class, "http://www.turntoinez.nl/api/backendApi.php/faq/" + faq.getRemoteId() + "?" + secret(), HttpMethod.POST, payload);
            messageService.informUser("updated faq with id: " + faq.getRemoteId());
        } catch (Exception e) {
            messageService.warnUser(e.getMessage());
        }
    }

    @Programmatic
    public Integer addOnRemote(final Faq faq){
        FaqVm payload = new FaqVm(null, faq.getSortOrder(), faq.getLanguage(), faq.getQuestion(), faq.getAnswer());
        ApiResult result = null;
        try {
            result = (ApiResult) restClientService.callRestApi(ApiResult.class, "http://www.turntoinez.nl/api/backendApi.php/faq/" + "?" + secret(), HttpMethod.POST, payload);
            messageService.informUser("added faq with id: " + result.getInserted());
        } catch (Exception e) {
            messageService.warnUser(e.getMessage());
        }
        return Integer.valueOf(result.getInserted());
    }

    @Inject
    private FaqRepository faqRepository;

    @Inject
    private RestClientService restClientService;

    @Inject
    private IsisConfiguration configuration;

    @Inject
    private MessageService messageService;



}
