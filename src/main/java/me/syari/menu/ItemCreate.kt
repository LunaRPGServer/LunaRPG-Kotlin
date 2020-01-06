package me.syari.menu

import me.syari.server.Get
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ItemCreate {
    private val menu = Menu.Inv.ItemCreate
    private enum class Item(val slot: Int, val display: String){
        CreatedItem(3, ""),
        Display(10, "&d表示名"),
        Material(12, "&dアイテム"),
        Type(14, "&dタイプ"),
        Desc(16, "&d説明文"),
        Back(menu.line * 9 - 4, "&f&l戻る")
    }

    private fun Inventory.set(item: Item, material: Material, vararg lore: String){
        setItem(item.slot, Get.Item.item(item.display, material, *lore))
    }

    fun open(p: Player){
        val u = Users.getUser(p)
        if(menu.onlyAdmin && ! u.isAdmin()) return
        val inv = Menu.getMenu(menu)
        inv.setItem(3, getData(p).create())
        inv.set(Item.Display, Material.BLUE_STAINED_GLASS_PANE)
        inv.set(Item.Material, Material.BLUE_STAINED_GLASS_PANE)
        inv.set(Item.Type, Material.BLUE_STAINED_GLASS_PANE)
        inv.set(Item.Desc, Material.BLUE_STAINED_GLASS_PANE)
        inv.set(Item.Back, Material.RED_STAINED_GLASS_PANE)
        p.openInventory(inv)
    }

    fun click(e: InventoryClickEvent){
        e.isCancelled = true
        if (Menu.isSame(e)) {
            val p = e.whoClicked as Player
            val u = Users.getUser(p)
            val c = Get.Inv.targetSlot(e)
            val inv = e.inventory
            val item = getClick(e)
            when(item) {
                Item.Display -> {
                    p.inventory.addItem(inv.getItem(c))
                }
                Item.Back -> {
                    AdminMenu.open(p)
                }
            }
        }
    }

    private fun getClick(e: InventoryClickEvent) : Item? {
        val slot = Get.Inv.targetSlot(e)
        return Item.values().firstOrNull { v -> v.slot == slot }
    }

    enum class Type(val jp: String, val color: Get.Color.ColorList){
        None("未設定", Get.Color.ColorList.White),
        Mineral("鉱物", Get.Color.ColorList.Gray),
        Plant("植物", Get.Color.ColorList.Lime),
        Food("食料", Get.Color.ColorList.Yellow),
        Fish("魚", Get.Color.ColorList.Aqua),
        Important("重要", Get.Color.ColorList.Pink);

        companion object {
            fun fromJP(s: String) = Type.values().firstOrNull { t -> t.jp == s }
        }
    }

    private val data = mutableMapOf<Player, ItemData>()
    fun getData(p: Player) = data.getOrPut(p) { ItemData() }
    fun deleteData(p: Player) {
        data.remove(p)
    }

    fun toItemData(i: ItemStack){
        var type = Type.None
        val desc = mutableListOf<String>()
        val r = "アイテムタイプ: .*".toRegex()
        for(l in i.itemMeta.lore){
            val g = r.matchEntire(l)?.groupValues
            if(g != null){
                type = Type.fromJP(g[1]) ?: continue
            } else {
                desc.add(l)
            }
        }
        ItemData(i.itemMeta.displayName, i.type, type, *desc.toTypedArray())
    }

    class ItemData(private var display: String = "",
                   private var material: Material = Material.STONE,
                   private var type: Type = Type.None,
                   private vararg var desc: String) {

        fun create() : ItemStack {
            return Get.Item.item("${type.color}$display", material, getType(type), *desc)
        }

        fun getDisplay() = display
        fun getMaterial() = material
        fun getType() = type
        fun getDesc() = desc

        fun setDisplay(s: String){
            display = s
        }

        fun setMaterial(m: Material){
            material = m
        }

        fun setType(t: Type){
            type = t
        }

        fun setDesc(vararg d: String){
            desc = d
        }

        private fun getType(type: Type) = "&7アイテムタイプ: ${type.color}${type.jp}"
    }
}