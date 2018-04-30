CREATE TABLE tti."Notitie"
(
  id serial NOT NULL,
  datum date NOT NULL,
  foto1_naam character varying(255),
  foto1_mimetype character varying(255),
  foto1_bytes bytea,
  foto2_naam character varying(255),
  foto2_mimetype character varying(255),
  foto2_bytes bytea,
  notitie character varying(255),
  version bigint NOT NULL,
  CONSTRAINT "Notitie_PK" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE tti."Notitie"
  OWNER TO isis;

CREATE TABLE tti."NotitieLink"
(
  id serial NOT NULL,
  "notitie_id_OID" bigint NOT NULL,
  version bigint NOT NULL,
  "DISCRIMINATOR" character varying(255) NOT NULL,
  "bestelling_id_OID" bigint,
  "klant_id_OID" bigint,
  CONSTRAINT "NotitieLink_PK" PRIMARY KEY (id),
  CONSTRAINT "NotitieLink_FK1" FOREIGN KEY ("notitie_id_OID")
      REFERENCES tti."Notitie" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "NotitieLink_FK2" FOREIGN KEY ("bestelling_id_OID")
      REFERENCES tti."Bestelling" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "NotitieLink_FK3" FOREIGN KEY ("klant_id_OID")
      REFERENCES tti."Klant" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED
)
WITH (
  OIDS=FALSE
);

ALTER TABLE tti."NotitieLink"
  OWNER TO isis;

CREATE INDEX "NotitieLink_N49"
  ON tti."NotitieLink"
  USING btree
  ("bestelling_id_OID");

CREATE INDEX "NotitieLink_N50"
  ON tti."NotitieLink"
  USING btree
  ("klant_id_OID");

CREATE INDEX "NotitieLink_N51"
  ON tti."NotitieLink"
  USING btree
  ("notitie_id_OID");
