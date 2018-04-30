ALTER TABLE tti."Communicatie"
ADD COLUMN "afzender" character varying(2048);
UPDATE tti."Communicatie"
   SET "afzender"='klant';
ALTER TABLE tti."Communicatie"
ALTER COLUMN "afzender" SET NOT NULL;

