package me.syari.menu

import me.syari.chat.Chat
import me.syari.menu.party.PartyJoin
import me.syari.menu.party.PartyList
import me.syari.server.Board
import me.syari.server.Get
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object MainMenu {
    private val menu = Menu.Inv.MainMenu
    private enum class Item(val slot: Int, val display: String){
        Skill(0, "&6スキル"),
        Quest(2, "&6クエスト"),
        Party(4, "&6パーティー"),
        Chat(6, "&6チャット"),
        Back(menu.line * 9 - 4, "&f&l戻る")
    }

    private fun Inventory.set(item: Item, material: Material, vararg lore: String){
        setItem(item.slot, Get.Item.item(item.display, material, *lore))
    }

    fun open(p: Player){
        val u = Users.getUser(p)
        if(menu.onlyAdmin && ! u.isAdmin()) return
        val inv = Menu.getMenu(menu)
        inv.set(Item.Skill, Material.IRON_SWORD, "&f >> &aスキル選択 ")
        inv.set(Item.Quest, Material.EXPERIENCE_BOTTLE, " &f >> &aクエスト確認 ")
        inv.set(Item.Party, if (u.isPartyBelong()) Material.BOOK else Material.WRITABLE_BOOK, "&f >> &a${if (u.isPartyBelong()) "パーティー一覧・設定" else "パーティー加入"}")
        inv.set(Item.Chat, Material.FEATHER, "&7現在の設定 : &a${u.getChannel().jp}")
        inv.set(Item.Back, Material.RED_STAINED_GLASS_PANE)
        p.openInventory(inv)
        /*
        リンク
        ゴミ箱
         */
    }

    fun click(e: InventoryClickEvent){
        e.isCancelled = true
        if (Menu.isSame(e)) {
            val p = e.whoClicked as Player
            val u = Users.getUser(p)
            val item = getClick(e)
            when(item) {
                Item.Skill -> {

                }
                Item.Quest -> {

                }
                Item.Party -> {
                    if (u.isPartyBelong()) {
                        PartyList.open(p)
                    } else {
                        PartyJoin.open(p)
                    }
                }
                Item.Chat -> {
                    val ch = u.getChannel()
                    u.setChannel(if (u.isAdmin()) {
                        if (ch == Chat.Channel.Global) {
                            Chat.Channel.Admin
                        } else if (ch == Chat.Channel.Admin && u.isPartyBelong()) {
                            Chat.Channel.Party
                        } else {
                            Chat.Channel.Global
                        }
                    } else {
                        if (ch == Chat.Channel.Global && u.isPartyBelong()) {
                            Chat.Channel.Party
                        } else {
                            Chat.Channel.Global
                        }
                    })
                    open(p)
                    Board.Score.set(p)
                }
                Item.Back -> {
                    e.whoClicked.closeInventory()
                }
            }
        }
    }

    private fun getClick(e: InventoryClickEvent) : Item? {
        val slot = Get.Inv.targetSlot(e)
        return Item.values().firstOrNull { v -> v.slot == slot }
    }
}