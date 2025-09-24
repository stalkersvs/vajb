import React, { useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import { api } from '../utils/api'

function Register({ onDone }: { onDone: () => void }) {
  const [state, setState] = useState<{ error?: string }>({})

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setState({})
    const form = new FormData(e.currentTarget)
    const email = String(form.get('email'))
    const password = String(form.get('password'))
    try {
      await api.post('/api/auth/register', { email, password })
      onDone()
    } catch (err: any) {
      const msg = err?.response?.data?.error ?? 'Registration failed'
      setState({ error: msg })
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2 className="auth-title">Register</h2>
        <form className="auth-form" onSubmit={onSubmit}>
          <input
            name="email"
            type="email"
            placeholder="Email"
            required
            className="form-input"
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            required
            className="form-input"
          />
          <button
            type="submit"
            className="form-button"
          >
            Create Account
          </button>
        </form>
        {state.error && (
          <div className="auth-error">
            {String(state.error)}
          </div>
        )}
      </div>
    </div>
  )
}

export function Login() {
  const { refreshMe } = useAuth()
  const [state, setState] = useState<{ mode: 'login' | 'register'; error?: string }>({ mode: 'login' })

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setState(s => ({ ...s, error: undefined }))
    const form = new FormData(e.currentTarget)
    const email = String(form.get('email'))
    const password = String(form.get('password'))
    try {
      await api.post('/api/auth/login', { email, password })
      await refreshMe()
    } catch (err: any) {
      const status = err?.response?.status
      const msg = err?.response?.data?.error || (status === 403 ? 'Forbidden' : status === 401 ? 'Unauthorized' : 'Login failed')
      setState(s => ({ ...s, error: msg }))
    }
  }

  if (state.mode === 'register') return <Register onDone={() => setState({ mode: 'login' })} />

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2 className="auth-title">Login</h2>
        <form className="auth-form" onSubmit={onSubmit}>
          <input
            name="email"
            type="email"
            placeholder="Email"
            required
            className="form-input"
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            required
            className="form-input"
          />
          <button
            type="submit"
            className="form-button"
          >
            Sign In
          </button>
        </form>
        <div style={{ textAlign: 'center' }}>
          <button
            type="button"
            onClick={() => setState({ mode: 'register' })}
            className="auth-link"
          >
            Create account
          </button>
        </div>
        {state.error && (
          <div className="auth-error">
            {String(state.error)}
          </div>
        )}
      </div>
    </div>
  )
}
