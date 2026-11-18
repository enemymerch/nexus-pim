// Trusted check runner: the model cannot edit this file, dependency manifests or build configuration.
import { spawnSync } from 'node:child_process';
import { mkdirSync, readFileSync, writeFileSync, rmSync, statSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
const testFile = process.argv[2] || null;
if (testFile && !/^src\/[A-Za-z0-9_/-]+\.test\.tsx?$/.test(testFile)) throw Error('Invalid approved test path');
const report = resolve('build/test-results/TEST-frontend.xml');
const jsonFile = resolve('build/vitest-results.json');
mkdirSync(dirname(report), {recursive:true}); rmSync(report, {force:true}); rmSync(jsonFile,{force:true});
function run(file, args) {
  const result = spawnSync(process.execPath, [file, ...args], {stdio:'inherit', timeout:150000});
  if (result.error) throw result.error;
  return result.status ?? 1;
}
for (const [file,args] of [['node_modules/typescript/bin/tsc',['--noEmit']], ['node_modules/vite/bin/vite.js',['build']]]) {
  const code = run(file,args); if(code !== 0) process.exit(code);
}
const code = run('node_modules/vitest/vitest.mjs',['run',...(testFile ? [testFile] : []),'--reporter=default','--reporter=json',`--outputFile.json=${jsonFile}`]);
if (statSync(jsonFile).size > 2000000) throw Error('Test evidence exceeds byte limit');
const data = JSON.parse(readFileSync(jsonFile,'utf8'));
const assertions = (data.testResults || []).flatMap(suite => suite.assertionResults || []);
if (!assertions.length || assertions.length !== data.numTotalTests) throw Error('Missing or inconsistent executed test evidence');
const escape = value => String(value).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;').replaceAll("'",'&apos;');
let failures=0,skipped=0;
const cases = assertions.map(test => {
  if (!['passed','failed','pending','skipped','todo'].includes(test.status)) throw Error('Unknown test outcome');
  let result='';
  if(test.status==='failed'){failures++;result=`<failure>${escape((test.failureMessages||[]).join('\n').slice(0,20000))}</failure>`;}
  else if(test.status!=='passed'){skipped++;result='<skipped/>';}
  return `<testcase classname="FrontendTests" name="${escape(test.fullName||test.title)}">${result}</testcase>`;
}).join('');
writeFileSync(report,`<testsuite name="FrontendTests" tests="${assertions.length}" failures="${failures}" errors="0" skipped="${skipped}">${cases}</testsuite>`);
process.exit(code === 0 && failures === 0 ? 0 : 1);
