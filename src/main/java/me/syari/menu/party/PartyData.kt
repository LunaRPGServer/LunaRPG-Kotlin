package me.syari.menu.party

import org.bukkit.Material
import org.bukkit.entity.Player

class PartyData(private var leader : Player, private var public: Boolean) {

    private val member = mutableListOf(leader)
    private val invite = mutableListOf<Player>()
    private var icon = PartyIcon.icons.keys.first()

    fun setLeader(n: Player){
        leader = n
    }

    fun getLeader(): Player{
        return leader
    }

    fun isLeader(p: Player): Boolean{
        return leader == p
    }

    fun getMember(): List<Player>{
        return member
    }

    fun isMember(p: Player): Boolean{
        return member.contains(p)
    }

    fun addMember(p: Player){
        if(!isMember(p)) member.add(p)
    }

    fun removeMember(p: Player){
        if(isMember(p)) member.remove(p)
    }

    fun getInvite(): List<Player>{
        return invite
    }

    fun hasInvite(p: Player): Boolean{
        return invite.contains(p)
    }

    fun addInvite(p: Player){
        if(!hasInvite(p)) invite.add(p)
    }

    fun removeInvite(p: Player){
        if(hasInvite(p)) invite.remove(p)
    }

    fun clearInvite(){
        invite.clear()
    }

    fun isPublic(): Boolean{
        return public
    }

    fun togglePublic(){
        public = !public
    }

    fun getIcon(): Material{
        return icon
    }

    fun setIcon(m: Material){
        icon = m
    }

    fun isIcon(m: Material) = m == icon
}