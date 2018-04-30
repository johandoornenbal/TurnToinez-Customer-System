CREATE SCHEMA mailchimp
  AUTHORIZATION isis;

CREATE TABLE mailchimp."MailChimpList"
(
  id serial NOT NULL,
  "defaultMailSubject" character varying(255) NOT NULL,
  "isDeletedRemote" boolean,
  "listId" character varying(255) NOT NULL,
  "markedForDeletion" boolean,
  name character varying(255) NOT NULL,
  "newLocal" boolean,
  version bigint NOT NULL,
  CONSTRAINT "MailChimpList_PK" PRIMARY KEY (id),
  CONSTRAINT "MailChimpList_listId_UNQ" UNIQUE ("listId")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mailchimp."MailChimpList"
  OWNER TO isis;