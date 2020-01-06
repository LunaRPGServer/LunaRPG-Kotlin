package me.syari.discord

import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.Core
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.plugins.Plugin
import java.text.SimpleDateFormat
import java.util.*

@Plugin(name = "LogServerAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
object LogServerAppender : AbstractAppender("LunaRPG", null, null){

    private val date = Date()
    private val format = SimpleDateFormat("HH:mm:ss")

    override fun append(e: LogEvent) {
        date.time = System.currentTimeMillis()
        val msg = "[${format.format(date)} ${e.level.name()} ${e.message.formattedMessage.replace(Regex("\\u001b\\[[^m.]*m"), "")}"
        Bot.sendMessage(Bot.Channel.Console, msg)
    }
}