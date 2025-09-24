import axios from 'axios'

const baseURL = (import.meta as any).env?.VITE_API_BASE_URL || '/'

export const api = axios.create({
	baseURL,
	withCredentials: true,
})

// No Authorization header needed; backend reads JWT from HttpOnly cookie

api.interceptors.response.use(
	(res) => res,
	(err) => {
		if (err?.response?.status === 401) {
			// user not authenticated
		}
		return Promise.reject(err)
	}
)

