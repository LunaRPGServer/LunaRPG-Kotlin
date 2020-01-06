package me.syari.user

import me.syari.items.EquipLoad
import me.syari.items.item.UsableItem
import me.syari.items.item.usableitem.Armor
import me.syari.items.item.usableitem.Sword
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Status(val p: Player) {
    private val m = p.inventory.itemInMainHand
    private val b = p.inventory.boots
    private val l = p.inventory.leggings
    private val c = p.inventory.chestplate
    private val h = p.inventory.helmet

    private val sword = Sword(if(canLoad(m)) m else null)
    private val armors = listOf(
            Armor(if(canLoad(b)) b else null),
            Armor(if(canLoad(l)) l else null),
            Armor(if(canLoad(c)) c else null),
            Armor(if(canLoad(h)) h else null)
    )

    fun getSword(t: Sword.Template): Pair<Double, Boolean> {
        return Pair(sword.get(t).first / if(t.percent) 100 else 1, t.percent)
    }

    fun getBaseAttackSpeed() = sword.getBaseAttackSpeed()

    fun getBaseAttackDamage() = sword.getBaseAttackDamage()

    fun getArmor(t: Armor.Template): Pair<Double, Boolean> {
        var sum = 0.0
        armors.forEach { a -> sum += a.get(t).first }
        return Pair(sum / if(t.percent) 100 else 1, t.percent)
    }

    fun getExpBonus() : Pair<Double, Boolean> {
        var sum = sword.expBonus.first
        armors.forEach { a -> sum += a.expBonus.first }
        return Pair(sum, UsableItem.Template.ExpBonus.percent)
    }

    fun getUseMana() : Pair<Double, Boolean> {
        var sum = sword.useMana.first
        armors.forEach { a -> sum += a.useMana.first }
        return Pair(sum, UsableItem.Template.UseMana.percent)
    }

    fun getRegenMana() : Pair<Double, Boolean> {
        var sum = 0.0
        armors.forEach { a -> a.get(Armor.Template.RegenMana) }
        return Pair(sum, Armor.Template.RegenMana.percent)
    }

    fun getRegenHealth() : Pair<Double, Boolean> {
        var sum = 0.0
        armors.forEach { a -> a.get(Armor.Template.RegenHealth) }
        return Pair(sum, Armor.Template.RegenHealth.percent)
    }

    private fun canLoad(item: ItemStack) = EquipLoad.canUse(p, item)
}