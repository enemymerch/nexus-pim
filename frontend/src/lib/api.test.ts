import {afterEach,describe,expect,it,vi} from 'vitest';
import {compileRequest,execute,request} from './api';
import categories from '../features/categories';
import search from '../features/search';
afterEach(()=>vi.unstubAllGlobals());
describe('Workspace request contracts',()=>{
 it('encodes identifiers and keeps nullable parent updates',()=>{
  const op=categories.operations.find(item=>item.id==='update')!;
  expect(compileRequest(op,{categoryCode:'shoes/summer',name:' Summer shoes ',parentCode:'',updateParent:'true'})).toEqual({url:'/api/taxonomy/categories/shoes%2Fsummer',body:{name:'Summer shoes',parentCode:null,updateParent:true}});
 });
 it('uses query parameters and rejects invalid numeric input',()=>{
  const op=search.operations[0];
  expect(compileRequest(op,{q:'red & blue',limit:'10'}).url).toBe('/api/search/products?q=red+%26+blue&limit=10');
  expect(()=>compileRequest(op,{limit:'no'})).toThrow('whole number');
 });
 it('can clear assignments with an empty array',()=>{
  const op=categories.operations.find(item=>item.id==='sets')!;
  expect(compileRequest(op,{categoryCode:'shoes',attributeSetCodes:''}).body).toEqual({attributeSetCodes:[]});
 });
 it('prevents required-field requests and preserves backend errors',async()=>{
  const fetch=vi.fn().mockResolvedValue(new Response('{"message":"Category code already exists"}',{status:409}));vi.stubGlobal('fetch',fetch);
  await expect(execute(categories.operations[1],{})).rejects.toThrow('required');expect(fetch).not.toHaveBeenCalled();
  await expect(request('/api/taxonomy/categories','POST',{})).rejects.toThrow('Category code already exists');
 });
 it('handles no-content deletion and network failure',async()=>{
  vi.stubGlobal('fetch',vi.fn().mockResolvedValueOnce(new Response(null,{status:204})).mockRejectedValueOnce(new Error('offline')));
  await expect(request('/api/media/assets/example','DELETE')).resolves.toBeNull();
  await expect(request('/api/search/products')).rejects.toThrow('could not be reached');
 });
});
