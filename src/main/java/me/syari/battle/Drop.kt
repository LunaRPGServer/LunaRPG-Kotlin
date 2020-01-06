package me.syari.battle

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import me.syari.server.GiveItem
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.player.PlayerDropItemEvent

object Drop : Listener{

    private const val dropMin = 0.2

    private val data = mutableMapOf<LivingEntity, MutableMap<Player, Double>>()

    @EventHandler
    fun on(e: MythicMobDeathEvent){
        if(e.entity is LivingEntity && !e.drops.isEmpty()){
            val l = e.entity as LivingEntity
            val d = data[l]
            if(d != null && d.isEmpty()){
                val mh = l.getAttribute(Attribute.GENERIC_MAX_HEALTH).value
                val pp = mutableListOf<Player>()
                val m = getMaxDamage(l)
                for(c in d){
                    if(c.value / mh >= dropMin){
                        pp.add(c.key)
                    }
                }
                if(pp.isEmpty() && m != null){
                    pp.add(m)
                }
                GiveItem.drop(e.entity.location, e.drops, pp)
            }
        }
    }

    private fun getMaxDamage(l: LivingEntity): Player? {
        var mp : Player? = null
        var mv : Double = -1.0
        val d = data[l]
        if (d != null) {
            for (c in d){
                if(mv < c.value){
                    mv = c.value
                    mp = c.key
                }
            }
        }
        return if(mv >= dropMin) null else mp
    }

    @EventHandler
    fun on(e: PlayerDropItemEvent){
        val i = e.itemDrop
        i.addScoreboardTag(e.player.uniqueId.toString())
    }

    @EventHandler
    fun on(e: ItemMergeEvent){
        val t = e.target
        val b = e.entity
        if(t.scoreboardTags != b.scoreboardTags){
            e.isCancelled = true
        }
    }

    @EventHandler
    fun on(e: EntityPickupItemEvent){
        if(e.entity is Player){
            val p = e.entity as Player
            if(e.item.scoreboardTags != null && p.uniqueId.toString() in e.item.scoreboardTags || (p.isOp && p.isSneaking)){
                return
            }
        }
        e.isCancelled = true
    }
}