package me.syari.items.item.usableitem

import me.syari.items.item.UsableItem
import me.syari.server.Get
import org.bukkit.inventory.ItemStack

class Armor(i: ItemStack?) : UsableItem(i) {
    enum class Template(internal val prefix: String, internal val percent: Boolean = false){
        Defense("&7- &6防御力 &7: &e"),
        DodgeRate("&7- &6回避率 &7: &e", true),
        BlockRate("&7- &6ブロック率 &7: &e", true),
        MaxHealth("&7- &6体力 &7: &e"),
        RegenHealth("&7- &6体力回復 &7: &e"),
        MaxMana("&7- &6最大マナ &7: &e"),
        RegenMana("&7- &6マナ回復 &7: &e"),
    }

    fun get(t: Template) = getValue(t)

    private fun getStringValue(s: Template) : String {
        for(l in Get.Color.uncolor(lore)){
            if(l.startsWith(s.prefix)){
                return Get.Color.uncolor(l.substring(s.prefix.length + 1, l.length - if(s.percent) 1 else 0))
            }
        }
        return ""
    }

    private fun getValue(s: Template) : Pair<Double, Boolean> {
        val v = getStringValue(s)
        return Pair(Get.Number.double(v), s.percent)
    }
}