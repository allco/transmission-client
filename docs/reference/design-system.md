# Design system

This document lists the tokens, the icons and the components of the `:designsystem` module.
[ADR-6](../adr/ADR-6-design-system-module.md) records the decision and the rules. The page
"Design System" of the [Figma file](https://www.figma.com/design/2UXyddsaD6zijr7h0kdmox) is the
source of the tokens.

## Contents

1. [Module layout](#1-module-layout)
2. [Theme and tokens](#2-theme-and-tokens)
3. [Icons](#3-icons)
4. [Components](#4-components)
5. [Components that are not in the module](#5-components-that-are-not-in-the-module)

---

## 1. Module layout

```
designsystem/src/commonMain/kotlin/eu/alsk/transmissionremote/designsystem/
├── theme/
│   ├── AppTheme.kt      AppTheme { … } and the AppTheme object with the tokens
│   ├── Color.kt         the light and dark colour schemes, StatusColors
│   └── Dimens.kt        Spacing, Radius, Elevation, the shapes
├── icon/
│   └── AppIcons.kt      the icons
└── component/           one file for each component, with previews
```

## 2. Theme and tokens

Wrap each screen and each preview in `AppTheme`. `AppTheme(darkTheme = …)` selects the light or
the dark scheme. The default follows the system.

| Token | Code | Figma |
|---|---|---|
| Colour roles | `AppTheme.colors.primary`, `.surface`, `.onSurfaceVariant`, … | Colour variables, for example `primary`, `surface`, `on-surface-variant` |
| Status colours | `AppTheme.statusColors.success`, `.info`, `.warning`, and the `…Container` colours | `success`, `info`, `warning`, and the `…-container` variables |
| Type scale | `AppTheme.typography.titleMedium`, `.bodySmall`, … | Text styles, for example `Title/Medium`, `Body/Small` |
| Spacing | `Spacing.s2`, `s4`, `s8`, `s12`, `s16`, `s20`, `s24`, `s32`, `s48` | `space/2` … `space/48` |
| Radius | `Radius.xs` 4, `sm` 8, `md` 12, `lg` 16, `xl` 20, `xxl` 28, `full` | `radius/xs` … `radius/full` |
| Elevation | `Elevation.level1`, `level2`, `level3` | `Elevation/1` … `Elevation/3` |
| Tone | `Tone.Info`, `Success`, `Warning`, `Error`, `Neutral`, with `tone.color` and `tone.containerColor` | The `Tone` property of Badge, Banner and Progress bar |

The type scale is the Material 3 default. It has the same sizes as the text styles in Figma.

## 3. Icons

`AppIcons` holds 44 icons. Each name is the name of the Figma icon component (`Icon/<name>`) in
PascalCase, for example `Icon/arrow-down` → `AppIcons.ArrowDown`. The icons come from
[Lucide](https://lucide.dev/), through `com.composables:icons-lucide-cmp`.

These names differ from the Lucide names:

| `AppIcons` | Lucide |
|---|---|
| `MoreVertical` | `EllipsisVertical` |
| `AlertCircle` | `CircleAlert` |
| `AlertTriangle` | `TriangleAlert` |
| `Close` | `X` |
| `Sort` | `ArrowUpDown` |
| `Sliders` | `SlidersVertical` |
| `Refresh` | `RefreshCw` |
| `PlayCircle` | `CirclePlay` |

To add an icon, add the icon component to Figma. Then add a property to `AppIcons`.

## 4. Components

Each component has a file with the same name in `component/`. The file has light and dark
previews. `Dialog`, `DropdownMenu` and `BottomSheet` open a popup window. Their previews show the
panel of the popup, because the preview tools do not draw popup windows.

| Component | Figma component | Main parameters |
|---|---|---|
| `Button` | Button | `text`, `onClick`, `style: ButtonStyle` (Filled, Tonal, Outlined, Text, Danger), `enabled`, `leadingIcon` |
| `IconButton` | Icon button | `icon`, `contentDescription`, `onClick`, `style: IconButtonStyle` (Standard, Tonal, Filled) |
| `Fab` | FAB | `text`, `icon`, `onClick` |
| `TextField` | Text field | `label`, `value`, `onValueChange`, `placeholder`, `error`, `supportingText`, `trailingIcon` |
| `PasswordField` | Text field with the eye icon | `label`, `value`, `onValueChange`, `error`. It keeps the show and hide state itself. |
| `Switch` | Switch | `checked`, `onCheckedChange` |
| `Checkbox` | Checkbox | `checked`, `onCheckedChange` |
| `RadioButton` | Radio | `selected`, `onClick` |
| `SegmentedButtons` | Segmented button | `options: List<SegmentOption>`, `selectedIndex`, `onSelect` |
| `FilterChip` | Filter chip | `label`, `selected`, `onClick` |
| `Tabs` | Tab | `titles`, `selectedIndex`, `onSelect` |
| `Badge` | Badge | `text`, `tone` |
| `ProgressBar` | Progress bar | `progress` (0 to 1), `tone`, `size: ProgressBarSize` (Small 4 dp, Large 8 dp) |
| `CircularProgress` | Circular progress | `size` |
| `TopAppBar` | Top app bar, Main and Back | `title`, `subtitle`, `statusTone` (the dot), `onBack`, `actions` |
| `SearchTopAppBar` | Top app bar, Search | `query`, `onQueryChange`, `onBack`, `placeholder` |
| `SelectionTopAppBar` | Top app bar, Selection | `title`, `onClose`, `actions` |
| `ListItem` | List item | `headline`, `supporting`, `leadingIcon`, `trailing: ListItemTrailing`, `onClick` |
| `DropdownMenu`, `MenuItem`, `MenuDivider` | Menu item | `MenuItem(text, onClick, leadingIcon, trailing: MenuItemTrailing)` |
| `Divider` | Divider | — |
| `SectionHeader` | Section header | `text` |
| `SnackbarHost` | Snackbar | `hostState` |
| `Banner` | Banner | `message`, `tone`, `actionText`, `onAction` |
| `Dialog`, `DialogText`, `DialogCheckboxOption` | Dialog, Dialog with field | `title`, `confirmText`, `onConfirm`, `onDismiss`, `dismissText`, `danger`, `content` |
| `BottomSheet` | Bottom sheet header | `title`, `onDismiss`, `content` |
| `EmptyState` | Empty state | `icon`, `title`, `body`, `actionText`, `onAction` |
| `Card` | Card | `style: CardStyle` (Filled, Outlined), `onClick`, `content` |
| `Stat` | Stat | `icon`, `label`, `value`, `iconTint` |
| `Logo` | Logo | `size` |
| `SkeletonRow` | Skeleton row | — |

The trailing part of a `ListItem` is one of these values of `ListItemTrailing`: `None`, `Chevron`,
`Value(text, chevron)`, `Switch(checked, onCheckedChange)`, `Check(checked)` and
`Radio(selected)`. The trailing part of a `MenuItem` is one of `None`, `Value(text)`,
`Chevron(value)` and `Switch(checked)`.

The component names are the same as the names of the Material 3 components. Feature code imports
the components of this module. Feature code does not import a Material 3 component with the same
name.

## 5. Components that are not in the module

The Figma page "Design System" also has components that show the data of one feature. These
components are not in the module ([ADR-6](../adr/ADR-6-design-system-module.md), section 3). Each
feature builds them in its `views/` package from the components above, when the feature needs
them:

| Figma component | Feature |
|---|---|
| Torrent row, Torrent row expanded | TorrentList |
| Server item | ServerList |
| File item, Peer item, Tracker item | TorrentDetails |
