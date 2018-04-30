package domainapp.dom.migration;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.ViewModel;

import domainapp.dom.uren.Uren;
import domainapp.dom.uren.UrenRepository;
import lombok.Getter;
import lombok.Setter;

@ViewModel
public class MigrationTimeEntryImportVm {

    @Getter @Setter
    private Integer nummer;
    @Getter @Setter
    private LocalDate datum;
    @Getter @Setter
    private Integer uren;

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    @ActionLayout()
    public void migrate() {
        Uren uren = urenRepository.schrijfUren(getDatum(), getUren());
        uren.setHistoricId(getNummer());
    }

    @Inject UrenRepository urenRepository;

}
