-- One-time, additive authentication migration for the existing Task Flow schema.
-- Preserves every account and assigns a deterministic username where needed.
DECLARE
  column_count NUMBER;
  index_count NUMBER;
BEGIN
  SELECT COUNT(*) INTO column_count
    FROM USER_TAB_COLUMNS
   WHERE TABLE_NAME = 'USERS' AND COLUMN_NAME = 'USERNAME';

  IF column_count = 0 THEN
    EXECUTE IMMEDIATE 'ALTER TABLE USERS ADD (USERNAME VARCHAR2(30))';
  END IF;

  EXECUTE IMMEDIATE 'UPDATE USERS SET USERNAME = ''user_'' || USER_ID WHERE USERNAME IS NULL';
  EXECUTE IMMEDIATE 'ALTER TABLE USERS MODIFY (USERNAME NOT NULL)';

  SELECT COUNT(*) INTO index_count
    FROM USER_INDEXES
   WHERE INDEX_NAME = 'UQ_USERS_USERNAME_CI';

  IF index_count = 0 THEN
    EXECUTE IMMEDIATE 'CREATE UNIQUE INDEX UQ_USERS_USERNAME_CI ON USERS (LOWER(USERNAME))';
  END IF;

  COMMIT;
END;
/
