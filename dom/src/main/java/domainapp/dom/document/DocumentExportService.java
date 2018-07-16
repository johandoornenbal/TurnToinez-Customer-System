package domainapp.dom.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.Document_movedToExternalUrl;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import domainapp.dom.facturen.Factuur;
import domainapp.dom.facturen.FactuurRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentExportService {


    public void exportDocumentBlobs(final LocalDate before, final boolean onlyExportNoDelete){
        String home = System.getProperty("user.home");
        List<Factuur> fact =  factuurRepository.findByDatumBefore(before);
        fact.forEach(x->{

            final List<Paperclip> byAttachedTo = paperclipRepository.findByAttachedTo(x);
            if (!byAttachedTo.isEmpty()) {
                final DocumentAbstract document = byAttachedTo.get(0).getDocument();
                if (document.getSort() != DocumentSort.EXTERNAL_BLOB) {
                    boolean error = false;
                    Blob pdfTest = document.getBlob();
                    File outputFile = new File(home + File.separator + "facturen" + File.separator + document.getName());
                    FileOutputStream fout = null;
                    try {
                        fout = new FileOutputStream(outputFile);
                    } catch (FileNotFoundException e) {
                        error = true;
                        e.printStackTrace();
                    }
                    try {
                        pdfTest.writeBytesTo(fout);
                    } catch (IOException e) {
                        error = true;
                        e.printStackTrace();
                    }
                    if (!error && !onlyExportNoDelete){
                        Document_movedToExternalUrl mixin = new Document_movedToExternalUrl((Document) document);
                        mixin.$$(home + File.separator + "facturen" + File.separator, null);
                    }
                }
            }

        });


    }



    @Inject
    FactuurRepository factuurRepository;

    @Inject
    PaperclipRepository paperclipRepository;

}
