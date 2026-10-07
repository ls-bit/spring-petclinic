-- Run before db/postgres/data.sql by @IntegrationTest
TRUNCATE TABLE visits, pets, owners, types, vet_specialties, vets, specialties RESTART IDENTITY;
