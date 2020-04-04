package io.github.starwishsama.cleardrop.utils

import cn.nukkit.Server
import cn.nukkit.entity.item.EntityItem
import cn.nukkit.entity.mob.EntityMob
import cn.nukkit.entity.passive.EntityAnimal
import cn.nukkit.entity.passive.EntityWaterAnimal
import cn.nukkit.entity.projectile.EntityProjectile
import io.github.starwishsama.cleardrop.ClearDropPlugin
import io.github.starwishsama.cleardrop.module.ClearDropModule
import top.wetabq.easyapi.api.defaults.SimplePluginTaskAPI
import top.wetabq.easyapi.utils.color

var countDown = getConfig().countDown

fun clearDrop(): Array<Int> {
    val server = ClearDropPlugin.instance.server
    val levels = server.levels
    var itemCount = 0
    var entityCount = 0
    for ((_, level) in levels) {
        if (!getConfig().whiteListWorld.contains(level.name)) {
            for (entity in level.entities) {
                // @TODO 配置判断是否清理生物
                when {
                    entity is EntityItem -> {
                        if (!getConfig().whiteListItems.contains(entity.item.id)) {
                            level.removeEntity(entity)
                            itemCount += entity.item.count
                        }
                    }
                    entity is EntityMob && getConfig().doCleanMonster -> {
                        level.removeEntity(entity)
                        entityCount++
                    }
                    entity is EntityAnimal || entity is EntityWaterAnimal && getConfig().doCleanAnimal -> {
                        level.removeEntity(entity)
                        entityCount++
                    }
                    entity is EntityProjectile && getConfig().doCleanProjectile -> {
                        level.removeEntity(entity)
                        entityCount++
                    }
                }
            }
        }
    }
    return arrayOf(itemCount, entityCount)
}

fun runCleanTask(server: Server) {
    SimplePluginTaskAPI.repeating(20 * 1) { task, _ ->
        if (countDown > 0) {
            if (countDown == 30 || countDown == 10 || countDown == 5) {
                server.broadcastMessage((getConfig().pluginPrefix + getConfig().warnMessage.replace("%second%", countDown.toString())).color())
            }
            countDown--
        } else {
            val result = clearDrop()
            server.broadcastMessage((getConfig().pluginPrefix + getConfig().cleanEntityMsg.replace("%item%", result[0].toString()).replace("%entity%", result[1].toString())).color())
            task.cancel()
        }
    }
}

fun getConfig() = ClearDropModule.simpleConfig.safeGetData("clearDrop")


