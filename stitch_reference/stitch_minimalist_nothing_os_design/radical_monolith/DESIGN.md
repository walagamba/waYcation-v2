---
name: Radical Monolith
colors:
  surface: '#f9f9fa'
  surface-dim: '#dadadb'
  surface-bright: '#f9f9fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f4'
  surface-container: '#eeeeef'
  surface-container-high: '#e8e8e9'
  surface-container-highest: '#e2e2e3'
  on-surface: '#1a1c1d'
  on-surface-variant: '#47464a'
  inverse-surface: '#2f3132'
  inverse-on-surface: '#f0f1f2'
  outline: '#78767b'
  outline-variant: '#c8c5ca'
  surface-tint: '#5f5e60'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#1c1b1d'
  on-primary-container: '#858386'
  inverse-primary: '#c8c6c8'
  secondary: '#bb0010'
  on-secondary: '#ffffff'
  secondary-container: '#e8121c'
  on-secondary-container: '#fffbff'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#1f1a1a'
  on-tertiary-container: '#8a8282'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e5e1e4'
  primary-fixed-dim: '#c8c6c8'
  on-primary-fixed: '#1c1b1d'
  on-primary-fixed-variant: '#474649'
  secondary-fixed: '#ffdad6'
  secondary-fixed-dim: '#ffb4ab'
  on-secondary-fixed: '#410002'
  on-secondary-fixed-variant: '#93000a'
  tertiary-fixed: '#ebe0df'
  tertiary-fixed-dim: '#cec4c4'
  on-tertiary-fixed: '#1f1a1a'
  on-tertiary-fixed-variant: '#4c4545'
  background: '#f9f9fa'
  on-background: '#1a1c1d'
  surface-variant: '#e2e2e3'
typography:
  display:
    fontFamily: Space Grotesk
    fontSize: 56px
    fontWeight: '600'
    lineHeight: 60px
    letterSpacing: -0.04em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.03em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '500'
    lineHeight: 28px
    letterSpacing: -0.02em
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 18px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Geist
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: -0.01em
  body-md:
    fontFamily: Geist
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  body-sm:
    fontFamily: Geist
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
    letterSpacing: 0em
  label-mono:
    fontFamily: Space Grotesk
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.06em
  label-caps:
    fontFamily: Space Grotesk
    fontSize: 10px
    fontWeight: '600'
    lineHeight: 12px
    letterSpacing: 0.1em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-md: 1.5rem
  gutter-lg: 2rem
  margin: 1rem
  margin-md: 2rem
  margin-lg: 3.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
---

## Brand & Style

This design system embodies the marriage of industrial hardware precision and radical reduction. Inspired by functionalist electronics and tactile industrial design, it prioritizes pure utility, balanced tension, and intentional negative space. The aesthetic rejects decorative gradients, blurred shadows, and unnecessary digital artifacts in favor of physical tactility translated to digital surfaces.

The emotional tone is objective, quiet, deliberate, and premium. Content is treated as physical artifacts set upon calibrated planes. The target user appreciates mechanical clarity, Swiss modernism, and the Dieter Rams philosophy of "less, but better."

## Colors

The palette is stark, high-contrast, and strictly restrained:

- **Base Surfaces**: Pure chalk (`#f8f9fa`) functions as the global canvas, with optical white (`#ffffff`) reserved for elevated interactive modules and bento cards.
- **Structural Neutral**: Calibrated zinc (`#f4f4f5` to `#e4e4e7`) provides structural framing, component fills, and hairline boundary division.
- **Ink / Typography**: Absolute pitch black (`#09090b`) delivers uncompromising legibility, paired with secondary muted ink (`#71717a`) for metadata and system labels.
- **Hardware Accent**: Precision red (`#ff2a2a`) is used strictly as a singular status beacon, mechanical LED analog, or deliberate active-state indicator. It must never coat large backgrounds or fill broad surfaces.

## Typography

The typography pairs mechanical geometric character with high-efficiency neutral prose.

- **Headlines & Technical Labels**: Set in `Space Grotesk`. Display and title treatments employ tight tracking and solid weights, echoing silkscreened hardware specifications.
- **Continuous Reading & Data**: Set in `Geist`. Crisp, neutral grotesque proportions guarantee sustained readability across dense layouts without competing with display elements.
- **Micro-Copy**: Labels, tags, and status trackers render in uppercase with positive letter spacing (`0.06em` to `0.1em`) to mimic stamped model designations.

## Layout & Spacing

Layouts adhere to an orchestrated modular bento framework governed by structural rhythm:

- **Desktop (12 Columns)**: Max container width of 1440px with generous section gutters (`2rem`) and outer margins (`3.5rem`). Bento cells align to explicit modular units.
- **Tablet (8 Columns)**: Reflows multi-column modules into consolidated 2-column or 4-column groupings with `1.5rem` gutters.
- **Mobile (4 Columns)**: Bento units stack into vertical single-column modules, retaining a strict `1rem` outer gutter to preserve edge tension.
- **Rhythm**: Internal card padding matches `space-lg` (24px) for prominent content units and `space-md` (16px) for compact widgets. Whitespace is active, deliberate, and uncompromising.

## Elevation & Depth

Depth is articulated through planar tonal shifting and physical boundary hairlines rather than multi-layered ambient drop shadows:

- **Surface Tiers**: Base background uses `#f8f9fa`. Cards, panels, and dialogs step up to `#ffffff`. Interactive overlays or active item states rest on `#f4f4f5`.
- **Hairline Outlines**: Every surface separation relies on 1px solid boundaries (`#e4e4e7`). Hover or active focal points step to `#09090b`.
- **Shadow Absence**: Diffused drop shadows are completely omitted. If an overlay component requires separation from the canvas, it uses a solid 1px `#09090b` border with an optional hard 2px offset key-line shadow (`rgba(9, 9, 11, 0.06)`).

## Shapes

The geometry draws from precision-milled physical products:

- **Cards and Bento Modules**: Configured with balanced radii (`1.5rem` / 24px) to create smooth, tactile enclosures that stand out against the hairline boundaries.
- **Control Elements**: Buttons, inputs, and interactive pills utilize refined squircle profiles or full circular capsules (`9999px`) to denote interactive pressability.
- **Status Beacons**: Always exact, pixel-perfect 6px or 8px circles (`rounded-full`), mirroring physical LED indicators.

## Components

### Buttons
- **Primary**: Deep pitch black (`#09090b`) background, optical white (`#ffffff`) text, 40px height, Space Grotesk medium. Crisp 0.5rem radius. Hover shifts to `#27272a`.
- **Secondary**: Optical white (`#ffffff`) surface with a 1px `#e4e4e7` border and pitch black text. Hover shifts border color to `#09090b`.
- **Accent Indicator**: When a primary action signifies an active connection or recording state, embed an inline 6px `#ff2a2a` LED dot adjacent to the text.

### Bento Cards
- Crisp `#ffffff` container bounded by a 1px `#e4e4e7` hairline border. Internal padding is `1.5rem`.
- Header areas feature micro-labels set in uppercase Space Grotesk (`10px`, tracked). Subtle dividers between content zones utilize 1px `#f4f4f5`.

### Chips & Badges
- Capsule-shaped (`rounded-full`) pill containers with a `#f4f4f5` background and 1px `#e4e4e7` border.
- Active states feature a leading 6px `#ff2a2a` red dot accompanied by pitch black text (`label-mono`).

### Input Fields
- Height 44px, `#ffffff` surface, with a 1px `#e4e4e7` border.
- Focus switches the border directly to 1px `#09090b` with zero glow rings or drop-shadows. Placeholder text sits in `#a1a1aa`.

### Checkboxes & Radios
- Checkboxes: 18x18px squares with a 3px radius, 1px `#e4e4e7` border. Checked state transitions to a solid `#09090b` fill with a sharp white checkmark.
- Radio Buttons: 18x18px concentric circle. Checked state exhibits a solid `#09090b` perimeter holding an internal centered 6px `#ff2a2a` LED dot.

### Lists & Navigation
- Flat, non-alternating list rows demarcated by 1px `#e4e4e7` bottom border lines.
- Navigation items highlight selection through a single `#ff2a2a` micro-indicator beneath or beside the label, discarding traditional heavy background pills.