import React from 'react'
import { createRoot } from 'react-dom/client'
import { App } from './ui/App'
import { AuthProvider } from './auth/AuthContext'
import './styles/App.scss'

const root = createRoot(document.getElementById('root')!)
root.render(
	<React.StrictMode>
		<AuthProvider>
			<App />
		</AuthProvider>
	</React.StrictMode>
)

