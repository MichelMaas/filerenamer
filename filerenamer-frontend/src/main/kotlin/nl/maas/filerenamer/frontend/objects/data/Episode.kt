package nl.maas.filerenamer.frontend.objects.data

data class Episode(override val name: String, override val magnetLink: String) : Downloadable {
}