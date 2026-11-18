# Roadmap (Medium Series)

Bu dosya, Nexus PIM projesi uzerinden yazilacak Medium post serisinin yol haritasidir.

## Seri formati
- Format: Tek teknoloji / tek kavram = tek mini post
- Hedef: Enterprise PIM uygulamasinda kullandigim yaklasim ve araclari kisa, odakli yazilarla anlatmak
- Standart akýþ: Problem -> Neden bu secim -> Projede nasil kullandim -> Dikkat edilecekler -> Sonraki yaziya baglanti

## Bolum sirasi
- [x] 01. Giris: PIM nedir ve neden gerekli?
- [x] 02. Mimari temel: Clean Architecture ve katmanlama
- [x] 03. DDD pratigi: Domain/Application/Infrastructure ayrimi
- [ ] 04. Kotlin + Spring Boot 3 ile microservice iskeleti
- [ ] 05. Gradle multi-module yapi ve ortak ayarlar
- [ ] 06. Docker Compose ile local altyapi (Postgres/Kafka/Elastic/Redis)
- [ ] 07. Flyway ile DB migration yonetimi
- [ ] 08. Spring Data JPA ile persistence katmani
- [ ] 09. REST API tasarimi (Attribute servisi ornegi)
- [ ] 10. DTO Validation stratejisi (Request/Domain dogrulamasi)
- [ ] 11. Attribute modeli: Set/Group/Item iliskileri
- [ ] 12. Event contracts ve mesajlasma modeli
- [ ] 13. Kafka ile event backbone (event-driven sync)
- [ ] 14. Search servisi ve Elasticsearch projection
- [ ] 15. Observability: Micrometer + Prometheus + Grafana
- [ ] 16. Test stratejisi: smoke testler ve service-level testler
- [ ] 17. Yayinlama: production hazirliklari ve deployment notlari

## Notlar
- Her post, projedeki gercek kod parcalariyla desteklenecek.
- Her post sonunda bir sonraki bolume baglanti verilecek.
- Yazilar 5-8 dakikalik okuma hedefiyle yazilacak.

## TODO:
1- docker-compose ile bir kafka cluster'? olu?tur, elasticsearch ekle
2- event contract ve mesajla?ma modelini implemente et servislerde
3- search servisi ald??? mesajlar? , anlay?p bir elasticsearch index'ine bir search index'i olu?turacak ve search api'lerini açacak