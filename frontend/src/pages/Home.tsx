import React from 'react'
import { useAuth } from '../auth/AuthContext'

export function Home() {
  const { user } = useAuth()

  return (
    <div className="dashboard-content">
      <h2 className="content-title">Dashboard</h2>
      <div className="content-card">
        <h3>Welcome, {user?.email}!</h3>
        <p>You are successfully logged in.</p>
        <pre>{JSON.stringify(user, null, 2)}</pre>
      </div>
    </div>
  )
}
