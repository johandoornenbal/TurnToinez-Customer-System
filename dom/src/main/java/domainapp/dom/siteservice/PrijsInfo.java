package domainapp.dom.siteservice;

import org.apache.isis.applib.annotation.ViewModel;

import lombok.Getter;

@ViewModel
public class PrijsInfo {

    @Getter
    private Row headers;

    @Getter
    private Row rij1;

    @Getter
    private Row rij2;

    @Getter
    private Row rij3;

    @Getter
    private Row headersBroekje;

    @Getter
    private Row rijBroekje1;

    @Getter
    private Row rijBroekje2;

    @Getter
    private Row rijBroekje3;

    private class Row {

        @Getter
        private String col1;

        @Getter
        private String col2;

        @Getter
        private String col3;

    }

    private String rowString(Row row){
        return row.getCol1().concat("   ||  ")
                .concat(row.getCol2()).concat(" ||  ")
                .concat(row.getCol3());
    }

    public String toString(){
        return rowString(getHeaders()).concat("\r\n")
                .concat(rowString(getRij1())).concat("\r\n")
                .concat(rowString(getRij2())).concat("\r\n")
                .concat(rowString(getRij3())).concat("\r\n")
                .concat(rowString(getHeadersBroekje())).concat("\r\n")
                .concat(rowString(getRijBroekje1())).concat("\r\n")
                .concat(rowString(getRijBroekje2())).concat("\r\n")
                .concat(rowString(getRijBroekje3()));
    }

}
