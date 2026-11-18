import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
const ports: Record<string,number> = {catalog:8081,attribute:8082,taxonomy:8083,content:8084,media:8085,workflow:8086,search:8087};
const proxy = Object.fromEntries(Object.entries(ports).flatMap(([name,port]) => [
 [`/api/${name}`, {target:`http://127.0.0.1:${port}`,changeOrigin:true}],
 [`/health/${name}`, {target:`http://127.0.0.1:${port}`,rewrite:()=>'/actuator/health'}]
]));
export default defineConfig({plugins:[react()],server:{host:'127.0.0.1',port:5173,strictPort:true,proxy},
 test:{environment:'jsdom',setupFiles:['./src/test/setup.ts'],maxWorkers:1}});
