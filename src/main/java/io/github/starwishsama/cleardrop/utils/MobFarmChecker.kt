package io.github.starwishsama.cleardrop.utils

import cn.nukkit.entity.Entity
import cn.nukkit.entity.mob.EntityMob
import cn.nukkit.entity.passive.EntityAnimal
import cn.nukkit.entity.passive.EntityWaterAnimal
import cn.nukkit.level.Level
import cn.nukkit.math.SimpleAxisAlignedBB
import io.github.starwishsama.cleardrop.WClearDropPlugin
import io.github.starwishsama.cleardrop.utils.PluginUtils.getConfig
import top.wetabq.easyapi.api.defaults.SimplePluginTaskAPI

object MobFarmChecker {
	private val levels = WClearDropPlugin.instance.server.levels

	fun init() {
		if (getConfig().doCleanMobFarming) {
			SimplePluginTaskAPI.delayRepeating(
				getConfig().cleanMobFarmingDelay * 20,
				getConfig().cleanMobFarmingDelay * 20
			)
			{ _, _ -> checkForMobFarm() }
		}
	}

	private fun checkForMobFarm() {
		levels.forEach { (_, level) ->
			level.entities.forEach { entity ->
				if (isRemovableEntity(entity)) countAndCleanEntities(entity, level)
			}
		}
	}

	private fun countAndCleanEntities(entity: Entity, level: Level) {
		val entities = level.getNearbyEntities(
			SimpleAxisAlignedBB(entity.x, entity.y, entity.z, entity.x + 2.15, entity.y + 4.25, entity.z + 2.15), entity
		)
		var count = 0
		entities.forEach { if (isRemovableEntity(it)) count++ }

		if (count >= getConfig().cleanMobFarmingDelay) {
			level.removeEntity(entity)
		}
	}

	private fun isRemovableEntity(entity: Entity): Boolean {
		return entity is EntityMob || entity is EntityAnimal || entity is EntityWaterAnimal
	}
}