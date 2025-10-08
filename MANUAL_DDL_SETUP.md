# Manual DDL Setup - Policy ETL

## 📌 Özet

Bu proje **manuel database şema yönetimi** kullanır. Spring Boot otomatik DDL oluşturmaz.

## ✅ Yapılan Değişiklikler

### 1. Spring Boot Konfigürasyonu
**`application.yml`**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # ❌ update değil → ✅ validate
```

**Öncesi**: `ddl-auto: update` (Otomatik şema değişikliği)  
**Sonrası**: `ddl-auto: validate` (Sadece doğrulama, değiştirme yok)

### 2. Database Schema Script
**`docker/init-scripts/03-create-tables.sql`** ✨ YENİ
- 6 tablo tanımı (pdf_store, pdf_text, traffic_insurance_offer, vehicle, coverage, premium)
- 15+ index tanımı (performans)
- Foreign key constraint'leri
- Unique constraint'ler (business key)
- Table/column comment'leri (dokümantasyon)

### 3. Rollback Script
**`docker/init-scripts/99-rollback-schema.sql`** ✨ YENİ
- Tüm tabloları siler (DEV only!)
- CASCADE ile bağımlılıkları temizler

### 4. Dokümantasyon
**`docker/DATABASE_MIGRATION_GUIDE.md`** ✨ YENİ
- Migration stratejisi
- Best practices
- Örnek senaryolar
- Rollback rehberi

**`docker/README.md`** 🔄 GÜNCELLENDİ
- Şema yönetimi bölümü eklendi
- Init script açıklamaları

---

## 🚀 Hızlı Başlangıç

### 1. Docker ile Otomatik Kurulum
```bash
cd docker
start-dev.bat  # Windows
```

Bu komut:
✅ PostgreSQL container başlatır  
✅ 03-create-tables.sql otomatik çalışır  
✅ Tüm tablolar/index'ler oluşur  

### 2. Spring Boot Başlatma
```bash
cd ..
mvn spring-boot:run
```

Uygulama başlarken:
✅ Entity'leri DB şeması ile karşılaştırır  
✅ Uyuşuyorsa başlar  
❌ Uyuşmuyorsa hata verir (şemayı güncelleyin!)  

---

## 🔄 Workflow: Entity Değişikliği

### Senaryo: Yeni Alan Ekleme

#### 1. Entity'yi Güncelle
```java
@Entity
public class TrafficInsuranceOffer {
    // ... mevcut alanlar
    
    @Column(name = "new_field")
    private String newField;  // 🆕 YENİ
}
```

#### 2. Migration Script Oluştur
```bash
# Dosya: docker/init-scripts/04-add-new-field.sql
```
```sql
ALTER TABLE traffic_insurance_offer 
ADD COLUMN new_field VARCHAR(255);

COMMENT ON COLUMN traffic_insurance_offer.new_field 
IS 'Yeni alan açıklaması';
```

#### 3. Manuel Uygula
```bash
psql -U postgres -d policy_etl_db -f docker/init-scripts/04-add-new-field.sql
```

#### 4. Test Et
```bash
mvn spring-boot:run
# ✅ Başarıyla başlamalı (ddl-auto: validate geçer)
```

#### 5. Git'e Al
```bash
git add docker/init-scripts/04-add-new-field.sql
git add src/main/java/com/etl/policy/entity/insurance/TrafficInsuranceOffer.java
git commit -m "feat: add new_field to TrafficInsuranceOffer"
```

---

## 📊 Mevcut Şema

### Tablolar
```
Document Domain:
├── pdf_store (6 kolon, 3 index)
└── pdf_text (5 kolon, 2 index)

Insurance Domain:
├── traffic_insurance_offer (17 kolon, 6 index) [Aggregate Root]
├── traffic_insurance_offer_vehicle (11 kolon, 3 index)
├── traffic_insurance_offer_coverage (4 kolon, 2 index)
└── traffic_insurance_offer_premium (4 kolon, 2 index)
```

### İlişkiler
```
pdf_store 1:1 pdf_text
pdf_store 1:N traffic_insurance_offer
traffic_insurance_offer 1:N vehicle/coverage/premium (CASCADE ALL)
```

### Unique Constraints
- `pdf_store.content_sha256` (duplicate detection)
- `traffic_insurance_offer(offer_no, endorsement_no)` (business key)
- `vehicle.chassis_no`
- `coverage(header_id, coverage_key)`
- `premium(header_id, premium_key)`

---

## 🎯 Neden Manuel DDL?

### ✅ Avantajlar
| Özellik | Manuel DDL | Otomatik (`update`) |
|---------|-----------|---------------------|
| **Güvenlik** | ✅ Production safe | ❌ Tehlikeli |
| **Versiyon Kontrolü** | ✅ Git'te takip | ❌ Takip edilemez |
| **Rollback** | ✅ Kolay | ❌ Zor/Imkansız |
| **Index Optimizasyonu** | ✅ Manuel kontrol | ❌ Otomatik (suboptimal) |
| **Code Review** | ✅ Evet | ❌ Hayır |
| **Breaking Change Kontrolü** | ✅ Görünür | ❌ Surprise! |
| **Team Collaboration** | ✅ Şeffaf | ❌ Kaotik |

### ❌ Otomatik DDL Riskleri
- Production'da yanlışlıkla tablo drop
- Index'lerin silinmesi
- Veri kaybı (column rename)
- Migration geçmişi yok
- Geri alma imkansız

---

## 🔍 Doğrulama

### Schema Kontrolü
```sql
-- PostgreSQL'e bağlan
psql -U postgres -d policy_etl_db

-- Tabloları listele
\dt

-- Tablo yapısını görüntüle
\d traffic_insurance_offer

-- Index'leri listele
\di

-- Foreign key'leri görüntüle
\d+ traffic_insurance_offer
```

### Spring Boot Validation
```bash
mvn spring-boot:run
```

Beklenen log:
```
✅ Hibernate: Validation successful
✅ Started PolicyEtlApplication in X seconds
```

Hata durumunda:
```
❌ SchemaManagementException: Schema-validation: missing column [...]
```
→ Migration script'ini çalıştırmayı unuttunuz!

---

## 🆘 Sorun Giderme

### Hata: "Schema validation failed"
**Sebep**: Entity ile DB şeması uyuşmuyor  
**Çözüm**: Eksik migration script'ini çalıştırın

### Hata: "Table already exists"
**Sebep**: Docker volume'de eski şema var  
**Çözüm**:
```bash
docker-compose down -v  # Volume'ü sil
docker-compose up -d    # Yeniden başlat
```

### Şemayı Sıfırlama (Development)
```bash
# Yöntem 1: Docker volume sil
docker-compose down -v
docker-compose up -d

# Yöntem 2: Rollback script
psql -U postgres -d policy_etl_db -f docker/init-scripts/99-rollback-schema.sql
psql -U postgres -d policy_etl_db -f docker/init-scripts/03-create-tables.sql
```

---

## 📚 Daha Fazla Bilgi

- 📖 [Database Migration Guide](docker/DATABASE_MIGRATION_GUIDE.md) - Detaylı migration stratejisi
- 📖 [Docker README](docker/README.md) - Container yönetimi
- 📖 [SQL Scripts](docker/init-scripts/) - Tüm DDL dosyaları

---

## 🔮 Gelecek İyileştirmeler

İleride otomatik migration tool eklemek isterseniz:

### Flyway (Önerilen)
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

Dosya yapısı:
```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_new_field.sql
└── V3__add_index.sql
```

Avantajlar:
- Otomatik versiyon takibi
- Checksum validation
- Rollback desteği (Pro)
- Migration history tablosu

---

**Son Güncelleme**: 2025-10-08  
**Durum**: ✅ Production Ready  
**Spring Boot Version**: 3.5.5  
**Database**: PostgreSQL 16
