/** Contract: CategoryController.kt + CategoryDtos.kt. No list/delete endpoints exist in that controller. */
export interface Category {
  code: string; name: string; parentCode: string | null; depth: number; path: string;
  directAttributeSetCodes: string[]; effectiveAttributeSetCodes: string[];
}
export interface CreateCategory { code: string; name: string; parentCode: string | null }
export interface UpdateCategory { name: string; parentCode: string | null; updateParent: boolean }
export class ApiError extends Error {
  constructor(public status: number, message: string) { super(message); }
}
async function request(path: string, method = 'GET', body?: unknown): Promise<Category> {
  let response: Response;
  try { response = await fetch('/api/taxonomy/categories' + path, {
    method, headers: body === undefined ? undefined : {'Content-Type': 'application/json'},
    body: body === undefined ? undefined : JSON.stringify(body), signal: AbortSignal.timeout(10000)
  }); } catch { throw new ApiError(0, 'Cannot reach Taxonomy. Check the backend connection.'); }
  if (!response.ok) {
    const fallback = response.status === 404 ? 'Category not found.' : `Request failed (${response.status}).`;
    throw new ApiError(response.status, fallback);
  }
  return response.json();
}
export const getCategory = (code: string) => request('/' + encodeURIComponent(code));
export const createCategory = (value: CreateCategory) => request('', 'POST', value);
export const updateCategory = (code: string, value: UpdateCategory) => request('/' + encodeURIComponent(code), 'PUT', value);
