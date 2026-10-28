# 🛒 SmartShelf 5.0 — Machine Learning Retail Demand & Inventory Restocking System

**SmartShelf 5.0** is an enterprise-grade, full-stack AI/ML grocery shop management system. It integrates a **supervised Machine Learning demand forecasting pipeline** (XGBoost, Random Forest, Gradient Boosting) into a high-velocity supermarket management platform.

---

## 🌟 What's New in SmartShelf 5.0: ML Demand Forecasting Pipeline

1. **Supervised Regression ML Engine**:
   - Model Trained: **XGBoost 3.2.0** (alongside Random Forest, Gradient Boosting, Decision Trees, Ridge, Linear Regression).
   - Metrics: **R² Score: 0.8423**, **MAE: 8.31 units**, **RMSE: 11.18**, **MAPE: 24.0%**.
   - Validated with **5-Fold Cross Validation** (R²: 0.8350).

2. **14 Engineered Predictive Features**:
   - **Temporal**: `Days_Since_Last_Order`, `Shelf_Life_Days`, `Receive_Month`, `Receive_DayOfWeek`
   - **Financial & Stock**: `Price_x_Stock`, `Below_Reorder`
   - **Operational**: `Inventory_Turnover_Rate`, `Unit_Price`, `Stock_Quantity`, `Reorder_Level`, `Reorder_Quantity`
   - **Encodings**: `Category_Enc`, `Status_Enc`, `Supplier_Name_Enc`

3. **4-Tier Automated Restocking Matrix**:
   - 🔴 **URGENT RESTOCK**: `Stock < Reorder_Level` AND `Predicted Sales > Median`
   - 🟠 **RESTOCK**: `Stock < Reorder_Level` (Standard priority)
   - 🟡 **MONITOR**: `Stock <= 1.2 * Reorder_Level` (Within safety buffer)
   - 🟢 **SUFFICIENT**: Stock comfortably above reorder threshold
   - Auto-computes **Suggested Order Quantity** and **Estimated Procurement Expenditure ($ / ₹)**.

4. **Interactive "What-If" Demand Simulator**:
   - Live sliders to adjust price, stock, turnover, and reorder levels to see real-time ML inferences.

5. **Complete Jupyter Notebook (`Smart_Retail_Demand_Inventory_Prediction.ipynb`)**:
   - Fully documented CRISP-DM lifecycle notebook ready to run via `start_jupyter.bat`.
   - Downloadable directly from the web browser UI.

---

## 🚀 All Previous Features Kept 100% Intact

- 🛒 **POS Billing Terminal**: Barcode scanning, customer search, split payments, thermal receipts.
- 💬 **WhatsApp Integration**: Instant 1-click WhatsApp tax invoice dispatch & Udhar payment reminders.
- 🏷️ **Printable Shelf Barcode Stickers**: 60mm &times; 38mm adhesive price tags with Code128 barcodes.
- 🤖 **Google Gemini Vision AI Slip OCR**: Digitize handwritten paper grocery slips and 1-click transfer to POS.
- 📦 **Inventory & Audits**: Real-time stock tracking, physical count corrections, audit trails.
- 👥 **Customer Udhar Khaata**: Credit ledger tracking, repayment receipt generation.
- 📊 **Executive Analytics & CSV Exports**: Gross profit, COGS, inventory valuation, sales reports.

---

## 🏃 Quick Start Guide

### Option A: 1-Click Launch (Recommended)
Double-click:
- **`start_all.bat`** — Launches both Spring Boot backend (port 8090) and React frontend (port 5173).

### Option B: Individual Components
- **`start_backend.bat`** — Starts Spring Boot on `http://localhost:8090`
- **`start_frontend.bat`** — Starts React Vite on `http://localhost:5173`
- **`start_jupyter.bat`** — Launches Jupyter Notebook `Smart_Retail_Demand_Inventory_Prediction.ipynb`
- **`run_ml_training.bat`** — Re-trains and benchmarks all 6 ML models

---

## 🔑 Login Credentials

- **Store Owner / Admin**: `admin` / `admin123`
- **Cashier / Staff**: `cashier` / `cashier123`
