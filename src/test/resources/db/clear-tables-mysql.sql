-- Run before db/mysql/data.sql by ResetDatabaseListener
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE visits;
TRUNCATE TABLE pets;
TRUNCATE TABLE owners;
TRUNCATE TABLE types;
TRUNCATE TABLE vet_specialties;
TRUNCATE TABLE vets;
TRUNCATE TABLE specialties;
SET FOREIGN_KEY_CHECKS = 1;
