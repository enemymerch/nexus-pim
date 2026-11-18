import {useQueries,useQuery} from '@tanstack/react-query';
import {Link} from 'react-router-dom';
import {request} from '../lib/api';
import {resources} from '../features';
export const services=['catalog','attribute','taxonomy','content','media','workflow','search'];
export function Overview(){
 const health=useQueries({queries:services.map(name=>({queryKey:['health',name],queryFn:()=>request(`/health/${name}`) as Promise<{status:string}>,retry:false,refetchInterval:30000}))});
 const attributes=useQuery({queryKey:['resource','attributes','initial'],queryFn:()=>request('/api/attribute/attributes') as Promise<unknown[]>,retry:false});
 const search=useQuery({queryKey:['resource','search','initial'],queryFn:()=>request('/api/search/products') as Promise<unknown[]>,retry:false});
 const online=health.filter(item=>item.data?.status==='UP').length;
 return <section className="page"><div className="page-heading"><div><p className="eyebrow">YOUR PRODUCT WORKSPACE</p><h1>Everything, in one place.</h1><p className="subtitle">A clearer view of your product information.</p></div><span className="date-label">NEXUS PIM / LOCAL</span></div>
 <div className="metrics"><article><span>Connected services</span><strong>{health.some(item=>item.isPending)?'…':`${online} / 7`}</strong><small><i className={online===7?'dot positive':'dot'}/> {online===7?'All systems operational':'Service availability'}</small></article><article><span>Attribute definitions</span><strong>{attributes.data?.length??'—'}</strong><small>Reusable product fields</small></article><article><span>Indexed products</span><strong>{search.data?.length??'—'}</strong><small>Current search results · up to 20</small></article></div>
 <div className="section-title"><h2>Explore your workspace</h2><span>Built around your product lifecycle</span></div><div className="resource-cards">{resources.map((resource,index)=><Link className="resource-card" to={`/${resource.id}`} key={resource.id}><div><span className={`tile-icon color-${index%4}`}>{['◈','⌘','≡','▦','Aa','▧','⇄','⌕'][index]}</span><span className="card-arrow">↗</span></div><h3>{resource.title}</h3><p>{resource.subtitle}</p><span className="card-detail">{resource.operations.length} available actions <b>→</b></span></Link>)}</div>
 <section className="panel system-panel"><div className="panel-heading"><div><h2>Service status</h2><p>Checked against live backend health endpoints.</p></div><button className="text-button" onClick={()=>health.forEach(item=>void item.refetch())}>Refresh ↻</button></div><div className="service-grid">{services.map((service,index)=><div key={service}><span>{service}</span><b className={health[index].data?.status==='UP'?'status-up':'status-down'}>{health[index].isPending?'Checking':health[index].data?.status==='UP'?'Operational':'Unavailable'}</b></div>)}</div></section>
 </section>;
}
