CREATE TABLE mailchimp."MailChimpMember"
(
  id serial NOT NULL,
  "emailAddress" character varying(255) NOT NULL,
  "firstName" character varying(255),
  "lastName" character varying(255) NOT NULL,
  "memberId" character varying(255),
  version bigint NOT NULL,
  CONSTRAINT "MailChimpMember_PK" PRIMARY KEY (id),
  CONSTRAINT "MailChimpMember_emailAddress_UNQ" UNIQUE ("emailAddress")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mailchimp."MailChimpMember"
  OWNER TO isis;
