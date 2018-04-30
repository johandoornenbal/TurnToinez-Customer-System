package domainapp.dom.bestelservice;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;

import domainapp.dom.bestellingen.Postuur;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Bestellingen",
        menuOrder = "10"
)
public class Bestelservice {

    public BestellingInvoerViewmodel nieuweBestellingEnKlant(
            @Parameter(optionality = Optionality.OPTIONAL)
            final String naamTurnster,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String leeftijd,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String kledingmaat,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String lengte,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Postuur postuur,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String artikel1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijs1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String opmerking1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String artikel2,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijs2,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String opmerking2,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String artikel3,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BigDecimal prijs3,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String opmerking3

    ){
        BestellingInvoerViewmodel bestellingInvoerViewmodel =
        new BestellingInvoerViewmodel(
                naamTurnster,
                leeftijd,
                kledingmaat,
                lengte,
                postuur
        );
        if (artikel1!=null && prijs1 !=null){
            bestellingInvoerViewmodel.nieuwArtikel(artikel1, prijs1, opmerking1, 1);
        }
        if (artikel2!=null && prijs2 !=null){
            bestellingInvoerViewmodel.nieuwArtikel(artikel2, prijs2, opmerking2, 2);
        }
        if (artikel3!=null && prijs3 !=null){
            bestellingInvoerViewmodel.nieuwArtikel(artikel3, prijs3, opmerking3, 3);
        }
        return bestellingInvoerViewmodel;
    }

}
