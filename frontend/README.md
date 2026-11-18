# Nexus PIM workspace

React + TypeScript + Vite admin UI for the seven PIM backend services. Requires Node >=22.12.

```powershell
npm ci
npm run dev
```

Open http://127.0.0.1:5173. All backend services must run on localhost ports 8081–8087.
Vite proxies API and health requests during development. Production hosting must provide equivalent
reverse-proxy routes; no backend password, token or connection string belongs in frontend configuration.

Pages expose the operations actually declared in the Kotlin controllers. Unsupported list/update/delete
operations are not invented. Product search reads the Elasticsearch projection, not all catalog rows.
Media upload URLs are development contracts; this UI does not configure binary storage.
The local workspace owner display is not authentication. Authentication and production deployment are not implemented.

`npm run verify` runs TypeScript checks, a production build and Vitest tests. No mock dataset is used in the running UI.
