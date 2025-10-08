-- ============================================================================
-- Policy ETL - Database Schema (DDL)
-- ============================================================================
-- Bu script tüm tablo, index ve constraint'leri oluşturur.
-- Spring Boot uygulaması ddl-auto: validate ile bu şemayı doğrular.
-- ============================================================================

-- ============================================================================
-- DOCUMENT DOMAIN TABLES
-- ============================================================================

-- PDF Store Table - Uploaded PDF files
CREATE TABLE IF NOT EXISTS pdf_store (
    id BIGSERIAL PRIMARY KEY,
    source_name VARCHAR(255),
    filename VARCHAR(255),
    content BYTEA,  -- Large binary object for PDF content
    content_sha256 VARCHAR(64) NOT NULL,
    received_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT uk_pdf_store_sha256 UNIQUE (content_sha256)
);

-- PDF Text Table - Extracted text from PDFs
CREATE TABLE IF NOT EXISTS pdf_text (
    id BIGSERIAL PRIMARY KEY,
    pdf_id BIGINT,
    text TEXT,  -- Large text object for extracted content
    ocr_applied BOOLEAN DEFAULT FALSE,
    extracted_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key
    CONSTRAINT fk_pdf_text_pdf FOREIGN KEY (pdf_id) 
        REFERENCES pdf_store(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT uk_pdf_text_pdf_id UNIQUE (pdf_id)
);

-- ============================================================================
-- INSURANCE DOMAIN TABLES
-- ============================================================================

-- Traffic Insurance Offer - Aggregate Root (Header)
CREATE TABLE IF NOT EXISTS traffic_insurance_offer (
    id BIGSERIAL PRIMARY KEY,
    pdf_id BIGINT,
    
    -- Party Information (denormalized snapshots)
    insurer_name VARCHAR(255),
    insurer_address VARCHAR(500),
    agent_name VARCHAR(255),
    agent_registry_no VARCHAR(50),
    agent_address VARCHAR(500),
    customer_name VARCHAR(255),
    customer_id_masked VARCHAR(50),
    
    -- Offer Core Fields
    offer_no VARCHAR(50),
    endorsement_no VARCHAR(50),
    issue_date DATE,
    start_date DATE,
    end_date DATE,
    day_count INTEGER,
    source_confidence SMALLINT,
    
    -- Foreign Key
    CONSTRAINT fk_traffic_offer_pdf FOREIGN KEY (pdf_id) 
        REFERENCES pdf_store(id) ON DELETE SET NULL,
    
    -- Business Key Constraint
    CONSTRAINT uk_traffic_offer_business_key UNIQUE (offer_no, endorsement_no)
);

-- Traffic Insurance Offer Vehicle
CREATE TABLE IF NOT EXISTS traffic_insurance_offer_vehicle (
    id BIGSERIAL PRIMARY KEY,
    header_id BIGINT NOT NULL,
    
    -- Vehicle Information
    plate VARCHAR(20),
    brand VARCHAR(100),
    type VARCHAR(100),
    engine_no VARCHAR(50),
    chassis_no VARCHAR(50),
    model_year INTEGER,
    registration_date DATE,
    usage_type VARCHAR(100),
    seat_count INTEGER,
    step VARCHAR(50),
    
    -- Foreign Key
    CONSTRAINT fk_vehicle_header FOREIGN KEY (header_id) 
        REFERENCES traffic_insurance_offer(id) ON DELETE CASCADE,
    
    -- Business Constraint
    CONSTRAINT uk_vehicle_chassis_no UNIQUE (chassis_no)
);

-- Traffic Insurance Offer Coverage
CREATE TABLE IF NOT EXISTS traffic_insurance_offer_coverage (
    id BIGSERIAL PRIMARY KEY,
    header_id BIGINT NOT NULL,
    
    -- Coverage Details
    coverage_key VARCHAR(100),
    coverage_value DECIMAL(19, 2),
    
    -- Foreign Key
    CONSTRAINT fk_coverage_header FOREIGN KEY (header_id) 
        REFERENCES traffic_insurance_offer(id) ON DELETE CASCADE,
    
    -- Business Constraint (one coverage type per offer)
    CONSTRAINT uk_coverage_header_key UNIQUE (header_id, coverage_key)
);

-- Traffic Insurance Offer Premium
CREATE TABLE IF NOT EXISTS traffic_insurance_offer_premium (
    id BIGSERIAL PRIMARY KEY,
    header_id BIGINT NOT NULL,
    
    -- Premium Details
    premium_key VARCHAR(100),
    premium_value DECIMAL(19, 2),
    
    -- Foreign Key
    CONSTRAINT fk_premium_header FOREIGN KEY (header_id) 
        REFERENCES traffic_insurance_offer(id) ON DELETE CASCADE,
    
    -- Business Constraint (one premium type per offer)
    CONSTRAINT uk_premium_header_key UNIQUE (header_id, premium_key)
);

-- ============================================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================================

-- Document Domain Indexes
CREATE INDEX IF NOT EXISTS idx_pdf_store_source_name ON pdf_store(source_name);
CREATE INDEX IF NOT EXISTS idx_pdf_store_received_at ON pdf_store(received_at DESC);
CREATE INDEX IF NOT EXISTS idx_pdf_text_pdf_id ON pdf_text(pdf_id);

-- Insurance Domain Indexes
CREATE INDEX IF NOT EXISTS idx_traffic_offer_pdf_id ON traffic_insurance_offer(pdf_id);
CREATE INDEX IF NOT EXISTS idx_traffic_offer_offer_no ON traffic_insurance_offer(offer_no);
CREATE INDEX IF NOT EXISTS idx_traffic_offer_start_date ON traffic_insurance_offer(start_date);
CREATE INDEX IF NOT EXISTS idx_traffic_offer_end_date ON traffic_insurance_offer(end_date);
CREATE INDEX IF NOT EXISTS idx_traffic_offer_customer_name ON traffic_insurance_offer(customer_name);

-- Child Table Foreign Key Indexes (for JOIN performance)
CREATE INDEX IF NOT EXISTS idx_vehicle_header_id ON traffic_insurance_offer_vehicle(header_id);
CREATE INDEX IF NOT EXISTS idx_coverage_header_id ON traffic_insurance_offer_coverage(header_id);
CREATE INDEX IF NOT EXISTS idx_premium_header_id ON traffic_insurance_offer_premium(header_id);

-- Business Query Indexes
CREATE INDEX IF NOT EXISTS idx_vehicle_plate ON traffic_insurance_offer_vehicle(plate);
CREATE INDEX IF NOT EXISTS idx_coverage_key ON traffic_insurance_offer_coverage(coverage_key);
CREATE INDEX IF NOT EXISTS idx_premium_key ON traffic_insurance_offer_premium(premium_key);

-- ============================================================================
-- TABLE COMMENTS (Documentation)
-- ============================================================================

COMMENT ON TABLE pdf_store IS 'Stores uploaded PDF files with SHA256 hash for deduplication';
COMMENT ON TABLE pdf_text IS 'Extracted text content from PDF files (one-to-one with pdf_store)';
COMMENT ON TABLE traffic_insurance_offer IS 'Traffic insurance offer aggregate root (header)';
COMMENT ON TABLE traffic_insurance_offer_vehicle IS 'Vehicle information associated with traffic insurance offers';
COMMENT ON TABLE traffic_insurance_offer_coverage IS 'Coverage details for traffic insurance offers';
COMMENT ON TABLE traffic_insurance_offer_premium IS 'Premium breakdown for traffic insurance offers';

-- ============================================================================
-- COLUMN COMMENTS
-- ============================================================================

COMMENT ON COLUMN pdf_store.content_sha256 IS 'SHA256 hash for duplicate detection';
COMMENT ON COLUMN pdf_text.ocr_applied IS 'Flag indicating if OCR was applied to extract text';
COMMENT ON COLUMN traffic_insurance_offer.offer_no IS 'Business key: Offer number';
COMMENT ON COLUMN traffic_insurance_offer.endorsement_no IS 'Business key: Endorsement number';
COMMENT ON COLUMN traffic_insurance_offer.source_confidence IS 'Confidence score from PDF parsing (0-100)';
COMMENT ON COLUMN traffic_insurance_offer_vehicle.chassis_no IS 'Unique vehicle identifier';

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================

-- Log successful execution
INSERT INTO application_logs (log_level, message) 
VALUES ('INFO', 'Database schema created successfully - all tables, indexes, and constraints');
