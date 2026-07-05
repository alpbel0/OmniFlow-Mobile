# Trip Detail Page — Tasarım Kararları

> Bu doküman, Trip Detail sayfasının **sıfırdan yeniden tasarım** sürecinde alınan kararları içerir. Önceki tasarım (`OMNIFLOW_PAGE_ARCHITECTURE.md § 6.3`'te bulunan KATMAN 1-4 / Kategori-Gün toggle / blur-modal yaklaşımı) kullanıcının vizyonunu tam yansıtmadığı için terk edildi ve dokümandan temizlendi. Bu sayfa, o kararların yerine geçen **güncel ve tek kaynak** dokümandır.
>
> **Yöntem:** Bu sefer her karar netleşmeden ilerlenmiyor, boşluk bırakılmadan konuşuluyor. Tasarım netleştikçe Claude Design'a gönderilip görsel olarak doğrulanacak.

---

## Veri Orkestrasyonu (Sayfa Açılışı)

> **Karar:** Aggregate endpoint (`TripDetailAggregateResponse`) **yok** — mevcut backend konvansiyonu zaten REST-per-resource (Flights, Hotels, Budget Summary hepsi ayrı endpoint, hiçbiri `GetTripById`'ye birleştirilmemiş). Bu tutarlılığı bozmamak için Trip Detail açılışında **4 endpoint paralel** çağrılır, mobil tarafta birleştirilir.

**Çağrılan endpoint'ler ve besledikleri bölüm:**

> **Not (casing tutarsızlığı):** `TripsController`'ın route'ları `[controller]` token'ından geliyor → `/api/v1/Trips/...` (**büyük T**, sınıf adından). `TimelineController` ise explicit literal route override kullanıyor → `/api/v1/trips/...` (**küçük t**). Aşağıdaki path'ler gerçek kod casing'ini yansıtıyor — kısaltma değil.

| # | Endpoint | Besler |
|---|---|---|
| 1 | `GET /api/v1/Trips/{id}` | Detaylar + Map (destinasyon + koordinat, bkz. B0.12) |
| 2 | `GET /api/v1/trips/{tripId}/timeline` | Timeline (Review + Gün gün, tam entry listesi) |
| 3 | `GET /api/v1/Trips/{tripId}/budget-summary` | Toplam Bütçe satırı |
| 4 | `GET /api/v1/Trips/{id}/checklist` (bkz. B0.9, henüz implemente edilmedi) | Kategori kartlarının checkbox durumları |

**Partial Failure Davranışı:**

| Çağrı | Başarısız olursa |
|---|---|
| `GET /api/v1/Trips/{id}` | **Bloklayıcı** — tam ekran hata + "Tekrar Dene" (çekirdek veri, bu olmadan sayfa render edilemez) |
| `GET /api/v1/trips/{tripId}/timeline` | Timeline bölümü inline hata + "Tekrar Dene"; Detaylar/Map etkilenmez |
| `GET /api/v1/Trips/{tripId}/budget-summary` | Toplam Bütçe satırı inline hata/retry; geri kalan Timeline çalışmaya devam eder |
| `GET /api/v1/Trips/{id}/checklist` | Kategori kartı ring'leri "yükleniyor/bilinmiyor" durumunda kalır, checkbox etkileşimi geçici devre dışı; diğer her şey çalışır |

Bu davranış, projenin zaten kurulu "Pagination hatası → inline retry, mevcut liste korunur" konvansiyonuyla tutarlı (bkz. `MOBILE_ROADMAP.md` "UI State Konvansiyonu") — sadece **trip'in kendisi (1 numaralı çağrı) tüm sayfayı bloklar**, geri kalan 3 çağrı bağımsız/gracefully degrade eder.

> **5. çağrı (sayfa açılışında değil, on-demand):** `GET /api/v1/Trips/{id}/route` (bkz. `BACKEND_ROADMAP_V2.md → B0.15`) yukarıdaki 4'e dahil değil — sayfa açılışında otomatik çağrılmaz, kullanıcı Map'te `Yol` moduna geçtiğinde tetiklenir. Başarısız olursa Map bölümündeki mevcut fallback kuralı uygulanır (sessizce Kuş Bakışı'na dön, `Yol` toggle'ı devre dışı bırakılır) — ayrı bir hata state'i yok.

---

## Genel Sayfa Yapısı

> **Karar değişikliği:** Sayfa artık **tek parça scroll** ile değil, **sabit yükseklikli, kullanıcı tarafından yeniden boyutlandırılabilen 3 bölüm** (resizable panes) olarak çalışıyor. Genel sayfa kendisi scroll edilmiyor — bölümler arasındaki **2 handle** ile pay bölüşülüyor.

Sayfanın en üstünde **sabit bir üst bar** (resizable pane'lerin dışında, her zaman sabit) durur; altında **3 resizable bölüm**:

```
┌─────────────────────────┐
│  ← Geri   Başlık    ⋮   │  ← Sabit Üst Bar (resize edilmez)
├─────────────────────────┤
│    Detaylar              │
│    Map                   │
│    Timeline               │
└─────────────────────────┘
```

1. **Detaylar** — cover/özet bölümü (üstte)
2. **Map** — harita bölümü (ortada)
3. **Timeline** — gezinin içeriği (altta)

### Sabit Üst Bar

Üst bar, pane boyutlarından bağımsız, **her zaman aynı yükseklikte sabit** kalır (eski tasarımdaki KATMAN 1 mantığıyla aynı):

- **Sol:** `←` geri butonu
- **Orta:** Trip başlığı — uzun başlıklarda **ellipsis (`...`) ile kırpılır** (`maxLines=1`), standart mobil davranış
- **Sağ (Owner/Misafir farkı burada yaşıyor, her zaman sadece 2 ikon — simetrik):**
  - **Owner:** `✏️ Edit` + `⋮ Menü` — **menü içeriği trip durumuna göre değişir:**

    | Trip Durumu | Menü İçeriği |
    |---|---|
    | **Draft** | Yayınla · Sil |
    | **Published** | Arşivle · **Düzenlemek için Taslağa Al** · Paylaş · Sil *(bkz. `BACKEND_ROADMAP_V2.md → B0.14`)* |
    | **Archived** | Yayına Al · Sil *(bkz. `BACKEND_ROADMAP_V2.md → B0.11`; **Paylaş yok** — Archived owner-only/404, paylaşılan link işe yaramaz, Draft'la tutarlı)* |

    *(`Paylaş` M3'te aktif — native share sheet, backend metadata beklenmez; detay için Misafir menüsündeki nota bakınız)*

    > **🔴 Kritik davranış — `✏️ Edit` sadece Draft'ta aktif:** Backend'de trip metadata güncelleme (`UpdateTripCommand`) ve **tüm Timeline entry mutasyonları** (create/update/delete/reorder, Kilidi Aç dahil) **Draft-only** — `Published`/`Archived` durumunda bu komutlar reddedilir. Bu yüzden `✏️ Edit` butonu **sadece trip Draft durumundayken aktif**; Published/Archived'da **disabled/gri** gösterilir. Owner düzenlemek isterse önce `⋮ → Düzenlemek için Taslağa Al` (Published'dan, bkz. B0.14) veya Archived'dan önce `Yayına Al` sonra `Düzenlemek için Taslağa Al` (iki adım) ile Draft'a döner, düzenler, sonra tekrar `Yayınla`.
    >
    > **"Düzenlemek için Taslağa Al" onay dialogu:** Tıklanınca bir onay dialogu açılır — **"Bu geziyi düzenlemek için yayından kaldıracaksın. Düzenleme bitince tekrar yayınlaman gerekecek. Devam edilsin mi?"** + İptal/Devam Et butonları. Onaylanınca `POST /api/v1/Trips/{id}/unpublish` (bkz. B0.14) çağrılır, başarılı olursa trip Draft'a döner ve Timeline'daki Edit/Kilidi Aç/Sil/+Detay Ekle aksiyonları aktifleşir. Trip Draft'tayken herkese kapalıdır (B0.10) — owner düzenlemeyi bitirince `Yayınla` ile tekrar Published'a alır.

  - **Misafir:** `❤️ Upvote` (toggle) + `⋮ Menü`
    - Menü içeriği: **🔀 Fork** / **🔖 Kaydet** (toggle — kaydedilmişse "Kaydı Kaldır") / **📤 Paylaş** / **🚩 Şikayet Et**
    - *(Misafir sadece Published trip'lere erişebildiği için menü içeriği durum bazlı değişmez)*
    - **`📤 Paylaş` — M3'te aktif ama iki eksiklik bilinerek kabul ediliyor:** Basit **native Android share sheet** (`Intent.ACTION_SEND`) ile düz bir link (`https://omniflow.app/trips/{id}`) paylaşılır.
      1. **Zengin önizleme yok** (başlık/görsel kartı) — B4.3'e bağlı, o gelene kadar link düz metin olarak açılır
      2. **🔴 Link, alıcının cihazında app'i açmaz** — App Links (`assetlinks.json`) desteği `BACKEND_ROADMAP_V2.md → B4.5` (M10) kapsamında, M3'te yok. Link'e tıklayan biri **her zaman tarayıcıda** açar (app kurulu olsa bile). "Deep link" terimi burada yanıltıcı olabilir — M3'te bu aslında sadece paylaşılabilir bir **web URL'i**, gerçek bir deep link (app'i açan) değil. B4.5 gelince aynı link otomatik app'e yönlenir, mobil tarafta ekstra değişiklik gerekmez
    - **`🚩 Şikayet Et` — M3'te disabled:** backend'de hiçbir karşılığı yok (`B5.1` sıfırdan yazılacak, M11). Login ekranındaki Google butonuyla aynı konvansiyon (Task 1.7) — **tasarımı korunan ama tıklanamaz/gri** gösterilir, B5.1 gelince aktifleşir
- **`🔖 Kaydet`** buradaki toggle, My Trips'in **Kaydedilenler / Collections** akışını tetikler (kaydedince koleksiyon seçim bottom sheet'i açılır — bkz. `OMNIFLOW_PAGE_ARCHITECTURE.md § 6.2.1`). **Backend durumu:** Collections (`BACKEND_ROADMAP_V2.md → B4.1`) henüz implemente edilmedi; My Trips'in Kaydedilenler sekmesi zaten **local mock collections** ile çalışıyor (Task 3.1) — buradaki bottom sheet de **aynı mock mekanizmayı** kullanır, B4.1 gerçek backend'e bağlanınca ikisi birden gerçek endpoint'lere geçirilir. M3'ü B4.1'e bloke etmiyoruz.
- **`Sil` aksiyonu:** her zaman bir **onay dialogu** açar ("Bu gezi kalıcı olarak silinecek. Emin misin?" + İptal/Sil butonları) — onaylanmadan API çağrısı yapılmaz
- Bu butonlar **her zaman erişilebilir** — Detaylar bölümü %0'a inse bile (kapansa bile) owner/misafir aksiyonları kaybolmaz, çünkü üst barda yaşıyorlar. Detaylar içindeki ❤️/🔀 sayıları ise sadece **salt-okunur özet gösterim**, aksiyon değil.
- **Üst bar ↔ Detaylar senkronizasyonu:** Misafir üst bardaki `❤️ Upvote`'a basıp beğendiğinde, Detaylar paneli açık durumdaysa oradaki salt-okunur upvote sayısı **anında (reaktif)** artar. İki alan da ViewModel'deki **aynı single-source-of-truth `StateFlow`**'dan beslenir — ayrı bir senkronizasyon mekanizması gerekmez, state tek yerde tutulduğu için otomatik gelir

### Erişim Kontrolü (Draft/Archived + Deep Link)

> **Bağımlılık:** `BACKEND_ROADMAP_V2.md → B0.10` — bu güvenlik düzeltmesi olmadan aşağıdaki davranış çalışmaz (şu an backend hiçbir kontrol yapmıyor, düzeltilmesi gerekiyor).

- **Owner olmayan biri** (misafir veya anonim) bir Draft/Archived trip'e **deep link ile** (veya ID tahmin ederek) erişmeye çalışırsa → backend **404** döner
- Mobil taraf bu durumda **genel bir "Bu gezi bulunamadı" hata ekranı** gösterir — Draft mı Archived mı olduğunu **belli etmez** (private trip'in var olduğu bile sızdırılmaz)
- Owner kendi Draft/Archived trip'ine her zaman erişebilir
- **Anonim kullanıcı + auth gerektiren aksiyon:** Anonim (giriş yapmamış) kullanıcı Published bir trip'i görebilir, üst bardaki `❤️ Upvote` / `⋮ Menü` içindeki `🔀 Fork` / `🔖 Kaydet` gibi butonlar **aynen görünür** ama bunlardan birine dokununca sessizce başarısız olmaz — **"Giriş yapmalısın" uyarısı + Login ekranına yönlendirme** tetiklenir (Instagram vb. uygulamalardaki standart pattern: çıkışsız gezinebilirsin, etkileşim için giriş istenir)

> **Karar değişikliği (üst bar kalabalığı riski):** İlk halde Misafir'de 3 ikon (`❤️ 🔀 ⋮`) + uzun başlık yan yana taşma/kırpılma riski taşıyordu. Çözüm: **Fork, `⋮ Menü` içine taşındı** (daha az sık kullanılan, daha "ağır" bir aksiyon olduğu için menüde kalması sorun değil), Upvote ise sık kullanılan hafif aksiyon olduğu için direkt erişilebilir kaldı. Bu sayede Owner ve Misafir'de sağ tarafta **her zaman 2 ikon** var — simetrik ve tutarlı.

### Bölüm Boyutları ve Handle'lar

İki bölüm sınırında **handle (tutamaç)** bulunur:

```
┌─────────────────┐
│    Detaylar      │  0% – 30%
├───── ▬▬▬ ───────┤  ← Handle 1 (Detaylar ↔ Map)
│      Map         │  0% – 40%
├───── ▬▬▬ ───────┤  ← Handle 2 (Map ↔ Timeline)
│    Timeline      │  türetilen (bağımsız sınırı yok)
└─────────────────┘
```

| Bölüm | Min | Maks | Default | Not |
|---|---|---|---|---|
| **Detaylar** | %0 | %30 | **%30** | Kendi içinde scroll yok |
| **Map** | %0 | %40 | **%30** | Kendi içinde scroll yok |
| **Timeline** | %30 (türetilen) | ~%100 (türetilen) | **%40** | **Kendi içinde scroll edilebilir**; bağımsız bir max'ı yok — `100% - Detaylar - Map` formülüyle kalan alanı otomatik dolduruyor |

- Timeline'ın min'i (%30), Detaylar ve Map ikisi de maksimumdayken (30+40=%70) ortaya çıkıyor; maks'ı (~%100), ikisi de %0 olduğunda ortaya çıkıyor. Bu yüzden Timeline'a ayrı bir sabit sınır tanımlamaya gerek yok.
- **Default oranlar (30/30/40) tutarlı:** Detaylar maksimumda (%30) + Map'in kendi maksimum aralığında bir orta değeri (%30) alındığında Timeline doğal olarak %40'a düşüyor — üç değer tam %100'e tamamlanıyor, handle tap-to-snap davranışı bu üçlüye döner.

**Handle Davranışı:**
- **Tutup çekme (drag):** Manuel ince ayar — handle'ı istediğin yere sürükleyip iki komşu bölüm arasında payı değiştir
- **Tek dokunuş (tap):** İlgili sınırı **default boyutuna anında snap** eder (hızlı kısayol). **Zaten default boyuttaysa hiçbir şey değişmez** — toggle/aç-kapa mekanizması değil, sadece "default'a dön" davranışıdır
- **Cascade kuralı (çift yönlü/simetrik):** Bir handle, komşusunun min/maks sınırına dayanırsa **bloklanmaz**, sürükleme "geçişken" davranır ve diğer handle'ı da iterek bir sonraki bölümden alan çeker. Yön fark etmez:
  - **Handle 1 aşağı çekilirse** (Detaylar büyür) → Map %0'a dayanınca → **Handle 2 de kayar**, Timeline'dan alan çeker
  - **Handle 2 yukarı çekilirse** (Timeline büyür) → Map %0'a dayanınca → **Handle 1 de kayar**, Detaylar'dan alan çeker (Detaylar da %0'a kadar sıkışabilir)
  - Genel kural: **hangi handle sürüklenirse sürüklensin, komşu sınırına dayanınca "cascade-through" devreye girer**, tek yönlü özel durum değil
- **Görsel:** Handle üzerinde **basit crossfade animasyonu** ile bölüm adı (ör. "Map") ↔ tutamaç çizgileri (drag indicator dash'leri) arasında geçiş olur (kategori ring'lerindeki coin-flip'ten farklı olarak burada sade bir crossfade kullanılıyor — 3D dönüş yok)
- **Dokunma alanı:** Handle'ın görsel yüksekliği ince olsa bile (4-8dp çizgi), **dokunulabilir hit area'sı Material Design standardına göre en az 48dp×48dp**, handle'ın görsel pozisyonu etrafında ortalanmış. Bölüm %0'a inse bile bu 48dp'lik dokunma alanı **her zaman erişilebilir** kalır — komşu pane'lerin üzerine "taşan" bir hit-test alanı gibi davranır, sadece görsel olarak ince kalır

- **Harita ile gesture çakışması — tampon boşluk çözümü:** Handle'ın 48dp'lik hit-area'sı, Map bölümünün **kendi render alanına hiç taşmaz**. Handle için ayrılmış 48dp'lik "zone" ile Map'in başladığı sınır arasında **gerçek bir boşluk/tampon** bırakılır — Map'in dokunma alanı bu zone'un dışında başlar, üst üste binme ihtimali mimari olarak sıfırlanır. Bu ayrım özellikle önemli çünkü **MapLibre, Compose'a `AndroidView` ile sarmalanmış native bir View** — native View'in kendi touch dispatch'i, iki Compose composable'ı arası gesture izolasyonundan farklı davranır; sınırda temiz bir tampon olmadan native/Compose arası gesture çakışma riski Compose-içi senaryodan daha yüksek olurdu
- **Haptic feedback:** Handle sürüklenip **default orana snap** ettiğinde veya bir bölüm **tam kapandığında (%0)**, cihazda hafif bir titreşim (haptic feedback) tetiklenir — fiziksel bir düğmenin yerine oturması hissi verir. Sürekli sürükleme sırasında değil, sadece bu iki "sonuç anında" tetiklenir (rahatsız edici olmasın diye)

**Yüzdelerin referans aldığı alan:** `%100 = ekran yüksekliği − sabit üst bar − bottom nav − sistem insets (status bar / navigation bar)`. Sistem insets'ler Compose'da `WindowInsets` ile zaten otomatik hesaba katılır, bizim 30/30/40 hesabımıza dahil değildir — onlar hep dışarıda tutulan alanlardır. Bu sayfada doğrudan metin girişi (IME/klavye) olmadığı için (Detay Modal salt-okunur, Edit ayrı bir form ekranına gider) IME açılma senaryosu pane hesaplarını etkilemez.

### Yatay Mod (Landscape)

Portrait'teki dikey istifleme yatay modda ciddi sıkışmaya yol açacağı için **farklı bir layout** kullanılır — dikey yerine yatay bölünme:

```
┌───────────┬──────────────────┐
│           │  Detaylar         │  ← sabit maks (yükseklik ekseni)
│           ├──── ▬▬▬ ─────────┤  ← Handle B (Detaylar ↔ Map, yükseklik)
│ Timeline  │                  │
│(genişlik  │     Map           │  ← türetilen (kalan yükseklik)
│  ekseni)  │                  │
└───────────┴──────────────────┘
            ↑
      Handle A (Timeline ↔ Sağ sütun, genişlik)
```

- **Handle A** (dikey çizgi): **Timeline (sol)** ile **sağ sütun (Detaylar+Map)** arasındaki **genişlik** payını ayarlar
- **Handle B** (yatay çizgi, sağ sütun içinde): **Detaylar (üst)** ile **Map (alt)** arasındaki **yükseklik** payını ayarlar — bu iki eksen **birbirinden bağımsız**, aralarında cascade ilişkisi yok (portrait'teki 3'lü zincirden farklı olarak burada sadece 2 ayrı, ilişkisiz eksen var)

| Bölüm | Eksen | Min | Maks | Default | Not |
|---|---|---|---|---|---|
| **Timeline** | Genişlik | %0 | %60 | **%40** | Sol sütun, tüm yükseklik boyunca |
| **Sağ sütun** (Detaylar+Map) | Genişlik | türetilen | türetilen | %60 | `100% - Timeline genişliği` |
| **Detaylar** | Yükseklik (sağ sütun içinde) | %0 | %30 | **%30** | Portrait'teki Detaylar sınırlarıyla aynı |
| **Map** | Yükseklik (sağ sütun içinde) | türetilen | türetilen | %70 | `100% - Detaylar yüksekliği` (sağ sütunun kendi yüksekliği, tam ekran yüksekliği kadar) |

- Her iki handle da **aynı davranış kurallarını** miras alır: tutup çekme (manuel), tek dokunuş (default'a snap, zaten default'taysa değişmez), 48dp minimum dokunma alanı
- Cascade kuralı burada **gerekmez** çünkü Handle A ve Handle B tamamen farklı eksenlerde (genişlik vs yükseklik) ve birbirinin sınırını etkilemez — portrait'teki gibi zincirleme bir ilişki yok
- Bu oranlar da **trip-bazlı, cihaz-yerel** hatırlanır (portrait oranlarından ayrı bir kayıt — bir trip'i portrait'te açtığında portrait oranını, landscape'te açtığında landscape oranını hatırlar)

### Bölüm Oranlarının ve Görünüm Tercihlerinin Kalıcılığı (Trip-Bazlı)

- Kullanıcının handle'larla ayarladığı oran (ör. Detaylar %15, Map %20, Timeline %65) **trip-bazlı hatırlanır** — global bir tercih değil, her trip kendi son ayarlanan oranını saklar
- **Ayrıca hatırlanan diğer tercihler (aynı trip-bazlı kayda dahil):**
  - **Review / Gün gün** mod seçimi
  - **Seçili gün sekmesi** (Gün gün modundaysa hangi gün açıktı)
- **Cihaz-yerel** bir tercih — Room DB'de `tripId` anahtarlı bir kayıt yeterli; backend'e senkron gerekmez (bu bir görüntüleme tercihi, veri değil). Alanlar: `detaylarPercent`, `mapPercent`, `timelinePercent` (portrait), `landscapeTimelineWidthPercent`, `landscapeDetaylarHeightPercent` (landscape — sağ sütunun genişliği ve Map'in yüksekliği türetilen olduğu için ayrı saklanmaz), `displayMode`, `selectedDayIndex`
- Sayfadan çıkıp tekrar girme veya ekran döndürme (configuration change) durumunda hepsi korunur
- İlk açılışta (kayıt yoksa) default değerler kullanılır: oranlar 30/30/40, mod **Review**, gün **Gün 1**

### Detaylar Bölümünün İçeriği (Tam Açık — %30)

Detaylar bölümü tam açıkken şunları içerir (kapak fotoğrafı arka plan, üzerine bindirilmiş bilgiler):

- **Kapak fotoğrafı** (arka plan, yoksa gradyan placeholder)
- **Status badge** (`🟢 Yayında` / `🟡 Taslak` / `⚫ Arşiv`)
- **Tarih aralığı** (ör. `15 – 22 Tem 2025`)
- **Başlık** (trip adı)
- **Ülke** + **Kişi sayısı** (ör. "İtalya · 2 kişi")
- **❤️ Beğeni sayısı** / **🔀 Fork sayısı** (salt-okunur özet — aksiyon butonları değil, onlar sabit üst barda)

> Not: Owner/Misafir aksiyonları (Edit + Menü: Yayınla/Arşivle/Paylaş/Sil veya Upvote + Menü: Fork/Kaydet/Paylaş/Şikayet Et) burada **değil**, sabit üst barda yaşıyor (yukarıya bakınız) — böylece Detaylar %0'a inse bile aksiyonlar erişilebilir kalır.

### Detaylar Bölümünün İçerik Adaptasyonu (Küçülürken)

Detaylar küçüldükçe (Handle 1 yukarı doğru sürüklenince) içerik **kademeli olarak** kayboluyor, hepsi bir anda değil:

1. **İlk kaybolan:** ❤️ Beğeni / 🔀 Fork sayıları, ülke, kişi sayısı, tarih aralığı, status badge — Detaylar küçüldükçe bunlar **yavaş yavaş fade-out** olur
2. **En son kalan (en dayanıklı):** **Başlık** — Detaylar çok küçülse bile başlık en son kaybolan eleman
3. **%0'da:** Detaylar tamamen kapanır, başlık dahil hiçbir şey görünmez

### Map Bölümü

- Küçüldükçe (Handle 1 veya Handle 2 ile) görünen harita alanı basitçe küçülür, içerik adaptasyonu yok
- %0'a inince tamamen kapanır

### Map — 2 Mod (Önceki Tasarımdan Aynen Korunuyor)

Map bölümü, eski tasarımdaki (`OMNIFLOW_PAGE_ARCHITECTURE.md`'den önce silinen) mantığı **aynen koruyor**:

**Normal mod (varsayılan, resizable pane içinde):**
- **Sağ üst:** `[Kuş Bakışı | Yol]` toggle
  - `Kuş Bakışı`: pinler arası kesikli düz çizgi (crow-fly) — **ORS'a bağımlı değil, her zaman çalışır, varsayılan/güvenli mod**
  - `Yol`: OpenRouteService (ORS) polyline — gerçek yol üzerinden renkli çizgi, **best-effort**. **Mobil ORS'u doğrudan çağırmaz** — API key güvenliği için backend proxy'sinden geçer (`GET /api/v1/Trips/{id}/route`, bkz. `BACKEND_ROADMAP_V2.md → B0.15`); backend proxy hata döndürürse aynı fallback kuralı (sessizce Kuş Bakışı'na dön) uygulanır
- **Sağ alt:** `⛶` büyüt ikonu → Tam Ekran Harita Modu'nu açar

**M3 Kapsam Sınırı (mobil roadmap ile senkron, bkz. `MOBILE_ROADMAP.md → Task 3.2`):**
- MapLibre kurulumu, pinler, Kuş Bakışı, Yol (ORS best-effort), Tam Ekran modu — hepsi **M3'te** var
- **Konum izni / canlı GPS pin'i M3'te yok** — bu statik bir harita (planlanan rotayı gösterir), kullanıcının gerçek anlık konumunu göstermez. Canlı konum **M8 (Live Trip Mode)**'a ait
- **ORS Fallback Kuralı:** ORS isteği başarısız olur/timeout olursa kullanıcıya hata gösterilmez — **sessizce Kuş Bakışı moduna dönülür**, `Yol` toggle'ı devre dışı bırakılır
- Offline/tile yüklenemezse: boş/gri harita + "Harita yüklenemedi" placeholder yeterli, ayrı bir offline cache mekanizması M3 kapsamında değil

> ⛔ **Bağımlılık: `BACKEND_ROADMAP_V2.md → B0.12`** (Destinasyon koordinatı/geocoding) — Pinlerin çizilebilmesi için `TripDestination`'ın koordinata sahip olması gerekiyor; şu an backend bunu döndürmüyor. **Geocoding backend'de, tek seferlik yapılır** (mobil client-side geocode etmez). Bir destinasyonun geocode'u başarısız olmuşsa (koordinat `null`) o destinasyon için pin çizilmez, diğer pinler etkilenmez.
>
> **Koordinatı `null` olan destinasyon — rota çizim kuralı:** Mobil harita motoru, koordinatı `null` olan destinasyonu **hem pin hem rota çiziminde sessizce atlar**. Kuş Bakışı çizgisi ve ORS polyline'ı, sıradaki koordinatı `null` olan destinasyonu göz ardı ederek **koordinatı geçerli olan bir sonraki ardışık destinasyona** doğrudan bağlanır (ör. A→B→C sırasında B'nin koordinatı yoksa, çizgi A'dan direkt C'ye çizilir — kesik/boş bir segment bırakılmaz). Bu, "eksik rota parçası" görünümünden daha temiz bir sonuç verir.

**Tam Ekran Harita Modu:**
- Harita %100 ekran (Detaylar/Timeline pane'leri, bottom nav gizli)
- Floating `← Geri` + `[Kuş Bakışı | Yol]` toggle (floating pill'ler)
- **Floating Draggable Timeline Card** (varsayılan sol alt köşe): Timeline içeriği bu kart içinde overlay olarak durur; kart harita üzerinde sürüklenebilir, harita da kartın altında dahil her yerde parmakla pan edilebilir
- Kart üstünde `✕ Gizle` → kart kaybolur, sol altta küçük `☰` pill kalır (geri açmak için)

---

## Timeline Bölümü

Timeline **2 moda** ayrılır, üstte bir toggle ile seçilir: **`Review | Gün gün`**

### Review Modu (Varsayılan)

**Amaç:** Tüm gezinin genel durumunu hızlıca görmek — Wizard'daki Review ekranına benzer bir "ne kadar hazır" özeti. **Gün bazlı filtreleme yok** — bu mod tüm gezi genelini gösterir.

**Yerleşim (yukarıdan aşağıya):**

```
Map
─────────────────────────────────
💰 Toplam Bütçe        $2.400/$3.000  ›
████████████░░░░░░░░░░  %80
─────────────────────────────────
        [Review | Gün gün]
─────────────────────────────────
○ Flights  ✈️
  ☐ İstanbul → Roma
  ☐ Roma → Barcelona
○ Hotels  🏨
  ☐ Barcelona Gece 1 - 15 Tem
  ☐ Barcelona Gece 2 - 16 Tem
○ Mekan  🍽️/📸
```

> **Karar değişikliği (dar ekran riski):** Toplam Bütçe satırı + `[Review | Gün gün]` toggle aynı satırda **taşma riski** taşıyordu (portrait ~360dp ekranlarda ikisi yan yana sığmayabilir). Bu yüzden **alt alta, ayrı satırlara** ayrıldı — Toplam Bütçe tam genişlik kendi satırında, toggle hemen altında kendi satırında.

#### 1. Toplam Bütçe Satırı

- En üstte, Map'in hemen altında, **tam genişlik kendi satırında**
- Format: `💰 Toplam Bütçe   {harcanan} / {toplam bütçe}   ›` + altında progress bar + yüzde
- Sonundaki `›` ok'a dokununca **Budget Summary sayfasına** (Task 3.15) gider
- Burada **detaylı kırılım gösterilmez** — sadece özet/teaser; detay ayrı sayfada
- **Herkese açık (Owner/Misafir farkı yok, anonim dahil):** Bu satır ve arkasındaki Budget Summary sayfası **Owner, giriş yapmış Misafir ve giriş yapmamış (anonim) kullanıcı için aynı şekilde görünür** — bütçe/harcama bilgisi gizli tutulmuyor, gezinin ne kadara mal olacağı herkesin görebileceği bir bilgi olarak tasarlandı. Backend'de `[AllowAnonymous]` gerektirir (bkz. `BACKEND_ROADMAP_V2.md → B0.10`). Draft/Archived trip'lerde bu satır zaten hiç render edilmez (o trip'ler owner olmayan kimseye görünmez).

#### 2. Review/Gün gün Toggle

- Toplam Bütçe satırının **hemen altında, bağımsız bir satırda** (kategori kartlarının/gün sekmelerinin başladığı yerin hemen üstünde)

#### 3. Kategori Kartları (Flights / Hotels / Mekan)

Her kategori bir kart: **daire progress ring** + kategori adı + genişleyebilir checklist.

**Progress Ring:**
- Ring, `seçilen/işaretlenen ÷ beklenen` oranına göre dolar
- Ring'in ortasında **her 2-3 saniyede bir coin-flip (3D dönüş) animasyonuyla** şu ikisi arasında geçiş olur:
  - Kategori ikonu (✈️ Flights, 🏨 Hotels, 🍽️/📸 Mekan)
  - Yüzde değeri (`%67` gibi)

> **Performans notu:** Sürekli çalışan `rotationY` animasyonu, kart ekrandan çıkmışken (scroll ile) veya kart kapalıyken de çalışmaya devam ederse `LazyColumn` scroll'unda frame kaybı (jank) yaratabilir. Bu yüzden animasyon **sadece kart expand durumdayken ve ekranda görünürken** aktif olmalı — `LaunchedEffect` ile bu görünürlük/expand durumuna bağlı başlatılıp durdurulacak; kart kapanınca veya ekrandan çıkınca animasyon duraklatılır (kaynak israfı ve jank önlenir).

- **Renk kuralı:**
  - `< %100` → mavi
  - `= %100` → yeşil
  - `> %100` → kırmızı (özellikle Mekan kategorisinde anlamlı — kullanıcı önerilenden fazla yer seçmiş olabilir)

**Beklenen Sayı Hesaplama Mantığı:**

| Kategori | Beklenen Sayı Mantığı |
|---|---|
| **Flights** | Rijit bir formül yok. Her şehirden şehire geçiş (ör. İstanbul→Roma, Roma→Barcelona) için **bir checklist satırı** oluşturulur. Kullanıcı o bacağı nasıl hallettiyse (uçak/tren/otobüs fark etmez) checklist'i kendi işaretler. |
| **Hotels** | **Gece bazlı.** Her destinasyonun kalış süresi kadar gece satırı oluşturulur (ör. "Barcelona Gece 1 - 15 Tem", "Barcelona Gece 2 - 16 Tem"). Beklenen sayı = toplam gece sayısı. |
| **Mekan (Food/Activities)** | Wizard'daki **Tempo** cevabından hesaplanır: Slow≈3, Moderate≈5, Fast≈7 aktivite/gün × gün sayısı. Kullanıcı bunu aşarsa ring kırmızıya döner (bütçe aşımıyla da ilişkilendirilebilir). |

**Mekan Checklist Yapısı (Flights/Hotels'ten farklı — hiyerarşik):**

Flights/Hotels'te satırlar net isimlendirilebilir (leg, gece). Mekan'da ise satırlar **alt kategoriye göre gruplanmış** gerçek eklenmiş yer/aktivitelerdir:

```
○ Mekan  🍽️/📸
  ☐ Food
    ☐ Restoran adı 1
    ☐ Restoran adı 2
  ☐ Museum
    ☐ Müze adı 1
    ☐ Müze adı 2
  ☐ ...
```

- Her alt grup (Food, Museum, Park vb. — `PlaceCategory` enum'undan gelir) kendi altında, o kategoriden **eklenmiş her Timeline entry** için bir checklist satırı gösterir
- **Önemli fark:** Mekan'ın checkbox'ları **salt-okunur ve otomatik** — manuel işaretlenmez. "İşaretli" durumu, o entry'nin Timeline'a **zaten eklenmiş olmasından** gelir (backend'de ayrı bir confirmation kaydı yok, bkz. `BACKEND_ROADMAP_V2.md → B0.9`). Flights/Hotels'teki checkbox'lar ise gerçekten **manuel, kullanıcı tarafından toggle edilen** durumlardır (B0.9'un kapsadığı asıl kısım budur)

> **Karar değişikliği (nested scroll çakışması riski):** İlk halde "kendi içinde scroll edilebilir" denmişti, ama bu **Timeline'ın dış scroll'uyla iç içe (nested) çakışma** riski taşıyor — kullanıcının parmağı Mekan kutusunun üzerindeyken hangi scroll'un tetikleneceği belirsizleşir, sinir bozucu bir deneyim yaratır. Bunun yerine: **Mekan kutusu kendi scroll'una sahip değil**, tamamen **wrap-content** olarak sınırsız büyür (kaç entry varsa o kadar satır), taşan kısım **dış Timeline scroll'una dahil olur**. Tek scroll kaynağı — karışıklık yok. Mekan listesi pratikte sınırlı olduğu için (Tempo bazlı, en fazla ~7×gün sayısı) bu sorun yaratmaz.

**Kart Expand Davranışı:**
- **Tetikleyici 1 — manuel:** Ring'in yanında ayrı, küçük bir **checklist ikonu** (liste/checklist simgesi) — kartın geri kalanına (ring'e veya başlığa) dokunmanın etkisi yok, sadece bu ikon expand/collapse tetikler
- **Tetikleyici 2 — otomatik (Timeline pane büyüdükçe):** Kullanıcı Handle 2'yi çekip **Timeline pane'ini büyüttükçe**, kategori kartları **kendiliğinden, kademeli olarak** checklist'lerini açar — boşluk kalmasın diye. **Üç kart da aynı anda büyür** (sırayla değil, hepsi birlikte). Bu, manuel ikon tetikleyicisinin **yerine geçmez, onunla birlikte çalışır** — pane küçükken de kullanıcı ikona basıp manuel açabilir/kapatabilir
- **Birden fazla kart aynı anda açık kalabilir** — accordion değil, Flight/Hotel/Mekan'ın hepsi istenirse aynı anda expanded durumda durabilir
- Kart genişleyince checklist satırları görünür
- Yükseklik **sabit değil, içeriğe göre tamamen otomatik (wrap-content), sınırsız** — az satırlı kategori boşluk bırakmaz, çok satırlı kategori (Mekan gibi) da ne kadar uzarsa o kadar yer kaplar
- **Kartın kendi scroll'u yoktur** — büyüyen içerik her zaman dış **Timeline scroll'una** dahil olur, nested scroll conflict önlenir
- Ekstra bir "özet satırı" gerekmiyor — progress ring zaten durumu gösteriyor

**Checklist Satırı Davranışı (Flights / Hotels — manuel toggle edilen kategoriler):**
- Her satır: `☐ İstanbul → Roma` gibi bir checkbox + metin (+ sağda ok)
- **Checkbox'a dokunma** → metnin üstü çizilir (strikethrough) + progress ring dolar. **Reversible**: tekrar dokununca üstü çizili yazı geri gelir, ring azalır. Backend: `PUT /api/v1/Trips/{id}/checklist/{itemKey}`, **optimistic update + hata durumunda geri alma** (app-wide konvansiyon)

**`itemKey` Formatı (deterministik, `TripDestination.Id` GUID bazlı — isim/tarih bazlı değil):**
- Flight leg: `flight-leg:{fromDestinationId}:{toDestinationId}`
- Hotel night: `hotel-night:{destinationId}:{nightNumber}` — `nightNumber` **göreli/sıralı** (1, 2, 3... o destinasyonun kaçıncı gecesi), **takvim tarihi değil**

> **Neden tarih değil, sıra numarası?** Eğer kullanıcı bir destinasyonun tarihlerini kaydırırsa (ör. tüm kalışı 2 gün ileri alırsa, süre aynı kalarak), "1. gecenin oteli hallettim" onayı **hâlâ geçerli olmalı** — sadece takvim tarihi değişti, kullanıcının niyeti değişmedi. Eğer key'i takvim tarihine (`2025-07-15` gibi) bağlarsak, her tarih kaydırmasında **gereksiz yere tüm confirmation'lar geçersiz sayılır**. Sıra numarası bazlı key bu durumda daha stabildir — sadece **gece sayısı gerçekten azalırsa** (ör. kalış kısalırsa) o zaman fazla `nightNumber`'lar geçersiz olur (bkz. Reconciliation kuralı, `BACKEND_ROADMAP_V2.md → B0.9`).
>
> Tam format + reconciliation kuralları (destinasyon silinme/tarih değişimi davranışı) için tek kaynak: `BACKEND_ROADMAP_V2.md → Task B0.9`.

**API Contract:**
- `GET /api/v1/Trips/{id}/checklist` → `{ "items": [ { "itemKey": "flight-leg:{guid}:{guid}", "isConfirmed": true, "confirmedAt": "..." }, ... ] }` — sadece güncel geçerli item'lar döner, ring hesaplaması (`seçilen/beklenen`) **client-side** yapılır (backend beklenen sayıyı hesaplamaz)
- `PUT /api/v1/Trips/{id}/checklist/{itemKey}` — body: `{ "isConfirmed": true }`, response: `204 No Content`
- **Visibility (B0.10 ile aynı desen):** GET, Published trip'lerde **anonim dahil herkese açık** (`[AllowAnonymous]`); Draft/Archived'de sadece owner, aksi halde 404. PUT ise **giriş + owner** gerektirir — misafir/anonim checklist'i görebilir ama işaretleyemez (Owner/Misafir Farkı bölümüyle tutarlı)
- **Önemli netlik: Checklist toggle Draft-only DEĞİL.** Timeline entry mutasyonlarının (Edit/Kilidi Aç/Sil/+Detay Ekle) aksine, `ToggleChecklistItemCommand`'da **trip durumu kontrolü yok** — sadece owner kontrolü var. Yani owner, trip **Published** durumdayken de (Draft'a almadan) checklist'i işaretleyebilir — bu mantıklı, çünkü "hallettim" beyanı gezi sırasında/sonrasında da yapılabilecek bir aksiyon, trip'in düzenlenebilir olmasına bağlı değil
- **URL encoding:** `itemKey`'deki `:` karakterleri **mobil tarafça açıkça percent-encode edilir** (`:` → `%3A`) URL oluştururken — Retrofit/OkHttp'nin path segment encoding tutarlılığı için. ASP.NET Core route binding path parametrelerini otomatik url-decode ettiği için backend'de ekstra iş gerekmez

- **Metne veya oka dokunma** → **Detay modalı** açılır: arka plan **blur** olur, o spesifik legin/gecenin detay kartı üstte gösterilir
- *(Mekan bu davranışa dahil değil — otomatik/salt-okunur, yukarıdaki nota bakınız)*

**Owner/Misafir Farkı:**
- **Owner:** Flights/Hotels checklist'i tam etkileşimli — checkbox işaretlenebilir/geri alınabilir
- **Misafir:** Flights/Hotels checklist'i de **salt-okunur** — checkbox'lar ve progress ring görünür (owner'ın ne kadar hazırlandığını gösterir) ama **tıklanamaz/işaretlenemez**. Bu, owner'ın kişisel planlama verisi olduğu için misafirin değiştirmesine izin verilmez. Metne/oka dokunma (Detay modalı açma) misafir için de çalışır — sadece checkbox toggle'ı kısıtlı. (Mekan zaten her iki rolde de salt-okunur.)

### Gün gün Modu

**Amaç:** Seçili günün kronolojik, saat sıralı akışını göstermek.

**Yerleşim (yukarıdan aşağıya):**

```
Map
─────────────────────────────────
💰 Toplam Bütçe        $2.400/$3.000  ›
████████████░░░░░░░░░░  %80
─────────────────────────────────
        [Review | Gün gün]
─────────────────────────────────
[Gün 1] [Gün 2] [Gün 3] ...
─────────────────────────────────
[14:30]  ┊
         ┊  ✈️  Rixos Hotel
[16:00]  ┊
         ┊  🏨  Hotel Check-in
```

- **Toplam Bütçe satırı + Review/Gün gün toggle** Review moduyla aynı konumda (alt alta, ayrı satırlarda), **sabit kalır** (mod değişse de yer değiştirmez)
- Altında **Gün sekmeleri** — yatay, scroll edilebilir (Gün1/Gün2/Gün3...), Wizard'daki chip pattern'iyle tutarlı. Varsayılan: Gün 1 seçili
- **Not:** Dikey sidebar (Day1/Day2/Day3 sağda dikey liste) fikri değerlendirildi ve **reddedildi** — telefon ekranında genişlik kaybına yol açması ve Timeline'ın kendi dikey scroll'uyla çakışması (iki dikey scroll aynı ekranda kafa karıştırır) nedeniyle yatay sekmeler tercih edildi.

**Entry Satırı Formatı:**
- Her entry **tek satır**: `[Saat solda]` — kesikli dikey bağlantı çizgisi — `[emoji ikon daire] [entry adı]`
- Entry adı **tek satır, sade** (ör. "Rixos Hotel") — alt başlık/açıklama satırda gösterilmez, tüm ekstra bilgi Detay modalına taşınır
- İkon sistemi Review moduyla aynı kategori emojileri kullanır (✈️ Uçuş, 🏨 Konaklama, 🍽️ Yemek, 🚗 Transport, 🏛️ Müze, 🛍️ Alışveriş, 🌿 Doğa)
- Entry'ye (satıra) dokununca **Review moduyla aynı blur Detay modalı** açılır (tutarlılık)

> **Saat gösterimi — zaman dilimi kuralı:** Seyahat sektörü standardı: saatler her zaman **entry'nin ait olduğu destinasyonun yerel saatine göre** gösterilir, kullanıcının cihaz saatine/konumuna göre **değil** (ör. Barcelona otel check-in'i "14:00" ise, kullanıcının cihazı İstanbul'da veya farklı bir saat diliminde olsa bile "14:00" görünür — dönüştürülmez). Bu kural, backend'in `TripDestination.Timezone` (IANA) alanını ekleyeceği `BACKEND_ROADMAP_V2.md → Task B2.4` (M8) ile tam desteklenecek. **M3'te durum:** B2.4 henüz gelmediği için backend zaman alanlarını UTC+dönüşüm olarak saklamıyor/dönüştürmüyor — girilen saat ne ise öyle gösteriliyor, yani fiilen zaten "dönüşümsüz, girildiği gibi" davranıyor. Cihaz saatine göre yanlış dönüşüm riski **M3'te yok** (dönüşüm hiç yapılmadığı için); B2.4 gelince bu kural (destinasyon yerel saati) referans alınarak doğru dönüşüm eklenecek.

> ~~**Scroll Davranışı**~~ — **artık geçerli değil.** Sayfa scroll ile değil, "Genel Sayfa Yapısı" bölümündeki **2 handle** ile bölüm boyutları ayarlanıyor. Detaylar'ın küçülmesi/kaybolması artık scroll'a değil, Handle 1'in sürüklenmesine bağlı.

---

## Detay Modal — İçerik

> **Karar (revize edildi):** Eski implementasyon (`EntryDetailModal`) **gerçek bir `TimelineEntry` kaydı olduğu varsayımıyla** yazılmıştı. Ama Review modundaki Flights/Hotels checklist satırları (ör. "İstanbul → Roma") **her zaman bir TimelineEntry'ye bağlı olmayabilir** — kullanıcı checkbox'ı sadece manuel işaretlemiş olabilir (bkz. B0.9, `TripChecklistConfirmation`), gerçek bir uçuş/ulaşım kaydı hiç eklenmemiş olabilir. Bu yüzden modal artık **iki farklı duruma göre dallanıyor**.

### Durum A: Bağlı bir TimelineEntry var

Geçerli olduğu yerler: **Gün gün modu** (her zaman, çünkü oradaki tüm satırlar zaten gerçek entry'ler), **Mekan** checklist satırları (her zaman, çünkü Mekan satırları zaten var olan Place-tipi entry'lerdir), ve Flights/Hotels checklist satırları **eğer o bacağa/geceye karşılık gelen bir `CustomFlight`/`CustomTransport`/`CustomAccommodation` entry'si eklenmişse**.

- **Karar değişikliği (görsel):** İçerik artık düz label-value satırları değil, **entry tipine özel kart tasarımı** kullanıyor ("boarding pass" / bilet tarzı, markalı görünüm):

  **Flight kartı (uçuş biletine benzer tasarım):**
  - Üst satır: Havayolu adı (`Airline`) sol + uçuş no + tarih sağ (ör. "THY 1234 · 15 Tem 2025")
  - Büyük rota satırı: `{FlightFromAirport kodu}` — ✈️ ikonu (aralarında kesikli çizgi) — `{FlightToAirport kodu}` (ör. "IST ✈ FCO")
  - Altında: tam şehir adları (`FlightFromCity` / `FlightToCity`) + kalkış/varış saatleri (`FlightDepartureAt` / `FlightArrivalAt`)
  - **Kesikli/perforeli bilet ayırıcı çizgi** (görsel: bilet yırtılma hattı gibi)
  - Alt satır: süre (varış-kalkış farkından hesaplanır) + fiyat (`Price` + `CurrencyCode`)

  **Hotel kartı (rezervasyon kartı tarzı, aynı görsel dil):**
  - Üst: 🏨 ikonu + otel adı (`CustomName`) belirgin başlık
  - Orta: Check-in tarihi/saati — Check-out tarihi/saati (Flight kartındaki "iki nokta" düzenine benzer, şehir yerine tarih)
  - Adres satırı (`AccommodationAddress`)
  - Kesikli ayırıcı çizgi
  - Alt satır: gece sayısı (hesaplanır) + fiyat

  **Place / CustomEvent / CustomTransport (sade kalır):** Bu tipler için özel kart tasarımı yok — ikon + başlık + temel bilgi satırları (eski, sade label-value formatı) yeterli, çünkü bu tipler daha heterojen/basit içerik taşıyor

  Ortak modal davranışları (tip fark etmez):
  - Arka plan: koyu **scrim overlay** + hafif saydam kart (gerçek backdrop-blur olmadığı için)
  - Modal üstünde: entry'nin günü + saati
  - **Yana kaydırarak (`HorizontalPager`)** aynı gündeki/kategorideki diğer entry'lere geçiş
  - **`✏️ Edit`** — sadece owner, Task 3.13 düzenleme formuna (bu spesifik entry'yi düzenlemeye) götürür
  - **`🔀 Fork`** — sadece owner olmayana, Edit ile asla aynı anda görünmez
  - **🔴 Kritik — tüm entry mutasyonları Draft-only:** `UpdateTimelineEntryCommandHandler`/`DeleteTimelineEntryCommandHandler` (+ Create/Reorder) trip `Draft` değilse **komple reddediyor**. Yani `✏️ Edit`, `🔓 Kilidi Aç` ve `🗑️ Sil` **sadece trip Draft durumundayken aktif**; Published/Archived'da bu üçü **disabled/gri** gösterilir. Owner düzenlemek isterse önce üst bardan `Düzenlemek için Taslağa Al` (bkz. `BACKEND_ROADMAP_V2.md → B0.14`) ile trip'i Draft'a alması gerekir
  - **Locked entry davranışı — Edit (netleşti):** Backend'de (`UpdateTimelineEntryCommandHandler`) bu davranış zaten tam tasarlanmış, yeni backend işi gerekmez:
    - `CustomFlight`/`CustomTransport`/`CustomAccommodation`/`CustomEvent` tipi entry'ler **varsayılan olarak locked** (gerçek dünyada sabit saati olan kayıtların yanlışlıkla sürükle-bırak/saat değişimiyle bozulmasını önler); `Place` tipi hiç kilitlenmez
    - Edit formu açılınca, entry `IsLocked` ise **type-specific alanlar (saat, istasyon, uçuş no, adres vb.) read-only** gösterilir, üstünde `🔓 Kilidi Aç` butonu durur
    - **`🔓 Kilidi Aç` butonu bağımsız/anlık bir aksiyondur** — basılır basılmaz **hemen** `PUT /api/v1/trips/{tripId}/timeline/entry/{entryId}` isteği `isLocked: false` ile (başka alan değişmeden) gönderilir ve kalıcı olarak unlock edilir; forma kaydetmeyi beklemez. Unlock sonrası hem type-specific alanlar düzenlenebilir hale gelir **hem de** aşağıdaki Delete kısıtlaması kalkar
    - **Price ve Notes alanları kilit durumundan bağımsız her zaman düzenlenebilir** — backend `UpdateCommonFields` zaten lock kontrolü yapmıyor
    - Entry tekrar kilitlenmek istenirse (opsiyonel, ileri seviye) `isLocked: true` ile submit edilebilir (backend `lockAfterUpdate` destekliyor) — M3'te bu geri-kilitleme UI'ı zorunlu değil, backend'de zaten mevcut
  - **Locked entry davranışı — Delete (ek bulgu):** `DELETE /api/v1/trips/{tripId}/timeline/entry/{entryId}` endpoint'inin **`isLocked` parametresi yok** — Update'in aksine, aynı istekte "unlock + sil" yapılamaz. `DeleteTimelineEntryCommandHandler`, entry `IsLocked` ise **`403 ForbiddenException`** ile tamamen reddediyor. Bu yüzden UI'da: entry locked'ken **`🗑️ Sil` butonu gizli/disabled** kalır, yukarıdaki `🔓 Kilidi Aç` ile önce unlock edilmesi gerekir — unlock sonrası `Sil` görünür/aktif hale gelir. İki ayrı adım, tek adımda birleştirilemez (backend kısıtı)

### Durum B: Bağlı bir TimelineEntry yok (sadece manuel checkbox işaretlenmiş)

Sadece **Flights/Hotels checklist satırlarında** mümkün — kullanıcı o bacağı/geceyi kendi imkânıyla halletmiş ama uygulamaya hiç kayıt girmemiş.

- Modal daha sade bir **boş durum** gösterir: "Bu bacak/gece için henüz bir kayıt eklenmedi" mesajı + o legin/gecenin temel bilgisi (ör. "İstanbul → Roma" veya "Barcelona, Gece 1 - 15 Tem")
- **`+ Detay Ekle`** butonu — owner'a görünür, **sadece trip Draft durumundayken aktif** (aynı Draft-only kısıtı, `CreateTimelineEntryCommandHandler` da Draft-only). Trip Published/Archived ise buton disabled/gri, tıklanınca "Düzenlemek için önce Taslağa Al" hatırlatması gösterilebilir.
  - **Navigasyon kararı: ayrı bir sayfaya gider, modal inline'a dönüşmez.** Tıklanınca modal **kapanır**, Task 3.13'ün Create Timeline Entry formu (**tamamen ayrı bir ekran/route**) **ilgili gün/tip önceden doldurulmuş** olarak açılır (ör. Flights legi ise `CustomFlight` tipi, o günün destinasyonu önceden seçili). Form kaydedilince Trip Detail'e geri dönülür, o checklist satırı artık **Durum A**'ya geçmiş olarak görünür.
  - **Gerekçe:** 5 farklı entry tipinin (Place/CustomFlight/CustomTransport/CustomAccommodation/CustomEvent) her biri farklı sayıda/tipte alan istiyor — bu karmaşık dinamik formu modal içine sıkıştırmak yerine, zaten var olan tam ekran formu kullanmak form mantığının iki yerde tekrarlanmasını önlüyor.
  - Oluşturulan entry'ye **`planningSlotKey`** olarak o checklist satırının `itemKey`'i geçirilir (bkz. aşağıdaki "Hangi durumda olduğunu belirleme mantığı")
- **Owner olmayana (misafir) — otomatik fork yok:** Bu durumda **Edit/Ekle butonu hiç gösterilmez** — sadece "henüz kayıt yok" mesajı ve varsa `🔀 Fork` butonu görünür. Misafir düzenlemek isterse **bilinçli olarak Fork'a basmalı**; bu, trip'in kendi hesabına (Draft olarak) kopyalanmasını sağlar — misafir sonra **kendi kopyasında**, artık owner olduğu için, normal şekilde `+ Detay Ekle` kullanabilir. Otomatik/örtük bir fork tetiklenmez, kullanıcının açık bir aksiyonu gerekir.
- Kayıt eklendikten sonra bir daha bu satıra dokununca **Durum A**'ya geçilir (artık gerçek entry gösterilir)
- **Entry silinirse checklist ne olur:** Checklist confirmation (checkbox işaretli/işaretsiz durumu) entry'den **tamamen bağımsız** — entry silinse bile checkbox **checked kalır** (kullanıcının "hallettim" beyanı entry'nin varlığına bağlı değil). Sonraki modal açılışı **Durum B**'ye döner (artık bağlı entry yok), ama checklist durumu değişmez

**Hangi durumda olduğunu belirleme mantığı (revize edildi — heuristic değil, exact match):** İlk halde bu belirleme şehir adı/tarih heuristiği ile client-side yapılacaktı, ama bu **aynı leg/gece için birden fazla entry varsa yanlış eşleşme riski** taşıyordu (ör. kullanıcı önce uçuş sonra taksi transferi eklemişse, hangisi "asıl" entry?). Bunun yerine `TimelineEntry`'ye **`PlanningSlotKey`** (string?, bkz. `BACKEND_ROADMAP_V2.md → B0.13`) eklendi — Detay Modal'ın "+ Detay Ekle" CTA'sından oluşturulan entry'ler bu alana checklist satırının `itemKey`'ini kaydeder. Durum A/B belirlemesi artık basit bir **exact match**: `entries.any { it.planningSlotKey == itemKey }`. Normal Timeline ekleme akışından (bu CTA'dan değil) oluşturulan entry'ler `planningSlotKey = null` kalır ve checklist'e otomatik bağlanmaz — checklist confirmation onlardan bağımsız, manuel kalmaya devam eder.

---

## Açık / Sıradaki Konular

- [x] ~~Gün gün modunun tam davranışı~~ — netleşti (yukarıda)
- [x] ~~Detaylar (cover) bölümünün "basit" hissi~~ — resizable pane sistemine geçilerek çözüldü (handle sürükleme + istatistik ikonlarına adaptasyon)
- [x] ~~Map bölümünün içerik detayları~~ — netleşti, eski Kuş Bakışı/Yol toggle + Tam Ekran modu aynen korunuyor
- [x] ~~Detay modalının tam içeriği~~ — netleşti: Durum A (gerçek TimelineEntry varsa) eski implementasyon aynen korunuyor; Durum B (Flights/Hotels'te bağlı entry yoksa) sade boş durum + "Detay Ekle" CTA'sı
- [x] ~~TimelineEntry↔checklist eşleştirmesi~~ — netleşti: heuristic yerine `TimelineEntry.PlanningSlotKey` (exact match), bkz. `BACKEND_ROADMAP_V2.md → B0.13`
- [x] ~~Detaylar bölümünün kısmen-açık hallerinde hangi elemanlar gösterilecek~~ — netleşti (yukarıda: beğeni/fork önce gider, başlık en son kalır)
- [x] ~~Handle default boyutları~~ — netleşti: 30/30/40
- [x] ~~Sabit üst bar ve Owner/Misafir aksiyonları~~ — netleşti (yukarıda: üst bar ayrı, resize edilmez)
- [x] ~~Detaylar'ın tam açık haldeki içerik listesi~~ — netleşti (yukarıda)
- [x] ~~Mekan checklist satırlarının kimliği~~ — netleşti: `PlaceCategory` bazlı gruplandırılmış, **kendi scroll'u yok**, wrap-content ile dış Timeline scroll'una dahil (bkz. nested scroll notu)
- [x] ~~Misafir Review modunda checklist'i işaretleyebilir mi~~ — **hayır, salt-okunur**. Checkbox'lar görünür ama misafir için tıklanamaz/toggle edilemez — checklist owner'ın kişisel planlama verisi
- [x] ~~Mod tercihi (Review/Gün gün) ve seçili gün sekmesi hatırlanıyor mu~~ — **evet**, trip-bazlı kalıcılık kapsamına dahil (bkz. "Bölüm Oranlarının Kalıcılığı")

- [x] ~~Checklist expand tetikleyicisi~~ — netleşti: ring'in yanında ayrı küçük bir checklist ikonu var, expand sadece o ikona dokununca tetiklenir (kartın geri kalanına dokunmanın etkisi yok)
- [x] ~~Timeline pane'i büyütünce checklist otomatik açılır mı~~ — netleşti: **evet**, Handle 2 ile Timeline pane büyüdükçe kategori kartları kendiliğinden, **üçü birden aynı anda** checklist'lerini açar (boşluk kalmasın diye); bu, manuel ikon tetikleyicisinin **yerine geçmez, onunla birlikte** çalışır — bkz. "Kart Expand Davranışı"

**Şu an açık madde kalmadı.** Sıradaki adım: bu dokümanı temel alıp Claude Design prompt'u yazmak. Backend tarafı için bkz. `BACKEND_ROADMAP_V2.md → Task B0.9` (checklist confirmation state).
