# Trip Details Page — Kapsamlı Kontrol Listesi (Checklist)

> Bu doküman, `TripDetailScreen` için tasarım spesifikasyonunu (`TRIP_DETAILS_PAGE.md`) ve mobil yol haritasını
> (`MOBILE_ROADMAP.md → Task 3.2 / 3.3`) temel alan **manuel QA / doğrulama** kontrol listesidir. `MOBILE_ROADMAP.md`
> "bu iş yapıldı mı" diye takip eder; bu dosya ise "gerçekten doğru çalışıyor mu" diye **elle test etmek** içindir —
> ikisi birbirinin yerine geçmez, birlikte kullanılır.
>
> İlgili dosyalar: `app/src/main/java/com/omniflow/ui/trips/TripDetailScreen.kt`, `TripDetailViewModel.kt`,
> `TripDetailUiState.kt`, `TripDetailMapper.kt`, `data/repository/TripRepository(Impl).kt`,
> `data/remote/TripService.kt`, `data/local/TripPanePreferences*.kt`.

---

## 1. Veri Orkestrasyonu & Hata Toleransı

- [x] **Bloklayıcı çekirdek veri:** `GET /api/v1/Trips/{id}` (`getTripDetail`) başarısız olursa tam ekran hata
      (`uiState.error != null` dalı) + jenerik mesaj gösterilir; sayfa geri kalanı hiç render edilmez.
- [x] **Jenerik hata metni (security by obscurity):** Hata her zaman **"Bu gezi bulunamadı"** — ham `ApiResult.Error`
      mesajı, HTTP kodu veya Draft/Archived ayrımı hiçbir zaman kullanıcıya sızdırılmaz (Task 3.3.3).
- [x] **Paralel ikincil yükleme:** `getTripDetail` başarılı olduktan sonra `loadSecondaryData()` içinde Timeline
      (`GET /api/v1/trips/{tripId}/timeline`), Budget Summary (`GET /api/v1/Trips/{id}/budget-summary`) ve Checklist
      (`GET /api/v1/Trips/{id}/checklist`) `async { }` ile eşzamanlı başlatılıp `awaitAll(...)` ile beklenir.
- [x] **Route casing tutarsızlığı:** Timeline'a özel path **küçük "t"** (`trips/...` — `TimelineController`'ın
      literal route override'ı), Trips'e özel olanlar (`budget-summary`, `checklist`, `getTripDetail`, publish/
      archive/... vb.) **büyük "T"** (`Trips/...` — `[controller]` token'ından geliyor).
- [x] **Sessiz mock fallback:** Backend'de bu 3 endpoint (Timeline/Budget/Checklist, B0.9 dahil) henüz yoktur — hepsi
      hata döner ama kullanıcıya **hiçbir hata/retry banner'ı gösterilmez**; mevcut mapper mock'u (kategori kartları,
      bütçe, checklist durumu) sessizce ekranı beslemeye devam eder (`getTripDetail`/`getMyTrips` ile aynı repository
      konvansiyonu).
- [x] **"Inline hata + Tekrar Dene" UI'ı bilerek yok:** Spec bunu istiyor ama mock-fallback kararı yüzünden şu an
      hiçbir zaman tetiklenemez (test edilemeyen ölü kod olurdu) — backend bağımsız-başarısız-olabildiğinde eklenecek.
- [x] **On-demand rota yükleme:** `GET /api/v1/Trips/{id}/route` sayfa açılışında **çağrılmaz** — sadece kullanıcı
      haritada "Yol" moduna geçtiğinde (`onMapModeChange(MapMode.ROAD)`) tetiklenir.
- [x] **ORS sessiz fallback:** Rota çağrısı hata dönerse (`routeUnavailable = true`) harita **sessizce** Kuş
      Bakışı'na döner, "Yol" toggle'ı gri/tekrar-denemez olur — ayrı bir hata state'i/banner'ı yoktur.

## 2. Sabit Üst Bar & Erişim Kontrolleri

- [x] **Simetrik düzen:** Sol `←` geri butonu; başlık ortada `maxLines=1` + ellipsis; sağda **her zaman tam olarak
      2 ikon** (Owner: `✏️ Edit` + `⋮ Menü`, Misafir: `❤️ Upvote` + `⋮ Menü`).
- [x] **Owner `⋮` menüsü — durum bazlı:**
  - [x] **Draft** → Yayınla · Sil
  - [x] **Published** → Arşivle · Düzenlemek için Taslağa Al · Paylaş · Sil
  - [x] **Archived** → Yayına Al · Sil (**Paylaş yok** — owner-only trip, paylaşılan link işe yaramaz)
- [x] **`✏️ Edit` Draft-only kapısı:** Sadece `tripStatusEnum == Draft` iken tıklanabilir; Published/Archived'da
      gri/disabled (ikon rengi `TextSecondary.copy(alpha=0.4f)`), tıklama no-op.
- [x] **"Taslağa Al" onay dialogu:** Tıklanınca `MoveToDraftConfirmDialog` açılır ("...yayından kaldıracaksın...
      Devam edilsin mi?"); onaylanınca `unpublish()` çağrılır, iptal ederse hiçbir API çağrısı yapılmaz.
- [x] **Sil onay kapısı:** Owner/Misafir fark etmeksizin `Sil`e her basışta `DeleteConfirmDialog` açılır; onay
      olmadan `DELETE` isteği asla atılmaz.
- [x] **Misafir `⋮` menüsü:** Fork · Kaydet (durum bağımlı metin: "Kaydet"/"Kaydı Kaldır") · Paylaş · Şikayet Et.
- [x] **Şikayet Et pasif:** `enabled = false`, tıklanamaz, tasarımı korunur (B5.1 gelene kadar).
- [x] **Paylaşım:** `Intent.ACTION_SEND` ile düz metin link (`https://omniflow.app/trips/{id}`), native chooser
      açılır — zengin önizleme/gerçek deep-link **yok** (B4.3/B4.5 bekleniyor, bilinen sınırlama).
- [x] **Anonim kullanıcı kapısı:** Giriş yapmamış kullanıcı Upvote/Fork/Kaydet'e basarsa (`AUTH_REQUIRED_ACTIONS`
      seti + `TokenStore.sessionState != SignedIn`) API isteği **hiç atılmadan** `LoginRequiredDialog` açılır;
      "Giriş Yap" `Routes.Login`'e yönlendirir (dönüşte otomatik resume yok, manuel geri gidilir).
- [x] **Reaktif upvote senkronizasyonu:** Üst bardaki Upvote ile Detaylar panelindeki salt-okunur `❤️` sayısı **aynı
      `StateFlow<TripDetailUiState>`**'ten beslendiği için ayrı bir senkron mekanizması olmadan anında tutarlı kalır.

## 3. Yeniden Boyutlandırılabilir Paneller — Portrait (Task 3.2.2)

- [x] **3 pane dikey istif:** Detaylar (üstte, %0–30, varsayılan %30) → Handle 1 → Map (ortada, %0–40, varsayılan
      %30) → Handle 2 → Timeline (altta, türetilen = `1 - detaylar - map`, varsayılan %40).
- [x] **%100 referansı:** Ekran yüksekliği − sabit üst bar (56dp) − bottom nav − sistem insets; `Modifier.weight()`
      kullanıldığı için manuel piksel hesabı gerekmez.
- [x] **Drag:** Handle'ı tutup sürükleyince komşu iki panelin oranı değişir (`applyHandle1Drag`/`applyHandle2Drag`).
- [x] **Tap-to-snap:** Handle'a tek dokununca ilgili sınır **anında** 30/30/40 üçlüsüne döner; zaten default'taysa
      hiçbir şey değişmez, haptic tetiklenmez (toggle değil, sadece "default'a dön").
- [x] **Cascade (çift yönlü):** Bir handle komşu panelin min/maks sınırına dayanınca durmaz — diğer handle da kayarak
      bir sonraki panelden alan çeker (ör. Detaylar büyürken Map %0'a dayanırsa Handle 2 de kayıp Timeline'dan alan
      çeker). `detaylar + map + timeline == 1f` invariant'ı hep korunur (timeline hiç saklanmaz, türetilir).
- [x] **48dp hit-area her koşulda erişilebilir:** Komşu panel %0'a inse bile handle'ın kendi satırı (`Column` sibling,
      weight'siz sabit yükseklik) her zaman tam boyutuyla dokunulabilir kalır.
- [x] **Harita tampon boşluğu:** Map pane'in kendi içinde üst/alt 24dp `Spacer` — handle'ın 48dp hit-area'sı ile
      MapLibre'nin gerçek render/gesture alanı arasında yapısal ayrım (native View/Compose gesture çakışması riski
      sıfırlanır).
- [x] **Crossfade:** Handle'a dokunulunca isim (ör. "Map") ↔ tutamaç çizgileri arası crossfade; parmak kalkınca
      **~1 saniye** gecikmeyle isme geri döner.
- [x] **Haptic:** Snap anında VE bir panel `>0`'dan `≤0`'a geçtiği anda (`LongPress` tipi, `LocalHapticFeedback`) —
      sürekli sürüklemede değil, sadece bu iki "sonuç anı"nda.
- [x] **Trip-bazlı kalıcılık:** Oranlar + `displayMode` + `selectedDayIndex` Room'da (`TripPanePreferencesEntity`,
      `tripId` primary key) saklanır; owner aksiyonu sonrası `loadTripDetail()` tekrar çalışsa bile bu alanlar
      **sıfırlanmaz** (bilinen bug fix — mapper'ın taze sonucu üzerine mevcut değerler korunarak yazılır).

## 4. Yatay Mod — Landscape (Task 3.2.8)

- [x] **Farklı eksen düzeni:** Sol sütun Timeline (**genişlik** ekseni, %0–60, varsayılan %40); sağ sütun Detaylar
      (**yükseklik** ekseni, %0–30, varsayılan %30) üstte + Map (türetilen kalan yükseklik) altta.
- [x] **Handle A (dikey çizgi, yatay sürükleme):** Timeline ↔ sağ sütun genişlik payını ayarlar.
- [x] **Handle B (yatay çizgi, dikey sürükleme):** Sağ sütun içinde Detaylar ↔ Map yükseklik payını ayarlar.
- [x] **Cascade YOK:** Handle A ve B tamamen bağımsız eksenlerde — biri diğerini asla etkilemez (portrait'teki 3'lü
      zincirden kasıtlı olarak farklı).
- [x] **Ayrı kalıcılık:** `landscapeTimelineFraction`/`landscapeDetaylarFraction` portrait alanlarından **bağımsız**
      Room kolonlarında saklanır — bir trip'i portrait'te açtığında portrait oranını, landscape'te açtığında
      landscape oranını hatırlar (aynı satırda, tek trip-bazlı kayıt içinde).
- [x] **Tam Ekran Harita Modu tutarlılığı:** Yatay modda da aynı `Dialog`+`MapLibreView` mantığı çalışır, ekran
      döndürme sırasında crash olmaz.

## 5. Panel İçerikleri

### 5.1 Detaylar Paneli
- [x] Kapak fotoğrafı: `coverPhotoUrl` doluysa `AsyncImage` (Coil), null ise sıcak gradyan fallback — ikisinde de
      aynı okunabilirlik scrim'i (üstten koyu → şeffafa gradyan).
- [x] İçerik: status badge (**3 renk** — Draft=amber/Warning, Published=yeşil/Success, Archived=gri/TextSecondary),
      tarih aralığı, başlık, ülke+kişi sayısı, `❤️`/`🔀` sayıları (salt-okunur).
- [x] **Kademeli kaybolma:** Panel küçülürken tek eşik (`DETAYLAR_COLLAPSE_THRESHOLD`) altına inince badge/tarih/
      ülke/kişi/❤️/🔀 **hep birlikte** kaybolur; başlık pane tamamen (~%0) kapanana kadar en son kalan elemandır.

### 5.2 Map Paneli
- [x] MapLibre native SDK (`AndroidView` sarmalı) + OpenFreeMap `liberty` tile stili.
- [x] Pinler mock koordinat tablosundan (`MockCityCoordinates`) — bilinmeyen şehir → pin/rota çiziminden atlanır,
      bir sonraki geçerli pine direkt bağlanır (kesik segment bırakılmaz).
- [x] `[Kuş Bakışı | Yol]` toggle (sağ üst) + `⛶` tam ekran butonu (sağ alt).
- [x] Tam Ekran Harita Modu: floating `← Geri` + mode toggle, ayrı `MapView` instance'ı (kamera pozisyonu normal↔
      fullscreen arası **korunmaz** — bilinen basitleştirme).

### 5.3 Timeline Paneli — Review Modu
- [x] Toplam Bütçe satırı (tam genişlik, mock veri — `tripId` hash bazlı, herkese açık/anonim dahil) + progress bar.
- [x] Review/Gün gün toggle, Toplam Bütçe'nin hemen altında ayrı satırda.
- [x] Kategori kartları (Flights/Hotels/Mekan) — progress ring, **sadece kart expanded'ken** coin-flip (ikon↔yüzde)
      animasyonu aktif (gerçek viewport-görünürlük tespiti kapsam dışı, `LazyColumn` gerektirirdi).
- [x] Ring rengi: `<%100` mavi, `=%100` yeşil, `>%100` kırmızı.
- [x] Checklist: Flights/Hotels satırları `Checkbox` ile Owner'a tıklanabilir (Misafir'de `enabled=false`); Mekan
      satırları PlaceCategory alt-gruplu (Food/Museum/Shopping/Nature) ve **her zaman salt-okunur**.
- [x] Checklist toggle **kalıcı-lokal, revert yok:** `PUT /checklist/{itemKey}` backend'de yok (B0.9), hata sessizce
      yutulur, kullanıcı gördüğü işaretli/işaretsiz durumu oturum boyunca korur.
- [x] Kart expand: wrap-content, kendi scroll'u yok (Timeline'ın dış scroll'una dahil, nested-scroll conflict yok).

### 5.4 Timeline Paneli — Gün gün Modu
- [x] Gün sekmeleri **yatay scroll** (`horizontalScroll`), kısa "Gün N" metni (uzun tarih değil).
- [x] Seçili günün entry'leri: saat (sol, sabit genişlik) — düz bağlantı çizgisi — ikon dairesi (28dp, `IconContainer`
      arka plan) — entry adı; son entry'de bağlantı çizgisi yok.
- [x] Saat gösterimi **dönüşümsüz** (destinasyon yerel saati varsayımıyla, B2.4 gelene kadar girildiği gibi).
- [x] Boş gün: "Bu gün için henüz bir kayıt yok" mesajı, crash yok.

### 5.5 Detay Modal (Task 3.2.7)
- [x] `Dialog(usePlatformDefaultWidth=false)` + aynı gün+kategorideki sibling entry'ler arasında `HorizontalPager`
      ile swipe.
- [x] **Durum A — Flight:** boarding-pass tarzı kart (rota büyük satır, kesikli ayırıcı, süre/fiyat).
- [x] **Durum A — Hotel:** giriş/gece sayısı/fiyat kartı (aynı görsel dil, otel-özel alanlar).
- [x] **Durum A — Mekan:** sade kart (ikon + başlık + bilgi satırı, Edit/Fork yok).
- [x] **Durum B (bağlı kayıt yok):** boş durum mesajı + Owner&Draft'a `+ Detay Ekle` (no-op TODO, sayfa henüz yok);
      Misafir'e sadece mesaj + (varsa) `🔀 Fork`.
- [x] **Kilitli entry:** alanlar salt-okunur + `🔓 Kilidi Aç` (anlık, revert-yok PUT); Sil gizli/disabled; unlock
      sonrası (Owner+Draft) Edit/Sil aktifleşir.
- [x] **Sil aksiyonu:** `hasLinkedEntry=false` yapar (Durum A→B geçişi simüle edilir), aynı satırın `isConfirmed`
      (checklist) durumu **değişmeden kalır** (spec: entry silinmesi checklist confirmation'dan bağımsız), modal
      kapanır.
- [x] Owner+Draft dışında (Published/Archived) Edit/Kilidi Aç/Sil hepsi disabled.

## 6. Koleksiyon Seçici (Kaydet Bottom Sheet — Task 3.3.2)

- [x] **Optimistic toggle:** `isSaved` anında UI'da değişir, API çağrısı arka planda; hata dönerse (`ApiResult.Error`)
      eski değerine **geri alınır** (revert-on-error — checklist'in aksine, bu akış gerçek bir save/unsave
      endpoint'i olduğu için üçüncü elden bu davranış korunur).
- [x] **İlk kayıtta bottom sheet:** `isSaved=false` iken Kaydet'e basınca `ModalBottomSheet` açılır — `"Tümü"` hariç
      `MyTripsUiState.defaultCollections` (Avrupa, Yaz 2026, ...) listelenir (My Trips ile aynı mock, B4.1 gelince
      ikisi birden gerçek endpoint'e geçer).
- [x] Bir koleksiyon seçilince sheet kapanır ve `toggleSave()` (optimistic + revert-on-error) tetiklenir.
- [x] **Zaten kayıtlıysa** ("Kaydı Kaldır") bottom sheet açılmadan direkt toggle aktifleşir.

---

## Bilinen Sınırlamalar / Backend Bağımlılıkları (özet)

| Alan | Durum | Not |
|---|---|---|
| B0.9 Checklist confirmation | ❌ Yok | Toggle lokal-kalıcı, revert yok |
| B0.10 Draft/Archived erişim güvenliği | ❌ Yok | Mobil sadece jenerik hata mesajı gösterebilir |
| B0.11 Unarchive | ❌ Yok | Endpoint gerçek, çağrı şu an başarısız |
| B0.12 Destinasyon koordinatı (geocoding) | ❌ Yok | Mock şehir→koordinat tablosu kullanılıyor |
| B0.13 PlanningSlotKey (gerçek entry ID) | ❌ Yok | itemKey'ler şehir-adı bazlı mock |
| B0.14 Unpublish + entry mutasyonları | ❌ Yok | Endpoint'ler gerçek, backend yanıtı yok |
| B0.15 ORS route proxy | ❌ Yok | Sessiz fallback zaten spec'in istediği davranış |
| B4.1 Collections | ❌ Yok | Local mock (My Trips & Detail ortak) |
| B4.3 Share zengin önizleme | ❌ Yok | Düz link paylaşılıyor |
| B4.5 Deep link (app açma) | ❌ Yok | Paylaşılan link her zaman tarayıcıda açılır |

---

## 7. Farklı Hatalar, Eksiklikler ve Öneriler

### **7.1 UX / Dialog Kapatma Sorunu (Tasarım/Kod Bug'ı)**
*   **Hata:** `DetailModal` ([TripDetailScreen.kt:L1552](file:///C:/Users/yigit/Desktop/OmniFlow/omniflow-mobile/app/src/main/java/com/omniflow/ui/trips/TripDetailScreen.kt#L1552)) bileşeni `Dialog(properties = DialogProperties(usePlatformDefaultWidth = false))` şeklinde tasarlanmış ve en dıştaki `Column` tüm ekranı (`Modifier.fillMaxSize()`) kaplıyor. Ancak bu `Column` üzerinde herhangi bir tıklama yakalayıcı (click handler/modifier) olmadığı için, **kullanıcı modal kartının dışındaki boş/scrim alanına dokunarak modalı kapatamıyor.** Sistem geri tuşu veya cihaz kenarından swipe hareketi zorunlu kalıyor.
*   **Öneri:** En dıştaki `Column` modifier'ına `onDismiss` tetikleyecek bir click handler eklenmeli (klik görsel efekti engellenerek) ve içteki `Card`'ın tıklamaları yutması sağlanmalıdır (`Modifier.clickable(enabled = false) {}` veya click listener'sız tutulmalıdır).

### **7.2 Tam Ekran Harita Modunda Lejant/Gizle Bileşeninin Gösterilmemesi (Kod Eksikliği)**
*   **Hata:** `TripDetailScreen.kt` içinde, sağ altta draggable Rota/Gün lejantı ve "✕ Gizle" butonu barındıran zengin içerikli `FullScreenMapScreen` ([TripDetailScreen.kt:L1861](file:///C:/Users/yigit/Desktop/OmniFlow/omniflow-mobile/app/src/main/java/com/omniflow/ui/trips/TripDetailScreen.kt#L1861)) Composable fonksiyonu tanımlanmış. Ancak haritadaki `⛶` (Tam Ekran) butonuna tıklandığında bu Composable çağrılmıyor. Onun yerine, inline bir dialog ile sadece `MapLibreView` ve basit bir "← Geri" ile "Kuş Bakışı / Yol" butonları render ediliyor. Draggable Timeline kartı veya lejant gizleme özelliği tam ekran haritada devreye girmiyor.
*   **Öneri:** `isFullScreen = true` olduğunda açılan `Dialog` içeriğinde, doğrudan `FullScreenMapScreen` Composable'ı çağrılarak kurgulanan tasarım tam olarak ekrana yansıtılmalıdır.
