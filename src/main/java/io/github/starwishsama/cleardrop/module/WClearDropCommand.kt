package io.github.starwishsama.cleardrop.module

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.command.ConsoleCommandSender
import cn.nukkit.command.data.CommandParamType
import cn.nukkit.command.data.CommandParameter
import io.github.starwishsama.cleardrop.WClearDropPlugin
import io.github.starwishsama.cleardrop.utils.getConfig
import io.github.starwishsama.cleardrop.utils.runCleanTask
import org.apache.commons.lang3.StringUtils
import top.wetabq.easyapi.command.EasyCommand
import top.wetabq.easyapi.command.EasySubCommand
import top.wetabq.easyapi.utils.color

object WClearDropCommand : EasyCommand("wcleardrop", "WClearDrop's Command") {
    init {
        subCommand.add(object : EasySubCommand("clear") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (sender.isOp || sender is ConsoleCommandSender) {
                    runCleanTask(WClearDropPlugin.instance.server)
                }
                return true
            }

            override fun getAliases(): Array<String>? = arrayOf("cl")

            override fun getDescription() = "Execute clean task by command"

            override fun getParameters(): Array<CommandParameter>? = null
        })
        subCommand.add(object : EasySubCommand("add") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (sender.isOp || sender is ConsoleCommandSender) {
                    if (sender is Player && sender.inventory.itemInHand != null) {
                        if (getConfig().whiteListItems.contains(sender.inventory.itemInHand.id)) {
                            sender.sendMessage((getConfig().pluginPrefix + "&c${sender.inventory.itemInHand.name} 已经在清理物品白名单里了").color())
                        } else {
                            WClearDropModule.simpleConfig.safeGetData("clearDrop")
                            getConfig().whiteListItems.plus(sender.inventory.itemInHand.id)
                            sender.sendMessage((getConfig().pluginPrefix + "&a成功将物品 ${sender.inventory.itemInHand.name} 加入清理物品白名单").color())
                        }
                    } else if (args.size > 1) {
                        if (StringUtils.isNumeric(args[1])) {
                            if (getConfig().whiteListItems.contains(args[1].toInt())) {
                                sender.sendMessage((getConfig().pluginPrefix + "&cID ${args[1]} 已经在清理物品白名单里了").color())
                            } else {
                                getConfig().whiteListItems.plus(args[1].toInt())
                                sender.sendMessage((getConfig().pluginPrefix + "&a成功将物品 ID ${args[1]} 加入清理物品白名单").color())
                            }
                        } else {
                            sender.sendMessage((getConfig().pluginPrefix + "&c请输入有效的物品 ID!").color())
                        }
                    } else {
                        sender.sendMessage((getConfig().pluginPrefix + "/wcleardrop add/del [物品ID]").color())
                    }
                } else {
                    sender.sendMessage((getConfig().pluginPrefix + "&c你没有权限!").color())
                }
                return true
            }

            override fun getAliases(): Array<String>? = null

            override fun getDescription() = "Add item not to clean"

            override fun getParameters(): Array<CommandParameter>? = arrayOf(
                    CommandParameter("add", CommandParamType.STRING, true)
            )
        })
        subCommand.add(object : EasySubCommand("remove") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (sender.isOp || sender is ConsoleCommandSender) {
                    if (sender is Player) {
                        if (sender.inventory.itemInHand != null) {
                            if (!getConfig().whiteListItems.contains(sender.inventory.itemInHand.id)) {
                                sender.sendMessage((getConfig().pluginPrefix + "&c${sender.inventory.itemInHand.name} 不在清理物品白名单里").color())
                            } else {
                                getConfig().whiteListItems.minus(sender.inventory.itemInHand.id)
                                sender.sendMessage((getConfig().pluginPrefix + "&a成功将物品 ${sender.inventory.itemInHand.name} 移出清理物品白名单").color())
                            }
                        } else {
                            sender.sendMessage((getConfig().pluginPrefix + "手持要移出白名单的物品并使用 /cleardrop del").color())
                        }
                    } else if (args.size > 1) {
                        if (StringUtils.isNumeric(args[1])) {
                            if (getConfig().whiteListItems.contains(args[1].toInt())) {
                                sender.sendMessage((getConfig().pluginPrefix + "&cID ${args[1]} 不在清理物品白名单里了").color())
                            } else {
                                getConfig().whiteListItems.minus(args[1].toInt())
                                sender.sendMessage((getConfig().pluginPrefix + "&a成功将物品 ID ${args[1]} 移出清理物品白名单").color())
                            }
                        } else {
                            sender.sendMessage((getConfig().pluginPrefix + "&c请输入有效的物品 ID!").color())
                        }
                    } else {
                        sender.sendMessage((getConfig().pluginPrefix + "/wcleardrop add/del [物品ID]").color())
                    }
                } else {
                    sender.sendMessage((getConfig().pluginPrefix + "&c你没有权限!").color())
                }
                return true
            }

            override fun getAliases(): Array<String>? = null

            override fun getDescription() = "Remove item not to clean"

            override fun getParameters(): Array<CommandParameter>? = arrayOf(
                    CommandParameter("remove", CommandParamType.STRING, true)
            )
        })

        subCommand.add(object : EasySubCommand("world") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (sender.isOp || sender is ConsoleCommandSender) {
                    if (args.size > 1 && args[1].isNotEmpty()) {
                        if (!getConfig().whiteListWorld.isNullOrEmpty() && getConfig().whiteListWorld.contains(args[1])) {
                            getConfig().whiteListWorld = getConfig().whiteListWorld.minus(args[1])
                            sender.sendMessage((getConfig().pluginPrefix + "&a世界 ${args[1]} 已经从白名单中移除!").color())
                        } else {
                            getConfig().whiteListWorld = getConfig().whiteListWorld.plus(args[1])
                            sender.sendMessage((getConfig().pluginPrefix + "&a世界 ${args[1]} 已经添加至白名单!").color())
                        }
                    } else {
                        sender.sendMessage((getConfig().pluginPrefix + "/wcleardrop world [世界名]").color())
                    }
                } else {
                    sender.sendMessage((getConfig().pluginPrefix + "&c你没有权限!").color())
                }
                return true
            }

            override fun getAliases(): Array<String>? = arrayOf("w")

            override fun getDescription(): String = "Add/Remove whitelist world"

            override fun getParameters(): Array<CommandParameter>? = arrayOf(
                    CommandParameter("world", CommandParamType.STRING, false)
            )
        })

        loadCommandBase()
    }
}