package com.aaspaas.customer.core.ui.theme

import androidx.compose.ui.graphics.Color

// Brand Colors - AasPaasWala Palette per Figma Spec
val Primary = Color(0xFF30306F)
val PrimaryDark = Color(0xFF22234F)
val Accent = Color(0xFFC8664D)

// Neutrals
val Background = Color(0xFFFFFDF8)
val Surface = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF171717)
val TextSecondary = Color(0xFF6B6B6B)
val Border = Color(0xFFE7E4DE)

// Semantic
val Success = Color(0xFF25855A)
val Warning = Color(0xFFD89024)
val Error = Color(0xFFD64545)

// Reservation states
val StateActive = Success
val StatePending = Warning
val StateExpired = Color(0xFF95A5A6)
val StateCancelled = Error
val StateCompleted = Primary

// Availability
val Available = Success
val LowStock = Warning
val OutOfStock = Error
val Reserved = Color(0xFF9B59B6)

// Legacy aliases for backward compatibility
@Deprecated("Use Primary", ReplaceWith("Primary"))
val Brand = Primary
@Deprecated("Use Accent", ReplaceWith("Accent"))
val BrandAccent = Accent
@Deprecated("Use PrimaryDark", ReplaceWith("PrimaryDark"))
val BrandSecondary = PrimaryDark
@Deprecated("Use Surface", ReplaceWith("Surface"))
val White = Surface