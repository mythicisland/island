package net.mythicisland.core.island

import net.minestom.server.MinecraftServer
import net.minestom.server.listener.TabCompleteListener
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.EndOfFileException
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.ParsedLine
import org.jline.reader.UserInterruptException
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Kotlin port of https://github.com/emortalmc/games/blob/main/core/src/main/java/dev/emortal/minestom/core/MinestomTerminal.java
 */
object MinestomTerminal {
    private const val PROMPT = "> "

    @Volatile
    private var terminal: Terminal? = null

    @Volatile
    var reader: LineReader? = null
        internal set

    fun start() {
        thread(name = "Jline", isDaemon = true) {
            runCatching { terminal = TerminalBuilder.terminal() }

            val lineReader = LineReaderBuilder.builder()
                .completer(MinestomCompleter())
                .terminal(terminal)
                .build()
                .also { reader = it }

            while (true) {
                try {
                    val line = lineReader.readLine(PROMPT) ?: break
                    val commandManager = MinecraftServer.getCommandManager()
                    commandManager.execute(commandManager.consoleSender, line)
                } catch (_: UserInterruptException) {
                    // Handle Ctrl + C
                    exitProcess(0)
                } catch (_: EndOfFileException) {
                    break
                } catch (_: Exception) {
                    break
                }
            }
        }
    }

    fun stop() {
        reader = null
        runCatching { terminal?.close() }
        terminal = null
    }

    private class MinestomCompleter : Completer {
        override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
            val commandManager = MinecraftServer.getCommandManager()

            if (line.wordIndex() == 0) {
                val input = line.word().lowercase()
                commandManager.dispatcher.commands.forEach { command ->
                    val name = command.name
                    if (input.isBlank() || name.lowercase().startsWith(input)) {
                        candidates.add(Candidate(name))
                    }
                }
            } else {
                TabCompleteListener.getSuggestion(commandManager.consoleSender, line.line())
                    ?.entries
                    ?.forEach { candidates.add(Candidate(it.entry)) }
            }
        }
    }
}