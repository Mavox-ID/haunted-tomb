package free.taro.mak.call

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class Entry(
    val id: Long,
    val ts: Long,
    var title: String,
    val spread: Int,
    val question: String,
    val ids: List<Int>,
    val manual: Boolean
)

object Store {
    private const val FILE = "taro"
    private const val KEY = "history"

    fun load(ctx: Context): ArrayList<Entry> {
        val out = ArrayList<Entry>()
        try {
            val raw = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val a = o.getJSONArray("ids")
                val ids = ArrayList<Int>()
                for (k in 0 until a.length()) ids.add(a.getInt(k))
                val sp = o.getInt("spread")
                if (sp !in 0..3) continue
                if (ids.size != Spreads.all[sp].positions.size) continue
                if (ids.any { it !in 0..52 }) continue
                out.add(Entry(o.getLong("id"), o.getLong("ts"), o.optString("title", ""), sp, o.optString("q", ""), ids, o.optBoolean("manual", false)))
            }
        } catch (e: Exception) {
        }
        return out
    }

    fun save(ctx: Context, list: List<Entry>) {
        val arr = JSONArray()
        for (e in list) {
            val o = JSONObject()
            o.put("id", e.id)
            o.put("ts", e.ts)
            o.put("title", e.title)
            o.put("spread", e.spread)
            o.put("q", e.question)
            o.put("manual", e.manual)
            o.put("ids", JSONArray(e.ids))
            arr.put(o)
        }
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }
}
