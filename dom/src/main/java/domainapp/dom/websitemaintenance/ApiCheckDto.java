package domainapp.dom.websitemaintenance;

import org.apache.isis.applib.annotation.ViewModel;

import lombok.Getter;
import lombok.Setter;

@ViewModel
@Getter @Setter
public class ApiCheckDto {

    public String title(){
        return "Check";
    }

    private String api;
}
