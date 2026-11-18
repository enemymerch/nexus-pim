import {beforeEach,describe,expect,it,vi} from 'vitest';
import {screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {ResourcePage} from './ResourcePage';
import {renderPage} from '../test/render';
import categories from '../features/categories';
import media from '../features/media';
beforeEach(()=>vi.unstubAllGlobals());
describe('Live workspace forms',()=>{
 it('creates a category and displays the returned record',async()=>{
  const fetch=vi.fn().mockResolvedValue(new Response(JSON.stringify({code:'shoes',name:'Shoes',parentCode:null,depth:0,path:'/shoes'}),{status:201}));vi.stubGlobal('fetch',fetch);
  renderPage(<ResourcePage resource={categories}/>);const user=userEvent.setup();
  await user.click(screen.getByRole('tab',{name:'Create category'}));
  await user.type(screen.getByLabelText(/Category code/),'shoes');await user.type(screen.getByLabelText(/Category name/),'Shoes');
  await user.click(screen.getByRole('button',{name:/Create category/}));
  expect(await screen.findByText('Shoes',{selector:'span'})).toBeInTheDocument();
  expect(fetch).toHaveBeenCalledWith('/api/taxonomy/categories',expect.objectContaining({method:'POST',body:'{"code":"shoes","name":"Shoes","parentCode":null}'}));
 });
 it('shows backend validation errors without claiming a save',async()=>{
  vi.stubGlobal('fetch',vi.fn().mockResolvedValue(new Response('{"message":"Category not found"}',{status:404})));
  renderPage(<ResourcePage resource={categories}/>);const user=userEvent.setup();
  await user.type(screen.getByLabelText(/Category code/),'missing');await user.click(screen.getByRole('button',{name:/Find category/}));
  expect(await screen.findByRole('alert')).toHaveTextContent('Category not found');
  expect(screen.queryByText('Saved successfully.')).not.toBeInTheDocument();
 });
 it('requires explicit deletion confirmation',async()=>{
  const fetch=vi.fn().mockResolvedValue(new Response(null,{status:204}));vi.stubGlobal('fetch',fetch);
  renderPage(<ResourcePage resource={media}/>);const user=userEvent.setup();
  await user.click(screen.getByRole('tab',{name:'Delete asset'}));await user.type(screen.getByLabelText(/Asset ID/),'test-asset');
  await user.click(screen.getByRole('button',{name:/Delete asset/}));expect(fetch).not.toHaveBeenCalled();
  expect(await screen.findByRole('alert')).toHaveTextContent('Confirm deletion');
  await user.click(screen.getByRole('checkbox'));await user.click(screen.getByRole('button',{name:/Delete asset/}));
  expect(await screen.findByText('Saved successfully.')).toBeInTheDocument();
  expect(fetch).toHaveBeenCalledWith('/api/media/assets/test-asset',expect.objectContaining({method:'DELETE'}));
 });
});
