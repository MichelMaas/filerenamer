package nl.maas.filerenamer.frontend.services

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.events.TestEvent
import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.extrapolation.NewNameExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import nl.maas.filerenamer.frontend.wicket.objects.SearchCriteria
import nl.maas.filerenamer.frontend.wicket.objects.SearchResult
import nl.maas.filerenamer.io.FileHandler
import nl.maas.filerenamer.io.RenameOrder
import org.springframework.stereotype.Component
import java.io.File
import javax.enterprise.event.Observes

@Component
open class FileFinderService {
    fun findFilesToRename(searchCriteria: SearchCriteria): MutableMap<String, ExtrapolationResult> {
        var filesIn = FileHandler().searchFilesIn(searchCriteria.path)
        return processFiles(filesIn, SequenceExtrapolator.instance(searchCriteria.sequenceType))
    }

    fun updateFiles(searchResult: SearchResult): SearchResult {
        val newResult = SearchResult(searchResult.sequenceType, searchResult.fileData)
        findNewNames(newResult.fileData)
        return newResult
    }

    private fun processFiles(
        filesIn: Map<File, List<File>>,
        sequenceExtrapolator: SequenceExtrapolator
    ): MutableMap<String, ExtrapolationResult> {
        var map: Map<String, ExtrapolationResult> = filesIn.keys.map {
            it.path to sequenceExtrapolator.findSequenceForFiles(filesIn[it]!!.map { file ->
                FileMetaData(file)
            })
        }.toMap()
        return findNewNames(map)
    }

    private fun findNewNames(map: Map<String, ExtrapolationResult>): MutableMap<String, ExtrapolationResult> {
        var map1 = map
        map1 = map1.filter { entry ->
            !entry.value.files.all { fmd ->
                NewNameExtrapolator.determineName(fmd, entry.value.files.size).substringBeforeLast(".")
                    .equals(fmd.name.substringBeforeLast("."))
            }
        }.toMap()

        map1.keys.forEach { NewNameExtrapolator().determineNameForFiles(map1[it]!!.files) }
        return map1.toMutableMap()
    }

    fun commitChanges(files: List<FileMetaData>) {
        val fileHandler = FileHandler()
        files.filterNot { it.newName.isNullOrEmpty() }.map { RenameOrder(it.file, it.newName!!) }
            .forEach { fileHandler.renameFiles(it) }
    }

    fun handleTestEvent(@Observes event:TestEvent){
        println(event.test)
    }
}