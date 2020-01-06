package me.syari.server

import com.sucy.skill.api.event.PlayerClassChangeEvent
import com.sucy.skill.api.event.PlayerLevelUpEvent
import com.vexsoftware.votifier.model.VotifierEvent
import me.syari.chat.Chat
import me.syari.menu.Menu
import me.syari.trade.Trade
import me.syari.user.Users
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.scheduler.BukkitRunnable

object Server : Listener{

    @EventHandler
    fun on(e: ServerListPingEvent){
        e.motd = Get.Color.color("""
            &6&l  LunaRPG   &a&lVersion &7: &a&l1.13.2   &a&lPlayer &7: &a&l${e.numPlayers} &7/ &a&l${e.maxPlayers}
            &e&l        Website &7:&e&l https://luna-rpg.net
        """.trimIndent())
    }

    @EventHandler
    fun on(e: PlayerJoinEvent) {
        val p = e.player
        val u = Users.getUser(p)
        e.joinMessage = Get.Color.color("&7- &a&lJoin &7&l${p.name}")
        if (!p.isOp) {
            Perm.set(p, "*", false)
        } else {
            u.setChannel(Chat.Channel.Admin) // TODO 公開前に削除
        }
        object : BukkitRunnable() {
            override fun run() {
                p.inventory.setItem(8, Menu.getMenu())
                Board.Score.set(Get.onlinePlayer())
                Board.Tab.set(p, Board.Tab.Place.Header, "&6&lLunaRPG\n&eWeb &7:&e https://luna-rpg.net")
            }
        }.runTaskLater(Get.plugin(), 1)
    }

    @EventHandler
    fun on(e: PlayerQuitEvent){
        val p = e.player
        val u = Users.getUser(p)
        e.quitMessage = Get.Color.color("&7- &c&lQuit &7&l${p.name}")
        Board.Score.set(Get.onlinePlayer())
        Trade.deleteInvitePlayer(p)
        u.clearTrade()
        u.deletePartyInvite()
        u.leaveParty()
    }

    @EventHandler
    fun on(e: PlayerLevelUpEvent){
        Board.Score.set(e.playerData.player)
    }

    @EventHandler
    fun on(e: PlayerClassChangeEvent){
        Board.Score.set(e.playerData.player)
    }

    @EventHandler
    fun on(e: VotifierEvent){
        Send.broadcast("""
                &b&m-------------------------------",
                &r   &7[&b*&7] &b${e.vote.username} &fさんが
                &r          投票しました
                &r    &b https://vote.luna-rpg.net
                &b&m-------------------------------
                """.trimIndent()
        )
    }
}