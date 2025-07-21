import Modes.ARCHIVES
import Modes.NOTES
import NotesApp.callHistory
import NotesApp.mode
import NotesApp.select
import java.util.Scanner
import kotlin.system.exitProcess

fun main() {
    NotesApp.start()
}

sealed class Modes(val message: String) {
    companion object {
        var currentArchiveIndex: Int = 0
        val archives: MutableList<Archive> = mutableListOf()
    }
    abstract val selectParameter: MutableList<out Notable>
    abstract val duplicatable: MutableList<out Notable>
    abstract fun handleModeInput(choice: Int)
    abstract fun createObj() : MutableList<out Notable>

    object ARCHIVES : Modes("архив") {
        override val selectParameter: MutableList<Archive>
            get() {
                return archives
            }
        override val duplicatable
            get() = archives

        override fun handleModeInput(choice: Int) {
            currentArchiveIndex = choice
            mode = NOTES
            callHistory.addLast(archives[currentArchiveIndex])
            select(archives[currentArchiveIndex].notes)
        }

        override fun createObj(): MutableList<out Notable> {
            TODO("Not yet implemented")
        }
    }

    object NOTES : Modes("заметку") {
        var currentNote: Note = Note()
        override val selectParameter: MutableList<Note>
            get() {
                return archives[currentArchiveIndex].notes
            }
        override val duplicatable: MutableList<out Notable>
            get() = archives[currentArchiveIndex].notes

        override fun handleModeInput(choice: Int) {
            (mode as NOTES).currentNote = archives[currentArchiveIndex].notes[choice]
            println("Содержимое заметки: ${(mode as NOTES).currentNote.contents}")
        }

        override fun createObj(): MutableList<out Notable> {
            TODO("Not yet implemented")
        }
    }
}

object NotesApp {
    private const val CREATE_CHOICE = 1
    private const val EXIT_CHOICE = 2
    private const val CHOICE_OFFSET = 3

    val callHistory = ArrayDeque<Notable?>()
    var mode: Modes = ARCHIVES
    val input = Scanner(System.`in`)
    fun start() {
        println("Добро пожаловать в заметки!")
        while (true) {
            select(mode.selectParameter)
        }
    }

    fun <T : Notable> select(notables: MutableList<T>) {
        printContents(notables)
        val choice = validateInput(notables.size)
        handleInput(choice)
    }

    private fun validateInput(size: Int) : Int {
        var choice = input.nextLine().toIntOrNull()
        while (true) {
            if (choice == null) {
                println("Введите число")
            } else {
                if (choice < CREATE_CHOICE || choice > size + CHOICE_OFFSET - 1) {
                    println("Такого пункта меню не существует")
                } else return choice
            }
            choice = input.nextLine().toIntOrNull()
        }
    }

    private fun handleInput(choice: Int) {
        when (choice) {
            CREATE_CHOICE -> create()
            EXIT_CHOICE -> exit()
            else -> {
                mode.handleModeInput(choice - CHOICE_OFFSET)
            }
        }
    }

    private fun exit() {
        val previousState = callHistory.removeLastOrNull()
        if (previousState == null) {
            println("Завершение программы")
            exitProcess(0)
        }
        mode = if (previousState is Archive) {
            ARCHIVES
        } else NOTES
    }

    private fun <T : Notable> printContents(contents: MutableList<T>) {
        println("Выберите или создайте ${mode.message}:")
        println("1. Создать ${mode.message}\n2. Выход")
        for (i in 0..contents.size - 1) {
            println("${i + CHOICE_OFFSET}. ${contents[i].name}")
        }
    }

    private fun create() {
        print("Введите название: ")
        val title = input.nextLine()
        if (checkIfEmpty(title)) {
            return
        }
        when (mode) {
            is ARCHIVES -> {
                if (isDuplicate(title, Modes.archives)) {
                    return
                }
                val archive = Archive(title)
                Modes.archives.add(archive)
            }
            is NOTES -> {
                if (isDuplicate(title, Modes.archives[Modes.currentArchiveIndex].notes)) {
                    return
                }
                print("Введите содержимое файла: ")
                var contents = input.nextLine()
                if (checkIfEmpty(contents)) {
                    return
                }
                val note = Note(title, contents)
                Modes.archives[Modes.currentArchiveIndex].notes.add(note)
            }
        }
    }

    private fun checkIfEmpty(contents: String) : Boolean {
        if (contents.isEmpty()) {
            println("Содержание не может быть пустым")
            return true
        }
        return false
    }

    private fun <T : Notable> isDuplicate(title: String, notables: MutableList<T>) : Boolean {
        for (notable in notables) {
            if (title == notable.name) {
                println("Такое название уже существует")
                return true
            }
        }
        return false
    }
}