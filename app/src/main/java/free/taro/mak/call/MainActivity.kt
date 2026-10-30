package free.taro.mak.call

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.parseColor("#101114")
    private val surface = Color.parseColor("#1A1B20")
    private val line = Color.parseColor("#2E3038")
    private val light = Color.parseColor("#E6E6EA")
    private val muted = Color.parseColor("#8B8D98")
    private val body = Color.parseColor("#B9BBC6")
    private val redText = Color.parseColor("#E5646A")
    private val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
    private val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

    private var ru = false
    private var screen = 0
    private var detailId = 0L
    private var numFrom = "1"
    private var numTo = "100"
    private var numResult = ""
    private var numLo = 0L
    private var numHi = 0L
    private var numFromEt: EditText? = null
    private var numToEt: EditText? = null
    private var spreadIdx = 1
    private var manual = true
    private var cards: List<Card> = emptyList()
    private var flipped = BooleanArray(0)
    private var manualCards: Array<Card?> = arrayOfNulls<Card>(3)
    private var manualShown = false
    private var question = ""
    private var readQuestion = ""
    private var showReading = false
    private var animateIn = false
    private var history = ArrayList<Entry>()
    private val cardViews = ArrayList<CardView>()
    private var input: EditText? = null
    private var revealBtn: View? = null
    private var cardsRow: View? = null
    private lateinit var scroll: ScrollView
    private lateinit var content: LinearLayout
    private lateinit var readingBox: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lang = Locale.getDefault().language
        ru = lang == "ru" || lang == "uk"
        history = Store.load(this)
        scroll = ScrollView(this)
        scroll.setBackgroundColor(bg)
        scroll.fitsSystemWindows = true
        content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(dp(20), dp(16), dp(20), dp(40))
        scroll.addView(content, FrameLayout.LayoutParams(MATCH, WRAP))
        readingBox = LinearLayout(this)
        readingBox.orientation = LinearLayout.VERTICAL
        setContentView(scroll)
        resetState()
        render()
    }

    override fun onBackPressed() {
        if (screen == 2) {
            go(1)
        } else if (screen == 1 || screen == 3) {
            go(0)
        } else {
            super.onBackPressed()
        }
    }

    private fun go(s: Int) {
        screen = s
        render()
        scroll.post { scroll.scrollTo(0, 0) }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun rounded(color: Int, radius: Int, strokeColor: Int = 0, strokeW: Int = 0): GradientDrawable {
        val d = GradientDrawable()
        d.setColor(color)
        d.cornerRadius = dp(radius).toFloat()
        if (strokeW > 0) d.setStroke(dp(strokeW), strokeColor)
        return d
    }

    private fun label(text: String, sp: Float, color: Int, bold: Boolean = false): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        tv.setTextColor(color)
        if (bold) tv.setTypeface(tv.typeface, Typeface.BOLD)
        return tv
    }

    private fun lp(w: Int, h: Int, top: Int = 0): LinearLayout.LayoutParams {
        val p = LinearLayout.LayoutParams(w, h)
        p.topMargin = dp(top)
        return p
    }

    private fun hideKeyboard(v: View) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun toast(ruText: String, enText: String) {
        Toast.makeText(this, Text.t(ru, ruText, enText), Toast.LENGTH_SHORT).show()
    }

    private fun fmt(ts: Long): String = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(ts))

    private fun displayName(e: Entry): String = if (e.title.isNotBlank()) e.title else e.question

    private fun resetState() {
        val n = Spreads.all[spreadIdx].positions.size
        cards = emptyList()
        flipped = BooleanArray(0)
        manualCards = arrayOfNulls<Card>(n)
        manualShown = false
        showReading = false
        readQuestion = ""
    }

    private fun activeCards(): List<Card> {
        return if (manual) manualCards.filterNotNull() else cards
    }

    private fun addHistory() {
        val e = Entry(System.currentTimeMillis(), System.currentTimeMillis(), "", spreadIdx, readQuestion, activeCards().map { it.id }, manual)
        history.add(0, e)
        while (history.size > 300) history.removeAt(history.size - 1)
        Store.save(this, history)
    }

    private fun chip(text: String, color: Int = muted, action: () -> Unit): TextView {
        val c = label(text, 14f, color, true)
        c.setPadding(dp(14), dp(8), dp(14), dp(8))
        c.background = rounded(surface, 10)
        c.setOnClickListener { action() }
        return c
    }

    private fun segmented(items: List<String>, selected: Int, sp: Float, onSelect: (Int) -> Unit): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.background = rounded(surface, 10)
        row.setPadding(dp(3), dp(3), dp(3), dp(3))
        for (i in items.indices) {
            val on = i == selected
            val tv = label(items[i], sp, if (on) bg else muted, on)
            tv.gravity = Gravity.CENTER
            tv.maxLines = 2
            tv.setPadding(dp(4), dp(10), dp(4), dp(10))
            if (on) tv.background = rounded(light, 8)
            tv.setOnClickListener { if (!on) onSelect(i) }
            row.addView(tv, LinearLayout.LayoutParams(0, WRAP, 1f))
        }
        return row
    }

    private fun button(text: String, action: () -> Unit): TextView {
        val b = label(text, 15f, bg, true)
        b.gravity = Gravity.CENTER
        b.background = rounded(light, 10)
        b.setOnClickListener { action() }
        return b
    }

    private fun outlineButton(text: String, color: Int, action: () -> Unit): TextView {
        val b = label(text, 15f, color, true)
        b.gravity = Gravity.CENTER
        b.background = rounded(surface, 10, line, 1)
        b.setOnClickListener { action() }
        return b
    }

    private fun header(title: String, back: Boolean): View {
        val h = LinearLayout(this)
        h.orientation = LinearLayout.HORIZONTAL
        h.gravity = Gravity.CENTER_VERTICAL
        if (back) {
            h.addView(chip(Text.t(ru, "Назад", "Back")) { onBackPressed() }, LinearLayout.LayoutParams(WRAP, WRAP))
            val p = LinearLayout.LayoutParams(0, WRAP, 1f)
            p.leftMargin = dp(14)
            h.addView(label(title, 18f, light, true), p)
        } else {
            h.addView(label(title, 26f, light, true), LinearLayout.LayoutParams(0, WRAP, 1f))
            val pn = LinearLayout.LayoutParams(WRAP, WRAP)
            pn.rightMargin = dp(8)
            h.addView(chip(Text.t(ru, "Число", "Number")) { go(3) }, pn)
            val p = LinearLayout.LayoutParams(WRAP, WRAP)
            p.rightMargin = dp(8)
            h.addView(chip(Text.t(ru, "История", "History")) { go(1) }, p)
        }
        h.addView(chip(if (ru) "EN" else "RU") {
            ru = !ru
            render()
        }, LinearLayout.LayoutParams(WRAP, WRAP))
        return h
    }

    private fun render() {
        input?.let { question = it.text.toString() }
        numFromEt?.let { numFrom = it.text.toString() }
        numToEt?.let { numTo = it.text.toString() }
        content.removeAllViews()
        cardViews.clear()
        revealBtn = null
        cardsRow = null
        when (screen) {
            0 -> {
                numFromEt = null
                numToEt = null
                renderMain()
            }
            1 -> {
                input = null
                numFromEt = null
                numToEt = null
                renderList()
            }
            2 -> {
                input = null
                numFromEt = null
                numToEt = null
                renderDetail()
            }
            else -> {
                input = null
                renderNumber()
            }
        }
    }

    private fun numberField(hint: String, value: String): EditText {
        val et = EditText(this)
        et.setText(value)
        et.hint = hint
        et.setTextColor(light)
        et.setHintTextColor(muted)
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        et.gravity = Gravity.CENTER
        et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        et.maxLines = 1
        et.setPadding(dp(14), dp(14), dp(14), dp(14))
        et.background = rounded(surface, 10, line, 1)
        return et
    }

    private fun renderNumber() {
        content.addView(header(Text.t(ru, "Случайное число", "Random number"), true), lp(MATCH, WRAP))

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        val a = numberField(Text.t(ru, "От", "From"), numFrom)
        val b = numberField(Text.t(ru, "До", "To"), numTo)
        numFromEt = a
        numToEt = b
        val pa = LinearLayout.LayoutParams(0, WRAP, 1f)
        pa.rightMargin = dp(6)
        val pb = LinearLayout.LayoutParams(0, WRAP, 1f)
        pb.leftMargin = dp(6)
        row.addView(a, pa)
        row.addView(b, pb)
        content.addView(row, lp(MATCH, WRAP, 24))

        content.addView(button(Text.t(ru, "Выбрать число", "Pick a number")) { rollNumber() }, lp(MATCH, dp(52), 16))

        if (numResult.isNotEmpty()) {
            val box = LinearLayout(this)
            box.orientation = LinearLayout.VERTICAL
            box.gravity = Gravity.CENTER_HORIZONTAL
            box.setPadding(dp(16), dp(28), dp(16), dp(28))
            box.background = rounded(surface, 12, line, 1)
            val size = if (numResult.length > 8) 30f else if (numResult.length > 5) 42f else 56f
            val big = label(numResult, size, light, true)
            big.gravity = Gravity.CENTER
            big.maxLines = 1
            box.addView(big, LinearLayout.LayoutParams(MATCH, WRAP))
            val info = label(Text.t(ru, "от $numLo до $numHi", "from $numLo to $numHi"), 13f, muted)
            info.gravity = Gravity.CENTER
            box.addView(info, lp(MATCH, WRAP, 8))
            content.addView(box, lp(MATCH, WRAP, 22))
            box.alpha = 0f
            box.animate().alpha(1f).setDuration(250).start()
        }
    }

    private fun rollNumber() {
        numFromEt?.let { numFrom = it.text.toString() }
        numToEt?.let { numTo = it.text.toString() }
        numFromEt?.let { hideKeyboard(it) }
        val x = numFrom.trim().toLongOrNull()
        val y = numTo.trim().toLongOrNull()
        if (x == null || y == null) {
            toast("Введи оба числа", "Enter both numbers")
            return
        }
        val limit = 1_000_000_000_000L
        if (x < -limit || x > limit || y < -limit || y > limit) {
            toast("Слишком большие числа", "Numbers are too large")
            return
        }
        val lo = minOf(x, y)
        val hi = maxOf(x, y)
        val range = hi - lo + 1
        val r = lo + Math.floorMod(SecureRandom().nextLong(), range)
        numLo = lo
        numHi = hi
        numResult = r.toString()
        render()
    }

    private fun renderMain() {
        showReading = if (manual) manualShown && manualCards.all { it != null } else cards.isNotEmpty() && flipped.all { it }

        content.addView(header("Taro", false), lp(MATCH, WRAP))

        content.addView(
            segmented(listOf(Text.t(ru, "Свои карты", "My cards"), Text.t(ru, "Случайно", "Random")), if (manual) 0 else 1, 14f) {
                manual = it == 0
                resetState()
                render()
            },
            lp(MATCH, WRAP, 20)
        )

        content.addView(
            segmented(Spreads.all.indices.map { Text.spreadName(it, ru) }, spreadIdx, 12f) {
                spreadIdx = it
                resetState()
                render()
            },
            lp(MATCH, WRAP, 10)
        )

        val et = EditText(this)
        et.setText(question)
        et.setTextColor(light)
        et.setHintTextColor(muted)
        et.hint = Text.t(ru, "Вопрос или желание", "Question or wish")
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        et.maxLines = 3
        et.setPadding(dp(14), dp(12), dp(14), dp(12))
        et.background = rounded(surface, 10, line, 1)
        input = et
        content.addView(et, lp(MATCH, WRAP, 16))

        if (manual) buildManual() else buildRandom()
    }

    private fun buildRandom() {
        content.addView(
            button(if (cards.isEmpty()) Text.t(ru, "Перемешать и вытянуть", "Shuffle and draw") else Text.t(ru, "Перемешать заново", "Shuffle again")) { draw() },
            lp(MATCH, dp(52), 12)
        )
        if (cards.isEmpty()) return

        val sp = Spreads.all[spreadIdx]
        val row = buildRow(sp, { cards[it] }, { flipped[it] }, { flipCard(it) }, true)
        cardsRow = row
        content.addView(row, lp(MATCH, WRAP, 24))

        if (!showReading) {
            val reveal = chip(Text.t(ru, "Открыть все", "Reveal all"), light) {}
            reveal.gravity = Gravity.CENTER
            reveal.setOnClickListener {
                reveal.visibility = View.GONE
                var d = 0L
                for (i in cards.indices) {
                    if (!flipped[i]) {
                        val idx = i
                        reveal.postDelayed({ flipCard(idx) }, d)
                        d += 160L
                    }
                }
            }
            revealBtn = reveal
            content.addView(reveal, lp(MATCH, WRAP, 18))
        }

        content.addView(readingBox, lp(MATCH, WRAP, 18))
        if (showReading) fillReading(readingBox, cards, spreadIdx, readQuestion, false) else clearBox(readingBox)
    }

    private fun buildManual() {
        val sp = Spreads.all[spreadIdx]
        val row = buildRow(sp, { manualCards[it] }, { true }, { pickCard(it) }, false)
        cardsRow = row
        content.addView(row, lp(MATCH, WRAP, 18))

        content.addView(
            button(Text.t(ru, "Толковать", "Read the cards")) {
                if (manualCards.any { it == null }) {
                    toast("Выбери все карты", "Pick all the cards")
                } else {
                    input?.let {
                        question = it.text.toString()
                        hideKeyboard(it)
                    }
                    val q = question.trim()
                    if (q.isEmpty()) {
                        toast("Сначала напиши вопрос или желание", "Write a question or wish first")
                    } else {
                        if (!manualShown || q != readQuestion) {
                            readQuestion = q
                            manualShown = true
                            addHistory()
                        }
                        render()
                        readingBox.postDelayed({ scroll.smoothScrollTo(0, readingBox.top - dp(10)) }, 150)
                    }
                }
            },
            lp(MATCH, dp(52), 22)
        )

        content.addView(readingBox, lp(MATCH, WRAP, 18))
        if (showReading) fillReading(readingBox, manualCards.filterNotNull(), spreadIdx, readQuestion, false) else clearBox(readingBox)
    }

    private fun buildRow(sp: Spread, cardAt: (Int) -> Card?, up: (Int) -> Boolean, click: (Int) -> Unit, track: Boolean): View {
        val n = sp.positions.size
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER
        for (i in 0 until n) {
            val cell = LinearLayout(this)
            cell.orientation = LinearLayout.VERTICAL
            cell.gravity = Gravity.CENTER_HORIZONTAL
            val cv = CardView(this)
            cv.card = cardAt(i)
            cv.faceUp = up(i)
            cv.setOnClickListener { click(i) }
            if (track) {
                cardViews.add(cv)
                if (animateIn) {
                    cv.alpha = 0f
                    cv.translationY = dp(30).toFloat()
                    cv.animate().alpha(1f).translationY(0f).setStartDelay(i * 90L).setDuration(300).start()
                }
            }
            cell.addView(cv, LinearLayout.LayoutParams(MATCH, WRAP))
            val name = label(Text.posName(sp.positions[i], ru), if (n > 3) 10f else 12f, muted)
            name.gravity = Gravity.CENTER
            cell.addView(name, lp(MATCH, WRAP, 6))
            val cp = if (n == 1) LinearLayout.LayoutParams(dp(170), WRAP) else LinearLayout.LayoutParams(0, WRAP, 1f)
            cp.setMargins(dp(4), 0, dp(4), 0)
            row.addView(cell, cp)
        }
        if (track) animateIn = false
        return row
    }

    private fun draw() {
        input?.let {
            question = it.text.toString()
            hideKeyboard(it)
        }
        val q = question.trim()
        if (q.isEmpty()) {
            toast("Сначала напиши вопрос или желание", "Write a question or wish first")
            return
        }
        readQuestion = q
        val n = Spreads.all[spreadIdx].positions.size
        val rnd = SecureRandom()
        val deck = (0..52).toMutableList()
        deck.shuffle(rnd)
        val cut = 1 + rnd.nextInt(deck.size - 1)
        val cutDeck = deck.drop(cut) + deck.take(cut)
        cards = cutDeck.take(n).map { Card(it) }
        flipped = BooleanArray(n)
        showReading = false
        animateIn = true
        render()
        scroll.postDelayed({ scroll.smoothScrollTo(0, (cardsRow?.top ?: 0) - dp(12)) }, 150)
    }

    private fun flipCard(i: Int) {
        if (i >= flipped.size || flipped[i]) return
        flipped[i] = true
        cardViews[i].flip {
            if (flipped.all { it }) revealReading()
        }
    }

    private fun revealReading() {
        if (showReading) return
        showReading = true
        revealBtn?.visibility = View.GONE
        addHistory()
        fillReading(readingBox, cards, spreadIdx, readQuestion, true)
        readingBox.postDelayed({ scroll.smoothScrollTo(0, readingBox.top - dp(10)) }, 150)
    }

    private fun clearBox(box: LinearLayout) {
        box.removeAllViews()
        box.background = null
        box.setPadding(0, 0, 0, 0)
    }

    private fun fillReading(box: LinearLayout, list: List<Card>, sIdx: Int, q: String, anim: Boolean) {
        box.removeAllViews()
        val sections = Reader.read(list, Spreads.all[sIdx], q, ru)
        for (s in sections) {
            box.addView(label(s.first, 16f, light, true), lp(MATCH, WRAP, 16))
            val t = label(s.second, 15f, body)
            t.setLineSpacing(0f, 1.25f)
            box.addView(t, lp(MATCH, WRAP, 6))
        }
        box.background = rounded(surface, 12, line, 1)
        box.setPadding(dp(18), dp(4), dp(18), dp(20))
        if (anim) {
            box.alpha = 0f
            box.animate().alpha(1f).setDuration(400).start()
        } else {
            box.alpha = 1f
        }
    }

    private fun renderList() {
        content.addView(header(Text.t(ru, "История", "History"), true), lp(MATCH, WRAP))
        if (history.isEmpty()) {
            val e = label(Text.t(ru, "Пока пусто", "Nothing here yet"), 15f, muted)
            content.addView(e, lp(MATCH, WRAP, 28))
            return
        }
        for (e in history) {
            val item = LinearLayout(this)
            item.orientation = LinearLayout.VERTICAL
            item.setPadding(dp(16), dp(14), dp(16), dp(14))
            item.background = rounded(surface, 12, line, 1)
            val name = label(displayName(e), 16f, light, true)
            name.maxLines = 2
            name.ellipsize = TextUtils.TruncateAt.END
            item.addView(name)
            item.addView(label(Text.spreadName(e.spread, ru) + ", " + fmt(e.ts), 12f, muted), lp(MATCH, WRAP, 4))
            item.setOnClickListener {
                detailId = e.id
                go(2)
            }
            content.addView(item, lp(MATCH, WRAP, 10))
        }
    }

    private fun renderDetail() {
        val e = history.firstOrNull { it.id == detailId }
        if (e == null) {
            screen = 1
            renderList()
            return
        }
        content.addView(header(Text.t(ru, "Запись", "Entry"), true), lp(MATCH, WRAP))
        content.addView(label(displayName(e), 20f, light, true), lp(MATCH, WRAP, 22))
        content.addView(label(Text.spreadName(e.spread, ru) + ", " + fmt(e.ts), 13f, muted), lp(MATCH, WRAP, 4))
        if (e.title.isNotBlank()) {
            content.addView(label(e.question, 14f, body), lp(MATCH, WRAP, 10))
        }

        val row = buildRow(Spreads.all[e.spread], { Card(e.ids[it]) }, { true }, { }, false)
        content.addView(row, lp(MATCH, WRAP, 20))

        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        fillReading(box, e.ids.map { Card(it) }, e.spread, e.question, false)
        content.addView(box, lp(MATCH, WRAP, 18))

        val actions = LinearLayout(this)
        actions.orientation = LinearLayout.HORIZONTAL
        val p1 = LinearLayout.LayoutParams(0, dp(50), 1f)
        p1.rightMargin = dp(6)
        val p2 = LinearLayout.LayoutParams(0, dp(50), 1f)
        p2.leftMargin = dp(6)
        actions.addView(outlineButton(Text.t(ru, "Переименовать", "Rename"), light) { renameDialog(e) }, p1)
        actions.addView(outlineButton(Text.t(ru, "Удалить", "Delete"), redText) { deleteDialog(e) }, p2)
        content.addView(actions, lp(MATCH, WRAP, 22))
    }

    private fun renameDialog(e: Entry) {
        val et = EditText(this)
        et.setText(displayName(e))
        et.setSelection(et.text.length)
        val box = FrameLayout(this)
        box.setPadding(dp(20), dp(8), dp(20), 0)
        box.addView(et, FrameLayout.LayoutParams(MATCH, WRAP))
        AlertDialog.Builder(this)
            .setTitle(Text.t(ru, "Название", "Name"))
            .setView(box)
            .setPositiveButton(Text.t(ru, "Сохранить", "Save")) { _, _ ->
                e.title = et.text.toString().trim()
                Store.save(this, history)
                render()
            }
            .setNegativeButton(Text.t(ru, "Отмена", "Cancel"), null)
            .show()
    }

    private fun deleteDialog(e: Entry) {
        AlertDialog.Builder(this)
            .setTitle(Text.t(ru, "Удалить запись?", "Delete this entry?"))
            .setPositiveButton(Text.t(ru, "Удалить", "Delete")) { _, _ ->
                history.remove(e)
                Store.save(this, history)
                go(1)
            }
            .setNegativeButton(Text.t(ru, "Отмена", "Cancel"), null)
            .show()
    }

    private fun setManual(slot: Int, id: Int, dlg: Dialog) {
        for (i in manualCards.indices) {
            if (i != slot && manualCards[i]?.id == id) {
                toast("Эта карта уже выбрана", "This card is already picked")
                return
            }
        }
        manualCards[slot] = Card(id)
        manualShown = false
        dlg.dismiss()
        render()
    }

    private fun pickCard(slot: Int) {
        val dlg = Dialog(this)
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(14), dp(16), dp(14), dp(16))
        box.background = rounded(surface, 14, line, 1)
        val sel = intArrayOf(-1)

        box.addView(label(Text.posName(Spreads.all[spreadIdx].positions[slot], ru), 15f, light, true), lp(MATCH, WRAP))

        val suitRow = LinearLayout(this)
        suitRow.orientation = LinearLayout.HORIZONTAL
        val suitViews = ArrayList<TextView>()
        for (s in 0..3) {
            val name = Text.suitPlural(s, ru).replaceFirstChar { it.uppercase() }
            val tv = label(name, 13f, light, true)
            tv.gravity = Gravity.CENTER
            tv.setPadding(0, dp(12), 0, dp(12))
            suitViews.add(tv)
            val p = LinearLayout.LayoutParams(0, WRAP, 1f)
            p.setMargins(dp(3), 0, dp(3), 0)
            suitRow.addView(tv, p)
        }
        fun paint() {
            for (s in 0..3) {
                val on = sel[0] == s
                val tv = suitViews[s]
                tv.setTextColor(if (on) bg else if (s < 2) redText else light)
                tv.background = if (on) rounded(light, 8) else rounded(bg, 8, line, 1)
            }
        }
        for (s in 0..3) {
            suitViews[s].setOnClickListener {
                sel[0] = s
                paint()
            }
        }
        paint()
        box.addView(suitRow, lp(MATCH, WRAP, 12))

        val labels = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val rows = listOf(0..4, 5..9, 10..12)
        for (ri in rows.indices) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            for (idx in rows[ri]) {
                val tv = label(labels[idx], 16f, light, true)
                tv.gravity = Gravity.CENTER
                tv.setPadding(0, dp(13), 0, dp(13))
                tv.background = rounded(bg, 8, line, 1)
                tv.setOnClickListener {
                    if (sel[0] < 0) {
                        toast("Сначала выбери масть", "Pick a suit first")
                    } else {
                        setManual(slot, sel[0] * 13 + idx, dlg)
                    }
                }
                val p = LinearLayout.LayoutParams(0, WRAP, 1f)
                p.setMargins(dp(3), 0, dp(3), 0)
                row.addView(tv, p)
            }
            if (ri == rows.size - 1) {
                val jk = label(Text.t(ru, "Джокер", "Joker"), 14f, light, true)
                jk.gravity = Gravity.CENTER
                jk.setPadding(0, dp(13), 0, dp(13))
                jk.background = rounded(bg, 8, line, 1)
                jk.setOnClickListener { setManual(slot, 52, dlg) }
                val p = LinearLayout.LayoutParams(0, WRAP, 2f)
                p.setMargins(dp(3), 0, dp(3), 0)
                row.addView(jk, p)
            }
            box.addView(row, lp(MATCH, WRAP, 8))
        }

        if (manualCards[slot] != null) {
            val clear = label(Text.t(ru, "Убрать карту", "Remove card"), 14f, muted, true)
            clear.gravity = Gravity.CENTER
            clear.setPadding(0, dp(12), 0, dp(4))
            clear.setOnClickListener {
                manualCards[slot] = null
                manualShown = false
                dlg.dismiss()
                render()
            }
            box.addView(clear, lp(MATCH, WRAP, 8))
        }

        dlg.setContentView(box)
        dlg.show()
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.window?.setLayout((resources.displayMetrics.widthPixels * 0.92f).toInt(), WRAP)
    }
}
