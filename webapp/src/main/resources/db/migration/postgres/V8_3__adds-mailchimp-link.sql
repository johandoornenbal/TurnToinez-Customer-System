CREATE TABLE mailchimp."MailChimpListMemberLink"
(
  id serial NOT NULL,
  "isDeletedRemote" boolean,
  "list_id_OID" bigint NOT NULL,
  "markedForDeletion" boolean,
  "member_id_OID" bigint NOT NULL,
  "newLocal" boolean,
  status character varying(255) NOT NULL,
  version bigint NOT NULL,
  CONSTRAINT "MailChimpListMemberLink_PK" PRIMARY KEY (id),
  CONSTRAINT "MailChimpListMemberLink_FK1" FOREIGN KEY ("list_id_OID")
      REFERENCES mailchimp."MailChimpList" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "MailChimpListMemberLink_FK2" FOREIGN KEY ("member_id_OID")
      REFERENCES mailchimp."MailChimpMember" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
  CONSTRAINT "MailChimpListMemberLink_list_member_UNQ" UNIQUE ("list_id_OID", "member_id_OID")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mailchimp."MailChimpListMemberLink"
  OWNER TO isis;

-- Index: mailchimp."MailChimpListMemberLink_N49"

-- DROP INDEX mailchimp."MailChimpListMemberLink_N49";

CREATE INDEX "MailChimpListMemberLink_N49"
  ON mailchimp."MailChimpListMemberLink"
  USING btree
  ("list_id_OID");

-- Index: mailchimp."MailChimpListMemberLink_N50"

-- DROP INDEX mailchimp."MailChimpListMemberLink_N50";

CREATE INDEX "MailChimpListMemberLink_N50"
  ON mailchimp."MailChimpListMemberLink"
  USING btree
  ("member_id_OID");