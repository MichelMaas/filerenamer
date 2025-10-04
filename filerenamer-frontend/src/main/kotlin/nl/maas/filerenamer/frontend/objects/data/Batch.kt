package nl.maas.filerenamer.frontend.objects.data

data class Batch(override val name: String, override val magnetLink: String) : Downloadable {
}