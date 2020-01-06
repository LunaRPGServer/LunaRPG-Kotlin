package me.syari.user

import com.sucy.skill.SkillAPI
import com.sucy.skill.api.player.PlayerData
import me.syari.chat.Chat
import me.syari.menu.MainMenu
import me.syari.menu.Menu
import me.syari.menu.party.PartyData
import me.syari.menu.party.PartyList
import me.syari.server.Send
import me.syari.trade.TradeData
import org.bukkit.entity.Player

class User(private val p: Player){
    private var channel = Chat.Channel.Global
        fun getChannel() = channel
        fun setChannel(v: Chat.Channel) { channel = v }

    fun getParty() = Users.getParties().firstOrNull { d -> d.isMember(p) }
        fun isPartyBelong() = getParty() != null
        fun leaveParty(){
            val d = getParty() ?: return
            if (d.isLeader(p)) {
                Send.msg(d.getMember(), "パーティーが解散しました")
                d.getMember().forEach { m -> MainMenu.open(m) }
                Users.getParties().remove(d)
            } else {
                Send.msg(d.getMember(), "&6${p.name} &fがパーティーから脱退しました")
                PartyList.reopen(d.getMember())
                d.removeMember(p)
            }
        }
        fun deletePartyInvite(){
            for(d in Users.getParties().filter { u -> u.hasInvite(p) && !u.isMember(p) }){
                Send.msg(listOf(p, d.getLeader()), "&6${p.name} &fへのパーティー招待が取り消されました")
                PartyList.reopen(d.getMember())
                d.removeInvite(p)
            }
        }
        fun createParty(pub: Boolean){
            if(! isPartyBelong()){
                Users.getParties().add(PartyData(p, pub))
            }
        }

    fun isAdmin() = p.hasPermission("*") || p.isOp

    private var trade = TradeData(p)
        fun getTrade() = trade
        fun clearTrade() { trade = TradeData(p) }

    fun getClassData() : PlayerData = SkillAPI.getPlayerAccountData(p).activeData

    private var status = Status(p)
        fun getStatus() = status
        fun setStatus() { status = Status(p) }
}