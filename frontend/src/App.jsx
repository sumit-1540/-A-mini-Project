import React from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { Layout } from './components/layout/Layout';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { PosPage } from './pages/PosPage';
import { ProductsPage } from './pages/ProductsPage';
import { InventoryPage } from './pages/InventoryPage';
import { CustomersPage } from './pages/CustomersPage';
import { CreditUdharPage } from './pages/CreditUdharPage';
import { SalesPage } from './pages/SalesPage';
import { SuppliersPage } from './pages/SuppliersPage';
import { PurchasesPage } from './pages/PurchasesPage';
import { NewPurchasePage } from './pages/NewPurchasePage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { AiInsightsPage } from './pages/AiInsightsPage';
import { DemandPredictionPage } from './pages/DemandPredictionPage';
import { HandwrittenListPage } from './pages/HandwrittenListPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { ReportsPage } from './pages/ReportsPage';
import { UsersPage } from './pages/UsersPage';
import { SettingsPage } from './pages/SettingsPage';
import { LoadingSpinner } from './components/common/LoadingSpinner';

const ProtectedRoute = ({ children, requireAdmin = false }) => {
  const { isAuthenticated, isLoading, isAdmin } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <LoadingSpinner message="Checking authentication session..." />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requireAdmin && !isAdmin) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

export const App = () => {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      {/* Authenticated Routes with Sidebar & Navbar Layout */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="pos" element={<PosPage />} />
        <Route path="products" element={<ProductsPage />} />
        <Route path="inventory" element={<InventoryPage />} />
        <Route path="customers" element={<CustomersPage />} />
        <Route path="credit" element={<CreditUdharPage />} />
        <Route path="sales" element={<SalesPage />} />
        <Route path="ai/handwritten-list" element={<HandwrittenListPage />} />
        <Route path="notifications" element={<NotificationsPage />} />

        {/* Admin Backoffice Routes */}
        <Route
          path="suppliers"
          element={
            <ProtectedRoute requireAdmin>
              <SuppliersPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="purchases"
          element={
            <ProtectedRoute requireAdmin>
              <PurchasesPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="purchases/new"
          element={
            <ProtectedRoute requireAdmin>
              <NewPurchasePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="analytics"
          element={
            <ProtectedRoute requireAdmin>
              <AnalyticsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="ai/insights"
          element={
            <ProtectedRoute requireAdmin>
              <AiInsightsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="ai/demand"
          element={
            <ProtectedRoute requireAdmin>
              <DemandPredictionPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="reports"
          element={
            <ProtectedRoute requireAdmin>
              <ReportsPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="users"
          element={
            <ProtectedRoute requireAdmin>
              <UsersPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="settings"
          element={
            <ProtectedRoute requireAdmin>
              <SettingsPage />
            </ProtectedRoute>
          }
        />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
