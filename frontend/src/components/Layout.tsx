import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

interface LayoutProps {
  children: React.ReactNode
}

export function Layout({ children }: LayoutProps) {
  const { user, logout } = useAuth()
  const location = useLocation()

  if (!user) {
    return null // This should not happen as Layout is only used for authenticated users
  }

  const hasAdmin = Array.isArray(user.roles) 
    ? user.roles.includes('ROLE_ADMIN') || user.roles.includes('ADMIN') 
    : String(user.roles || '').includes('ADMIN')

  return (
    <div className="dashboard-page">
      <div className="dashboard-card">
        <div className="dashboard-nav">
          <Link
            to="/"
            className={`nav-button ${location.pathname === '/' ? 'active' : ''}`}
          >
            Home
          </Link>
          {hasAdmin && (
            <Link
              to="/admin"
              className={`nav-button ${location.pathname === '/admin' ? 'active' : ''}`}
            >
              Admin
            </Link>
          )}
          <button
            onClick={logout}
            className="logout-button"
          >
            Logout
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
