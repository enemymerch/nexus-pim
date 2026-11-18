export type Field = {key:string; label:string; location:'path'|'query'|'body'; kind?:'text'|'number'|'boolean'|'array'|'json'; required?:boolean; nullable?:boolean; choices?:string[]; initial?:string; help?:string};
export type Operation = {id:string; label:string; method:'GET'|'POST'|'PUT'|'PATCH'|'DELETE'; path:string; fields:Field[]; destructive?:boolean};
export type Resource = {id:string; title:string; service:string; subtitle:string; description:string; operations:Operation[]; auto?:string};
