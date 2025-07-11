# Stage 1: Build the React application
FROM node:18-alpine AS build
WORKDIR /app
COPY package.json ./
COPY package-lock.json ./
RUN npm install
COPY . ./
RUN npm run build

# Stage 2: Serve the application using Nginx
FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
# Copiar uma configuração personalizada do Nginx (opcional, mas bom para React Router)
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
