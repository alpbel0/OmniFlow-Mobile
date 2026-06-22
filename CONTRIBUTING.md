# Git Conventions — OmniFlow

Turkcell tarzı commit/branch/merge disiplini, solo proje + GitHub Issues'a uyarlanmış hali.

## 1. Commit Format

```
Keyword: [#issue-no] Short summary
```

Issue yoksa (örn. küçük bir bump ya da typo fix) `[#no]` kısmı atlanabilir:

```
Bump: Set app version to 1.2.0
```

### Keywords

| Keyword    | Anlamı                                              |
|------------|------------------------------------------------------|
| Feature    | Yeni özellik, ekran, bağımlılık ekleme                |
| Remove     | Özellik/bağımlılık kaldırma                           |
| Redesign   | UI/UX yeniden tasarım                                 |
| Refactor   | Davranış değişmeden kod yeniden yapılandırma          |
| Bugfix     | Hata düzeltme                                         |
| Bump       | Versiyon/dependency yükseltme                         |
| Build      | Build sistemi, CI/CD, tooling değişiklikleri          |
| Document   | Sadece dokümantasyon                                  |
| Revert     | Önceki commit'i geri alma                             |
| Test       | Unit/UI test yazma                                    |

### Yazım kuralları (Chris Beams – 7 madde)

1. Subject ile body arasına boş satır koy
2. Subject satırını 50 karaktere sığdır
3. Subject'i büyük harfle başlat
4. Subject sonuna nokta koyma
5. Subject'te emir kipi kullan ("Add", "Fix", "Update" — "Added", "Fixed" değil)
6. Body'yi 72 karakterde satır kaydır
7. Body'de **ne** ve **neden** olduğunu açıkla, **nasıl** olduğunu değil

### Örnekler

✅ İyi:
```
Feature: [#42] Add destination search to Explore screen
Bugfix: [#57] Fix crash on empty itinerary list
Refactor: [#42] Extract trip repository to separate module
Bump: Update Kotlin to 2.0.0
```

❌ Kötü:
```
Fix scroll
Added files for explore screen
Update wallet detail translations.
```

## 2. Branch Naming

```
feature/<issue-no>_<kebab-case-description>
bugfix/<issue-no>_<kebab-case-description>
project/<issue-no>_<kebab-case-description>   # birden fazla feature/bugfix PR'ını içeren büyük iş
revert/<issue-no>_<kebab-case-description>
```

Örnekler:
```
feature/42_destination-search
bugfix/57_itinerary-crash
project/30_onboarding-redesign
```

## 3. Merge Stratejileri

- `feature/*` veya `bugfix/*` → `project/*`: **squash** veya **fast-forward**
- `project/*` veya `feature/*` → `main`/`develop`: **sadece squash**
- Hotfix → `main`: squash

PR'ı merge ederken commit message'ı GitHub'ın default'una bırakma — yukarıdaki formata göre yeniden yaz:

```
❌ Merge pull request #12 from yigitalp/feature/42_destination-search
✅ Feature: [#42] Add destination search to Explore screen
```

## 4. Sync (kendi branch'ini güncel tutma)

- `main`/`develop`'tan **rebase** al (önerilen), gerekirse merge. PR açmadan direkt push edebilirsin.
- `project/*`'ten açılan `feature/*`/`bugfix/*` branch'leri **project'ten rebase** almalı.

## 5. Otomatik Kontroller

| Aşama              | Kontrol eden                     | Ne kontrol eder                            |
|--------------------|-----------------------------------|---------------------------------------------|
| `git commit`       | `.githooks/commit-msg`           | Commit mesajı formatı (local)                |
| `git push`         | `.githooks/pre-push`             | Branch ismi formatı (local)                  |
| PR açma/güncelleme | `.github/workflows/pr-lint.yml`  | Branch ismi + PR title formatı (GitHub Actions, CI) |

Kurulum (repo kökünde):

```bash
git config core.hooksPath .githooks
chmod +x .githooks/commit-msg .githooks/pre-push
```

CI check'i fail olursa PR'a kırmızı ✗ düşer. Mergeable olmasını engellemesini istiyorsan: **Settings → Branches → Branch protection rule → "Require status checks to pass before merging"** kısmından `check-branch-name` ve `check-pr-title` job'larını seçili işaretle.
