package domainapp.dom.mailchimpintegration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import domainapp.dom.bestellingen.Bestelling;
import domainapp.dom.bestellingen.BestellingRepository;
import domainapp.dom.bestellingen.Status;
import domainapp.dom.mailchimpintegration.module.api.IMailChimpParty;
import domainapp.dom.mailchimpintegration.module.api.MailChimpService;
import domainapp.dom.mailchimpintegration.module.impl.MailChimpList;

@DomainService(nature = NatureOfService.DOMAIN)
public class TtiMailChimpService extends MailChimpService {

    public MailChimpList customersWithLastOrderInPeriod(final MailChimpList list, final LocalDate startDate, final LocalDate endDate){

        // find customers to add / maintain in list
        List<Bestelling> bestellingen = bestellingRepository.zoekOpStatus(Status.KLAAR)
                .stream().filter(x->x.getDatumKlaar().isBefore(endDate) && x.getDatumKlaar().isAfter(startDate))
                .collect(Collectors.toList());
        List<IMailChimpParty> customersWithLastOrderInPeriod = new ArrayList<>();
        for (Bestelling bestelling : bestellingen){
            if (!customersWithLastOrderInPeriod.contains(bestelling)){
                customersWithLastOrderInPeriod.add(bestelling.getKlant());
            }
        }

        return setMembersToCorrespondWithPartyList(customersWithLastOrderInPeriod, list);

    }

    @Inject BestellingRepository bestellingRepository;

}
