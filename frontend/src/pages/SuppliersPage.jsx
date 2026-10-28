import React, { useState, useEffect } from 'react';
import { Building2, Plus, Phone, Mail, MapPin, Edit2, Trash2 } from 'lucide-react';
import { supplierService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { SupplierFormModal } from '../components/suppliers/SupplierFormModal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useToast } from '../context/ToastContext';

export const SuppliersPage = () => {
  const { success, error } = useToast();
  const [suppliers, setSuppliers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Modal
  const [selectedSupplier, setSelectedSupplier] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    loadSuppliers();
  }, []);

  const loadSuppliers = async () => {
    try {
      setIsLoading(true);
      const data = await supplierService.getAllSuppliers();
      setSuppliers(data || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Deactivate supplier ${name}?`)) return;
    try {
      await supplierService.deleteSupplier(id);
      success(`Supplier '${name}' deactivated.`);
      loadSuppliers();
    } catch {
      error('Failed to deactivate supplier');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <Building2 className="w-7 h-7 text-emerald-400" />
            Suppliers & Wholesale Dealers
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Manage vendor directories, GSTIN registrations, and stock supply contacts
          </p>
        </div>

        <Button
          variant="primary"
          leftIcon={<Plus className="w-4 h-4" />}
          onClick={() => {
            setSelectedSupplier(null);
            setIsModalOpen(true);
          }}
        >
          Register Supplier
        </Button>
      </div>

      {isLoading ? (
        <LoadingSpinner message="Loading suppliers directory..." />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {suppliers.map((supplier) => (
            <div
              key={supplier.id}
              className="p-5 rounded-3xl bg-slate-900 border border-slate-800 hover:border-slate-700 transition-all flex flex-col justify-between shadow-xl space-y-4"
            >
              <div>
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center gap-3">
                    <div className="w-11 h-11 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 font-extrabold text-base flex items-center justify-center">
                      {supplier.name?.charAt(0) || 'S'}
                    </div>
                    <div>
                      <h3 className="text-sm font-bold text-white leading-tight">{supplier.name}</h3>
                      {supplier.company && (
                        <div className="text-xs text-slate-400 mt-0.5">{supplier.company}</div>
                      )}
                    </div>
                  </div>

                  <Badge variant={supplier.active ? 'success' : 'default'} size="sm">
                    {supplier.active ? 'Active' : 'Inactive'}
                  </Badge>
                </div>

                {/* Details */}
                <div className="mt-4 pt-3 border-t border-slate-800/80 space-y-2 text-xs text-slate-400">
                  {supplier.phone && (
                    <div className="flex items-center gap-2">
                      <Phone className="w-3.5 h-3.5 text-slate-500" />
                      <span className="font-mono text-slate-300">{supplier.phone}</span>
                    </div>
                  )}

                  {supplier.email && (
                    <div className="flex items-center gap-2">
                      <Mail className="w-3.5 h-3.5 text-slate-500" />
                      <span className="text-slate-300">{supplier.email}</span>
                    </div>
                  )}

                  {supplier.gstNumber && (
                    <div className="flex items-center gap-2 font-mono text-[11px]">
                      <span className="text-slate-500 font-sans">GSTIN:</span>
                      <span className="text-emerald-400 font-semibold">{supplier.gstNumber}</span>
                    </div>
                  )}

                  {supplier.address && (
                    <div className="flex items-start gap-2 text-[11px] pt-1">
                      <MapPin className="w-3.5 h-3.5 text-slate-500 shrink-0 mt-0.5" />
                      <span className="line-clamp-2">{supplier.address}</span>
                    </div>
                  )}
                </div>
              </div>

              {/* Actions */}
              <div className="flex items-center justify-between pt-3 border-t border-slate-800/80">
                <span className="text-xs text-slate-500">
                  Supplying {supplier.suppliedProductCount || 0} products
                </span>
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => {
                      setSelectedSupplier(supplier);
                      setIsModalOpen(true);
                    }}
                    className="p-1.5 text-slate-400 hover:text-emerald-400 hover:bg-slate-800 rounded-lg transition-colors"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => handleDelete(supplier.id, supplier.name)}
                    className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800 rounded-lg transition-colors"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          ))}

          {suppliers.length === 0 && (
            <div className="col-span-full py-16 text-center text-slate-400 text-xs">
              No suppliers registered yet. Click "Register Supplier" to get started.
            </div>
          )}
        </div>
      )}

      {/* Supplier Modal */}
      <SupplierFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        supplier={selectedSupplier}
        onSaved={loadSuppliers}
      />
    </div>
  );
};
