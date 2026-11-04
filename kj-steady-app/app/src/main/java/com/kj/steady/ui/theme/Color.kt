package com.kj.steady.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Base & Neutral Surfaces (Clean, High Contrast)
// ==========================================
val Slate50 = Color(0xFFF8FAFC)       // Crisp light background
val Slate100 = Color(0xFFF1F5F9)      // Card sub-surface / chip background
val Slate200 = Color(0xFFE2E8F0)      // Crisp borders
val Slate300 = Color(0xFFCBD5E1)      // Dividers
val Slate400 = Color(0xFF94A3B8)      // Subtle secondary text
val Slate500 = Color(0xFF64748B)      // Muted / captions
val Slate600 = Color(0xFF475569)      // Body text secondary
val Slate700 = Color(0xFF334155)      // Dark mode borders
val Slate800 = Color(0xFF1E293B)      // Dark mode cards / surfaces
val Slate900 = Color(0xFF0F172A)      // Deep primary text / hero midnight
val Slate950 = Color(0xFF0B0F19)      // Deep dark background

val SurfaceWhite = Color(0xFFFFFFFF)

// Core ink and line definitions
val InkText = Slate900
val MutedText = Slate500
val LineBorder = Slate200
val SandBg = Slate50

// Dark Theme Core
val DarkBg = Slate950
val DarkSurface = Slate800
val DarkInkText = Color(0xFFF1F5F9)
val DarkMutedText = Color(0xFF94A3B8)
val DarkBorder = Slate700

// ==========================================
// Semantic Domain Colors (Multi-Colored System)
// ==========================================

// 1. Primary Brand & Weight (Electric Indigo & Midnight Navy)
val BrandBlue = Color(0xFF2563EB)         // Royal Blue for primary actions & charts
val BrandBlueDark = Color(0xFF1D4ED8)     // Deep Blue
val BrandBlueLight = Color(0xFFEFF6FF)    // Tint background
val BrandBlueBorder = Color(0xFFBFDBFE)   // Tint border
val HeroMidnight = Color(0xFF0F172A)      // Deep Midnight Hero card
val HeroCyan = Color(0xFF38BDF8)          // Vivid Sky/Cyan accent

// Legacy color aliases (re-pointed to new vibrant system for backward-compatibility)
val ForestGreen = BrandBlue
val ForestGreenLight = BrandBlueLight
val ForestGreenDark = Color(0xFF1E3A8A)
val RestrainedLime = HeroCyan
val LimeDark = BrandBlue

// 2. Steps & Physical Activity (Energetic Tangerine / Sunrise Orange)
val StepsOrange = Color(0xFFEA580C)
val StepsOrangeLight = Color(0xFFFFF7ED)
val StepsOrangeBorder = Color(0xFFFED7AA)
val StepsOrangeDark = Color(0xFFC2410C)

// 3. Sleep & Recovery (Calming Royal Violet)
val SleepViolet = Color(0xFF7C3AED)
val SleepVioletLight = Color(0xFFF5F3FF)
val SleepVioletBorder = Color(0xFFDDD6FE)
val SleepVioletDark = Color(0xFF6D28D9)

// 4. Hydration & Fluids (Refreshing Aqua / Sky Blue)
val WaterBlue = Color(0xFF0284C7)
val WaterBlueLight = Color(0xFFF0F9FF)
val WaterBlueBorder = Color(0xFFBAE6FD)
val WaterBlueDark = Color(0xFF0369A1)

// Coffee & Americano (Rich Warm Chestnut / Amber)
val CoffeeChestnut = Color(0xFF92400E)
val CoffeeChestnutLight = Color(0xFFFEF3C7)
val CoffeeChestnutBorder = Color(0xFFFDE68A)

// 5. Nutrition & Meals (Healthy Fresh Emerald)
val NutritionEmerald = Color(0xFF059669)
val NutritionEmeraldLight = Color(0xFFECFDF5)
val NutritionEmeraldBorder = Color(0xFFA7F3D0)
val NutritionEmeraldDark = Color(0xFF047857)

// Protein & Energy (Warm Amber / Gold)
val ProteinAmber = Color(0xFFD97706)
val ProteinAmberLight = Color(0xFFFFFBEB)
val ProteinAmberBorder = Color(0xFFFDE68A)

// Takeaway / Fast Food (Vibrant Tangerine/Coral)
val TakeawayCoral = Color(0xFFEA580C)
val TakeawayCoralLight = Color(0xFFFFF7ED)

// 6. Cardiovascular & Blood Pressure (Crimson Rose)
val HeartRose = Color(0xFFE11D48)
val HeartRoseLight = Color(0xFFFFF1F2)
val HeartRoseBorder = Color(0xFFFECDD3)

// 7. Clinical & Status Alerts
val AmberWarning = Color(0xFFFEF3C7)
val AmberText = Color(0xFF92400E)
val AmberBorder = Color(0xFFFDE68A)

val ErrorRed = Color(0xFFDC2626)
val ErrorBg = Color(0xFFFEF2F2)
val ErrorBorder = Color(0xFFFECACA)

val SuccessGreen = Color(0xFF16A34A)
val SuccessBg = Color(0xFFF0FDF4)
val SuccessBorder = Color(0xFFBBF7D0)
