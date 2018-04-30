package domainapp.fixture.dom.tti;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import domainapp.dom.financieleadministratie.Boeking;
import domainapp.dom.financieleadministratie.BoekingPost;
import domainapp.dom.financieleadministratie.BoekingType;
import domainapp.dom.financieleadministratie.KostenPostRepository;

public class BoekingenFixtures extends BoekingAbstract {

    @Override protected void execute(final ExecutionContext executionContext) {

        Boeking b1 = createBoeking(
                new LocalDate(2017, 01,01),
                BoekingType.UIT,
                "Glitterstof",
                new BigDecimal("121.00"),
                new BigDecimal("100.00"),
                new BigDecimal("21.00"),
                BoekingPost.INKOOP,
                kostenPostRepository.findByNaamAndPost("stof", BoekingPost.INKOOP),
                null,
                false,
                "2017-01-01T00:00etc",
                executionContext
        );

        Boeking b2 = createBoeking(
                new LocalDate(2017, 02,02),
                BoekingType.UIT,
                "Postzegels",
                new BigDecimal("65.00"),
                new BigDecimal("65.00"),
                new BigDecimal("0.00"),
                BoekingPost.ONKOSTEN_ALGEMEEN,
                kostenPostRepository.findByNaamAndPost("porto", BoekingPost.ONKOSTEN_ALGEMEEN),
                null,
                true,
                "2017-01-01T00:01etc",
                executionContext
        );

    }

    @Inject KostenPostRepository kostenPostRepository;

}
