package domainapp.dom.mailchimpintegration.module.impl;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MailChimpLists {

    @Getter @Setter
    private List<MailChimpList.MailListMemberDto> lists;

}
