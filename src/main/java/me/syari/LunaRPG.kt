package me.syari

import me.syari.battle.Drop
import me.syari.battle.Exp
import me.syari.chat.Chat
import me.syari.collect.Fish
import me.syari.discord.Bot
import me.syari.discord.LogServerAppender
import me.syari.items.Battle
import me.syari.items.EquipLoad
import me.syari.menu.Menu
import me.syari.server.SQL
import me.syari.server.Server
import me.syari.trade.Trade
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

object LunaRPG : JavaPlugin() {

    override fun onEnable() {
        SQL.createTable()
        addListener(Menu, Trade, Server, Chat, Drop, Exp, EquipLoad, Battle, Fish)
        LogServerAppender
        Bot.setup()
    }

    private fun addListener(vararg list: Listener){
        list.forEach { u -> server.pluginManager.registerEvents(u, this) }
    }
}