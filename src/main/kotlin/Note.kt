data class Note (
    override var name: String = "default",
    var contents: String? = null,
) : Notable