# Farmer Records App — Lab 01_hm1

**JKUAT | ICS 2300 | Mobile Application Development**  
**Student:** Patrick Kilonzo Mbithi (SCT211-0036/2025)  
**Instructor:** Dr. Lawrence Nderu  
**Due:** 18 November 2026, 11:59 p.m. EAT

---

## 📱 App Overview

A complete Android (Java) application that captures **farmer records** with validation, displays them in a `RecyclerView` with a live count, and correctly handles **Android lifecycle events** (rotation, Home/return, process restart).

### Features
| Feature | Implementation |
|---------|----------------|
| **Input Fields** | Farmer Name/Reference, Crop, Quantity (kg) — Material `TextInputLayout` + `TextInputEditText` |
| **Validation** | Required fields, numeric quantity, ≥ 0, finite, zero allowed (valid zero harvest) |
| **Error Display** | Field-level red errors via `TextInputLayout.setError()` |
| **Record List** | `RecyclerView` with adapter, newest record at top |
| **Live Count** | `TextView` updates on every insert |
| **Rotation Handling** | `onSaveInstanceState` restores draft field values |
| **Process Restart** | In-memory data lost (expected — no persistent storage in this lab) |

---

## 🏗 Project Structure

```
Farmerrecords/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/farmerrecords/
│   │   │   └── MainActivity.java          # Complete Activity logic
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml      # Material Design form + RecyclerView
│   │   │   ├── values/
│   │   │   │   ├── themes.xml             # MaterialComponents theme
│   │   │   │   ├── colors.xml
│   │   │   │   └── strings.xml
│   │   │   ├── xml/
│   │   │   │   ├── backup_rules.xml
│   │   │   │   └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle                   # Dependencies: Material, RecyclerView, AppCompat
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── gradle/
├── gradlew, gradlew.bat
├── FarmerRecords_App_Walkthrough.html     # 📖 Full lab walkthrough (open in browser)
└── screenshots/                           # 📸 Place your evidence screenshots here
```

---

## 🚀 Build & Run

### Prerequisites
- **Android Studio** Koala (2024.1.2) or later
- **JDK 17+**
- **Android SDK** API 34 (compileSdk 34, minSdk 24)

### Steps
```bash
# Clone the repo
git clone https://github.com/ai-patrick/farmer_records_app.git
cd farmer_records_app

# Open in Android Studio
# File → Open → select the Farmerrecords folder

# Build & Run
# Click ▶ Play button (emulator API 34 or physical device)
```

Or from command line:
```bash
./gradlew assembleDebug
# Install on connected device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📖 Documentation

### HTML Walkthrough (Primary Submission)
Open **`FarmerRecords_App_Walkthrough.html`** in any browser for the complete lab documentation following the **form_input_application format**:

1. **Lab Objective** — Feature cards
2. **How the App Works** — Workflow + component map
3. **User Interface** — XML layout breakdown (header, 3 fields, button, list)
4. **Activity Logic** — `onCreate`, `onSaveInstanceState`, `onSubmit`, validation, model, adapter
5. **Themes & Dependencies** — MaterialComponents requirement
6. **Execution & Evidence** — 📸 **Screenshot guide with 10 evidence items**
7. **Lifecycle Trace** — Predicted vs observed table + what-survives-what
8. **Design Choices, Defect Corrected, Limitation**
9. **Relation to MavunoLink**

> **For PDF submission:** Open HTML in browser → Print → Save as PDF (enable "Background graphics").

### Screenshots (Evidence)
Create a `screenshots/` folder next to the HTML and add these files:

| File | Evidence ID | Captures |
|------|-------------|----------|
| `step-1-empty-form.png` | `launch-empty-001` | Cold launch, empty form |
| `step-2-validation-errors.png` | `validation-all-empty-002` | All 3 required-field errors |
| `step-3-quantity-negative.png` | `validation-qty-neg-003` | Negative quantity rejected |
| `step-3-quantity-nonnumeric.png` | `validation-qty-nonnumeric-004` | Non-numeric rejected |
| `step-3-quantity-nonfinite.png` | `validation-qty-nonfinite-005` | Non-finite rejected |
| `step-4-valid-record.png` | `valid-record-added-006` | Valid input → list + count |
| `step-5-multiple-records.png` | `multiple-records-007` | Multiple records, newest first |
| `step-6-rotation-before.png` | `rotation-test-008` | Partial form before rotation |
| `step-6-rotation-after.png` | `rotation-test-008` | Form restored after rotation |
| `step-7-home-return.png` | `home-return-009` | Home/return state intact |
| `step-8-process-restart.png` | `process-restart-010` | Process restart → data lost |

---

## ✅ Lab Requirements Coverage

| Lab Task | Status | Location |
|----------|--------|----------|
| 1. Create/reuse farmer-record app; capture name, crop, quantity; display in list with count; explain input→model→adapter→list | ✅ | `MainActivity.java`, `activity_main.xml`, Walkthrough §3–4 |
| 2. Define validation: reject missing identity, blank/negative/non-numeric/non-finite quantity; explain zero distinction | ✅ | `onSubmit()`, Walkthrough §4.4, §6 Step 3 |
| 3. Record lifecycle events: launch, Home, return, rotation; predict sequence; run cases; explain differences; retain draft across rotation; explain what survives process restart | ✅ | `onSaveInstanceState`, Walkthrough §6 Steps 6–8, §7 |
| 4. Explain two design choices, one defect corrected, one limitation; relate to MavunoLink | ✅ | Walkthrough §8–9 |

---

## 🐛 Known Defect (Corrected)

**Issue:** Early version rejected zero quantity (`quantity <= 0`).  
**Fix:** Changed to `quantity < 0` + explicit `Double.isFinite()` check.  
**Reasoning:** Zero weekly harvest is a **valid business event** (different from earlier "delivery" exercise which required positive quantity).

---

## ⚠ Limitation

**No persistent storage** — records are kept in an in-memory `ArrayList`. On process restart (swipe from Recents), all data is lost. For MavunoLink production, this would use **Room database** + **WorkManager** sync.

---

## 🔗 Relation to MavunoLink

| This Lab | MavunoLink Production |
|----------|----------------------|
| Free-text farmer name | Lookup from farmer registry (ID + cache) |
| Free-text crop | Spinner from crop master data |
| Client-side validation only | Client + server validation (max yield/acre) |
| In-memory `ArrayList` | Room DAO + LiveData/Flow observed by RecyclerView |
| Activity-scoped lifecycle | ViewModel + SavedStateHandle + WorkManager |

---

## 📦 Submission Checklist

- [ ] Android project builds and runs (API 24+)
- [ ] All 3 fields validate correctly (empty, negative, non-numeric, non-finite)
- [ ] Zero quantity accepted
- [ ] Records appear in RecyclerView, newest first
- [ ] Record count updates live
- [ ] Rotation restores draft field values
- [ ] Home/return preserves list and drafts
- [ ] Process restart loses data (expected)
- [ ] Screenshots captured for all 10 evidence items
- [ ] `FarmerRecords_App_Walkthrough.html` complete with screenshots
- [ ] PDF generated from HTML (with background graphics)
- [ ] Source ZIP or repo snapshot included
- [ ] Submission label: `Lab_01_hm1_Mbithi_SCT211-0036-2025.pdf`

---

## 📄 License

Academic submission for JKUAT ICS 2300. Not for production use.

---

**Generated:** November 2026  
**Environment:** Android Studio Koala | Emulator API 34 | compileSdk 34 | minSdk 24  
**AI Assistance:** HTML documentation generation only; all code written and tested manually.