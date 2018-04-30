CREATE TABLE financieleadministratie."KostenRegel"
(
  id serial NOT NULL,
  aantekening character varying(255),
  bedrag numeric NOT NULL,
  datum date NOT NULL,
  medewerker character varying(255) NOT NULL,
  onderwerp character varying(255) NOT NULL,
  version bigint NOT NULL,
  CONSTRAINT "KostenRegel_PK" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE financieleadministratie."KostenRegel"
  OWNER TO isis;

CREATE TABLE financieleadministratie."VerdienRegel"
(
  id serial NOT NULL,
  aantekening character varying(255),
  "bestelling_id_OID" bigint,
  datum date NOT NULL,
  kosten numeric NOT NULL,
  medewerker character varying(255) NOT NULL,
  onderwerp character varying(255) NOT NULL,
  percentage integer NOT NULL,
  prijs numeric NOT NULL,
  verdienste numeric NOT NULL,
  version bigint NOT NULL,
  CONSTRAINT "VerdienRegel_PK" PRIMARY KEY (id),
  CONSTRAINT "VerdienRegel_FK1" FOREIGN KEY ("bestelling_id_OID")
      REFERENCES tti."Bestelling" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);
ALTER TABLE financieleadministratie."VerdienRegel"
  OWNER TO isis;

CREATE INDEX "VerdienRegel_N49"
  ON financieleadministratie."VerdienRegel"
  USING btree
  ("bestelling_id_OID");
