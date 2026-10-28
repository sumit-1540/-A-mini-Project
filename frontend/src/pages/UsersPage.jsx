import React, { useState, useEffect } from 'react';
import { UserCheck, Plus } from 'lucide-react';
import { userService } from '../services/api';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useToast } from '../context/ToastContext';

export const UsersPage = () => {
  const { success, error } = useToast();
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // Registration Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    fullName: '',
    email: '',
    password: '',
    role: 'ROLE_CASHIER',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setIsLoading(true);
      const data = await userService.getAllUsers();
      setUsers(data || []);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  const handleToggleStatus = async (user) => {
    try {
      if (user.active) {
        await userService.deactivateUser(user.id);
        success(`User ${user.username} deactivated.`);
      } else {
        await userService.activateUser(user.id);
        success(`User ${user.username} activated.`);
      }
      loadUsers();
    } catch {
      error('Failed to update user status');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      await userService.registerUser(formData);
      success(`Staff account '${formData.username}' created!`);
      setIsModalOpen(false);
      setFormData({
        username: '',
        fullName: '',
        email: '',
        password: '',
        role: 'ROLE_CASHIER',
      });
      loadUsers();
    } catch (err) {
      error(err.response?.data?.message || 'Failed to create user account');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white tracking-tight flex items-center gap-2.5">
            <UserCheck className="w-7 h-7 text-emerald-400" />
            Staff Accounts & Roles
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Manage store owners, cashier POS login accounts, and permission boundaries
          </p>
        </div>

        <Button
          variant="primary"
          leftIcon={<Plus className="w-4 h-4" />}
          onClick={() => setIsModalOpen(true)}
        >
          Create Staff Account
        </Button>
      </div>

      {/* Users Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl">
        {isLoading ? (
          <LoadingSpinner message="Loading user accounts..." />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-slate-400 uppercase tracking-wider bg-slate-900/80 border-b border-slate-800">
                <tr>
                  <th className="px-5 py-4 font-semibold">User</th>
                  <th className="px-4 py-4 font-semibold">Username</th>
                  <th className="px-4 py-4 font-semibold">Email</th>
                  <th className="px-4 py-4 font-semibold">Role</th>
                  <th className="px-4 py-4 font-semibold text-center">Status</th>
                  <th className="px-5 py-4 font-semibold text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {users.map((u) => (
                  <tr key={u.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="px-5 py-3.5">
                      <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-xl bg-slate-800 border border-slate-700 flex items-center justify-center font-bold text-white text-xs">
                          {u.fullName?.charAt(0) || 'U'}
                        </div>
                        <div className="font-semibold text-white">{u.fullName}</div>
                      </div>
                    </td>

                    <td className="px-4 py-3.5 font-mono text-slate-300">@{u.username}</td>

                    <td className="px-4 py-3.5 text-slate-400">{u.email}</td>

                    <td className="px-4 py-3.5">
                      <Badge
                        variant={u.role === 'ROLE_ADMIN' ? 'purple' : 'info'}
                        size="sm"
                      >
                        {u.role === 'ROLE_ADMIN' ? 'Admin / Owner' : 'Cashier'}
                      </Badge>
                    </td>

                    <td className="px-4 py-3.5 text-center">
                      <Badge variant={u.active ? 'success' : 'danger'} size="sm">
                        {u.active ? 'Active' : 'Disabled'}
                      </Badge>
                    </td>

                    <td className="px-5 py-3.5 text-right">
                      <Button
                        size="sm"
                        variant={u.active ? 'ghost' : 'outline'}
                        onClick={() => handleToggleStatus(u)}
                      >
                        {u.active ? 'Deactivate' : 'Enable'}
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Register Staff Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Create New Staff Account"
        subtitle="Grant POS Cashier access or Full Admin Store Privileges"
        maxWidth="md"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Full Name *</label>
            <input
              type="text"
              required
              placeholder="e.g. Anand Sharma"
              value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Username (Login ID) *</label>
            <input
              type="text"
              required
              placeholder="e.g. anand_pos"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value.toLowerCase() })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white font-mono focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Email Address *</label>
            <input
              type="email"
              required
              placeholder="anand@smartshelf.com"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Password *</label>
            <input
              type="password"
              required
              placeholder="••••••••"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Role / Permission Level *</label>
            <select
              value={formData.role}
              onChange={(e) => setFormData({ ...formData, role: e.target.value })}
              className="w-full px-3.5 py-2 bg-slate-800 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-emerald-500"
            >
              <option value="ROLE_CASHIER">Cashier (POS Billing, Receipt Printing, Customer Accounts)</option>
              <option value="ROLE_ADMIN">Admin / Owner (Full Access, Stock Restock, Financials, Reports)</option>
            </select>
          </div>

          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-800">
            <Button variant="secondary" type="button" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" isLoading={isSubmitting}>
              Create Account
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
