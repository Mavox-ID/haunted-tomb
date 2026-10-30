package free.taro.mak.call

class Card(val id: Int) {
    val joker: Boolean get() = id == 52
    val aceOfSpades: Boolean get() = id == 39
    val suit: Int get() = if (id == 52) -1 else id / 13
    val rank: Int get() = if (id == 52) 0 else id % 13 + 1
    val key: String get() = if (id == 52) "JK" else "A23456789TJQK"[id % 13].toString() + "HDCS"[id / 13]
}

class Spread(val positions: List<String>)

object Spreads {
    val all = listOf(
        Spread(listOf("SINGLE")),
        Spread(listOf("PAST", "PRESENT", "FUTURE")),
        Spread(listOf("PAST", "PRESENT", "FUTURE", "HIDDEN", "OUTCOME")),
        Spread(listOf("MINE", "THEIRS", "COMMON", "BLOCK", "DEVELOP"))
    )
}
