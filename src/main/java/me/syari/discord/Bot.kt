package me.syari.discord

import me.syari.chat.Chat
import me.syari.server.Get
import me.syari.server.Send
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.TimeUnit
import javax.security.auth.login.LoginException


object Bot : ListenerAdapter(){
    private const val buildDelay = 5L
    private const val token = "MzUxMzE0ODA4Njk3NzgyMjcz.Dr3XHg.iPAiKJaJh3EkHTjBpEPpc7QYpUg"

    private var bot : JDA? = null

    enum class Channel(val id: String){
        Global("509405359698346021"),
        Admin("426066211957112844"),
        Console("507824260861919234")
    }

    fun getBot() = bot

    private fun getPrefix(c: Chat.Channel, u: User) = "${c.prefix} &5Discord &f${u.name} &b≫ &f"

    private fun hasRole(g: Guild, u: User, p: String) : Boolean{
        for(r in g.getMember(u).roles){
            if(r.name == p) return true
        }
        return false
    }

    fun setup(){
        try {
            TimeUnit.SECONDS.sleep(buildDelay)
            bot = JDABuilder(AccountType.BOT).setToken(token).build()
            getBot()?.addEventListener(this)
        } catch (e: InterruptedException) {
            bot = null
            e.printStackTrace()
        } catch (e: LoginException) {
            bot = null
            e.printStackTrace()
        }
    }

    override fun onMessageReceived(e: MessageReceivedEvent){
        if(e.message != null && !e.author.isBot && !e.isFromType(ChannelType.PRIVATE)){
            when(e.channel.id){
                Channel.Global.id -> Send.msg(Get.onlinePlayer(), e.message.contentDisplay, getPrefix(Chat.Channel.Global, e.author))
                Channel.Admin.id -> Send.msg(Get.onlineAdmin(), e.message.contentDisplay, getPrefix(Chat.Channel.Admin, e.author))
                Channel.Console.id -> {
                    if(e.author == e.guild.owner.user || hasRole(e.guild, e.author, "Manager")){
                        object : BukkitRunnable() {
                            override fun run() {
                                Bukkit.getServer().dispatchCommand(Bukkit.getServer().consoleSender, e.message.contentDisplay)
                            }
                        }.runTask(Get.plugin())
                    }
                }
            }
        }
    }

    fun sendMessage(ch: Channel, s: String){
        getBot()?.getTextChannelById(ch.id)?.sendMessage(s)?.queue()
    }
}