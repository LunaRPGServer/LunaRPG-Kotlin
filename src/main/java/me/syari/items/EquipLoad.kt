package me.syari.items

import com.sucy.skill.api.event.PlayerLevelUpEvent
import me.syari.items.item.UsableItem
import me.syari.items.item.usableitem.Armor
import me.syari.items.item.usableitem.Sword
import me.syari.menu.Menu
import me.syari.server.Get
import me.syari.user.Users
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

object EquipLoad : Listener {
    fun getValue(d: Double, p: Pair<Double, Boolean>, addMode: Boolean = false): Double{
        return if(p.second) d * p.first - if(addMode) d else 0.0
        else d + p.first
    }

    private fun checkCanUse(p: Player){
        for(i in p.inventory.contents){
            if(canUse(p, i)) {
                val r = UsableItem(i).getRarity()
                if(r != null){
                    i.itemMeta.displayName = Get.Color.color("${r.color}${Get.Color.uncolor(i.itemMeta.displayName)}")
                }
            } else {
                i.itemMeta.displayName = Get.Color.color("&c${Get.Color.uncolor(i.itemMeta.displayName)}")
            }
        }
    }

    fun canUse(p: Player, item: ItemStack): Boolean{
        val i = UsableItem(item)
        val d = Users.getUser(p).getClassData()
        return (i.needLevel.first <= d.mainClass.level) && (i.needSkill != "" || d.hasSkill(i.needSkill))
    }

    private fun load(p: Player){
        object : BukkitRunnable() {
            override fun run() {
                checkCanUse(p)
                val u = Users.getUser(p)
                u.setStatus()
                val s = u.getStatus()
                p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).baseValue = getValue(s.getBaseAttackSpeed(), s.getSword(Sword.Template.AttackSpeed))
                p.getAttribute(Attribute.GENERIC_ARMOR).baseValue = 0.0
                u.getClassData().addMaxHealth(getValue(u.getClassData().mainClass.health, s.getArmor(Armor.Template.MaxHealth), true))
                u.getClassData().addMaxMana(getValue(u.getClassData().maxMana, s.getArmor(Armor.Template.MaxMana), true))
            }
        }.runTaskLater(Get.plugin(), 1)
    }

    @EventHandler
    fun on(e: InventoryClickEvent){
        val slot = Get.Inv.targetSlot(e)
        if(slot in 0..9 || slot in 36..40){
            val p = e.whoClicked as Player
            load(p)
        }
    }

    @EventHandler
    fun on(e: PlayerLevelUpEvent){
        load(e.playerData.player)
    }

    @EventHandler
    fun on(e: PlayerSwapHandItemsEvent){
        load(e.player)
    }

    @EventHandler
    fun on(e: PlayerDropItemEvent){
        load(e.player)
    }

    @EventHandler
    fun on(e: PlayerItemHeldEvent){
        load(e.player)
    }
}