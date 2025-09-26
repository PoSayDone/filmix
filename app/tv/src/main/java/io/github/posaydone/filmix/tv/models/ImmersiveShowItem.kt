package io.github.posaydone.filmix.tv.models

import io.github.posaydone.filmix.core.model.Show

/**
 * A data class that holds the original Show object along with
 * enriched metadata (backdrop, logo, description) fetched from Kinopoisk.
 */
data class ImmersiveShowItem(
    // The original show data used for the card and navigation
    val show: Show,
    // Enriched data from Kinopoisk
    val backdropUrl: String?,
    val logoUrl: String?,
    val description: String?,
)
