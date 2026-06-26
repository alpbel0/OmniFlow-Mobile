# OmniFlow Mobile Page Architecture

Bu dokuman, OmniFlow uygulamasinin mobile odakli sayfa mimarisini tanimlar. Amaç, uygulamada hangi page'lerin bulunacagini, bu page'lerin ne ise yaradigini, hangi kullanici tipine hitap ettigini ve temel aksiyonlarini tek yerde toplamaktir.

Bu calisma:

- sadece mobile uygulama akisini baz alir,
- admin panelini de dahil eder,
- error state / loading state gibi mikro ekranlari detaylandirmaz,
- "olabilir" ekranlari degil, uygulamada bulunmasi gereken gercek page'leri yazar.

Not:

- Bu dokumanda `collaborator` referanslari, trip collaboration ozelliginin gelecekte eklenecegi varsayimiyla hedef modeli de yansitir.
- Su an collaboration aktif degilse, bu alanlar "gelecek faza hazir page kurgusu" olarak okunmalidir.

---

## 1. Uygulama Genel YapisI

Mobile uygulama genel olarak 5 ana alan etrafinda kurulur:

1. Auth ve onboarding
2. Ana kullanici navigasyonu
3. Trip planning ve trip yonetimi
4. Sosyal / kesif / topluluk
5. Profil / ayarlar / admin

Onerilen genel mobile yapi:

- `Auth Stack`
- `Main Bottom Tab Navigation`
- `Trip Stack`
- `Social Stack`
- `Profile Stack`
- `Admin Stack`

---

## 2. Auth ve Giris Oncesi Sayfalar

## 2.1 Splash Screen

**Amac:** Uygulama acildiginda kullaniciya markayi gostermek, token kontrolu ve ilk yonlendirmeyi yapmak.

**Kim gorur:** Herkes

**Ana icerik:**
- OmniFlow logo
- Kisa bekleme / auto routing

**Ana aksiyonlar:**
- Oturum varsa ana uygulamaya gecis
- Oturum yoksa onboarding veya auth sayfalarina gecis

## 2.2 Onboarding Screen 1

**Amac:** Uygulamayi ilk kez goren kullaniciya urunun temel degerini anlatmak.

**Kim gorur:** Yeni kullanici

**Ana icerik:**
- Kisa tanitim
- Uygulamanin ne yaptigi

**Ana aksiyonlar:**
- Ileri
- Gec

## 2.3 Onboarding Screen 2

**Amac:** Trip planning ve sosyal yonu anlatmak.

**Kim gorur:** Yeni kullanici

**Ana icerik:**
- Route planning
- Explore / fork / social ozeti

**Ana aksiyonlar:**
- Ileri
- Gec

## 2.4 Onboarding Screen 3

**Amac:** Live Trip Mode, paylaşım ve topluluk tarafini anlatmak.

**Kim gorur:** Yeni kullanici

**Ana icerik:**
- Seyahat sirasinda kullanim
- Trip paylasma ve takip

**Ana aksiyonlar:**
- Basla
- Giris Yap
- Kayit Ol

## 2.5 Login Page

**Amac:** Mevcut kullanicinin hesaba giris yapmasi.

**Kim gorur:** Oturumu olmayan kullanici

**Ana icerik:**
- Email
- Password
- Google ile giris butonu

**Ana aksiyonlar:**
- Login
- Google Login
- Forgot Password
- Register sayfasina gecis

## 2.6 Register Page

**Amac:** Yeni hesap olusturma.

**Kim gorur:** Oturumu olmayan kullanici

**Ana icerik:**
- Username
- Email
- Password
- Password confirm

**Ana aksiyonlar:**
- Register
- Login sayfasina donus

## 2.7 Verify Email Info Page

**Amac:** Kullaniciya email dogrulamasi gerektigini gostermek.

**Kim gorur:** Kayit sonrasi kullanici

**Ana icerik:**
- Email gonderildi bilgisi
- Mailini kontrol et yonlendirmesi

**Ana aksiyonlar:**
- Resend verification
- Login ekranina don

## 2.8 Forgot Password Page

**Amac:** Kullanici sifre sifirlama talebi olusturur.

**Kim gorur:** Oturumu olmayan kullanici

**Ana icerik:**
- Email input

**Ana aksiyonlar:**
- Reset link gonder

## 2.9 Reset Password Page

**Amac:** Kullanici gelen token ile sifresini yeniler.

**Kim gorur:** Sifre sifirlama akisi icindeki kullanici

**Ana icerik:**
- New password
- Confirm password

**Ana aksiyonlar:**
- Sifreyi yenile

---

## 3. Ana Mobile Navigation

Bottom tab yapisinda temel 5 ana alan onerilir:

1. `Home`
2. `Explore`
3. `Trips`
4. `Community`
5. `Profile`

Admin kullanicilar icin buna ek olarak panel girisi `Profile` icinden acilabilir.

### Tasarım Kararları (M2)

Bottom tab'ın kesinleşen hali:

```
Home    Explore    [ + CREATE ]    Trips    Community
```

- **4 sekme + ortada raised Create butonu.** Sekmeler **sadece ikon** (text label yok); aktif = dolu ikon + mavi, pasif = çizgi ikon + gri.
- Ortadaki `+` → doğrudan **Create Trip wizard**'ı açar; 56dp mavi daire, bottom nav pill'in 8dp üstüne çıkar.
- Bottom nav **floating pill** stili: tam genişlik değil, kenarlarda ~16dp boşluk bırakarak havada asılı görünür; hafif gölge.
- **Profile sekme değil** → ana sekme sayfalarının **sağ üstünde avatar** olarak durur (tıkla → Profile; admin de oradan).
- **Notifications sekme değil** → ana sekme sayfalarının **sağ üstünde zil ikonu**.
- **Üst bar kuralı:** Ana sekme sayfalarında (Home/Explore/Trips/Community) sağ üstte **zil + avatar**. Detay/alt sayfalarda üst bar bağlamsaldır: sol üstte **← geri**, sağ üstte sayfaya özel aksiyon (zil/avatar burada **yoktur**).

---

## 4. Home ve Ana Kullanici Deneyimi

## 4.1 Home Page

**Amac:** Uygulamanin ana giris noktasi. Kullaniciya kisayollar, aktif trip, oneriler ve temel hareket alanlari sunulur.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Devam eden / en yakin trip karti
- Quick actions
- Featured trips
- Onerilen aksiyonlar

**Ana aksiyonlar:**
- Yeni trip olustur
- Aktif trip'i ac
- Explore'a git
- Saved trips'e git
- ~~AI Asistan'a git~~ *(⏸️ askıya alındı — bkz. 4.3; M12'ye ertelendi)*

### Tasarım Kararları (M2)

**Yaklaşım: "Kişisel Seyahat Paneli"**
Home = "benim ne durumum?" sorusunun cevabı. Keşif işi Explore'a, sosyal içerik Community'ye bırakılır; Home sadece kullanıcıyı karşılar ve durumu özetler.

**Dikey kurgu (yukarıdan aşağı):**

1. **Selam** — "Merhaba {username} 👋" + sağ üstte 🔔
2. **Kompakt arama çubuğu** — "Where can we take you?" placeholder; **tıklanınca Explore'a gider, search açık**. Veri burada tutulmaz; sadece giriş noktası.
3. **Yaklaşan Gezi kartı** — kullanıcının aktif/yakın trip'i. Birden fazla varsa **yatay swipe + dot**.
   - **Öncelik sırası:** şu an seyahatte → en yakın published upcoming → en son draft → (yoksa) boş durum CTA.
   - **Durum metni:** upcoming → "3 gün sonra başlıyor" · bugün → "Bugün başlıyor" · seyahatte → "Devam ediyor".
   - Karta tıklama → Trip Detail açar (Live Trip Mode hook'u M8).
4. **İlham Al** — yatay kaydırmalı **Destination kartları** (yuvarlak köşeli, full-photo, şehir adı altta, ok ikonu). Tıklama → **Destination Detail** ekranı (bkz. 5.5). M2'de statik liste; B4.6 hazır olunca `GET /api/v1/destinations/trending` ile dinamik.
5. **Öne Çıkan Geziler** — `GET /explore/featured` yatay carousel; kart → public Trip Detail. Rating + kullanıcı adı gösterilir. Boşsa bölüm gizlenir.
6. **Topluluktan** — 1-2 community post preview ("@selin.k yeni bir gezi paylaştı"). Tıklama → Community Feed veya Post Detail.

**Boş durum (hiç trip yok):**
Yaklaşan Gezi kartı yerine **"İlk seyahatini planla"** büyük CTA kartı gösterilir. Arama çubuğu + İlham Al + Topluluktan bölümleri yine görünür (boş hissetmez).

**Ekran durumları:**
- **Loading:** skeleton/shimmer; tam ekran spinner yok.
- **Kısmi hata:** bir bölüm yüklenmezse sadece o bölüm "tekrar dene" gösterir, geri kalanı çökmez.
- **Pull-to-refresh:** var.

**AI:** Home'da AI girişi yok (⏸️ askıya alındı — bkz. 4.3).

## 4.2 Notifications Page

**Amac:** Kullanici bildirimlerini tek yerde gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Follow
- Upvote
- Comment
- Mention
- Fork
- Trip ile ilgili reminder bildirimleri

**Ana aksiyonlar:**
- Bildirimi ac
- Okundu isaretle
- Tumunu okundu yap

### Tasarım Kararları (M2)

**Liste kurgusu:**
- Kronolojik sıra (en yeni üstte), tarih bazlı gruplama yok.
- **Okunmamış** → mavi arka plan; **okunmuş** → beyaz. Bildirime normal tap yapılınca okundu sayılır ve beyaza döner, ilgili sayfaya (trip/post/profil) gider.

**Kategori filtresi:**
Listenin üstünde yatay kaydırmalı chip'ler:
`Tümü · Sosyal · Trip · Hatırlatıcı`
Chip'ler bir yere **götürmez**, sadece listeyi filtreler.

**Multi-select (Gmail pattern):**
- **Uzun basış** → seçim modu açılır; bildirimin solunda checkbox belirir.
- Seçim modunda üstte action bar: `X öğe seçildi | Okundu işaretle | Sil | Tümünü seç`
- Seçim iptal (geri tuşu veya boş alana tap) → normal moda dön.

**Bildirim satırı formatı:**
- Sol: tip ikonu (👤 Follow · ⬆️ Upvote · 💬 Comment · 🔁 Fork · 📅 Reminder)
- Orta: kullanıcı adı + eylem metni (ör. "@selin gezini beğendi") · zaman damgası
- Sağ (varsa): içerik thumbnail (trip kapağı / post görseli)

**Tap davranışı:**
- **Avatar veya kullanıcı adına tap** → o kişinin Public Profile sayfası
- **Satırın geri kalanı / thumbnail'a tap** → ilgili içerik (Trip Detail / Post Detail)
- **Uzun basış** → seçim modu açılır

**Boş durum:** "Henüz bildirim yok" + ikon; chip filtresi aktifken "Bu kategoride bildirim yok"

**Pull-to-refresh:** var.

## 4.3 AI Chat / Assistant Page

> ⏸️ **ASKIYA ALINDI.** AI özelliği şu an kapsam dışıdır; `MOBILE_ROADMAP.md → M12` / `BACKEND_ROADMAP_V2 → B6`'ya ertelenmiştir. Backend tarafı da scaffold seviyesinde (henüz uygulanmadı). Bu sayfa ileride ele alınacak; mevcut tasarım/akış kararlarına dahil edilmez. Home ve diğer ekranlarda AI girişi şimdilik gösterilmez.

**Amac:** Kullanicinin AI destekli gezi yardimi almasi. Bu sayfa tam rota ureten bir ekran degil; once ihtiyaci netlestiren, sonra tool-grounded arama ve oneriler sunan yardimci katmandir.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Chat arayuzu
- Soru-cevap akisi
- Onerilen sonuc kartlari
- Trip baglamina gore yonlendirme

**Ana aksiyonlar:**
- Soru sor
- Oneriyi ac
- Sonucu trip'e uygula
- Place / provider sonucunu ac

---

## 5. Explore / Kesif Sayfalari

## 5.1 Explore Main Page

**Amac:** Public trip'lerin kesfedildigi ana ekran.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Explore trip listesi
- Filtreler
- Sort secenekleri
- Search girisi

**Ana aksiyonlar:**
- Filtre uygula
- Trip detail ac
- Trip kaydet
- Trip forkla

### Tasarım Kararları (M3)

- **Üstte:** klasik arama çubuğu + filtre ikonu (Explore'a Home search bar'ından da gelinebilir, o zaman search alanı otomatik açık gelir).
- **Kategori chips:** yatay kaydırmalı, trip türüne göre (Kültür, Doğa, Sahil, Şehir turu vb.). Home'da değil, burada yaşar.
- **Soru bazlı slide filter:** "Hangi şehir?" / "Fiyat aralığı?" / "Tür?" eksenlerinde yatay kaydırmalı filtre şeridi — onboarding slide mantığına benzer, kompakt seçim deneyimi. Klasik filtre ikonunun altında oturur.

## 5.2 Explore Featured Page

**Amac:** One cikan trip'leri gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Featured / highlighted trip listesi

**Ana aksiyonlar:**
- Trip detail ac
- Kaydet
- Forkla

## 5.3 Search Results Page

**Amac:** Kullanici, trip, place, post veya tag arama sonuclarini gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Search result sections
- Tab veya segmented yapi

**Ana aksiyonlar:**
- Ilgili detail sayfasina git

## 5.5 Destination Detail Page

**Amac:** Bir destinasyona (şehir/ülke) ait genel keşif ekranı. Home'daki "İlham Al" kartlarından ve Explore'dan erişilir.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Destinasyon adı + hero fotoğraf
- Bu destinasyona yapılmış public trip'ler (community)
- Öne çıkan yerler / attractions özeti
- Destinasyona giden kullanıcı planları

**Ana aksiyonlar:**
- Public trip detail aç
- Trip oluştur (bu destinasyonu otomatik doldur)
- Place detail aç

---

## 5.4 Place Detail Page

**Amac:** Tek bir place kaydini detayli gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Place adi
- Fotograf
- Kategori
- Aciklama
- Travel style uyumu
- Google / OSM metadata

**Ana aksiyonlar:**
- Trip'e ekle
- Haritada ac
- Benzer place'leri gor

---

## 6. Trips Alani

## 6.1 My Trips Page

**Amac:** Kullanicinin trip listesini gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Draft / Published / Archived filtreleri
- Trip kartlari

**Ana aksiyonlar:**
- Trip detail ac
- Yeni trip olustur

## 6.2 Saved Trips Page

**Amac:** Kaydedilen trip'leri gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Saved trip listesi

**Ana aksiyonlar:**
- Trip detail ac
- Saved'den cikar

## 6.2.1 Collections Page

**Amac:** Kullanicinin olusturdugu coklu kaydetme koleksiyonlarini gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Koleksiyon listesi
- Her koleksiyondaki trip sayisi

**Ana aksiyonlar:**
- Koleksiyon ac
- Yeni koleksiyon olustur
- Koleksiyon duzenle
- Koleksiyon sil

## 6.2.2 Collection Detail Page

**Amac:** Tek bir koleksiyonun icindeki trip'leri gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Koleksiyon adi
- Icindeki trip listesi

**Ana aksiyonlar:**
- Trip detail ac
- Koleksiyondan cikar
- Koleksiyona trip ekle

## 6.3 Trip Detail Page

**Amac:** Bir trip'in genel detaylarini gostermek.

**Kim gorur:** Owner veya yetkili kullanici / public trip ise diger kullanicilar

**Ana icerik:**
- Kapak gorseli
- Baslik / aciklama
- Trip bilgileri
- Destinations ozeti
- Timeline ozeti
- Flight / hotel ozeti
- Budget ozeti

**Ana aksiyonlar:**
- Publish
- Archive
- Edit
- Delete
- Paylas
- Save
- Unsave
- Upvote
- Fork
- Live Trip Mode'a gec
- Budget summary ac
- Recommend places ac

## 6.4 Create Trip Entry Page

**Amac:** Kullaniciya yeni trip olusturmak icin giris noktasi vermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Wizard ile olustur secenegi
- Belki onceki template / duplicate secenekleri

**Ana aksiyonlar:**
- Wizard baslat

---

## 7. Trip Planning Wizard Sayfalari

Wizard'in her adimi ayri page olarak ele alinmalidir.

## 7.1 Wizard - Origin Page

**Amac:** Kalkis noktasi ve cikis ulkesini almak.

**Ana icerik:**
- Origin city
- Origin country

**Ana aksiyonlar:**
- Devam et

## 7.2 Wizard - Destinations Page

**Amac:** Gidilecek sehirleri ve tarihlerini toplamak.

**Ana icerik:**
- Destinations listesi
- Arrival / departure tarihleri
- Order bilgisi

**Ana aksiyonlar:**
- Destination ekle
- Destination sil
- Sirayi degistir
- Devam et

## 7.3 Wizard - Person Count Page

**Amac:** Kac kisi gidilecegini belirlemek.

**Ana icerik:**
- Person count secimi

**Ana aksiyonlar:**
- Devam et

## 7.4 Wizard - Travel Companion Page

**Amac:** Seyahat tipi secmek.

**Ana icerik:**
- Solo / Couple / Family / Friends vb.

**Ana aksiyonlar:**
- Devam et

## 7.5 Wizard - Budget Page

**Amac:** Butce tier'i ve gerekiyorsa manual butce degerini almak.

**Ana icerik:**
- Economy / Standard / Premium
- Manual budget

**Ana aksiyonlar:**
- Devam et

## 7.6 Wizard - Vibe / Travel Styles Page

**Amac:** Kullaniciya uygun travel style secimini almak.

**Ana icerik:**
- Multi-select style secimi

**Ana aksiyonlar:**
- Devam et

## 7.7 Wizard - Tempo Page

**Amac:** Gunluk gezi temposunu almak.

**Ana icerik:**
- Slow / Moderate / Fast

**Ana aksiyonlar:**
- Devam et

## 7.8 Wizard - Transport Preference Page

**Amac:** Sehir ici ulasim tercihini almak.

**Ana icerik:**
- Walk / Transit / Mixed vb.

**Ana aksiyonlar:**
- Devam et

## 7.9 Wizard - Review & Create Page

**Amac:** Tum secimlerin son kontrolden gecip trip'in olusturulmasi.

**Ana icerik:**
- Ozet bilgi
- Destination listesi
- Budget fallback sonucu

**Ana aksiyonlar:**
- Trip olustur
- Onceki adimlara don

---

## 8. Trip Planning Icin Alt Sayfalar

## 8.1 Trip Destinations Management Page

**Amac:** Trip olustuktan sonra destinasyonlari sonradan duzenlemek.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Destination listesi
- Tarihler
- Order

**Ana aksiyonlar:**
- Destination ekle
- Guncelle
- Sil

## 8.2 Budget Summary Page

**Amac:** Trip'in butce dagilimini gostermek.

**Kim gorur:** Owner veya yetkili kullanici

**Ana icerik:**
- Tahmini toplam maliyet
- Adjusted budget tier
- Flight / hotel / activity bazli kirilim

**Ana aksiyonlar:**
- Ileri planlamaya don

## 8.3 Recommended Places Page

**Amac:** Bir destination icin puanlanmis ve onerilmis place listesi gostermek.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Recommended
- Neutral
- Other

**Ana aksiyonlar:**
- Timeline'a ekle
- Place detail ac

---

## 9. Timeline ve Live Trip Sayfalari

## 9.1 Timeline Page

**Amac:** Trip'in planlanan tum entry'lerini gostermek.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Gun bazli timeline
- Entry listesi
- Lock / visited / siralama durumu

**Ana aksiyonlar:**
- Entry ekle
- Entry duzenle
- Entry sil
- Reorder
- Optimize et
- Mark visited

## 9.1.1 Timeline Optimization Result Page

**Amac:** Sistem tarafindan onerilen daha verimli timeline siralamasini gostermek.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Mevcut sira vs onerilen sira
- Tahmini zaman kazanci
- Hangi entry'lerin yer degistirdigi

**Ana aksiyonlar:**
- Oneriyi uygula
- Reddet

## 9.2 Create Timeline Entry Page

**Amac:** Yeni timeline entry olusturmak.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Entry type secimi
- Place / custom event / transport / flight / accommodation formlari

**Ana aksiyonlar:**
- Kaydet

## 9.3 Edit Timeline Entry Page

**Amac:** Var olan bir timeline entry'yi duzenlemek.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Entry alanlari
- Lock / timing / notes / provider referanslari

**Ana aksiyonlar:**
- Guncelle
- Sil

## 9.4 Live Trip Mode Page

**Amac:** Seyahat sirasinda kullaniciya bugunun planini operasyonel olarak gostermek.

**Kim gorur:** Aktif trip icindeki kullanici

**Ana icerik:**
- Bugunun timeline'i
- Harita
- Aktif konum
- Yakindaki mekanlar

**Ana aksiyonlar:**
- Visited isaretle
- Place detail ac
- Haritada rota gor
- Notes / visit log ac

## 9.5 Visit Log Page

**Amac:** Kullanici bir yeri gercekte nasil deneyimledigini kaydeder.

**Kim gorur:** Aktif trip kullanicisi

**Ana icerik:**
- Gercek harcama
- Puan
- Kisa yorum

**Ana aksiyonlar:**
- Log kaydet

## 9.6 Memories / Journal Page

**Amac:** Gezi sirasinda olusan notlari, fotoğraflari ve gunluk akis kayitlarini gostermek.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Gunluk anilar
- Kisa notlar
- Fotograflar

**Ana aksiyonlar:**
- Yeni memory ekle
- Guncelle
- Paylas

## 9.7 Trip Summary Page

**Amac:** Trip bittikten sonra kapanis deneyimi sunmak.

**Kim gorur:** Trip owner / collaborator

**Ana icerik:**
- Kac yer ziyaret edildi
- Tahmini vs gercek harcama
- One cikan duraklar
- Toplam rota ozeti

**Ana aksiyonlar:**
- Paylas
- Journal'a git
- Trip'i tekrar kullan

---

## 10. Provider ve Dis Veri Sayfalari

## 10.1 Provider Flights Page

**Amac:** Trip veya planning akisi icin kullanilabilecek flight verilerini gostermek.

**Kim gorur:** Planning yapan kullanici

**Ana icerik:**
- Route bazli ucuslar
- Snapshot / freshness bilgisi

**Ana aksiyonlar:**
- Timeline'a ekle
- Detay gor

## 10.2 Provider Hotels Page

**Amac:** Trip veya planning akisi icin kullanilabilecek hotel verilerini gostermek.

**Kim gorur:** Planning yapan kullanici

**Ana icerik:**
- Hotel listesi
- Segment / budget bilgisi
- Freshness bilgisi

**Ana aksiyonlar:**
- Timeline'a ekle
- Detay gor

## 10.3 External Booking Browser Page

**Amac:** Uygulama kapsami disindaki provider sitelerini uygulama icinden acmak.

**Kim gorur:** Trip planlayan kullanici

**Ana icerik:**
- In-app browser / custom tab

**Ana aksiyonlar:**
- Booking.com vb. ac

---

## 11. Community ve Sosyal Sayfalar

> **M2 geçici kararı:** Community tab (👥) M5'te tam Feed yapılana kadar **"Gezginleri Keşfet" ekranını** (Suggested Follows + Top Contributors) doğrudan gösterir. M5 tamamlanınca Community tab'ı Feed'e geçer; bu ekran "Gezginler" alt bölümü olarak içine taşınır.

## 11.1 Community Feed Page

**Amac:** Sosyal icerik akisinin ana sayfasi.

**Kim gorur:** Authenticated user

**Ana icerik:**
- For You
- Following
- Latest

**Ana aksiyonlar:**
- Post detail ac
- Yeni post olustur
- Like / comment

## 11.2 Create Post Page

**Amac:** Yeni sosyal post paylasmak.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Metin
- Fotograf
- Tag
- Iliskili trip / place

**Ana aksiyonlar:**
- Postla

## 11.3 Post Detail Page

**Amac:** Bir post'u tam detayiyla gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Icerik
- Fotograf
- Yorumlar
- Upvote durumu

**Ana aksiyonlar:**
- Comment ekle
- Upvote
- Report

## 11.4 Comments Page

**Amac:** Post yorumlarinin tam akis halinde goruntulenmesi.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Comment tree
- Reply yapisi

**Ana aksiyonlar:**
- Comment ekle
- Reply yaz

## 11.5 Community Tips Page

**Amac:** Bir trip'e birakilan topluluk onerilerini gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Tip listesi
- Upvote bilgisi

**Ana aksiyonlar:**
- Tip ekle
- Upvote
- Report

## 11.6 Create Tip Page

**Amac:** Yeni tip eklemek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Tip metni
- Ilgili place / trip baglami

**Ana aksiyonlar:**
- Tip gonder

---

## 12. Profil ve Kullanici Sayfalari

## 12.1 My Profile Page

**Amac:** Kullanicinin kendi profilini gormesi.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Profil bilgileri
- Karma score
- Followers / following
- Kendi postlari
- Kendi trip'leri

**Ana aksiyonlar:**
- Profili duzenle
- Profil fotografi yukle
- Saved trips'e git
- Collections'a git
- Settings'e git

### Tasarım Kararları (M2)

**Header — kompakt layout (sol avatar + sağ bilgi):**
- Sol: avatar (72dp daire, profil fotoğrafı)
- Sağ: `@username` (16sp SemiBold) · bio (13sp #6F7F95) · `⭐ 1.240 karma` (13sp)
- Altında: `[142] Takipçi  [89] Takip` — her ikisi de tıklanabilir → Followers/Following listesi
- Header altında: `[Profili Düzenle]` full-width outlined buton

**Karma gösterimi:** Sadece sayı (`⭐ 1.240`). Badge/seviye sistemi backend'de yok; şimdilik eklenmez.

**İçerik filtresi:**
Yatay chip'ler: `Tümü · Geziler · Paylaşımlar`
- Default: `Tümü` — geziler ve paylaşımlar karma karışık grid/liste halinde gelir
- `Geziler` chip'ine basınca sadece trip kartları (`GET /api/v1/users/{id}/trips`)
- `Paylaşımlar` chip'ine basınca sadece postlar (`GET /api/v1/users/me/posts`)
- Grid: 2 sütun kart (geziler için tam foto kart, postlar için metin kartı)

**Boş durum:** İçerik yoksa chip altında "Henüz içerik paylaşılmadı" + ikon. Chip bazlı: "Bu kategoride içerik yok."

**Top bar:** `← geri` sol · `⚙️ Ayarlar` sağ (Settings sayfasına gider)

## 12.2 Edit Profile Page

**Amac:** Profil alanlarini guncellemek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Bio
- Profile photo

**Ana aksiyonlar:**
- Kaydet

### Tasarım Kararları (M2)

**Düzenlenebilir alanlar:**
- **Profil fotoğrafı:** Ortada büyük avatar (96dp) + "📷 Değiştir" → galeri/kamera → `POST /api/v1/users/me/profile-photo`. Yükleme sırasında avatar üzerinde loading overlay; başarıda snackbar.
- **Bio:** Çok satırlı text field, max 150 karakter, sayaç gösterilir (`0/150`). `PUT /api/v1/users/me`
- **Konum:** Tek satır text field, ör. "İstanbul, Türkiye". `PUT /api/v1/users/me` *(⛔ Bağımlılık: B0.5)*
- **Seyahat Stili:** Multi-select chip'ler (mevcut `TravelStyle` enum'undan): `Macera · Kültür · Sahil · Şehir · Doğa · Gastronomi`. Seçili chip mavi, seçilmemiş outline. `PUT /api/v1/users/me` *(⛔ Bağımlılık: B0.5)*

**Read-only alan:**
- **Kullanıcı adı:** Gösterilir ama düzenlenemez (`🔒` ikonu + "Değiştirilemez" yardım metni). Backend `UpdateProfileRequest` username güncellemesini desteklemiyor.

**Top bar:** `← geri` sol · `[Kaydet]` text button sağ (aktif değişiklik yoksa disabled)

## 12.3 Public User Profile Page

**Amac:** Baska bir kullanicinin profilini gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Username
- Bio
- Karma
- Public postlar
- Public trip'ler

**Ana aksiyonlar:**
- Follow
- Unfollow
- Report
- Block

## 12.4 Followers Page

**Amac:** Bir kullanicinin follower listesini gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Follower listesi

**Ana aksiyonlar:**
- Profile git
- Follow back

## 12.5 Following Page

**Amac:** Bir kullanicinin takip ettigi hesaplari gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Following listesi

**Ana aksiyonlar:**
- Profile git
- Unfollow

## 12.6 Suggested Follows Page

**Amac:** Kullaniciya takip onermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Onerilen kullanici listesi

**Ana aksiyonlar:**
- Follow
- Profile git

## 12.7 Top Contributors Page

**Amac:** En aktif / en yuksek katki yapan kullanicilari gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Ranking listesi

**Ana aksiyonlar:**
- Profile git

## 12.8 Blocked Users Page

**Amac:** Engellenen kullanicilari gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Blocked user listesi

**Ana aksiyonlar:**
- Unblock

---

## 13. Ayarlar Sayfalari

## 13.1 Settings Page

**Amac:** Hesap ve uygulama ayarlarina giris noktasi.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Account
- Notifications
- Privacy
- Currency
- Logout

**Ana aksiyonlar:**
- Alt ayarlara git

## 13.2 Notification Preferences Page

**Amac:** Hangi bildirimlerin alinacagini secmek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Push tercihleri
- Sosyal / trip reminder tercihleri

**Ana aksiyonlar:**
- Tercihleri kaydet

## 13.3 Privacy & Visibility Page

**Amac:** Hesap ve trip gorunurluk ayarlarini yonetmek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Profil gorunurlugu
- Trip paylasim ayarlari

**Ana aksiyonlar:**
- Ayarlari kaydet

## 13.4 Currency Preferences Page

**Amac:** Kullaniciya tercih edilen para birimi secimi vermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Ana para birimi
- Ikinci gosterim tercihleri

**Ana aksiyonlar:**
- Kaydet

## 13.5 Trip Collaboration Management Page

**Amac:** Bir trip'e davet edilen kullanicilari ve rollerini yonetmek.

**Kim gorur:** Trip owner

**Ana icerik:**
- Collaborator listesi
- Bekleyen davetler
- Yetki seviyeleri

**Ana aksiyonlar:**
- Davet gonder
- Daveti iptal et
- Collaborator cikar
- Yetki degistir

**Not:** Bu sayfa, trip collaboration ozelligi aktif edildiginde devreye girecek gelecege hazir ekranlardan biridir.

---

## 14. Moderasyon ile Ilgili Kullanici Sayfalari

## 14.1 Report Reason Page

**Amac:** Kullaniciya rapor sebebi sectirmek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Spam
- Harassment
- Fake info
- Inappropriate content

**Ana aksiyonlar:**
- Report gonder

## 14.2 Report Submitted Page

**Amac:** Raporun alindigini gostermek.

**Kim gorur:** Authenticated user

**Ana icerik:**
- Tesekkur / alindi mesaji

**Ana aksiyonlar:**
- Geri don

---

## 15. Admin Panel Sayfalari

Admin panel mobile uygulama icinden ayrilmis bir admin stack olarak kurgulanabilir.

## 15.1 Admin Dashboard Page

**Amac:** Moderasyon ve sistem yonetiminin ana giris noktasi.

**Kim gorur:** Admin

**Ana icerik:**
- Ozet kartlar
- Bekleyen raporlar
- Kritik aksiyon kisayollari

**Ana aksiyonlar:**
- Users
- Posts
- Reports
- Logs

## 15.2 Admin Users Page

**Amac:** Tum kullanicilari listelemek.

**Kim gorur:** Admin

**Ana icerik:**
- Kullanici listesi
- Filtre / search

**Ana aksiyonlar:**
- User detail ac
- Suspend
- Unsuspend

## 15.3 Admin User Detail Page

**Amac:** Tek bir kullaniciya ait detayli moderasyon ekranini sunmak.

**Kim gorur:** Admin

**Ana icerik:**
- Profil bilgisi
- Urettigi icerikler
- Durum bilgisi

**Ana aksiyonlar:**
- Suspend
- Unsuspend
- Soft restriction uygula

## 15.4 Admin Posts Page

**Amac:** Post moderasyonu yapmak.

**Kim gorur:** Admin

**Ana icerik:**
- Post listesi
- Search / filter

**Ana aksiyonlar:**
- Detail ac
- Delete
- Hide

## 15.5 Admin Post Detail Page

**Amac:** Bir post'u detayli incelemek.

**Kim gorur:** Admin

**Ana icerik:**
- Post icerigi
- Ilgili yorumlar
- Kullanici bilgisi

**Ana aksiyonlar:**
- Delete
- Hide
- Report baglamini gor

## 15.6 Admin Reports Page

**Amac:** Kullanici raporlarini listelemek ve incelemek.

**Kim gorur:** Admin

**Ana icerik:**
- Report listesi
- Hedef icerik / kullanici
- Sebep bilgisi

**Ana aksiyonlar:**
- Report detail ac
- Isleme al
- Kapat

## 15.7 Admin Report Detail Page

**Amac:** Bir raporun tam inceleme ekranini sunmak.

**Kim gorur:** Admin

**Ana icerik:**
- Kim raporlamis
- Neyi raporlamis
- Sebep
- Ilgili hedef nesne

**Ana aksiyonlar:**
- Ignore
- Hide
- Delete
- Suspend

## 15.8 Admin Audit Log Page

**Amac:** Admin aksiyonlarinin izini surmek.

**Kim gorur:** Admin

**Ana icerik:**
- Kim ne zaman ne yapti
- Suspend / unsuspend
- Delete / hide
- Report resolution

**Ana aksiyonlar:**
- Filtrele
- Detay incele

---

## 16. Onerilen Ana Kullanici Akislari

## 16.1 Yeni Kullanici Akisi

`Splash -> Onboarding -> Register/Login -> Verify Email Info -> Home`

## 16.2 Trip Olusturma Akisi

`Home / My Trips -> Create Trip Entry -> Wizard adimlari -> Review & Create -> Trip Detail -> Timeline / Recommendations / Budget`

## 16.3 Seyahat Sirasinda Kullanim Akisi

`Home -> Aktif Trip -> Live Trip Mode -> Timeline / Harita -> Visited -> Visit Log -> Trip Summary`

## 16.4 Sosyal Kesif Akisi

`Explore -> Trip Detail -> Save / Fork`

`Community Feed -> Post Detail -> Comment / Follow / Profile`

## 16.5 Admin Moderasyon Akisi

`Profile -> Admin Dashboard -> Reports / Posts / Users -> Detail -> Action -> Audit Log`

---

## 17. Sonuc

Bu page mimarisi ile OmniFlow mobile uygulamasi:

- giris ve onboarding akisini,
- trip planning ve live trip deneyimini,
- explore ve social akislarini,
- profil ve ayarlar alanlarini,
- ayrica admin moderasyon panelini

tek bir tutarli mobile urun yapisinda birlestirmis olur.

Bu sayfa listesi, mevcut backend feature'lari ve planlanan yeni ozellikler baz alinarak olusturulmustur.
