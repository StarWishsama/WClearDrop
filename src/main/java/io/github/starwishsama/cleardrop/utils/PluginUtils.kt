package io.github.starwishsama.cleardrop.utils

import cn.nukkit.Server
import cn.nukkit.entity.item.EntityItem
import cn.nukkit.entity.mob.EntityMob
import cn.nukkit.entity.passive.EntityAnimal
import cn.nukkit.entity.passive.EntityWaterAnimal
import cn.nukkit.entity.projectile.EntityProjectile
import io.github.starwishsama.cleardrop.ClearDropPlugin
import io.github.starwishsama.cleardrop.module.ClearDropModule
import org.apache.commons.lang3.StringUtils
import top.wetabq.easyapi.api.defaults.SimplePluginTaskAPI

var countDown = 30

fun clearDrop(): Int {
    val server = ClearDropPlugin.instance.server
    val levels = server.levels
    var count = 0
    for ((_, level) in levels) {
        if (!ClearDropModule.unCleanWorld.contains(level.name)) {
            for (entity in level.entities) {
                // @TODO 配置判断是否清理生物
                when {
                    entity is EntityItem -> {
                        if (!ClearDropModule.unCleanItems.contains(entity.item.id)) {
                            level.removeEntity(entity)
                            count += entity.item.count
                        }
                    }
                    entity is EntityMob && ClearDropModule.cleanMonsters -> {
                        level.removeEntity(entity)
                        count++
                    }
                    entity is EntityAnimal || entity is EntityWaterAnimal && ClearDropModule.cleanAnimals -> {
                        level.removeEntity(entity)
                        count++
                    }
                    entity is EntityProjectile && ClearDropModule.cleanProjectile -> {
                        level.removeEntity(entity)
                        count++
                    }
                }
            }
        }
    }
    return count
}

fun runCleanTask(server: Server) {
    SimplePluginTaskAPI.repeating(20 * 1) { task, _ ->
        if (countDown > 0) {
            if (countDown == 30 || countDown == 10 || countDown == 5) {
                server.broadcastMessage(ClearDropModule.prefix + ClearDropModule.warnMessage.replace("%second%", countDown.toString()))
            }
            countDown--
        } else {
            server.broadcastMessage(ClearDropModule.prefix + ClearDropModule.cleanedMessage.replace("%count%", clearDrop().toString()))
            task.cancel()
        }
    }
}

fun string2list(string: String): MutableList<*> {
    if (string != "[]") {
        var split = string.substring(1, string.length - 1).split(", ")
        var containsNumber = false
        for (value in split) {
            if (StringUtils.isNumeric(value)) {
                containsNumber = true
                break
            }
        }

        if (containsNumber) {
            val list = listOf<Int>()

            for (value in split) {
                try {
                    list.plusElement(value.toInt())
                } catch (e: NumberFormatException) {
                    continue
                }
            }
        } else return split.toMutableList()
    }
    return mutableListOf<String>()
}


