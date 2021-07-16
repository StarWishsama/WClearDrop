package io.github.starwishsama.cleardrop

import cn.nukkit.plugin.PluginBase
import cn.nukkit.plugin.PluginLogger
import io.github.starwishsama.cleardrop.module.WClearDropModule
import io.github.starwishsama.cleardrop.utils.PluginUtils.isPluginExists
import top.wetabq.easyapi.module.EasyAPIModuleManager

class WClearDropPlugin : PluginBase() {
	override fun onEnable() {
		instance = this

		try {
			if (!isPluginExists(server, "KotlinLib")) {
				logger.error("你还没有安装 KotlinLib")
				logger.error("请到 https://cloudburstmc.org/resources/kotlinlib.48/ 下载安装")
				this.isEnabled = false
			}

			if (!isPluginExists(server, "EasyAPI")) {
				logger.error("你还没有安装 EasyAPI")
				logger.error("请到 https://github.com/WetABQ/EasyAPI-Nukkit/releases 下载安装")
				this.isEnabled = false
			}

			pluginLogger = logger

			EasyAPIModuleManager.register(WClearDropModule)
		} catch (e: Exception) {
			logger.warning("在加载时发生了意料之外的错误", e)
			logger.warning("你可以在这里反馈问题: ")
			logger.warning("https://github.com/StarWishsama/WClearDrop/issues")
		}
	}

	companion object {
		lateinit var instance: WClearDropPlugin
		lateinit var pluginLogger: PluginLogger
	}
}