import { afterEach, describe, expect, it, vi } from 'vitest';
import { createCategory, getCategory, updateCategory } from './api';
afterEach(() => vi.unstubAllGlobals());
const value = {code:'shoes',name:'Shoes',parentCode:null,depth:0,path:'/shoes',directAttributeSetCodes:[],effectiveAttributeSetCodes:[]};
describe('Taxonomy HTTP contract',()=>{
  it('encodes the lookup path', async()=>{
    const fetch = vi.fn().mockResolvedValue(new Response(JSON.stringify(value),{status:200})); vi.stubGlobal('fetch',fetch);
    await expect(getCategory('shoes/summer')).resolves.toEqual(value);
    expect(fetch).toHaveBeenCalledWith('/api/taxonomy/categories/shoes%2Fsummer',expect.objectContaining({method:'GET'}));
  });
  it('sends JSON on creation',async()=>{
    const fetch=vi.fn().mockResolvedValue(new Response(JSON.stringify(value),{status:201}));vi.stubGlobal('fetch',fetch);
    const body={code:'shoes',name:'Shoes',parentCode:null}; await createCategory(body);
    expect(fetch).toHaveBeenCalledWith('/api/taxonomy/categories',expect.objectContaining({method:'POST',body:JSON.stringify(body),headers:{'Content-Type':'application/json'}}));
  });
  it('sends the parent-update flag',async()=>{
    const fetch=vi.fn().mockResolvedValue(new Response(JSON.stringify(value),{status:200}));vi.stubGlobal('fetch',fetch);
    const body={name:'Footwear',parentCode:null,updateParent:true}; await updateCategory('shoes',body);
    expect(fetch).toHaveBeenCalledWith('/api/taxonomy/categories/shoes',expect.objectContaining({method:'PUT',body:JSON.stringify(body)}));
  });
  it('reports missing categories and network errors',async()=>{
    const fetch=vi.fn().mockResolvedValueOnce(new Response('',{status:404})).mockRejectedValueOnce(new TypeError('offline'));vi.stubGlobal('fetch',fetch);
    await expect(getCategory('missing')).rejects.toThrow('Category not found.');
    await expect(getCategory('missing')).rejects.toThrow('Cannot reach Taxonomy');
  });
});
