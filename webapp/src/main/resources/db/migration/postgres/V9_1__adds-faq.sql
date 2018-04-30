CREATE SCHEMA website
  AUTHORIZATION isis;

CREATE TABLE website."Faq"
(
  id serial NOT NULL,
  answer character varying(255) NOT NULL,
  language character varying(255) NOT NULL,
  question character varying(255) NOT NULL,
  "remoteId" integer NOT NULL,
  "sortOrder" integer NOT NULL,
  version bigint NOT NULL,
  CONSTRAINT "Faq_PK" PRIMARY KEY (id),
  CONSTRAINT "Faq_remoteId_UNQ" UNIQUE ("remoteId")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE website."Faq"
  OWNER TO isis;
