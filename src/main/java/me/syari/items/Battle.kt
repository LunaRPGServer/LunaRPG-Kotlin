package me.syari.items

import com.sucy.skill.api.event.PlayerCastSkillEvent
import com.sucy.skill.api.event.PlayerManaGainEvent
import me.syari.user.Users
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.potion.PotionEffectType

object Battle : Listener {
    @EventHandler
    fun on(e: PlayerManaGainEvent){
        val s = Users.getUser(e.playerData.player).getStatus()
        e.amount = EquipLoad.getValue(e.amount, s.getRegenMana())
    }

    @EventHandler
    fun on(e: EntityRegainHealthEvent){
        if(e.entity is Player){
            val p = e.entity as Player
            val s = Users.getUser(p).getStatus()
            e.amount = EquipLoad.getValue(e.amount, s.getRegenHealth())
        }
    }

    @EventHandler
    fun on(e: PlayerCastSkillEvent){
        val s = Users.getUser(e.player).getStatus()
        e.manaCost = e.manaCost - EquipLoad.getValue(e.manaCost, s.getUseMana(), true)
    }

    @EventHandler
    fun on(e: EntityDamageEvent){
        val l = e.entity as LivingEntity
        val m = l.getAttribute(Attribute.GENERIC_MAX_HEALTH).value
        e.damage = when(e.cause){
            EntityDamageEvent.DamageCause.FALL -> (m / 25) * e.damage
            EntityDamageEvent.DamageCause.STARVATION -> m / 15
            EntityDamageEvent.DamageCause.DROWNING -> m / 10
            EntityDamageEvent.DamageCause.VOID -> m
            else -> e.damage
        }
    }

    @EventHandler
    fun on(e: EntityDamageByEntityEvent){

    }

    // dmg = weapon.getDamage() * getCharge(e)
    private fun getCharge(e: EntityDamageByEntityEvent): Double {
        val p = e.damager as Player
        var dmg = e.damage
        if (isCrit(p)) dmg /= 1.5
        val base = Users.getUser(p).getStatus().getBaseAttackDamage()
        return if (base > 0) dmg / base else 0.0
    }

    private fun isCrit(p: Player): Boolean{
        return (p.fallDistance > 0.0F && !p.isOnGround && !p.hasPotionEffect(PotionEffectType.BLINDNESS) && p.vehicle == null)
    }
}