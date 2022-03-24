package nl.maas.filerenamer.frontend.wicket.objects

data class Language(
    val code: String,
    val name: String,
    val pages: List<Page>
)