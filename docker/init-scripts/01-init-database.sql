-- Database initialization script for policy-etl
-- Bu script PostgreSQL container'ı başlatıldığında otomatik olarak çalışır

-- Eğer database yoksa oluştur (PostgreSQL Docker image zaten POSTGRES_DB ile oluşturuyor)
-- CREATE DATABASE IF NOT EXISTS policy_etl_db; -- PostgreSQL syntax farklı, gerek yok

-- Temel kullanıcı ve yetkiler (gerekirse)
-- GRANT ALL PRIVILEGES ON DATABASE policy_etl_db TO postgres;

-- İhtiyaç duyulabilecek extension'lar
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log tablosu (isteğe bağlı)
CREATE TABLE IF NOT EXISTS application_logs (
    id SERIAL PRIMARY KEY,
    log_level VARCHAR(20),
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- İsteğe bağlı: İlk veri ekleme
INSERT INTO application_logs (log_level, message) 
VALUES ('INFO', 'PostgreSQL database initialized successfully for policy-etl');

-- Comments
COMMENT ON DATABASE policy_etl_db IS 'Policy ETL application database';
COMMENT ON TABLE application_logs IS 'Application logging table';
