CREATE TABLE tti."Communicatie"
(
  id serial NOT NULL,
  datum date NOT NULL,
  "klant_id_OID" bigint NOT NULL,
  "localTime" time without time zone NOT NULL,
  tekst character varying(8192),
  titel character varying(255) NOT NULL,
  version bigint NOT NULL,
  CONSTRAINT "Communicatie_PK" PRIMARY KEY (id),
  CONSTRAINT "Communicatie_FK1" FOREIGN KEY ("klant_id_OID")
      REFERENCES tti."Klant" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "Communicatie_UNQ" UNIQUE ("klant_id_OID", datum, "localTime")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tti."Communicatie"
  OWNER TO isis;

-- Index: tti."Communicatie_N49"

-- DROP INDEX tti."Communicatie_N49";

CREATE INDEX "Communicatie_N49"
  ON tti."Communicatie"
  USING btree
  ("klant_id_OID");

