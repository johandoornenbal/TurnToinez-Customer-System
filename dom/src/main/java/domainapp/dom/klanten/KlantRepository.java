package domainapp.dom.klanten;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        repositoryFor = KlantRepository.class
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Klanten",
        menuOrder = "20"
)
public class KlantRepository {

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "10")
    public List<Klant> zoekKlant(final String zoek){
        return repositoryService.allMatches(
            new QueryDefault<>(
                Klant.class,
                "findByKlantObjectContains",
                "searchString",
                zoek
            )
        );
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "100")
    public List<Klant> vindMogelijkDubbel() {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Klant.class,
                        "findByMogelijkDubbel",
                        "mogelijkDubbel", true));
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public List<Klant> alleKlanten() {
        return repositoryService.allInstances(Klant.class);
    }

    @Programmatic
    public List<Klant> findByNaam(final String naam) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Klant.class,
                        "findByNaamContains",
                        "naam", naam));
    }

    @Programmatic
    public List<Klant> findByEmail(final String email) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Klant.class,
                        "findByEmailContains",
                        "email", email));
    }

    @Programmatic
    public List<Klant> findByPlaats(final String plaats) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Klant.class,
                        "findByPlaatsContains",
                        "plaats", plaats));
    }

    @Programmatic
    public Klant findUnique(final String uniqueId) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Klant.class,
                        "findUnique",
                        "uniqueId", uniqueId));
    }

    @Programmatic
    public Klant findByUniqueHistoricId(final Integer historicId) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Klant.class,
                        "findByUniqueHistoricId",
                        "historicId", historicId));
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "20")
    public Klant nieuweKlant(
            final String naam,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String straatnaam,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String postcode,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String plaats,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String email,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String telefoon1,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String telefoon2,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 5)
            final String aantekeningen
    ) {
        String unique = UUID.randomUUID().toString();
        if (email!=null && !email.equals("")){
            unique = email.toLowerCase();
        }
        Klant klant = findUnique(unique);
        if (klant==null) {
            klant = create(
                    naam,
                    straatnaam,
                    postcode,
                    plaats,
                    email,
                    telefoon1,
                    telefoon2,
                    aantekeningen,
                    null
            );
        } else {
            klant.setNaam(naam);
            klant.setStraat(straatnaam);
            klant.setPostcode(postcode);
            klant.setPlaats(plaats);
            klant.setTelefoon1(telefoon1);
            klant.setTelefoon2(telefoon2);
            if (aantekeningen!=null && !aantekeningen.equals("") && klant.getAantekeningen()!=null) {
                klant.setAantekeningen(aantekeningen.concat(" | ").concat(klant.getAantekeningen()));
            } else {
                klant.setAantekeningen(aantekeningen);
            }
        }
        klant.zetPinOpGoogleMaps();
        return klant;
    }

    private Klant create(
            final String naam,
            final String straatnaam,
            final String postcode,
            final String plaats,
            final String email,
            final String telefoon1,
            final String telefoon2,
            final String aantekeningen,
            final Boolean mogelijkDubbel){
        String unique = UUID.randomUUID().toString();
        if (email!=null && !email.equals("")){
            unique = email.toLowerCase();
        }
        Klant klant = new Klant(
                naam,
                straatnaam,
                postcode,
                plaats,
                email,
                telefoon1,
                telefoon2,
                aantekeningen
        );
        klant.setUniqueId(unique);
        klant.setMogelijkDubbel(mogelijkDubbel);
        serviceRegistry.injectServicesInto(klant);
        repositoryService.persist(klant);
        return klant;
    }

    // for import API
    @Programmatic
    public Klant nieuweKlantByApi(
            final String naam,
            final String straatnaam,
            final String postcode,
            final String plaats,
            final String email,
            final String telefoon1,
            final String telefoon2,
            final String aantekeningen,
            final Integer historicId
    ) {
        Klant nwKlant = findByUniqueHistoricId(historicId);
        String emailstr = new String("");
        Boolean mogelijkDubbel = null;
        if (nwKlant==null){
            if (email!=null){
                if (findUnique(email.toLowerCase())!=null){
                    emailstr = email + "DUBBEL?" + UUID.randomUUID().toString();
                    mogelijkDubbel = true;
                }
            }
            nwKlant = create(naam, straatnaam, postcode, plaats, emailstr.equals("") ? email : emailstr, telefoon1, telefoon2, aantekeningen, mogelijkDubbel);
            nwKlant.setHistoricId(historicId);
        }
        return nwKlant;
    }

    @Action(hidden = Where.EVERYWHERE)
    public List<Klant> autoComplete(final String searchString){
        return zoekKlant(searchString);
    }

    @Programmatic
    public void remove(final Klant klant){
        MapsView view = mapsViewRepository.findByKlant(klant);
        if (view!=null){
            mapsViewRepository.remove(view);
        }
        repositoryService.remove(klant);
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<Klant> zoekKlantenZonderBestellingen(){
        return alleKlanten().stream().filter(x->x.getBestellingen().size()==0).collect(Collectors.toList());
    }


    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

    @Inject
    MapsViewRepository mapsViewRepository;

}
