# CivilReport

*A free, open‑source mobile app that lets civil engineers create professional site reports in minutes.*

---

## ✨ Overview

CivilReport streamlines the on‑site reporting workflow for **civil engineers, inspectors, and project managers**. Capture photos, sketches, and notes, then generate a polished PDF in a single tap—no desktop software, no recurring fees.

Originally commissioned by a private engineering firm, the project is now public so practitioners everywhere can benefit and contribute.

---

## 🚀 Key Features

- **Template‑driven reports** – Build once, reuse on every job.
- **Offline‑first data capture** – Work in tunnels, basements, or remote sites; sync later.
- **Photo & markup tools** – Annotate images right inside the app.
- **Rich inputs** –  GPS coordinates, checklists, signatures.
- **One‑tap export** – Generate PDF (or DOCX) bundles you can email on the spot.
- **Custom branding** – Insert your company logo, colours, and footer text.
- **Free & extensible** – Apache‑2.0 license, no proprietary lock‑in.

---

## 🛠 Tech Stack

| Layer              | Choice                            |
| ------------------ | --------------------------------- |
| **Platform**       | Android (Kotlin) |
| **Persistence**    | Room + DataStore                  |
| **Sync**           | Firebase module                   |
| **PDF generation** | OpenPDF                           |
| **DI & tooling**   | Hilt, Coroutines, KSP             |

> **No private keys** or paid SDKs are stored in this repository

---

## 📂 Repository Layout

```
app/            👉 Android client
  └─ ui/        👉 Jetpack Compose screens
  └─ data/      👉 Room entities & DAOs
  └─ export/    👉 PDF builder
scripts/        👉 helper scripts (CI, lint)
```

*(Structure may evolve as modules are extracted.)*

---

## 🔄 Project Status

- ✅ Core domain model & local storage
- ✅ Basic PDF export (single‑page template)
- 🔄 Multi‑page templates with tables *(in progress)*
- 🔜 Cloud sync & web dashboard

---

##

---

##
