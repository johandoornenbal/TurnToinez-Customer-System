package domainapp.dom.emailService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.inject.Singleton;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceClassPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.runtime.services.email.EmailServiceDefault;

import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import domainapp.dom.facturen.Factuur;

@Singleton
@DomainService(nature = NatureOfService.DOMAIN)
public class TtiEmailService extends EmailServiceDefault{

    @Programmatic
    public boolean sendFactuur(final Factuur factuur, final String onderwerp, final String formattedTekst, final String unFormattedTekst, final String bcc){
        DataSource attachment = paperclipRepository.findByAttachedTo(factuur).get(0).getDocument().asDataSource();
        List<String> bccList = new ArrayList<>();
        if (bcc != null) {
            bccList.add(bcc);
        }
        boolean result = send(
                Arrays.asList(factuur.getKlant().getEmail()),
                null,
                bccList,
                onderwerp,
                formattedTekst,
                unFormattedTekst,
                attachment
        );
        if (result){
            factuur.setVerzonden(true);
        }
        return result;
    }

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceDefault.class);
    private String senderEmailAddress;
    private String senderEmailUsername;
    private String senderEmailPassword;
    private Integer senderEmailPort;
    private boolean initialized;

    @PostConstruct
    @Programmatic
    public void init() {
        if(!this.initialized) {
            this.senderEmailAddress = this.getSenderEmailAddress();
            this.senderEmailUsername = this.getSenderEmailUsername();
            this.senderEmailPassword = this.getSenderEmailPassword();
            this.senderEmailPort = this.getSenderEmailPort();
            this.initialized = true;
            if(!this.isConfigured()) {
                LOG.warn("NOT configured");
            } else {
                LOG.debug("configured");
            }
        }
    }

    protected String getSenderEmailUsername() {
        return this.configuration.getString("isis.service.email.sender.username");
    }


    @Programmatic
    public boolean send(List<String> toList, List<String> ccList, List<String> bccList, String subject, String body, String tekst, DataSource... attachments) {
        try {
            ImageHtmlEmail ex = new ImageHtmlEmail();
            ex.setAuthenticator(new DefaultAuthenticator(this.senderEmailUsername, this.senderEmailPassword));
            ex.setHostName(this.getSenderEmailHostName());
            ex.setSmtpPort(this.senderEmailPort.intValue());
            ex.setStartTLSEnabled(this.getSenderEmailTlsEnabled().booleanValue());
            ex.setDataSourceResolver(new DataSourceClassPathResolver("/", true));
            Properties properties = ex.getMailSession().getProperties();
            properties.put("mail.smtps.auth", "true");
            properties.put("mail.debug", "true");
            properties.put("mail.smtps.port", "" + this.senderEmailPort);
            properties.put("mail.smtps.socketFactory.port", "" + this.senderEmailPort);
            properties.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtps.socketFactory.fallback", "false");
            properties.put("mail.smtp.starttls.enable", "" + this.getSenderEmailTlsEnabled());
            ex.setFrom(this.senderEmailAddress);
            ex.setSubject(subject);
            ex.setHtmlMsg(body);
            ex.setTextMsg(tekst);
            if(attachments != null && attachments.length > 0) {
                DataSource[] arr$ = attachments;
                int len$ = attachments.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    DataSource attachment = arr$[i$];
                    ex.attach(attachment, attachment.getName(), "");
                }
            }

            if(this.notEmpty(toList)) {
                ex.addTo((String[])toList.toArray(new String[toList.size()]));
            }

            if(this.notEmpty(ccList)) {
                ex.addCc((String[])ccList.toArray(new String[ccList.size()]));
            }

            if(this.notEmpty(bccList)) {
                ex.addBcc((String[])bccList.toArray(new String[bccList.size()]));
            }

            ex.send();
            return true;
        } catch (EmailException var13) {
            LOG.error("An error occurred while trying to send an email about user email verification", var13);
            return false;
        }
    }

    private boolean notEmpty(List<String> toList) {
        return toList != null && !toList.isEmpty();
    }

    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    IsisConfiguration configuration;
}

