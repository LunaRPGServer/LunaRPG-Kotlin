package me.syari.menu.party

import me.syari.menu.Menu
import me.syari.server.Get
import me.syari.user.Users
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

object PartyIcon {
    val icons = mapOf(
            Material.WHITE_BANNER to "&6白色の旗",
            Material.STONE_SWORD to "&6石の剣",
            Material.STONE_PICKAXE to "&6石のツルハシ",
            Material.FISHING_ROD to "&6釣竿",
            Material.MAP to "&6地図",
            Material.DANDELION to "&6タンポポ",
            Material.POPPY to "&6ポピー",
            Material.RED_MUSHROOM to "&6キノコ",
            Material.TORCH to "&6松明",
            Material.EMERALD to "&6エメラルド",
            Material.EXPERIENCE_BOTTLE to "&6エンチャントの瓶",
            Material.POTION to "&6ポーション",
            Material.APPLE to "&6リンゴ",
            Material.MELON to "&6スイカ",
            Material.COOKIE to "&6クッキー",
            Material.COD to "&6生魚",
            Material.ROTTEN_FLESH to "&6腐った肉",
            Material.BONE to "&6骨",
            Material.GRASS to "&6草",
            Material.SAND to "&6砂",
            Material.STONE to "&6石",
            Material.OAK_LOG to "&6原木",
            Material.CRAFTING_TABLE to "&6作業台"
    )

    private val menu = Menu.Inv.PartyIcon
    private enum class Item(val slot: Int, val display: String){
        Back(menu.line * 9 - 4, "&f&l戻る")
    }

    private fun Inventory.set(item: Item, material: Material, vararg lore: String){
        setItem(item.slot, Get.Item.item(item.display, material, *lore))
    }

    fun open(p: Player){
        val u = Users.getUser(p)
        if(menu.onlyAdmin && ! u.isAdmin()) return
        val d = u.getParty() ?: return
        if (d.isLeader(p)) {
            val inv = Menu.getMenu(menu)
            for (i in icons.entries.withIndex()) {
                inv.setItem(i.index, Get.Item.item(i.value.value + if(d.isIcon(i.value.key)) " &a設定中" else "", i.value.key))
            }
            inv.set(Item.Back, Material.RED_STAINED_GLASS_PANE)
            p.openInventory(inv)
        }
    }

    fun click(e: InventoryClickEvent){
        e.isCancelled = true
        if (Menu.isSame(e)) {
            val p = e.whoClicked as Player
            val u = Users.getUser(p)
            val slot = Get.Inv.targetSlot(e)
            val d = u.getParty() ?: return
            if(slot in 0..26){
                d.setIcon(Get.Inv.slotType(e.inventory, slot) ?: d.getIcon())
                PartyList.reopen(d.getMember())
                PartyJoin.reopen(if(d.isPublic()) Get.onlinePlayer() else d.getInvite())
                reopen(p)
            } else if(slot == Item.Back.slot){
                open(p)
            }
        }
    }

    fun reopen(p: Player){
        if(Menu.equalOpenInventoryTitleAndMenu(p, Menu.Inv.PartyIcon)){
            open(p)
        }
    }
}