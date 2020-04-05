package io.github.starwishsama.cleardrop

import cn.nukkit.plugin.PluginBase
import io.github.starwishsama.cleardrop.module.WClearDropModule
import top.wetabq.easyapi.module.EasyAPIModuleManager

class WClearDropPlugin : PluginBase() {
    override fun onEnable() {
        instance = this
        EasyAPIModuleManager.register(WClearDropModule)
    }

    companion object {
        lateinit var instance: WClearDropPlugin
    }
}