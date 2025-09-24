import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { Login } from '../pages/Login'
import { Home } from '../pages/Home'
import { Admin } from '../pages/Admin'
import { Layout } from '../components/Layout'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth()
  return user ? <>{children}</> : <Navigate to="/login" replace />
}

function AdminRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth()
  
  if (!user) {
    return <Navigate to="/login" replace />
  }
  
  const hasAdmin = Array.isArray(user.roles) 
    ? user.roles.includes('ROLE_ADMIN') || user.roles.includes('ADMIN') 
    : String(user.roles || '').includes('ADMIN')
  
  return hasAdmin ? <>{children}</> : <Navigate to="/" replace />
}

export function App() {
  const { user } = useAuth()

  return (
    <Router>
      <Routes>
        <Route path="/login" element={user ? <Navigate to="/" replace /> : <Login />} />
        <Route path="/" element={
          <ProtectedRoute>
            <Layout>
              <Home />
            </Layout>
          </ProtectedRoute>
        } />
        <Route path="/admin" element={
          <AdminRoute>
            <Layout>
              <Admin />
            </Layout>
          </AdminRoute>
        } />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  )
}