package io.github.starwishsama.cleardrop.module

import cn.nukkit.Player
import cn.nukkit.event.player.PlayerChatEvent
import io.github.starwishsama.cleardrop.PluginConfig
import io.github.starwishsama.cleardrop.WClearDropPlugin
import io.github.starwishsama.cleardrop.utils.MobFarmChecker
import io.github.starwishsama.cleardrop.utils.PluginUtils.getConfig
import io.github.starwishsama.cleardrop.utils.PluginUtils.runCleanTask
import top.wetabq.easyapi.api.defaults.AsyncListenerAPI
import top.wetabq.easyapi.api.defaults.CommandAPI
import top.wetabq.easyapi.api.defaults.ConfigAPI
import top.wetabq.easyapi.api.defaults.SimplePluginTaskAPI
import top.wetabq.easyapi.config.encoder.advance.SimpleCodecEasyConfig
import top.wetabq.easyapi.listener.AsyncListener
import top.wetabq.easyapi.module.ModuleInfo
import top.wetabq.easyapi.module.ModuleVersion
import top.wetabq.easyapi.module.SimpleEasyAPIModule

object WClearDropModule : SimpleEasyAPIModule() {

	private const val MODULE_NAME = "ClearDropModule"
	private const val AUTHOR = "StarWishsama"

	private const val SIMPLE_CONFIG = "clearDropSimpleConfig"

	private var nextRequestTime = mutableMapOf<Player, Long>()

	lateinit var simpleConfig: SimpleCodecEasyConfig<PluginConfig>

	override fun getModuleInfo(): ModuleInfo = ModuleInfo(
		WClearDropPlugin.instance,
		MODULE_NAME,
		AUTHOR,
		ModuleVersion(1, 0, 0)
	)

	override fun moduleRegister() {
		try {
			simpleConfig = object : SimpleCodecEasyConfig<PluginConfig>(
				"clearDrop",
				WClearDropPlugin.instance,
				PluginConfig::class.java,
				PluginConfig()
			) {}

			simpleConfig.init()

			// Setup config
			if (!simpleConfig.simpleConfig.containsKey("clearDrop")) {
				simpleConfig.simpleConfig["clearDrop"] = simpleConfig.getDefaultValue()
				simpleConfig.save()
			}

			this.registerAPI(SIMPLE_CONFIG, ConfigAPI())
				.add(simpleConfig)

			AsyncListenerAPI.add(object : AsyncListener {
				override fun onPlayerChatEvent(event: PlayerChatEvent) {
					if (event.message.contains(getConfig().requestMessage) && !isCoolDown(event.player)
					) {
						runCleanTask(WClearDropPlugin.instance.server)
					}
				}
			})


			SimplePluginTaskAPI.delayRepeating(
				20 * getConfig().clearDropCD,
				20 * getConfig().clearDropCD
			) { _, _ ->
				runCleanTask(WClearDropPlugin.instance.server)
			}

			this.registerAPI("clearDropCommand", CommandAPI())
				.add(WClearDropCommand)

			MobFarmChecker.run()

		} catch (e: Exception) {
			WClearDropPlugin.pluginLogger.warning("在注册组件时发生了意料之外的错误", e)
			WClearDropPlugin.pluginLogger.warning("你可以在这里反馈问题: ")
			WClearDropPlugin.pluginLogger.warning("https://github.com/StarWishsama/WClearDrop/issues")
		}
	}

	override fun moduleDisable() {}

	private fun isCoolDown(player: Player): Boolean {
		if (nextRequestTime.containsKey(player)) {
			nextRequestTime[player]?.let { time ->
				return System.currentTimeMillis() - time > simpleConfig.safeGetData("clearDrop").clearDropCD
			}
		} else {
			nextRequestTime[player] = System.currentTimeMillis()
			return false
		}
		return false
	}
}
