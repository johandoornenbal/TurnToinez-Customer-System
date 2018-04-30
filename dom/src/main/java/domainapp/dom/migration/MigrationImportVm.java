package domainapp.dom.migration;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.ViewModel;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Postuur;
import domainapp.dom.bestellingen.Status;
import domainapp.dom.facturen.Factuur;
import domainapp.dom.facturen.FactuurRepository;
import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.BoekingRepository;
import domainapp.dom.financieleadministratie.BoekingType;
import domainapp.dom.klanten.Klant;
import domainapp.dom.klanten.KlantRepository;
import lombok.Getter;
import lombok.Setter;

@ViewModel
public class MigrationImportVm {

    // Klant
    @Getter @Setter
    private Integer klant_klantnummer;
    @Getter @Setter
    private String aanhef;
    @Getter @Setter
    private String voornaam;
    @Getter @Setter
    private String tussen;
    @Getter @Setter
    private String achternaam;
    @Getter @Setter
    private String straat;
    @Getter @Setter
    private String postcode;
    @Getter @Setter
    private String plaats;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String telefoon;
    @Getter @Setter
    private String mobiel;
    @Getter @Setter
    private String aantekeningen;

    // Bestelling
    @Getter @Setter
    private Integer ordernummer;
    @Getter @Setter
    private String status;
    @Getter @Setter
    private Integer order_klantnummer;
    @Getter @Setter
    private String turnster_naam;
    @Getter @Setter
    private String leeftijd;
    @Getter @Setter
    private String kledingmaat;
    @Getter @Setter
    private String postuur;
    @Getter @Setter
    private String art_1;
    @Getter @Setter
    private String opm_1;
    @Getter @Setter
    private BigDecimal prijs_1;
    @Getter @Setter
    private String art_2;
    @Getter @Setter
    private String opm_2;
    @Getter @Setter
    private BigDecimal prijs_2;
    @Getter @Setter
    private String art_3;
    @Getter @Setter
    private String opm_3;
    @Getter @Setter
    private BigDecimal prijs_3;
    @Getter @Setter
    private String art_4;
    @Getter @Setter
    private String opm_4;
    @Getter @Setter
    private BigDecimal prijs_4;
    @Getter @Setter
    private String art_5;
    @Getter @Setter
    private String opm_5;
    @Getter @Setter
    private BigDecimal prijs_5;
    @Getter @Setter
    private BigDecimal prijs_tot;
    @Getter @Setter
    private BigDecimal btw;
    @Getter @Setter
    private LocalDate datum_offerte;
    @Getter @Setter
    private LocalDate datum_order;
    @Getter @Setter
    private LocalDate datum_betaald;
    @Getter @Setter
    private LocalDate datum_klaar;

    // Factuur
    @Getter @Setter
    private Integer factuurnummer;
    @Getter @Setter
    private Integer factuur_klantnummer;
    @Getter @Setter
    private Integer factuur_ordernummer;
    @Getter @Setter
    private String f_art_1;
    @Getter @Setter
    private BigDecimal f_prijs_1;
    @Getter @Setter
    private String f_art_2;
    @Getter @Setter
    private BigDecimal f_prijs_2;
    @Getter @Setter
    private String f_art_3;
    @Getter @Setter
    private BigDecimal f_prijs_3;
    @Getter @Setter
    private String f_art_4;
    @Getter @Setter
    private BigDecimal f_prijs_4;
    @Getter @Setter
    private String f_art_5;
    @Getter @Setter
    private BigDecimal f_prijs_5;
    @Getter @Setter
    private BigDecimal f_prijs_tot;
    @Getter @Setter
    private BigDecimal f_btw;
    @Getter @Setter
    private LocalDate f_datum_factuur;
    @Getter @Setter
    private LocalDate f_datum_betaald;
    @Getter @Setter
    private String vervallen;

    // Boeking
    @Getter @Setter
    private Integer boeking_nummer;
    @Getter @Setter
    private Integer boeking_klantnummer;
    @Getter @Setter
    private LocalDate boeking_datum;
    @Getter @Setter
    private String in_uit;
    @Getter @Setter
    private String b_omschrijving;
    @Getter @Setter
    private BigDecimal b_prijs_ex;
    @Getter @Setter
    private BigDecimal b_btw;
    @Getter @Setter
    private BigDecimal b_prijs_incl;
    @Getter @Setter
    private String post;
    @Getter @Setter
    private Integer b_factuur;

    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    @ActionLayout()
    public void migrate(){

        String klantnaam = createKlantNaam(aanhef, voornaam, tussen, achternaam);
        Klant klant = klantRepository.nieuweKlantByApi(klantnaam, straat, postcode, plaats, email, telefoon, mobiel, aantekeningen, getKlant_klantnummer());
        if (getStatus()!=null && bestellingRepository.findByUniqueHistoricId(ordernummer)==null){
            Postuur nwPostuur = null;
            if (getPostuur()!=null) {
                if (getPostuur().toLowerCase().contains("s")) {
                    nwPostuur = Postuur.STEVIG;
                } else {
                    if (getPostuur().toLowerCase().contains("t") || getPostuur().toLowerCase().contains("kl")) {
                        nwPostuur = Postuur.TENGER;
                    }
                }
                if (getPostuur().toLowerCase().contains("n") || getPostuur().toLowerCase().contains("gem")) {
                    nwPostuur = Postuur.GEMIDDELD;
                }
            }
            Bestelling bestelling = klant.nieuweBestelling(
                    turnster_naam,
                    leeftijd,
                    kledingmaat,
                    null,
                    nwPostuur
            );
            Factuur factuur = null;
            bestelling.setHistoricId(ordernummer);
            if (getStatus()!=null && getStatus().contains("klaar")){
                bestelling.setStatus(Status.KLAAR);
                bestelling.setDatumBestelling(getDatum_order());
                bestelling.setDatumBetaald(getDatum_betaald());
                bestelling.setDatumKlaar(getDatum_klaar());
                if (vervallen==null && factuurRepository.findByUniqueHistoricId(factuurnummer)==null) {
                    factuur = newFactuur(bestelling);
                    factuur.setDatumBetaald(f_datum_betaald);
                }
            }
            if (getStatus()!=null && getStatus().contains("betaald")){
                bestelling.setStatus(Status.BETAALD);
                bestelling.setDatumBestelling(getDatum_order());
                bestelling.setDatumBetaald(getDatum_betaald());
                if (vervallen==null && factuurRepository.findByUniqueHistoricId(factuurnummer)==null) {
                    factuur = newFactuur(bestelling);
                    factuur.setDatumBetaald(f_datum_betaald);
                }
            }
            if (getStatus()!=null && vervallen==null && getStatus().contains("order")){
                bestelling.setStatus(Status.BESTELLING);
                bestelling.setDatumBestelling(getDatum_order());
                if (factuurRepository.findByUniqueHistoricId(factuurnummer)==null) {
                    factuur = newFactuur(bestelling);
                }
            }
            if (vervallen!=null && vervallen.contains("ja")){
                factuur = newFactuur(bestelling);
                factuur.setVervallen(true);
            }
            if (getArt_1()!=null && !getArt_1().equals("")) {
                bestelling.nieuweRegel(art_1, prijs_1, opm_1, 1);
            }
            if (getArt_2()!=null && !getArt_2().equals("")) {
                bestelling.nieuweRegel(art_2, prijs_2, opm_2, 2);
            }
            if (getArt_3()!=null && !getArt_3().equals("")) {
                bestelling.nieuweRegel(art_3, prijs_3, opm_3, 3);
            }
            if (getArt_4()!=null && !getArt_4().equals("")) {
                bestelling.nieuweRegel(art_4, prijs_4, opm_4, 4);
            }
            if (getArt_5()!=null && !getArt_5().equals("")) {
                bestelling.nieuweRegel(art_5, prijs_5, opm_5, 5);
            }
            if (boeking_nummer!=null){
                Boeking nwBoeking = boekingRepository.findByUniqueHistoricId(getBoeking_nummer());
                if (nwBoeking==null){
                    nwBoeking = boekingRepository.create(
                            boeking_datum,
                            BoekingType.IN,
                            "Bestelling",
                            b_prijs_incl,
                            b_prijs_ex,
                            b_btw,
                            BoekingPost.VERKOOP,
                            null,
                            factuur,
                            false
                    );
                    nwBoeking.setHistoricId(boeking_nummer);
                }
            }
        }
    }

    private Factuur newFactuur(Bestelling bestelling){
        Factuur factuur = bestelling.maakFactuur();
        factuur.setHistoricId(getFactuurnummer());
        factuur.setDatum(f_datum_factuur);
        factuur.setNummer(getFactuurnummer().toString());
        factuur.setDraft(false);
        factuur.setBtw(f_btw);
        factuur.setBedragInclBtw(f_prijs_tot);
        factuur.setBedragExclBtw(getF_prijs_tot().subtract(getF_btw()));
        factuur.setVerzonden(true);
        return factuur;
    }

    private String createKlantNaam(final String aanhef, final String voornaam, final String tussen, final String achternaam) {
        String result = new String("");
        if (aanhef!=null && !aanhef.equals("")){
            result=result.concat(aanhef).concat(" ");
        }
        if (voornaam!=null && !voornaam.equals("")){
            result=result.concat(voornaam).concat(" ");
        }
        if (tussen!=null && !tussen.equals("")){
            result=result.concat(tussen).concat(" ");
        }
        if (achternaam!=null && !achternaam.equals("")){
            result=result.concat(achternaam);
        }
        return result;
    }

    @Inject KlantRepository klantRepository;

    @Inject BestellingRepository bestellingRepository;

    @Inject FactuurRepository factuurRepository;

    @Inject BoekingRepository boekingRepository;

}
