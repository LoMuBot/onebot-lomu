package cn.luorenmu.action.commandProcess

import cn.hutool.core.lang.ClassScanner

/**
 * @author LoMu
 * Date 2025.01.28 13:30
 */
class CommandRegister {
    private val commands = mutableMapOf<String, CommandProcess>()

    init {
        register()
    }

    private fun register() {
        ClassScanner.scanPackage("cn.luorenmu.action.commandProcess").forEach {
            if (it.isAssignableFrom(CommandProcess::class.java) && !it.isInterface) {
                it.getDeclaredConstructor().newInstance().let { commandProcess ->
                    if (commandProcess is CommandProcess) {
                        commands[commandProcess.commandName()] = commandProcess
                    }
                }
            }
        }
    }

    fun getCommand(commandName: String): CommandProcess {
        return commands[commandName] ?: throw IllegalArgumentException("commandName $commandName not found")
    }
}