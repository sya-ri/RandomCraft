package com.github.syari.yululi.randomcraft

import com.github.syari.spigot.api.event.EventRegister
import com.github.syari.spigot.api.event.Events
import com.github.syari.spigot.api.item.customModelData
import com.github.syari.spigot.api.item.itemStack
import org.bukkit.Material
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack

object EventListener : EventRegister {
    override fun Events.register() {
        event<PrepareItemCraftEvent> {
            if (it.recipe != null) {
                it.inventory.result = itemStack(Material.GRAY_DYE, "&6?????") {
                    customModelData = 1
                }
            }
        }
        event<CraftItemEvent> {
            val randomItem = ItemStack(Material.values().random())
            if (it.isShiftClick) {
                if (it.whoClicked.inventory.firstEmpty() != -1) {
                    it.currentItem = randomItem
                }
            } else {
                val cursor = it.cursor
                if (cursor == null || cursor.type == Material.AIR) {
                    it.inventory.result = randomItem
                }
            }
        }
    }
}
