# Database Migration Rehberi

## 📋 Genel Bakış

Policy ETL projesinde **database şema yönetimi manuel** olarak yapılır. Spring Boot otomatik DDL oluşturmaz.

### ✅ Avantajlar
- Versiyon kontrolü (Git)
- Geri alma (Rollback) desteği
- Production güvenliği
- Index optimizasyonu
- Team collaboration
- Code review sürecine dahil edilme

---

## 🔧 Konfigürasyon

### application.yml
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Entity'leri DB şeması ile karşılaştırır, değiştirmez
```

**Olası değerler:**
- `validate`: Entity'ler ile DB şemasını doğrular (önerilen - production)
- `none`: Hiçbir şey yapmaz (en katı)
- ~~`update`~~: Kullanma! (Otomatik şema değişikliği yapar)
- ~~`create`~~: Kullanma! (Her başlangıçta yeniden oluşturur)
- ~~`create-drop`~~: Kullanma! (Kapanışta siler)

---

## 📁 Dosya Yapısı

```
docker/init-scripts/
├── 01-init-database.sql       # Database ve extension'lar
├── 02-spring-batch-schema.sql # Spring Batch meta tabloları
├── 03-create-tables.sql       # 🔥 Ana şema (MANUAL)
└── 99-rollback-schema.sql     # Rollback scripti (sadece dev)
```

### Script Çalışma Sırası
Docker Compose başlatıldığında scriptler **alfabetik sıra** ile çalışır:
1. `01-init-database.sql` → Extensions ve log tablosu
2. `02-spring-batch-schema.sql` → Spring Batch tabloları
3. `03-create-tables.sql` → Application tabloları

---

## 🚀 İlk Kurulum

### 1. Docker ile Otomatik Kurulum
```bash
cd docker
start-dev.bat  # Windows
# veya
./start-dev.sh  # Linux/Mac
```

Docker container başlatıldığında init scriptleri otomatik çalışır.

### 2. Manuel Kurulum (Mevcut DB'ye)
```bash
psql -U postgres -d policy_etl_db -f docker/init-scripts/03-create-tables.sql
```

---

## 🔄 Migration Stratejisi

### Yeni Tablo/Kolon Ekleme

1. **Entity'yi güncelle**
   ```java
   @Entity
   public class TrafficInsuranceOffer {
       @Column(name = "new_field")
       private String newField; // Yeni alan
   }
   ```

2. **Migration scripti oluştur**
   ```sql
   -- docker/init-scripts/04-add-new-field.sql
   ALTER TABLE traffic_insurance_offer 
   ADD COLUMN new_field VARCHAR(255);
   
   COMMENT ON COLUMN traffic_insurance_offer.new_field 
   IS 'Description of new field';
   ```

3. **Manuel uygula**
   ```bash
   psql -U postgres -d policy_etl_db -f docker/init-scripts/04-add-new-field.sql
   ```

4. **Git'e commit et**
   ```bash
   git add docker/init-scripts/04-add-new-field.sql
   git commit -m "feat: add new_field to traffic_insurance_offer"
   ```

### Rollback (Geri Alma)
```sql
-- docker/init-scripts/04-add-new-field-rollback.sql
ALTER TABLE traffic_insurance_offer DROP COLUMN new_field;
```

---

## 🗑️ Tüm Şemayı Silme (DEV ONLY!)

⚠️ **UYARI**: Bu işlem TÜM verileri siler!

```bash
# Sadece development ortamında!
psql -U postgres -d policy_etl_db -f docker/init-scripts/99-rollback-schema.sql
```

Veya Docker container'ı tamamen yeniden başlat:
```bash
cd docker
stop-dev.bat
docker volume rm policy-etl_postgres-data
start-dev.bat
```

---

## 🔍 Şema Doğrulama

### Spring Boot Başlatma
Uygulama başlatıldığında `ddl-auto: validate` sayesinde:
- ✅ Entity'ler ile DB şeması eşleşirse → Başarıyla başlar
- ❌ Uyumsuzluk varsa → Hata fırlatır ve başlamaz

### Manuel Kontrol
```sql
-- Tüm tabloları listele
\dt

-- Tablo yapısını incele
\d traffic_insurance_offer

-- Index'leri listele
\di

-- Foreign key'leri görüntüle
SELECT 
    tc.table_name, 
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY';
```

---

## 📊 Mevcut Şema Yapısı

### Document Domain
- `pdf_store` → Uploaded PDF files
- `pdf_text` → Extracted text content

### Insurance Domain
- `traffic_insurance_offer` → Aggregate root (header)
- `traffic_insurance_offer_vehicle` → Vehicle details
- `traffic_insurance_offer_coverage` → Coverage breakdown
- `traffic_insurance_offer_premium` → Premium breakdown

### İlişkiler
```
pdf_store (1) ←→ (1) pdf_text
pdf_store (1) → (n) traffic_insurance_offer
traffic_insurance_offer (1) → (n) traffic_insurance_offer_vehicle
traffic_insurance_offer (1) → (n) traffic_insurance_offer_coverage
traffic_insurance_offer (1) → (n) traffic_insurance_offer_premium
```

---

## 🎯 Best Practices

### ✅ Yapılması Gerekenler
1. Her değişiklik için ayrı migration scripti oluştur
2. Migration'ları versiyon kontrolüne al
3. Rollback scriptleri hazırla
4. Migration'ları code review'dan geçir
5. `ddl-auto: validate` kullan (production)
6. Index'leri bilinçli ekle/optimize et
7. Foreign key'lerde `ON DELETE` davranışını belirle

### ❌ Yapılmaması Gerekenler
1. `ddl-auto: update/create` kullanma (production)
2. Manuel değişiklikleri git'e almadan bırakma
3. Production'da doğrudan ALTER TABLE çalıştırma (önce test et)
4. Rollback stratejisi olmadan migration yapma
5. Breaking change'leri aniden uygulama

---

## 🔮 Gelecek: Liquibase/Flyway

İleride otomatik migration tool eklemek isterseniz:

### Flyway
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Migration dosyaları:
```
src/main/resources/db/migration/
├── V1__init_schema.sql
├── V2__add_new_field.sql
└── V3__add_index.sql
```

### Liquibase
```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

---

## 📞 Yardım

- Entity değişikliğinden sonra migration script'ini güncellemeyi unutma!
- Migration script'lerini alfabetik sıraya göre isimlendirin (örn: `04-`, `05-`)
- Production'a geçmeden önce tüm migration'ları test ortamında deneyin

---

**Son Güncelleme**: 2025-10-08  
**Proje**: Policy ETL  
**Database**: PostgreSQL 16
