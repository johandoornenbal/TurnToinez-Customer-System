package domainapp.dom.migration;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        named = "Instellingen"
)
public class MigrationManagerMenu {

    public MigrationManager migrationDataImport(){
        return new MigrationManager();
    }

}
