<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Task Flow</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="icon" type="image/svg+xml" href="${ctx}/images/favicon.svg?v=3">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@500;600;700&family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@600;700;800&display=swap" rel="stylesheet">
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css">

<style>
/* Task Flow landing page: self-contained so the landing page cannot break if the external stylesheet is stale. */
.landing-page{margin:0;background:#f7fafb;color:#0b2a4a;font-family:"DM Sans",Arial,sans-serif;overflow-x:hidden}
.landing-page *{box-sizing:border-box}.landing-page a{text-decoration:none;color:inherit}
.landing-page .front-page{min-height:100vh;background:#f8fbfb;padding-bottom:24px}
.landing-page .front-nav{height:74px;width:min(1220px,calc(100% - 56px));margin:0 auto;display:grid;grid-template-columns:1fr auto 1fr;align-items:center;gap:28px;padding:0 18px;background:#fff}
.landing-page .front-brand{display:flex;align-items:center;gap:9px;font:800 18px/1 "DM Sans",Arial,sans-serif;color:#0b2a4a}
.landing-page .front-brand-mark{width:29px;height:29px;color:#16a985;display:grid;place-items:center}.landing-page .front-brand-mark svg{width:29px;height:29px}
.landing-page .front-links{display:flex;align-items:center;justify-content:center;gap:38px}.landing-page .front-links a{font-size:11px;font-weight:600;color:#48617a;padding:9px 2px;position:relative}.landing-page .front-links a.active{color:#0b2a4a}.landing-page .front-links a.active:after{content:"";position:absolute;left:50%;bottom:2px;width:18px;height:2px;background:#14a88a;border-radius:99px;transform:translateX(-50%)}
.landing-page .front-actions{display:flex;justify-content:flex-end;align-items:center;gap:24px}.landing-page .front-login{font-size:11px;font-weight:600;color:#48617a}.landing-page .front-start{display:inline-flex;align-items:center;justify-content:center;height:34px;padding:0 19px;border-radius:18px;background:#0b2a4a;color:#fff;font-size:10px;font-weight:800;box-shadow:0 6px 14px rgba(11,42,74,.16)}
.landing-page .front-hero{position:relative;width:min(1220px,calc(100% - 56px));height:440px;margin:0 auto;overflow:hidden;background:linear-gradient(90deg,#fff8ea 0%,#fff7e9 42%,#eaf5f2 100%);}
.landing-page .front-copy{position:relative;z-index:5;width:45%;height:100%;padding:88px 0 0 42px}.landing-page .front-copy h1{margin:0;color:#0b2a4a;font-family:"Caveat","Comic Sans MS",cursive;font-size:59px;font-weight:700;line-height:.94;letter-spacing:-1.4px}.landing-page .front-subtitle{margin:18px 0 0;color:#173f60;font-size:18px;font-weight:600}.landing-page .front-description{margin:13px 0 0;color:#55728b;font-size:12px;line-height:1.65;max-width:395px}.landing-page .front-cta{display:flex;gap:14px;margin-top:22px}.landing-page .front-cta-primary,.landing-page .front-cta-secondary{height:39px;min-width:110px;border-radius:21px;display:inline-flex;align-items:center;justify-content:center;font-size:10px;font-weight:800}.landing-page .front-cta-primary{background:#0b2a4a;color:#fff;box-shadow:0 8px 18px rgba(11,42,74,.15)}.landing-page .front-cta-secondary{background:rgba(255,255,255,.7);border:1px solid #a9c5d4;color:#274b67}
.landing-page .front-scene{position:absolute;z-index:1;left:31%;right:0;top:0;bottom:0;overflow:hidden;background:#f8efdF}.landing-page .front-scene:before{content:"";position:absolute;z-index:2;inset:0;background:linear-gradient(90deg,#fff7e9 0%,rgba(255,247,233,.86) 7%,rgba(255,247,233,0) 30%);pointer-events:none}.landing-page .landscape-svg{position:absolute;inset:0;width:100%;height:100%;display:block}
.landing-page .front-note{position:absolute;z-index:5;top:58px;right:145px;display:flex;flex-direction:column;align-items:center;font-family:"Caveat",cursive;font-size:20px;font-weight:600;line-height:.84;color:#396d7d;transform:rotate(-7deg);text-align:center}.landing-page .front-note svg{width:19px;height:43px;margin-top:2px;transform:translateX(7px)}
.landing-page .front-features{position:relative;z-index:8;width:min(1220px,calc(100% - 56px));margin:-1px auto 0;display:grid;grid-template-columns:repeat(5,1fr);background:#fff;border:1px solid #edf0ee;border-radius:2px 2px 13px 13px;box-shadow:0 8px 26px rgba(31,61,76,.08);overflow:hidden}.landing-page .front-feature{min-height:126px;padding:18px 12px 14px;text-align:center;border-right:1px solid #edf0ee}.landing-page .front-feature:last-child{border-right:0}.landing-page .front-feature-icon{width:40px;height:40px;border-radius:50%;margin:0 auto 9px;display:grid;place-items:center}.landing-page .front-feature-icon svg{width:19px;height:19px}.landing-page .front-feature h3{margin:0;color:#24455f;font-size:11px;line-height:1.15;font-weight:800}.landing-page .front-feature p{margin:7px 0 0;color:#72869a;font-size:9px;line-height:1.4}.landing-page .feature-pink .front-feature-icon{background:#eadcff;color:#a35ac5}.landing-page .feature-green .front-feature-icon{background:#d8f3e7;color:#2c9b73}.landing-page .feature-orange .front-feature-icon{background:#ffe4b7;color:#e18b23}.landing-page .feature-purple .front-feature-icon{background:#e7ddff;color:#7657ca}.landing-page .feature-mint .front-feature-icon{background:#d9f4e9;color:#2b9776}
@media(max-width:900px){.landing-page .front-nav{width:calc(100% - 28px);grid-template-columns:1fr auto;height:68px}.landing-page .front-links{display:none}.landing-page .front-hero{width:calc(100% - 28px);height:650px}.landing-page .front-copy{width:100%;height:auto;padding:54px 28px 0}.landing-page .front-scene{left:0;top:180px}.landing-page .front-scene:before{background:linear-gradient(180deg,#fff7e9 0%,rgba(255,247,233,.65) 12%,rgba(255,247,233,0) 38%)}.landing-page .front-features{width:calc(100% - 28px);grid-template-columns:repeat(2,1fr)}.landing-page .front-feature:nth-child(2){border-right:0}.landing-page .front-feature:nth-child(n+3){border-top:1px solid #edf0ee}.landing-page .front-feature:nth-child(5){grid-column:1/-1;border-right:0}}
@media(max-width:560px){.landing-page .front-nav{width:calc(100% - 18px);padding:0 9px}.landing-page .front-login{display:none}.landing-page .front-hero{width:calc(100% - 18px);height:610px}.landing-page .front-copy{padding:40px 20px 0}.landing-page .front-copy h1{font-size:49px}.landing-page .front-subtitle{font-size:15px}.landing-page .front-description{font-size:11px}.landing-page .front-note{right:45px;top:56px;font-size:17px}.landing-page .front-features{width:calc(100% - 18px)}.landing-page .front-feature{min-height:122px}}
/* Keep the landscape behind the complete landing page. */
.landing-page{background:#eaf2ef url('${ctx}/images/front-bg.png?v=2') center center/cover fixed no-repeat}
.landing-page .front-page{background:#eaf2ef url('${ctx}/images/front-bg.png?v=2') center center/cover fixed no-repeat}
.landing-page .front-nav{background:rgba(255,255,255,.86);backdrop-filter:blur(12px)}
.landing-page .front-hero{background:transparent}
.landing-page .front-features{background:rgba(255,255,255,.94);backdrop-filter:blur(10px)}
.landing-page .front-scene{display:none}
@media(max-width:900px){.landing-page,.landing-page .front-page{background-attachment:scroll;background-position:61% top}}
.landing-page .front-brand{font-size:23px;gap:12px;letter-spacing:-.04em}
.landing-page .front-brand-mark{width:38px;height:38px;border-radius:13px;background:linear-gradient(145deg,#2bd4ae,#087a68);color:#fff;box-shadow:0 8px 20px rgba(16,168,138,.25)}
.landing-page .front-brand-mark svg{width:26px;height:26px}
.landing-page .front-links a{font-size:14px}.landing-page .front-login{font-size:14px}
.landing-page .front-start{height:42px;padding:0 22px;font-size:13px;border-radius:22px}
.landing-page .front-copy h1{font-size:clamp(66px,6vw,82px)}
.landing-page .front-subtitle{font-size:21px}.landing-page .front-description{font-size:16px;line-height:1.75}
.landing-page .front-cta-primary,.landing-page .front-cta-secondary{height:47px;min-width:132px;font-size:13px}
.landing-page .front-feature h3{font-size:14px}.landing-page .front-feature p{font-size:12px;line-height:1.5}
@media(max-width:900px){.landing-page .front-copy h1{font-size:60px}}
@media(max-width:560px){.landing-page .front-brand{font-size:20px}.landing-page .front-copy h1{font-size:49px}.landing-page .front-subtitle{font-size:16px}.landing-page .front-description{font-size:13px}.landing-page .front-feature h3{font-size:13px}.landing-page .front-feature p{font-size:11px}}

/* Match the supplied compact, illustrated landing-page reference. */
.landing-page{min-height:100vh;padding:18px 0 24px;background:#f2f5f7;color:#102f4d}
.landing-page .front-page{width:min(1280px,calc(100% - 32px));min-height:0;margin:0 auto;padding:0 0 22px;background:rgba(255,255,255,.34);border:1px solid rgba(20,48,74,.08);border-radius:14px;overflow:hidden;box-shadow:0 10px 32px rgba(22,45,66,.08)}
.landing-page .front-nav{width:100%;height:58px;padding:0 28px;grid-template-columns:130px 1fr auto;gap:24px;background:rgba(255,255,255,.82);backdrop-filter:blur(8px)}
.landing-page .front-brand{font-size:17px;gap:6px}.landing-page .front-brand-mark{width:24px;height:24px;background:transparent;color:#148d71;box-shadow:none;border-radius:0}.landing-page .front-brand-mark svg{width:24px;height:24px}.landing-page .front-brand-mark svg path:first-child{fill:currentColor;stroke:none}.landing-page .front-brand-mark svg path:last-child{stroke:#e2f6ed}
.landing-page .front-links{justify-self:start;gap:30px}.landing-page .front-links a{font-size:12px;color:#314e67}.landing-page .front-links a.active:after{display:none}
.landing-page .front-actions{gap:24px}.landing-page .front-login{font-size:12px;color:#314e67}.landing-page .front-start{height:28px;min-width:76px;padding:0 12px;font-size:10px;border-radius:18px}
.landing-page .front-hero{width:100%;height:clamp(380px,43vw,520px);min-height:0;margin:0;background-color:#f8eedb;background-image:linear-gradient(90deg,rgba(255,249,239,.98) 0%,rgba(255,249,239,.91) 28%,rgba(255,249,239,.48) 47%,rgba(255,249,239,0) 68%),url('${ctx}/images/front-bg.png?v=4');background-position:center,center;background-size:cover,cover;background-repeat:no-repeat}
.landing-page .front-copy{width:48%;padding:35px 0 0 36px}.landing-page .front-copy h1{font-size:clamp(39px,5.2vw,66px);line-height:.92;letter-spacing:-.7px;color:#102f4d}
.landing-page .front-subtitle{margin-top:14px;font-size:16px;color:#254d6c}.landing-page .front-description{max-width:330px;margin-top:12px;font-size:12px;line-height:1.55;color:#486982}
.landing-page .front-cta{gap:12px;margin-top:17px}.landing-page .front-cta-primary,.landing-page .front-cta-secondary{height:35px;min-width:108px;font-size:11px;border-radius:20px}
.landing-page .front-note{top:7%;right:20%;font-size:19px;line-height:.92;color:#234c69}.landing-page .front-note svg{height:42px}
.landing-page .front-features{width:calc(100% - 40px);margin:0 auto;background:rgba(255,255,255,.92);border:1px solid rgba(31,61,76,.07);border-radius:0 0 11px 11px;box-shadow:0 8px 25px rgba(31,61,76,.08)}
.landing-page .front-feature{min-height:128px;padding:14px 12px 13px;border-color:#edf0ee}.landing-page .front-feature-icon{width:39px;height:39px;margin-bottom:8px}.landing-page .front-feature-icon svg{width:20px;height:20px}.landing-page .front-feature h3{font-size:12px;line-height:1.25;color:#284760}.landing-page .front-feature p{margin-top:7px;font-size:10px;line-height:1.45;color:#708398}
@media(max-width:900px){.landing-page{padding:12px 0}.landing-page .front-page{width:calc(100% - 24px)}.landing-page .front-nav{height:54px;padding:0 11px 0 18px;grid-template-columns:109px 1fr auto}.landing-page .front-links{display:flex;gap:15px}.landing-page .front-hero{height:clamp(277px,34vw,420px);min-height:0}.landing-page .front-copy{width:54%;padding:32px 0 0 24px}.landing-page .front-copy h1{font-size:clamp(36px,4.8vw,54px)}.landing-page .front-note{right:14%}.landing-page .front-features{width:calc(100% - 40px);grid-template-columns:repeat(5,minmax(0,1fr))}.landing-page .front-feature:nth-child(2),.landing-page .front-feature:nth-child(4){border-right:1px solid #edf0ee}.landing-page .front-feature:nth-child(n+3){border-top:0}.landing-page .front-feature:nth-child(5){grid-column:auto}}
@media(max-width:560px){.landing-page{padding:0}.landing-page .front-page{width:100%;border-radius:0;border:0}.landing-page .front-nav{height:58px;padding:0 14px;grid-template-columns:1fr auto}.landing-page .front-links{display:none}.landing-page .front-brand{font-size:18px}.landing-page .front-start{height:32px}.landing-page .front-hero{height:570px;background-position:center,58% center;background-image:linear-gradient(180deg,rgba(255,249,239,.98) 0%,rgba(255,249,239,.9) 34%,rgba(255,249,239,.12) 63%),url('${ctx}/images/front-bg.png?v=4');background-size:cover,cover}.landing-page .front-copy{width:100%;padding:32px 22px 0}.landing-page .front-copy h1{font-size:48px}.landing-page .front-subtitle{margin-top:12px;font-size:15px}.landing-page .front-description{font-size:12px}.landing-page .front-note{top:48%;right:10%;font-size:16px}.landing-page .front-features{width:calc(100% - 20px);grid-template-columns:repeat(2,1fr)}.landing-page .front-feature{min-height:120px}.landing-page .front-feature:nth-child(2){border-right:0}.landing-page .front-feature:nth-child(n+3){border-top:1px solid #edf0ee}.landing-page .front-feature:nth-child(5){grid-column:1/-1}}
</style>
</head>
<body class="landing-page">
  <main class="front-page">
    <header class="front-nav">
      <a class="front-brand" href="${ctx}/" aria-label="Task Flow home">
        <span class="front-brand-mark" aria-hidden="true">
          <svg viewBox="0 0 32 32" fill="none" stroke="currentColor" stroke-width="2.1" stroke-linecap="round" stroke-linejoin="round">
            <path d="M25.5 5.5C15.2 5.7 7.4 10.5 7.4 18.2c0 4.4 2.9 7.1 6.9 7.1 7.7 0 12.1-7.7 11.2-19.8Z"/>
            <path d="M7.7 25.1c3.4-6.1 7.9-10.3 14.1-14.4"/>
          </svg>
        </span>
        <span>Task Flow</span>
      </a>

      <nav class="front-links" aria-label="Main navigation">
        <a class="active" href="${ctx}/">Home</a>
        <a href="#features">Features</a>
        <a href="#about">About</a>
      </nav>

      <div class="front-actions">
        <a class="front-login" href="${ctx}/login">Login</a>
        <a class="front-start" href="${ctx}/register">Get Started</a>
      </div>
    </header>

    <section class="front-hero" id="about">
      <div class="front-copy">
        <h1>Plan less.<br>Do more.</h1>
        <p class="front-subtitle">Your personal productivity companion.</p>
        <p class="front-description">Keep your plans simple, focus on what matters,<br class="desktop-break"> and make steady progress every day.</p>
        <div class="front-cta">
          <a class="front-cta-primary" href="${ctx}/register">Get Started</a>
          <a class="front-cta-secondary" href="${ctx}/login">Login</a>
        </div>
      </div>

      <div class="front-scene" aria-label="A calm productivity landscape illustration">
        <svg class="landscape-svg" viewBox="0 0 760 430" role="img" aria-hidden="true" preserveAspectRatio="xMidYMid slice">
          <defs>
            <linearGradient id="sky" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stop-color="#FFF9ED"/>
              <stop offset=".55" stop-color="#FCEBD8"/>
              <stop offset="1" stop-color="#E7F5EF"/>
            </linearGradient>
            <linearGradient id="mountainBack" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0" stop-color="#BBDDE1"/>
              <stop offset="1" stop-color="#93C9D2"/>
            </linearGradient>
            <linearGradient id="mountainMid" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0" stop-color="#8AC2CF"/>
              <stop offset="1" stop-color="#4D9FB0"/>
            </linearGradient>
            <linearGradient id="mountainFront" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0" stop-color="#75BBA6"/>
              <stop offset="1" stop-color="#378A7D"/>
            </linearGradient>
            <linearGradient id="water" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stop-color="#D8F1ED"/>
              <stop offset="1" stop-color="#A8D8D4"/>
            </linearGradient>
            <filter id="softGlow" x="-50%" y="-50%" width="200%" height="200%">
              <feGaussianBlur stdDeviation="14"/>
            </filter>
          </defs>
          <rect width="760" height="430" fill="url(#sky)"/>
          <ellipse cx="493" cy="135" rx="55" ry="55" fill="#FFF3C8" opacity=".7" filter="url(#softGlow)"/>
          <circle cx="493" cy="135" r="39" fill="#FFF1C4"/>
          <path d="M0 273C75 242 103 215 165 235c51 17 76-21 128-31 58-11 83 34 132 28 47-6 73-53 122-52 47 1 78 47 121 45 37-2 61-20 92-31v236H0Z" fill="url(#mountainBack)" opacity=".82"/>
          <path d="M0 310c72-32 116-76 169-66 55 10 73 50 119 48 63-3 79-73 137-74 51-1 75 54 122 61 53 8 89-37 140-44 31-4 51 6 73 17v178H0Z" fill="url(#mountainMid)"/>
          <path d="M0 345c72-28 123-52 174-45 52 8 75 47 121 47 54 0 76-43 123-51 62-11 96 34 148 38 46 4 81-21 112-33 29-11 55-11 82-1v130H0Z" fill="url(#mountainFront)" opacity=".96"/>
          <path d="M0 351c75-12 145-7 201 18 56 25 91 34 149 30 62-4 104-29 159-24 59 5 101 38 159 39 34 1 65-8 92-17v34H0Z" fill="url(#water)"/>
          <path d="M0 381c97-18 175-4 248 13 67 16 129 8 191 2 63-7 116-4 168 11 52 15 98 15 153 3v20H0Z" fill="#D6EDE7" opacity=".8"/>
          <path d="M577 213c13-31 29-59 45-82 16 21 28 47 35 82" fill="#76BCA6"/>
          <path d="M598 217c5-27 15-57 24-72 10 20 17 43 21 72" fill="#4F9A86"/>
          <g transform="translate(486 249)">
            <ellipse cx="41" cy="129" rx="48" ry="9" fill="#5E9C92" opacity=".28"/>
            <path d="M30 64c0-25 11-43 27-43 20 0 34 25 34 51 0 22-9 44-26 52-21 9-35-22-35-60Z" fill="#F39C62"/>
            <path d="M39 44c1-20 10-33 25-33 17 0 27 15 27 34-13-9-34-11-52-1Z" fill="#202C40"/>
            <path d="M38 74c-13 8-19 20-19 36v32h22l10-36 12 36h20v-39c0-13-7-23-17-29Z" fill="#F5A866"/>
            <path d="M20 140h23l-2 15H17Z" fill="#294761"/><path d="M62 140h21l7 15H63Z" fill="#294761"/>
            <path d="M37 73c8 6 17 8 28 6" stroke="#F5D1AE" stroke-width="3" fill="none" stroke-linecap="round"/>
          </g>
          <g transform="translate(418 340)">
            <path d="M14 31c-8-7-11-16-6-22 6-7 15-2 21 3 8-5 17-10 23-3 5 6 1 15-7 22-7 6-20 10-31 0Z" fill="#27364A"/>
            <circle cx="31" cy="10" r="8" fill="#27364A"/>
            <path d="M23 3 19-3l9 4M38 3l6-5-2 9" fill="#27364A"/>
            <path d="M6 31c12 8 30 9 46 1" stroke="#27364A" stroke-width="5" stroke-linecap="round"/>
          </g>
          <g opacity=".5" stroke="#FFFFFF" stroke-width="2" stroke-linecap="round">
            <path d="M72 328h126"/><path d="M110 338h88"/><path d="M554 369h111"/><path d="M576 379h69"/>
          </g>
          <g transform="translate(654 238)">
            <path d="M45 185C31 151 30 115 44 83c11-26 24-44 45-67 17 29 24 58 17 87-10 39-28 62-61 82Z" fill="#4C927F"/>
            <path d="M54 184c8-39 20-75 38-110" stroke="#2F6F63" stroke-width="4" stroke-linecap="round"/>
            <path d="M78 102c-17-3-29-10-39-24 20-4 36 2 48 13M87 81c-10-15-12-29-8-45 14 12 18 27 15 43M70 125c-17 3-31 1-44-8 17-9 31-7 44 1" fill="#67AD96"/>
          </g>
        </svg>

        <div class="front-note" aria-hidden="true">
          <span>Better</span>
          <span>Days</span>
          <span>Ahead</span>
          <svg viewBox="0 0 30 65" fill="none">
            <path d="M12 3c8 7 9 12 1 17" stroke="#1D8070" stroke-width="2.2" stroke-linecap="round"/>
            <path d="M12 21c8 7 9 12 1 17" stroke="#1D8070" stroke-width="2.2" stroke-linecap="round"/>
            <path d="M12 39c8 7 9 12 1 17" stroke="#E56C67" stroke-width="2.2" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
    </section>

    <section class="front-features" id="features">
      <article class="front-feature feature-pink">
        <div class="front-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3v18M5 8h7a4 4 0 0 1 4 4 4 4 0 0 1-4 4H5"/><path d="m17 7 2-2 2 2"/></svg></div>
        <h3>Smart Task<br>Management</h3>
        <p>Stay organised<br>and focused.</p>
      </article>
      <article class="front-feature feature-green">
        <div class="front-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"><rect x="4" y="5" width="16" height="15" rx="2"/><path d="M8 3v4M16 3v4M4 10h16M8 14h3M8 17h6"/></svg></div>
        <h3>Calendar Planning</h3>
        <p>Never miss<br>a deadline.</p>
      </article>
      <article class="front-feature feature-orange">
        <div class="front-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19V9M10 19V5M16 19v-8M22 19H2"/><path d="M5 6h3M13 3h3"/></svg></div>
        <h3>Productivity<br>Insights</h3>
        <p>Track your progress<br>and improve.</p>
      </article>
      <article class="front-feature feature-purple">
        <div class="front-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M8 4h8v5a4 4 0 0 1-8 0Z"/><path d="M8 6H5v2a3 3 0 0 0 3 3M16 6h3v2a3 3 0 0 1-3 3M12 13v4M8 20h8M9 17h6"/></svg></div>
        <h3>Achievements</h3>
        <p>Turn consistency<br>into rewards.</p>
      </article>
      <article class="front-feature feature-mint">
        <div class="front-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3a9 9 0 1 0 8.2 5.3"/><path d="M12 7v5l3 2"/><path d="M16 3h5v5"/></svg></div>
        <h3>Privacy First</h3>
        <p>Your data belongs<br>to you.</p>
      </article>
    </section>
    <footer class="front-footer">&#169; 2026 Task Flow</footer>
  </main>
</body>
</html>
