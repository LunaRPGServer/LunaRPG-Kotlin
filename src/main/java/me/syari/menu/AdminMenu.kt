package me.syari.menu

import me.syari.server.Get
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object AdminMenu {
    private val menu = Menu.Inv.AdminMenu
    private enum class Item(val slot: Int, val display: String){
        Back(menu.line * 9 - 4, "&f&l戻る")
    }

    private fun Inventory.set(item: Item, material: Material, vararg lore: String){
        setItem(item.slot, Get.Item.item(item.display, material, *lore))
    }

    fun open(p: Player){
        val menu = Menu.Inv.AdminMenu
        val u = Users.getUser(p)
        if(menu.onlyAdmin && ! u.isAdmin()) return
        val inv = Menu.getMenu(menu)
        inv.set(Item.Back, Material.RED_STAINED_GLASS_PANE)
        p.openInventory(inv)
    }

    fun click(e: InventoryClickEvent) {
        e.isCancelled = true
        if (Menu.isSame(e)) {
            val p = e.whoClicked as Player
            val u = Users.getUser(p)
            val item = getClick(e)
            when (item) {
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