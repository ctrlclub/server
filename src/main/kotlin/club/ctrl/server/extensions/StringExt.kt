package club.ctrl.server.extensions

fun String.fillPlaceholders(vararg pairs: Pair<String, String>): String =
    pairs.fold(this) { acc, (k, v) -> acc.replace("%$k%", v) }
