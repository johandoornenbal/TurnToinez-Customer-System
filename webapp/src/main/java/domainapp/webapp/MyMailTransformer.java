package domainapp.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeMultipart;

import com.google.gson.Gson;

import org.apache.camel.Exchange;
import org.datanucleus.util.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import domainapp.dom.communicatie.Communicatie;
import lombok.Getter;

public class MyMailTransformer {

    public void doTransform(Exchange exchange) throws Exception {

        String from = String.valueOf(exchange.getIn().getHeader("From"));
        String date = String.valueOf(exchange.getIn().getHeader("Date"));
        String subject = String.valueOf(exchange.getIn().getHeader("Subject"));
        String body = multipartHandler(exchange);

        String emailAddress;
        if (from.split("<").length>1) {
            emailAddress = removeLastChar(from.split("<")[1]);
        } else {
            emailAddress = removeLastChar(from.split("<")[0]);
        }

        exchange.getIn().setHeader("emailAddress", emailAddress);

        MailDateFormat mailDateFormat = new MailDateFormat();
        Date utilDate = mailDateFormat.parse(date);
        LocalDate localDate = LocalDate.fromDateFields(utilDate);
        LocalTime localTime = LocalTime.fromDateFields(utilDate);
        Payload payload = new Payload(emailAddress, subject, localDate, localTime, body);
        String json = new Gson().toJson(payload);

        exchange.getIn().setBody(json);

    }

    private String multipartHandler(final Exchange exchange) throws MessagingException, IOException {
        String result;
        if (exchange.getIn().hasAttachments()) {
            result = parseMultipart(exchange.getIn().getBody(MimeMultipart.class)).split("<!")[0];
        } else {
            result = exchange.getIn().getBody(String.class);
        }
        Integer lengthInBytesEstimate = result.length()*2 + 200;
        if (lengthInBytesEstimate > Communicatie.MAX_LENGTH){
            result = "[LET OP: BERICHT IS INGEKORT]\n\n" + result;
            result = StringUtils.leftAlignedPaddedString(result, Communicatie.MAX_LENGTH-10);
            Integer newLength = result.getBytes("UTF-8").length;
            while (newLength > Communicatie.MAX_LENGTH-10){
                newLength = newLength-1;
                result = StringUtils.leftAlignedPaddedString(result, newLength);
            }
        }
        return result;
    }

    private String parseMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder text = new StringBuilder();
        List<String> attachments = new ArrayList<String>(1);
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart part = mimeMultipart.getBodyPart(i);
            if (part.getContent() instanceof MimeMultipart) {
                text.append(parseMultipart((MimeMultipart) part.getContent()));
            }
            if (part.getContentType().contains("text/plain") || part.getContentType().contains("text/html")) {
                text.append(part.getContent());
            }
            if (part.getHeader("Content-Disposition") != null) {
                attachments.add(part.getHeader("Content-Disposition")[0]);
                text.append("[ER ZIJN BIJLAGEN IN DE ORIGINELE MAIL]\n\n");
            }
        }
        return text.toString();
    }

    private String removeLastChar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length()-1)=='>') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    class Payload {

        public Payload(
                final String email,
                final String title,
                final LocalDate date,
                final LocalTime time,
                final String body
        ){
            this.emailAddress = new Mapping(email);
            this.title = new Mapping(title);
            this.date = new Mapping(date.toString("yyyy-MM-dd"));
            this.time = new Mapping(time.toString("HH:mm"));
            this.body = new Mapping(body);
        }

        @Getter
        private Mapping emailAddress;

        private Mapping title;

        private Mapping date;

        private Mapping time;

        private Mapping body;

        class Mapping {

            public Mapping(final String value){
                this.value = value;
            }

            @Getter
            private String value;

        }

    }

}
