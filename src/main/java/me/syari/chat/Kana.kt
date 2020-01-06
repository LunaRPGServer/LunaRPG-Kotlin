package me.syari.chat

object Kana{
    private val list = mapOf(
            "" to arrayOf("あ","い","う","え","お"),
            "k" to arrayOf("か","き","く","け","こ"),
            "s" to arrayOf("さ","し","す","せ","そ"),
            "t" to arrayOf("た","ち","つ","て","と"),
            "n" to arrayOf("な","に","ぬ","ね","の"),
            "h" to arrayOf("は","ひ","ふ","へ","ほ"),
            "m" to arrayOf("ま","み","む","め","も"),
            "y" to arrayOf("や","い","ゆ","いぇ","よ"),
            "r" to arrayOf("ら","り","る","れ","ろ"),
            "w" to arrayOf("わ","うぃ","う","うぇ","を"),
            "g" to arrayOf("が","ぎ","ぐ","げ","ご"),
            "z" to arrayOf("ざ","じ","ず","ぜ","ぞ"),
            "j" to arrayOf("じゃ","じ","じゅ","じぇ","じょ"),
            "d" to arrayOf("だ","ぢ","づ","で","ど"),
            "b" to arrayOf("ば","び","ぶ","べ","ぼ"),
            "p" to arrayOf("ぱ","ぴ","ぷ","ぺ","ぽ"),
            "gy" to arrayOf("ぎゃ","ぎぃ","ぎゅ","ぎぇ","ぎょ"),
            "gw" to arrayOf("ぐぁ","ぐぃ","ぐぅ","ぐぇ","ぐぉ"),
            "zy" to arrayOf("じゃ","じぃ","じゅ","じぇ","じょ"),
            "jy" to arrayOf("じゃ","じぃ","じゅ","じぇ","じょ"),
            "dy" to arrayOf("ぢゃ","ぢぃ","ぢゅ","ぢぇ","ぢょ"),
            "dh" to arrayOf("でゃ","でぃ","でゅ","でぇ","でょ"),
            "dw" to arrayOf("どぁ","どぃ","どぅ","どぇ","どぉ"),
            "by" to arrayOf("びゃ","びぃ","びゅ","びぇ","びょ"),
            "py" to arrayOf("ぴゃ","ぴぃ","ぴゅ","ぴぇ","ぴょ"),
            "v" to arrayOf("ゔぁ","ゔぃ","ゔ","ゔぇ","ゔぉ"),
            "vy" to arrayOf("ゔゃ","ゔぃ","ゔゅ","ゔぇ","ゔょ"),
            "sh" to arrayOf("しゃ","し","しゅ","しぇ","しょ"),
            "sh" to arrayOf("しゃ","し","しゅ","しぇ","しょ"),
            "c" to arrayOf("か","し","く","せ","こ"),
            "ch" to arrayOf("ちゃ","ち","ちゅ","ちぇ","ちょ"),
            "cy" to arrayOf("ちゃ","ち","ちゅ","ちぇ","ちょ"),
            "f" to arrayOf("ふぁ","ふぃ","ふ","ふぇ","ふぉ"),
            "fw" to arrayOf("ふゃ","ふぃ","ふゅ","ふぇ","ふょ"),
            "q" to arrayOf("くぁ","くぃ","く","くぇ","くぉ"),
            "cy" to arrayOf("きゃ","きぃ","きゅ","きぇ","きょ"),
            "kw" to arrayOf("くぁ","くぃ","く","くぇ","くぉ"),
            "ty" to arrayOf("ちゃ","ちぃ","ちゅ","ちぇ","ちょ"),
            "ts" to arrayOf("つぁ","つぃ","つ","つぇ","つぉ"),
            "th" to arrayOf("てゃ","てぃ","てゅ","てぇ","てょ"),
            "tw" to arrayOf("とぁ","とぃ","とぅ","とぇ","とぉ"),
            "ny" to arrayOf("にゃ","にぃ","にゅ","にぇ","にょ"),
            "hy" to arrayOf("ひゃ","ひぃ","ひゅ","ひぇ","ひょ"),
            "my" to arrayOf("みゃ","みぃ","みゅ","みぇ","みょ"),
            "ry" to arrayOf("りゃ","りぃ","りゅ","りぇ","りょ"),
            "l" to arrayOf("ぁ","ぃ","ぅ","ぇ","ぉ"),
            "x" to arrayOf("ぁ","ぃ","ぅ","ぇ","ぉ"),
            "ly" to arrayOf("ゃ","ぃ","ゅ","ぇ","ょ"),
            "xy" to arrayOf("ゃ","ぃ","ゅ","ぇ","ょ"),
            "wy" to arrayOf("わ","ゐ","う","ゑ","を"),
            "lt" to arrayOf("た","ち","っ","て","と"),
            "xt" to arrayOf("た","ち","っ","て","と"),
            "lk" to arrayOf("ゕ","き","く","ゖ","こ"),
            "xk" to arrayOf("ゕ","き","く","ゖ","こ"),
            "wh" to arrayOf("うぁ","うぃ","う","うぇ","うぉ")
    )

    private fun getKana(s: String, n: Int): String{
        return list[s]?.get(n) ?: s + list[""]?.get(n)
    }

    private fun getVowel(s: Char): Int{
        return when(s){
            'a' -> 0
            'i' -> 1
            'u' -> 2
            'e' -> 3
            'o' -> 4
            else -> -1
        }
    }

    fun conv(b: String): String? {
        if(b.startsWith(".")) return null
        var l = ""
        val r = StringBuilder()
        for(i in 0 until b.length){
            val t = b.substring(i, i+1).toCharArray()[0]
            val n = getVowel(t)
            if(n != -1){
                r.append(getKana(l, n))
                l = ""
            } else {
                if(l == "n" && t != 'y'){
                    r.append("ん")
                    l = ""
                    if(t == 'n') continue
                }
                if(t in 'a'..'z'){
                    if(l == t.toString()){
                        r.append("っ")
                        l = t.toString()
                    } else {
                        l += t
                    }
                } else if(t in 'A'..'Z'){
                    r.append(l + t)
                    l = ""
                } else {
                    r.append(l + when(t){
                        '-' -> 'ー'
                        ',' -> '、'
                        '?' -> '？'
                        '!' -> '！'
                        '[' -> '「'
                        ']' -> '」'
                        '<' -> '＜'
                        '>' -> '＞'
                        '&' -> '＆'
                        '"' -> '”'
                        '　' -> " "
                        else -> t
                    })
                    l = ""
                }
            }
        }
        r.append(l)
        return r.toString()
    }
}