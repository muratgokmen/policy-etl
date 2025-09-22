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

`init-scripts/` dizinindeki SQL dosyaları PostgreSQL container'ı ilk kez başlatıldığında otomatik olarak çalıştırılır.
