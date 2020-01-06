package me.syari.server

import com.sucy.skill.SkillAPI
import me.syari.menu.party.PartyList
import me.syari.user.Users
import net.minecraft.server.v1_13_R2.ChatComponentText
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

object Board {
    object Score {
        fun set(p: Player){
            val u = Users.getUser(p)
            val m = Bukkit.getScoreboardManager()
            val b = m.newScoreboard
            val o = b.registerNewObjective("Sidebar", "", Get.Color.color("&e★&6&l LunaRPG &e★   "))
            o.displaySlot = DisplaySlot.SIDEBAR
            o.getScore(Get.Color.color("&a&m----------------------&f")).score = 15
            o.getScore(Get.Color.color("&e - &6&lName")).score = 14
            o.getScore(Get.Color.color("&e&l  ${p.name}")).score = 13
            o.getScore(Get.Color.color("&e - &6&lOnline")).score = 12
            o.getScore(Get.Color.color("&e&l  ${Bukkit.getOnlinePlayers().size}&7 / &e&l${Bukkit.getMaxPlayers()}")).score = 11
            o.getScore(Get.Color.color("&e - &6&lParty")).score = 10
            o.getScore(Get.Color.color("&e&l  ${u.getParty()?.getMember()?.size ?: 0}&7 / &e&l${PartyList.maxMember}")).score = 9
            o.getScore(Get.Color.color("&e - &6&lClass")).score = 8
            o.getScore(Get.Color.color("&e&l  Lv. ${
                    try {
                        Users.getUser(p).getClassData().mainClass.level
                    } catch (e: Exception) {
                        0
                    }
            }")).score = 7
            o.getScore(Get.Color.color("&e - &6&lChat")).score = 6
            o.getScore(Get.Color.color("&e&l  ${u.getChannel().jp}")).score = 5
            p.scoreboard = b
        }

        fun set(pp: List<Player>){
            pp.forEach { p -> set(p) }
        }

    }

    object Tab{
        enum class Place(val field: String) {
            Header("header"),
            Footer("footer")
        }

        fun set(p: Player, place: Place, s: String){
            val cp = p as CraftPlayer
            val connection = cp.handle.playerConnection
            val content = ChatComponentText(Get.Color.color(s))
            val packet = PacketPlayOutPlayerListHeaderFooter()
            try {
                val field = packet.javaClass.getDeclaredField(place.field)
                field.isAccessible = true
                field.set(packet, content)
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            // TODO  エラー
            //connection.sendPacket(packet)
        }
    }
}