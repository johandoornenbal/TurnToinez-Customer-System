/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package domainapp.dom.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        auditing = Auditing.DISABLED
)
@DomainObjectLayout(
        named = "Migration manager",
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class MigrationManager {

    public String title() {
        return "Migration manager";
    }

    public MigrationManager() {
        this.name = "Migration";
    }

    public MigrationManager(final MigrationManager manager){
        this.name = manager.getName();
    }

    @Getter @Setter
    private String name;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public List<MigrationImportVm> importDataFromSheet(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        WorksheetSpec spec = new WorksheetSpec(MigrationImportVm.class, "migration");
        List<List<?>> objects =  excelService.fromExcel(spreadsheet, spec);
        List<MigrationImportVm> results = new ArrayList<>();
        for (Object o :objects){
            MigrationImportVm vm = (MigrationImportVm) o;
            vm.migrate();
        }
        return results;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public List<MigrationBookingEntryImportVm> importBookingEntriesFromSheet(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        WorksheetSpec spec = new WorksheetSpec(MigrationBookingEntryImportVm.class, "administratie");
        List<List<?>> objects =  excelService.fromExcel(spreadsheet, spec);
        List<MigrationBookingEntryImportVm> results = new ArrayList<>();
        for (Object o :objects){
            results.add((MigrationBookingEntryImportVm) o);
        }
        return results;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public List<MigrationTimeEntryImportVm> importTimeEntriesFromSheet(
            @ParameterLayout(named = "Excel spreadsheet") final Blob spreadsheet) {
        WorksheetSpec spec = new WorksheetSpec(MigrationTimeEntryImportVm.class, "uren");
        List<List<?>> objects =  excelService.fromExcel(spreadsheet, spec);
        List<MigrationTimeEntryImportVm> results = new ArrayList<>();
        for (Object o :objects){
            MigrationTimeEntryImportVm vm = (MigrationTimeEntryImportVm) o;
            vm.migrate();
        }
        return results;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadTemplate() {
        final String fileName = "Migration template.xlsx";
        WorksheetSpec spec = new WorksheetSpec(MigrationImportVm.class, "migration");
        WorksheetContent worksheetContent = new WorksheetContent(Arrays.asList(new MigrationImportVm()), spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    @Inject
    private ExcelService excelService;

}
