package io.dotanuki.demos.core.navigator

sealed class Screen {
    object FactsList : Screen()
    object SearchQuery : Screen()

    override fun toString() = when (this) {
        FactsList -> "Facts Screen"
        SearchQuery -> "SearchQuery Query Screen"
    }
}
