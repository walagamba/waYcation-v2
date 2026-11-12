---
name: Monochrome Telemetry
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#3a3939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1c1b1b'
  surface-container: '#201f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353534'
  on-surface: '#e5e2e1'
  on-surface-variant: '#e8bcb7'
  inverse-surface: '#e5e2e1'
  inverse-on-surface: '#313030'
  outline: '#af8783'
  outline-variant: '#5e3f3b'
  surface-tint: '#ffb4ab'
  primary: '#ffb4ab'
  on-primary: '#690005'
  primary-container: '#ff5449'
  on-primary-container: '#5c0004'
  inverse-primary: '#c00011'
  secondary: '#c6c6c7'
  on-secondary: '#2f3131'
  secondary-container: '#454747'
  on-secondary-container: '#b4b5b5'
  tertiary: '#c6c5cf'
  on-tertiary: '#2f3038'
  tertiary-container: '#909099'
  on-tertiary-container: '#282931'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffdad6'
  primary-fixed-dim: '#ffb4ab'
  on-primary-fixed: '#410002'
  on-primary-fixed-variant: '#93000a'
  secondary-fixed: '#e2e2e2'
  secondary-fixed-dim: '#c6c6c7'
  on-secondary-fixed: '#1a1c1c'
  on-secondary-fixed-variant: '#454747'
  tertiary-fixed: '#e3e1ec'
  tertiary-fixed-dim: '#c6c5cf'
  on-tertiary-fixed: '#1a1b22'
  on-tertiary-fixed-variant: '#46464e'
  background: '#131313'
  on-background: '#e5e2e1'
  surface-variant: '#353534'
  pure-black: '#000000'
  surface-translucent: rgba(10, 10, 10, 0.75)
  border-structural: '#27272a'
  border-subtle: '#18181b'
  border-dotted: '#3f3f46'
  signal-red: '#ff2a2a'
  signal-red-glow: rgba(255, 42, 42, 0.2)
  telemetry-gray: '#a1a1aa'
  stark-white: '#ffffff'
typography:
  headline-xl:
    fontFamily: Space Grotesk
    fontSize: 56px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.04em
  headline-xl-mobile:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 40px
    fontWeight: '600'
    lineHeight: 48px
    letterSpacing: -0.03em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '500'
    lineHeight: 28px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Space Mono
    fontSize: 13px
    fontWeight: '700'
    lineHeight: 18px
    letterSpacing: 0.06em
  label-md:
    fontFamily: Space Mono
    fontSize: 11px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0.08em
  label-sm:
    fontFamily: Space Mono
    fontSize: 10px
    fontWeight: '400'
    lineHeight: 14px
    letterSpacing: 0.1em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-md: 1.5rem
  gutter-lg: 2rem
  margin: 1rem
  margin-md: 2rem
  margin-lg: 4rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
  space-2xl: 4rem
---

## Brand & Style

This design system reimagines collaborative travel and hospitality discovery through an austere, technical lens inspired by Nothing OS and Teenage Engineering. It strips away conventional travel photography cliches, saturated resort gradients, and decorative UI bloat in favor of tactile minimalism, wireframe clarity, and stark contrast. 

The aesthetic is functional, transparent, and pocket-sized: engineered like a precision field instrument. The interface treats travel coordination as an exact science—group votes are telemetry streams, hotel listings are structured technical datasheets, and discovery swipe states mimic mechanical toggle switches. 

The emotional response is focused, cerebral, and unencumbered. High-density negative space, calibrated monospaced metrics, crisp physical outlines, and selective phosphor-red accents replace traditional emotional hand-holding with immediate, utilitarian confidence.

## Colors

The palette operates on absolute chromatic economy. The interface is predominantly dark, constructed from deep obsidian layers (`#000000` base, `#0a0a0a` modular containers) and partitioned by precise charcoal borders (`#18181b`, `#27272a`). 

Stark white (`#ffffff`) is strictly reserved for high-priority typography, active icon glyphs, and unselected structural pins. Telemetry gray (`#a1a1aa`) and zinc accents (`#71717a`) house secondary data tags, metadata readouts, and inactive states to avoid retina fatigue.

Color is never decorative; it is a live signal. The signature high-intensity red (`#ff2a2a`) is used exclusively for primary critical paths, state verifications (e.g., active locks, selected voting pathways, recording snapshots), destructive alerts, and pinpoint notification pips. Transparent fills (`rgba(10, 10, 10, 0.75)`) combined with backdrop filtration supply physical depth without introducing extraneous hue noise.

## Typography

The typographic hierarchy establishes clear structural tension between human-readable editorial clarity and mechanical readouts:

- **Display & Headings (Space Grotesk):** Engineered, geometric sans-serif delivering structural weight. Headings are tightly tracked (`-0.02em` to `-0.04em`) to maintain dense, poster-like impact across fluid viewports.
- **Body Text (Inter):** Neutral, high-legibility workhorse used for room inventories, accommodation policies, and group itinerary sequences. Maintains calm neutrality against mechanical UI borders.
- **Data, Status & Codes (Space Mono):** Used for database transaction markers, pricing figures, dates, coordinate parameters, invite tokens, and dotted pills. Styled in small-scale tabular configurations, frequently set in uppercase with tracked out spacing (`0.06em` to `0.1em`) to mimic dot-matrix and CNC engraving aesthetics.

## Layout & Spacing

Layouts adhere to an explicit, modular bento-grid framework built on an 8px architectural cadence. The content never floats unbound; it sits inside bordered coordinate panels.

- **Grid Architecture:** 
  - Desktop (≥1024px): 12-column bento system with `2rem` gutters and structured `4rem` outer margins. Content groups lock to 3, 4, 6, or 12-column spans. Sticky inspector sidebars occupy a strict 4-column module alongside an 8-column main dossier.
  - Tablet (768px - 1023px): 6-column grid with `1.5rem` gutters. Sidebars collapse to inline panels.
  - Mobile (≤767px): 2-column or single-column stack with `1rem` gutters and `1rem` outer canvas padding. Component heights snap strictly to 48px or 56px intervals for thumb ergonomics.
- **Swipe Sandbox:** Gamified voting workflows lock within a strict `420px` maximum viewport container, centered horizontally on desktop, creating a dedicated pocket-console viewport.

## Elevation & Depth

This system avoids realistic drop shadows and blurred volumetric lighting. Depth is tactile, physical, and planar:

- **Structural Outlines:** Hierarchy is established primarily via crisp 1px borders. Inactive elements use `#18181b`, standard panels use `#27272a`, and focused or selected elements escalate directly to `#ffffff` or `#ff2a2a`.
- **Translucent Layering:** Floating headers, quick-action navigation rails, and modal shields use `rgba(10, 10, 10, 0.75)` with an ultra-fine `backdrop-filter: blur(12px)`. Stacked elements reveal the underlying grid lines.
- **Tactile Insets:** Recessed controls (e.g., input fields, inactive voting zones) employ monochromatic planar fills (`#050505`) with interior hairline strokes instead of outer shadows.
- **Signal Ambient Aura:** High-criticality nodes (such as the primary active lock indicator or swipe confirmation) project a localized, zero-spread hard blur using `rgba(255, 42, 42, 0.2)`.

## Shapes

The geometric silhouette balances industrial boxiness with ergonomic comfort. 

- **Containers & Panels:** Rely on soft, industrial radius tokens (`roundedness: 1`, 0.25rem / 4px base, expanding to 0.5rem / 8px on master bento cells) to evoke machined enclosures.
- **Pill Badges & Control Triggers:** Reserved status indicators, physical step-counters, and system tags use fully rounded pills (`border-radius: 9999px`), frequently bounded by dotted hairline strokes (`border: 1px dotted #3f3f46`).
- **Interactive Cutouts:** Key interactive triggers incorporate rectangular notches, crosshairs, or dotted separators that reinforce the aesthetic of hardware modules and audio synthesizer interfaces.

## Components

### Buttons
- **Primary Action:** Solid stark white fill (`#ffffff`), pitch-black Space Grotesk text (`#000000`), subtle 4px rounded radius, zero drop shadow. On hover, inverts to `#ff2a2a` fill with white text.
- **Secondary / Wireframe:** Transparent fill, 1px solid border (`#27272a`), stark white text. On hover, border shifts to `#ffffff` with subtle mechanical haptic highlight.
- **Critical / Destructive:** Transparent fill with 1px solid `#ff2a2a` border and `#ff2a2a` Space Mono text. Hover yields a 10% red wash.
- **Voting Consoles:** Tactile circular triggers (Pass: 64px, Like: 80px) built with transparent cores, 1px perimeter outlines, monospaced icon glyphs, and high-contrast feedback states.

### Chips & Badges
- Encased in `rounded-full` silhouettes with a 1px dotted outline (`#3f3f46`).
- Uses `label-sm` Space Mono typography.
- Status dots utilize a live 6px squared or circular pip (White for neutral telemetry, `#ff2a2a` for live or atomic states).

### Input Fields
- Deep obsidian fill (`#050505`), enclosed in a 1px outline (`#27272a`).
- Placeholder text set in zinc (`#71717a`). Active focus snaps immediately to a 1px solid stark white border with an optional monospace coordinates caption at top-right.

### Cards & Bento Modules
- Segmented by hairline borders with no surface gradients.
- Images inside cards are rendered in stark monochrome or high-contrast duotone by default, shifting to full natural spectrum only on active hover or selection focus.
- Metadata sections are demarcated by horizontal dotted dividers (`border-top: 1px dotted #27272a`).

### Lists & Tables
- Borderless rows separated strictly by 1px horizontal lines (`#18181b`).
- Numeric values, timestamps, and currency markers align right in tabular Space Mono figures.
- Selected rows highlight with a solid red indicator bar along the left edge (2px width).