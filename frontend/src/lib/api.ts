import type {Field,Operation} from './contracts';
export class ApiError extends Error { constructor(public status:number,message:string){super(message);} }
export function compileRequest(operation:Operation,values:Record<string,string>) {
 let path=operation.path;const query=new URLSearchParams();const body:Record<string,unknown>={};
 for(const field of operation.fields){
  const text=(values[field.key]??field.initial??'').trim();
  if(!text&&field.required)throw new Error(`${field.label} is required.`);
  let value:unknown=text;
  if(!text){if(field.kind==='array')value=[];else if(field.nullable)value=null;else continue;}
  else if(field.kind==='number'){value=Number(text);if(!Number.isFinite(value)||!Number.isSafeInteger(value as number)||Number(value)<0)throw new Error(`${field.label} must be a non-negative whole number.`);}
  else if(field.kind==='boolean')value=text==='true';
  else if(field.kind==='array')value=text.split(',').map(item=>item.trim()).filter(Boolean);
  else if(field.kind==='json'){try{value=JSON.parse(text);}catch{throw new Error(`${field.label} must contain valid JSON.`);}}
  if(field.location==='path')path=path.replace(`{${field.key}}`,encodeURIComponent(text));
  else if(field.location==='query'){if(text)query.set(field.key,text);}
  else body[field.key]=value;
 }
 if(/\{[^}]+\}/.test(path))throw new Error('A required identifier is missing.');
 return {url:path+(query.size?`?${query}`:''),body:operation.method==='GET'||operation.method==='DELETE'?undefined:body};
}
export async function request(url:string,method='GET',body?:unknown):Promise<unknown>{
 let response:Response;
 try{response=await fetch(url,{method,headers:body===undefined?undefined:{'Content-Type':'application/json'},body:body===undefined?undefined:JSON.stringify(body),signal:AbortSignal.timeout(15000)});}
 catch{throw new ApiError(0,'The service could not be reached. Check that the backend is running.');}
 const text=await response.text();let data:unknown=null;
 if(text){try{data=JSON.parse(text);}catch{data=text;}}
 if(!response.ok){const detail=data&&typeof data==='object'?data as Record<string,unknown>:{};throw new ApiError(response.status,String(detail.detail||detail.message||detail.error||`Request failed (${response.status}).`));}
 return data;
}
export async function execute(operation:Operation,values:Record<string,string>){const {url,body}=compileRequest(operation,values);return request(url,operation.method,body);}
