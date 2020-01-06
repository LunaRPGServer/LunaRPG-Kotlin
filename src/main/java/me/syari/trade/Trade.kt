package me.syari.trade

import me.syari.items.Item
import me.syari.menu.Menu
import me.syari.server.Get
import me.syari.server.GiveItem
import me.syari.server.Send
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

object Trade : Listener {

    @EventHandler
    fun on(e: PlayerInteractEntityEvent){
        if(e.rightClicked is Player && e.player.isSneaking
                && e.player.inventory.itemInMainHand != null && e.player.inventory.itemInMainHand.type == Material.AIR){
            val p = e.player
            val dp = Users.getUser(p).getTrade()
            val t = e.rightClicked as Player
            val dt = Users.getUser(t).getTrade()
            if(!dt.nowTrade() && !dp.hasInvite(t) && !dp.nowTrade() && dp.canInvite()){
                dp.addInvite(t)
                if(dt.hasInvite(p)){
                    startTrade(p, t)
                    startTrade(t, p)
                } else {
                    Send.msg(t, "&6" + p.name + " &fからトレード申請が来ました")
                    Send.msg(p, "&6" + t.name + " &fにトレード申請を出しました")
                    object : BukkitRunnable() {
                        override fun run() {
                            if(dp.hasInvite(t)){
                                dp.removeInvite(t)
                                Send.msg(p, "&6" + t.name + " &fへのトレード申請がキャンセルされました")
                            }
                        }
                    }.runTaskLater(Get.plugin(), 3600)
                }
            }
        }
    }

    // @EventHandler
    fun clickEvent(e: InventoryClickEvent){
        if(isTradeInv(e.inventory)){
            val p = e.whoClicked as Player
            val dp = Users.getUser(p).getTrade()
            val t = dp.partner
            val dt = Users.getUser(t).getTrade()
            if(dp.isWait() || e.click.isShiftClick || e.click == ClickType.DOUBLE_CLICK) {
                e.isCancelled = true
            }
            if(Menu.isSame(e)) {
                val slot = Get.Inv.targetSlot(e)
                if (isMySelfSlot(slot)) {
                    dp.setItem(slot)
                    if(!(Item(e.currentItem).canTrade && Item(e.cursor).canTrade)){
                        e.isCancelled = true
                    }
                } else {
                    e.isCancelled = true
                    if (slot == 12) {
                        dp.setWait()
                        if (dp.isWait() && dt.isWait()) {
                            stopTrade(p, false)
                            stopTrade(t, false)
                        }
                    } else if(slot == 21){
                        stopTrade(p)
                        stopTrade(t)
                    }
                }
            }
        }
    }

    @EventHandler
    fun on(e: InventoryCloseEvent){
        if(isTradeInv(e.inventory)){
            val p = e.player as Player
            val dp = Users.getUser(p).getTrade()
            val t = dp.partner
            if(dp.nowTrade()){
                stopTrade(p)
                stopTrade(t)
            }
        }
    }

    private fun startTrade(p: Player, t: Player){
        val dp = Users.getUser(p).getTrade()
        dp.partner = t
        val inv = Menu.getMenu(Menu.Inv.Trade)
        inv.setItem(3, Get.Item.head("&6" + p.name, p))
        inv.setItem(12, Get.Item.item("&a設定中", Material.GRAY_DYE, "&6クリックで決定"))
        inv.setItem(21, Get.Item.item("&cキャンセル", Material.BARRIER))
        listOf(4, 13, 22).forEach { i -> inv.setItem(i, Get.Item.item("&0", Material.GRAY_STAINED_GLASS_PANE)) }
        inv.setItem(5, Get.Item.head("&6" + t.name, t))
        inv.setItem(14, Get.Item.item("&a設定中", Material.GRAY_DYE, "&6クリックで決定"))
        inv.setItem(23, Get.Item.item("&cキャンセル", Material.BARRIER))
        p.openInventory(inv)
    }

    private fun stopTrade(p: Player, c: Boolean = true){
        val inv = p.openInventory.topInventory
        if(isTradeInv(inv)) {
            val items = mutableListOf<ItemStack>()
            if (c) {
                Send.msg(p, "トレードがキャンセルされました")
                getMySelfSlot()
            } else {
                Send.msg(p, "トレードが完了しました")
                getPartnerSlot()
            }.forEach { i ->
                if(inv.getItem(i) != null) {
                    items.add(inv.getItem(i))
                }
            }
            GiveItem.give(p, items)
            Users.getUser(p).clearTrade()
            p.closeInventory()
        }
    }

    private  fun isMySelfSlot(s: Int) : Boolean {
        return getMySelfSlot().contains(s)
    }

    private fun getMySelfSlot() : List<Int>{
        return listOf(0, 1, 2, 9, 10, 11, 18, 19, 20)
    }

    private fun getPartnerSlot() : List<Int>{
        return listOf(6, 7, 8, 15, 16, 17, 24, 25, 26)
    }

    private fun isTradeInv(inv: Inventory?) : Boolean {
        return (inv != null) && (inv.name != null) && (Get.Color.uncolor(inv.name) == "トレード")
    }

    fun deleteInvitePlayer(p: Player){
        for(d in Users.getUsers().filter { u -> u.getTrade().getInvite().contains(p) }){
            d.getTrade().removeInvite(p)
        }
    }
}