package me.syari.items

import me.syari.server.Get
import org.bukkit.inventory.ItemStack

open class Item(val i: ItemStack?){
    private enum class Template(val prefix: String, val percent: Boolean = false){
        TradeRestriction("&4交換不可"),
        SellRestriction("&4売却不可"),
    }

    val canTrade = !(startWith(Template.TradeRestriction))
    val canSell = !(startWith(Template.SellRestriction))
    val lore : List<String> = if(i != null && i.itemMeta != null && i.itemMeta.lore != null) i.itemMeta.lore else emptyList()

    private fun startWith(s: Template): Boolean {
        for(l in Get.Color.uncolor(lore)){
            if(l.startsWith(s.prefix)){
                return true
            }
        }
        return false
    }
}