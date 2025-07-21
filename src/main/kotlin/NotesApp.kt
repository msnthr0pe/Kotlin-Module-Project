import java.util.Scanner
import kotlin.system.exitProcess

/* Помогите, кажется  жётско перемудрил с кодом.
Я очень пытался сделать его расширяемым, но как быдто бы
получился какой-то треш. Не придумал, куда пристроить лямбду.
Честно говоря вообще не понял зачем она в данной ситуации, если
я могу использовать абстрактный класс*/

object NotesApp {
    private const val CREATE_CHOICE = 1
    private const val EXIT_CHOICE = 2
    private const val CHOICE_OFFSET = 3

    val callHistory = ArrayDeque<Modes>()
    var mode: Modes = ArchivesMode
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
        mode = previousState
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
        if (checkForEmptyString(title)) {
            return
        }
        if (isDuplicate(title, mode.duplicatable)) {
            return
        }
        mode.createObj(title)
    }

    fun checkForEmptyString(contents: String) : Boolean {
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