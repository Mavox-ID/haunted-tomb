package free.taro.mak.call

object Text {
    private val rankRu = arrayOf("", "Туз", "Двойка", "Тройка", "Четвёрка", "Пятёрка", "Шестёрка", "Семёрка", "Восьмёрка", "Девятка", "Десятка", "Валет", "Дама", "Король")
    private val rankEn = arrayOf("", "Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King")
    private val rankPluRu = arrayOf("", "Тузы", "Двойки", "Тройки", "Четвёрки", "Пятёрки", "Шестёрки", "Семёрки", "Восьмёрки", "Девятки", "Десятки", "Валеты", "Дамы", "Короли")
    private val rankPluEn = arrayOf("", "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Sevens", "Eights", "Nines", "Tens", "Jacks", "Queens", "Kings")
    private val suitRu = arrayOf("червей", "бубен", "треф", "пик")
    private val suitEn = arrayOf("Hearts", "Diamonds", "Clubs", "Spades")
    private val suitPluRu = arrayOf("червы", "бубны", "трефы", "пики")
    private val areaRu = arrayOf("чувства, отношения и семья", "деньги, новости и возможности", "работа, учёба, действия и друзья", "препятствия, решения и серьёзные перемены")
    private val areaEn = arrayOf("feelings, relationships and family", "money, news and opportunities", "work, study, action and friends", "obstacles, decisions and serious change")
    private val themeRu = arrayOf("", "новое начало", "выбор и союз", "новости и рост", "стабильность", "перемены и напряжение", "движение к улучшению", "сомнения и ожидание", "разговоры и новости", "кульминация", "завершение этапа", "сообщения и молодые люди", "женщины в ситуации", "мужчины и авторитеты")
    private val themeEn = arrayOf("", "a fresh start", "choice and union", "news and growth", "stability", "change and tension", "movement toward improvement", "doubt and waiting", "talk and news", "culmination", "the end of a stage", "messages and young people", "women in the situation", "men and authority")

    fun t(ru: Boolean, a: String, b: String): String = if (ru) a else b

    fun cardName(c: Card, ru: Boolean): String {
        if (c.joker) return if (ru) "Джокер" else "Joker"
        if (c.aceOfSpades) return if (ru) "Рваный туз (туз пик)" else "Torn Ace (Ace of Spades)"
        return if (ru) rankRu[c.rank] + " " + suitRu[c.suit] else rankEn[c.rank] + " of " + suitEn[c.suit]
    }

    fun rankPlural(r: Int, ru: Boolean): String = if (ru) rankPluRu[r] else rankPluEn[r]
    fun theme(r: Int, ru: Boolean): String = if (ru) themeRu[r] else themeEn[r]
    fun suitPlural(s: Int, ru: Boolean): String = if (ru) suitPluRu[s] else suitEn[s]
    fun area(s: Int, ru: Boolean): String = if (ru) areaRu[s] else areaEn[s]

    fun spreadName(i: Int, ru: Boolean): String = when (i) {
        0 -> t(ru, "Одна карта", "One card")
        1 -> t(ru, "Три карты", "Three cards")
        2 -> t(ru, "Пять карт", "Five cards")
        else -> t(ru, "Отношения", "Relationship")
    }

    fun posName(key: String, ru: Boolean): String = when (key) {
        "SINGLE" -> t(ru, "Ответ", "Answer")
        "PAST" -> t(ru, "Прошлое", "Past")
        "PRESENT" -> t(ru, "Настоящее", "Present")
        "FUTURE" -> t(ru, "Будущее", "Future")
        "HIDDEN" -> t(ru, "Скрытое", "Hidden")
        "OUTCOME" -> t(ru, "Итог", "Outcome")
        "MINE" -> t(ru, "Моё отношение", "My view")
        "THEIRS" -> t(ru, "Их отношение", "Their view")
        "COMMON" -> t(ru, "Что объединяет", "Common ground")
        "BLOCK" -> t(ru, "Что мешает", "What blocks")
        else -> t(ru, "Развитие", "Development")
    }

    fun templates(key: String, ru: Boolean): List<String> = when (key) {
        "SINGLE" -> if (ru) listOf("Ответ на твой вопрос: %s.", "Карта говорит о таком: %s.") else listOf("The answer to your question: %s.", "The card speaks of this: %s.")
        "PAST" -> if (ru) listOf("Раньше в этой истории были: %s.", "Корни ситуации уходят сюда: %s.") else listOf("Earlier in this story there were: %s.", "The roots of the situation lie here: %s.")
        "PRESENT" -> if (ru) listOf("Сейчас вокруг тебя: %s.", "Главное в настоящем моменте: %s.") else listOf("Right now around you: %s.", "The main thing in the present: %s.")
        "FUTURE" -> if (ru) listOf("Дальше всё идёт к этому: %s.", "Впереди тебя ждёт: %s.") else listOf("From here things lead to: %s.", "Ahead of you waits: %s.")
        "HIDDEN" -> if (ru) listOf("Что остаётся в тени: %s.", "За кадром действует: %s.") else listOf("What stays in the shadow: %s.", "Working behind the scenes: %s.")
        "OUTCOME" -> if (ru) listOf("Итог ситуации выглядит так: %s.", "Всё приведёт к этому: %s.") else listOf("The outcome looks like this: %s.", "Everything leads to this: %s.")
        "MINE" -> if (ru) listOf("С твоей стороны это: %s.", "Ты приносишь в ситуацию: %s.") else listOf("From your side it is: %s.", "You bring into the situation: %s.")
        "THEIRS" -> if (ru) listOf("С их стороны это: %s.", "Другой человек несёт в ситуацию: %s.") else listOf("From their side it is: %s.", "The other person brings: %s.")
        "COMMON" -> if (ru) listOf("Вас объединяет: %s.", "Общая почва между вами: %s.") else listOf("What unites you: %s.", "Your common ground: %s.")
        "BLOCK" -> if (ru) listOf("Мешает вам: %s.", "Преградой стоит: %s.") else listOf("What gets in the way: %s.", "Standing as a barrier: %s.")
        else -> if (ru) listOf("Отношения могут развиваться так: %s.", "Дальше между вами может быть: %s.") else listOf("The relationship may develop like this: %s.", "Further between you there may be: %s.")
    }

    fun topicName(i: Int, ru: Boolean): String = when (i) {
        0 -> t(ru, "любви и чувств", "love and feelings")
        1 -> t(ru, "денег", "money")
        else -> t(ru, "работы и учёбы", "work and study")
    }

    fun topicLine(i: Int, matches: Int, sum: Int, ru: Boolean): String {
        val n = topicName(i, ru)
        if (matches == 0) return t(ru, "Твой вопрос про $n, но карт этой масти в раскладе нет. Ответ приходит окольным путём, через соседние сферы жизни.", "Your question is about $n, but no card of that suit appears. The answer comes indirectly, through neighbouring areas of life.")
        if (sum > 0) return t(ru, "По теме $n карты складываются в твою пользу.", "On the matter of $n the cards line up in your favour.")
        if (sum < 0) return t(ru, "По теме $n карты просят осторожности: есть что пересмотреть.", "On the matter of $n the cards ask for caution: something needs a second look.")
        return t(ru, "По теме $n картина смешанная: многое зависит от твоих действий.", "On the matter of $n the picture is mixed: much depends on what you do.")
    }
}
