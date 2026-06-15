# Autolog Frontend

Simple Vite frontend for the Spring Boot Autolog API.

## Run

Start the backend on port `8080`, then run:

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

The Vite dev server proxies `/api` requests to `http://localhost:8080`, so no backend CORS changes are required during local development.
