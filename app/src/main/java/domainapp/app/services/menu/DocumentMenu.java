/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.app.services.menu;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Financiën",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "900")
public class DocumentMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<Document> vindFactuurPdf(
            final LocalDate startDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate
    ) {
        return documentRepository.findBetween(startDate, endDate);
    }

    public LocalDate default0VindFactuurPdf() {
        // one week ago.
        return clockService.now().plusDays(-7);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, hidden = Where.EVERYWHERE)
    @MemberOrder(sequence = "1")
    public DocumentType newDocumentType(final String ref, final String name){
        return documentTypeRepository.create(ref, name);
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    @MemberOrder(sequence = "2")
    public List<DocumentType> allDocumentTypes() {
        return documentTypeRepository.allTypes();
    }

    @Inject
    private DocumentTypeRepository documentTypeRepository;

    @Inject
    private ClockService clockService;

    @Inject
    private DocumentRepository documentRepository;

}
