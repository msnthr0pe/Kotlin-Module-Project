abstract class Modes(val message: String) {
    companion object {
        var currentArchiveIndex: Int = 0
        val archives: MutableList<Archive> = mutableListOf()
    }
    abstract val selectParameter: MutableList<out Notable>
    abstract val duplicatable: MutableList<out Notable>
    abstract fun handleModeInput(choice: Int)
    abstract fun createObj(title: String)
}