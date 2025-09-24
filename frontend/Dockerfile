# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Optional API base URL for Vite at build time (defaults to '/')
ARG VITE_API_BASE_URL=/
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

COPY package.json package-lock.json* pnpm-lock.yaml* yarn.lock* ./
RUN if [ -f package-lock.json ]; then npm ci --no-fund --no-audit; elif [ -f pnpm-lock.yaml ]; then npm i -g pnpm && pnpm i; elif [ -f yarn.lock ]; then yarn install --frozen-lockfile; else npm install --no-fund --no-audit; fi
COPY . .
RUN npm run build

# Runtime stage
FROM nginx:1.27-alpine
WORKDIR /usr/share/nginx/html
COPY --from=build /app/dist .
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
