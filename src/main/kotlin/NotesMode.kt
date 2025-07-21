import NotesApp.checkForEmptyString
import NotesApp.input
import NotesApp.mode

object NotesMode : Modes("заметку") {
    var currentNote: Note = Note()
    override val selectParameter: MutableList<Note>
        get() {
            return archives[currentArchiveIndex].notes
        }
    override val duplicatable: MutableList<out Notable>
        get() = archives[currentArchiveIndex].notes

    override fun handleModeInput(choice: Int) {
        (mode as NotesMode).currentNote = archives[currentArchiveIndex].notes[choice]
        println("Содержимое заметки: ${(mode as NotesMode).currentNote.contents}")
    }

    override fun createObj(title: String) {
        print("Введите содержимое файла: ")
        var contents = input.nextLine()
        if (checkForEmptyString(contents)) {
            return
        }
        val note = Note(title, contents)
        archives[currentArchiveIndex].notes.add(note)        }
}