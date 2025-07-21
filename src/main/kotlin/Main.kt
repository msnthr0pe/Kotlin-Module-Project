import Modes.*
import Modes.ARCHIVES.archives
import java.util.Scanner
import kotlin.system.exitProcess

fun main() {
    NotesApp.start()
}

sealed class Modes(val message: String) {
    var currentArchive: Archive = Archive()
    abstract val selectParameter: MutableList<out Notable>
    object ARCHIVES : Modes("архив") {
        val archives: MutableList<Archive> = mutableListOf()
        override val selectParameter = archives
    }
    object NOTES : Modes("заметку") {
        val notes: MutableList<Note> = mutableListOf()
        var currentNote: Note = Note()
        override val selectParameter = currentArchive.notes
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

    private fun <T : Notable> select(notables: MutableList<T>) {
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
                when (mode) {
                    is ARCHIVES -> {
                        mode.currentArchive = archives[choice - CHOICE_OFFSET]
                        mode = NOTES
                        callHistory.addLast(mode.currentArchive)
                        select(mode.selectParameter)
                    }
                    is NOTES -> {
                        (mode as NOTES).currentNote = mode.currentArchive.notes[choice - CHOICE_OFFSET]
                        println("Содержимое заметки: ${(mode as NOTES).currentNote.contents}")
                    }
                }
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

    private fun create() : Notable? {
        print("Введите название: ")
        val title = input.nextLine()
        return when (mode) {
            is ARCHIVES -> {
                val archive = Archive(title)
                if (isDuplicate(title, archives)) {
                    return null
                }
                archives.add(archive)
                archive
            }
            is NOTES -> {
                if (isDuplicate(title, mode.currentArchive.notes)) {
                    return null
                }
                print("Введите содержимое файла: ")
                val contents = input.nextLine()
                val note = Note(title,contents)
                mode.currentArchive.notes.add(note)
                note
            }
        }
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