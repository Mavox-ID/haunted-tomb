package free.taro.mak.call

object Reader {
    private fun tone(c: Card): Int {
        if (c.joker) return 0
        if (c.aceOfSpades) return 1
        val neg = when (c.suit) {
            3 -> c.rank in listOf(2, 3, 4, 5, 7, 11, 12, 13)
            0 -> c.rank in listOf(3, 4, 5, 7, 11, 13)
            else -> c.rank in listOf(3, 4, 5, 7, 11, 12)
        }
        if (neg) return -1
        if (c.suit == 3 && c.rank in listOf(8, 9, 10)) return 0
        return 1
    }

    private fun join(l: List<String>, ru: Boolean): String {
        return l.dropLast(1).joinToString(", ") + (if (ru) " и " else " and ") + l.last()
    }

    private fun topic(q: String): Int {
        val s = q.lowercase()
        val love = listOf("любов", "отношен", "парен", "девушк", "влюб", "чувств", "love", "relationship", "crush", "boyfriend", "girlfriend")
        val money = listOf("деньг", "зарплат", "купить", "куплю", "money", "salary", "buy", "rich", "income")
        val work = listOf("работ", "учёб", "учеб", "экзамен", "проект", "карьер", "work", "job", "exam", "study", "project", "career", "school")
        if (love.any { s.contains(it) }) return 0
        if (money.any { s.contains(it) }) return 1
        if (work.any { s.contains(it) }) return 2
        return -1
    }

    fun read(cards: List<Card>, spread: Spread, question: String, ru: Boolean): List<Pair<String, String>> {
        val out = ArrayList<Pair<String, String>>()
        val seedRaw = (cards.fold(17) { a, c -> a * 31 + c.id } + question.trim().lowercase().hashCode() * 7) and Int.MAX_VALUE
        val seed = seedRaw % 100000

        for (i in cards.indices) {
            val c = cards[i]
            val pk = spread.positions[i]
            val ms = Data.meanings(c, ru)
            val start = (seed + i * 3 + c.id) % ms.size
            val picked = ArrayList<String>()
            for (k in 0 until 3) picked.add(ms[(start + k) % ms.size])
            val tpl = Text.templates(pk, ru)
            var body = tpl[(seed / 3 + i) % tpl.size].format(join(picked, ru))
            val tn = tone(c)
            if (c.joker) {
                body += " " + Text.t(ru, "Джокер не отменяет остальные карты, он добавляет в расклад непредсказуемый фактор.", "The Joker does not cancel the other cards, it adds an unpredictable factor to the spread.")
            } else if (c.aceOfSpades) {
                body += " " + Text.t(ru, "Рваный туз, особая карта. Старое рвётся, и это к лучшему: дальше путь легче.", "The Torn Ace, the special card. The old thing tears away and it is for the better: the road ahead is lighter.")
            } else if (tn > 0) {
                body += " " + Text.t(ru, "Знак благоприятный.", "A favourable sign.")
            } else if (tn < 0) {
                body += " " + Text.t(ru, "Знак тревожный, но это предупреждение, а не приговор.", "A worrying sign, but a warning, not a verdict.")
            } else {
                body += " " + Text.t(ru, "Знак нейтральный, многое зависит от твоих шагов.", "A neutral sign, a lot depends on your steps.")
            }
            out.add(Pair(Text.posName(pk, ru) + ": " + Text.cardName(c, ru), body))
        }

        val real = cards.filter { !it.joker }
        val counts = IntArray(4)
        real.forEach { counts[it.suit]++ }
        var dom = -1
        var domN = 0
        for (s in 0..3) {
            if (counts[s] > domN) {
                domN = counts[s]
                dom = s
            }
        }
        val need = if (cards.size >= 5) 3 else 2
        val lines = ArrayList<String>()
        if (cards.size > 1 && domN >= need) {
            lines.add(
                Text.t(
                    ru,
                    "Преобладают ${Text.suitPlural(dom, ru)}: в центре расклада ${Text.area(dom, ru)}.",
                    "${Text.suitPlural(dom, ru)} dominate: the heart of the spread is ${Text.area(dom, ru)}."
                )
            )
        }
        val rc = IntArray(14)
        real.forEach { rc[it.rank]++ }
        for (r in 1..13) {
            if (rc[r] >= 2) {
                lines.add(
                    Text.t(
                        ru,
                        "Повтор: ${Text.rankPlural(r, ru)} (${rc[r]}). Тема усиливается: ${Text.theme(r, ru)}.",
                        "Repeat: ${Text.rankPlural(r, ru)} (${rc[r]}). The theme is amplified: ${Text.theme(r, ru)}."
                    )
                )
            }
        }
        val tp = topic(question)
        if (tp >= 0) {
            var matches = 0
            var sum = 0
            for (c in real) {
                if (c.suit == tp) {
                    matches++
                    sum += tone(c)
                }
            }
            lines.add(Text.topicLine(tp, matches, sum, ru))
        }
        if (lines.isNotEmpty()) {
            out.add(Pair(Text.t(ru, "Общая картина", "The big picture"), lines.joinToString("\n\n")))
        }
        return out
    }
}
