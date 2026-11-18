# Workspace API contracts

The running app uses `src/features/*.ts` to describe existing backend operations. `ResourcePage.tsx`
renders forms and results; `src/lib/api.ts` validates fields, encodes identifiers and sends requests.
`src/features/index.ts` registers the eight sections. Routes are registered from these definitions.

Use React 19 and TanStack Query v5: import hooks from `@tanstack/react-query`, use object arguments
such as `useMutation({mutationFn: ...})` and use `isPending`. Main supplies QueryClient and BrowserRouter.
Tests use the existing renderPage helper, Vitest and React Testing Library.

Controllers and DTOs under `services/<service>/src/main/kotlin/com/nexus/<service>/web/` are authoritative.
Do not invent absent endpoints. Categories and products have no list/delete endpoints. Search reads
Elasticsearch, which can be empty while catalog products exist. Binary media storage is not configured.

Same-origin API and health paths are proxied by Vite to ports 8081–8087 during development. Production
hosting needs equivalent routes. No backend credentials belong in frontend source. The local owner
badge is a display label, not authentication.

New tasks should edit registered operation definitions or existing components and add regression tests
for rendered behavior and HTTP payloads. An unregistered new page does not implement a running feature.
Writes need pending/error feedback; deletion needs explicit confirmation. Errors must never claim a save.
