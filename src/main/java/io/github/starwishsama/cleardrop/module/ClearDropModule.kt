package io.github.starwishsama.cleardrop.module

import cn.nukkit.Player
import cn.nukkit.event.player.PlayerChatEvent
import io.github.starwishsama.cleardrop.ClearDropPlugin
import io.github.starwishsama.cleardrop.utils.runCleanTask
import io.github.starwishsama.cleardrop.utils.string2list
import top.wetabq.easyapi.api.defaults.AsyncListenerAPI
import top.wetabq.easyapi.api.defaults.CommandAPI
import top.wetabq.easyapi.api.defaults.SimpleConfigAPI
import top.wetabq.easyapi.api.defaults.SimplePluginTaskAPI
import top.wetabq.easyapi.config.defaults.SimpleConfigEntry
import top.wetabq.easyapi.listener.AsyncListener
import top.wetabq.easyapi.module.ModuleInfo
import top.wetabq.easyapi.module.ModuleVersion
import top.wetabq.easyapi.module.SimpleEasyAPIModule
import top.wetabq.easyapi.utils.color

object ClearDropModule : SimpleEasyAPIModule() {

    private const val MODULE_NAME = "ClearDropModule"
    private const val AUTHOR = "StarWishsama"

    private const val SIMPLE_CONFIG = "clearDropSimpleConfig"

    private const val CLEAR_DROP_CD = "clearDropCD"
    private const val REQUEST_MESSAGE = "requestMessage"
    private const val WARN_MESSAGE = "warnMessage"
    const val UNCLEANED_ITEMS = "unCleanItems"
    private const val CLEAR_MONSTERS = "clearMonsters"
    private const val CLEAR_ANIMALS = "clearAnimals"
    private const val CLEAR_PROJECTILE = "clearProjectile"
    private const val PLUGIN_PREFIX = "pluginPrefix"
    const val UNCLEAN_WORLDS = "uncleanWorld"

    private var clearDropCD = 240
    private var nextRequestTime: MutableMap<Player, Long> = mutableMapOf()
    var unCleanItems = mutableListOf<Int>()
    var requestMessage = "清理掉落物"
    var warnMessage = "&b地面上的物品将在 &a%second% &b秒后清理!".color()
    var cleanedMessage = "&a成功清理了 &e%count% &a个物品".color()
    var cleanMonsters = true
    var cleanAnimals = true
    var cleanProjectile = true
    var prefix = "&aClearDrop &7> &r".color()
    var unCleanWorld = mutableListOf<String>()
    lateinit var simpleConfig: SimpleConfigAPI

    override fun getModuleInfo(): ModuleInfo = ModuleInfo(
            ClearDropPlugin.instance,
            MODULE_NAME,
            AUTHOR,
            ModuleVersion(1, 0, 0)
    )

    override fun moduleRegister() {
        // Setup config
        simpleConfig = this.registerAPI(SIMPLE_CONFIG, SimpleConfigAPI(ClearDropPlugin.instance))
                .add(SimpleConfigEntry(CLEAR_DROP_CD, clearDropCD))
                .add(SimpleConfigEntry(REQUEST_MESSAGE, requestMessage))
                .add(SimpleConfigEntry(UNCLEANED_ITEMS, unCleanItems.toString()))
                .add(SimpleConfigEntry(WARN_MESSAGE, warnMessage))
                .add(SimpleConfigEntry(CLEAR_ANIMALS, cleanAnimals))
                .add(SimpleConfigEntry(CLEAR_MONSTERS, cleanMonsters))
                .add(SimpleConfigEntry(CLEAR_PROJECTILE, cleanProjectile))
                .add(SimpleConfigEntry(PLUGIN_PREFIX, prefix))
                .add(SimpleConfigEntry(UNCLEAN_WORLDS, unCleanWorld.toString()))

        clearDropCD = simpleConfig.getPathValue(CLEAR_DROP_CD)?.toString()?.toInt() ?: clearDropCD
        requestMessage = simpleConfig.getPathValue(REQUEST_MESSAGE)?.toString()?.color() ?: requestMessage.color()
        warnMessage = simpleConfig.getPathValue(WARN_MESSAGE)?.toString()?.color() ?: warnMessage.color()
        cleanAnimals = simpleConfig.getPathValue(CLEAR_ANIMALS)?.toString()?.toBoolean() ?: cleanAnimals
        cleanMonsters = simpleConfig.getPathValue(CLEAR_MONSTERS)?.toString()?.toBoolean() ?: cleanMonsters
        cleanProjectile = simpleConfig.getPathValue(CLEAR_PROJECTILE)?.toString()?.toBoolean() ?: cleanProjectile
        prefix = simpleConfig.getPathValue(PLUGIN_PREFIX)?.toString()?.color() ?: prefix.color()
        unCleanWorld = (simpleConfig.getPathValue(UNCLEAN_WORLDS)?.toString()?.let { string2list(it) }
                ?: unCleanWorld) as MutableList<String>
        unCleanItems = (simpleConfig.getPathValue(UNCLEANED_ITEMS)?.toString()?.let { string2list(it) }
                ?: unCleanItems) as MutableList<Int>

        AsyncListenerAPI.add(object : AsyncListener {
            override fun onPlayerChatEvent(event: PlayerChatEvent) {
                event.player.sendMessage(isCoolDown(event.player).toString())
                if (event.message.contains(requestMessage) && !isCoolDown(event.player)) {
                    runCleanTask(ClearDropPlugin.instance.server)
                }
            }
        })

        SimplePluginTaskAPI.delayRepeating(20 * clearDropCD, 20 * clearDropCD) { _, _ ->
            runCleanTask(ClearDropPlugin.instance.server)
        }

        this.registerAPI("clearDropCommand", CommandAPI())
                .add(ClearDropCommand)
    }

    override fun moduleDisable() {
    }

    fun isCoolDown(player: Player): Boolean {
        return if (nextRequestTime.containsKey(player)) {
            nextRequestTime[player]?.let { time ->
                ((System.currentTimeMillis() - time) < clearDropCD * 1000)
            } ?: false
        } else {
            nextRequestTime[player] = System.currentTimeMillis()
            false
        }

    }
}
