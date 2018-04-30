CREATE SCHEMA sync
  AUTHORIZATION isis;

CREATE TABLE sync."StatusEntry"
(
  id serial NOT NULL,
  "localETag" character varying(255) NOT NULL,
  "remoteETag" character varying(255) NOT NULL,
  "uniqueId" character varying(255) NOT NULL,
  version timestamp with time zone NOT NULL,
  CONSTRAINT "StatusEntry_PK" PRIMARY KEY (id),
  CONSTRAINT "SyncObjectLocal_uniqueId_UNQ" UNIQUE ("uniqueId")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sync."StatusEntry"
  OWNER TO isis;


CREATE TABLE sync."SyncObjectLocal"
(
  id serial NOT NULL,
  field character varying(255) NOT NULL,
  "uniqueId" character varying(255) NOT NULL,
  version timestamp with time zone NOT NULL,
  CONSTRAINT "SyncObjectLocal_PK" PRIMARY KEY (id),
  CONSTRAINT "ObjectLocal_uniqueId_UNQ" UNIQUE ("uniqueId")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sync."SyncObjectLocal"
  OWNER TO isis;

CREATE TABLE sync."SyncObjectRemote"
(
  id serial NOT NULL,
  field character varying(255) NOT NULL,
  "uniqueId" character varying(255) NOT NULL,
  version timestamp with time zone NOT NULL,
  CONSTRAINT "SyncObjectRemote_PK" PRIMARY KEY (id),
  CONSTRAINT "ObjectRemote_uniqueId_UNQ" UNIQUE ("uniqueId")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sync."SyncObjectRemote"
  OWNER TO isis;
