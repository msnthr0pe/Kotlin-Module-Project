import Modes.*
import java.util.Scanner
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    NotesApp.start()
}

sealed class Modes(val message: String) {
    class ARCHIVE : Modes("архив")
    class NOTE : Modes("заметку")
}

object NotesApp {
    val archives: MutableList<Archive> = mutableListOf()
    val notes: MutableList<Note> = mutableListOf()
    val callHistory = ArrayDeque<Notable?>()
    var mode: Modes = ARCHIVE()
    val input = Scanner(System.`in`)
    fun start() {
        println("Добро пожаловать в заметки!")
        while (true) {
            when (mode) {
                is ARCHIVE -> select(archives)
                is NOTE -> select(notes)
            }
        }
    }

    private fun <T : Notable> select(notables: MutableList<T>) {
        printContents(notables)
        val choice = validateInput(notables.size)
        handleInput(notables, choice)
    }

    private fun validateInput(size: Int) : Int {
        var choice = input.nextLine().toIntOrNull()
        while (true) {
            if (choice == null) {
                println("Введите число")
            } else {
                if (choice < 1 || choice > size + 2) {
                    println("Такого пункта меню не существует")
                }
                else return choice
            }
            choice = input.nextLine().toIntOrNull()
        }
    }

    private fun <T : Notable> handleInput(notables: MutableList<T>, choice: Int) {
        when (choice) {
            1 -> create()
            2 -> exit()
            else -> {
                val notable = notables[choice - 3]

            }
        }

    }

    private fun exit() {
        val previousState = callHistory.removeLastOrNull()
        if (previousState == null) {
            println("Завершение программы")
            exitProcess(0)
        }

    }

    private fun <T : Notable> printContents(contents: MutableList<T>) {
        println("Выберите или создайте ${mode.message}:")
        println("1. Создать ${mode.message}\n2. Выход")
        for (i in 0..contents.size-1) {
            println("${i+3}. ${contents[i].name}")
        }
    }

    private fun create() : Notable {
        print("Введите название: ")
        val title = input.nextLine()
        return when (mode) {
            is ARCHIVE -> {
                val archive = Archive(title)
                callHistory.addLast(archive)
                archives.add(archive)
                archive
            }
            is NOTE -> {
                print("Введите содержимое файла: ")
                val contents = input.nextLine()
                val note = Note(title,contents)
                callHistory.addLast(note)
                note
            }
        }
    }
}