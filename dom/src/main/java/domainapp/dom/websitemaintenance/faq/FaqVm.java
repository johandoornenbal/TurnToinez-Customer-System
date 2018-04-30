package domainapp.dom.websitemaintenance.faq;

import org.apache.isis.applib.annotation.ViewModel;

import domainapp.dom.websitemaintenance.WebsiteLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ViewModel
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FaqVm {

    private Integer id;

    private Integer sortorder;

    private WebsiteLanguage language;

    private String question;

    private String answer;

}
