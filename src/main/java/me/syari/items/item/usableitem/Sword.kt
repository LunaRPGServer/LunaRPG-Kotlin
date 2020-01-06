package me.syari.items.item.usableitem

import me.syari.items.item.UsableItem
import me.syari.server.Get
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Sword(i: ItemStack?): UsableItem(i){
    enum class Template(internal val prefix: String, internal val percent: Boolean = false){
        Damage("&7- &6攻撃力 &7: &e"),
        CriticalRate("&7- &6会心率 &7: &e", true),
        CriticalDamage("&7- &6会心攻撃力 &7: &e", true),
        HitRate("&7- &6命中率 &7: &e", true),
        AttackSpeed("&7- &6攻撃速度 &7: &e"),
        CounterRate("&7- &6反撃率&7: &e", true),
        CounterDamage("&7- &6反撃攻撃力 &7: &e", true),
    }

    fun get(t: Template) = getValue(t)
    fun getBaseAttackSpeed() : Double {
        val baseAttackSpeedList = mapOf<Material?, Double>(
                Material.DIAMOND_HOE to 1.0,
                Material.IRON_HOE to 0.0,
                Material.STONE_HOE to 1.0,
                Material.DIAMOND_SWORD to 1.4,
                Material.GOLDEN_SWORD to 1.4,
                Material.IRON_SWORD to 1.4,
                Material.STONE_SWORD to 1.4,
                Material.WOODEN_SWORD to 1.4,
                Material.DIAMOND_PICKAXE to 1.8,
                Material.GOLDEN_PICKAXE to 1.8,
                Material.IRON_PICKAXE to 1.8,
                Material.STONE_PICKAXE to 1.8,
                Material.WOODEN_PICKAXE to 1.8,
                Material.TRIDENT to 1.9,
                Material.DIAMOND_SHOVEL to 2.0,
                Material.GOLDEN_SHOVEL to 2.0,
                Material.IRON_SHOVEL to 2.0,
                Material.STONE_SHOVEL to 2.0,
                Material.WOODEN_SHOVEL to 2.0,
                Material.GOLDEN_AXE to 2.0,
                Material.DIAMOND_AXE to 2.0,
                Material.GOLDEN_HOE to 2.0,
                Material.WOODEN_HOE to 2.0,
                Material.IRON_AXE to 2.1,
                Material.STONE_AXE to 2.2,
                Material.WOODEN_AXE to 2.2
        )
        return baseAttackSpeedList.getOrDefault(i?.type, 0.0)
    }
    fun getBaseAttackDamage() : Double {
        val baseAttackDamageList = mapOf<Material?, Double>(
                Material.DIAMOND_AXE to 9.0,
                Material.IRON_AXE to 9.0,
                Material.STONE_AXE to 9.0,
                Material.TRIDENT to 9.0,
                Material.DIAMOND_SWORD to 7.0,
                Material.GOLDEN_AXE to 7.0,
                Material.WOODEN_AXE to 7.0,
                Material.IRON_SWORD to 6.0,
                Material.DIAMOND_AXE to 5.5,
                Material.STONE_SWORD to 5.0,
                Material.DIAMOND_PICKAXE to 5.0,
                Material.IRON_SHOVEL to 4.5,
                Material.WOODEN_SWORD to 4.0,
                Material.GOLDEN_SWORD to 4.0,
                Material.IRON_PICKAXE to 4.0,
                Material.STONE_SHOVEL to 3.5,
                Material.STONE_PICKAXE to 3.0,
                Material.WOODEN_SHOVEL to 2.5,
                Material.GOLDEN_SHOVEL to 2.5,
                Material.WOODEN_PICKAXE to 2.0,
                Material.GOLDEN_PICKAXE to 2.0,
                Material.DIAMOND_HOE to 1.0,
                Material.GOLDEN_HOE to 1.0,
                Material.IRON_HOE to 1.0,
                Material.STONE_HOE to 1.0,
                Material.WOODEN_HOE to 1.0
        )
        return baseAttackDamageList.getOrDefault(i?.type, 0.0)
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