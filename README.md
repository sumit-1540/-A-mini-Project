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
   - Fully documented CRISP-DM lifecycle notebook.
   - Downloadable directly from the web browser UI.

---

## 🚀 All Features Kept 100% Intact

- 🛒 **Dual POS Terminal**: Express Walk-in billing & Daily Customer billing with loyalty points.
- 💬 **WhatsApp Integration**: Instant 1-click WhatsApp tax invoice dispatch & credit repayment reminders.
- 🏷️ **Printable Shelf Barcode Stickers**: 60mm &times; 38mm adhesive price tags with Code128 barcodes.
- 🤖 **Google Gemini Vision AI Slip OCR**: Digitize handwritten paper grocery slips and transfer to POS.
- 📦 **Inventory & Audits**: Real-time stock tracking, physical count corrections, audit trails.
- 👥 **Customer Credit (Udhar) Khaata**: Credit ledger tracking, repayment receipt generation.
- 📊 **Executive Analytics & CSV Exports**: Gross profit, COGS, inventory valuation, sales reports.

---

## 🏃 Local Quick Start Guide

### Option A: 1-Click Launch (Recommended)
Double-click:
- **`start_all.bat`** — Launches both Spring Boot backend (port 8093) and React frontend (port 5173).

### Option B: Individual Components
- **`start_backend.bat`** — Starts Spring Boot on `http://localhost:8093`
- **`start_frontend.bat`** — Starts React Vite on `http://localhost:5173`

---

## ☁️ Cloud Deployment on Render (Single Web Service)

SmartShelf is fully configured for zero-CORS single-service deployment on **Render's Free Tier** using a multi-stage Dockerfile that bundles the React frontend and Spring Boot backend into one container.

### Step 1: Push Project to GitHub
```bash
git remote add origin https://github.com/<YOUR-USERNAME>/<YOUR-REPO-NAME>.git
git branch -M main
git push -u origin main
```

### Step 2: Deploy on Render
1. Go to [dashboard.render.com](https://dashboard.render.com) and click **"New +"** $\rightarrow$ **"Web Service"**.
2. Select your GitHub repository.
3. Configuration:
   - **Runtime**: `Docker`
   - **Dockerfile Path**: `./Dockerfile`
   - **Instance Type**: `Free`
4. Set **Environment Variables**:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<aiven-host>:22069/grocery_db?sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - `SPRING_DATASOURCE_USERNAME`: `avnadmin`
   - `SPRING_DATASOURCE_PASSWORD`: `<your-aiven-password>`
   - `SMARTSHELF_UPI_VPA`: `farandesumit@oksbi`
   - `SMARTSHELF_MERCHANT_NAME`: `Sumit Farande`
5. Click **"Deploy Web Service"**.

Render will automatically build the container and provide a live URL: `https://<service-name>.onrender.com`.

---

## 🔑 Login Credentials

- **Store Owner / Admin**: `admin` / `admin123`
- **Cashier / Staff**: `cashier` / `cashier123`
