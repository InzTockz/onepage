# ============================================
# ETAPA 1: Build - Compila Angular
# ============================================
FROM node:22-alpine AS build

WORKDIR /app

# Copiar package.json y lock primero (aprovecha cache)
COPY package.json package-lock.json* ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# Copiar el resto del codigo
COPY . .

# Compilar para produccion
RUN npm run build

# ============================================
# ETAPA 2: Servir con Nginx
# ============================================
FROM nginx:alpine

# Copiar los archivos compilados al directorio de Nginx
COPY --from=build /app/dist/frontend-onepage/browser /usr/share/nginx/html

# Configuracion personalizada de Nginx (SPA routing + proxy al backend)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
