package domainapp.dom.utils;

public class HtmlUtils {

    public static String formatString(final String string){
        String formattedText = new String("<html><body>");
        String htmlString = toHtml(string);
        formattedText = formattedText.concat(htmlString);
        formattedText = formattedText.concat("</body></html>");
        return formattedText;
    }

    public static String toHtml(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for( char c : s.toCharArray() ) {
            if( c == ' ' ) {
                if( previousWasASpace ) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch(c) {
            case '<': builder.append("&lt;"); break;
            case '>': builder.append("&gt;"); break;
            case '&': builder.append("&amp;"); break;
            case '"': builder.append("&quot;"); break;
            case '\n': builder.append("<br>"); break;
            // We need Tab support here, because we print StackTraces as HTML
            case '\t': builder.append("&nbsp; &nbsp; &nbsp;"); break;
            default:
                if( c < 128 ) {
                    builder.append(c);
                } else {
                    builder.append("&#").append((int)c).append(";");
                }
            }
        }
        return builder.toString();
    }

    public static String fromHtml(String s) {
        s = s.replace("&nbsp;", " ");
        s = s.replace("&lt;", "<");
        s = s.replace("&gt;", ">");
        s = s.replace("&amp;", "&");
        s = s.replace("&quot;", "\"");
        s = s.replace("<br>", "\n");
        return s;
    }

}
