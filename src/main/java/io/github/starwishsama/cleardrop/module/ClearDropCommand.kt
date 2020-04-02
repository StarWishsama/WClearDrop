package io.github.starwishsama.cleardrop.module

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandParameter
import io.github.starwishsama.cleardrop.ClearDropPlugin
import io.github.starwishsama.cleardrop.utils.runCleanTask
import org.apache.commons.lang3.StringUtils
import top.wetabq.easyapi.command.EasyCommand
import top.wetabq.easyapi.command.EasySubCommand
import top.wetabq.easyapi.config.defaults.SimpleConfigEntry

object ClearDropCommand : EasyCommand("cleardrop", "ClearDrop's Command") {
    init {
        subCommand.add(object : EasySubCommand("clear") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                runCleanTask(ClearDropPlugin.instance.server)
                return true
            }

            override fun getAliases(): Array<String>? = arrayOf("cl")

            override fun getDescription() = "Execute clean task by command"

            override fun getParameters(): Array<CommandParameter>? = null
        })
        subCommand.add(object : EasySubCommand("add") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (sender is Player) {
                    if (sender.inventory.itemInHand != null) {
                        if (ClearDropModule.unCleanItems.contains(sender.inventory.itemInHand.id)) {
                            sender.sendMessage(ClearDropModule.prefix + "&c${sender.inventory.itemInHand.name} 已经在清理物品白名单里了")
                        } else {
                            ClearDropModule.unCleanItems.add(sender.inventory.itemInHand.id)
                            ClearDropModule.simpleConfig.setPathValue(SimpleConfigEntry(ClearDropModule.UNCLEANED_ITEMS, ClearDropModule.unCleanItems))
                            sender.sendMessage(ClearDropModule.prefix + "&a成功将物品 ${sender.inventory.itemInHand.name} 加入清理物品白名单")
                        }
                    } else {
                        sender.sendMessage(ClearDropModule.prefix + "  /cleardrop add")
                    }
                } else if (args.size == 2) {
                    if (StringUtils.isNumeric(args[1])) {
                        if (ClearDropModule.unCleanItems.contains(args[1].toInt())) {
                            sender.sendMessage(ClearDropModule.prefix + "&cID ${args[1]} 已经在清理物品白名单里了")
                        } else {
                            ClearDropModule.unCleanItems.add(args[1].toInt())
                            ClearDropModule.simpleConfig.setPathValue(SimpleConfigEntry(ClearDropModule.UNCLEANED_ITEMS, ClearDropModule.unCleanItems))
                            sender.sendMessage(ClearDropModule.prefix + "&a成功将物品 ID ${args[1]} 加入清理物品白名单")
                        }
                    } else {
                        sender.sendMessage(ClearDropModule.prefix + "&c请输入有效的物品 ID!")
                    }
                } else {
                    sender.sendMessage(ClearDropModule.prefix + "/cleardrop add/del [物品ID]")
                }
                return true
            }

            override fun getAliases(): Array<String>? = null

            override fun getDescription() = "Add item not to clean"

            override fun getParameters(): Array<CommandParameter>? = null
        })
        subCommand.add(object : EasySubCommand("remove") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (sender is Player) {
                    if (sender.inventory.itemInHand != null) {
                        if (!ClearDropModule.unCleanItems.contains(sender.inventory.itemInHand.id)) {
                            sender.sendMessage(ClearDropModule.prefix + "&c${sender.inventory.itemInHand.name} 不在清理物品白名单里")
                        } else {
                            ClearDropModule.unCleanItems.remove(sender.inventory.itemInHand.id)
                            ClearDropModule.simpleConfig.setPathValue(SimpleConfigEntry(ClearDropModule.UNCLEANED_ITEMS, ClearDropModule.unCleanItems))
                            sender.sendMessage(ClearDropModule.prefix + "&a成功将物品 ${sender.inventory.itemInHand.name} 移出清理物品白名单")
                        }
                    } else {
                        sender.sendMessage(ClearDropModule.prefix + "手持要移出白名单的物品并使用 /cleardrop del")
                    }
                } else if (args.size == 2) {
                    if (StringUtils.isNumeric(args[1])) {
                        if (ClearDropModule.unCleanItems.contains(args[1].toInt())) {
                            sender.sendMessage(ClearDropModule.prefix + "&cID ${args[1]} 不在清理物品白名单里了")
                        } else {
                            ClearDropModule.unCleanItems.remove(args[1].toInt())
                            ClearDropModule.simpleConfig.setPathValue(SimpleConfigEntry(ClearDropModule.UNCLEANED_ITEMS, ClearDropModule.unCleanItems))
                            sender.sendMessage(ClearDropModule.prefix + "&a成功将物品 ID ${args[1]} 移出清理物品白名单")
                        }
                    } else {
                        sender.sendMessage(ClearDropModule.prefix + "&c请输入有效的物品 ID!")
                    }
                } else {
                    sender.sendMessage(ClearDropModule.prefix + "/cleardrop add/del [物品ID]")
                }
                return true
            }

            override fun getAliases(): Array<String>? = null

            override fun getDescription() = "Remove item not to clean"

            override fun getParameters(): Array<CommandParameter>? = null
        })

        subCommand.add(object : EasySubCommand("world") {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                if (args.size == 2) {
                    if (args[1].isNotBlank()) {
                        if (ClearDropModule.unCleanWorld.contains(args[1])) {
                            ClearDropModule.unCleanWorld.remove(args[1])
                            sender.sendMessage(ClearDropModule.prefix + "&a世界 ${args[1]} 已经从白名单中移除!")
                        } else {
                            ClearDropModule.unCleanWorld.add(args[1])
                            sender.sendMessage(ClearDropModule.prefix + "&a世界 ${args[1]} 已经添加至白名单!")
                        }
                    } else {
                        sender.sendMessage(ClearDropModule.prefix + "/cleardrop world [世界名]")
                    }
                } else {
                    sender.sendMessage(ClearDropModule.prefix + "/cleardrop world [世界名]")
                }
                return true
            }

            override fun getAliases(): Array<String>? = arrayOf("w")

            override fun getDescription(): String = "Add/Remove whitelist world"

            override fun getParameters(): Array<CommandParameter>? = null
        })

        loadCommandBase()
    }
}