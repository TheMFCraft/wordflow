const STORAGE_KEY = "wordflow-v1";
const DAILY_GOAL = 24;
const TAB_ORDER = ["home", "learn", "stats", "pro"];
const RING = 2 * Math.PI * 52;

const ICONS = {
  home: `<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 12 12 3l9 9"/><path d="M5 10v10h14V10"/></svg>`,
  learn: `<svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="3" width="18" height="18" rx="3"/><path d="M8 12h8M12 8v8"/></svg>`,
  stats: `<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 20V10M10 20V4M16 20v-6M22 20v-4"/></svg>`,
  pro: `<svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="10"/><path d="M12 6v12M8 10l4-4 4 4"/></svg>`,
  profile: `<svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 4-7 8-7s8 3 8 7"/></svg>`,
  close: `<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 6 6 18M6 6l12 12"/></svg>`,
  check: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>`,
  x: `<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M18 6 6 18M6 6l12 12"/></svg>`,
  speak: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M11 5 6 9H2v6h4l5 4V5z"/><path d="M15.5 8.5a5 5 0 0 1 0 7"/><path d="M19 5a9 9 0 0 1 0 14"/></svg>`,
  lock: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><rect x="4" y="11" width="16" height="10" rx="2"/><path d="M8 11V8a4 4 0 0 1 8 0v3"/></svg>`,
  chevron: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M9 18l6-6-6-6"/></svg>`,
  flame: `<svg viewBox="0 0 24 24" width="16" height="16" fill="oklch(75% 0.16 65)" stroke="none" aria-hidden="true"><path d="M12 2s4 4.2 4 8a4 4 0 0 1-8 0c0-1.6.7-3.2 2-4.5C9 8 10 9.5 10 11c0 1.1.9 2 2 2s2-.9 2-2c0-3.2-2-5.8-2-9z"/><path d="M8.5 16.5A5.5 5.5 0 0 0 12 22a5.5 5.5 0 0 0 3.5-5.5c0-2-1.2-3.6-2.2-4.6-.3 1.3-1 2.1-1.3 2.1s-1-.8-1.3-2.1C9.7 12.9 8.5 14.5 8.5 16.5z"/></svg>`,
  share: `<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/><polyline points="16 6 12 2 8 6"/><line x1="12" y1="2" x2="12" y2="15"/></svg>`,
};

function todayKey() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

function addDays(key, delta) {
  const [y, m, d] = key.split("-").map(Number);
  const date = new Date(y, m - 1, d);
  date.setDate(date.getDate() + delta);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function formatLongDate(date = new Date()) {
  return date.toLocaleDateString("de-DE", { weekday: "long", day: "numeric", month: "long" });
}

function formatActivityWhen(isoDate) {
  const today = todayKey();
  if (isoDate === today) return "Heute";
  if (isoDate === addDays(today, -1)) return "Gestern";
  const [y, m, d] = isoDate.split("-").map(Number);
  const then = new Date(y, m - 1, d);
  const diff = Math.round((new Date(today) - then) / 86400000);
  if (diff > 1 && diff < 7) return `Vor ${diff} Tagen`;
  return then.toLocaleDateString("de-DE", { day: "numeric", month: "short" });
}

function defaultState() {
  const progress = {};
  for (const lang of LANGUAGES) progress[lang.id] = {};
  return {
    name: "Anna",
    isPro: false,
    selectedLang: "es",
    selectedPlan: "yearly",
    chartRange: 30,
    streak: 0,
    lastStudyDate: null,
    todayDate: todayKey(),
    todayCount: 0,
    todayCorrect: 0,
    todayWrong: 0,
    totalCorrect: 0,
    totalAttempts: 0,
    totalSeconds: 0,
    progress,
    daily: {},
    activity: [],
    view: "home",
  };
}

function loadState() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return defaultState();
    const saved = JSON.parse(raw);
    const base = defaultState();
    const merged = { ...base, ...saved, progress: { ...base.progress, ...(saved.progress || {}) } };
    if (merged.todayDate !== todayKey()) {
      merged.todayDate = todayKey();
      merged.todayCount = 0;
      merged.todayCorrect = 0;
      merged.todayWrong = 0;
    }
    merged.view = "home";
    return merged;
  } catch {
    return defaultState();
  }
}

function persist() {
  const { view, ...rest } = state;
  localStorage.setItem(STORAGE_KEY, JSON.stringify(rest));
}

let state = loadState();
let session = null;
let clockTimer = null;

function langById(id) {
  return LANGUAGES.find((l) => l.id === id);
}

function wordsFor(langId) {
  return WORDS[langId] || [];
}

function isUnlocked(lang) {
  return lang.free || state.isPro;
}

function learnedCount(langId) {
  const map = state.progress[langId] || {};
  return Object.values(map).filter((box) => box >= 2).length;
}

function totalLearned() {
  return LANGUAGES.reduce((sum, lang) => sum + learnedCount(lang.id), 0);
}

function accuracy() {
  if (!state.totalAttempts) return 0;
  return Math.round((state.totalCorrect / state.totalAttempts) * 100);
}

function unlockedLangCount() {
  return LANGUAGES.filter(isUnlocked).length;
}

function touchStreak() {
  const today = todayKey();
  if (state.lastStudyDate === today) return;
  if (state.lastStudyDate === addDays(today, -1)) state.streak += 1;
  else state.streak = 1;
  state.lastStudyDate = today;
}

function navigate(view, direction = "forward") {
  const update = () => {
    state.view = view;
    if (view !== "learn") session = null;
    render();
    history.replaceState(null, "", `#${view}`);
  };
  if (typeof document.startViewTransition === "function") {
    document.startViewTransition({ update, types: [direction] });
  } else {
    update();
  }
}

function directionFor(next) {
  const from = TAB_ORDER.indexOf(state.view);
  const to = TAB_ORDER.indexOf(next);
  if (from === -1 || to === -1) return next === "home" ? "backward" : "forward";
  return to >= from ? "forward" : "backward";
}

function startSession(langId) {
  const lang = langById(langId);
  if (!lang) return;
  if (!isUnlocked(lang)) {
    navigate("pro", "forward");
    return;
  }
  const words = [...wordsFor(langId)];
  const progress = state.progress[langId] || {};
  words.sort((a, b) => (progress[a.id] || 0) - (progress[b.id] || 0));
  session = {
    langId,
    startedAt: Date.now(),
    queue: words.map((w) => w.id),
    index: 0,
    results: {},
    flipped: false,
  };
  state.selectedLang = langId;
  navigate("learn", "forward");
}

function currentWord() {
  if (!session) return null;
  const id = session.queue[session.index];
  return wordsFor(session.langId).find((w) => w.id === id) || null;
}

function grade(knew) {
  if (!session) return;
  const word = currentWord();
  if (!word) return;
  const firstPass = session.results[word.id] === undefined;
  state.progress[langId] ||= {};
  const prev = state.progress[langId][word.id] || 0;
  state.progress[langId][word.id] = knew ? Math.min(5, Math.max(2, prev + 1)) : 1;
  session.results[word.id] = knew ? "know" : "again";
  state.todayCount += 1;
  state.daily[todayKey()] = (state.daily[todayKey()] || 0) + 1;
  state.totalAttempts += 1;
  if (knew) {
    state.todayCorrect += 1;
    state.totalCorrect += 1;
  } else {
    state.todayWrong += 1;
  }
  touchStreak();

  if (!knew && firstPass) session.queue.push(word.id);
  session.index += 1;
  session.flipped = false;

  if (session.index >= session.queue.length) {
    finishSession();
    return;
  }
  persist();
  render();
}

function finishSession() {
  const lang = langById(session.langId);
  const results = Object.values(session.results);
  const known = results.filter((r) => r === "know").length;
  const total = results.length;
  const seconds = Math.round((Date.now() - session.startedAt) / 1000);
  state.totalSeconds += seconds;
  state.activity.unshift({
    langId: session.langId,
    title: `${lang.name} · ${lang.pack}`,
    words: total,
    accuracy: total ? Math.round((known / total) * 100) : 0,
    date: todayKey(),
  });
  state.activity = state.activity.slice(0, 12);
  persist();
  session = { ...session, done: true, known, total };
  render();
}

function speakWord(word, langId) {
  if (!("speechSynthesis" in window)) return;
  window.speechSynthesis.cancel();
  const utter = new SpeechSynthesisUtterance(word);
  utter.lang = langById(langId)?.speech || "de-DE";
  utter.rate = 0.92;
  window.speechSynthesis.speak(utter);
}

function clock() {
  const el = document.getElementById("clock");
  if (!el) return;
  el.textContent = new Date().toLocaleTimeString("de-DE", { hour: "2-digit", minute: "2-digit" });
}

function tabbar(active) {
  if (active === "learn" && session && !session.done) return "";
  const items = [
    ["home", "Home", ICONS.home],
    ["learn", "Lernen", ICONS.learn],
    ["stats", "Statistik", ICONS.stats],
    ["pro", "Pro", ICONS.pro],
  ];
  return `<nav class="tabbar" aria-label="Hauptnavigation">${items
    .map(
      ([id, label, icon]) =>
        `<button type="button" class="tab ${active === id ? "active" : ""}" data-nav="${id}" aria-current="${active === id ? "page" : "false"}">${icon}${label}</button>`
    )
    .join("")}</nav>`;
}

function langCards(limit) {
  const list = limit ? LANGUAGES.slice(0, limit) : LANGUAGES;
  return list
    .map((lang) => {
      const locked = !isUnlocked(lang);
      const count = learnedCount(lang.id);
      return `<button type="button" class="lang-card ${state.selectedLang === lang.id ? "active" : ""} ${locked ? "locked" : ""}" data-lang="${lang.id}">
        <span class="lang-flag">${lang.flag}</span>
        <span class="lang-info">
          <span class="lang-name">${lang.name}${locked ? " · Pro" : ""}</span>
          <span class="lang-level">${count} Wörter · ${lang.level}</span>
        </span>
        <span class="lang-arrow">${locked ? ICONS.lock : ICONS.chevron}</span>
      </button>`;
    })
    .join("");
}

function viewHome() {
  const today = Math.min(state.todayCount, DAILY_GOAL);
  const remaining = Math.max(0, DAILY_GOAL - state.todayCount);
  const offset = RING * (1 - today / DAILY_GOAL);
  const acc = accuracy();
  const lang = langById(state.selectedLang);
  return `
    <div class="header">
      <div>
        <p class="greeting">${formatLongDate()}</p>
        <h1>Moin, ${escapeHtml(state.name)}!</h1>
      </div>
      <button type="button" class="icon-btn" data-open="profile" aria-label="Profil">${ICONS.profile}</button>
    </div>
    <section class="pad" style="margin-bottom:12px">
      <div class="row-between">
        <div class="streak-badge">${ICONS.flame} ${state.streak} ${state.streak === 1 ? "Tag" : "Tage"} streak!</div>
        <span class="pill">Niveau ${lang?.level || "A1"}</span>
      </div>
    </section>
    <section class="pad" style="margin-bottom:16px">
      <div class="card accent" style="padding:24px;text-align:center">
        <p class="meta" style="margin:0 0 12px;color:rgba(255,255,255,.72)">HEUTIGER FORTSCHRITT</p>
        <div class="progress-ring" style="margin:0 auto 8px">
          <svg width="120" height="120" viewBox="0 0 120 120" aria-hidden="true">
            <circle class="ring-bg" cx="60" cy="60" r="52" stroke-width="10"/>
            <circle class="ring-fill" cx="60" cy="60" r="52" stroke-width="10" stroke-dasharray="${RING.toFixed(2)}" stroke-dashoffset="${offset.toFixed(2)}"/>
          </svg>
          <span class="ring-label">${today}</span>
          <span class="ring-sub">/ ${DAILY_GOAL} Wörter</span>
        </div>
        <p style="margin:8px 0 16px;font-size:14px;color:rgba(255,255,255,.85)">${
          remaining ? `Noch ${remaining} Wörter bis zum Ziel!` : "Tagesziel erreicht. Stark!"
        }</p>
        <button type="button" class="btn-primary" data-start="${state.selectedLang}" style="box-shadow:none">${
          state.todayCount ? "Weiterlernen" : "Heute lernen"
        }</button>
      </div>
    </section>
    <section class="pad" style="margin-bottom:16px">
      <div class="grid-3">
        <div class="card mini-stat"><div class="stat-value" style="color:var(--accent)">${totalLearned()}</div><div class="stat-label">Gelernt</div></div>
        <div class="card mini-stat"><div class="stat-value" style="color:var(--accent-2)">${acc}%</div><div class="stat-label">Genauigkeit</div></div>
        <div class="card mini-stat"><div class="stat-value" style="color:var(--success)">${unlockedLangCount()}</div><div class="stat-label">Sprachen</div></div>
      </div>
    </section>
    <section class="pad">
      <div class="row-between" style="margin-bottom:12px">
        <p class="h3">Deine Sprachen</p>
        <button type="button" class="btn-link" data-nav="languages">Alle</button>
      </div>
      <div class="stack" style="gap:10px">${langCards(3)}</div>
    </section>`;
}

function viewLearn() {
  if (!session) {
    const lang = langById(state.selectedLang);
    return `
      <div class="header">
        <div>
          <p class="greeting">${lang.name} · ${lang.pack}</p>
          <h1>Karteikarten</h1>
        </div>
      </div>
      <section class="pad" style="padding-top:24px;text-align:center">
        <p class="h2" style="margin-bottom:8px">Bereit?</p>
        <p class="meta" style="margin-bottom:20px">12 Karten aus ${lang.name} — tippe, drehe, bewerte.</p>
        <button type="button" class="btn-primary" data-start="${lang.id}">Lektion starten</button>
      </section>`;
  }

  if (session.done) {
    return `
      <div class="header">
        <div>
          <p class="greeting">Geschafft</p>
          <h1>Lektion fertig</h1>
        </div>
        <button type="button" class="icon-btn" data-nav="home" aria-label="Schließen">${ICONS.close}</button>
      </div>
      <section class="pad">
        <div class="card accent gradient session-done">
          <p class="meta" style="color:rgba(255,255,255,.72)">GEWUSST</p>
          <div class="big">${session.known}/${session.total}</div>
          <p style="margin:0;color:rgba(255,255,255,.85)">${
            session.total ? Math.round((session.known / session.total) * 100) : 0
          }% in dieser Runde</p>
        </div>
        <div class="stack" style="margin-top:16px">
          <button type="button" class="btn-primary" data-start="${session.langId}">Nochmal üben</button>
          <button type="button" class="btn-link" data-nav="home">Zurück zur Übersicht</button>
        </div>
      </section>`;
  }

  const lang = langById(session.langId);
  const word = currentWord();
  const total = session.queue.length;
  const current = session.index + 1;
  const pct = Math.round((session.index / total) * 100);
  const unique = [...new Set(session.queue)];
  const dots = unique
    .map((id, i) => {
      const result = session.results[id];
      const firstIndex = session.queue.indexOf(id);
      let cls = "dot";
      if (firstIndex === session.index) cls += " current";
      else if (result === "know") cls += " done";
      else if (result === "again") cls += " wrong";
      return `<span class="${cls}"></span>`;
    })
    .join("");

  return `
    <div class="header">
      <div>
        <p class="greeting">${lang.name} · ${lang.pack}</p>
        <h1>Karteikarten</h1>
      </div>
      <button type="button" class="icon-btn" data-nav="home" aria-label="Beenden">${ICONS.close}</button>
    </div>
    <section class="pad" style="margin-bottom:16px">
      <div class="row-between" style="margin-bottom:8px">
        <span class="meta">Fortschritt</span>
        <span class="meta num">${current} / ${total}</span>
      </div>
      <div class="progress"><span style="width:${pct}%"></span></div>
    </section>
    <section class="pad" style="margin-bottom:8px">
      <div class="flashcard-container">
        <div class="flashcard ${session.flipped ? "flipped" : ""}" id="flashcard" role="button" tabindex="0" aria-label="Karte umdrehen">
          <div class="flashcard-face flashcard-front">
            <button type="button" class="speak-btn" data-speak="${escapeHtml(word.word)}" aria-label="Aussprache">${ICONS.speak}</button>
            <span class="pill" style="margin-bottom:16px">${word.pos}</span>
            <p class="flashcard-word">${escapeHtml(word.word)}</p>
            <p class="flashcard-phonetic">${escapeHtml(word.phonetic)}</p>
            <p class="flashcard-example">„${escapeHtml(word.example)}“</p>
            <span class="flashcard-tap-hint">Tippen zum Umdrehen</span>
          </div>
          <div class="flashcard-face flashcard-back">
            <span class="pill" style="margin-bottom:16px;background:rgba(255,255,255,.2);color:#fff">Übersetzung</span>
            <p class="flashcard-word">${escapeHtml(word.translation)}</p>
            <p class="flashcard-phonetic">${word.gender ? `${word.pos} · ${word.gender}` : word.pos}</p>
            <p class="flashcard-example">„${escapeHtml(word.exampleDe)}“</p>
            <span class="flashcard-tap-hint">Tippen zum Umdrehen</span>
          </div>
        </div>
      </div>
    </section>
    <div class="action-row">
      <button type="button" class="action-btn dont-know" data-grade="0" aria-label="Nochmal lernen">${ICONS.x}<span class="btn-sub">Nochmal</span></button>
      <button type="button" class="action-btn know" data-grade="1" aria-label="Gewusst">${ICONS.check}<span class="btn-sub">Gewusst</span></button>
    </div>
    <div class="progress-dots">${dots}</div>
    <div class="word-counter"><span class="num">${current} von ${total} Wörtern</span></div>`;
}

function dailySeries(days) {
  const out = [];
  for (let i = days - 1; i >= 0; i--) {
    const key = addDays(todayKey(), -i);
    out.push({ key, value: state.daily[key] || 0 });
  }
  return out;
}

function chartSvg(days) {
  const series = dailySeries(days);
  const max = Math.max(10, ...series.map((p) => p.value));
  const w = 320;
  const h = 140;
  const left = 28;
  const right = 8;
  const top = 18;
  const bottom = 28;
  const innerW = w - left - right;
  const innerH = h - top - bottom;
  const step = series.length === 1 ? 0 : innerW / (series.length - 1);
  const pts = series.map((p, i) => {
    const x = left + i * step;
    const y = top + innerH - (p.value / max) * innerH;
    return { x, y, ...p };
  });
  const line = pts.map((p) => `${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(" ");
  const area = `${line} ${pts.at(-1).x.toFixed(1)},${top + innerH} ${pts[0].x.toFixed(1)},${top + innerH}`;
  const sample = days <= 7 ? pts : pts.filter((_, i) => i % Math.ceil(pts.length / 7) === 0 || i === pts.length - 1);
  const labels = sample
    .map((p) => {
      const [, m, d] = p.key.split("-");
      return `<text class="chart-label" x="${p.x - 10}" y="138">${Number(d)}.${days > 7 ? "" : ""}</text>`;
    })
    .join("");
  const dots = (days <= 7 ? pts : [pts[0], pts.at(-1)])
    .map((p, i, arr) => `<circle class="chart-dot" cx="${p.x}" cy="${p.y}" r="${i === arr.length - 1 ? 5 : 4}"/>`)
    .join("");
  return `<svg class="chart-svg" viewBox="0 0 320 140" role="img" aria-label="Wörter pro Tag">
    <defs>
      <linearGradient id="chartGradient" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0%" stop-color="oklch(70% 0.16 55)" stop-opacity="0.2"/>
        <stop offset="100%" stop-color="oklch(70% 0.16 55)" stop-opacity="0"/>
      </linearGradient>
    </defs>
    <line class="chart-grid" x1="0" y1="35" x2="320" y2="35"/>
    <line class="chart-grid" x1="0" y1="70" x2="320" y2="70"/>
    <line class="chart-grid" x1="0" y1="105" x2="320" y2="105"/>
    <text class="chart-label" x="4" y="38">${max}</text>
    <text class="chart-label" x="4" y="73">${Math.round(max / 2)}</text>
    <text class="chart-label" x="4" y="108">0</text>
    <path class="chart-area" d="M${area} Z"/>
    <polyline class="chart-line" points="${line}"/>
    ${dots}
    ${labels}
  </svg>`;
}

function viewStats() {
  const hours = Math.max(0, Math.round((state.totalSeconds / 3600) * 10) / 10);
  const hourLabel = hours < 1 ? `${Math.round(state.totalSeconds / 60)}m` : `${hours}h`;
  const activity = state.activity.length
    ? state.activity
        .slice(0, 3)
        .map((item) => {
          const ok = item.accuracy >= 85;
          const color = ok ? "var(--success)" : "var(--accent)";
          const bg = ok
            ? "color-mix(in oklch, var(--success) 14%, transparent)"
            : "color-mix(in oklch, var(--accent) 12%, transparent)";
          const icon = ok
            ? `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="${color}" stroke-width="2"><path d="M20 6 9 17l-5-5"/></svg>`
            : `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="${color}" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/></svg>`;
          return `<div class="activity-row">
            <div class="activity-icon" style="background:${bg}">${icon}</div>
            <div>
              <div class="activity-title">${escapeHtml(item.title)}</div>
              <div class="activity-sub">${item.words} Wörter · ${item.accuracy}% richtig</div>
            </div>
            <span class="meta">${formatActivityWhen(item.date)}</span>
          </div>`;
        })
        .join("")
    : `<p class="empty">Noch keine Aktivität. Starte deine erste Lektion.</p>`;

  return `
    <div class="header">
      <div>
        <p class="greeting">Deine Fortschritte</p>
        <h1>Statistik</h1>
      </div>
      <button type="button" class="icon-btn" data-share aria-label="Teilen">${ICONS.share}</button>
    </div>
    <section class="pad" style="margin-bottom:16px">
      <div class="grid-2">
        <div class="card stat-card">
          <div class="stat-icon" style="background:var(--accent-soft)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
          </div>
          <div class="stat-value">${totalLearned()}</div>
          <div class="stat-label">Wörter gelernt</div>
        </div>
        <div class="card stat-card">
          <div class="stat-icon" style="background:color-mix(in oklch, var(--accent-2) 18%, transparent)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="oklch(60% 0.14 80)" stroke-width="2"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z"/><path d="M12 6v6l4 2"/></svg>
          </div>
          <div class="stat-value">${hourLabel}</div>
          <div class="stat-label">Lernzeit</div>
        </div>
      </div>
    </section>
    <section class="pad" style="margin-bottom:16px">
      <div class="chart-container">
        <div class="chart-header">
          <p class="h3">Lernkurve</p>
          <div class="chart-tabs">
            ${[7, 30, 90]
              .map(
                (d) =>
                  `<button type="button" class="chart-tab ${state.chartRange === d ? "active" : ""}" data-range="${d}">${d}T</button>`
              )
              .join("")}
          </div>
        </div>
        ${chartSvg(state.chartRange)}
      </div>
    </section>
    <section class="pad" style="margin-bottom:16px">
      <div class="grid-3">
        <div class="card stat-card" style="padding:12px 8px"><div class="stat-value" style="font-size:20px;color:var(--success)">${accuracy()}%</div><div class="stat-label">Genauigkeit</div></div>
        <div class="card stat-card" style="padding:12px 8px"><div class="stat-value" style="font-size:20px;color:var(--accent)">${state.streak}</div><div class="stat-label">Streak</div></div>
        <div class="card stat-card" style="padding:12px 8px"><div class="stat-value" style="font-size:20px;color:var(--accent-2)">${unlockedLangCount()}</div><div class="stat-label">Sprachen</div></div>
      </div>
    </section>
    <section class="pad">
      <p class="h3" style="margin:0 0 8px">Letzte Aktivität</p>
      <div class="card" style="padding:4px 16px">${activity}</div>
    </section>`;
}

function viewPro() {
  const yearly = state.selectedPlan === "yearly";
  return `
    <div class="header">
      <div>
        <p class="greeting">Upgrade</p>
        <h1>WordFlow Pro</h1>
      </div>
      <button type="button" class="icon-btn" data-nav="home" aria-label="Schließen">${ICONS.close}</button>
    </div>
    <section class="pad" style="margin-bottom:16px">
      <div class="card accent gradient" style="padding:28px 24px;text-align:center">
        <p class="meta" style="margin:0 0 8px;color:rgba(255,255,255,.72)">${state.isPro ? "AKTIV" : "LERN OHNE GRENZEN"}</p>
        <h2 class="h2" style="color:#fff">${state.isPro ? "Du lernst mit Pro." : "Freunde dich mit jeder Sprache an."}</h2>
        <p style="margin:6px 0 0;font-size:14px;color:rgba(255,255,255,.8);line-height:1.5">${
          state.isPro ? "Alle Sprachen und Listen sind freigeschaltet." : "Unlimited Zugriff auf alle Funktionen und Sprachen."
        }</p>
      </div>
    </section>
    <section class="pad" style="margin-bottom:16px">
      <p class="h3" style="margin:0 0 8px">Was du bekommst</p>
      <div class="card" style="padding:4px 16px">
        <div class="feature-item"><div class="feature-icon" style="background:var(--accent-soft)"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" stroke-width="2"><path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z"/><path d="M2 12h20"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/></svg></div><div><p class="feature-title">Unlimited Sprachen</p><p class="feature-desc">Lerne alle verfügbaren Sprachen — ohne Einschränkung.</p></div></div>
        <div class="feature-item"><div class="feature-icon" style="background:color-mix(in oklch, var(--success) 12%, transparent)"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--success)" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg></div><div><p class="feature-title">Offline lernen</p><p class="feature-desc">Dein Fortschritt bleibt lokal auf diesem Gerät.</p></div></div>
        <div class="feature-item"><div class="feature-icon" style="background:color-mix(in oklch, var(--accent-2) 16%, transparent)"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="oklch(60% 0.14 80)" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></div><div><p class="feature-title">Alle Listen</p><p class="feature-desc">Japanisch, Italienisch und Englisch dazu.</p></div></div>
        <div class="feature-item"><div class="feature-icon" style="background:color-mix(in oklch, var(--error) 10%, transparent)"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="oklch(62% 0.18 25)" stroke-width="2"><path d="M12 20V10"/><path d="M18 20V4"/><path d="M6 20v-4"/></svg></div><div><p class="feature-title">Detaillierte Statistik</p><p class="feature-desc">Verfolge deinen Fortschritt mit Lernanalysen.</p></div></div>
      </div>
    </section>
    ${
      state.isPro
        ? `<section class="pad"><button type="button" class="btn-primary" data-cancel-pro>Pro beenden (Demo)</button><p class="disclaimer">Nur in dieser Demo — kein echtes Abo.</p></section>`
        : `<section class="pad" style="margin-bottom:8px">
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
          <button type="button" class="pricing-card ${yearly ? "" : "selected"}" data-plan="monthly">
            <div class="plan-name">Monatlich</div>
            <div class="plan-price">4,99<span style="font-size:16px;font-weight:600">€</span></div>
            <div class="plan-period">pro Monat</div>
          </button>
          <button type="button" class="pricing-card ${yearly ? "selected" : ""}" data-plan="yearly">
            <div class="price-badge">BELIEBT</div>
            <div class="plan-name">Jährlich</div>
            <div class="plan-price">29,99<span style="font-size:16px;font-weight:600">€</span></div>
            <div class="plan-period">pro Jahr</div>
            <div class="plan-save">Spare 50%</div>
          </button>
        </div>
      </section>
      <section class="pad" style="padding-top:16px">
        <button type="button" class="btn-primary" data-activate-pro>7 Tage kostenlos testen</button>
        <p class="disclaimer">Danach ${yearly ? "29,99€/Jahr" : "4,99€/Monat"}. Demo ohne Zahlung. Jederzeit kündbar.</p>
      </section>`
    }`;
}

function viewLanguages() {
  return `
    <div class="header">
      <div>
        <p class="greeting">Katalog</p>
        <h1>Sprachen</h1>
      </div>
      <button type="button" class="icon-btn" data-nav="home" aria-label="Zurück">${ICONS.close}</button>
    </div>
    <section class="pad">
      <div class="stack" style="gap:10px">${langCards()}</div>
      ${state.isPro ? "" : `<p class="disclaimer" style="margin-top:16px">Japanisch, Italienisch und Englisch sind Teil von WordFlow Pro.</p>`}
    </section>`;
}

function escapeHtml(str) {
  return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

function render() {
  const root = document.getElementById("view");
  const views = { home: viewHome, learn: viewLearn, stats: viewStats, pro: viewPro, languages: viewLanguages };
  const renderView = views[state.view] || viewHome;
  root.innerHTML = renderView();
  document.getElementById("tabs").innerHTML = tabbar(state.view === "languages" ? "home" : state.view);
  document.title = `WordFlow — ${state.view === "pro" ? "Pro" : state.view === "stats" ? "Statistik" : state.view === "learn" ? "Lernen" : "Home"}`;
  clock();
  bind();
}

function bind() {
  document.querySelectorAll("[data-nav]").forEach((el) => {
    el.addEventListener("click", () => {
      const next = el.getAttribute("data-nav");
      if (next === "learn") startSession(state.selectedLang);
      else navigate(next, directionFor(next));
    });
  });
  document.querySelectorAll("[data-lang]").forEach((el) => {
    el.addEventListener("click", () => startSession(el.getAttribute("data-lang")));
  });
  document.querySelectorAll("[data-start]").forEach((el) => {
    el.addEventListener("click", () => startSession(el.getAttribute("data-start")));
  });
  document.querySelectorAll("[data-grade]").forEach((el) => {
    el.addEventListener("click", (event) => {
      event.stopPropagation();
      grade(el.getAttribute("data-grade") === "1");
    });
  });
  document.querySelectorAll("[data-range]").forEach((el) => {
    el.addEventListener("click", () => {
      state.chartRange = Number(el.getAttribute("data-range"));
      persist();
      render();
    });
  });
  document.querySelectorAll("[data-plan]").forEach((el) => {
    el.addEventListener("click", () => {
      state.selectedPlan = el.getAttribute("data-plan");
      persist();
      render();
    });
  });
  document.querySelector("[data-activate-pro]")?.addEventListener("click", () => {
    state.isPro = true;
    persist();
    render();
  });
  document.querySelector("[data-cancel-pro]")?.addEventListener("click", () => {
    state.isPro = false;
    if (!langById(state.selectedLang).free) state.selectedLang = "es";
    persist();
    render();
  });
  document.querySelector("[data-open='profile']")?.addEventListener("click", () => {
    document.getElementById("name-input").value = state.name;
    document.getElementById("profile-dialog").showModal();
  });
  document.querySelector("[data-share]")?.addEventListener("click", async () => {
    const text = `WordFlow: ${totalLearned()} Wörter gelernt, ${state.streak} Tage Streak.`;
    try {
      if (navigator.share) await navigator.share({ title: "WordFlow", text });
      else await navigator.clipboard.writeText(text);
    } catch {
      /* user cancelled */
    }
  });
  const card = document.getElementById("flashcard");
  if (card) {
    card.addEventListener("click", (event) => {
      if (event.target.closest("[data-speak]")) return;
      session.flipped = !session.flipped;
      card.classList.toggle("flipped", session.flipped);
    });
  }
  document.querySelector("[data-speak]")?.addEventListener("click", (event) => {
    event.stopPropagation();
    const word = currentWord();
    if (word) speakWord(word.word, session.langId);
  });
}

document.getElementById("profile-form").addEventListener("submit", (event) => {
  event.preventDefault();
  const name = document.getElementById("name-input").value.trim() || "Anna";
  state.name = name.slice(0, 24);
  persist();
  document.getElementById("profile-dialog").close();
  render();
});

document.getElementById("profile-cancel").addEventListener("click", () => {
  document.getElementById("profile-dialog").close();
});

document.addEventListener("keydown", (event) => {
  if (state.view !== "learn" || !session || session.done) return;
  if (event.target instanceof HTMLInputElement) return;
  if (event.key === " " || event.key === "Enter") {
    event.preventDefault();
    session.flipped = !session.flipped;
    document.getElementById("flashcard")?.classList.toggle("flipped", session.flipped);
  }
  if (event.key === "ArrowLeft") grade(false);
  if (event.key === "ArrowRight") grade(true);
});

window.addEventListener("hashchange", () => {
  const view = location.hash.replace("#", "") || "home";
  if (["home", "learn", "stats", "pro", "languages"].includes(view) && view !== state.view) {
    if (view === "learn") startSession(state.selectedLang);
    else navigate(view, directionFor(view));
  }
});

clockTimer = setInterval(clock, 10000);
const initial = location.hash.replace("#", "");
if (["home", "stats", "pro", "languages"].includes(initial)) state.view = initial;
render();

function isNativeApp() {
  return Boolean(window.Capacitor?.isNativePlatform?.());
}

if (isNativeApp()) {
  document.documentElement.classList.add("is-native");
  const plugins = window.Capacitor.Plugins || {};
  plugins.StatusBar?.setStyle?.({ style: "LIGHT" });
  plugins.StatusBar?.setBackgroundColor?.({ color: "#f7f3ea" });
  plugins.App?.addListener?.("backButton", () => {
    const dialog = document.getElementById("profile-dialog");
    if (dialog?.open) {
      dialog.close();
      return;
    }
    if (state.view !== "home") {
      navigate("home", "backward");
      return;
    }
    plugins.App.exitApp();
  });
}
