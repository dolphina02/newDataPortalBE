-- LINA Data Portal Database Initialization
-- Run this script to set up the complete database

-- ============================================
-- DATABASE SETUP
-- ============================================

-- Create database (if running as superuser)
-- CREATE DATABASE neondb;
-- \c neondb;

-- Create schema if needed
-- CREATE SCHEMA IF NOT EXISTS public;

-- ============================================
-- EXECUTE SCHEMA CREATION
-- ============================================

\echo 'Creating database schema...'
\i schema.sql

-- ============================================
-- EXECUTE SAMPLE DATA INSERTION
-- ============================================

\echo 'Inserting sample data...'
\i sample-data.sql

-- ============================================
-- VERIFICATION
-- ============================================

\echo 'Verifying database setup...'

-- Show all tables
SELECT table_name, table_type 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- Show record counts
SELECT 
    'dashboards' as table_name, 
    COUNT(*) as record_count 
FROM dashboards
UNION ALL
SELECT 'dashboard_tags', COUNT(*) FROM dashboard_tags
UNION ALL
SELECT 'approvals', COUNT(*) FROM approvals
UNION ALL
SELECT 'reports', COUNT(*) FROM reports
UNION ALL
SELECT 'report_tags', COUNT(*) FROM report_tags
UNION ALL
SELECT 'data_tables', COUNT(*) FROM data_tables
UNION ALL
SELECT 'data_table_tags', COUNT(*) FROM data_table_tags
UNION ALL
SELECT 'data_table_columns', COUNT(*) FROM data_table_columns
UNION ALL
SELECT 'ml_models', COUNT(*) FROM ml_models
UNION ALL
SELECT 'model_features', COUNT(*) FROM model_features
UNION ALL
SELECT 'model_tags', COUNT(*) FROM model_tags
UNION ALL
SELECT 'api_endpoints', COUNT(*) FROM api_endpoints
UNION ALL
SELECT 'api_tags', COUNT(*) FROM api_tags
ORDER BY table_name;

\echo 'Database initialization completed successfully!'
\echo 'You can now start the Spring Boot application.'