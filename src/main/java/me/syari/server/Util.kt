package me.syari.server

import me.syari.menu.ItemCreate
import me.syari.user.Users
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.Plugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


object Get {
    object Number {
        fun double(s: String): Double{
            val r = Regex("[+-]?\\d+(?:\\.\\d+)?").find(s)?.value ?: ""
            return try {
                r.toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
        }
        fun int(s: String): Int{
            val r = Regex("[+-]?\\d").find(s)?.value ?: ""
            return try {
                r.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        }
    }
    object Color {
        enum class ColorList(val color: String){
            White("&f"),
            Blue("&9"),
            Red("&c"),
            Purple("&5"),
            Yellow("&e"),
            Gray("&8"),
            Pink("&d"),
            Aqua("&b"),
            Lime("&a")
        }

        fun color(s: String) = ChatColor.translateAlternateColorCodes('&', s)

        fun color(s: List<String>) : List<String> {
            val r = mutableListOf<String>()
            s.forEach { c -> r.add(color(c)) }
            return r
        }

        fun uncolor(s: String) = ChatColor.stripColor(color(s))

        fun uncolor(s: List<String>) : List<String> {
            val r = mutableListOf<String>()
            s.forEach { c -> r.add(uncolor(c)) }
            return r
        }
    }

    object Item {
        fun item(id: String): ItemStack? {
            val resultSet = SQL.executeQuery("Select * From Item where ID = '$id'") ?: return null
            resultSet.next()
            return ItemCreate.create(
                    resultSet.getString("Display"),
                    Material.getMaterial(resultSet.getString("Material")),
                    ItemCreate.getType(resultSet.getString("Type")),
                    resultSet.getString("Description")
            )
        }

        fun item(display: String, material: Material, vararg desc: String) : ItemStack{
            val i = ItemStack(material, 1)
            val im = i.itemMeta
            im.displayName = Color.color(display)
            im.lore = Color.color(desc.asList())
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
            i.itemMeta = im
            return i
        }

        fun head(n: String, p: Player, vararg  desc: String) : ItemStack{
            val i = item(n, Material.PLAYER_HEAD, *desc)
            val s : SkullMeta = i.itemMeta as SkullMeta
            s.owningPlayer = p
            i.itemMeta = s
            return i
        }
    }

    object Inv {
        fun clickSlotDisplayName(e: InventoryClickEvent): String {
            val slot = targetSlot(e)
            val s = e.inventory.getItem(slot)?.itemMeta?.displayName ?: return ""
            return Get.Color.uncolor(s)
        }

        fun slotType(inv: Inventory, slot: Int): Material? {
            return inv.getItem(slot)?.type
        }

        fun slotLore(inv: Inventory, slot: Int): List<String> {
            val l = inv.getItem(slot)?.itemMeta?.lore ?: return listOf()
            return Get.Color.uncolor(l)
        }

        fun targetSlot(e: InventoryClickEvent) = if (e.click == ClickType.NUMBER_KEY) e.rawSlot else e.slot
    }

    fun player(s: String) : Player? = Bukkit.getPlayer(s)

    fun onlineAdmin() = Bukkit.getOnlinePlayers().filter { p -> Users.getUser(p).isAdmin() }.toList()

    fun onlinePlayer()  = Bukkit.getOnlinePlayers().toList()

    fun plugin() : Plugin = Bukkit.getServer().pluginManager.getPlugin("LunaRPG")
}

object Send {
    fun msg(p: Player, msg: String, prefix: String = "&7[&a#&7]&r ") {
        p.sendMessage(Get.Color.color(prefix + msg))
    }

    fun msg(pp: List<Player>, msg: String, prefix: String = "&7[&a#&7]&r ") {
        pp.forEach { p -> p.sendMessage(Get.Color.color(prefix + msg)) }
    }

    fun broadcast(vararg msg: String){
        for(m in msg){
            Bukkit.broadcastMessage(Get.Color.color(m))
        }
    }
}

object GiveItem {
    fun give(p: Player, items: List<ItemStack?>) {
        var dropped = false
        for (item in items) {
            if (item != null) {
                if (p.inventory.firstEmpty() == -1) {
                    drop(p.location, listOf(item), listOf(p))
                    if(!dropped) dropped = true
                } else {
                    p.inventory.addItem(item)
                }
            }
        }
        if(dropped){
            Send.msg(p, "&cアイテムを全て受け取ることが出来ませんでした")
        }
    }

    fun drop(loc: Location, items: List<ItemStack>, pp: List<Player>){
        for(item in items){
            val i = loc.world.dropItemNaturally(loc, item)
            pp.forEach { p -> i.addScoreboardTag(p.uniqueId.toString()) }
        }
    }
}

object Perm {
    fun set(p: Player, perm: String, bool: Boolean) {
        p.addAttachment(Get.plugin()).setPermission(perm, bool)
    }

    fun unset(p: Player, perm: String) {
        p.addAttachment(Get.plugin()).unsetPermission(perm)
    }
}

object SQL {
    private const val host = "play.luna-rpg.net"
    private const val database = "LunaRPG"
    private const val username = "LunaRPG"
    private const val password = "Cv_bn&5"
    private const val port = 3306

    private fun getConnection() = DriverManager.getConnection("jdbc:mysql://$host:$port/$database", username, password)
    private fun getStatement(connection: Connection?) = connection?.createStatement()

    fun createTable(){
        val connection = getConnection()
        val statement = getStatement(connection)
        statement?.executeUpdate("CREATE TABLE IF NOT EXISTS $database.Town (Name VARCHAR(255), World VARCHAR(255), X FLOAT(10, 1), Y FLOAT(10, 1), Z FLOAT(10, 1));")
        statement?.executeUpdate("CREATE TABLE IF NOT EXISTS $database.Item (ID VARCHAR(255), Display VARCHAR(255), Material VARCHAR(255), Type VARCHAR(255), Description VARCHAR(255));")
        statement?.executeUpdate("CREATE TABLE IF NOT EXISTS $database.Fish (World VARCHAR(255), X FLOAT(10, 1), Y FLOAT(10, 1), Z FLOAT(10, 1), XZr FLOAT(10, 1), Yr FLOAT(10, 1), Value TINYINT, Item VARCHAR(255));")
        connection.close()
    }

    fun executeQuery(s: String) : ResultSet? {
        val connection = getConnection()
        val statement = getStatement(connection)
        val res = statement?.executeQuery(s)
        statement?.executeQuery(s)
        connection.close()
        return res
    }

    fun executeUpdate(s: String){
        val connection = getConnection()
        val statement = getStatement(connection)
        statement?.executeUpdate(s)
        connection.close()
    }
}