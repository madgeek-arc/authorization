CREATE TABLE Auth_Triple (
  id        INTEGER PRIMARY KEY,
  subject   VARCHAR(128) NOT NULL,
  action    VARCHAR(128) NOT NULL,
  object    VARCHAR(128) NOT NULL);
