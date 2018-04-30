package domainapp.fixture.dom.tti;

import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.KostenPost;

public class KostenPostFixtures extends KostenPostAbstract{

    @Override protected void execute(final ExecutionContext executionContext) {
        KostenPost p1_1 = createKostenPost(
                "stof",
                BoekingPost.INKOOP,
                executionContext);

        KostenPost p1_2 = createKostenPost(
                "fournituren",
                BoekingPost.INKOOP,
                executionContext);

        KostenPost p1_3 = createKostenPost(
                "diversen inkoop",
                BoekingPost.INKOOP,
                executionContext);

        KostenPost p2_1 = createKostenPost(
                "porto",
                BoekingPost.ONKOSTEN_ALGEMEEN,
                executionContext);

        KostenPost p2_2 = createKostenPost(
                "kantoor artikelen",
                BoekingPost.ONKOSTEN_ALGEMEEN,
                executionContext);

        KostenPost p2_3 = createKostenPost(
                "advertentie",
                BoekingPost.ONKOSTEN_VERKOOP,
                executionContext);

        KostenPost p2_4 = createKostenPost(
                "website",
                BoekingPost.ONKOSTEN_VERKOOP,
                executionContext);

        KostenPost p2_5 = createKostenPost(
                "reiskosten",
                BoekingPost.ONKOSTEN_VERKOOP,
                executionContext);

        KostenPost p2_6 = createKostenPost(
                "diversen onkosten",
                BoekingPost.ONKOSTEN_ALGEMEEN,
                executionContext);

        KostenPost p3_1 = createKostenPost(
                "machines",
                BoekingPost.INVESTERING,
                executionContext);


    }
}
