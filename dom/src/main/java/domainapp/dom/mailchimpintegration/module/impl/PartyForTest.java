package domainapp.dom.mailchimpintegration.module.impl;

import domainapp.dom.mailchimpintegration.module.api.IMailChimpParty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class PartyForTest implements IMailChimpParty {

    private String firstName;

    private String lastName;

    private String emailAddress;

    private Boolean excludeFromLists;

    @Override
    public Boolean excludeFromLists() {
        return getExcludeFromLists();
    }
}
