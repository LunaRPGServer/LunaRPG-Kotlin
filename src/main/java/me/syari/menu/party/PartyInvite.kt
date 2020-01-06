package me.syari.menu.party

import me.syari.menu.Menu
import me.syari.server.Get
import me.syari.server.Send
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object PartyInvite {
    private val menu = Menu.Inv.PartyInvite
    private val DisplayPlayerNumber = (menu.line - 1) * 9
    private enum class Item(val slot: Int, val display: String){
        Prev(menu.line * 9 - 9, "&f&l<<"),
        Back(menu.line * 9 - 4, "&f&l戻る"),
        Next(menu.line * 9 - 1, "&f&l>>")
    }

    private fun Inventory.set(item: Item, material: Material, vararg lore: String){
        setItem(item.slot, Get.Item.item(item.display, material, *lore))
    }

    fun open(p: Player, page : Int = 1){
        val u = Users.getUser(p)
        if(menu.onlyAdmin && ! u.isAdmin()) return
        val inv = Menu.getMenu(menu)
        val d = u.getParty() ?: return
        var c = 0
        for(o in Get.onlinePlayer().filter{ g -> ! Users.getUser(g).isPartyBelong() && ! d.hasInvite(g) }.withIndex()) {
            if(c < DisplayPlayerNumber && ((page - 1) * DisplayPlayerNumber) <= o.index){
                inv.setItem(c, Get.Item.head("&6${o.value.name}", o.value))
                c ++
            }
        }
        if(page > 1){
            inv.set(Item.Prev, Material.WHITE_STAINED_GLASS_PANE, "&a${page - 1} &7ページ")
        }
        if((c + 1) == DisplayPlayerNumber){
            inv.set(Item.Next, Material.WHITE_STAINED_GLASS_PANE, "&a${page + 1} &7ページ")
        }
        inv.set(Item.Back, Material.RED_STAINED_GLASS_PANE)
        p.openInventory(inv)
    }

    fun click(e: InventoryClickEvent){
        e.isCancelled = true
        if (Menu.isSame(e)) {
            val p = e.whoClicked as Player
            val u = Users.getUser(p)
            val d = u.getParty() ?: return
            val slot = Get.Inv.targetSlot(e)
            val inv = e.inventory ?: return
            val item = getClick(e)
            if(slot in 0..(DisplayPlayerNumber - 1)){
                val t = Get.player(Get.Inv.clickSlotDisplayName(e))
                if(t != null && !d.hasInvite(t) && d.getInvite().size <= PartyList.maxInvite){
                    d.addInvite(t)
                    reopen(p)
                    PartyList.reopen(d.getMember())
                    PartyJoin.reopen(listOf(t))
                    Send.msg(p, "&6${t.name} &fをパーティーに招待しました")
                    Send.msg(t, "&6${p.name} &fのパーティーに招待されました")
                }
                return
            }
            when(item) {
                Item.Prev -> {
                    val txt = Get.Inv.slotLore(inv, slot).getOrNull(0) ?: return
                    val page = Get.Number.int(txt)
                    open(p, page)
                }
                Item.Next -> {
                    val txt = Get.Inv.slotLore(inv, slot).getOrNull(0) ?: return
                    val page = Get.Number.int(txt)
                    open(p, page)
                }
                Item.Back -> {
                    PartyList.open(p)
                }
            }
        }
    }

    fun reopen(p: Player){
        if(Menu.equalOpenInventoryTitleAndMenu(p, menu)){
            val txt = Get.Inv.slotLore(p.openInventory.topInventory, Item.Next.slot).getOrNull(0) ?: return
            val nextPage = Get.Number.int(txt)
            open(p, nextPage - 1)
        }
    }

    private fun getClick(e: InventoryClickEvent) : Item? {
        val slot = Get.Inv.targetSlot(e)
        return Item.values().firstOrNull { v -> v.slot == slot }
    }
}