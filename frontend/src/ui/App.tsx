import React, { useEffect, useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import { api } from '../utils/api'
import { Admin } from './Admin'

function Register({ onDone }: { onDone: () => void }) {
	const [state, setState] = (function useLocal() {
		const [v, setV] = useState<any>({})
		return [v, setV]
	})()

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
		<div style={{ maxWidth: 360, margin: '10vh auto', fontFamily: 'system-ui' }}>
			<h2>Register</h2>
			<form onSubmit={onSubmit}>
				<input name="email" type="email" placeholder="email" required style={{ width: '100%', padding: 8, marginBottom: 8 }} />
				<input name="password" type="password" placeholder="password" required style={{ width: '100%', padding: 8, marginBottom: 8 }} />
				<button type="submit" style={{ width: '100%', padding: 10 }}>Create account</button>
			</form>
			{state.error && <div style={{ color: 'crimson', marginTop: 8 }}>{String(state.error)}</div>}
		</div>
	)
}

function Login() {
	const { refreshMe } = useAuth()
	const [state, setState] = (function useLocal() {
		const [v, setV] = useState<any>({ mode: 'login' })
		return [v, setV]
	})()

	async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
		e.preventDefault()
		setState((s: any) => ({ ...s, error: undefined }))
		const form = new FormData(e.currentTarget)
		const email = String(form.get('email'))
		const password = String(form.get('password'))
		try {
			await api.post('/api/auth/login', { email, password })
			await refreshMe()
		} catch (err: any) {
			const status = err?.response?.status
			const msg = err?.response?.data?.error || (status === 403 ? 'Forbidden' : status === 401 ? 'Unauthorized' : 'Login failed')
			setState((s: any) => ({ ...s, error: msg }))
		}
	}

	if (state.mode === 'register') return <Register onDone={() => setState({ mode: 'login' })} />

	return (
		<div style={{ maxWidth: 360, margin: '10vh auto', fontFamily: 'system-ui' }}>
			<h2>Login</h2>
			<form onSubmit={onSubmit}>
				<input name="email" type="email" placeholder="email" required style={{ width: '100%', padding: 8, marginBottom: 8 }} />
				<input name="password" type="password" placeholder="password" required style={{ width: '100%', padding: 8, marginBottom: 8 }} />
				<button type="submit" style={{ width: '100%', padding: 10 }}>Sign in</button>
			</form>
			<div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8 }}>
				<button type="button" onClick={() => setState({ mode: 'register' })} style={{ background: 'transparent', border: 'none', color: '#0366d6', cursor: 'pointer' }}>Create account</button>
			</div>
			{state.error && <div style={{ color: 'crimson', marginTop: 8 }}>{String(state.error)}</div>}
		</div>
	)
}

function Dashboard() {
	const { user, refreshMe, logout } = useAuth()
	const [state, setState] = (function useLocal() {
		const [v, setV] = useState<any>({ loading: true, page: 'home' })
		return [v, setV]
	})()

	useEffect(() => {
		let cancelled = false
		async function load() {
			try {
				await refreshMe()
				if (!cancelled) setState((s: any) => ({ ...s, loading: false }))
			} catch (e: any) {
				const status = e?.response?.status
				if (!cancelled) setState({ loading: false, error: status === 403 ? 'Forbidden' : status === 401 ? 'Unauthorized' : 'Error', page: 'home' })
			}
		}
		load()
		return () => { cancelled = true }
	}, [])

	if (!user) return <Login />
	if (state.loading) return <div style={{ padding: 24 }}>Loading...</div>
	if (state.error) return <div style={{ padding: 24 }}>Error: {state.error}</div>

	const hasAdmin = Array.isArray((user as any).roles) ? (user as any).roles.includes('ROLE_ADMIN') || (user as any).roles.includes('ADMIN') : String((user as any).roles || '').includes('ADMIN')

	return (
		<div style={{ padding: 24, fontFamily: 'system-ui' }}>
			<div style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
				<button onClick={() => setState((s: any) => ({ ...s, page: 'home' }))}>Home</button>
				{hasAdmin && <button onClick={() => setState((s: any) => ({ ...s, page: 'admin' }))}>Admin</button>}
				<div style={{ marginLeft: 'auto' }}>
					<button onClick={async () => { await logout() }}>Logout</button>
				</div>
			</div>
			{state.page === 'home' && (
				<div>
					<h2>Dashboard</h2>
					<pre>{JSON.stringify(user, null, 2)}</pre>
				</div>
			)}
			{state.page === 'admin' && <Admin />}
		</div>
	)
}

export function App() {
	const { user } = useAuth()
	return user ? <Dashboard /> : <Login />
}

