# WordFlow Brand Spec

## Summary
Friendly, warm vocabulary trainer with orange/yellow accents on white. Rounded shapes, gamification micro-illustrations.

## Tokens (OKLCh)
- `--bg`: oklch(98.5% 0.008 90) — warm off-white
- `--surface`: oklch(100% 0 0) — pure white cards
- `--fg`: oklch(20% 0.01 60) — warm near-black
- `--muted`: oklch(55% 0.015 70) — warm gray
- `--border`: oklch(93% 0.01 85) — warm light border
- `--accent`: oklch(70% 0.16 55) — warm orange
- `--accent-2`: oklch(82% 0.14 85) — golden yellow
- `--success`: oklch(65% 0.15 145) — green for streaks
- `--accent-soft`: color-mix(in oklch, var(--accent) 12%, transparent)
- `--fg-soft`: color-mix(in oklch, var(--fg) 6%, transparent)

## Fonts
- Display: 'Nunito', 'Rounded Mplus 1c', system rounded
- Body: -apple-system, BlinkMacSystemFont, 'SF Pro Text', system-ui, sans-serif
- Mono: ui-monospace, 'SF Mono', Menlo, monospace

## Rules
1. Rounded everything: cards ≥ 18px radius, pills 999px
2. Warm gradient accents only — never cold or neon
3. Micro-illustrations use accent colors, max 2 per screen
4. Progress indicators use warm orange fills
5. Gamification elements (streaks, badges) get golden yellow accents
