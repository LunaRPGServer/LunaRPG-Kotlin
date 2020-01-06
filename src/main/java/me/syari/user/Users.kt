package me.syari.user

import me.syari.menu.party.PartyData
import org.bukkit.entity.Player
import java.util.*

object Users {
    private val userList = mutableMapOf<UUID, User>()
    private val partyList : MutableList<PartyData> = mutableListOf()

    fun getUsers() = userList.values
    fun getUser(p: Player) = userList.getOrPut(p.uniqueId) { User(p) }
    fun getParties() = partyList
}