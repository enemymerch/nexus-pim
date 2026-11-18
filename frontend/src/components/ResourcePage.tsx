import {useState} from 'react';
import {useMutation,useQuery,useQueryClient} from '@tanstack/react-query';
import {execute} from '../lib/api';
import type {Resource,Operation} from '../lib/contracts';
import {ResultView} from './ResultView';
export function ResourcePage({resource}:{resource:Resource}){
 const [selected,setSelected]=useState(resource.operations[0].id);
 const [values,setValues]=useState<Record<string,string>>({});
 const [confirm,setConfirm]=useState(false);
 const [localError,setLocalError]=useState('');
 const operation=resource.operations.find(item=>item.id===selected)!;
 const client=useQueryClient();
 const initial=resource.operations.find(item=>item.id===resource.auto);
 const listing=useQuery({queryKey:['resource',resource.id,'initial'],queryFn:()=>execute(initial!,{}),enabled:!!initial,retry:false});
 const mutation=useMutation({mutationFn:({op,values}:{op:Operation;values:Record<string,string>})=>execute(op,values),onSuccess:()=>{if(operation.method!=='GET')void client.invalidateQueries({queryKey:['resource']});setConfirm(false);}});
 function select(id:string){setSelected(id);setValues({});setConfirm(false);setLocalError('');mutation.reset();}
 const currentError=localError||(mutation.error instanceof Error?mutation.error.message:'');
 const showResult=mutation.isSuccess||(!mutation.isPending&&!!initial&&listing.isSuccess);
 const result=mutation.isSuccess?mutation.data:listing.data;
 return <section className="page"><div className="page-heading"><div><p className="eyebrow">PRODUCT WORKSPACE / {resource.service.toUpperCase()}</p><h1>{resource.title}</h1><p className="subtitle">{resource.subtitle}</p></div><span className="service-label"><i/>{resource.service}</span></div>
 <p className="description">{resource.description}</p>
 <div className="operation-tabs" role="tablist" aria-label={`${resource.title} actions`}>{resource.operations.map(item=><button key={item.id} role="tab" aria-selected={selected===item.id} className={selected===item.id?'selected':''} disabled={mutation.isPending} onClick={()=>select(item.id)}>{item.label}</button>)}</div>
 <div className="workspace-grid"><form className="panel action-panel" onSubmit={event=>{event.preventDefault();setLocalError('');if(operation.destructive&&!confirm){setLocalError('Confirm deletion before continuing.');return;}mutation.mutate({op:operation,values:{...values}});}}>
 <div className="panel-heading"><div><h2>{operation.label}</h2><p>Work with your live PIM data.</p></div><span className={`method ${operation.method==='GET'?'read':''}`}>{operation.method==='GET'?'READ':'WRITE'}</span></div>
 <div className="form-fields">{operation.fields.map(field=><label key={field.key} className={field.kind==='json'?'wide':''}><span>{field.label}{field.required&&<b className="required"> *</b>}</span>
 {field.choices||field.kind==='boolean'?<select value={values[field.key]??field.initial??''} required={field.required} onChange={event=>setValues({...values,[field.key]:event.target.value})}>
 {(field.choices||(field.kind==='boolean'?['false','true']:[])).map(value=><option key={value} value={value}>{value===''?'All / none':field.kind==='boolean'?(value==='true'?'Yes':'No'):value}</option>)}
 </select>:field.kind==='json'?<textarea spellCheck={false} rows={4} required={field.required} value={values[field.key]??field.initial??''} onChange={event=>setValues({...values,[field.key]:event.target.value})}/>:<input required={field.required} type={field.kind==='number'?'number':'text'} min={field.kind==='number'?0:undefined} value={values[field.key]??field.initial??''} placeholder={field.label} onChange={event=>setValues({...values,[field.key]:event.target.value})}/>}
 {field.help&&<small>{field.help}</small>}</label>)}</div>
 {operation.destructive&&<label className="delete-confirm"><input type="checkbox" checked={confirm} onChange={event=>setConfirm(event.target.checked)}/> I understand this deletes the asset record.</label>}
 {currentError&&<div role="alert" className="error-message">{currentError}</div>}
 <div className="form-footer"><span>{operation.destructive?'This action cannot be undone.':'Changes are saved to the connected service.'}</span><button className={`primary-button ${operation.destructive?'danger':''}`} disabled={mutation.isPending}>{mutation.isPending?'Working…':operation.label}<span aria-hidden="true">↗</span></button></div>
 </form><aside className="context-card"><span className="context-icon">✳</span><h3>Connected to your workspace</h3><p>Results come from the running {resource.service} service.</p><div className="context-divider"/><p>Use exact codes to look up records. Related categories and attribute definitions must exist before you create a product.</p><span className="local-label">LOCAL DEVELOPMENT</span></aside></div>
 {(mutation.isPending||listing.isLoading)&&<p role="status" className="loading">Loading workspace data…</p>}
 {!mutation.isPending&&listing.isError&&!mutation.isSuccess&&<div role="alert" className="error-message">{listing.error.message} <button type="button" className="text-button" onClick={()=>void listing.refetch()}>Retry</button></div>}
 {showResult&&<section className="panel result-panel"><div className="panel-heading"><div><h2>{mutation.isSuccess?operation.label+' · Result':'Workspace records'}</h2><p role="status">{operation.method==='GET'||!mutation.isSuccess?'Live data from your connected service.':'Saved successfully.'}</p></div><span className="result-count">{Array.isArray(result)?`${result.length} records`:'Record details'}</span></div><ResultView key={`${selected}-${mutation.submittedAt}`} data={result}/></section>}
 </section>;
}
