package me.syari.trade

import me.syari.server.Get
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitRunnable


class TradeData(val player: Player) {

    private var invite = mutableListOf<Player>()
    var partner = player
    private var canInvite = true

    fun nowTrade() : Boolean{
        return this.partner != player
    }

    fun getInvite() : List<Player> {
        return invite
    }

    fun canInvite() : Boolean {
        return this.canInvite
    }

    fun addInvite(t: Player){
        invite.add(t)
        this.canInvite = false
        object : BukkitRunnable() {
            override fun run() {
                canInvite = true
            }
        }.runTaskLater(Get.plugin(), 100)
    }

    fun hasInvite(t: Player) : Boolean{
        return invite.contains(t)
    }

    fun removeInvite(t: Player){
        this.invite.remove(t)
    }

    fun isWait() : Boolean {
        return (player.openInventory?.topInventory != null && player.openInventory.topInventory.getItem(12)?.type == Material.LIME_DYE)
    }

    fun setWait(){
        val pInv = player.openInventory.topInventory
        if(nowTrade()){
            val tInv = partner.openInventory.topInventory
            if(isTradeInv(pInv) && isTradeInv(tInv)) {
                if (isWait()) {
                    pInv.setItem(12, Get.Item.item("&a設定中", Material.GRAY_DYE, "&6クリックで決定"))
                    tInv.setItem(14, Get.Item.item("&a設定中", Material.GRAY_DYE, "&6クリックで決定"))
                } else {
                    pInv.setItem(12, Get.Item.item("&a待機中", Material.LIME_DYE, "&6クリックで設定"))
                    tInv.setItem(14, Get.Item.item("&a待機中", Material.LIME_DYE, "&6クリックで設定"))
                }
            }
        }
    }

    fun setItem(s: Int){
        object : BukkitRunnable() {
            override fun run() {
                val pInv = player.openInventory.topInventory
                if (nowTrade()) {
                    val tInv = partner.openInventory.topInventory
                    if (isTradeInv(pInv) && isTradeInv(tInv)) {
                        val item = pInv.getItem(s)
                        if (item != null) {
                            tInv.setItem(s + 6, item)
                        }
                    }
                }
            }
        }.runTaskLater(Get.plugin(), 1)
    }

    private fun isTradeInv(inv: Inventory?) : Boolean {
        return (inv != null) && (inv.name != null) && (Get.Color.uncolor(inv.name) == "トレード")
    }
}