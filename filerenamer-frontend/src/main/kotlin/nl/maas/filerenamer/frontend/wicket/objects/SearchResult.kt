package nl.maas.filerenamer.frontend.wicket.objects

import nl.maas.filerenamer.extrapolation.FileMetaData
import java.io.Serializable

data class SearchResult(
    val sequenceType: SearchCriteria.SequenceType,
    val fileData: MutableMap<String, MutableList<FileMetaData>>
) : Serializable {

    fun isEmpty() = fileData.isEmpty()
}