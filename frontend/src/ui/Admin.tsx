import React, { useEffect, useState, useCallback } from 'react'
import { api } from '../utils/api'
import { useAuth } from '../auth/AuthContext'

export function Admin() {
	const { user } = useAuth()
	const [state, setState] = useState<any>({ loading: true, items: [] })

	const load = useCallback(async () => {
		setState((s: any) => ({ ...s, loading: true, error: undefined }))
		try {
			const res = await api.get('/api/admin/users')
			setState({ loading: false, items: res.data })
		} catch (e: any) {
			setState({ loading: false, items: [], error: e?.response?.data?.error || 'Error' })
		}
	}, [])

	useEffect(() => {
		let cancel = false
		;(async () => {
			await load()
		})()
		return () => { cancel = true }
	}, [load])

	if (!user) return null
	const isAdmin = Array.isArray((user as any).roles) ? (user as any).roles.includes('ROLE_ADMIN') || (user as any).roles.includes('ADMIN') : String((user as any).roles || '').includes('ADMIN')
	if (!isAdmin) return <div style={{ padding: 24 }}>Forbidden</div>

	if (state.loading) return <div style={{ padding: 24 }}>Loading users...</div>
	if (state.error) return <div style={{ padding: 24, color: 'crimson' }}>{String(state.error)}</div>

	return (
		<div style={{ padding: 24, fontFamily: 'system-ui' }}>
			<h2>Admin - Users</h2>
			<div style={{ marginBottom: 12 }}>
				<button onClick={load}>Reload</button>
			</div>
			<table style={{ width: '100%', borderCollapse: 'collapse' }}>
				<thead>
					<tr>
						<th align="left">ID</th>
						<th align="left">Email</th>
						<th align="left">Roles</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					{state.items.map((u: any) => (
						<tr key={u.id}>
							<td>{u.id}</td>
							<td>{u.email}</td>
							<td>{u.roles}</td>
							<td>
								<button onClick={async () => {
									await api.delete(`/api/admin/users/${u.id}`)
									await load()
								}}>Delete</button>
							</td>
						</tr>
					))}
				</tbody>
			</table>
		</div>
	)
}
