# Category UI contract

Installed: TanStack Query v5. Import useState from 'react', useMutation from '@tanstack/react-query'.
Use useMutation({mutationFn: getCategory, onSuccess: data => {...}}) and mutation.isPending.
Use three mutations (lookup/create/update), not the legacy react-query package or positional hook arguments.

Authoritative backend files:
services/taxonomy/src/main/kotlin/com/nexus/taxonomy/web/controller/CategoryController.kt
services/taxonomy/src/main/kotlin/com/nexus/taxonomy/web/dto/CategoryDtos.kt

Import { getCategory, createCategory, updateCategory, type Category } from './api'.
getCategory(code: string): Promise<Category> -> GET /api/taxonomy/categories/{encodedCode}
createCategory({code,name,parentCode}): Promise<Category> -> POST /api/taxonomy/categories (201).
updateCategory(code,{name,parentCode,updateParent}): Promise<Category> -> PUT /api/taxonomy/categories/{encodedCode}.
Category has code, name, parentCode (nullable), depth (number), path, directAttributeSetCodes and effectiveAttributeSetCodes (string arrays).
There is no list or delete endpoint. Codes are immutable during updates. Empty parent input means null.

Required UI: heading Categories; a lookup form with input label Category code and button Find category.
A separate creation form: labels New category code, New category name, New parent code; button Create category.
After lookup/create, show returned code, name, path and depth. Show an edit form with labels Category name
and Parent code, button Save changes. Updates send updateParent:true with the selected parent (null clears it).
All inputs controlled; blank required code/name prevented before API calls; trim input. Show API error.message
in role=alert, visible pending status, disabled pending action button and role=status success feedback.
Use TanStack Query useMutation for requests. No extra provider is needed; main.tsx supplies QueryClient.
Keep CategoryPage.tsx below 6000 characters. Use className page, eyebrow, card, grid; CSS is provided.

Tests: import renderPage from '../../test/render'; it supplies QueryClient and MemoryRouter.
Use vi.mock('./api',()=>({getCategory:vi.fn(),createCategory:vi.fn(),updateCategory:vi.fn()}));
then vi.mocked(getCategory).mockResolvedValue(category), similarly for writes. beforeEach vi.resetAllMocks().
Cover successful lookup, lookup error, correct create payload, and edit payload from a loaded category.
Use @testing-library/user-event and await user.type/click; await screen.findBy... for mutations.
Use input labels listed above; pick unique sample category names. Do not call nonexistent endpoints.
