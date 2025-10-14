package nl.maas.filerenamer.frontend.objects.data

data class Show(
    val name: String,
    val showHome: String,
    var information: String = "",
    var pictureURL: String = "",
    var status: String = "",
    var batches: List<Batch> = listOf(),
    var episodes: List<Episode> = listOf()
)
