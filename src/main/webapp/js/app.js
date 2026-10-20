document.addEventListener('DOMContentLoaded',()=>{
  document.querySelectorAll('[data-auto-submit]').forEach(e=>e.addEventListener('change',()=>e.form?.requestSubmit()));
  document.querySelectorAll('[data-confirm]').forEach(e=>e.addEventListener('click',ev=>{if(!confirm(e.dataset.confirm))ev.preventDefault();}));
});
function passwordStrength(v){const bar=document.querySelector('.strength span');if(!bar)return;let n=0;if(v.length>=8)n++;if(/[A-Z]/.test(v))n++;if(/[0-9]/.test(v))n++;if(/[^A-Za-z0-9]/.test(v))n++;bar.style.width=(n*25)+'%';}

document.addEventListener('DOMContentLoaded',()=>{
  const menu=document.getElementById('mobileMenuBtn');
  const sidebar=document.getElementById('sidebar');
  const closeMenu=()=>{
    if(!menu || !sidebar || !sidebar.classList.contains('open'))return;
    sidebar.classList.remove('open');
    menu.setAttribute('aria-expanded','false');
    menu.setAttribute('aria-label','Open navigation');
  };
  if(menu && sidebar){
    menu.addEventListener('click',()=>{
      const open=sidebar.classList.toggle('open');
      menu.setAttribute('aria-expanded',String(open));
      menu.setAttribute('aria-label',open?'Close navigation':'Open navigation');
    });
    document.querySelectorAll('.sidebar a').forEach(a=>a.addEventListener('click',closeMenu));
    document.addEventListener('click',event=>{
      if(sidebar.classList.contains('open') && !sidebar.contains(event.target) && !menu.contains(event.target))closeMenu();
    });
    document.addEventListener('keydown',event=>{
      if(event.key==='Escape' && sidebar.classList.contains('open')){closeMenu();menu.focus();}
    });
  }
  const themeToggle=document.getElementById('themeToggle');
  const syncThemeToggle=()=>{
    if(!themeToggle)return;
    const dark=document.documentElement.getAttribute('data-theme')==='dark';
    themeToggle.setAttribute('aria-label',dark?'Switch to light mode':'Switch to dark mode');
    themeToggle.setAttribute('aria-pressed',String(dark));
    const label=themeToggle.querySelector('.theme-toggle-label');
    if(label)label.textContent=dark?'Light':'Dark';
    const preference=document.getElementById('themePreference');
    if(preference)preference.value=dark?'dark':'light';
  };
  syncThemeToggle();
  if(themeToggle)themeToggle.addEventListener('click',()=>{
    const dark=document.documentElement.getAttribute('data-theme')!=='dark';
    document.documentElement.setAttribute('data-theme',dark?'dark':'light');
    try{localStorage.setItem('taskflow-theme',dark?'dark':'light');}catch(ignored){}
    syncThemeToggle();
  });
  const themePreference=document.getElementById('themePreference');
  if(themePreference)themePreference.addEventListener('change',()=>{
    const dark=themePreference.value==='dark';
    document.documentElement.setAttribute('data-theme',dark?'dark':'light');
    try{localStorage.setItem('taskflow-theme',dark?'dark':'light');}catch(ignored){}
    syncThemeToggle();
  });
  const currentPath=window.location.pathname;
  document.querySelectorAll('.sidebar-links a[href],.mobile-nav a[href]').forEach(link=>{
    try{
      const target=new URL(link.href,window.location.href);
      const active=target.pathname===currentPath && (!target.search || target.search===window.location.search);
      if(active)link.setAttribute('aria-current','page');
      else link.removeAttribute('aria-current');
    }catch(ignored){}
  });
  document.querySelectorAll('[data-password-toggle]').forEach(btn=>{
    btn.addEventListener('click',()=>{
      const input=document.getElementById(btn.dataset.passwordToggle);
      if(!input)return;
      input.type=input.type==='password'?'text':'password';
      btn.setAttribute('aria-label',input.type==='password'?'Show password':'Hide password');
      if(btn.hasAttribute('data-toggle-text'))btn.textContent=input.type==='password'?'Show':'Hide';
    });
  });
});

document.addEventListener('DOMContentLoaded',()=>{
  const dateEl=document.getElementById('todayDate');
  if(dateEl){
    dateEl.textContent=new Intl.DateTimeFormat(undefined,{weekday:'long',month:'long',day:'numeric',year:'numeric'}).format(new Date());
  }
  const greetingDate=document.getElementById('greetingDate');
  if(greetingDate){
    greetingDate.textContent=new Intl.DateTimeFormat(undefined,{weekday:'long',month:'long',day:'numeric'}).format(new Date());
  }
});
