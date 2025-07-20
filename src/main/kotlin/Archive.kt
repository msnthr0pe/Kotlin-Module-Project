data class Archive (
    override var name: String = "default",
    val notes: MutableList<Note> = mutableListOf(),
) : Notable