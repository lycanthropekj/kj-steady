const assert=require('assert'),fs=require('fs'),vm=require('vm');
const L=require('./assets/logic.js');
let template=()=>({version:1,settings:{start:'2026-11-04',deadline:'2027-02-01',startWeight:104,goal:85},entries:[]});
assert.equal(L.days('2026-11-04','2027-02-01'),89);assert(!L.validDate('2026-02-30'));
assert.throws(()=>L.validate({...template(),version:99}));assert.throws(()=>L.validate({...template(),settings:{...template().settings,deadline:'2026-11-03'}}));
const elements={};function el(id){return elements[id]??=( {value:'',checked:false,style:{},textContent:'',innerHTML:'',listeners:{},classList:{toggle(){}},addEventListener(k,f){this.listeners[k]=f;},click(){},files:[]});}
const storage={};let failSave=false;
function context(){let ctx={Logic:L,document:{getElementById:el,querySelectorAll(){return []},createElement:()=>el('download')},console,setTimeout:()=>1,clearTimeout(){},Date,JSON,Number,String,Math,Uint8Array,TextDecoder,atob,confirm:()=>true,localStorage:{getItem:k=>storage[k]||null,setItem:(k,v)=>{if(failSave)throw Error('full');storage[k]=v;}},window:{scrollTo(){}}};ctx.window.localStorage=ctx.localStorage;vm.createContext(ctx);vm.runInContext(fs.readFileSync('./assets/app.js','utf8'),ctx);return ctx;}
let c=context();c.openLog();el('weight').value='103.5';el('steps').value='11200';el('sleep').value='7.2';el('dinner').value='<script>alert(1)</script> & curd';el('entryForm').listeners.submit({preventDefault(){}});assert.equal(JSON.parse(storage['kj-steady']).entries[0].weight,103.5);
c=context();assert(el('latest').innerHTML.includes('103.5'));c.openLog();assert.equal(el('dinner').value,'<script>alert(1)</script> & curd');el('weight').value='103.2';el('entryForm').listeners.submit({preventDefault(){}});assert.equal(JSON.parse(storage['kj-steady']).entries.length,1);c.renderHistory();assert(el('historyList').innerHTML.includes('&lt;script&gt;'));assert(!el('historyList').innerHTML.includes('<script>'));
c.openLog();el('sys').value='120';el('dia').value='';el('entryForm').listeners.submit({preventDefault(){}});assert.equal(JSON.parse(storage['kj-steady']).entries[0].sys,null);
c.openLog();el('weight').value='102';failSave=true;el('entryForm').listeners.submit({preventDefault(){}});assert.equal(JSON.parse(storage['kj-steady']).entries[0].weight,103.2);failSave=false;
let old=storage['kj-steady'];c.applyBackup('{bad json');assert.equal(storage['kj-steady'],old);c.applyBackup(JSON.stringify(template()));assert.equal(JSON.parse(storage['kj-steady']).entries.length,0);
c.openLog();el('weight').value='103';el('entryForm').listeners.submit({preventDefault(){}});c.openLog();c.deleteCurrent();assert.equal(JSON.parse(storage['kj-steady']).entries.length,0);
console.log('PASS: date arithmetic, invalid dates, backup schema, deadline ordering, save/reload/edit, duplicate protection, text escaping, paired BP validation, failed-save preservation, invalid import, valid import, confirmed deletion.');
