import NotesApp.callHistory
import NotesApp.mode
import NotesApp.select

object ArchivesMode : Modes("архив") {
    override val selectParameter: MutableList<Archive>
        get() {
            return archives
        }
    override val duplicatable
        get() = archives

    override fun handleModeInput(choice: Int) {
        currentArchiveIndex = choice
        mode = NotesMode
        callHistory.addLast(ArchivesMode)
        select(archives[currentArchiveIndex].notes)
    }

    override fun createObj(title: String) {
        val archive = Archive(title)
        archives.add(archive)
    }
}