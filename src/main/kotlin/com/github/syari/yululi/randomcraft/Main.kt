package com.github.syari.yululi.randomcraft

import com.github.syari.spigot.api.event.EventRegister.Companion.registerEvents
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {
    override fun onEnable() {
        registerEvents(EventListener)
    }
}
