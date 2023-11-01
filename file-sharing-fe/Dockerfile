FROM node:16 AS build

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build --prod

FROM nginx:alpine
COPY --from=build /app/dist/file-sharing-fe /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
