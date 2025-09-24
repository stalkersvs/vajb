import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { api } from '../utils/api'

export type AuthUser = {
	email: string
	roles?: any
}

type AuthContextValue = {
	user: AuthUser | null
	refreshMe: () => Promise<void>
	logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
	const [user, setUser] = useState<AuthUser | null>(null)

	const refreshMe = useCallback(async () => {
		try {
			const res = await api.get('/api/auth/me')
			setUser(res.data)
		} catch {
			setUser(null)
		}
	}, [])

	const logout = useCallback(async () => {
		try { await api.post('/api/auth/logout') } catch {}
		setUser(null)
	}, [])

	useEffect(() => {
		refreshMe()
	}, [refreshMe])

	const value = useMemo<AuthContextValue>(() => ({ user, refreshMe, logout }), [user, refreshMe, logout])
	return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
	const ctx = useContext(AuthContext)
	if (!ctx) throw new Error('useAuth must be used within AuthProvider')
	return ctx
}
