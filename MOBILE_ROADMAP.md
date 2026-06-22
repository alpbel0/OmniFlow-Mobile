# OmniFlow Mobile — Project Roadmap (Kotlin / Jetpack Compose)

**Proje:** OmniFlow Mobile — Android (Kotlin) uygulaması
**Mimari:** Jetpack Compose + MVVM + Clean Architecture (data / domain / ui)
**Backend:** ASP.NET Core 8.0 API (ayrı repo) — `https://omniflow-backend-...azurewebsites.net`
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
| Harita | Google Maps Compose (M8'de) |
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
        │       │   ├── data/
        │       │   │   ├── local/
        │       │   │   │   ├── OmniFlowDatabase.kt   (Room)
        │       │   │   │   ├── dao/
        │       │   │   │   └── datastore/            (PreferencesManager)
        │       │   │   └── remote/
        │       │   │       └── dto/                  (PagedResponse, ErrorResponse, ortak)
        │       │   │
        │       │   ├── designsystem/                 ← TASARIMDAN gelir
        │       │   │   ├── theme/
        │       │   │   │   ├── Color.kt
        │       │   │   │   ├── Type.kt
        │       │   │   │   ├── Shape.kt
        │       │   │   │   └── OmniFlowTheme.kt
        │       │   │   └── components/
        │       │   │       ├── OmniButton.kt
        │       │   │       ├── OmniTextField.kt
        │       │   │       ├── OmniCard.kt
        │       │   │       ├── OmniTopBar.kt
        │       │   │       ├── LoadingIndicator.kt
        │       │   │       ├── ErrorView.kt
        │       │   │       └── EmptyState.kt
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
        │       └── features/                         ← Her feature: data / domain / ui
        │           │
        │           ├── auth/                         (M1)
        │           │   ├── data/
        │           │   │   ├── remote/
        │           │   │   │   ├── AuthApi.kt
        │           │   │   │   └── dto/              (LoginRequest, RegisterRequest, AuthResponse...)
        │           │   │   └── repository/
        │           │   │       └── AuthRepositoryImpl.kt
        │           │   ├── domain/
        │           │   │   ├── model/                (AuthUser, Tokens)
        │           │   │   ├── repository/
        │           │   │   │   └── AuthRepository.kt
        │           │   │   └── usecase/              (Login, Register, ForgotPassword, ResetPassword, VerifyEmail)
        │           │   └── ui/
        │           │       ├── splash/               (SplashScreen, SplashViewModel)
        │           │       ├── onboarding/
        │           │       ├── login/
        │           │       ├── register/
        │           │       ├── verifyemail/
        │           │       └── resetpassword/
        │           │
        │           ├── home/                         (M2)
        │           │   ├── data/ · domain/ · ui/
        │           │
        │           ├── profile/                      (M2)
        │           │   ├── data/ · domain/
        │           │   └── ui/  (me, edit, public, followers, following, suggested, topContributors, settings)
        │           │
        │           ├── notifications/                (M2/M6)
        │           │   └── data/ · domain/ · ui/
        │           │
        │           ├── trips/                        (M3)
        │           │   ├── data/ · domain/
        │           │   └── ui/
        │           │       ├── mytrips/
        │           │       ├── detail/
        │           │       ├── wizard/               (8 adım — her adım Composable + ortak WizardViewModel)
        │           │       ├── destinations/
        │           │       ├── timeline/             (list, createEntry, editEntry, reorder, visited)
        │           │       ├── budget/
        │           │       ├── recommendplaces/
        │           │       └── savedtrips/
        │           │
        │           ├── explore/                      (M4)
        │           │   └── data/ · domain/ · ui/ (explore, featured, search, placeDetail)
        │           │
        │           ├── providers/                    (M4)
        │           │   └── data/ · domain/ · ui/ (flights, hotels)
        │           │
        │           ├── social/                       (M5)
        │           │   ├── data/ · domain/
        │           │   └── ui/ (feed, postDetail, createPost, comments, tips)
        │           │
        │           ├── admin/                        (M6)
        │           │   └── data/ · domain/ · ui/ (dashboard, users, posts)
        │           │
        │           ├── livetrip/                     (M8) ← yeni
        │           │   └── data/ · domain/ · ui/ (liveMode, map, visitLog, summary)
        │           ├── collections/                  (M10) ← yeni
        │           ├── aichat/                       (M12) ← yeni
        │           └── moderation/                   (M11) ← yeni (report ekranları)
        │
        ├── test/                                     (JVM unit testler — ViewModel)
        │   └── java/com/omniflow/...
        │
        └── androidTest/                              (minimal — sadece kritik Compose UI)
            └── java/com/omniflow/...
```

### Mimari Akış (tek feature için)

```
UI (Composable) → ViewModel (UiState/Flow) → UseCase → Repository (interface, domain)
                                                              ↑
                                            RepositoryImpl (data) → Api / Dao
DTO ──(mapper)──► Domain Model ──(mapper)──► UI Model
```

| Katman | Sorumluluk | Bağımlılık |
|--------|------------|------------|
| **domain** | Saf iş modeli, UseCase, repository **interface**'leri | Hiçbir Android/framework bağımlılığı yok |
| **data** | Api, Dao, DTO, repository **implementasyon**, mapper | domain'i implemente eder |
| **ui** | Composable ekran + ViewModel + UiState | domain (UseCase) çağırır |

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
- [x] `core/` ve `features/` paket iskeleti oluştur (yukarıdaki şema)
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

### Task 1.1: AuthApi (Retrofit)

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] `AuthApi` — register, login, refresh, verify-email, resend-verification, forgot-password, reset-password endpoint imzaları

---

### Task 1.2: DTO + Domain Model + Mapper

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Request/Response DTO'ları (LoginRequest, RegisterRequest, AuthResponse...)
- [ ] Domain model (`AuthUser`, `Tokens`)
- [ ] DTO ↔ domain mapper'lar

---

### Task 1.3: AuthRepository + Impl

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] `AuthRepository` (domain interface)
- [ ] `AuthRepositoryImpl` (data) — Api çağrıları + `ApiResult` sarmalama + token saklama entegrasyonu

---

### Task 1.4: Auth UseCase'ler

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] `LoginUseCase`, `RegisterUseCase`
- [ ] `VerifyEmailUseCase`, `ResendVerificationUseCase`
- [ ] `ForgotPasswordUseCase`, `ResetPasswordUseCase`

---

### Task 1.5: Splash Ekranı

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Splash** — token kontrolü → Home veya Onboarding/Login yönlendirmesi
- [ ] `SplashViewModel` + UiState

---

### Task 1.6: Onboarding Ekranı

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Onboarding** (3 ekran, swipe)
- [ ] "görüldü" flag'i DataStore'da (bir kez gösterim)
- [ ] ViewModel + UiState

---

### Task 1.7: Login Ekranı

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Login** — email/şifre, hata gösterimi, "forgot password" linki
- [ ] Yanlış kimlik → inline "Email veya şifre hatalı" (401)
- [ ] Başarıda token saklanır → Home
- [ ] `LoginViewModel` + UiState

---

### Task 1.8: Register Ekranı

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Register** — username/email/şifre/şifre tekrar → 202 + "verify email" ekranına
- [ ] 422 → alan bazlı hata; duplicate email → inline
- [ ] ViewModel + UiState

---

### Task 1.9: Verify Email Info Ekranı

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Verify Email Info** — bilgi + resend (geri sayım)
- [ ] Resend hatası → snackbar
- [ ] ViewModel + UiState

---

### Task 1.10: Forgot & Reset Password Ekranları

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Forgot Password** — email input → reset link gönder
- [ ] **Reset Password** — token + yeni şifre; geçersiz/expired token → "Link geçersiz" + Login'e dön
- [ ] ViewModel'ler + UiState

---

### Ekran Durumları (M1)

| Ekran | Loading | Empty | Error | Success |
|-------|---------|-------|-------|---------|
| Splash | Tam ekran logo + spinner | — (yok) | Token yenilenemezse → Login'e düş | Geçerli oturum → Home, yoksa → Onboarding/Login |
| Onboarding | — | — | — | 3 sayfa swipe + "Başla/Giriş/Kayıt" |
| Login | Buton içi loading | — | Yanlış kimlik → inline "Email veya şifre hatalı" (401) | Token saklanır → Home |
| Register | Buton içi loading | — | 422 → alan bazlı hata; duplicate email → inline | 202 → Verify Email Info |
| Verify Email Info | Resend buton loading | — | Resend hatası → snackbar | "Mail gönderildi" + geri sayım |
| Forgot Password | Buton içi loading | — | Hata → snackbar | "Reset linki gönderildi" mesajı |
| Reset Password | Buton içi loading | — | Geçersiz/expired token → "Link geçersiz" + Login'e dön | Başarı → Login + snackbar |

### Definition of Done (M1)

- [ ] Kayıt → email doğrulama bilgisi → giriş akışı uçtan uca çalışıyor
- [ ] Giriş sonrası token saklanıyor, app yeniden açılınca oturum korunuyor
- [ ] Onboarding bir kez gösteriliyor
- [ ] Şifre sıfırlama akışı çalışıyor

### Test (Minimal)

- [ ] `LoginViewModel` unit testi (başarılı giriş, hatalı kimlik, validation)

---

## 🎯 M2 — Ana Navigasyon + Home + Profil

> **Backend:** Mevcut (users, notifications, explore/featured, media). Bağımlılık yok.

### Scope

Bottom navigation devreye girer; Home, bildirimler ve tüm profil/sosyal-kullanıcı ekranları.

---

### Task 2.1: Home Ekranı

**Tahmini Süre:** 3.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Home** — aktif/yaklaşan trip kartı, quick actions, featured trips (`GET /explore/featured`), önerilen aksiyonlar
- [ ] Aktif trip yoksa → "Bir trip planla" CTA kartı
- [ ] Featured boşsa o bölüm gizlenir
- [ ] `HomeViewModel` + UiState

---

### Task 2.2: Notifications Ekranı

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Notifications** — listeleme, okundu işaretle, tümünü okundu, unread badge (`/notifications`, `/notifications/unread-count`)
- [ ] Tarihe göre gruplu liste + unread vurgusu + pagination footer
- [ ] ViewModel + UiState

---

### Task 2.3: My Profile Ekranı

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **My Profile** — profil bilgisi, karma, followers/following sayıları, kendi postları/trip'leri (`/users/me`, `/users/me/posts`, `/users/{id}/trips`)
- [ ] Post/trip yoksa sekme içi "Henüz paylaşım yok"
- [ ] ViewModel + UiState

---

### Task 2.4: Edit Profile + Foto Yükleme

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Edit Profile** — bio + profil fotoğrafı yükleme (`PUT /users/me`, `POST /users/me/profile-photo`, media upload)
- [ ] Foto yükleme sırasında foto alanında loading; başarıda snackbar
- [ ] ViewModel + UiState

---

### Task 2.5: Public User Profile + Follow/Block

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Public User Profile** — başka kullanıcı (`/users/{username}`) + Follow/Unfollow/Block
- [ ] Engellenmiş kullanıcı → metrikler sıfır/gizli
- [ ] Follow toggle optimistic
- [ ] ViewModel + UiState

---

### Task 2.6: Followers / Following

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Followers / Following** — liste + search
- [ ] Search sonucu boşsa "Sonuç yok"

---

### Task 2.7: Suggested Follows + Top Contributors

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Suggested Follows** + **Top Contributors** — sıralı kullanıcı listesi

---

### Task 2.8: Settings Shell

**Tahmini Süre:** 0.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Settings (shell)** — logout + alt ayar girişleri (içerikler ileride dolacak)
- [ ] Logout → oturum temizle → Login

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
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **My Trips** — Draft/Published/Archived sekmeleri (`GET /trips`)
- [ ] Sekme bazlı "Henüz {Draft/Published/Archived} trip yok" + Wizard CTA
- [ ] Pagination footer
- [ ] ViewModel + UiState

---

### Task 3.2: Trip Detail Görünümü

**Tahmini Süre:** 2.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Trip Detail** — kapak, başlık, destinasyon özeti, timeline özeti, flight/hotel özeti, budget özeti
- [ ] Owner değilse edit/publish/delete gizli; Draft/Archived sadece owner'a görünür
- [ ] ViewModel + UiState

---

### Task 3.3: Trip Detail Aksiyonları

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Detail aksiyonları: publish, archive, edit, delete, save/unsave, upvote, fork
- [ ] Save/upvote optimistic + snackbar

---

### Task 3.4: Saved Trips

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Saved Trips** (`GET /saved-trips`)
- [ ] "Henüz kayıtlı trip yok" + Explore CTA
- [ ] Unsave → optimistic + snackbar

---

### Task 3.5: Wizard İskeleti & WizardViewModel

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Ortak `WizardViewModel` — adımlar arası state (her adımın verisi tek `WizardState`'te tutulur), ileri/geri navigasyon, her adımda kendi validasyonu geçmeden "Devam" pasif
- [ ] Her adım ayrı Composable; üstte ilerleme göstergesi (1/8 ...)
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

### Task 3.7: Wizard Adım 3 (Person Count) + Adım 4 (Travel Companion)

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Adım 3 — Person Count**
  - Alan: `personCount` (int)
  - Validasyon: `≥ 1` (stepper, makul üst sınır örn. 20)
- [ ] **Adım 4 — Travel Companion**
  - Alan: `travelCompanion` (enum: Solo / Couple / Family / Friends — backend `TravelCompanion`)
  - Validasyon: tek seçim zorunlu

---

### Task 3.8: Wizard Adım 5 (Budget) + Adım 6 (Travel Styles)

**Tahmini Süre:** 1.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Adım 5 — Budget**
  - Alanlar: `budgetTier` (Economy/Standard/Premium), `manualBudget` (decimal, opsiyonel)
  - Validasyon: tier zorunlu · manualBudget girilirse `> 0` · para birimi gösterimi
  - Bilgi: "Bütçe yetersizse sistem otomatik daha düşük tier önerebilir" (fallback notu)
- [ ] **Adım 6 — Vibe / Travel Styles**
  - Alan: `travelStyles` (multi-select, backend 11 değer: Romantic, Cultural, Adventure, Nature, Local, Relax, Shopping, Gastronomy, Influencer, Nightlife, Budget)
  - Validasyon: **en az 1, en fazla 3** seçim

---

### Task 3.9: Wizard Adım 7 (Tempo) + Adım 8 (Transport)

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Adım 7 — Tempo**
  - Alan: `tempo` (Slow / Moderate / Fast — backend `Tempo`)
  - Validasyon: tek seçim zorunlu · her birinin günlük kapasite etkisi açıklaması (Slow≈3, Moderate≈5, Fast≈7)
- [ ] **Adım 8 — Transport Preference**
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
- [ ] Son adımda `POST /trips/wizard` → `CreateTripWizardResponse` (budget fallback sonucu) → Trip Detail'e yönlendir

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

**Yapılacaklar:**
- [ ] **Timeline** — gün bazlı liste, lock/visited/sıralama durumu (`GET /trips/{id}/timeline`)
- [ ] Gün için "Bu güne entry yok" + ekle CTA
- [ ] Locked entry kilitli rozet

---

### Task 3.13: Create / Edit Timeline Entry

**Tahmini Süre:** 3 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Create/Edit Timeline Entry** — 5 tip (Place, CustomFlight, CustomTransport, CustomAccommodation, CustomEvent)
- [ ] Tip seçimine göre dinamik form (her tip farklı alan seti)

---

### Task 3.14: Timeline Reorder + Visited

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Reorder** (drag) → `PUT /timeline/reorder`
- [ ] **Visited** toggle
- [ ] Reorder/visited optimistic

---

### Task 3.15: Budget Summary

**Tahmini Süre:** 1 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Budget Summary** — gerçek zamanlı kırılım (`GET /trips/{id}/budget-summary`)
- [ ] Veri yoksa "Bütçe için entry ekle"
- [ ] Fallback uygulanmışsa bilgi rozeti (adjusted tier)

---

### Task 3.16: Recommend Places

**Tahmini Süre:** 0.5 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] **Recommend Places** — recommended/neutral/other (`GET /trips/{id}/recommend-places`) → timeline'a ekle
- [ ] "Öneri bulunamadı" → empty; Timeline'a ekle → snackbar

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

> ⛔ **Bağımlılık:** `BACKEND_ROADMAP_V2 → B2` (Visit Log, Trip Summary, Timezone). Ayrıca **Google Maps API anahtarı** gerekir.

### Scope

Seyahat sırasında kullanım: bugünün planı + harita + konum; gerçek ziyaret kayıtları; kapanış özeti.

---

### Task 8.1: Google Maps Compose + API Key

**Tahmini Süre:** 2 saat
**Durum:** [ ] Bekliyor

**Yapılacaklar:**
- [ ] Google Maps Compose + API key (Manifest)

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
| M0 | Proje kurulumu & iskelet | — | 🔄 |
| M1 | Auth & Onboarding | — | [ ] |
| M2 | Navigasyon + Home + Profil | — | [ ] |
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

- **Her feature aynı zincirle:** data (Api+DTO+Repo+mapper) → domain (model+UseCase) → ui (ViewModel+Composable). Önce data/domain, sonra UI.
- **State:** Her ekran tek bir `UiState` (loading/data/error) ile yönetilir; ViewModel `StateFlow` döner.
- **Hata yönetimi:** `ApiResult.Error` → `UiText` → kullanıcıya gösterim. 401 otomatik refresh (M0).
- **Görsel:** Coil ile lazy görsel; placeholder/hata state'leri design system component'lerinden.
- **Tasarım:** Tasarımı olmayan ekranlar mevcut design system component'leriyle, tasarım diline sadık biçimde üretilir.
- **Test:** Minimal — yalnızca kritik ViewModel unit testleri; gerisi manuel QA.
