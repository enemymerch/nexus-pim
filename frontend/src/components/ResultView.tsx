import {useState} from 'react';
const title=(value:string)=>value.replace(/([A-Z])/g,' $1').replace(/^./,char=>char.toUpperCase());
function Value({value}:{value:unknown}){if(value===null||value===undefined)return <span className="muted">—</span>;if(typeof value==='boolean')return <span className={`pill ${value?'positive':''}`}>{value?'Yes':'No'}</span>;if(typeof value==='object')return <ResultView data={value}/>;return <span>{String(value)}</span>;}
export function ResultView({data}:{data:unknown}){
 const [expanded,setExpanded]=useState<number|null>(null);
 if(Array.isArray(data)){
  if(!data.length)return <div className="empty-state"><span className="empty-icon">◇</span><h3>No records to show</h3><p>Try another filter, or create a record to get started.</p></div>;
  if(data.every(item=>item!==null&&typeof item==='object'&&!Array.isArray(item))){
   const columns=Array.from(new Set(data.flatMap(item=>Object.keys(item)))).filter(key=>data.some(item=>item[key]===null||typeof item[key]!=='object')).slice(0,6);
   return <><div className="table-scroll"><table><thead><tr>{columns.map(key=><th key={key}>{title(key)}</th>)}<th><span className="sr-only">Details</span></th></tr></thead><tbody>{data.map((row,index)=><tr key={String(row.id||row.code||index)}>{columns.map(key=><td key={key}><Value value={row[key]}/></td>)}<td><button className="text-button" onClick={()=>setExpanded(expanded===index?null:index)} aria-expanded={expanded===index}>Details ↗</button></td></tr>)}</tbody></table></div>{expanded!==null&&<div className="record-detail"><ResultView data={data[expanded]}/></div>}</>;
  }return <ul className="value-list">{data.map((value,index)=><li key={index}><Value value={value}/></li>)}</ul>;
 }
 if(data!==null&&typeof data==='object')return <dl className="record-fields">{Object.entries(data).map(([key,value])=><div key={key}><dt>{title(key)}</dt><dd><Value value={value}/></dd></div>)}</dl>;
 return <ValueText data={data}/>;
}
function ValueText({data}:{data:unknown}){return <p>{data===null?'Operation completed successfully.':String(data)}</p>;}
