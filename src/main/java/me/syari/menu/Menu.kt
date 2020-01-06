package me.syari.menu

import me.syari.menu.party.PartyIcon
import me.syari.menu.party.PartyInvite
import me.syari.menu.party.PartyJoin
import me.syari.menu.party.PartyList
import me.syari.server.Get
import me.syari.trade.Trade
import me.syari.user.Users
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object Menu : Listener{

    enum class Inv(val title: String, val line: Int, val onlyAdmin: Boolean){
        MainMenu("メニュー", 4, false),
        AdminMenu("アドミンメニュー", 4, true),
        PartyJoin("パーティー加入", 4, false),
        PartyInvite("パーティー招待",4, false),
        PartyList("パーティー一覧",4, false),
        PartyIcon("パーティーアイコン", 4, false),
        Trade("トレード", 4, false),
        ItemCreate("アイテム作成", 4, true),
        Sell("アイテム売却", 4, true),
    }

    fun getMenu(i: Inv) : Inventory{
        val inv = Bukkit.createInventory(null, i.line * 9, Get.Color.color("&0&l" + i.title))
        for(v in 0..9){
            inv.setItem(((i.line - 1) * 9) + v, Get.Item.item("&0", Material.GRAY_STAINED_GLASS_PANE))
        }
        return inv
    }

    @EventHandler
    fun on(e: PlayerInteractEvent) {
        if (isMenu(e.item)) {
            e.isCancelled = true
            val p = e.player
            if(Users.getUser(p).isAdmin() && p.isSneaking){
                AdminMenu.open(p)
            } else {
                MainMenu.open(p)
            }
        }
    }

    fun getMenu() = Get.Item.item("&6&lメニュー", Material.BOOK, "&a右クリックでメニューを開く")

    private fun isMenu(i: ItemStack?) = i != null && i.hasItemMeta() && Get.Color.uncolor(i.itemMeta.displayName) == Get.Color.uncolor(getMenu().itemMeta.displayName)

    fun isSame(e: InventoryClickEvent) = e.inventory == e.clickedInventory

    fun equalOpenInventoryTitleAndMenu(p: Player, menu: Menu.Inv) = Get.Color.uncolor(p.openInventory.title) == menu.title

    @EventHandler
    fun on(e: InventoryClickEvent) {
        when (getInv(e.inventory)) {
            Inv.MainMenu -> MainMenu.click(e)
            Inv.AdminMenu -> AdminMenu.click(e)
            Inv.PartyInvite -> PartyInvite.click(e)
            Inv.PartyJoin -> PartyJoin.click(e)
            Inv.PartyList -> PartyList.click(e)
            Inv.PartyIcon -> PartyIcon.click(e)
            Inv.Trade -> Trade.clickEvent(e)
            Inv.ItemCreate -> ItemCreate.click(e)
        }
    }

    private fun getInv(inv: Inventory?): Inv? {
        return if(inv == null || inv.name == null) null
        else Inv.values().firstOrNull { v -> v.title == Get.Color.uncolor(inv.name)}
    }

    /* Template
    import me.syari.menu.MainMenu
    import me.syari.menu.Menu
    import me.syari.server.Get
    import me.syari.user.Users
    import org.bukkit.Material
    import org.bukkit.entity.Player
    import org.bukkit.event.inventory.InventoryClickEvent
    import org.bukkit.inventory.Inventory

    object ? {
        private val menu = Menu.Inv.?
        private enum class Item(val slot: Int, val display: String){
            Back(menu.line * 9 - 4, "&f&l戻る")
        }

        private fun Inventory.set(item: Item, material: Material, vararg lore: String){
            setItem(item.slot, Get.Item.item(item.display, material, *lore))
        }

        fun open(p: Player){
            val u = Users.getUser(p)
            if(menu.onlyAdmin && ! u.isAdmin()) return
            val inv = Menu.getMenu(menu)
            p.openInventory(inv)
        }

        fun click(e: InventoryClickEvent){
            e.isCancelled = true
            if (Menu.isSame(e)) {
                val p = e.whoClicked as Player
                val u = Users.getUser(p)
                val item = getClick(e)
                when(item) {
                    Item.Back -> {
                        MainMenu.open(p)
                    }
                }
            }
        }

        private fun getClick(e: InventoryClickEvent) : Item? {
            val slot = Get.Inv.targetSlot(e)
            return Item.values().firstOrNull { v -> v.slot == slot }
        }
    }
     */
}