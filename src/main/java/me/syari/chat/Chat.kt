package me.syari.chat

import me.syari.server.Get
import me.syari.server.Send
import me.syari.user.Users
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object Chat : Listener {

    enum class Channel(val prefix: String, val jp: String) {
        Global("&6&lGlobal", "グローバル"),
        Party("&2&lParty", "パーティー"),
        Admin("&9&lAdmin", "アドミン"),
        Private("&3&lPrivate &e{} &e<<", "プライベート")
    }

    private fun getArrow(p: Player) = "&r ${p.displayName} &b≫ &r"

    private fun getMessage(s: String): String {
        val k = Kana.conv(s)
        return when(k) {
            null -> s.substring(2..s.length)
            s -> s
            else -> "$k &b(${Get.Color.uncolor(s)})"
        }
    }

    @EventHandler
    fun on(e: AsyncPlayerChatEvent){
        e.isCancelled = true
        val p = e.player
        val u = Users.getUser(p)
        val msg = e.message
        val d = u.getParty()
        val s = msg.split(Regex("\\s+"))
        val ch = u.getChannel()
        if(s.isNotEmpty() && s[0] == "@"){
            if(s.size >= 2){
                val t = Get.player(s[1])
                if(t != null && t.isOnline){
                    if(t == p){
                        Send.msg(p, "&c自分にメッセージを送ることは出来ません")
                    } else {
                        val c = StringBuilder()
                        (2 until s.size).forEach { i -> c.append(s[i] + " ") }
                        val prefix = Channel.Private.prefix.replace("{}", t.displayName) + getArrow(p)
                        Send.msg(listOf(p, t), getMessage(c.toString()), prefix)
                    }
                } else {
                    Send.msg(p, "&cそのプレイヤーはサーバーに存在していません")
                }
            } else {
                Send.msg(p, "&a@ ID 送る内容 &fと入力することでメッセージを送れます")
            }
        } else if(ch == Channel.Admin && u.isAdmin()){
            Send.msg(Get.onlineAdmin(), getMessage(msg), Channel.Admin.prefix + getArrow(p))
        } else if(ch == Channel.Party && d != null){
            Send.msg(d.getMember(), getMessage(msg), Channel.Party.prefix + getArrow(p))
        } else {
            Send.msg(Get.onlinePlayer(), getMessage(msg), Channel.Global.prefix + getArrow(p))
        }
    }


}