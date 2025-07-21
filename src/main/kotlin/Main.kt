import Modes.*
import java.util.Scanner
import kotlin.system.exitProcess

fun main() {
    NotesApp.start()
}

sealed class Modes(val message: String) {
    var currentArchive: Archive = Archive()
    object ARCHIVES : Modes("архив") {
        val archives: MutableList<Archive> = mutableListOf()
    }
    object NOTES : Modes("заметку") {
        val notes: MutableList<Note> = mutableListOf()
        var currentNote: Note = Note()
    }
}

object NotesApp {
    private const val CREATE_CHOICE = 1
    private const val EXIT_CHOICE = 2
    private const val CHOICE_OFFSET = 3

    val archives: MutableList<Archive> = mutableListOf()
    var currentArchive: Archive = Archive()
    var currentNote: Note = Note()
    val callHistory = ArrayDeque<Notable?>()
    var mode: Modes = ARCHIVES
    val input = Scanner(System.`in`)
    fun start() {
        println("Добро пожаловать в заметки!")
        while (true) {
            when (mode) {
                is ARCHIVES -> select(archives)
                is NOTES -> select(currentArchive.notes)
            }
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
                        currentArchive = archives[choice - CHOICE_OFFSET]
                        mode = NOTES
                        callHistory.addLast(currentArchive)
                        select(currentArchive.notes)
                    }
                    is NOTES -> {
                        currentNote = currentArchive.notes[choice - CHOICE_OFFSET]
                        println("Содержимое заметки: ${currentNote.contents}")
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
                if (isDuplicate(title, currentArchive.notes)) {
                    return null
                }
                print("Введите содержимое файла: ")
                val contents = input.nextLine()
                val note = Note(title,contents)
                currentArchive.notes.add(note)
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