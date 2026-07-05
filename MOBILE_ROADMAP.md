# OmniFlow Mobile — Project Roadmap (Kotlin / Jetpack Compose)

**Proje:** OmniFlow Mobile — Android (Kotlin) uygulaması
**Mimari:** Jetpack Compose + MVVM + katmanlı yapı (core / data / ui — feature bazlı UI, merkezi data, UseCase/domain yok)
**Backend:** ASP.NET Core 8.0 API (ayrı repo) — `https://omniflow-backend-dmh5e8c7caaxd0cw.spaincentral-01.azurewebsites.net/`
**Bu roadmap'in mantığı:** Önce **mevcut backend'e karşı çalışan tam bir mobil MVP** (M0–M6), sonra **backend gerektiren ileri özellikler** (M7–M14). Backend gerektiren her madde, `BACKEND_ROADMAP_V2.md`'deki task'a **⛔ Bağımlılık** etiketiyle bağlanır.

> **Yapı:** Bu roadmap, `BACKEND_ROADMAP_MVP.md` ile aynı kırılım disiplinini kullanır: her **Milestone (M)** → numaralı **Task**'lara bölünür. Her Task'ın kendi **Tahmini Süre**, **Durum** ve **Yapılacaklar** (checklist) alanı vardır. Task numarası `{milestone}.{task}` biçimindedir (örn. `Task 3.5`).
>
> **Durum etiketleri:** `✅ Tamamlandı` · `🔄 Devam ediyor` · `[ ] Bekliyor`

> **Test politikası (Minimal):** Her fazda yalnızca kritik **ViewModel unit testleri** (JVM, MockK + Turbine + coroutines-test) yazılır. UI ve uçtan uca testler manuel QA ile yürütülür. Bu, solo geliştirme + sık değişen UI için bilinçli bir tercihtir.

---

## 🛠️ Teknoloji Yığını

| Katman | Teknoloji |
|--------|-----------|
| UI | Jetpack Compose + Material 3 |
| Mimari | MVVM + Clean Architecture (3 katman) |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Network | Retrofit + OkHttp + kotlinx.serialization (veya Moshi) |
| Auth depolama | DataStore (Preferences) + EncryptedSharedPreferences (token) |
| Yerel DB / cache | Room + DataStore |
| Görsel | Coil |
| Navigation | Navigation-Compose (type-safe routes) |
| Harita | MapLibre Android SDK (native, `AndroidView` ile sarmalanır) + OpenFreeMap tile (ücretsiz, API key/kart gerektirmez) — rota çizimi: OpenRouteService backend proxy (ORS), ileride OSRM'e geçilebilir |
| Push | Firebase Cloud Messaging (M9'da) |
| Test | JUnit + MockK + Turbine + coroutines-test |
| Build | Gradle (Kotlin DSL) + Version Catalog (`libs.versions.toml`) |

**Min SDK:** 26 (Android 8.0) · **Target/Compile SDK:** güncel kararlı

---

## 📁 Klasör Şeması

```
omniflow-mobile/                         ← Backend'den AYRI repo
│
├── settings.gradle.kts
├── build.gradle.kts                     (root)
├── gradle/
│   └── libs.versions.toml               (Version Catalog — tüm bağımlılıklar)
│
└── app/
    ├── build.gradle.kts                 (Hilt, Compose, Retrofit, Room... plugin & deps)
    ├── google-services.json             (M9 / FCM ile gelir)
    │
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   │
        │   └── java/com/omniflow/
        │       │
        │       ├── OmniFlowApp.kt        (Application, @HiltAndroidApp)
        │       ├── MainActivity.kt       (tek Activity, NavHost host'u)
        │       │
        │       ├── core/                 ← Tüm feature'ların paylaştığı altyapı
        │       │   ├── network/
        │       │   │   ├── ApiResult.kt              (sealed: Success / Error / Loading)
        │       │   │   ├── NetworkModule.kt          (Hilt — Retrofit, OkHttp, Json)
        │       │   │   ├── interceptors/
        │       │   │   │   ├── AuthInterceptor.kt    (Bearer token ekler)
        │       │   │   │   └── TokenAuthenticator.kt (401'de refresh-token akışı)
        │       │   │   └── ErrorParser.kt            (ErrorResponse → UiText)
        │       │   │
        │       │   ├── auth/
        │       │   │   ├── TokenManager.kt           (access/refresh — DataStore/Encrypted)
        │       │   │   └── SessionState.kt           (oturum durumu — uygulama geneli)
        │       │   │
        │       │   ├── designsystem/                 ← Tema token'ları (TASARIMDAN gelir)
        │       │   │   └── theme/                    (Color, Type, Shape, OmniFlowTheme)
        │       │   │
        │       │   ├── preferences/                  (DataStore tabanlı tercih erişimi)
        │       │   │
        │       │   ├── navigation/
        │       │   │   ├── OmniFlowNavHost.kt
        │       │   │   ├── Routes.kt                 (sealed route tanımları)
        │       │   │   └── BottomNavBar.kt
        │       │   │
        │       │   ├── common/
        │       │   │   ├── UiState.kt                (ortak ekran state pattern)
        │       │   │   ├── UiText.kt                 (string/res sarmalayıcı)
        │       │   │   ├── Constants.kt
        │       │   │   └── extensions/               (Flow, Modifier, Date ext.)
        │       │   │
        │       │   └── di/
        │       │       ├── AppModule.kt
        │       │       ├── DatabaseModule.kt
        │       │       └── DispatcherModule.kt
        │       │
        │       ├── ui-components/                   ← Tüm feature'ların paylaştığı ORTAK component'ler
        │       │   ├── OmniButton.kt   OmniTextField.kt   OmniCard.kt   OmniTopBar.kt
        │       │   └── LoadingIndicator.kt   ErrorView.kt   EmptyState.kt
        │       │
        │       ├── data/                            ← BÜTÜN veri işi merkezi burada
        │       │   ├── local/                       (OmniFlowDatabase, dao/, datastore/) — Room/DataStore
        │       │   ├── remote/                      (AuthService, AdminService ... API servisleri)
        │       │   ├── models/                      ← feature'a göre gruplu (request+response+model birlikte)
        │       │   │   ├── auth/                     (AuthUser, Tokens, RegistrationResult,
        │       │   │   │                              AuthRequestDtos, AuthResponseDtos, RefreshTokenDtoModel)
        │       │   │   └── common/                   (paylaşılan network modelleri: ErrorResponse, ValidationErrorDetail)
        │       │   ├── mapper/                       (DTO → model dönüşümü, ör. AuthMappers)
        │       │   └── repository/                  (AuthRepository [interface] + AuthRepositoryImpl ...)
        │       │
        │       └── ui/                              ← SADECE feature ekranları (data/domain YOK)
        │           │   her ekran = Screen + ViewModel + UiState + UiModel + Event + Mapper
        │           │
        │           ├── auth/                        (M1) → splash, onboarding, login, register,
        │           │                                       verifyemail, forgotpassword, resetpassword
        │           ├── home/                        (M2) → HomeScreen, HomeViewModel, HomeMapper,
        │           │                                       HomeUiState, HomeUiModel, HomeUiEvent
        │           ├── profile/                     (M2) → me, edit, public, followers, following,
        │           │                                       suggested, topContributors, settings
        │           ├── notifications/               (M2/M6)
        │           ├── trips/                       (M3) → mytrips, detail, wizard, destinations,
        │           │                                       timeline, budget, recommendplaces, savedtrips
        │           ├── explore/                     (M4) → explore, featured, search, placeDetail
        │           ├── providers/                   (M4) → flights, hotels
        │           ├── social/                      (M5) → feed, postDetail, createPost, comments, tips
        │           ├── admin/                       (M6) → dashboard, users, posts
        │           ├── livetrip/                    (M8) → liveMode, map, visitLog, summary
        │           ├── collections/                 (M10)
        │           ├── aichat/                      (M12)
        │           └── moderation/                  (M11) → report ekranları
        │
        ├── test/                                     (JVM unit testler — ViewModel)
        │   └── java/com/omniflow/...
        │
        └── androidTest/                              (minimal — sadece kritik Compose UI)
            └── java/com/omniflow/...
```

> ✅ **`auth` feature** yeni yapıya taşındı (Step 2 tamamlandı): veri katmanı merkezi `data/`'ya çıktı (`AuthService`, request/response DTO'lar, `AuthRepository`+Impl, mapper), **usecase/domain kaldırıldı**, DI `core/di/AuthModule`'a taşındı, ekranlar `ui/auth/<screen>/` olarak düzleştirildi. Şema artık tüm feature'lar için geçerli.

### Mimari Akış (tek feature için)

```
UI (Composable) → ViewModel (UiState/Flow) → Repository (data/)
                                                   ↑
                                  RepositoryImpl → Api (remote) / Dao (local)
Response DTO ──(Mapper · feature içinde)──► UiModel
```

| Katman | Sorumluluk | Not |
|--------|------------|-----|
| **core** | Ortak altyapı: network, token/session, theme, navigation, di, common, preferences | Feature'a özel değil |
| **ui-components** | Tüm feature'ların paylaştığı Compose component'leri | Feature'a özel component'ler ilgili feature içinde kalır |
| **data** | API servisleri, request/response DTO'ları, repository (interface + impl), local (Room/DataStore) | Tüm veri tek merkezde |
| **ui (feature)** | Sadece ekran: Screen + ViewModel + UiState + UiModel + Event + Mapper | ViewModel → Repository çağırır; Mapper DTO → UiModel çevirir. **UseCase/domain katmanı yok** |

---

## 🧩 UI State Konvansiyonu

Veri çeken **her ekran**, tek bir `UiState` ile yönetilir ve aşağıdaki 4 durumu kapsar. Bu durumların **varsayılan davranışı** burada bir kez tanımlanır; sonraki fazlardaki ekran tablolarında yalnızca **ekrana özel** kısımlar (özellikle Empty mesajı ve Success düzeni) belirtilir.

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>          // başarılı ama veri yok
    data class Error(val message: UiText) : UiState<Nothing>
}
```

| Durum | Varsayılan davranış | Component |
|-------|---------------------|-----------|
| **Loading** | İçeriğin şekline uygun **skeleton/shimmer** (liste için placeholder kartlar). Tam ekran spinner yalnızca Splash'te. | `LoadingIndicator` / skeleton |
| **Empty** | Başarılı yanıt + 0 kayıt → **ekrana özel mesaj + (varsa) CTA**. Her ekranda ayrı yazılır. | `EmptyState` |
| **Error** | İkon + mesaj + **"Tekrar dene"**. Aksiyon hataları (upvote vb.) için tam ekran yerine **snackbar/inline**. | `ErrorView` |
| **Success** | Gerçek içerik. Sayfalı listelerde alt kısımda **load-more / sonraki sayfa** durumu ayrıca yönetilir. | ekrana özel |

**Ek kurallar:**
- **Pagination ekranları:** ilk yükleme = Loading; sayfa sonu = footer loading; ilk sayfa boşsa = Empty; sonraki sayfa hatası = inline retry (mevcut liste korunur).
- **Form ekranları:** alan bazlı validasyon hatası (kırmızı yardım metni) + submit sırasında buton loading + başarıda yönlendirme/snackbar.
- **Aksiyon durumları** (save/upvote/follow): optimistic update + hata olursa geri alma + snackbar.
- **401:** global olarak M0'daki `TokenAuthenticator` ile yönetilir (ekran bazında ele alınmaz).

> Sonraki her fazda "**Ekran Durumları**" tablosu vardır. Loading/Error çoğu ekranda varsayılanı kullanır; tabloda asıl **Empty** ve **Success** kararları netleştirilir.

---

## 🎯 M0 — Proje Kurulumu & Mimari İskelet

### Scope

Boş Android projesinden, ilk gerçek ekrandan önce tüm altyapının hazır olması: build, DI, network (token/refresh dahil), design system, navigation iskeleti.

---

### Task 0.1: Proje Oluşturma & Git

**Tahmini Süre:** 2 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] Android Studio'da yeni Compose projesi (`com.omniflow`)
- [x] Ayrı git repo init (backend'den bağımsız)

---

### Task 0.2: Version Catalog & Bağımlılıklar

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `libs.versions.toml` Version Catalog kurulumu
- [x] Bağımlılıklar: Compose BOM, Material3, Hilt, Retrofit, OkHttp, kotlinx.serialization, Coil, Room, DataStore, Navigation-Compose, Coroutines, MockK/Turbine (test)

---

### Task 0.3: Paket İskeleti & Application/Activity

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `core/`, `data/`, `ui/` ve `ui-components/` paket iskeleti oluştur (yukarıdaki şema)
- [x] `OmniFlowApp` (@HiltAndroidApp), `MainActivity` (setContent + Theme + NavHost placeholder)
- [x] Build başarılı, uygulama boş ekranla açılıyor

---

### Task 0.4: ApiResult & Error Parsing

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `ApiResult` sealed wrapper (Success/Error/Loading)
- [x] `ErrorResponse` parse (backend 422 `ValidationErrorDetail` formatına uygun)

---

### Task 0.5: NetworkModule (Retrofit/OkHttp/Json)

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `NetworkModule` (Hilt) — Retrofit + OkHttp + Json
- [x] Base URL config (debug/release)

---

### Task 0.6: Auth Interceptor & Token Authenticator

**Tahmini Süre:** 2 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `AuthInterceptor` — istek başlığına Bearer access token
- [x] `TokenAuthenticator` — 401'de `POST /api/account/refresh-token` (mobile: body + `X-Platform: mobile`) ile yeni token al, isteği tekrarla; başarısızsa oturumu kapat

---

### Task 0.7: TokenManager & SessionState

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `TokenManager` — access/refresh token DataStore/Encrypted saklama
- [x] `SessionState` — uygulama geneli oturum durumu (Flow)

---

### Task 0.8: Tema (Renk / Tipografi / Shape)

**Tahmini Süre:** 2 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Tasarımdan** renk paleti, tipografi, shape → `OmniFlowTheme` (Material 3)

---

### Task 0.9: Temel Component'ler

**Tahmini Süre:** 2 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] Temel component'ler: `OmniButton`, `OmniTextField`, `OmniCard`, `OmniTopBar`, `LoadingIndicator`, `ErrorView`, `EmptyState`

---

### Task 0.10: Navigation İskeleti

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `Routes` (sealed) + `OmniFlowNavHost` + `BottomNavBar` (5 sekme placeholder)

---

### Task 0.11: UiState/UiText & Extension'lar

**Tahmini Süre:** 0.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `UiState` / `UiText` ortak pattern'leri
- [x] Ortak extension'lar (Flow `asUiState`, Modifier, tarih formatlama)

---

### Definition of Done (M0)

- [ ] Uygulama derleniyor ve açılıyor
- [x] Token saklama + otomatik refresh altyapısı hazır (henüz ekran yok ama test edilebilir)
- [x] Tema ve temel component'ler kullanılabilir
- [x] Navigation iskeleti ayakta

### Test (Minimal)

- [x] `ApiResult` / `ErrorParser` map'leme unit testi (backend hata formatı doğru parse ediliyor mu)

---

## 🎯 M1 — Auth & Onboarding

> **Backend:** Mevcut (`/api/account/*`). Bağımlılık yok.

### Scope

Splash → onboarding → kayıt/giriş → email doğrulama → şifre sıfırlama. M0 token altyapısı burada uçtan uca bağlanır.

---

### Task 1.1: AuthService (Retrofit)

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `AuthService` — register, login, refresh, verify-email, resend-verification, forgot-password, reset-password endpoint imzaları

**Plan:**
- `data/remote/AuthService.kt` Retrofit `suspend` imzalarıyla `/api/account/*` sözleşmesini tanımlar.
- `login` ve `refresh-token` isteklerinde mobil token gövdesi için mevcut global `X-Platform: mobile` davranışı kullanılır.
- `refresh-token` imzası `AuthService` içinde sözleşme bütünlüğü için bulunur; 401 otomatik yenileme M0'daki ayrı, authenticator içermeyen `RefreshTokenApi` üzerinden çalışmaya devam eder.
- Başarı tipleri endpoint'e özel olur: register `202 RegistrationVerificationResponseDto`, login/refresh `200 AuthResponseDto`, diğerleri `200 MessageResponseDto`.
- Retrofit dışı HTTP/status/error dönüşümü Task 1.3'te repository sorumluluğunda kalır; `AuthService` içine iş mantığı eklenmez.

---

### Task 1.2: DTO + Model + Mapper

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] Request/Response DTO'ları (LoginRequest, RegisterRequest, AuthResponse...)
- [x] Data modelleri (`AuthUser`, `Tokens`, `RegistrationResult`) → `data/models/`
- [x] DTO → model mapper'lar → `data/mapper/`

**Plan:**
- Tüm auth DTO'ları `data/models/auth/` altında `Dto` son ekiyle oluşturulur (request + response birlikte): login, register, verify email, resend verification, forgot password, reset password.
- Response modelleri backend ile birebir eşleşir: auth (`accessToken`, nullable `refreshToken`, `id`, `username`, `email`, `role`), registration verification ve message.
- `AuthUser` data modeli `id`, `username`, `email`, `role`; `Tokens` modeli access/refresh token taşır (`data/models/`). Bu modeller serialization annotation taşımaz, DTO'lardan ayrıdır.
- Mapper'lar data katmanında tutulur. `AuthResponseDto`, kullanıcı ve token modellerine ayrı map edilir; mobil login/refresh için boş veya null refresh token geçersiz sözleşme kabul edilir.
- Mevcut placeholder repository constructor kullanımları yalnızca yeni data modelini derletecek kadar uyarlanır; API çağrısı ve token kaydetme Task 1.3'e bırakılır.

#### Task 1.1–1.2 Uygulama Sırası

1. Backend alan/status sözleşmesini DTO'lara sabitle.
2. Data modellerini (`data/models/`) ve tek yönlü DTO → model mapper'larını (`data/mapper/`) oluştur.
3. `AuthService` endpoint imzalarını DTO tiplerine bağla.
4. M0 refresh DTO/API ayrımını koruyup isim çakışmalarını temizle.
5. Serialization, mapper ve endpoint path/header testlerini ekle.
6. `testDebugUnitTest`, `lintDebug` ve `assembleDebug` çalıştır.

#### Etki Alanı ve Riskler

- `AuthUser` alan değişikliği `AuthRepositoryImpl` imzalarını etkiler; davranış eklemeden derleme uyumu sağlanır.
- `refresh-token` iki Retrofit client tarafından temsil edilir; testler path/body/header sözleşmesinin ayrışmasını engeller.
- Backend `refreshToken` alanı web yanıtında nullable, mobil yanıtta zorunludur; null kontrolü mapper/repository sınırında yapılır.
- Backend mesajları doğrudan UI metni kabul edilmez; yerelleştirme Task 1.7–1.10 ekranlarında yapılır.

#### Doğrulama Standardı

- [x] Yedi endpoint doğru HTTP methodu, path ve DTO tipiyle tanımlı.
- [x] DTO JSON alanları backend örnekleriyle serialize/deserialize oluyor.
- [x] Auth mapper kullanıcı ve token alanlarını kayıpsız dönüştürüyor.
- [x] `data/models` modellerinde Retrofit veya serialization importu yok (DTO'lardan ayrı).
- [x] M0 `TokenAuthenticatorTest` regresyonsuz geçiyor.
- [x] Unit test, lint ve debug build başarılı.

---

### Task 1.3: AuthRepository + Impl

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] `AuthRepository` (data/repository interface)
- [x] `AuthRepositoryImpl` (data) — Api çağrıları + `ApiResult` sarmalama + token saklama entegrasyonu

**Plan:**
- `AuthRepository` tüm auth işlemlerini `ApiResult` ile sunar; Retrofit DTO'ları UI katmanına sızmaz.
- `login(email, password)` başarılı yanıttan `AuthUser` üretir, access/refresh token çiftini dönüşten önce `TokenStore`'a atomik olarak kaydeder ve UI'a token döndürmez.
- `register(username, email, password, confirmPassword)` backend'in gerçek request sözleşmesine geçirilir ve `RegistrationResult(requiresEmailVerification)` döndürür.
- Verify, resend, forgot ve reset işlemleri başarıda `Unit` döndürür; backend mesajları doğrudan UI metni olarak kullanılmaz.
- Ortak `ApiCallExecutor`, `HttpException` gövdesini `ErrorParser` ile `ApiResult.Error`'a çevirir; ağ/bilinmeyen hatalara güvenli fallback uygular ve coroutine cancellation'ı yutmaz.
- `core/di/AuthModule`, Retrofit'ten `AuthService` üretir ve `AuthRepositoryImpl` bağlamasını Hilt'e ekler.

---

### Task 1.4: Auth UseCase'ler

**Tahmini Süre:** 1 saat
**Durum:** ❌ **Kaldırıldı (Step 2 tamamlandı)**

> **Mimari kararı (mentör):** UseCase/domain katmanı tamamen kaldırıldı. ViewModel doğrudan `AuthRepository`'yi (data katmanı) çağırır. Bu task artık geçerli değil; referans için bırakıldı.

**Eski maddeler (artık kodda yok):**
- ~~`LoginUseCase`, `RegisterUseCase`~~
- ~~`VerifyEmailUseCase`, `ResendVerificationUseCase`~~
- ~~`ForgotPasswordUseCase`, `ResetPasswordUseCase`~~

**Plan:**
- Altı use case yalnız domain repository sözleşmesini dışarı açar; Retrofit, DTO, `TokenStore` veya Android bağımlılığı içermez.
- Login use case `ApiResult<AuthUser>`, register use case `ApiResult<RegistrationResult>`, mesaj tabanlı dört işlem `ApiResult<Unit>` döndürür.
- Parametreler backend sözleşmesiyle kayıpsız taşınır; UI validation ve kullanıcıya gösterilecek yerelleştirilmiş metinler Task 1.7–1.10 ViewModel'lerinde kalır.

#### Task 1.3 RFC-Lite Uygulama Planı

**Amaç:** Auth veri sınırını gerçek backend çağrılarıyla tamamlamak ve başarılı login oturumunu güvenle saklamak. (UseCase/domain katmanı yoktur; ViewModel doğrudan repository çağırır.)

**Teknik Strateji:**
- **Pattern:** Repository; tekrar eden HTTP hata dönüşümü için ortak executor.
- **State:** Token oturumu `TokenStore`'da; repository stateless.
- **Constraints:** Backend değişikliği yok, şifre kalıcı depoya yazılmaz, cancellation yeniden fırlatılır, `data/models` modellerine Retrofit/serialization sızmaz.

**Dosya Değişiklikleri:**

| Aksiyon | Dosya | Amaç |
|:--|:--|:--|
| Yeni | `core/network/ApiCallExecutor.kt` | Ortak HTTP/ağ hata dönüşümü |
| Yeni | `data/models/auth/RegistrationResult.kt` | Kayıt sonucunu temsil etme |
| Yeni | `data/repository/AuthRepository.kt` | Altı kullanıcı auth operasyonunun `ApiResult` sözleşmesi (interface) |
| Yeni | `data/repository/AuthRepositoryImpl.kt` | API çağrıları, mapper ve token saklama |
| Yeni | `core/di/AuthModule.kt` | `AuthService` provider ve repository binding |
| Yeni | `core/network/ApiCallExecutorTest.kt` | HTTP, network ve cancellation testleri |
| Yeni | `data/repository/AuthRepositoryImplTest.kt` | Request, mapping, hata ve token testleri |

**Uygulama Sırası:**
1. Repository ve executor davranış testlerini kırmızı aşamada ekle.
2. `ApiCallExecutor` ile ortak hata sınırını oluştur.
3. Data sonuç modelini ve repository imzalarını düzelt.
4. `AuthRepositoryImpl` içinde yedi endpoint'i bağla; login token kaydını başarı koşulu yap.
5. Hilt `AuthModule`'ü tamamla.
6. Hedef testleri, tüm unit testleri, lint ve debug build'i çalıştır.

**Etki Alanı ve Riskler:**
- `AuthRepository` dönüş/imza değişiklikleri gelecekteki Task 1.7–1.9 ViewModel sözleşmelerini belirler; henüz bağlı ekran olmadığı için mevcut runtime kırılımı yoktur.
- Altı repository metodu tek hata dönüştürücü olmadan shotgun surgery üretir; `ApiCallExecutor` sonraki feature repository'lerinde de kullanılacak ortak sınırdır.
- Refresh-token kullanıcı işlemi değildir; M0 `TokenAuthenticator` üzerinden otomatik yönetilmeye devam eder.
- Token kaydı başarısızsa login başarı sayılmaz; yarım oturumla Home'a geçiş engellenir.
- `401`, `403`, `409` ve `422` kodları `ApiResult.Error.code` ile korunur; alan hataları `validationErrors` üzerinden ViewModel'e taşınır.
- Verify-email işlemi MVP akışında doğrudan kullanılmasa da Task 10.6 app-link doğrulaması için repository sözleşmesinde tutulur.

**Doğrulama Standardı:**
- [x] Login başarılıysa kullanıcı map edilir ve tokenlar tam bir kez saklanır.
- [x] Login/API/token saklama hatasında sahte başarı veya yarım oturum oluşmaz.
- [x] Register request'i username/email/password/confirmPassword alanlarını eksiksiz taşır.
- [x] Altı repository metodu tüm parametreleri kayıpsız iletir.
- [x] HTTP status, backend validation detayları ve ağ hataları doğru `ApiResult.Error` üretir.
- [x] Cancellation yutulmaz; `data/models` modeline framework bağımlılığı girmez.
- [x] Hilt graph derlenir; unit test, lint ve debug build başarılıdır.

---

### Task 1.5: Splash Ekranı

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Splash** — token kontrolü → Home veya Onboarding/Login yönlendirmesi
- [x] `SplashViewModel` + UiState

**Plan:**
- Splash yerel `SessionState` ve onboarding görülme bilgisini birlikte gözlemler; başlangıç verileri hazır olana kadar loading durumunda kalır.
- `SignedIn` → Home, `SignedOut + onboarding görülmedi` → Onboarding, `SignedOut + onboarding görüldü` → Login kararı verilir.
- Token geçerliliği için Splash'ta ağ isteği yapılmaz; şifrelenmiş token çifti oturum varlığını belirler, süresi dolmuş token ilk korumalı istekte M0 `TokenAuthenticator` tarafından yenilenir.
- Navigation tek seferlik destination etkisiyle çalışır; hedefe geçerken Splash back stack'ten inclusive kaldırılır ve geri tuşuyla Splash'a dönülmez.
- Mevcut sabit `600ms` gecikme yerine ViewModel başlangıcından itibaren minimum `1000ms` görünürlük kapısı kullanılır; veri daha geç gelirse ek süre bindirilmez. Ekran veri okunurken OmniFlow wordmark/brand işareti ve loading göstergesini light/dark palette uygun gösterir.

#### Task 1.5 RFC-Lite Uygulama Planı

**Amaç:** Uygulama açılışında yerel oturum ve onboarding durumunu deterministik biçimde çözerek kullanıcıyı doğru başlangıç ekranına yönlendirmek.

**Teknik Strateji:**
- **Pattern:** Hilt ViewModel + tek yönlü `StateFlow<SplashUiState>` + navigation callback.
- **State:** `Loading` veya hedef destination; Composable yalnız state render eder ve navigation etkisini iletir.
- **Constraints:** Splash'ta ağ çağrısı yok, minimum görünürlük `1000ms`, token/şifre loglanmaz, Task 1.6 dışında onboarding flag yazılmaz, mevcut auth refresh akışı korunur.

**Dosya Değişiklikleri:**

| Aksiyon | Dosya | Amaç |
|:--|:--|:--|
| Yeni | `core/preferences/OnboardingStore.kt` | Onboarding durumunu data katmanından soyutlama |
| Değiştir | `data/local/datastore/PreferencesManager.kt` | `OnboardingStore` sözleşmesini uygulama |
| Değiştir | `core/di/AppModule.kt` | Onboarding store Hilt binding'i |
| Yeni | `ui/auth/splash/SplashUiState.kt` | Loading ve launch destination modeli |
| Yeni | `ui/auth/splash/SplashViewModel.kt` | Session/onboarding akışlarını birleştirme |
| Değiştir | `ui/auth/splash/SplashScreen.kt` | Brand loading görünümü ve state tüketimi |
| Değiştir | `core/navigation/OmniFlowNavHost.kt` | Destination eşleme ve back-stack temizliği |
| Değiştir | `res/values/strings.xml` | Splash erişilebilir metinleri |
| Yeni | `ui/auth/splash/SplashViewModelTest.kt` | Dört yönlendirme/loading senaryosu |

**Mimari Sınır:**
- Değişiklik beşten fazla dosyaya yayılır; `OnboardingStore` soyutlaması ViewModel'in doğrudan DataStore sınıfına bağlanmasını önler ve Task 1.6'nın okuma/yazma ihtiyacını tek sözleşmede toplar.
- `SplashDestination` navigation route string'i taşımaz; route eşleme yalnız `OmniFlowNavHost` içinde kalır.

**Uygulama Sırası:**
1. Splash ViewModel'in loading ve üç destination davranışını testlerle kırmızı aşamada sabitle.
2. `OnboardingStore` sözleşmesini ve mevcut `PreferencesManager` adaptasyonunu ekle.
3. `SplashUiState` ile ViewModel akış birleştirme mantığını uygula.
4. Splash Composable'ını state-driven brand/loading görünümüne dönüştür.
5. NavHost destination eşlemesini inclusive back-stack temizliğiyle bağla.
6. Hedef testleri, tüm unit testleri, lint ve debug build'i çalıştır; bağlı telefonda üç launch yolunu kontrol et.

**Etki Alanı ve Riskler:**
- Task 1.6 tamamlanana kadar onboarding placeholder'ı flag'i değiştirmez; signed-out kullanıcı her yeniden açılışta Onboarding görür. Flag yazma sorumluluğu Task 1.6'da kalır.
- Yerelde token çifti varsa Splash Home'a geçer; refresh başarısız olursa mevcut authenticator oturumu temizler, sonraki global session navigation davranışı Splash kapsamı dışındadır.
- `Unknown` session state navigation üretmez; erken Login/Home sıçraması ve çift navigation engellenir.
- NavHost callback değişikliği yalnız Splash çağrı noktasını etkiler; diğer route sözleşmeleri korunur.

**Doğrulama Standardı:**
- [x] `Unknown` session durumunda Splash loading'de kalır ve navigation üretmez.
- [x] Hazır destination minimum `1000ms` dolmadan navigation üretmez.
- [x] `SignedIn` doğrudan Home destination üretir.
- [x] `SignedOut` kullanıcı onboarding flag'ine göre Onboarding veya Login'e gider.
- [x] Navigation yalnız bir kez çalışır ve Splash geri yığından kaldırılır.
- [x] Splash light/dark temada wordmark ve progress göstergesini okunabilir gösterir.
- [x] ViewModel doğrudan DataStore/Android navigation bağımlılığı taşımaz.
- [x] Hilt graph derlenir; unit test, lint ve debug build başarılıdır.

---

### Task 1.6: Onboarding Ekranı

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Onboarding** (3 ekran, swipe)
- [x] "görüldü" flag'i DataStore'da (bir kez gösterim)
- [x] ViewModel + UiState

**Plan:**
- **Development notu:** Debug build'de görsel geliştirme süresince onboarding her uygulama açılışında zorunlu gösterilir; release build kalıcı `onboardingSeen` flag'ini kullanmaya devam eder.
- Üç sayfa sırasıyla OmniFlow ürün değerini, route planning + explore/fork özelliklerini ve Live Trip + paylaşım/topluluk deneyimini anlatır.
- İlk iki sayfada swipe, “Next” ve “Skip” aksiyonları bulunur. Son sayfada “Get Started” Register'a, “Skip” Login'e yönlendirir.
- Onboarding yalnız çıkış aksiyonunda `OnboardingStore.setOnboardingSeen(true)` başarılı olduktan sonra kapanır; yalnız sayfaları gezmek flag'i değiştirmez.
- Flag yazımı sırasında butonlar kilitlenir. Yazma hatasında navigation yapılmaz, kullanıcıya tekrar deneyebileceği yerelleştirilmiş hata gösterilir.
- Login/Register'a geçerken Onboarding back stack'ten inclusive kaldırılır; Splash sonraki açılışta flag üzerinden doğrudan Login'i seçer.

#### Task 1.6 RFC-Lite Uygulama Planı

**Amaç:** İlk kullanıcıya OmniFlow'un üç temel değer alanını kısa, swipe edilebilir bir akışla anlatmak ve tamamlanma durumunu kalıcı olarak saklamak.

**Teknik Strateji:**
- **Pattern:** Data-driven `HorizontalPager` + Hilt ViewModel + `StateFlow<OnboardingUiState>` + tek seferlik navigation effect.
- **State:** Aktif sayfa, kayıt/loading durumu ve hata; sayfa içerikleri immutable UI modelleridir.
- **Constraints:** Yeni görsel/dependency paketi yok, metinler string resource'ta, flag yalnız kullanıcı çıkışında yazılır, mevcut `OnboardingStore` yeniden kullanılır.

**Dosya Değişiklikleri:**

| Aksiyon | Dosya | Amaç |
|:--|:--|:--|
| Yeni | `ui/auth/onboarding/OnboardingUiState.kt` | Sayfa, loading, hata ve destination modelleri |
| Yeni | `ui/auth/onboarding/OnboardingViewModel.kt` | Sayfa state'i ve flag/navigation orkestrasyonu |
| Değiştir | `ui/auth/onboarding/OnboardingScreen.kt` | Üç sayfalı pager, göstergeler ve CTA'lar |
| Değiştir | `core/navigation/OmniFlowNavHost.kt` | Onboarding destination eşleme ve back-stack temizliği |
| Değiştir | `res/values/strings.xml` | Başlık, açıklama, aksiyon ve hata metinleri |
| Yeni | `ui/auth/onboarding/OnboardingViewModelTest.kt` | Page state, persistence, hata ve çift tıklama testleri |

**Mimari Sınır:**
- Altı dosyalık etki UI, state, navigation ve kaynak katmanlarına dağılır; üç ayrı ekran kopyalamak yerine data-driven page modeli ortak pager kabuğunu korur.
- ViewModel Compose `PagerState` veya route string'i bilmez; UI page değişimini bildirir, navigation eşlemesi NavHost'ta kalır.

**Uygulama Sırası:**
1. Sayfa değişimi, flag-before-navigation, hata ve duplicate action davranışlarını testlerle kırmızı aşamada sabitle.
2. `OnboardingUiState`, page modeli ve ViewModel'i uygula.
3. Mevcut placeholder'ı üç sayfalı responsive `HorizontalPager` ile değiştir.
4. Page indicator, Next/Skip/Get Started aksiyonlarını loading ve erişilebilirlik durumlarıyla bağla.
5. Navigation effect'lerini Login/Register route'larına inclusive back-stack temizliğiyle eşle.
6. Hedef testleri, tüm unit testleri, lint ve debug build'i çalıştır; bağlı telefonda swipe, skip, register ve yeniden açılış yollarını doğrula.

**Etki Alanı ve Riskler:**
- Task 1.5 Splash artık onboarding flag'ini tüketiyor; yanlış erken yazım onboarding'in kalıcı atlanmasına yol açacağından persistence yalnız açık çıkış aksiyonunda yapılır.
- DataStore yazımı başarısızken navigation yapılırsa sonraki açılışta onboarding tekrar görünür; bu nedenle yazma başarı koşuludur.
- Hızlı çift tıklama iki DataStore yazımı ve iki navigation üretebilir; ViewModel `isSaving` kapısıyla tekrarları reddeder.
- Onboarding ekranı sistem geri tuşuyla kapanırsa flag yazılmaz; kullanıcı bir sonraki signed-out açılışta onboarding'i tekrar görür.

**Doğrulama Standardı:**
- [x] Üç sayfa swipe ve “Next” ile doğru sırada gezilir; indicator aktif sayfayı gösterir.
- [x] “Skip” flag'i yazdıktan sonra Login'e gider.
- [x] Son sayfadaki “Get Started” flag'i yazdıktan sonra Register'a gider.
- [x] Flag yazma hatasında navigation oluşmaz ve tekrar deneme mümkün olur.
- [x] Hızlı tekrar aksiyonu tek flag yazımı ve tek navigation üretir.
- [x] Onboarding route'u çıkış sonrası back stack'te kalmaz; yeniden açılış Login'e yönlenir.
- [x] Light/dark tema, font ölçekleme ve erişilebilir semantics ile içerik okunabilir kalır.
- [x] Hilt graph derlenir; unit test, lint ve debug build başarılıdır.

---

### Task 1.7: Login Ekranı

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Login** — email/şifre, hata gösterimi, "forgot password" linki
- [x] Yanlış kimlik → inline "Email veya şifre hatalı" (401)
- [x] Başarıda token saklanır → Home
- [x] `LoginViewModel` + UiState

**Plan:**
- Login ekranı controlled state kullanır; email, şifre, şifre görünürlüğü, alan hataları, genel hata ve loading `LoginUiState` içinde tutulur.
- `LoginViewModel` doğrudan `AuthRepository.login` çağırır. Başarılı repository sonucu tokenların kaydedildiği anlamına gelir; ViewModel tek seferlik Home navigation effect'i üretir.
- Boş/geçersiz email ve boş şifre istemci tarafında doğrulanır. Alan düzenlenince ilgili hata temizlenir; loading sırasında alanlar ve tekrar submit devre dışıdır.
- `401` sabit, yerelleştirilmiş yanlış kimlik mesajına; `403` doğrulanmamış email mesajına; ağ ve beklenmeyen hatalar güvenli genel mesaja dönüştürülür. `403` mesajı yanında Verify Email ekranına geçiş aksiyonu sunulur; backend metni doğrudan UI kopyası yapılmaz.
- Şifre maskeli başlar ve görünürlük kontrolü sunar. Email klavyesi, password IME action ve klavye submit davranışı bağlanır.
- Forgot Password ve Create Account mevcut route'lara gider. Başarılı login Home'a geçerken Login route'u inclusive temizlenir; geri tuşuyla Login'e dönüş engellenir.
- Görsel uygulama sağlanan 393×852 Login referansını responsive Compose yerleşimine çevirir: `#F5F7F8` zemin, sağ üst mavi glow, 52 dp logo, ortalanmış başlık/alt başlık, 345 dp maksimum form genişliği, 58 dp alan/buton yüksekliği ve 18 dp radius.
- Tasarımdaki divider ve Google CTA görsel olarak eklenir. Backend B1 ve mobil M7 tamamlanana kadar buton tasarım renklerini koruyan disabled durumda gösterilir; tıklanabilir sahte/no-op auth aksiyonu üretilmez.

#### Task 1.7 RFC-Lite Uygulama Planı

**Amaç:** Email/şifre girişini canlı auth repository'sine bağlamak; validation, loading, hata ve başarılı Home geçişini deterministik hale getirmek.

**Teknik Strateji:**
- **Pattern:** Unidirectional UI state + one-shot navigation effect; ViewModel doğrudan repository kullanır.
- **State:** Form ve request durumu `LoginUiState`; navigation buffered effect akışı.
- **Constraints:** UseCase yok, backend değişikliği yok, şifre persist/log edilmez, çift submit engellenir, backend hata metni doğrudan gösterilmez.

**Dosya Değişiklikleri:**

| Aksiyon | Dosya | Amaç |
|:--|:--|:--|
| Yeni | `ui/auth/login/LoginUiState.kt` | Form, validation, loading ve effect sözleşmesi |
| Yeni | `ui/auth/login/LoginViewModel.kt` | Validation, repository çağrısı ve hata eşleme |
| Değiştir | `ui/auth/login/LoginScreen.kt` | State-driven form, password visibility, inline hata ve loading UI |
| Değiştir | `uicomponents/OmniTextField.kt` | Geriye uyumlu trailing icon ve IME action desteği |
| Değiştir | `core/navigation/OmniFlowNavHost.kt` | Başarılı login back-stack temizliği |
| Değiştir | `res/values/strings.xml` | Yerelleştirilebilir login metinleri ve hata mesajları |
| Yeni | `test/.../ui/auth/login/LoginViewModelTest.kt` | State, validation, sonuç ve duplicate submit testleri |

**Uygulama Sırası:**
1. ViewModel validation, success, `401`, `403`, genel hata ve duplicate submit testlerini kırmızı aşamada ekle.
2. `LoginUiState`, effect ve `LoginViewModel` repository entegrasyonunu uygula.
3. Ortak text field'i password visibility ve IME ihtiyaçlarını destekleyecek şekilde genişlet; yeni `trailingIcon` ve `keyboardActions` parametrelerine geriye uyumlu varsayılanlar ver.
4. Login ekranını state-driven, responsive ve erişilebilir Compose formuna dönüştür.
5. Home, Register ve Forgot Password navigation davranışlarını ve back stack'i bağla.
6. Task 1.7 checkbox/durumunu güncelle; unit test, debug build ve bağlı telefonda uçtan uca login doğrulaması yap.

**Etki Alanı ve Riskler:**
- `OmniTextField` ortak bileşendir; `trailingIcon = null` ve `keyboardActions = KeyboardActions.Default` varsayılanlarıyla Register ve Reset Password çağrıları kırılmamalıdır.
- Repository başarıdan önce token kaydeder; ViewModel'in ayrıca token yazması çift kayıt ve tutarsız oturum üretir.
- Backend `403` doğrulanmamış, askıya alınmış veya başka forbidden durumlar döndürebilir; Task 1.7 inline hata ve Verify Email geçişi sunar, email doğrulama köprüsünün tam davranışı Task 1.9'da tamamlanır.
- Splash ve Onboarding Login'e geçerken kendilerini zaten yığından siler. Bu nedenle başarılı girişte `popUpTo(Routes.Login.route) { inclusive = true }` zorunludur; mevcut `popUpTo(Splash)` Login'i yığında bırakır.
- Task 1.10 tamamlanana kadar Forgot Password mevcut reset placeholder route'una gider; login task'ı reset akışını genişletmez.

**Doğrulama Standardı:**
- [x] Geçersiz form repository çağrısı yapmaz ve doğru alan hatalarını gösterir.
- [x] Loading sırasında form kilitlenir; hızlı tekrar yalnız bir login isteği üretir.
- [x] `401` ve `403` ayrı, yerelleştirilmiş inline mesajlara dönüşür.
- [x] `403` durumunda Verify Email aksiyonu görünür ve doğru route'a gider.
- [x] Ağ/bilinmeyen hata güvenli mesaj gösterir; kullanıcı düzenleyip yeniden deneyebilir.
- [x] Başarılı login token kaydından sonra Home'a yalnız bir kez gider ve Login back stack'ten kalkar.
- [x] Şifre varsayılan maskeli, görünürlük kontrolü ve IME submit erişilebilir çalışır.
- [x] Register/Forgot bağlantıları doğru route'a gider; mevcut ekranlar ortak bileşen değişiminden etkilenmez.
- [x] Login → Home geçişinden sonra sistem geri tuşu Login'e dönmez.
- [x] Tasarım 393×852 referansına sadık, küçük/büyük telefon ve klavye açık durumunda taşmasızdır; divider ve Google CTA M7'ye kadar disabled gösterilir.
- [x] Unit test, Android test derleme ve debug build başarılı; APK bağlı telefona kuruldu.
- [x] Canlı Azure login akışı bağlı telefonda doğrulandı. (Agent ortamında Azure hostname DNS çözümlemesi engellendi.)

---

### Task 1.8: Register Ekranı

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Register** — username/email/şifre/şifre tekrar → 202 + "verify email" ekranına
- [x] 422 → `field/code` ile alan bazlı hata; duplicate email/username `400` → ilgili alanda inline
- [x] `RegisterViewModel` + `RegisterUiState` + tek seferlik navigation effect'leri
- [x] Canlı şifre checklist'i — 8+ karakter, büyük/küçük harf, rakam ve özel karakter
- [x] Kesin CTA sırası — Create account → divider → disabled Google → Log in
- [x] `verticalScroll` + `imePadding`; scroll içinde `weight` kullanılmıyor
- [x] Başarılı kayıtta email `SavedStateHandle` ile Verify Email akışına taşınıyor
- [x] Unit testler, Android test kaynak derlemesi ve debug build başarılı; APK bağlı Xiaomi cihaza kuruldu
- [x] Canlı Azure `202/400/422` akışı bağlı telefonda doğrulandı
- [x] Register ekranı fiziksel cihazda manuel QA ile doğrulandı; `verticalScroll` + `imePadding` klavye senaryosu başarılı

---

### Task 1.9: Verify Email Info Ekranı

**Tahmini Süre:** 3 saat
**Durum:** ✅ Tamamlandı

> **Akış kararı:** Backend email doğrulamayı **zorunlu** kılar (login, doğrulanmamış kullanıcıya `403` döner). Maildeki doğrulama linki **web frontend'ine** iner (`FrontendVerifyUrl`), mobil app'e değil. Bu yüzden bu ekran bir **"doğrula → app'e dön → giriş yap" köprüsüdür**. **Polling yapılmaz**; doğrulamayı login'in 403'ü garanti eder. (Linkin doğrudan app'te açılması ayrı bir iş → `Task 10.6`.)

#### Amaç

Figma'daki `Verify Email / Default`, `Error - Not Verified`, `Resend Success + Cooldown` ve `Button Loading` durumlarını Compose ile uygulamak; resend, mail uygulaması ve kullanıcı tetiklemeli doğrulama sonrası login akışını güvenli ve test edilebilir hale getirmek.

#### Teknik Strateji

- `VerifyEmailViewModel`, `VerifyEmailUiState` ve tek seferlik `VerifyEmailEffect` kullanılacak; ViewModel doğrudan `AuthRepository` çağıracak.
- Kayıt/Login parolası navigation state'e veya diske yazılmayacak. Sessiz login için parola yalnızca process-memory içinde yaşayan `PendingAuthCredentialsStore` içinde tutulacak ve başarı/iptalde temizlenecek.
- Email hassas parola bilgisinden ayrılarak `SavedStateHandle` ile Login'e taşınacak. Verify ekranından "Login'e dön" veya credential-missing fallback sonrasında Login email alanı otomatik dolu açılacak.
- `@Singleton` store process death sonrasında korunmaz. Process ölümü sonrası parola kaybolursa sessiz login denenmeyecek; `SavedStateHandle` ile kurtarılan email ön-dolgulu Login açılıp kullanıcıdan yalnızca parola yeniden istenecek.
- Register kaynağında ilk `60 sn` cooldown ekran açılışında başlayacak; Login `403` kaynağında resend hemen aktif olacak. Başarılı resend sonrası cooldown yeniden `60 sn` olacak.
- Backend `429`, ağ ve beklenmeyen resend hataları kalıcı kutu yerine yaklaşık 3 saniyelik hata snackbar'ına; başarı kısa süreli başarı snackbar'ına dönüşecek.
- Doğrulama hatası için sabit yükseklik ayrılacak; state değişiminde iki ana butonun konumu oynamayacak.
- Herhangi bir loading sırasında doğrulama, mail açma ve resend aksiyonları birlikte kilitlenecek.
- Uzun email iki satırla sınırlandırılıp ellipsis uygulanacak. Mail intent'i çözülemezse hata snackbar'ı gösterilecek.
- Sistem geri tuşu doğal olarak akışın geldiği ekrana dönecek: Register kaynağı Register'a, Login kaynağı Login'e. "Yanlış email? Değiştir" aynı davranışı kullanacak; "Login'e dön" Login'e tekil geçiş yapacak.

#### Dosya Değişiklikleri

| Aksiyon | Dosya | Amaç |
|:--|:--|:--|
| Yeni | `core/auth/PendingAuthCredentialsStore.kt` | Parolayı yalnızca süreç belleğinde kısa süre tutmak ve deterministik temizlemek |
| Yeni | `ui/auth/verifyemail/VerifyEmailUiState.kt` | Email, kaynak, loading, cooldown ve inline doğrulama hata state'leri |
| Yeni | `ui/auth/verifyemail/VerifyEmailViewModel.kt` | Resend, sayaç, doğrulama sonrası login ve effect yönetimi |
| Değiştir | `ui/auth/verifyemail/VerifyEmailScreen.kt` | Figma uyumlu responsive UI, sabit hata alanı, snackbar ve mail intent'i |
| Değiştir | `ui/auth/register/RegisterViewModel.kt` ve `ui/auth/login/LoginViewModel.kt` | Verify öncesi geçici credential hazırlamak; Login açılışında taşınan email'i state'e uygulamak |
| Değiştir | `core/navigation/OmniFlowNavHost.kt` ve `Routes.kt` | Email/kaynak bilgisini `SavedStateHandle` ile taşıma, Login ön-dolumu ve doğru back-stack temizliği |
| Değiştir | `res/values/strings.xml` | Verify email metinleri, cooldown, başarı ve hata kopyaları |
| Yeni | `test/.../VerifyEmailViewModelTest.kt` | State, cooldown, 200/403/429/ağ hatası ve çift tıklama testleri |
| Yeni | `androidTest/.../VerifyEmailScreenTest.kt` | Default/error/loading/cooldown görünümü ve sabit layout testi |

> **Blast radius:** Register ve Login yalnızca geçici credential köprüsü ile etkilenir; mevcut repository/API sözleşmesi değişmez. Store soyutlaması, credential bilgisinin birden fazla ViewModel ve navigation callback'ine dağılmasını önler.

#### Uygulama Sırası

1. ViewModel unit testlerini kırmızı yazarak default state, kaynak bazlı cooldown ve API sonuç sözleşmesini sabitle.
2. Process-memory credential store ile UiState/Effect modellerini oluştur; disk ve navigation üzerinden parola taşınmadığını test et.
3. VerifyEmailViewModel içinde 60 saniyelik sayaç, resend başarı/hata ve kullanıcı tetiklemeli login akışını uygula.
4. Figma node'ları `1:731`, `1:754`, `1:778`, `1:803` temel alınarak ekranı ve tüm durumları Compose'a taşı.
5. Register/Login kaynaklarını ve back-stack davranışını NavHost'a bağla; "Login'e dön" ve process-death fallback'ini Login email ön-dolumuyla tamamla.
6. Unit, Compose, build ve fiziksel cihaz QA kapılarını çalıştır; sonuçları bu task altında kaydet.

#### Doğrulama Standartları

- [x] `VerifyEmailViewModelTest`: ilk cooldown, sayaç bitişi, resend success, `429`, ağ hatası, login `200/403` ve credential-missing fallback geçer.
- [x] `LoginViewModelTest`: Verify ekranından dönüşte email ön-dolumu ve process death sonrası parolanın geri yüklenmemesi geçer.
- [x] `VerifyEmailScreenTest`: email gösterimi, spinner/disabled durumları, sabit hata alanı ve snackbar tetikleri geçer.
- [x] `./gradlew testDebugUnitTest assembleDebug` başarılıdır.
- [x] Fiziksel cihaz manuel QA tamamlandı: cooldown, mail açma, email ön-dolum, Register/Login kaynaklı geri navigasyon ve sessiz login doğrulandı.
- [x] 393dp referansta Figma ile; dar/uzun ekran ve uzun email ile taşma olmadan görsel QA yapılır.
- [x] Mail uygulaması olan cihaz senaryosu; Register/Login kaynaklı geri navigasyon ayrı ayrı doğrulanır.

**Yapılacaklar:**
- [x] **Verify Email Info** — "**{email}** adresine doğrulama linki gönderdik" (email bir önceki ekrandan state ile taşınır)
- [x] **Mail uygulamasını aç** butonu (`ACTION_MAIN` + `CATEGORY_APP_EMAIL` intent); handler yoksa hata snackbar'ı
- [x] **Tekrar gönder** — backend ile uyumlu `60 sn` cooldown / geri sayım; başarı ve hata → kısa süreli snackbar
- [x] Spam/junk klasörü uyarısı (gri yardım metni)
- [x] **"Doğruladım, giriş yap"** butonu → process-memory credential varsa sessiz login dener: `200` → Home, `403` → rezerve inline alanda "Email henüz doğrulanmadı"; credential yoksa Login `SavedStateHandle` üzerinden email ön-dolgulu açılır. Otomatik poll YOK
- [x] **"Login'e dön"** → Login ekranı kayıt sırasında kullanılan email otomatik doldurulmuş halde açılır; parola hiçbir zaman ön-doldurulmaz veya kalıcılaştırılmaz
- [x] "Yanlış email mi? Değiştir" → geldiği Register/Login ekranına dön
- [x] ViewModel + UiState + tek seferlik Effect; tüm aksiyonlarda ortak loading kilidi

**Uygulama Notu (2026-06-24):**
- Figma node'ları `1:731`, `1:754`, `1:778`, `1:803` temel alınarak default, doğrulanmamış, resend/cooldown ve loading durumları uygulandı.
- `PendingAuthCredentialsStore` parolayı yalnızca process-memory'de tutar; email `SavedStateHandle` üzerinden taşınır. Process death sonrası Login email ön-dolgulu, parola boş açılır.
- `VerifyEmailScreen`, `VerifyEmailContent`, `VerifyEmailVisuals` ve `VerifyEmailActions` olarak düz ekran dosyalarına ayrıldı; en büyük yeni üretim dosyası 162 satırdır.
- `testDebugUnitTest`: **47 test, 0 failure, 0 error**. `assembleDebug` ve `compileDebugAndroidTestKotlin` başarılı.
- `VerifyEmailScreenTest` fiziksel Xiaomi `2312DRA50G` üzerinde başlatılmak istendi; test APK ve debug APK kurulumu cihaz tarafından `INSTALL_FAILED_USER_RESTRICTED: Install canceled by user` ile engellendi. USB üzerinden yükleme izni açıldıktan sonra cihaz UI QA tekrar çalıştırılmalı.
- Manuel QA raporu ile register → verify email → email gösterimi → resend cooldown → Login'e dönüşte email ön-dolum → doğrulama sonrası sessiz login → Home akışı bağlı telefonda başarıyla doğrulandı.

---

### Task 1.10: Forgot & Reset Password Ekranları

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ **Forgot Password tamamlandı; Reset Password deep link aşamasına ertelendi**

**Yapılacaklar:**
- [x] **Forgot Password** — email input → reset link gönder
- [ ] **Reset Password** — token + yeni şifre; geçersiz/expired token → "Link geçersiz" + Login'e dön *(mobil ekran kaldırıldı; **M10 / Task 10.7**'ye ertelendi — App Links / B4.5)*
- [x] `ForgotPasswordViewModel` + `ForgotPasswordUiState`

**Uygulama Notları:**
- Link isteme ekranı `ResetPassword` yerine işlevine uygun `ForgotPassword` adıyla ayrıldı. `Routes.ResetPassword`, gelecekte mail token'ıyla açılacak yeni şifre ekranı için rezerve edildi.
- Figma `X7EXui2QilsW4TK9GW8jey / 1:827` node'undaki default, validation error, loading ve success durumları Compose'a aktarıldı.
- Email doğrulaması Login/Register ile aynı JVM-safe regex'i kullanır; Android `Patterns.EMAIL_ADDRESS` bağımlılığı yoktur.
- Başarılı ilk gönderim ve her başarılı resend sonrası coroutine tabanlı 60 saniye cooldown yeniden başlar.
- Success ekranındaki `Mail uygulamasını aç` aksiyonu ürün kararıyla dolu mavi primary CTA'dır.
- Validation hata satırı için sabit alan ayrıldı; state değişiminde buton konumu zıplamaz. Ağ/sunucu hataları Türkçe snackbar ile gösterilir.
- Manuel QA raporu ile Forgot Password ekranında enumeration korumalı generic success, 60s cooldown, mail uygulaması CTA'sı ve reset mailinin hesaba ulaşması doğrulandı. M1 kapsamı link isteme/mail gönderimi olarak kapatıldı; token ile yeni şifre belirleme ekranı **M10 / Task 10.7** (App Links / B4.5) kapsamında kalır.

---

### Ekran Durumları (M1)

| Ekran | Loading | Empty | Error | Success |
|-------|---------|-------|-------|---------|
| Splash | Tam ekran logo + spinner | — (yok) | Token yenilenemezse → Login'e düş | Geçerli oturum → Home, yoksa → Onboarding/Login |
| Onboarding | — | — | — | 3 sayfa swipe + "Başla/Giriş/Kayıt" |
| Login | Buton içi loading | — | Yanlış kimlik → inline "Email veya şifre hatalı" (401) | Token saklanır → Home |
| Register | Buton içi loading | — | 422 → alan bazlı hata; duplicate email → inline | 202 → Verify Email Info |
| Verify Email Info | Resend buton loading | — | Resend hatası → snackbar; "Doğruladım" denemesi 403 → "Email henüz doğrulanmadı" | Email gösterimi + mail aç + geri sayımlı resend; doğrulanınca login → Home (köprü ekran, polling yok) |
| Forgot Password | Buton içi loading | — | Hata → snackbar | "Reset linki gönderildi" mesajı |
| Reset Password | Buton içi loading | — | Geçersiz/expired token → "Link geçersiz" + Login'e dön | Başarı → Login + snackbar |

### Definition of Done (M1)

- [x] Kayıt → email doğrulama bilgisi → giriş akışı uçtan uca çalışıyor
- [x] Giriş sonrası token saklanıyor, app yeniden açılınca oturum korunuyor
- [x] Onboarding bir kez gösteriliyor
- [x] Şifre sıfırlama link isteme + reset mail gönderimi çalışıyor; token ile yeni şifre ekranı M10 deep link aşamasına ertelendi

### Test (Minimal)

- [x] `LoginViewModel` unit testi (başarılı giriş, hatalı kimlik, validation)
- [x] `RegisterViewModel` unit testi (validation, checklist, 202 effect, 400 duplicate, 422 field mapping)
- [x] `VerifyEmailViewModel` unit testi + fiziksel cihaz manuel QA
- [x] `ForgotPasswordViewModel` unit testi + fiziksel cihaz manuel QA

---

## 🎯 M2 — Ana Navigasyon + Home + Profil

> **Backend:** Mevcut (users, notifications, explore/featured, media). Bağımlılık yok.

### Scope

Bottom navigation devreye girer; Home, bildirimler ve tüm profil/sosyal-kullanıcı ekranları.

---

### Task 2.1: Home Ekranı

**Tahmini Süre:** 4 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Selam** — "Merhaba {username} 👋" + sağ üstte bildirim zili (TopBar, avatar baş harf + notification dot)
- [x] **Kompakt arama çubuğu** — "Where can we take you?" → tıklanınca Explore tab'ına yönlendirir, search açık gelir
- [x] **Yaklaşan Gezi kartı** — `GET /api/v1/trips` (upcoming/active); Trip yoksa → "İlk gezini planla" CTA kartı (EmptyStateHeroCard). Birden fazla trip varsa ilk sıradaki gösterilir; swipe/dot erken iyileştirme olarak sonraya bırakıldı
- [x] **"İlham Al" destination kartları** — yuvarlak köşeli, full-photo, şehir adı; tıklama → Destination Detail. M2'de **statik liste** (hardcode 5-6 şehir); B4.6 tamamlanınca `GET /api/v1/destinations/trending` ile dinamik yapılır *(⛔ Tam dinamik: Bağımlılık B4.6)*
- [x] **Featured trips** — `GET /explore/featured` yatay carousel → Trip Detail. Boşsa bölüm gizlenir
- [x] **Topluluktan** — `GET /api/v1/feed` ilk 2 öğesi preview olarak
- [x] Floating pill bottom nav devreye girer: Home · Explore · **[+ CREATE]** · Trips · Community (raised mavi buton ortada, label yok)
- [x] `HomeViewModel` + `HomeUiState` + `HomeUiModel`

**Uygulama Notları:**
- HomeScreen UI, tasarım referansına sadık kalınarak HomeUiModel'e adapte edildi
- Tüm hardcoded renkler theme token'larına (MaterialTheme + OmniTokens) dönüştürüldü
- BottomNavPill, HomeScreen'ten NavHost seviyesine taşındı (ortak global navigation)
- 7 yeni drawable ikonu eklendi: ic_bell, ic_search, ic_filter, ic_compass, ic_add, ic_trips, ic_community
- HomeMapper (zaten mevcut): trip öncelik sıralaması, Türkçe tarih formatı, göreceli zaman, progressFraction hesaplaması
- HomeRepository 5 paralel API çağrısı yapıyor; profil başarısızsa tüm sayfa hata, bölüm bazlı hata toleransı var
- Loading → skeleton/shimmer yerine tam sayfa LoadingIndicator (sadeleştirme kararı)
- TripDetail / DestinationDetail / CreateTrip wizard navigasyonları M3'te bağlanacak

---

### Task 2.2: Notifications Ekranı

**Tahmini Süre:** 2.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Notifications** — listeleme, okundu işaretle, tümünü okundu, unread badge (`/notifications`, `/notifications/unread-count`)
- [x] Tarihe göre gruplu liste + unread vurgusu + pagination footer
- [x] ViewModel + UiState

**Uygulama Notları:**
- NotificationsScreen UI, tasarım referansına sadık kalınarak tokenize edildi (tüm hardcoded renkler MaterialTheme/OmniTokens/NotificationPalette'e taşındı)
- BottomNavPill NotificationsScreen'ten çıkarıldı, NavHost seviyesindeki ortak BottomNavPill kullanılıyor
- Notifications route `bottomBarRoutes`'tan çıkarıldı — zil ikonundan açılıyor, bottom bar yok
- Data layer: `NotificationService` (4 Retrofit endpoint), `NotificationRepository` (interface + impl), DTO → Model mapper
- `NotificationsViewModel`: client-side filtreleme (ALL/SOCIAL/TRIP/REMINDER), select mode (long-press), mark-as-read, mark-all-read, mark-read-selected
- Backend `NotificationType` → UI `NotifTypeUi` mapping: Follow→FOLLOW, Comment/Mention→COMMENT, PostUpvote→LIKE, TripUpvote/CommentUpvote/TipUpvote→UPVOTE, Fork→FORK
- "Hatırlatıcı" (REMINDER) filtre çipi tutuldu; backend'de henüz yok, seçilince empty state gösterir
- Select mode'daki "Sil" butonu işlevsiz (no-op) — backend'de delete notification endpoint yok, BACKEND_ROADMAP_V2.md'ye not eklendi
- 3 Preview: Normal, Select Mode, Empty
- Login debug credentials LoginUiState'ten LoginViewModel init'ine taşındı (test uyumluluğu için)

---

### Task 2.3: My Profile Ekranı

**Tahmini Süre:** 2 saat
**Durum:** ✅ Tamamlandı (Task 2.4 ile birlikte)

**Yapılacaklar:**
- [x] **My Profile** — profil bilgisi, karma, followers/following sayıları, kendi postları/trip'leri (`/users/me`, `/users/me/posts`, `/users/{id}/trips`)
- [x] Post/trip yoksa sekme içi "Henüz paylaşım yok"
- [x] ViewModel + UiState
- [x] **Edit Profile** — profil fotoğrafı, bio, konum, seyahat stili (`PUT /api/v1/users/me`, `POST /api/v1/users/me/profile-photo`)
- [x] **Kullanıcı adı** read-only gösterilir (🔒), düzenlenemez
- [x] **Bio** — çok satırlı text field, max 150 karakter, sayaç
- [x] **Seyahat Stili** — multi-select chip'ler; seçili = mavi, seçilmemiş = outline
- [x] Foto yükleme placeholder + [Kaydet] butonu
- [x] Tüm hardcoded renkler ve dp/sp tokenize edildi

**Uygulama Notları:**
- Task 2.3 ve 2.4 birlikte implemente edildi (3 varyant: VIEW/EMPTY/EDIT)
- ProfileScreen UI, tasarım referansına sadık kalınarak tokenize edildi
- BottomNavPill ProfileScreen'ten çıkarıldı, NavHost seviyesindeki ortak BottomNavPill kullanılıyor
- Profil route `bottomBarRoutes`'ta değil — avatar ikonundan açılıyor
- Data layer: `ProfileService` (5 Retrofit endpoint), `ProfileRepository` (interface + impl), paralel API çağrıları
- `ProfileViewModel`: tab switching (ALL/TRIPS/POSTS), edit mode, bio save, photo placeholder
- Konum ve Seyahat Stili alanları UI'da gösteriliyor, kaydetme no-op (backend B0.5 bekliyor)
- Profil fotoğrafı: URL varsa AsyncImage, yoksa baş harf gradient avatar
- Mock veri: backend boş dönerse/hata verirse "(Mock)" etiketli örnek profil gösterilir
- 3 Preview: WITH_CONTENT, EMPTY, EDIT
- 29 drawable ikonunun tamamı mevcut — eksik ikon yok
- ProfileDimens.kt: 60+ boyut token'ı, ProfilePalette.kt: avatar/kart gradientleri + renk paletleri

---

### Task 2.4: Edit Profile + Foto Yükleme

**Tahmini Süre:** 2.5 saat
**Durum:** ✅ Tamamlandı (Backend B0.5 bağımlılığı nedeniyle kaydetme no-op bağlandı)

> ⛔ **Bağımlılık: B0.5** (Konum + Seyahat Stili alanları için backend hazır olmalı)

**Yapılacaklar:**
- [x] **Edit Profile** — profil fotoğrafı, bio, konum, seyahat stili (`PUT /api/v1/users/me`, `POST /api/v1/users/me/profile-photo`)
- [x] **Kullanıcı adı** read-only gösterilir (🔒), düzenlenemez
- [x] **Konum** — tek satır text field (ör. "İstanbul, Türkiye")
- [x] **Seyahat Stili** — multi-select chip'ler: `Macera · Kültür · Sahil · Şehir · Doğa · Gastronomi`; seçili = mavi, seçilmemiş = outline
- [x] Foto yükleme sırasında avatar üzerinde loading overlay; başarıda snackbar
- [x] `[Kaydet]` butonu değişiklik yokken disabled
- [x] ViewModel + UiState

---

### Task 2.5: Public User Profile + Follow/Block

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Public User Profile** — başka kullanıcı (`/users/{username}`) + Follow/Unfollow/Block
- [x] Engellenmiş kullanıcı → metrikler sıfır/gizli
- [x] Follow toggle optimistic
- [x] ViewModel + UiState

---

### Task 2.6: Followers / Following

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Followers / Following** — liste + search
- [x] Search sonucu boşsa "Sonuç yok"

---

### Task 2.7: Suggested Follows + Top Contributors

**Tahmini Süre:** 1 saat
**Durum:** ✅ Tamamlandı

> **Entry point kararı:** Community tab (👥) M2'de bu ekranı doğrudan gösterir — M5'te tam Community Feed yapılana kadar placeholder görevi görür. M5 tamamlanınca Community tab'ının içine "Gezginler" alt bölümü olarak taşınır.

**Yapılacaklar:**
- [x] **Community tab** → M2'de doğrudan bu ekrana yönlenir (NavHost'ta Community route = GezginleriKesfet)
- [x] **Suggested Follows** — yatay scroll kart listesi (avatar + şehir + karma + "Takip Et")
- [x] **Top Contributors** — sıralı dikey liste, #1 altın sol border, madalya/numara rozeti
- [x] "Takip Et" optimistic toggle (takip ediyorsun / takip et)
- [x] M5'te Community Feed gelince bu ekran sub-screen'e düşer; route değişikliği o milestone'da yapılır

---

### Task 2.8: Settings Shell

**Tahmini Süre:** 0.5 saat
**Durum:** ✅ Tamamlandı

**Yapılacaklar:**
- [x] **Settings (shell)** — logout + alt ayar girişleri (içerikler ileride dolacak)
- [x] Logout → oturum temizle → Login

**Uygulama Notu:**
- **5 yeni dosya**: `SettingsPalette.kt`, `SettingsDimens.kt`, `SettingsUiState.kt` (data modelleri + UiState), `SettingsViewModel.kt` (TokenManager.clearSession → Login), `SettingsScreen.kt` (tokenize).
- **OmniTextStyles.settingsSectionTitle** (12.sp SemiBold) eklendi.
- **Routes.Settings** (`"settings"`) eklendi; NavHost'ta `composable(Routes.Settings.route)` bağlandı. BottomNavPill Settings'te gösterilmez (`bottomBarRoutes` içinde değil).
- **ProfileScreen** `onSettingsTap` → `navController.navigate(Routes.Settings.route)` olarak bağlandı (daha önce TODO'ydı).
- **Logout akışı**: ViewModel `onLogout()` → `tokenStore.clearSession()` → `loggedOut=true` → `LaunchedEffect` ile `Routes.Login` (Home inclusive popUpTo) navigasyonu. Loading sırasında `CircularProgressIndicator` gösterilir.
- **Tokenizasyon**: 8 inline `Color(0x…)` → `SettingsPalette`; ~20 inline `.dp` → `SettingsDimens`; tüm `fontSize =` → `MaterialTheme.typography.*` / `OmniTextStyles.*`. `.copy(fontWeight = …)` sadece fontWeight için kullanıldı (lint güvenli).
- **Data modelleri** (`SettingRowType`, `SettingRow`, `SettingGroup`, `defaultSettingGroups`) `SettingsUiState.kt`'e taşındı; 4 grup (Hesap, Uygulama, Gizlilik & Güvenlik, Destek) korundu.
- **Alt ekranlar** (Hesap Bilgileri, Şifre Değiştir, vb.) şu an placeholder; ilgili milestone'larda dolacak.
- `compileDebugKotlin`, `lintDebug`, `installDebug` başarılı; APK Xiaomi `2312DRA50G` cihazda.

---

### Ekran Durumları (M2)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Home | Aktif trip yoksa → "Bir trip planla" CTA kartı | Aktif trip kartı + quick actions + featured | Featured boşsa o bölüm gizlenir |
| Notifications | "Henüz bildirimin yok" | Tarihe göre gruplu liste + unread vurgusu | Pagination footer |
| My Profile | Post/trip yoksa sekme içi "Henüz paylaşım yok" | Profil başlığı + karma + sekmeler (postlar/trip'ler) | — |
| Edit Profile | — | Form (bio + foto) | Foto yükleme sırasında foto alanında loading; başarıda snackbar |
| Public Profile | İçerik yoksa "Henüz public içerik yok" | Profil + Follow/Unfollow/Block | Engellenmiş kullanıcı → metrikler sıfır/gizli |
| Followers / Following | "Henüz takipçi/takip yok" | Liste + search | Search sonucu boşsa "Sonuç yok" |
| Suggested / Top Contributors | "Şu an öneri yok" | Sıralı kullanıcı listesi | — |
| Settings | — | Ayar girişleri + logout | Logout → oturum temizle → Login |

### Definition of Done (M2)

- [ ] 5 sekmeli bottom nav çalışıyor
- [ ] Home'dan trip/explore/saved'e geçişler çalışıyor
- [ ] Kendi ve başka kullanıcının profili görüntülenebiliyor, follow/unfollow çalışıyor
- [ ] Profil düzenleme + foto yükleme çalışıyor

### Test (Minimal)

- [ ] `ProfileViewModel` unit testi (profil yükleme + follow toggle)

---

## 🎯 M3 — Trips (Wizard, Detail, Timeline, Budget)

> **Backend:** Mevcut (trips, wizard, destinations, timeline, budget-summary, recommend-places, saved-trips). Bağımlılık yok.

### Scope

Uygulamanın kalbi. Trip listesi, detay, 8 adımlı oluşturma wizard'ı, destinasyon yönetimi, timeline ve bütçe.

---

### Task 3.1: My Trips Listesi

**Tahmini Süre:** 2.5 saat
**Durum:** ✅ Tamamlandı

> ⛔ **Bağımlılık: B0.6** (Trip %hazır), **B0.7** (Görüntülenme), **B0.8** (Kapak fotoğrafı)

**Yapılacaklar:**
> **Sekme kararı:** `Taslak | Yayında | Kaydedilenler` — 3 sekme. "Arşiv" ayrı sekme değil; arşivlenmiş trip'ler "Yayında" sekmesinde `Arşiv` badge'iyle gösterilir.
>
> ⛔ **Bağımlılık (Kaydedilenler sekmesi — Collections):** `BACKEND_ROADMAP_V2.md → B4.1`

- [x] **My Trips** — `Taslak / Yayında / Kaydedilenler` sekmeleri (`GET /api/v1/Trips?status=` + `GET /api/v1/saved-trips`)
- [x] **Trip kartı** — renk gradyanı placeholder (kapak fotoğrafı yoksa); tripId hash'ine göre deterministik gradyan
- [x] **Kart navigasyonu** — karta tıklamak Trip Detail'e gider (TODO Task 3.2)
- [x] **Draft kart**: `Taslak` badge + `%{progressPercent} hazır` (mock); alt satır: kişi sayısı · destinasyon sayısı
- [x] **Published kart**: `X gün kaldı` (yeşil) veya `Tamamlandı` (gri) badge; sağ üst rating · fork; alt satır: kişi · destinasyon · gün
- [x] **Archived kart** (Yayında sekmesinde): `Arşiv` badge (gri), aynı alt satır
- [x] **Yayın Özeti kartı** — Published sekmesi altında aggregate stats (ViewModel'de canlı hesaplanır)
- [x] **Kaydedilenler sekmesi** — grid, filtre chip'leri (local mock collections), `+` butonu ile koleksiyon ekleme
- [x] **Draft aksiyon butonları** — Düzenle / Yayınla (publish loading state ile)
- [x] `MyTripsViewModel` + `MyTripsUiState` + `TripsMapper`

**Uygulama Notu:**
- **13 yeni dosya**: `TripDtos.kt`, `TripModels.kt`, `TripMappers.kt` (data/mapper), `TripService.kt`, `TripRepository.kt`, `TripRepositoryImpl.kt` (mock fallback), `TripsModule.kt` (Hilt), `TripsPalette.kt`, `TripsDimens.kt`, `MyTripsUiState.kt`, `MyTripsGradients.kt`, `TripsMapper.kt` (ui), `MyTripsViewModel.kt`.
- **OmniTextStyles yeni**: `tripCardTitle` (20.sp Bold), `tripSavedTitle` (13.5.sp Bold), `tripSavedUser` (10.5.sp SemiBold), `tripFilterChip` (12.5.sp SemiBold), `tripMetaSmall` (10.sp Normal).
- **Gradient**: 8 renk set'i, tripId hash'ine göre deterministik seçim (`gradientForId(id)`).
- **PublishSummary**: ViewModel'de published trip'lerden hesaplanır; rota sayısı, çatallanma, ortalama puan.
- **Collections**: Local/mock liste (Tümü, Avrupa, Yaz 2026). Backend B4.1 hazır olunca canlı API'ye geçilir.
- **Mock fallback**: `getMyTrips` ve `getSavedTrips` API hatasında mock veri döner; 2 draft + 2 published/archived + 2 saved.
- `compileDebugKotlin`, `lintDebug`, `installDebug` başarılı.
- [x] **Kart navigasyonu** — karta tıklamak Trip Detail'e gider; kart üzerinde hiçbir aksiyon butonu yok
- [x] **Draft kart**: `🟡 Taslak` badge + `%{CompletionPercentage} hazır` *(⛔ B0.6)*; alt satır: 👤 · 📍; tarih yoksa "Tarih belirlenmedi"
- [x] **Published kart**: `🟢 X gün kaldı` (yeşil) veya `⚫ Tamamlandı` (gri) badge; sağ üst ❤️{UpvoteCount} · 🔀{ForkCount}; alt satır: 👤 · 📍 · gün sayısı
- [x] **Archived kart** (Yayında sekmesinde, Published kartlarla karışık): `⚫ Arşiv` badge (gri), aynı alt satır
- [x] **Yayın Özeti kartı** — Yayında sekmesi üstünde aggregate stats: Rota · Görüntülenme · Beğeni · Çatallanma *(⛔ B0.7)*
- [x] **Kaydedilenler sekmesi** — `GET /api/v1/saved-trips`; kart: kapak + trip adı + @username + ❤️ · 🔀; yenileme = sunucu gerçeği (optimistic yok)
- [x] Sekme bazlı boş durum: Taslak → "İlk gezini planla" + Wizard CTA · Yayında → "Yayınlanmış gezi yok" · Kaydedilenler → "Kaydettiğin gezi yok" + Keşfet CTA
- [x] Pagination footer
- [x] `MyTripsViewModel` + `MyTripsUiState` + `TripCardUiModel`

---

### Task 3.2: Trip Detail Görünümü

**Tahmini Süre:** ~21 saat (aşağıdaki alt görevlerin toplamı)
**Durum:** [ ] Bekliyor — sıfırdan yeniden tasarlandı, alt görevlere bölündü

> **Tek kaynak:** Tüm tasarım kararları, tam ölçüler, API contract'ları ve edge case'ler için **`omniflow-mobile/TRIP_DETAILS_PAGE.md`**. Aşağıdaki her alt görev o dokümanın ilgili bölümüne referans verir. Önceki mimari (Gün sekmeleri + Kategori/Gün toggle + blur-modal, eski Task 3.2.1–3.2.7) kullanıcının vizyonunu yansıtmadığı için **tamamen terk edildi**; kod tarafında `TripDetailScreen.kt` + `TripDetailViewModel.kt` hâlâ eski implementasyonu içeriyor, aşağıdaki alt görevler bunu rework edecek.
>
> **Backend bağımlılıkları (tüm alt görevler için ortak havuz, her birinde ayrıca belirtilir):** B0.9 (Checklist Confirmation), B0.10 (🔴 Güvenlik — visibility helper), B0.11 (Unarchive), B0.12 (Geocoding), B0.13 (PlanningSlotKey), B0.14 (🔴 Unpublish), B0.15 (ORS Proxy) — hepsi `BACKEND_ROADMAP_V2.md`'de.
>
> ⚠️ **Sıralama düzeltmesi (Map bağımlılığı):** `MapLibre Compose Kurulumu` daha önce M8/Task 8.1'de planlıydı, bu task'a (3.2.4) taşındı — M8/Task 8.1 artık sadece Live Trip'e özel GPS katmanı ekliyor.
>
> ⚠️ **B4.1 (Collections) — bloklayıcı değil:** Kaydet bottom sheet'i local mock collections kullanır (Task 3.1 ile aynı çözüm).
> ⚠️ **B4.3 (Share metadata) / B5.1 (Report) — bloklayıcı değil:** Paylaş M3'te aktif (zengin önizleme olmadan), Şikayet Et M3'te disabled.

---

#### Task 3.2.1: Sabit Üst Bar

**Tahmini Süre:** 2 saat
**Durum:** [x] Tamamlandı — `TripDetailScreen.kt` package düzeltildi (`com.omniflow.ui.trips`), gerçek `TripDetailViewModel`/`TripDetailUiState`'e bağlandı. `unpublishTrip`/`unarchiveTrip` gerçek `@POST` endpoint olarak eklendi (B0.11/B0.14 backend'i henüz yok, mock fallback KULLANILMADI — hata durumunda gerçek `ApiResult.Error` döner).

> Referans: `TRIP_DETAILS_PAGE.md → Sabit Üst Bar`
> ⛔ Bağımlılık: B0.11, B0.14 (durum bazlı menü içeriği) — mobil taraf backend'i beklemeden tamamlandı, gerçek endpoint entegrasyonu backend hazır olunca test edilecek

- [x] `←` geri + başlık (ellipsis, `maxLines=1`) + sağda 2 ikon (Owner: `✏️`+`⋮`, Misafir: `❤️`+`⋮`)
- [x] Owner `⋮` menüsü **durum bazlı**: Draft→Yayınla·Sil; Published→Arşivle·Düzenlemek için Taslağa Al·Paylaş·Sil; Archived→Yayına Al·Sil
- [x] `✏️ Edit` sadece Draft'ta aktif, Published/Archived'da disabled/gri
- [x] Bu butonlar üst barda, gövdeden ayrı sabit bir Row'da yaşıyor (resizable pane altyapısı henüz yok — Task 3.2.2)
- [ ] Misafir menüsündeki `🔖 Kaydet` şu an basit toggle (collection-picker bottom sheet henüz yok — Task 3.3.2/B4.1'e bırakıldı)
- [ ] Anonim kullanıcı + auth-gerektiren aksiyon (login-required uyarısı) henüz eklenmedi — Task 3.3.2'ye bırakıldı

---

#### Task 3.2.2: Resizable Pane Altyapısı (Portrait)

**Tahmini Süre:** 3 saat
**Durum:** ✅ Tamamlandı

> Referans: `TRIP_DETAILS_PAGE.md → Genel Sayfa Yapısı / Bölüm Boyutları ve Handle'lar`
> Not: Bu görev, `TripDetailScreen.kt`'deki mevcut tek-parça `TripDetailBody`'yi (Task 3.2.1'de bilinçli olarak minimal/tek-scroll bırakılmıştı) Detaylar/Map/Timeline 3 ayrı pane + 2 handle olarak yeniden yazacak.

- [x] `%100` referans alanı = ekran yüksekliği − 56dp üst bar − bottom nav − `WindowInsets` (`Modifier.weight` ile otomatik — manuel piksel hesabı yok)
- [x] Drag state: local Compose state (`remember { mutableStateOf }`) — sürüklerken anlık/60fps; `TripDetailUiState`'e (ViewModel'e) **sadece drag-end/tap-snap anında commit edilir** (`onPaneResize` → ViewModel → Room)
- [x] 2 handle (Detaylar↔Map, Map↔Timeline) — min/maks/default: Detaylar 0-30(30), Map 0-40(30), Timeline türetilen(40) — `DETAYLAR_MAX=0.30f`, `MAP_MAX=0.40f`, default'lar `0.30f/0.30f`
- [x] Drag: tek `pointerInput` + `awaitEachGesture` (tap+drag çakışması yok), `onSizeChanged` ile `totalHeightPx` ölçümü → `positionChange().y / totalHeightPx` ile fraksiyonel delta
- [x] Tek dokunuş: drag yoksa `onTap` → default orana snap; zaten default'taysa (`liveDetaylar != DETAYLAR_DEFAULT || liveMap != MAP_DEFAULT` karşılaştırması) no-op + haptic yok
- [x] Cascade kuralı (simetrik) — `applyHandle1Drag` (Detaylar büyür, Map geriler) ve `applyHandle2Drag` (Map'in own clamp'i → overflow Detaylar'a kaskad) — Timeline hiç mutate edilmez, `1f - detaylar - map` türetilen, invariant float drift birikmez
- [x] Performans: `Modifier.weight(fraction)` ile layout yerleşimi (manuel piksel/yükseklik hesabı yok), drag-commit sadece drag-end'de
- [x] Handle görsel: 32dp×4dp drag-indicator çizgisi, 48dp sabit yükseklikli Column sibling (komşu pane weight küçülse de handle satırı korunur) — gerçek MapLibre `AndroidView` entegrasyonu/gesture-tuning Task 3.2.4'e bırakıldı
- [x] Tampon boşluk: Map pane içinde `padding(vertical = 24.dp)` → handle hit-area ile Map render sınırı arası yapısal buffer (native-View tuning 3.2.4)
- [x] Handle görsel crossfade: `Crossfade` + `LaunchedEffect(isInteracting) { delay(1000); showIdleLabel = true }` — idle → isim, dokunma → çizgi, parmak kalkınca ~1sn gecikmeyle isme dönüş
- [x] %0 edge case: `coerceAtLeast(EPSILON=0.0001f)` ile weight floor; `if (liveX > epsilon)` ile içerik render — görsel %0, layout geçerli, handle 48dp hit-area her zaman erişilebilir
- [x] Haptic feedback: `LocalHapticFeedback.current` — (a) tap-to-snap anında, (b) drag sırasında pane >0'dan ≤0'a geçince (edge dedeksiyon `prevDetaylar`/`prevMap` ile tek seferlik LongPress)
- [x] Timeline scroll: `rememberScrollState()` aynı composable seviyesinde — resize sırasında Column yeniden yaratılmaz, sadece height/weight değişir, scroll pozisyonu korunur
- [x] Trip-bazlı, cihaz-yerel kalıcılık — Room DB: `TripPanePreferencesEntity` (`tripId` PK, `detaylarFraction`, `mapFraction`, `displayMode`, `selectedDayIndex` — Timeline saklanmaz, türetilen), `TripPanePreferencesDao` (`@Upsert` + `get`), `OmniFlowDatabase` version 1→2 + `.fallbackToDestructiveMigration()`, `TripPanePreferencesRepository` + Impl (Entity↔domain izolasyonu). **Landscape alanları dahil edilmedi** (Task 3.2.8'de ayrı migration)

**Ek bug-fix (loadTripDetail):** Mevcut `TripDetailViewModel.loadTripDetail()` her çağrıda (owner aksiyonlarından sonra `runAction()` içinden de tetikleniyor) `_uiState`'i mapper'ın taze/default sonucuyla komple değiştiriyordu — bu `displayMode`/`selectedDayIndex`'i (ve eklenecek pane oranlarını) sessizce sıfırlıyordu. Düzeltildi: mapper sonucu `fresh.copy(...)` ile mevcut tercihler (detaylar/map/displayMode/selectedDayIndex) korunarak uygulanıyor.

**Yapılan dosya değişiklikleri:**
- Yeni: `data/local/TripPanePreferencesEntity.kt`, `data/local/dao/TripPanePreferencesDao.kt`, `data/repository/TripPanePreferencesRepository.kt`
- Değiştirilen: `data/local/OmniFlowDatabase.kt` (v2 + DAO), `core/di/DatabaseModule.kt` (fallbackToDestructiveMigration + DAO provider), `core/di/TripsModule.kt` (@Binds), `ui/trips/TripDetailUiState.kt` (2 alan + timelineFraction), `ui/trips/TripDetailViewModel.kt` (repo injekt, bug fix, load/save/persist), `ui/trips/TripDetailScreen.kt` (TripDetailBody → TripDetailPanes + ResizeHandle), `core/navigation/OmniFlowNavHost.kt` (onPaneResize callback)

**Doğrulama:** `./gradlew :app:compileDebugKotlin --rerun-tasks` başarılı (KSP Room dahil). Sadece mevcut `Divider` deprecated uyarıları (bu değişiklik dışında). Manuel QA planı (12 madde)spec'te listelendi — drag + tap + cascade + haptic + crossfade timing + force-stop persistence + bug-fix regression + landscape crash yok.

---

#### Task 3.2.3: Detaylar Bölümü

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

> Referans: `TRIP_DETAILS_PAGE.md → Detaylar Bölümünün İçeriği / İçerik Adaptasyonu`

- [x] Tam açık içerik: kapak fotoğrafı (gerçek `coverPhotoUrl` — Coil `AsyncImage` + gradyan null-fallback, HomeScreen pattern'iyle birebir), status badge (3 renk — `statusColor()`: Draft→Warning amber, Published→Success yeşil, Archived→TextSecondary gri), tarih aralığı, başlık, ülke+kişi sayısı, ❤️/🔀 sayıları (salt-okunur)
- [x] Küçülürken kademeli kaybolma — **tek eşik** (`DETAYLAR_COLLAPSE_THRESHOLD = 0.20f`): %20'nin altına inince badge/tarih/ülke/kişi/❤️/🔀 hepsi birlikte kaybolur, başlık `EPSILON`'a kadar kalmaya devam eder
- [x] %0'da tamamen kapanır — başlık da dahil (`if (uiState.detaylarFraction > EPSILON)`)
- [x] Layout: `Box` + `align(TopStart)`/`align(BottomStart)` (badge/tarih üstte, başlık/özet altta — referans mockup'la tutarlı), scrim bindirme (foto ve gradyan fallback üzerinde metin her zaman beyaz)

**Ek bug-fix (status renk):** Mevcut `StatusPill` Draft trip'ler de dahil hepsinde `Success` yeşil gösteriyordu. `TripDetailPalette`'daki amber tonla aynı hex (`Color(0xFFF59E0B)`) `OmniColor.Warning` olarak eklendi, 3 yollu `statusColor(status)` fonksiyonu yazıldı.

**Ek temizlik (Timeline kopya satır):** TimelinePane'in en üstünde tekrar gösterilen status/tarih/ülke/kişi/❤️/🔀 satırı kaldırıldı — bu iş Detaylar'ın, Timeline sadece Toplam Bütçe (3.2.5) + kategori kartları gösterir (spec). Kaldırılan yerde kısa yorumla not düşüldü.

**Yapılan dosya değişiklikleri:**
- Değiştirilen: `ui/trips/TripDetailUiState.kt` (`coverPhotoUrl: String? = null`), `ui/trips/TripDetailMapper.kt` (`coverPhotoUrl = coverPhotoUrl`), `ui/trips/TripDetailScreen.kt` (`OmniColor.Warning` + `DetaylarPane` [yeni, `DetaylarPanePlaceholder` yerine] + `statusColor()` + `DETAYLAR_COLLAPSE_THRESHOLD` + TimelinePane kopya sil + `coil.compose.AsyncImage`/`ContentScale` import'ları)

**Doğrulama:** `./gradlew :app:compileDebugKotlin` başarılı (yeni import'lar dahil). Sadece mevcut `Divider` deprecated uyarıları.

---

#### Task 3.2.4: Map Bölümü + MapLibre Kurulumu

**Tahmini Süre:** 3 saat
**Durum:** [x] Tamamlandı — MapLibre native Android SDK ile kuruldu (`org.maplibre.gl:android-sdk` v11.8.0), OpenFreeMap `liberty` stili entegre edildi. Mock şehir koordinatları (`MockCityCoordinates`) ile pinler ve Kuş Bakışı rotası pürüzsüz çizildi. `GET /api/v1/Trips/{id}/route` endpoint'i gerçek olarak bağlandı; ORS hatası durumunda sessizce Kuş Bakışı'na fallback ve toggle pasifleştirme lojiği doğrulandı. Tam ekran modu floating `← Geri` ve dynamic kamera kadrajı ile tamamlandı.

> Referans: `TRIP_DETAILS_PAGE.md → Map — 2 Mod`
> ⛔ Bağımlılık: B0.12 (koordinat), B0.15 (ORS proxy)

**M3 Map Kapsamı:**

| Kapsam | M3'te var mı | Not |
|---|---|---|
| MapLibre Compose kurulumu (OpenFreeMap tile) | ✅ Evet | M8'den taşındı |
| Destinasyon pinleri | ✅ Evet | `TripDestination` koordinatlarından (B0.12) |
| **Kuş Bakışı** modu | ✅ Evet | ORS'a bağımlı değil, varsayılan/güvenli mod |
| **Yol** modu (ORS) | ✅ Evet, best-effort | Backend proxy'den (B0.15), mobil ORS'u hiç çağırmaz |
| Tam Ekran Harita Modu + floating card | ✅ Evet | Statik, GPS gerektirmez |
| Konum izni / canlı GPS | ❌ Hayır | M8'e ait |
| Offline davranışı | ❌ Kapsam dışı | Boş/gri harita + placeholder yeterli |

- [x] MapLibre Compose bağımlılığı + OpenFreeMap tile style kurulumu
- [x] Pinler, `[Kuş Bakışı | Yol]` toggle, `⛶` büyüt ikonu
- [x] Null koordinatlı destinasyon → pin ve rota çiziminden atlanır, bir sonraki geçerliye direkt bağlanır
- [x] ORS proxy çağrısı (`GET /api/v1/Trips/{id}/route`), hata → sessizce Kuş Bakışı'na fallback
- [x] Tam Ekran Harita Modu: floating `← Geri` + toggle + draggable Timeline card (varsayılan sol alt, `✕ Gizle`/`☰` geri aç)

> **Sonradan iyileştirme (Rota kartı):** "📅 Rota" kartı artık başlık satırından tutup ekranın her yerine sürüklenebiliyor (`detectDragGestures`, sınır içinde `coerceIn`). "✕ Gizle" kaldırıldı — yerine `ic_chevron_down` ikonu geldi, karta hiç dokunmuyor sadece tek satıra küçültüyor (kart hiçbir zaman tamamen kaybolmuyor). Gün satırına dokununca o günün saat/ikon/başlık listesi (`dayEntries` filtrelenerek) accordion şeklinde altında açılıyor; bir entry'ye dokununca mevcut Detay Modal açılıyor.

---

#### Task 3.2.5: Timeline — Review Modu

**Tahmini Süre:** 3.5 saat
**Durum:** [x] Tamamlandı — `buildCategoryCards()` gerçek mantığa göre yeniden yazıldı (Flights=leg bazlı `origin+destinationList` ardışık geçişleri, Hotels=destinasyon başına sabit 2 gece varsayımı, Mekan=sabit "Moderate≈5/gün" + PlaceCategory alt grup). Toplam Bütçe satırı (mock veri, tripId hash bazlı) + progress ring (coin-flip, sadece kart expanded'ken aktif) + checklist toggle (Flights/Hotels manuel, Mekan otomatik/salt-okunur) eklendi.

> Referans: `TRIP_DETAILS_PAGE.md → Timeline Bölümü / Review Modu`
> ⛔ Bağımlılık: B0.9 (checklist confirmation) — mobil taraf backend'i beklemeden tamamlandı

- [x] Toplam Bütçe satırı (tam genişlik, herkese açık — anonim dahil, mock veri) + Review/Gün gün toggle (ayrı satırda, altında)
- [x] Flights/Hotels/Mekan kategori kartları — progress ring (coin-flip animasyonu sadece kart expanded'ken aktif; gerçek viewport-görünürlük tespiti kapsam dışı bırakıldı, `LazyColumn`'a geçiş gerektirirdi)
- [x] Ring renk kuralı: <%100 mavi, =%100 yeşil, >%100 kırmızı
- [x] Checklist satırları (Flights/Hotels manuel toggle, Mekan otomatik/salt-okunur) — `itemKey` formatı + `PUT /checklist/{itemKey}` gerçek endpoint (mock fallback yok)
- [x] Kart expand: wrap-content, kendi scroll'u yok (nested scroll conflict önlenir)
- [x] Misafir: checklist salt-okunur (görür, işaretleyemez — `Checkbox(enabled = isOwner)`)

**Bilinen mock/stand-in'ler (backend eksikliğinden):**
- `itemKey`'ler gerçek `TripDestination.Id` GUID yerine şehir adı bazlı (`flight-leg:{fromCity}:{toCity}`, `hotel-night:{city}:{night}`) — B0.13 gelince güncellenecek.
- Hotel gece sayısı sabit 2/destinasyon (gerçek tarih aralığı yok); Mekan beklenen sayısı sabit "Moderate≈5/gün" (Wizard Tempo alanı henüz mobile akmıyor).
- Checklist `PUT` çağrısı backend olmadığı için her zaman hata dönüyor — **kullanıcı kararıyla bilerek geri alınmıyor** (revert yok), toggle lokalde kalıcı görünür. B0.9 gelince upvote/save'deki optimistic+revert-on-error pattern'ine geçilecek.
- Toplam Bütçe tamamen mock (`tripId.hashCode()` bazlı) — gerçek `budget-summary` endpoint'i Task 3.2.9'da bağlanacak.

**Doğrulama:** `./gradlew :app:compileDebugKotlin` başarılı (sadece önceden var olan `Divider`/MapLibre/`LocalLifecycleOwner` deprecation uyarıları).

---

#### Task 3.2.6: Timeline — Gün gün Modu

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

> Referans: `TRIP_DETAILS_PAGE.md → Timeline Bölümü / Gün gün Modu`

- [x] Gün sekmeleri — yatay scroll (horizontalScroll), "Gün N" kısa metin (day.index'ten türetilir), mevcut SegmentPill/MapModePill ile tutarlı pill görsel dili
- [x] Seçili günün kronolojik listesi — saat + düz bağlantı çizgisi (Box width(1.5.dp).weight(1f).background(Border)) + ikon daire (28dp CircleShape, IconContainer + Primary ikon) + entry adı (tek satır), son entry'de çizgi yok
- [x] Zaman gösterimi: entry.time dönüşümsüz (girildiği gibi, B2.4 gelene kadar)
- [x] Boş gün mesajı: "Bu gün için henüz bir kayıt yok" (dayItems.isEmpty() durumu)
- [x] Entry'ye dokunma → mevcut detay dialogu (onEntryDetailClick), gerçek Detay Modal Task 3.2.7'ye bırakıldı

---

#### Task 3.2.7: Detay Modal

**Tahmini Süre:** 3 saat
**Durum:** ✅ Tamamlandı

> Referans: `TRIP_DETAILS_PAGE.md → Detay Modal — İçerik`
> ⛔ Bağımlılık: B0.13 (PlanningSlotKey, exact match), B0.14 (Draft-only gate) — mock stand-in'lerle bypass edildi

- [x] Durum A (gerçek entry): FlightCardContent (rota/tarih/süre/fiyat boarding-pass kartı), HotelCardContent (giriş/gece/fiyat), SimpleEntryContent (Mekan: ikon+başlık+bilgi, düz görünüm)
- [x] Durum B (bağlı entry yok): EmptyLegContent — boş durum mesajı + owner+Draft'a `+ Detay Ekle`, Misafir'e sadece mesaj + `🔀 Fork`
- [x] HorizontalPager ile aynı gün+kategorideki diğer entry'lere swipe
- [x] Locked entry (isLocked=true): salt-okunur alanlar + `🔓 Kilidi Aç` butonu (canMutate=Draft-only), Sil gizli; unlock sonrası (isLocked=false) ✏️ Edit + Sil aktif
- [x] Owner + Draft değilse (Published/Archived) Edit/Kilidi Aç/Sil disabled (gri)
- [x] Entry silme (onDeleteEntry): hasLinkedEntry=false yapar (Durum A→B geçişi), aynı satırın checklist isConfirmed durumu DEĞİŞMEZ, modal kapanır
- [x] mock: CategoryEntry'e hasLinkedEntry/isLocked/price/durationLabel/category alanları eklendi; Flight'ların ~%67'si, Hotel'lerin ilk gecesi Durum A
- [x] Backend endpoint'leri: unlockTimelineEntry (PUT), deleteTimelineEntry (DELETE) — gerçek çağrı, mock fallback yok; B0.14 gelene kadar hata sessiz yutulur
- [x] CategoryEntry modeli genişletildi (5 yeni alan, hepsi default değerli — mevcut kod kırılmaz)
- [x] UnlockEntryDto (TripDtos.kt), TripService/TripRepository/Impl, ViewModel (onUnlockEntry/onDeleteEntry/updateEntry helper)
- [x] OmniFlowNavHost: onUnlockEntry/onDeleteEntry ViewModel'e bağlandı, onEditEntryClick/onAddDetailClick TODO no-op

---

#### Task 3.2.8: Yatay Mod (Landscape)

**Tahmini Süre:** 2 saat
**Durum:** ✅ Tamamlandı

> Referans: `TRIP_DETAILS_PAGE.md → Yatay Mod (Landscape)`

- [x] Orientation dispatch: `LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE` → `TripDetailPanesLandscape`, aksi → `TripDetailPanes` (mevcut)
- [x] Layout: Timeline sol sütun (genişlik ekseni, `LANDSCAPE_TIMELINE_MIN=0f, MAX=0.60f, DEFAULT=0.40f`), Detaylar+Map sağ sütun (üst-alt, yükseklik ekseni, `LANDSCAPE_DETAYLAR_MIN=0f, MAX=0.30f, DEFAULT=0.30f`)
- [x] Handle A (dikey ayraç, yatay sürükleme — `HandleAxis.Horizontal`) + Handle B (yatay ayraç, dikey sürükleme — `HandleAxis.Vertical`) — bağımsız eksenler, cascade yok
- [x] ResizeHandle genelleştirmesi: `axis: HandleAxis = HandleAxis.Vertical` parametresi — hit-area, drag delta ekseni, crossfade çizgisi orientation'ı axis'e göre değişir; gesture/crossfade/haptic mantığı ortak (DRY)
- [x] Trip-bazlı kalıcılık: `TripPanePreferencesEntity` + `TripPanePreferences` + `TripDetailUiState` — `landscapeTimelineFraction`, `landscapeDetaylarFraction` alanları; portrait'ten ayrı Room kaydı (aynı tablo satırında ek kolon, version=3, `fallbackToDestructiveMigration`)
- [x] ViewModel: `onLandscapePaneResize(timelineFraction, detaylarFraction)` — clamp + persist; `loadPanePreferences`/`persistPanePreferences` tüm alanları kapsıyor; `loadTripDetail` bug-fix genişletildi (landscape alanları da korunuyor)
- [x] Tam Ekran Harita Modu: yatay modda da portrait'tekiyle aynı `Dialog` mantığı (ayrı MapView, mode toggle)
- [x] Eski kod temizliği: `TripDetailLandscapeScreen`, `CompactBudgetRow`, `CompactCategoryRow`, `TripCategory`, `defaultCategories` silindi

---

#### Task 3.2.9: Veri Orkestrasyonu

**Tahmini Süre:** 1.5 saat
**Durum:** ✅ Tamamlandı

> Referans: `TRIP_DETAILS_PAGE.md → Veri Orkestrasyonu`

- [x] 4 endpoint paralel çağrı (GetById, Timeline, BudgetSummary, Checklist) — aggregate endpoint yok, `loadSecondaryData()` ile `awaitAll(async { ... }, ...)` paralel başlatılır
- [x] Partial failure matrisi: sadece GetById bloklayıcı (tam ekran hata, mevcut davranış), diğer 3 bağımsız/non-blocking
- [x] `GET /route` (B0.15) sayfa açılışında değil, Yol moduna geçilince on-demand çağrılır (Task 3.2.4'te zaten doğru kurulmuştu — bu görev bu davranışı değiştirmedi)
- [x] Sessiz mock fallback kararı: backend'de Timeline/BudgetSummary/Checklist endpoint'leri henüz yok (B0.9 dahil) → üçü de hata döner, sonuçlar sessizce yutulur, mevcut mapper mock'u (categoryCards/budget/checklist) çalışmaya devam eder. Backend gelince `ApiResult.Success` dalları gerçek veriyi UI state'e besleyecek şekilde genişletilecek.
- [x] "Inline hata + Tekrar Dene" UI'ı bilerek yazılmadı — mock fallback nedeniyle hiçbir zaman tetiklenemezdi (ölü kod). Backend gelip bu çağrılar bağımsız başarısız olabildiğinde eklenecek (MOBILE_ROADMAP.md'de not düşüldü).
- [x] Yeni DTO'lar: `TimelineResponseDto`, `TimelineEntryDto`, `BudgetSummaryResponseDto`, `ChecklistResponseDto`, `ChecklistItemDto` — hepsi backend TBD şekil tahmini
- [x] TripService: `getTimeline`, `getBudgetSummary`, `getChecklist` — gerçek endpoint, route casing spec'e uygun (timeline küçük-t, diğerleri büyük-T)
- [x] TripRepository/Impl: her üç metod `apiCallExecutor.execute { tripService.getXxx(tripId) }` — repository seviyesinde mock fallback yok
- [x] TripDetailViewModel: `loadSecondaryData()` — `viewModelScope.launch { awaitAll(async { getTimeline }, async { getBudgetSummary }, async { getChecklist }) }`; `loadTripDetail()`'in Success dalından çağrılır

---

### Task 3.3: Trip Detail Aksiyonları

**Tahmini Süre:** ~4 saat (aşağıdaki alt görevlerin toplamı)
**Durum:** [x] Tamamlandı — 3.3.1/3.3.2/3.3.3 hepsi bitti

> Eski implementasyon notu (aşağıda, geçmiş kayıt) eski mimariye (V1/V2, Gün kartları) aitti — yeni tasarımla bu alt görevler geçerli.

---

#### Task 3.3.1: Durum Bazlı Owner Aksiyonları

**Tahmini Süre:** 2 saat
**Durum:** [x] Tamamlandı — Task 3.2.1 ile birlikte, aynı turda uygulandı.

> ⛔ Bağımlılık: B0.11 (Unarchive), B0.14 (🔴 Unpublish) — mobil taraf backend'i beklemeden tamamlandı

- [x] Yayınla (Draft→Published), Arşivle (Published→Archived), Yayına Al (Archived→Published)
- [x] **Düzenlemek için Taslağa Al** (Published→Draft) — onay dialogu: "Bu geziyi düzenlemek için yayından kaldıracaksın. Düzenleme bitince tekrar yayınlaman gerekecek. Devam edilsin mi?" + İptal/Devam Et (`MoveToDraftConfirmDialog`)
- [x] Sil — onay dialogu ("Bu gezi kalıcı olarak silinecek. Emin misin?", `DeleteConfirmDialog`), onaylanmadan API çağrısı yok
- [ ] Not: Sil onaylandıktan sonra ekran otomatik geri gitmiyor (mevcut `loadTripDetail()` sonrası davranışı korunuyor) — nav-back-on-delete ayrı bir iyileştirme olarak bırakıldı

---

#### Task 3.3.2: Misafir/Herkes Aksiyonları

**Tahmini Süre:** 1.5 saat
**Durum:** [x] Tamamlandı — `TokenStore.sessionState` (mevcut, network'süz) ile anonim kontrolü eklendi; Kaydet artık boş değilse önce koleksiyon-seçim bottom sheet'i açıyor (My Trips'in `defaultCollections` mock listesi reuse edildi), zaten kaydedilmişse direkt toggle ediyor.

> ⛔ Bağımlılık: B4.1 (Collections, mock ile ilerlenir)

- [x] Upvote (toggle), Fork, Kaydet (Collections bottom sheet, local mock — `MyTripsUiState.defaultCollections` reuse edildi, B4.1 gelince gerçek endpoint'e geçirilir)
- [x] Paylaş — native Android share sheet, düz link (zaten 3.2.1'de tamamdı, değişiklik yok)
- [x] Şikayet Et — M3'te disabled (zaten 3.2.1'de tamamdı, değişiklik yok)
- [x] **Anonim + auth gereken aksiyon:** `onAction()` içinde `AUTH_REQUIRED_ACTIONS` (UPVOTE/FORK/SAVE) + `isAnonymous` kontrolü — anonimse `showLoginRequiredDialog=true`, API çağrısı hiç yapılmaz; dialogdaki "Giriş Yap" `Routes.Login`'e yönlendirir (dönüşte manuel geri navigasyon — resume-flow mekanizması bu kapsamda yok)

---

#### Task 3.3.3: Erişim Kontrolü (Draft/Archived + Deep Link)

**Tahmini Süre:** 0.5 saat
**Durum:** [x] Tamamlandı (mobil tarafın yapabileceği tek şeyle sınırlı) — `loadTripDetail()`'in hata dalı artık her zaman jenerik "Bu gezi bulunamadı" gösteriyor, ham `result.message` hiç sızdırılmıyor.

> ⛔ Bağımlılık: B0.10 (🔴 Güvenlik) — gerçek 404 enforcement tamamen backend işi, mobil bunu üretemez/test edemez

- [x] Hata mesajı jenerik hale getirildi (teknik detay/40x-50x ayrımı sızdırılmıyor)
- [ ] Owner olmayan biri Draft/Archived'e deep link ile erişirse → backend 404 (B0.10 gelmeden gerçekleşmiyor, mobil zaten hazır — jenerik mesaj otomatik devreye girecek)
- [x] Owner kendi Draft/Archived trip'ine erişimi mevcut davranışla zaten çalışıyor (backend şu an hiç kısıtlama yapmıyor)

---

**Eski İmplementasyon (Geçmiş Kayıt — eski mimariye ait, referans amaçlı korunuyor):**
- [x] ~~Üst bar — Owner: ✏️ Edit + ⋮ menu~~
- [x] ~~Üst bar — Guest: share + ⋮ menu~~
- [x] ~~Detail aksiyonları: publish, archive, delete, upvote/remove-upvote, save/unsave, fork~~
- [x] ~~Upvote/save optimistic toggle + API hatasında geri alma~~
- 6 dosya: `TripDetailPalette.kt`, `TripDetailDimens.kt`, `TripDetailUiState.kt`, `TripDetailMapper.kt`, `TripDetailViewModel.kt`, `TripDetailScreen.kt` (V1+V2) — yeni mimariye göre rework edilecek
- Modifiye edilmiş dosyalar (`TripService.kt`, `TripRepository.kt` vb.) korunuyor, üzerine inşa edilecek

---

### Task 3.4: ~~Saved Trips~~ — My Trips'e Taşındı
> **Karar (M3):** Saved Trips ayrı bir sayfa olmaktan çıkarıldı. My Trips ekranında `Kaydedilenler` adlı üçüncü sekme olarak yaşıyor. Bkz. Task 3.1.

---

### Task 3.5: Wizard İskeleti & WizardViewModel

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Wizard Yapısı (Kesinleşmiş):**
- **7 adım** (8'den indirildi — Person Count, Travel Companion adımına birleştirildi)
- Her adım **tam ekran** Composable
- Üstte **horizontal scroll chip bar**: tamamlanan adımların özeti; chip'e tıklayınca o adıma atla, düzenle, Devam'a bas → sonraki adımlar **korunur** (üstüne yazar)
- Progress bar: `3/7` formatında
- Tek seçimli adımlarda (Companion, Tempo, Transport) seçim yapılınca **"Devam Et" butonu aktifleşir** ama otomatik ilerleme olmaz

**7 Adım:**
1. Origin (nereden)
2. Destinations (nereye + tarihler)
3. Kim ile + Kaç kişi (Travel Companion + personCount aynı ekranda)
4. Bütçe (Budget tier + opsiyonel tutar)
5. Travel Styles (max 3)
6. Tempo (Slow/Moderate/Fast)
7. Transport Preference (Walk/Transit/Mixed)
→ Review & Create (özet ekranı, adım sayılmaz)

**Yapılacaklar:**
- [ ] Ortak `WizardViewModel` — adımlar arası state (her adımın verisi tek `WizardState`'te tutulur), ileri/geri navigasyon, her adımda kendi validasyonu geçmeden "Devam" pasif
- [ ] Her adım ayrı Composable; üstte progress bar (`1/7` ...) + tamamlanan adım chip'leri (horizontal scroll)
- [ ] Chip'e tıklayınca ilgili adıma git; düzenleme sonrası "Devam"a basınca sonraki adımlar korunur
- [ ] Kısmi state kaybını önlemek için `SavedStateHandle` / process-death koruması

---

### Task 3.6: Wizard Adım 1 (Origin) + Adım 2 (Destinations)

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Adım 1 — Origin**
  - Alanlar: `origin` (şehir), `originCountry`
  - Validasyon: ikisi de zorunlu, boş olamaz
  - Empty/başlangıç: arama/autocomplete ile şehir seçimi
- [ ] **Adım 2 — Destinations**
  - Alanlar (her destinasyon): `city`, `country`, `arrivalDate`, `departureDate`, `orderIndex`
  - Validasyon: 1-10 destinasyon · en az 1 zorunlu · `departureDate ≥ arrivalDate` · **sıralı tarihler** (bir sonraki destinasyonun arrival'ı, öncekinin departure'ından önce olamaz) · origin ile aynı şehir uyarısı (opsiyonel)
  - Aksiyonlar: ekle / sil / sırala (drag)
  - Empty: "Henüz destinasyon eklemedin"

---

### Task 3.7: Wizard Adım 3 (Travel Companion + Person Count)

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Adım 3 — Kim ile + Kaç kişi** (tek ekran)
  - Alanlar: `travelCompanion` (enum: Solo / Couple / Family / Friends) + `personCount` (int)
  - Companion: 2×2 grid kart seçimi (ikon + etiket)
  - Person Count stepper: Solo seçilince **gizlenir** (personCount=1 otomatik), diğerlerinde görünür
  - Validasyon: companion zorunlu · personCount `≥ 1` · Solo dışında `≥ 2`

---

### Task 3.8: Wizard Adım 4 (Budget) + Adım 5 (Travel Styles)

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Adım 4 — Budget**
  - Alanlar: `budgetTier` (Economy/Standard/Premium), `currency` (USD/EUR), `manualBudget` (decimal, opsiyonel)
  - UI düzeni:
    - Üst kısım: Economy / Standard / Premium — tam genişlik dikey liste kart
    - Alt kısım "Tahmini bütçe (opsiyonel)":
      - `[$]` `[€]` toggle + hemen yanında text input aynı satırda
      - Preset chip'ler: `[500-1k]` `[1k-2.5k]` `[2.5k-5k]` `[5k-10k]` `[10k+]`
      - Chip'e tıklayınca input'a o değer yazılır; elle de değiştirilebilir
      - Para birimi M13'e kadar sabit $veya€ (M13: Currency Servisi B7)
  - Validasyon: tier zorunlu · manualBudget girilirse `> 0`
  - Bilgi: "Bütçe yetersizse sistem otomatik daha düşük tier önerebilir" (fallback notu)
- [ ] **Adım 5 — Vibe / Travel Styles**
  - Alan: `travelStyles` (multi-select, backend 11 değer: Romantic, Cultural, Adventure, Nature, Local, Relax, Shopping, Gastronomy, Influencer, Nightlife, Budget)
  - Validasyon: **en az 1, en fazla 3** seçim

---

### Task 3.9: Wizard Adım 6 (Tempo) + Adım 7 (Transport)

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Adım 6 — Tempo**
  - Alan: `tempo` (Slow / Moderate / Fast — backend `Tempo`)
  - Validasyon: tek seçim zorunlu · her birinin günlük kapasite etkisi açıklaması (Slow≈3, Moderate≈5, Fast≈7)
- [ ] **Adım 7 — Transport Preference**
  - Alan: `transportPreference` (Walk / Transit / Mixed — backend `TransportPreference`)
  - Validasyon: tek seçim zorunlu

---

### Task 3.10: Wizard Review & Create

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Review & Create**
  - Tüm seçimlerin özeti (düzenle linkleriyle) + destinasyon listesi + tahmini bütçe fallback sonucu
  - Aksiyon: "Trip Oluştur" → submit (buton loading) · hata → snackbar + ilgili adıma dön
- [ ] Son adımda `POST /api/v1/Trips/wizard` → `CreateTripWizardResponse` (budget fallback sonucu) → Trip Detail'e yönlendir

---

### Task 3.11: Destinations Management

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Destinations Management** — list/add/update/delete (`/trips/{id}/destinations`)
- [ ] "Destinasyon yok" → ekle CTA
- [ ] Tarih çakışması → inline hata

---

### Task 3.12: Timeline Listesi

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

> ⛔ **Bağımlılık: B0.10** — Liste **Published trip'lerde anonim dahil herkese açık** (görüntüleme); Draft/Archived'de sadece owner (404 diğerlerine). Bkz. `OMNIFLOW_PAGE_ARCHITECTURE.md § 9.1`.

**Yapılacaklar:**
- [ ] **Timeline** — gün bazlı liste, lock/visited/sıralama durumu (`GET /api/v1/trips/{tripId}/timeline`)
- [ ] Gün için "Bu güne entry yok" + ekle CTA
- [ ] Locked entry kilitli rozet

---

### Task 3.13: Create / Edit Timeline Entry

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

> **Not:** Create/Edit/Delete **sadece owner'a açık** (misafir/anonim salt-okunur, bu ekranlara hiç erişemez).

**Yapılacaklar:**
- [ ] **Create/Edit Timeline Entry** — 5 tip (Place, CustomFlight, CustomTransport, CustomAccommodation, CustomEvent)
- [ ] Tip seçimine göre dinamik form (her tip farklı alan seti)

---

### Task 3.14: Timeline Reorder + Visited

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

> **Not:** Reorder/Visited **sadece owner'a açık** — misafir/anonim bu aksiyonları göremez/tetikleyemez.

**Yapılacaklar:**
- [ ] **Reorder** (drag) → `PUT /api/v1/trips/{tripId}/timeline/reorder`
- [ ] **Visited** toggle
- [ ] Reorder/visited optimistic

---

### Task 3.15: Budget Summary

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

> ⛔ **Bağımlılık: B0.10** — Backend'de bu endpoint şu an **katı owner-only** (`ForbiddenException`). Karar: **bütçe herkese açık** olacak (Owner/Misafir farkı yok), bu yüzden backend'in status-bazlı erişime (Published'da herkes, Draft/Archived'de sadece owner) çevrilmesi gerekiyor — bkz. `BACKEND_ROADMAP_V2.md → B0.10` ek düzeltme notu.

**Yapılacaklar:**
- [ ] **Budget Summary** — gerçek zamanlı kırılım (`GET /api/v1/Trips/{tripId}/budget-summary`)
- [ ] Veri yoksa "Bütçe için entry ekle"
- [ ] Fallback uygulanmışsa bilgi rozeti (adjusted tier)
- [ ] Owner/Misafir ayrımı yok — sayfa herkese aynı şekilde görünür (Published trip'lerde)

---

### Task 3.16: Recommend Places

**Tahmini Süre:** 0.5 saat
**Durum:** [ ] Bekliyor

> ⛔ **Bağımlılık: B0.10** — Liste **Published trip'lerde anonim dahil herkese açık**; Draft/Archived'de sadece owner (404 diğerlerine). **"Timeline'a ekle" aksiyonu ise sadece owner'a görünür** — misafir/anonim listeyi görebilir ama ekleyemez. Bkz. `OMNIFLOW_PAGE_ARCHITECTURE.md § 8.3`.

**Yapılacaklar:**
- [ ] **Recommend Places** — recommended/neutral/other (`GET /api/v1/Trips/{id}/recommend-places`) → timeline'a ekle
- [ ] "Öneri bulunamadı" → empty; Timeline'a ekle → snackbar
- [ ] Owner değilse "Timeline'a ekle" butonu gizli/gösterilmez

---

### Ekran Durumları (M3)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| My Trips | Sekme bazlı "Henüz {Draft/Published/Archived} trip yok" + Wizard CTA | Sekmeli kart listesi | Pagination footer |
| Trip Detail | — | Kapak + özetler + aksiyon barı | Owner değilse edit/publish/delete gizli; Draft/Archived sadece owner'a görünür |
| Saved Trips | "Henüz kayıtlı trip yok" + Explore CTA | Kart listesi | Unsave → optimistic + snackbar |
| Wizard (her adım) | Adıma özel (bkz. adım detayları) | Geçerli seçim → Devam aktif | Validasyon geçmeden Devam pasif |
| Destinations Mgmt | "Destinasyon yok" | Sıralı liste + ekle/sil/düzenle | Tarih çakışması → inline hata |
| Timeline | Gün için "Bu güne entry yok" + ekle CTA | Gün bazlı entry listesi | Reorder/visited optimistic; locked entry kilitli rozet |
| Create/Edit Entry | — | Tip seçimine göre dinamik form | 5 tipin her biri farklı alan seti |
| Budget Summary | Veri yoksa "Bütçe için entry ekle" | Kırılımlı özet + adjusted tier | Fallback uygulanmışsa bilgi rozeti |
| Recommend Places | "Öneri bulunamadı" | recommended/neutral/other grupları | Timeline'a ekle → snackbar |

### Definition of Done (M3)

- [ ] Kullanıcı wizard ile çok-destinasyonlu trip oluşturabiliyor
- [ ] Timeline'a entry ekleyip sıralayıp visited işaretleyebiliyor
- [ ] Bütçe özeti ve önerilen yerler görüntülenebiliyor
- [ ] Trip publish/archive/fork/save/upvote çalışıyor

### Test (Minimal)

- [ ] `WizardViewModel` unit testi (adım geçişleri + final request map'leme)
- [ ] `TimelineViewModel` unit testi (reorder + visited state)

---

## 🎯 M4 — Explore & Provider Verisi

> **Backend:** Mevcut (explore, featured, search param, places, providers). Bağımlılık yok.

### Scope

Keşif ekranları ve planlama için provider (uçak/otel) verisi.

---

### Task 4.1: Explore Main

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Explore Main** — liste, filtreler (city/country/budget/style/tags), sort, search (`GET /explore?searchTerm=`)
- [ ] Filtre sonucu boşsa "Bu kriterlere uygun trip yok" + filtreleri temizle
- [ ] Aktif filtre çipleri; search debounce
- [ ] ViewModel + UiState

---

### Task 4.2: Explore Featured

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Explore Featured** (`GET /explore/featured`)
- [ ] "Şu an öne çıkan trip yok" → empty

---

### Task 4.3: Cursor Infinite Scroll

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Cursor-based infinite scroll (footer loading + sonraki sayfa hatası inline retry)

---

### Task 4.4: Place Detail

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Place Detail** — foto, kategori, açıklama, travel style uyumu, Google/OSM metadata (`GET /places/{id}`)
- [ ] Koordinat varsa "Haritada aç"

---

### Task 4.5: Explore'dan Kaydet / Fork

**Tahmini Süre:** 0.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Explore'dan trip kaydet/fork (optimistic + snackbar)

---

### Task 4.6: Provider Flights

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Provider Flights** — route bazlı (`GET /providers/flights`, `GET /providers/origin-cities`)
- [ ] Origin city seçimi gerekli
- [ ] "Bu route için uçuş yok" → empty

---

### Task 4.7: Provider Hotels

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Provider Hotels** — segment/bütçe (`GET /providers/hotels`)
- [ ] "Bu şehir için otel yok" → empty

---

### Task 4.8: Timeline'a Ekleme + Freshness Gösterimi

**Tahmini Süre:** 0.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Freshness/snapshot bilgisi gösterimi (varsa freshness rozeti)
- [ ] Provider sonucundan timeline'a custom flight/accommodation ekleme

---

### Ekran Durumları (M4)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Explore Main | Filtre sonucu boşsa "Bu kriterlere uygun trip yok" + filtreleri temizle | Cursor'lu kart listesi | Aktif filtre çipleri; search debounce |
| Explore Featured | "Şu an öne çıkan trip yok" | Highlight kart listesi | — |
| Place Detail | — | Foto + metadata + travel style uyumu | Koordinat varsa "Haritada aç" |
| Provider Flights | "Bu route için uçuş yok" | Route bazlı liste + freshness rozeti | Origin city seçimi gerekli |
| Provider Hotels | "Bu şehir için otel yok" | Segment/bütçe bazlı liste + freshness | — |

### Definition of Done (M4)

- [ ] Explore filtre + sort + infinite scroll çalışıyor
- [ ] Place detay görüntülenebiliyor
- [ ] Provider uçak/otel listeleri görüntülenip timeline'a eklenebiliyor

### Test (Minimal)

- [ ] `ExploreViewModel` unit testi (filtre + pagination)

---

## 🎯 M5 — Social & Community

> **Backend:** Mevcut (feed, posts, comments, tips, follows, liked, trending-tags). Bağımlılık yok.

### Scope

Topluluk akışı, gönderiler, yorumlar, tip'ler ve etkileşimler.

---

### Task 5.1: Community Feed

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Community Feed** — ForYou/Following/Latest sekmeleri + cursor (`GET /feed`)
- [ ] Following sekmesi boşsa "Kimseyi takip etmiyorsun" + öneri CTA; diğer sekmeler "Henüz içerik yok"
- [ ] Upvote optimistic
- [ ] ViewModel + UiState

---

### Task 5.2: Create Post

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Create Post** — metin, foto (media upload), tag, ilişkili trip/place (`POST /posts`)
- [ ] Foto yükleme loading; min içerik validasyonu

---

### Task 5.3: Post Detail + Upvote/Edit/Delete

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Post Detail** — içerik, foto, upvote, yorumlar (`GET /posts/{id}`)
- [ ] Post upvote/remove-upvote, edit/delete (owner)

---

### Task 5.4: Liked Posts + Trending Tags

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Liked Posts** + **Trending Tags**
- [ ] "Henüz beğeni yok" / "Trend etiket yok" → empty

---

### Task 5.5: Comments

**Tahmini Süre:** 3.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Comments** — listeleme, 1 seviye reply, upvote (`/posts/{id}/comments`)
- [ ] "Henüz yorum yok" → "İlk yorumu sen yap"
- [ ] Cross-post reply engeli (backend)

---

### Task 5.6: Community Tips

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Community Tips** — trip bazlı listeleme/oluşturma/upvote (`/trips/{id}/tips`)
- [ ] "Bu trip için henüz tip yok" + ekle CTA

---

### Ekran Durumları (M5)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Feed | Following sekmesi boşsa "Kimseyi takip etmiyorsun" + öneri CTA; diğer sekmeler "Henüz içerik yok" | 3 sekmeli cursor'lu liste | Upvote optimistic |
| Create Post | — | Metin + foto + tag + ilişki formu | Foto yükleme loading; min içerik validasyonu |
| Post Detail | Yorum yoksa "İlk yorumu sen yap" | İçerik + yorumlar + upvote | Owner'a edit/delete |
| Comments | "Henüz yorum yok" | Yorum + 1 seviye reply | Cross-post reply engeli (backend) |
| Community Tips | "Bu trip için henüz tip yok" + ekle CTA | Tip listesi + upvote | — |
| Liked Posts / Trending Tags | "Henüz beğeni yok" / "Trend etiket yok" | Liste | — |

### Definition of Done (M5)

- [ ] Feed 3 sekme + infinite scroll çalışıyor
- [ ] Post oluşturma (fotolu), yorum ve tip akışları çalışıyor
- [ ] Upvote/follow etkileşimleri çalışıyor

### Test (Minimal)

- [ ] `FeedViewModel` unit testi (tab değişimi + pagination)

---

## 🎯 M6 — Notifications detay + Block + Admin → 🎯 MOBİL MVP TAMAM

> **Backend:** Mevcut (blocks, admin). Bağımlılık yok.

### Scope

MVP'yi kapatan son parçalar: engelleme yönetimi ve admin paneli.

---

### Task 6.1: Blocked Users

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Blocked Users** — liste + unblock (`/users/{id}/blocked-users`)
- [ ] "Engellenen kullanıcı yok" → empty
- [ ] Unblock optimistic + snackbar

---

### Task 6.2: Profilden Block/Unblock Entegrasyonu

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Profilden block/unblock entegrasyonu (M2 ile tamamlanır)

---

### Task 6.3: Admin Dashboard

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Admin Dashboard** (admin stack girişi, `[Authorize Admin]`)
- [ ] Özet kartlar + kısayollar (sadece Admin rolü)

---

### Task 6.4: Admin Users (Suspend/Unsuspend)

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Admin Users** — liste, suspend/unsuspend (`/admin/users`)
- [ ] Search sonucu boşsa "Kullanıcı bulunamadı"
- [ ] Aksiyon onayı (dialog)

---

### Task 6.5: Admin Posts (Delete)

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Admin Posts** — liste, delete (`/admin/posts`)
- [ ] Delete onayı (dialog)

---

### Ekran Durumları (M6)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Blocked Users | "Engellenen kullanıcı yok" | Liste + unblock | Unblock optimistic + snackbar |
| Admin Dashboard | — | Özet kartlar + kısayollar | Sadece Admin rolü |
| Admin Users | Search sonucu boşsa "Kullanıcı bulunamadı" | Liste + suspend/unsuspend | Aksiyon onayı (dialog) |
| Admin Posts | "Post yok" | Liste + delete | Delete onayı (dialog) |

### Definition of Done (M6 / MVP)

- [ ] Engelleme uçtan uca çalışıyor
- [ ] Admin kullanıcı uygulama içinden moderasyon yapabiliyor
- [ ] **🎯 Mobil MVP tamam:** mevcut backend'in tüm çekirdek özellikleri mobilde kullanılabilir durumda

### MVP Success Metrics

- [ ] Auth + onboarding + oturum kalıcılığı çalışıyor
- [ ] Wizard ile trip oluşturma → timeline → publish → explore → fork tam döngü çalışıyor
- [ ] Sosyal akış (feed/post/comment/follow) çalışıyor
- [ ] Profil + admin + block çalışıyor
- [ ] Tüm ekranlar gerçek backend'e bağlı, mock yok

---

# 🚀 MVP SONRASI — Yeni Özellikler (Backend Gerektiren)

> Bu fazlardan her biri, `BACKEND_ROADMAP_V2.md`'deki ilgili backend fazı **önce** tamamlanmadan başlatılamaz.

---

## 🎯 M7 — Google ile Giriş

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B1` (Google OAuth) tamamlanmalı.

### Scope

Email/şifre akışının yanına Google ile giriş eklenir. Google Sign-In SDK ID token üretir; backend doğrular ve OmniFlow JWT'si döner.

---

### Task 7.1: Google Sign-In SDK + OAuth Client

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Google Sign-In SDK (Credential Manager) entegrasyonu
- [ ] Google Cloud OAuth client id

---

### Task 7.2: "Google ile Devam Et" Butonu

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Login/Register ekranlarına "Google ile devam et" butonu

---

### Task 7.3: Token Akışı (ID token → JWT)

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Alınan ID token → `POST /api/account/google` → OmniFlow JWT
- [ ] Token saklama mevcut `TokenManager` ile aynı

---

### Definition of Done (M7)

- [ ] Kullanıcı Google ile giriş yapıp uygulamaya girebiliyor (yeni ve mevcut kullanıcı)

### Test (Minimal)
- [ ] `LoginViewModel` Google akışı için genişletilmiş unit test (token → session)

---

## 🎯 M8 — Live Trip Mode + Harita + Visit Log + Trip Summary

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B2` (Visit Log, Trip Summary, Timezone). Harita için Google Maps API anahtarı **gerekmiyor** — MapLibre Compose + OpenFreeMap ücretsiz/API-key'siz kullanılıyor.
>
> ⚠️ **Sıralama notu:** MapLibre Compose'un temel kurulumu (kütüphane + OpenFreeMap tile) artık **M3 / Task 3.2**'de yapılıyor (Trip Detail zaten haritayı kullandığı için önce oraya taşındı). Bu fazda (M8) sadece **Live Trip'e özel GPS/konum katmanı** ekleniyor, temel harita altyapısı tekrar kurulmuyor.

### Scope

Seyahat sırasında kullanım: bugünün planı + harita + konum; gerçek ziyaret kayıtları; kapanış özeti.

---

### Task 8.1: Live Trip Harita — Konum Katmanı

**Tahmini Süre:** 1 saat *(2 saatten düşürüldü — temel MapLibre kurulumu M3'e taşındığı için)*
**Durum:** [ ] Bekliyor

> ⛔ **Bağımlılık: M3 / Task 3.2** (MapLibre Compose kurulumu orada tamamlanmış olmalı)

**Yapılacaklar:**
- [ ] M3'te kurulan MapLibre haritasına **canlı konum pin'i** ve gerekli overlay'ler eklenir (temel kütüphane kurulumu tekrarlanmaz)

---

### Task 8.2: Konum İzni + FusedLocationProvider

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Konum izni (just-in-time), `FusedLocationProvider`

---

### Task 8.3: Timeline Koordinat Pinleme

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Timeline entry koordinatlarını haritada pinleme

---

### Task 8.4: Live Trip Mode Ekranı

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Live Trip Mode** — bugünün timeline'ı + harita + aktif konum aynı ekranda
- [ ] Bugün entry yoksa "Bugün için plan yok"
- [ ] Konum izni reddedilirse harita merkez fallback + uyarı
- [ ] Timeline'dan visited işaretleme ile entegrasyon (mevcut)

---

### Task 8.5: Yakındaki Mekanlar Önerisi

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Yakındaki mekanlar önerisi (kendi places + kapsam dışı için ileride canlı API)

---

### Task 8.6: Visit Log

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Visit Log** — gerçek harcama + puan + not (`/trips/{id}/visit-logs`)
- [ ] Submit loading; başarı snackbar

---

### Task 8.7: Trip Summary Ekranı

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Trip Summary** — kapanış ekranı (ziyaret/harcama/öne çıkanlar) (`/trips/{id}/summary`)
- [ ] Veri azsa "Henüz yeterli ziyaret kaydı yok"
- [ ] Paylaş aksiyonu
- [ ] (Sonraki sürüm) Offline cache notu — M14'e bırakılır

---

### Ekran Durumları (M8)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Live Trip Mode | Bugün entry yoksa "Bugün için plan yok" | Bugünün timeline'ı + harita + konum pini | Konum izni reddedilirse harita merkez fallback + uyarı |
| Visit Log | — | Harcama + puan + not formu | Submit loading; başarı snackbar |
| Trip Summary | Veri azsa "Henüz yeterli ziyaret kaydı yok" | Ziyaret/harcama/öne çıkanlar özeti | Paylaş aksiyonu |

### Definition of Done (M8)

- [ ] Seyahat sırasında bugünün planı harita + konumla gösteriliyor
- [ ] Durak ziyareti gerçek veriyle loglanıyor
- [ ] Trip bitiminde özet ekranı geliyor

### Test (Minimal)
- [ ] `LiveTripViewModel` unit testi (bugünün entry'lerini filtreleme + visited)

---

## 🎯 M9 — Push Notifications + Tercihler

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B3` (FCM + Preferences).

### Scope

Mevcut in-app notification'a push katmanı: token kaydı, deep link, tercihler.

---

### Task 9.1: Firebase + FCM SDK

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Firebase projesi + `google-services.json` + FCM SDK

---

### Task 9.2: Bildirim İzni + Channels

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Bildirim izni (Android 13+), notification channels

---

### Task 9.3: FCM Token Yönetimi

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] FCM token alma + `POST /api/v1/push-tokens` ile kaydetme; logout'ta silme

---

### Task 9.4: Push → Deep Link Yönlendirme

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Gelen push → ilgili ekrana deep link (notification → post/trip/profil)

---

### Task 9.5: Notification Preferences Ekranı

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Notification Preferences** ekranı (`/users/me/notification-preferences`)
- [ ] Tip bazlı toggle listesi; toggle optimistic + kaydet; izin kapalıysa sistem ayarı uyarısı

---

### Ekran Durumları (M9)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Notification Preferences | — | Tip bazlı toggle listesi | Toggle optimistic + kaydet; izin kapalıysa sistem ayarı uyarısı |

### Definition of Done (M9)

- [ ] Cihaz push alıyor ve tıklayınca doğru ekrana gidiyor
- [ ] Kullanıcı bildirim tiplerini açıp kapatabiliyor

### Test (Minimal)
- [ ] `NotificationPreferencesViewModel` unit testi (toggle + kaydet)

---

## 🎯 M10 — Collections, Global Search, Deep-link, Memories

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B4`.

### Scope

Kişisel düzenleme ve keşif katmanı: koleksiyonlar, global arama, paylaşım linki, gezi günlüğü.

---

### Task 10.1: Collections

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Collections** — liste, oluştur/düzenle/sil (`/collections`)
- [ ] "Henüz koleksiyon yok" + oluştur CTA
- [ ] Sil → onay dialog

---

### Task 10.2: Collection Detail

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Collection Detail** — içindeki trip'ler, ekle/çıkar
- [ ] "Bu koleksiyon boş" + trip ekle
- [ ] Çıkar optimistic

---

### Task 10.3: Global Search

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Global Search** — tek arama kutusu, sekmeli sonuç (kullanıcı/trip/post/place/tag) (`/search?q=`)
- [ ] Arama öncesi son aramalar / öneriler; sonuç yoksa "Sonuç bulunamadı"
- [ ] Debounce + min karakter

---

### Task 10.4: Trip Deep-link / Share Intent

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Trip paylaşım (deep link / share intent) → link tıklanınca trip detail açılır

---

### Task 10.5: Memories / Journal

**Tahmini Süre:** 4 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Memories / Journal** — gezi notları + fotoğraflar (`/trips/{id}/memories`)
- [ ] "Henüz anı eklenmedi" → empty; gün bazlı not + foto akışı; foto yükleme loading

---

### Task 10.6: Email Verify App Link (Deep Link Altyapısı Üstüne)

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

> **Bağlam:** MVP'de (Task 1.9) email doğrulama linki **web frontend'ine** iniyordu ve Verify ekranı bir login köprüsüydü. Bu task, Task 10.4 ile kurulan Android App Links altyapısının üstüne email doğrulamayı bağlar: maildeki link **doğrudan app'i açar**, kullanıcı login formuna dönmek zorunda kalmaz.
>
> ⛔ **Backend bağımlılığı:** `BACKEND_ROADMAP_V2 → Task B4.5` (App Link uyumlu email verify URL + `assetlinks.json`). `AccountService` şu an doğrulama URL'ini `FrontendVerifyUrl` ile (web) üretiyor; B4.5 bu URL'i App Link uyumlu hale getirir ve `assetlinks.json` host'lar. Bu task B4.5 tamamlanmadan başlatılamaz; `assetlinks.json` için package name + SHA-256 imza parmak izi backend'e iletilmeli.

**Yapılacaklar:**
- [ ] `assetlinks.json` (Digital Asset Links) frontend domaininde host'lanır + `AndroidManifest`'e `autoVerify` intent-filter
- [ ] Email verify path'i için deep link route'u (`/verify-email?email=&token=`) app içinde handle edilir
- [ ] Link açıldığında `POST /api/account/verify-email` çağrılır → başarı → otomatik login / Home'a yönlendirme
- [ ] Geçersiz/expired token → Verify Email Info ekranına anlamlı hata ile düşülür
- [ ] Fallback: App Link doğrulanmazsa (app kurulu değil / domain doğrulanmadı) mevcut web akışı korunur

---

### Task 10.7: Reset Password Deep Link Ekranı (Deep Link Altyapısı Üstüne)

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

> **Bağlam:** MVP'de (Task 1.10) şifre sıfırlama **web'de** tamamlanıyordu; mobil `ResetPasswordScreen` bu yüzden **kaldırıldı** (link `FrontendResetUrl`'e iniyordu, mobile değil). Bu task, Task 10.6 ile kurulan App Links altyapısının üstüne mobil reset ekranını **geri getirir**: maildeki reset linki doğrudan app'i açar, kullanıcı şifresini app içinde yeniler — web'e gitmez.
>
> ⛔ **Backend bağımlılığı:** `BACKEND_ROADMAP_V2 → Task B4.5` (App Link uyumlu URL + `assetlinks.json`). Aynı `assetlinks.json`; backend `FrontendResetUrl`'i App Link uyumlu hale getirmeli. Task 10.6 ile birlikte yapılır.

**Yapılacaklar:**
- [ ] Reset path'i için deep link route'u (`/reset-password?email=&token=`) app içinde handle edilir
- [ ] **Reset Password ekranı** geri eklenir (5 state: Default / Validation / Loading / Success / Invalid-Expired token) — daha önce tasarlandığı haliyle
- [ ] Yeni şifre canlı kurallar (backend 422 ile eşleşir) + confirm match
- [ ] Link açıldığında `POST /api/account/reset-password` → başarı → otomatik login / Home (backend reset sonrası `EmailConfirmed=true` yapar)
- [ ] Geçersiz/expired token → "Link geçersiz veya süresi dolmuş" + "Yeni link iste" (→ Forgot Password)
- [ ] Fallback: App Link doğrulanmazsa mevcut web reset akışı korunur

---

### Ekran Durumları (M10)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Collections | "Henüz koleksiyon yok" + oluştur CTA | Koleksiyon listesi (trip sayısı ile) | Sil → onay dialog |
| Collection Detail | "Bu koleksiyon boş" + trip ekle | İçindeki trip'ler | Çıkar optimistic |
| Global Search | Arama öncesi son aramalar / öneriler; sonuç yoksa "Sonuç bulunamadı" | Sekmeli sonuç (kullanıcı/trip/post/place/tag) | Debounce + min karakter |
| Memories / Journal | "Henüz anı eklenmedi" | Gün bazlı not + foto akışı | Foto yükleme loading |

### Definition of Done (M10)

- [ ] Koleksiyon oluşturup trip ekleme çalışıyor
- [ ] Global arama çalışıyor
- [ ] Trip paylaşım linki dış uygulamadan trip detail'e açılıyor
- [ ] Gezi günlüğü eklenebiliyor
- [ ] (Backend koordinasyonu varsa) Email doğrulama linki doğrudan app'i açıp doğrulamayı tamamlıyor
- [ ] (Backend koordinasyonu varsa) Reset linki doğrudan app'te açılıp şifre app içinde yenilenebiliyor

### Test (Minimal)
- [ ] `SearchViewModel` unit testi (debounce + sonuç gruplama)

---

## 🎯 M11 — Report + Moderasyon UI

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B5`.

### Scope

Kullanıcı raporlama akışı + admin moderasyon (rapor yönetimi, soft moderation, audit log).

---

### Task 11.1: Report Aksiyonu

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Post/comment/tip/trip/profil üzerinde **Report** aksiyonu

---

### Task 11.2: Report Reason + Submitted Ekranları

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Report Reason** + **Report Submitted** ekranları (`POST /reports`)
- [ ] Sebep seçimi (radio) + opsiyonel açıklama; aynı içeriği tekrar raporlama → bilgi mesajı

---

### Task 11.3: Admin Reports Listesi + Detail + Aksiyon

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Admin: **Reports** listesi + **Report Detail** + aksiyon (ignore/hide/delete/suspend) (`/admin/reports`)
- [ ] Status filtreli liste; aksiyon onayı

---

### Task 11.4: Admin Audit Log Ekranı

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Admin: **Audit Log** ekranı (`/admin/audit-log`)
- [ ] Kronolojik aksiyon listesi + filtre

---

### Task 11.5: Soft Moderation Aksiyonları

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Admin: soft moderation aksiyonları (hide/review-pending/restrict)

---

### Ekran Durumları (M11)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Report Reason | — | Sebep seçimi (radio) + opsiyonel açıklama | Aynı içeriği tekrar raporlama → bilgi mesajı |
| Report Submitted | — | "Raporun alındı" + geri dön | — |
| Admin Reports | "Bekleyen rapor yok" | Status filtreli liste | — |
| Admin Report Detail | — | Raporlayan/hedef/sebep + aksiyonlar | ignore/hide/delete/suspend onayı |
| Admin Audit Log | "Kayıt yok" | Kronolojik aksiyon listesi + filtre | — |

### Definition of Done (M11)

- [ ] Kullanıcı içerik raporlayabiliyor
- [ ] Admin raporları görüp aksiyon alabiliyor, audit log görüntülenebiliyor

### Test (Minimal)
- [ ] `ReportViewModel` unit testi (sebep seçimi + gönderim)

---

## 🎯 M12 — AI Chat / Timeline Optimize

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B6`.

### Scope

AI önerir/optimize eder (tam rota üretmez). AI chat + timeline optimizasyon sonucu.

---

### Task 12.1: AI Chat / Assistant Ekranı

**Tahmini Süre:** 3.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **AI Chat / Assistant** ekranı — soru-cevap, önerilen sonuç kartları, trip bağlamına göre yönlendirme (`POST /ai/chat`)
- [ ] Başlangıçta öneri/örnek sorular; cevap beklerken "yazıyor" göstergesi; hata → tekrar dene

---

### Task 12.2: AI Sonucunu Trip/Timeline'a Uygulama

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Sonucu trip'e / timeline'a uygulama

---

### Task 12.3: Timeline Optimization Result Ekranı

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Timeline Optimization Result** ekranı — mevcut sıra vs önerilen, tahmini kazanç (`POST /trips/{id}/timeline/optimize`)
- [ ] "İyileştirme önerisi yok" (zaten optimal) → empty

---

### Task 12.4: Öneri Onay → Reorder Uygulama

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Öneri otomatik uygulanmaz; onaylanınca mevcut reorder kullanılır
- [ ] Kilitli entry değişmez

---

### Ekran Durumları (M12)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| AI Chat | Başlangıçta öneri/örnek sorular | Mesaj akışı + sonuç kartları | Cevap beklerken "yazıyor" göstergesi; hata → tekrar dene |
| Timeline Optimization Result | "İyileştirme önerisi yok" (zaten optimal) | Mevcut vs önerilen sıra + tahmini kazanç | Uygula → reorder; kilitli entry değişmez |

### Definition of Done (M12)

- [ ] AI chat gerçek veriye dayalı öneriler veriyor
- [ ] Timeline optimizasyon önerisi gösterilip onaylanınca uygulanıyor (kilitli entry korunuyor)

### Test (Minimal)
- [ ] `AiChatViewModel` unit testi (mesaj gönder/al state akışı)

---

## 🎯 M13 — Para Birimi Çift Gösterim

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B7` (Currency servisi).

### Scope

Lokal para birimi ana, kullanıcının para birimi ikincil gösterilir.

---

### Task 13.1: Currency Preferences Ekranı

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Currency Preferences** ekranı (ana + ikincil para birimi)
- [ ] Kur verisi yüklenirken loading; kaydet snackbar

---

### Task 13.2: Kur Verisi Çekme + Cache

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Kur verisini çekme (`/currency/rates`) + cache

---

### Task 13.3: Çift Para Birimi Format

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Fiyat gösterimlerinde lokal (ana) + kullanıcı para birimi (ikincil/küçük) format
- [ ] Kur çekilemezse sadece ana birim + "kur güncellenemedi"

---

### Task 13.4: Ekranlara Uygulama

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Budget/provider/visit-log ekranlarında uygulama

---

### Ekran Durumları (M13)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Currency Preferences | — | Ana + ikincil para birimi seçimi | Kur verisi yüklenirken loading; kaydet snackbar |
| (Fiyat gösterimleri) | — | Ana büyük + ikincil küçük format | Kur çekilemezse sadece ana birim + "kur güncellenemedi" |

### Definition of Done (M13)

- [ ] Fiyatlar çift para biriminde tutarlı gösteriliyor
- [ ] Kullanıcı para birimi tercihini değiştirebiliyor

### Test (Minimal)
- [ ] Para birimi dönüşüm/format util unit testi

---

## 🎯 M14 — Offline Sync & Trip Collaboration

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B8`.

### Scope

İki ileri özellik: offline cache/senkron ve trip collaboration. En sona bırakılır.

---

### Task 14.1: Trip Room Cache (Offline Görüntüleme)

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Trip verisini Room'a cache'leme; internetsiz timeline/harita görüntüleme

---

### Task 14.2: updatedSince Delta Senkronizasyon

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] `updatedSince` delta ile senkronizasyon; online olunca güncelleme

---

### Task 14.3: Live Trip Mode Offline Çalışma

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Live Trip Mode'un offline çalışması (asıl kullanım senaryosu)

---

### Task 14.4: Trip Collaboration Management

**Tahmini Süre:** 4 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Trip Collaboration Management** — davet gönder/iptal, collaborator listesi, rol (`/trips/{id}/collaborators`)
- [ ] Sadece owner; rol değiştir/çıkar onayı

---

### Task 14.5: Davet Kabul + Rol Bazlı Yetki

**Tahmini Süre:** 4 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Davet kabul akışı + rol bazlı düzenleme yetkisi UI'da

---

### Ekran Durumları (M14)

| Ekran | Empty | Success | Not |
|-------|-------|---------|-----|
| Offline (genel) | Cache yoksa "İnternet gerekli" | Cache'den içerik + "çevrimdışı" rozeti | Online olunca sessiz senkron + güncelleme rozeti |
| Trip Collaboration Mgmt | "Henüz collaborator yok" | Collaborator listesi + bekleyen davetler | Sadece owner; rol değiştir/çıkar onayı |

### Definition of Done (M14)

- [ ] Trip offline görüntülenip senkronize olabiliyor
- [ ] Bir trip birden fazla kullanıcı tarafından (rol bazlı) düzenlenebiliyor

### Test (Minimal)
- [ ] Offline cache repository unit testi (cache hit/miss + sync merge)

---

## 📌 Genel Notlar & Yol Haritası Özeti

| Faz | Konu | Backend bağımlılığı | Durum |
|-----|------|---------------------|-------|
| M0 | Proje kurulumu & iskelet | — | ✅ |
| M1 | Auth & Onboarding | — | ✅ |
| M2 | Navigasyon + Home + Profil | — | 🔄 |
| M3 | Trips (wizard/timeline/budget) | — | [ ] |
| M4 | Explore & Provider | — | [ ] |
| M5 | Social & Community | — | [ ] |
| M6 | Block + Admin → **MVP** | — | [ ] |
| M7 | Google login | B1 | [ ] |
| M8 | Live Trip + Harita + Visit Log | B2 (+ Maps key) | [ ] |
| M9 | Push + tercihler | B3 | [ ] |
| M10 | Collections/Search/Deep-link/Memories | B4 | [ ] |
| M11 | Report + moderasyon | B5 | [ ] |
| M12 | AI Chat / Optimize | B6 | [ ] |
| M13 | Para birimi | B7 | [ ] |
| M14 | Offline & Collaboration | B8 | [ ] |

### Çalışma Disiplini

- **Katman zinciri:** Veri işi merkezi `data/`'da (Api servisi + request/response DTO + repository [interface+impl] + local). Feature'lar (`ui/<feature>/`) **sadece UI**: Screen + ViewModel + UiState + UiModel + Event + Mapper. **UseCase/domain katmanı yok** — ViewModel doğrudan repository çağırır; Mapper, response DTO'sunu UiModel'e çevirir. Ortak component'ler `ui-components/`'ta.
- **State:** Her ekran tek bir `UiState` (loading/data/error) ile yönetilir; ViewModel `StateFlow` döner.
- **Hata yönetimi:** `ApiResult.Error` → `UiText` → kullanıcıya gösterim. 401 otomatik refresh (M0).
- **Görsel:** Coil ile lazy görsel; placeholder/hata state'leri design system component'lerinden.
- **Tasarım:** Tasarımı olmayan ekranlar mevcut design system component'leriyle, tasarım diline sadık biçimde üretilir.
- **Test:** Minimal — yalnızca kritik ViewModel unit testleri; gerisi manuel QA.

---

## 🧹 Teknik Borç & Polish (milestone'a bağlı olmayan)

Bunlar sabit bir feature milestone'una ait değildir; tetikleyicisi/zamanı aşağıda.

| İş | Ne zaman | Not |
|----|----------|-----|
| **Custom lint kuralı** (ekran dosyalarında `Color(0x` / inline `.sp` / magic spacing `.dp` yasağı) | ✅ **Yapıldı** (token rollout sonrası) | Design token konvansiyonunu regresyona karşı korur. |
| **Motion / animation token'ları** | **Demo öncesi polish pass** | İlk ciddi animasyon işine başlanınca; MVP için zorunlu değil. Sabit milestone yok. |
| **Component redesign** | **Event-driven (tarihsiz)** | Tasarım bir component'in görünümünü değiştirdiğinde tetiklenir; planlı task değildir. Tokenization ≠ redesign. |
| **Reset Password deep-link ekranı** | **M10 / Task 10.7** | App Links altyapısına (B4.5) bağlı; M10'a taşındı (bkz. Task 10.7). |

> **Not:** "ertelenen her şeyi M14 sonuna yığma" anti-pattern'inden kaçınmak için bu işler buraya ayrıldı. Özellikle lint önleyici olduğu için **erteleyince değeri kaybolur** → token rollout'uyla birlikte kapatıldı.
