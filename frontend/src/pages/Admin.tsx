import React, { useEffect, useState, useCallback } from 'react'
import { api } from '../utils/api'
import { ConfirmationModal } from '../components/ConfirmationModal'

interface User {
  id: number
  email: string
  roles: string
}

export function Admin() {
  const [users, setUsers] = useState<User[]>([])
  const [loadingUsers, setLoadingUsers] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [deleteModal, setDeleteModal] = useState<{
    isOpen: boolean
    user: User | null
  }>({ isOpen: false, user: null })

  const fetchUsers = useCallback(async () => {
    setLoadingUsers(true)
    setError(null)
    try {
      const response = await api.get('/api/admin/users')
      setUsers(response.data)
    } catch (err: any) {
      const msg = err?.response?.data?.error ?? 'Failed to fetch users'
      setError(msg)
    } finally {
      setLoadingUsers(false)
    }
  }, [])

  useEffect(() => {
    fetchUsers()
  }, [fetchUsers])

  const handleDeleteClick = (user: User) => {
    setDeleteModal({ isOpen: true, user })
  }

  const handleDeleteConfirm = async () => {
    if (!deleteModal.user) return
    
    try {
      await api.delete(`/api/admin/users/${deleteModal.user.id}`)
      fetchUsers() // Refresh the list after deletion
      setDeleteModal({ isOpen: false, user: null })
    } catch (err: any) {
      const msg = err?.response?.data?.error ?? 'Failed to delete user'
      setError(msg)
    }
  }

  const handleDeleteCancel = () => {
    setDeleteModal({ isOpen: false, user: null })
  }

  return (
    <div className="dashboard-content">
      <h2 className="content-title">Admin Dashboard</h2>
      <div className="admin-reload">
        <button
          onClick={fetchUsers}
          className="reload-button"
        >
          Reload
        </button>
      </div>

      {loadingUsers && <p>Loading users...</p>}
      {error && <p style={{ color: '#ff6b6b' }}>Error: {error}</p>}

      {!loadingUsers && !error && users.length === 0 && <p>No users registered.</p>}

      {!loadingUsers && !error && users.length > 0 && (
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Email</th>
              <th>Roles</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.email}</td>
                <td>{user.roles}</td>
                <td>
                  <button
                    onClick={() => handleDeleteClick(user)}
                    className="admin-button"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <ConfirmationModal
        isOpen={deleteModal.isOpen}
        onClose={handleDeleteCancel}
        onConfirm={handleDeleteConfirm}
        title="Delete User"
        message={`Are you sure you want to delete user "${deleteModal.user?.email}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        isDestructive={true}
      />
    </div>
  )
}
