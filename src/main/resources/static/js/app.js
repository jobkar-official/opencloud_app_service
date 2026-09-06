
function qs(s){return document.querySelector(s)}
function qsa(s){return [...document.querySelectorAll(s)]}
// Tabs
qsa('[data-tab]').forEach(btn=>{
  btn.addEventListener('click',()=>{
    const g=btn.dataset.tabGroup||'default';
    qsa(`[data-tab][data-tab-group='${g}']`).forEach(b=>b.classList.remove('active'));
    qsa(`[data-tab-panel][data-tab-group='${g}']`).forEach(p=>p.style.display='none');
    btn.classList.add('active');
    const panel=qs(`[data-tab-panel='${btn.dataset.tab}'][data-tab-group='${g}']`);
    if(panel) panel.style.display='block';
  })
});
// Wizard
let wizardIndex=0;
const steps=qsa('.wizard-step');
function showWizard(i){steps.forEach((s,idx)=>s.classList.toggle('active',idx===i)); wizardIndex=i; const bar=qs('#wizard-progress'); if(bar) bar.style.width=((i+1)/steps.length*100)+'%';}
qsa('[data-wizard-next]').forEach(b=>b.addEventListener('click',()=>{if(wizardIndex<steps.length-1) showWizard(wizardIndex+1)}));
qsa('[data-wizard-prev]').forEach(b=>b.addEventListener('click',()=>{if(wizardIndex>0) showWizard(wizardIndex-1)}));
showWizard(0);

// Provider icon picker - shows selected icon
qsa('[data-provider]').forEach(card=>{
  card.addEventListener('click',()=>{
    qsa('[data-provider]').forEach(c=>c.classList.remove('selected'));
    card.classList.add('selected');
    const inp=qs('input[name=provider]'); if(inp) inp.value=card.dataset.provider;
  })
});

async function api(path, opts={}){const r=await fetch(path,{headers:{'Content-Type':'application/json',...(opts.headers||{})},...opts}); if(!r.ok) throw new Error(await r.text()); return r.json();}
