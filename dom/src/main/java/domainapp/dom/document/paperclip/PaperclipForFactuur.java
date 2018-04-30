package domainapp.dom.document.paperclip;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.mixins.T_preview;

import domainapp.dom.facturen.Factuur;


@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema="tti")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject
public class PaperclipForFactuur extends Paperclip {

    private Factuur factuur;

    @Column(
            allowsNull = "false",
            name = "factuurId"
    )
    @Property(
            editing = Editing.DISABLED
    )
    public Factuur getFactuur() {
        return factuur;
    }

    public void setFactuur(final Factuur factuur) {
        this.factuur = factuur;
    }

    //region > attachedTo (hook, derived)
    @NotPersistent
    @Override
    public Object getAttachedTo() {
        return getFactuur();
    }

    @Override
    protected void setAttachedTo(final Object object) {
        setFactuur((Factuur) object);
    }
    //endregion

    //region > SubtypeProvider SPI implementation
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends PaperclipRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(Factuur.class, PaperclipForFactuur.class);
        }
    }
    //endregion

    //region > mixins
    @Mixin
    public static class _preview extends T_preview<Factuur> {
        public _preview(final Factuur factuur) {
            super(factuur);
        }
    }

//    @Mixin
//    public static class _documenten extends T_documents<Factuur> {
//        public _documenten(final Factuur factuur) {
//            super(factuur);
//        }
//    }

//    @Mixin
//    public static class _createAndAttachDocumentAndRender extends T_createAndAttachDocumentAndRender<Factuur> {
//        public _createAndAttachDocumentAndRender(final Factuur factuur) {
//            super(factuur);
//        }
//    }
    //endregion

}
