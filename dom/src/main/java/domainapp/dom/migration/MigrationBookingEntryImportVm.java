package domainapp.dom.migration;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.ViewModel;

import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;
import domainapp.dom.financieleadministratie.KostenPost;
import domainapp.dom.financieleadministratie.KostenPostRepository;
import lombok.Getter;
import lombok.Setter;

@ViewModel
public class MigrationBookingEntryImportVm {

    // Boeking
    @Getter @Setter
    private Integer nummer;
    @Getter @Setter
    private LocalDate datum;
    @Getter @Setter
    private String omschrijving;
    @Getter @Setter
    private BigDecimal bedrag_ex;
    @Getter @Setter
    private BigDecimal btw;
    @Getter @Setter
    private BigDecimal bedrag_incl;
    @Getter @Setter
    private String post;

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    @ActionLayout()
    public void migrate() {
        BoekingPost boekingPost;
        KostenPost kostenPost;
        Boolean laterBijwerken = false;
        if (post==null || post.equals("")){
            boekingPost = BoekingPost.ONKOSTEN_ALGEMEEN;
            kostenPost = kostenPostRepository.findByNaamContains("diversen onkosten").get(0);
            laterBijwerken = true;
        } else {
            if (post.toLowerCase().contains("stof")){
                boekingPost = BoekingPost.INKOOP;
                kostenPost = kostenPostRepository.findByNaamContains("stof").get(0);
            } else {
                if (post.toLowerCase().contains("adverten")){
                    boekingPost = BoekingPost.ONKOSTEN_VERKOOP;
                    kostenPost = kostenPostRepository.findByNaamContains("advertentie").get(0);
                } else {
                    if (post.toLowerCase().contains("four")){
                        boekingPost = BoekingPost.INKOOP;
                        kostenPost = kostenPostRepository.findByNaamContains("fournituren").get(0);
                    } else {
                        if (post.toLowerCase().contains("port") || post.toLowerCase().contains("zend") || omschrijving.toLowerCase().contains("port") || omschrijving.toLowerCase().contains("frank")){
                            boekingPost = BoekingPost.ONKOSTEN_ALGEMEEN;
                            kostenPost = kostenPostRepository.findByNaamContains("porto").get(0);
                        } else {
                            if (post.toLowerCase().contains("kantoor") || post.toLowerCase().contains("verpak")){
                                boekingPost = BoekingPost.ONKOSTEN_ALGEMEEN;
                                kostenPost = kostenPostRepository.findByNaamContains("kantoor").get(0);
                            } else {
                                if (post.toLowerCase().contains("machine")){
                                    boekingPost = BoekingPost.INVESTERING;
                                    kostenPost = kostenPostRepository.findByNaamContains("machines").get(0);
                                } else {
                                    boekingPost = BoekingPost.ONKOSTEN_ALGEMEEN;
                                    kostenPost = kostenPostRepository.findByNaamContains("diversen onkosten").get(0);
                                    laterBijwerken = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        Boeking nwBoeking = boekingRepository.create(datum, BoekingType.UIT, omschrijving, bedrag_incl, bedrag_ex, btw, boekingPost, kostenPost,null, laterBijwerken);
        if (post!=null && !post.equals("")){
            nwBoeking.setGemigreerdePost(post);
        }
    }
    @Inject BoekingRepository boekingRepository;

    @Inject KostenPostRepository kostenPostRepository;

}
