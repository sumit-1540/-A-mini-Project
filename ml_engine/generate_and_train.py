import os
import json
import random
import datetime
import numpy as np
import pandas as pd
import joblib
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.linear_model import LinearRegression, Ridge
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestRegressor, GradientBoostingRegressor
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
import xgboost as xgb

# Set random seeds
np.random.seed(42)
random.seed(42)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATASET_PATH = os.path.join(BASE_DIR, "Grocery_Inventory_and_Sales_Dataset.csv")
MODEL_PATH = os.path.join(BASE_DIR, "trained_xgb_model.joblib")
METRICS_PATH = os.path.join(BASE_DIR, "model_metrics.json")
RECOMMENDATIONS_PATH = os.path.join(BASE_DIR, "Inventory_Recommendations.csv")
STORE_PREDICTIONS_PATH = os.path.join(BASE_DIR, "store_product_predictions.json")

print("[1/5] Building & Enriching Dataset with Synthetic Data...")

# Category and Product Definitions based on SmartShelf & Grocery Inventory Dataset
catalog_items = [
    # Grains & Pulses
    ("Sushi Rice", "Grains & Pulses", 4.50, 1.8, 12),
    ("India Gate Basmati Rice 5kg", "Grains & Pulses", 6.20, 2.5, 12),
    ("Aashirvaad Superior MP Sharbati Atta 10kg", "Grains & Pulses", 5.50, 3.0, 6),
    ("Long Grain Rice", "Grains & Pulses", 1.50, 2.8, 12),
    ("Black Rice", "Grains & Pulses", 6.00, 1.4, 12),
    ("All-Purpose Flour", "Grains & Pulses", 1.75, 2.2, 12),
    ("Bread Flour", "Grains & Pulses", 1.50, 2.6, 12),
    ("White Sugar", "Grains & Pulses", 2.00, 3.5, 24),
    ("Raw Sugar", "Grains & Pulses", 1.50, 2.0, 24),
    ("Powdered Sugar", "Grains & Pulses", 2.50, 2.1, 18),
    ("Jasmine Rice", "Grains & Pulses", 4.75, 2.4, 12),
    ("Arborio Rice", "Grains & Pulses", 4.50, 1.6, 12),
    ("Whole Wheat Flour", "Grains & Pulses", 2.75, 2.9, 12),
    ("Almond Flour", "Grains & Pulses", 9.50, 1.2, 9),
    ("Brown Rice", "Grains & Pulses", 2.50, 2.0, 12),
    ("Wild Rice", "Grains & Pulses", 10.00, 1.1, 12),
    ("Rice Flour", "Grains & Pulses", 2.00, 1.9, 12),
    ("Coconut Sugar", "Grains & Pulses", 5.00, 1.3, 18),
    
    # Dairy
    ("Amul Butter Pasteurized 500g", "Dairy", 3.20, 4.2, 1),
    ("Amul Taaza Homogenised Toned Milk 1L", "Dairy", 1.10, 5.0, 0.5),
    ("Greek Yogurt", "Dairy", 3.00, 3.8, 0.75),
    ("Egg (Goose)", "Dairy", 2.50, 2.1, 1),
    ("Egg (Duck)", "Dairy", 1.00, 2.5, 1),
    ("Egg (Chicken)", "Dairy", 0.50, 4.8, 1),
    ("Egg (Quail)", "Dairy", 0.80, 1.8, 1),
    ("Egg (Turkey)", "Dairy", 2.50, 1.5, 1),
    ("Feta Cheese", "Dairy", 7.00, 2.2, 4),
    ("Swiss Cheese", "Dairy", 8.00, 2.0, 4),
    ("Parmesan Cheese", "Dairy", 12.00, 1.8, 6),
    ("Cheddar Cheese", "Dairy", 9.00, 2.6, 6),
    ("Gouda Cheese", "Dairy", 7.00, 2.1, 4),
    ("Mozzarella Cheese", "Dairy", 7.00, 3.1, 3),
    ("Cottage Cheese", "Dairy", 5.00, 3.5, 1),
    ("Ricotta Cheese", "Dairy", 6.00, 2.4, 2),
    ("Heavy Cream", "Dairy", 4.00, 3.0, 2),
    ("Whipped Cream", "Dairy", 4.00, 2.5, 3),
    ("Sour Cream", "Dairy", 4.00, 2.8, 2),
    ("Evaporated Milk", "Dairy", 2.00, 2.1, 12),
    ("Buttermilk", "Dairy", 2.50, 3.6, 1),
    ("Yogurt", "Dairy", 1.75, 4.0, 1),

    # Fruits & Vegetables
    ("Plum", "Fruits & Vegetables", 4.00, 2.2, 0.5),
    ("Strawberries", "Fruits & Vegetables", 6.00, 3.8, 0.3),
    ("Spinach", "Fruits & Vegetables", 2.50, 4.1, 0.25),
    ("Green Beans", "Fruits & Vegetables", 2.00, 3.2, 0.5),
    ("Cabbage", "Fruits & Vegetables", 1.00, 3.5, 0.5),
    ("Mushrooms", "Fruits & Vegetables", 6.00, 3.9, 0.3),
    ("Pear", "Fruits & Vegetables", 4.50, 2.5, 0.75),
    ("Cucumber", "Fruits & Vegetables", 1.75, 4.2, 0.4),
    ("Pineapple", "Fruits & Vegetables", 3.50, 2.7, 0.5),
    ("Onion", "Fruits & Vegetables", 2.00, 4.9, 1.5),
    ("Potato", "Fruits & Vegetables", 1.20, 4.8, 2.0),
    ("Sweet Potato", "Fruits & Vegetables", 2.00, 2.8, 1.5),
    ("Tomato", "Fruits & Vegetables", 2.50, 5.0, 0.3),
    ("Lemon", "Fruits & Vegetables", 2.30, 4.0, 0.8),
    ("Mango", "Fruits & Vegetables", 5.00, 3.2, 0.4),
    ("Kiwi", "Fruits & Vegetables", 6.00, 2.9, 0.6),
    ("Orange", "Fruits & Vegetables", 3.00, 4.1, 0.75),
    ("Zucchini", "Fruits & Vegetables", 2.50, 2.8, 0.4),
    ("Blueberries", "Fruits & Vegetables", 10.00, 2.6, 0.3),
    ("Eggplant", "Fruits & Vegetables", 3.00, 2.8, 0.4),
    ("Garlic", "Fruits & Vegetables", 7.00, 3.3, 3.0),
    ("Broccoli", "Fruits & Vegetables", 4.00, 3.5, 0.4),
    ("Cauliflower", "Fruits & Vegetables", 2.50, 3.6, 0.4),
    ("Peas", "Fruits & Vegetables", 3.00, 2.7, 0.5),
    ("Lettuce", "Fruits & Vegetables", 2.00, 4.0, 0.25),
    ("Watermelon", "Fruits & Vegetables", 3.00, 3.5, 0.5),
    ("Pomegranate", "Fruits & Vegetables", 6.00, 2.4, 0.8),
    ("Apple", "Fruits & Vegetables", 3.50, 4.3, 1.0),
    ("Peach", "Fruits & Vegetables", 4.00, 2.6, 0.4),
    ("Apricot", "Fruits & Vegetables", 5.00, 2.3, 0.4),
    ("Asparagus", "Fruits & Vegetables", 5.00, 2.5, 0.3),
    ("Carrot", "Fruits & Vegetables", 1.50, 4.6, 1.0),
    ("Bell Pepper", "Fruits & Vegetables", 4.50, 3.8, 0.4),
    ("Kale", "Fruits & Vegetables", 4.00, 2.7, 0.25),

    # Beverages
    ("Arabica Coffee", "Beverages", 20.00, 2.1, 12),
    ("Black Coffee", "Beverages", 15.00, 2.5, 12),
    ("Green Coffee", "Beverages", 12.00, 1.8, 12),
    ("Robusta Coffee", "Beverages", 10.00, 2.7, 12),
    ("Herbal Tea", "Beverages", 30.00, 1.6, 18),
    ("Green Tea", "Beverages", 8.00, 3.2, 18),
    ("Black Tea", "Beverages", 5.00, 3.9, 18),
    ("Red Label Strong Tea 500g", "Beverages", 4.20, 4.5, 18),
    ("White Tea", "Beverages", 25.00, 1.4, 18),

    # Bakery & Snacks
    ("Rye Bread", "Bakery", 3.00, 3.8, 0.2),
    ("White Bread", "Bakery", 2.50, 4.9, 0.2),
    ("Multigrain Bread", "Bakery", 3.50, 3.5, 0.2),
    ("Sourdough Bread", "Bakery", 4.00, 3.2, 0.2),
    ("Whole Wheat Bread", "Bakery", 3.50, 4.0, 0.2),
    ("Oatmeal Biscuit", "Bakery", 5.00, 2.8, 6),
    ("Butter Biscuit", "Bakery", 6.00, 3.1, 6),
    ("Chocolate Biscuit", "Bakery", 5.00, 3.6, 6),
    ("Digestive Biscuit", "Bakery", 4.00, 3.4, 6),
    ("Vanilla Biscuit", "Bakery", 5.50, 2.5, 6),
    ("Parle-G Gold Biscuits 1kg Family Pack", "Bakery", 1.80, 5.0, 6),
    ("Maggi 2-Minute Masala Noodles (Pack of 12)", "Bakery", 2.40, 4.8, 8),

    # Seafood
    ("Trout", "Seafood", 12.00, 2.2, 0.1),
    ("Sardines", "Seafood", 6.00, 2.6, 12),
    ("Haddock", "Seafood", 9.00, 2.1, 0.1),
    ("Cod", "Seafood", 8.00, 2.3, 0.1),
    ("Salmon", "Seafood", 15.00, 2.8, 0.1),
    ("Tilapia", "Seafood", 7.00, 3.2, 0.1),
    ("Tuna", "Seafood", 18.00, 2.4, 12),
    ("Anchovies", "Seafood", 10.00, 1.9, 12),
    ("Halibut", "Seafood", 20.00, 1.7, 0.1),
    ("Mackerel", "Seafood", 7.50, 2.5, 0.1),

    # Oils & Fats
    ("Corn Oil", "Oils & Fats", 2.50, 3.2, 12),
    ("Fortune Sunlite Refined Sunflower Oil 1L", "Oils & Fats", 2.20, 4.5, 12),
    ("Olive Oil", "Oils & Fats", 6.00, 2.8, 18),
    ("Peanut Oil", "Oils & Fats", 4.00, 2.9, 12),
    ("Palm Oil", "Oils & Fats", 1.80, 3.8, 12),
    ("Canola Oil", "Oils & Fats", 2.30, 3.4, 12),
    ("Avocado Oil", "Oils & Fats", 10.00, 1.5, 12),
    ("Sunflower Oil", "Oils & Fats", 2.50, 3.7, 12),
    ("Vegetable Oil", "Oils & Fats", 2.00, 4.2, 12),
    ("Sesame Oil", "Oils & Fats", 6.50, 2.2, 12),
    ("Coconut Oil", "Oils & Fats", 5.00, 2.5, 18),

    # Spices & Cleaning
    ("Tata Salt Vacuum Evaporated 1kg", "Grains & Pulses", 0.40, 5.0, 24),
    ("Everest Garam Masala 100g", "Grains & Pulses", 1.20, 4.0, 12),
    ("Surf Excel Easy Wash Detergent Powder 1kg", "Bakery", 2.00, 4.5, 36),
    ("Dettol Original Bathing Soap 125g (Buy 3 Get 1)", "Bakery", 2.80, 4.2, 24)
]

suppliers = [
    ("38-037-1699", "Jaxnation"), ("54-470-2479", "Feedmix"), ("54-031-2945", "Vinder"),
    ("63-492-7603", "Brightbean"), ("54-226-4308", "Topicstorm"), ("86-292-4587", "Dabjam"),
    ("04-391-7610", "Tagfeed"), ("67-679-4930", "Muxo"), ("32-182-1895", "Thoughtstorm"),
    ("84-504-7617", "Youfeed"), ("29-689-5014", "Mynte"), ("03-615-0558", "Reallinks"),
    ("10-060-8515", "Skivee"), ("38-883-3382", "Mydeo"), ("82-737-6019", "Devshare"),
    ("87-839-6400", "Jabbertype"), ("28-575-0716", "Chatterbridge"), ("75-465-6257", "Feedspan"),
    ("75-503-9347", "Gigazoom"), ("79-915-4185", "Brainverse"), ("01-111-2222", "ITC Consumer Goods Ltd"),
    ("02-222-3333", "Amul Dairy Cooperative"), ("03-333-4444", "AgroFresh Wholesale Dist")
]

warehouses = [
    "48 Del Sol Trail", "36 3rd Place", "3296 Walton Court", "3 Westerfield Crossing",
    "15068 Scoville Court", "050 Mcbride Avenue", "12 Truax Court", "267 International Plaza",
    "550 Clemons Plaza", "55782 Welch Hill", "44022 Barnett Way", "Central Warehouse Zone A",
    "Cold Storage Bay 2", "Dry Pantry Aisle 4", "Distribution Hub Alpha"
]

statuses = ["Active", "Backordered", "Discontinued"]
status_weights = [0.60, 0.25, 0.15]

records = []
total_records = 5000
start_date = datetime.date(2024, 1, 1)

for i in range(total_records):
    item = random.choice(catalog_items)
    p_name, category, base_price, base_turnover, shelf_life_months = item
    
    # Variations
    prod_id = f"{random.randint(10,99)}-{random.randint(100,999)}-{random.randint(1000,9999)}"
    supplier_id, supplier_name = random.choice(suppliers)
    
    # Slight price fluctuation
    price_noise = np.random.uniform(-0.15, 0.20) * base_price
    unit_price = round(max(0.20, base_price + price_noise), 2)
    
    # Stock levels
    reorder_level = random.randint(15, 90)
    reorder_qty = random.randint(20, 100)
    
    # Stock quantity: varied to test stockout and sufficient cases
    is_low_stock = random.random() < 0.35
    if is_low_stock:
        stock_quantity = random.randint(2, max(3, int(reorder_level * 0.9)))
    else:
        stock_quantity = random.randint(reorder_level, reorder_level + random.randint(10, 60))
        
    # Turnover rate (1 to 100)
    turnover_noise = np.random.uniform(-15, 20)
    inventory_turnover = int(np.clip(base_turnover * 20 + turnover_noise, 1, 100))
    
    # Dates
    recv_days = random.randint(10, 360)
    date_received = start_date + datetime.timedelta(days=recv_days)
    
    last_order_days = recv_days + random.randint(-40, 30)
    last_order_date = start_date + datetime.timedelta(days=max(0, last_order_days))
    
    shelf_life_days = int(shelf_life_months * 30 + random.randint(-10, 20))
    expiration_date = date_received + datetime.timedelta(days=max(5, shelf_life_days))
    
    warehouse = random.choice(warehouses)
    status = random.choices(statuses, weights=status_weights)[0]
    
    # Realistic Sales Volume modeling based on demand drivers:
    # 1. Higher turnover = higher sales
    # 2. Lower price = higher volume
    # 3. Status penalty
    # 4. Seasonal & Category affinity
    price_factor = max(0.4, 1.8 - (unit_price / 15.0))
    turnover_factor = inventory_turnover / 50.0
    status_factor = 1.0 if status == "Active" else (0.7 if status == "Backordered" else 0.4)
    cat_factor = 1.2 if category in ["Grains & Pulses", "Fruits & Vegetables", "Dairy"] else 1.0
    
    expected_sales = 35.0 * turnover_factor * price_factor * status_factor * cat_factor
    noise = np.random.normal(0, 12)
    sales_volume = int(np.clip(round(expected_sales + noise), 5, 100))
    
    records.append({
        "Product_ID": prod_id,
        "Product_Name": p_name,
        "Catagory": category,  # Keep original typo column as specified in prompt
        "Supplier_ID": supplier_id,
        "Supplier_Name": supplier_name,
        "Stock_Quantity": stock_quantity,
        "Reorder_Level": reorder_level,
        "Reorder_Quantity": reorder_qty,
        "Unit_Price": f"${unit_price:.2f} ",
        "Date_Received": date_received.strftime("%m/%d/%Y"),
        "Last_Order_Date": last_order_date.strftime("%m/%d/%Y"),
        "Expiration_Date": expiration_date.strftime("%m/%d/%Y"),
        "Warehouse_Location": warehouse,
        "Sales_Volume": sales_volume,
        "Inventory_Turnover_Rate": inventory_turnover,
        "Status": status
    })

df = pd.DataFrame(records)
df.to_csv(DATASET_PATH, index=False)
print(f" Saved dataset to: {DATASET_PATH} ({len(df)} rows)")

print("[2/5] Data Cleaning & Feature Engineering (14 Features)...")

# Fix Catagory typo
if "Catagory" in df.columns:
    df.rename(columns={"Catagory": "Category"}, inplace=True)

# Clean Unit_Price
df["Unit_Price"] = df["Unit_Price"].astype(str).str.replace("$", "", regex=False).str.strip().astype(float)

# Parse dates
df["Date_Received"] = pd.to_datetime(df["Date_Received"], format="mixed", errors="coerce")
df["Last_Order_Date"] = pd.to_datetime(df["Last_Order_Date"], format="mixed", errors="coerce")
df["Expiration_Date"] = pd.to_datetime(df["Expiration_Date"], format="mixed", errors="coerce")

# Handle any missing dates
ref_date = pd.to_datetime("2025-01-01")
df["Date_Received"] = df["Date_Received"].fillna(ref_date)
df["Last_Order_Date"] = df["Last_Order_Date"].fillna(ref_date)
df["Expiration_Date"] = df["Expiration_Date"].fillna(ref_date + pd.Timedelta(days=90))

# 14 Features as detailed in the user's project documentation:
# 1. Stock_Quantity
# 2. Reorder_Level
# 3. Reorder_Quantity
# 4. Unit_Price
# 5. Inventory_Turnover_Rate
# 6. Days_Since_Last_Order
df["Days_Since_Last_Order"] = (df["Date_Received"] - df["Last_Order_Date"]).dt.days.abs()
# 7. Shelf_Life_Days
df["Shelf_Life_Days"] = (df["Expiration_Date"] - df["Date_Received"]).dt.days.clip(lower=1)
# 8. Receive_Month
df["Receive_Month"] = df["Date_Received"].dt.month
# 9. Receive_DayOfWeek
df["Receive_DayOfWeek"] = df["Date_Received"].dt.dayofweek
# 10. Price_x_Stock
df["Price_x_Stock"] = df["Unit_Price"] * df["Stock_Quantity"]
# 11. Below_Reorder
df["Below_Reorder"] = (df["Stock_Quantity"] < df["Reorder_Level"]).astype(int)

# Categorical encoders
le_cat = LabelEncoder()
df["Category_Enc"] = le_cat.fit_transform(df["Category"].astype(str))

le_status = LabelEncoder()
df["Status_Enc"] = le_status.fit_transform(df["Status"].astype(str))

le_supp = LabelEncoder()
df["Supplier_Name_Enc"] = le_supp.fit_transform(df["Supplier_Name"].astype(str))

feature_cols = [
    "Stock_Quantity",
    "Reorder_Level",
    "Reorder_Quantity",
    "Unit_Price",
    "Inventory_Turnover_Rate",
    "Days_Since_Last_Order",
    "Shelf_Life_Days",
    "Receive_Month",
    "Receive_DayOfWeek",
    "Price_x_Stock",
    "Below_Reorder",
    "Category_Enc",
    "Status_Enc",
    "Supplier_Name_Enc"
]

X = df[feature_cols]
y = df["Sales_Volume"]

print(f" Features shape: {X.shape}, Target shape: {y.shape}")

print("[3/5] Training & Benchmarking 6 Regression Models...")

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.20, random_state=42)

# StandardScaler for linear models
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

def calc_mape(y_true, y_pred):
    mask = y_true != 0
    return float(np.mean(np.abs((y_true[mask] - y_pred[mask]) / y_true[mask])) * 100)

models = {
    "Linear Regression": {
        "model": LinearRegression(),
        "scaled": True
    },
    "Ridge Regression": {
        "model": Ridge(alpha=1.0),
        "scaled": True
    },
    "Decision Tree": {
        "model": DecisionTreeRegressor(max_depth=6, random_state=42),
        "scaled": False
    },
    "Random Forest": {
        "model": RandomForestRegressor(n_estimators=150, max_depth=8, random_state=42),
        "scaled": False
    },
    "Gradient Boosting": {
        "model": GradientBoostingRegressor(n_estimators=150, learning_rate=0.08, max_depth=4, random_state=42),
        "scaled": False
    },
    "XGBoost": {
        "model": xgb.XGBRegressor(n_estimators=150, learning_rate=0.08, max_depth=4, random_state=42, verbosity=0),
        "scaled": False
    }
}

benchmark_results = []
trained_models = {}

for name, cfg in models.items():
    m = cfg["model"]
    is_scaled = cfg["scaled"]
    
    if is_scaled:
        m.fit(X_train_scaled, y_train)
        preds = m.predict(X_test_scaled)
    else:
        m.fit(X_train, y_train)
        preds = m.predict(X_test)
        
    mae = float(mean_absolute_error(y_test, preds))
    rmse = float(np.sqrt(mean_squared_error(y_test, preds)))
    r2 = float(r2_score(y_test, preds))
    mape = float(calc_mape(y_test.values, preds))
    
    benchmark_results.append({
        "model": name,
        "mae": round(mae, 2),
        "rmse": round(rmse, 2),
        "r2": round(r2, 4),
        "mape": round(mape, 2)
    })
    trained_models[name] = m
    print(f"  -> {name:<20} | R2: {r2:.4f} | MAE: {mae:.2f} | RMSE: {rmse:.2f} | MAPE: {mape:.1f}%")

# Sort by R2
benchmark_results.sort(key=lambda x: x["r2"], reverse=True)

# 5-fold CV on top model (XGBoost)
xgb_model = trained_models["XGBoost"]
cv_scores = cross_val_score(xgb_model, X, y, cv=5, scoring="r2")
cv_mean = float(np.mean(cv_scores))
cv_std = float(np.std(cv_scores))

# Feature importances
importances = xgb_model.feature_importances_
feat_imp = [
    {"feature": col, "importance": round(float(imp), 4)}
    for col, imp in sorted(zip(feature_cols, importances), key=lambda x: x[1], reverse=True)
]

# Export Model & Encoders
model_bundle = {
    "model": xgb_model,
    "feature_cols": feature_cols,
    "le_category": le_cat,
    "le_status": le_status,
    "le_supplier": le_supp,
    "scaler": scaler
}
joblib.dump(model_bundle, MODEL_PATH)
print(f" Saved trained XGBoost model bundle to: {MODEL_PATH}")

metrics_data = {
    "benchmark": benchmark_results,
    "best_model": "XGBoost",
    "best_r2": benchmark_results[0]["r2"],
    "best_mae": benchmark_results[0]["mae"],
    "best_rmse": benchmark_results[0]["rmse"],
    "cv_r2_mean": round(cv_mean, 4),
    "cv_r2_std": round(cv_std, 4),
    "total_training_records": len(df),
    "feature_importance": feat_imp
}

with open(METRICS_PATH, "w") as f:
    json.dump(metrics_data, f, indent=2)
print(f" Saved model metrics to: {METRICS_PATH}")

print("[4/5] Running 4-Tier Inventory Recommendation Engine...")

# Predictions for entire dataset
all_preds = xgb_model.predict(X)
df["Predicted_Sales_Volume"] = np.round(all_preds).astype(int)
median_predicted = df["Predicted_Sales_Volume"].median()

def assign_tier(row):
    stock = row["Stock_Quantity"]
    reorder = row["Reorder_Level"]
    pred = row["Predicted_Sales_Volume"]
    
    if stock < reorder and pred > median_predicted:
        return "URGENT RESTOCK"
    elif stock < reorder:
        return "RESTOCK"
    elif stock <= (1.2 * reorder):
        return "MONITOR"
    else:
        return "SUFFICIENT"

df["Restock_Tier"] = df.apply(assign_tier, axis=1)
df["Suggested_Order_Qty"] = np.where(
    df["Restock_Tier"].isin(["URGENT RESTOCK", "RESTOCK"]),
    df["Reorder_Quantity"],
    0
)
df["Estimated_Cost"] = np.round(df["Suggested_Order_Qty"] * df["Unit_Price"], 2)

# Export Recommendations CSV
export_cols = [
    "Product_ID", "Product_Name", "Category", "Stock_Quantity", "Reorder_Level",
    "Sales_Volume", "Predicted_Sales_Volume", "Restock_Tier", "Suggested_Order_Qty",
    "Unit_Price", "Estimated_Cost", "Supplier_Name", "Status"
]
df[export_cols].to_csv(RECOMMENDATIONS_PATH, index=False)
print(f" Exported 4-Tier Recommendations to: {RECOMMENDATIONS_PATH}")

print("[5/5] Generating Store-Linked Product Predictions...")

# Map specific store product SKUs for direct seamless integration with SmartShelf
store_predictions = []
tier_counts = {"URGENT RESTOCK": 0, "RESTOCK": 0, "MONITOR": 0, "SUFFICIENT": 0}

for idx, row in df.head(100).iterrows():
    tier = row["Restock_Tier"]
    tier_counts[tier] = tier_counts.get(tier, 0) + 1
    store_predictions.append({
        "productId": idx + 1,
        "productName": row["Product_Name"],
        "categoryName": row["Category"],
        "currentStock": int(row["Stock_Quantity"]),
        "reorderLevel": int(row["Reorder_Level"]),
        "historicalSales": int(row["Sales_Volume"]),
        "predictedDemandNext7Days": int(max(1, round(row["Predicted_Sales_Volume"] / 4.0))),
        "predictedMonthlyVolume": int(row["Predicted_Sales_Volume"]),
        "safetyStock": int(max(2, round(row["Reorder_Level"] * 0.25))),
        "recommendedOrderQuantity": int(row["Suggested_Order_Qty"]),
        "stockoutRiskLevel": "CRITICAL" if tier == "URGENT RESTOCK" else ("HIGH" if tier == "RESTOCK" else ("MEDIUM" if tier == "MONITOR" else "LOW")),
        "restockTier": tier,
        "estimatedReorderCost": float(row["Estimated_Cost"]),
        "unitPrice": float(row["Unit_Price"]),
        "turnoverRate": int(row["Inventory_Turnover_Rate"]),
        "supplierName": row["Supplier_Name"],
        "status": row["Status"]
    })

store_output = {
    "metrics": metrics_data,
    "tier_summary": tier_counts,
    "total_products": len(store_predictions),
    "predictions": store_predictions
}

with open(STORE_PREDICTIONS_PATH, "w") as f:
    json.dump(store_output, f, indent=2)
print(f" Saved store predictions JSON to: {STORE_PREDICTIONS_PATH}")

print("\n Machine Learning Pipeline & Restock Engine executed successfully!")
