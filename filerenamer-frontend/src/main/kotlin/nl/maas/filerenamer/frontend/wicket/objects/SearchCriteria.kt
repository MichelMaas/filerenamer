package nl.maas.filerenamer.frontend.wicket.objects

import java.io.File
import java.io.Serializable

data class SearchCriteria(var file: File? = null) : Serializable {

    companion object {
        fun default() = SearchCriteria()
    }
}