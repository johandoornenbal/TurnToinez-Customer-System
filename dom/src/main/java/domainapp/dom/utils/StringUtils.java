package domainapp.dom.utils;


public class StringUtils {

    public static String firstNameOf(final String name){
        return name.contains(" ") ? name.substring(0, name.indexOf(" ")) : "";
    }

    public static String lastNameOf(final String name){
        return name.contains(" ") ? name.substring(name.indexOf(" ")+1) : name;
    }

}
