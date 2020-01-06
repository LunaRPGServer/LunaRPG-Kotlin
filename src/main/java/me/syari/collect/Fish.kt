package me.syari.collect

import me.syari.server.Get
import me.syari.server.SQL
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

object Fish : Listener{
    @EventHandler
    fun on(e: PlayerFishEvent){
        if(e.state == PlayerFishEvent.State.CAUGHT_FISH) {
            e.isCancelled = true
            val loc = e.hook.location
            val resultSet = SQL.executeQuery(
               """
                    Select * from Fish where
                    XZr <= 0 or Yr <= 0 or
                    (
                        World = ${loc.world} and
                        X between ${loc.x} - XZr and ${loc.x} + XZr and
                        Y between ${loc.y} - Yr and ${loc.y} + Yr and
                        Z between ${loc.z} - XZr and ${loc.z} + XZr
                    );
                """.trimIndent()
            ) ?: return
            val fishes = mutableMapOf<IntRange, ItemStack>()
            var number = 0
            while(resultSet.next()) {
                val item = Get.Item.item(resultSet.getString("Item")) ?: continue
                val first = number
                number += resultSet.getInt("Value")
                fishes[first .. number] = item
            }
            val r = (0..number).random()
            val f = fishes.entries.firstOrNull { entry -> r in entry.key } ?: return
            e.player.inventory.addItem(f.value)
        }
    }

    /*
    select count(*), x, y, z from Fish group by x, y, z;
    select Item, value, XZr, Yr from Fish where x = $x and y = $y and z = $z;
     */
}