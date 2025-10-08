# Docker Setup için Policy ETL

Bu dizinde PostgreSQL Docker setup dosyaları bulunmaktadır.

## Kullanım

### 1. PostgreSQL'i Başlatma
```bash
docker-compose up -d postgres
```

### 2. PostgreSQL + pgAdmin'i Başlatma
```bash
docker-compose up -d
```

### 3. Servisleri Durdurma
```bash
docker-compose down
```

### 4. Verileri Temizleme (DİKKAT: Tüm veriler silinir!)
```bash
docker-compose down -v
```

## Bağlantı Bilgileri

### PostgreSQL
- **Host:** localhost
- **Port:** 5432
- **Database:** policy_etl_db
- **Username:** postgres
- **Password:** postgres

### pgAdmin (Web UI)
- **URL:** http://localhost:8080
- **Email:** admin@policy-etl.com
- **Password:** admin123

## Init Scripts

`init-scripts/` dizinindeki SQL dosyaları PostgreSQL container'ı ilk kez başlatıldığında **alfabetik sıra** ile otomatik olarak çalıştırılır:

1. **01-init-database.sql** - Database, extension'lar ve log tablosu
2. **02-spring-batch-schema.sql** - Spring Batch meta tabloları
3. **03-create-tables.sql** - 🔥 **Ana application şeması (MANUAL)**
4. **99-rollback-schema.sql** - Rollback scripti (sadece development)

⚠️ **ÖNEMLİ**: Spring Boot `ddl-auto: validate` ile çalışır. Şema değişiklikleri otomatik yapılmaz!

Detaylı bilgi için: [DATABASE_MIGRATION_GUIDE.md](DATABASE_MIGRATION_GUIDE.md)

## Şema Yönetimi

### İlk Kurulum
```bash
# Windows
start-dev.bat

# Linux/Mac  
./start-dev.sh
```

Container başlatıldığında tüm tablolar, index'ler ve constraint'ler otomatik oluşur.

### Yeni Migration Ekleme
1. Yeni SQL dosyası oluştur: `docker/init-scripts/04-my-migration.sql`
2. Mevcut DB'ye manuel uygula:
   ```bash
   psql -U postgres -d policy_etl_db -f docker/init-scripts/04-my-migration.sql
   ```
3. Git'e commit et

### Şemayı Tamamen Sıfırlama (DEV ONLY!)
```bash
docker-compose down -v
docker-compose up -d
```

Veya manuel rollback:
```bash
psql -U postgres -d policy_etl_db -f docker/init-scripts/99-rollback-schema.sql
```
