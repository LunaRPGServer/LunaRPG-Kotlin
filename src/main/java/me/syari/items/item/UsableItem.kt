package me.syari.items.item

import me.syari.items.Item
import me.syari.server.Get
import org.bukkit.inventory.ItemStack

open class UsableItem(i: ItemStack?) : Item(i){
    enum class Template(internal val prefix: String, internal val percent: Boolean = false){
        Rarity("&6レア度 &7: "),
        LevelRestriction("&6レベル制限 &7: &e"),
        NeedSkill("&6必要スキル &7: &e"),
        UseMana("&7- &6マナ消費 &7: &e", true),
        ExpBonus("&7- &6経験値 &7: &e", true),
    }

    enum class Rarity(val color : Get.Color.ColorList){
        Common(Get.Color.ColorList.White),
        Uncommon(Get.Color.ColorList.Blue),
        Rare(Get.Color.ColorList.Red),
        Epic(Get.Color.ColorList.Purple),
        Legendary(Get.Color.ColorList.Yellow),
        Bad(Get.Color.ColorList.Gray),
        Unique(Get.Color.ColorList.Pink),
        Goddess(Get.Color.ColorList.Aqua),
    }

    val needLevel = getValue(Template.LevelRestriction)
    val needSkill = getStringValue(Template.NeedSkill)
    val useMana = getValue(Template.UseMana)
    val expBonus = getValue(Template.ExpBonus)

    fun getRarity() : Rarity? {
        val r = getStringValue(Template.Rarity)
        return Rarity.values().firstOrNull { v -> Get.Color.uncolor(v.color.color) == r }
    }

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