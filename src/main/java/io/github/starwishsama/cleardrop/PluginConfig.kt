package io.github.starwishsama.cleardrop

data class PluginConfig(var clearDropCD: Int = 240,
                        var countDown: Int = 30,
                        var requestFeature: Boolean = true,
                        var requestMessage: String = "清理掉落物",
                        var warnMessage: String = "&b将在 &a%second% &b秒后清理实体!",
                        var cleanEntityMsg: String = "&a成功清理了 &e%item% &a个物品和 &e%entity% &a个实体!",
                        var doCleanAnimal: Boolean = true,
                        var doCleanMonster: Boolean = true,
                        var doCleanProjectile: Boolean = true,
                        var pluginPrefix: String = "&bWClearDrop &7>&r ",
                        var whiteListItems: List<Int> = mutableListOf(),
                        var whiteListWorld: List<String> = mutableListOf(),
                        var doCleanMobFarming: Boolean = false,
                        var cleanMobFarmingDelay: Int = 600,
                        val cleanMobFarmingValue: Int = 50
)