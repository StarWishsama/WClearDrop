package io.github.starwishsama.cleardrop

import cn.nukkit.plugin.PluginBase
import io.github.starwishsama.cleardrop.module.ClearDropModule
import top.wetabq.easyapi.module.EasyAPIModuleManager

class ClearDropPlugin : PluginBase() {
    override fun onEnable() {
        instance = this
        EasyAPIModuleManager.register(ClearDropModule)
    }

    companion object {
        lateinit var instance: ClearDropPlugin
    }
}