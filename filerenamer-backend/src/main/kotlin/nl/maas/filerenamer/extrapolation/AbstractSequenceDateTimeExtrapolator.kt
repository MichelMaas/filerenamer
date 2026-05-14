package nl.maas.filerenamer.extrapolation

import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.io.MediaMetadataClient
import java.nio.file.Path
import java.time.format.DateTimeFormatter

abstract class AbstractSequenceDateTimeExtrapolator(val pattern: String) : SequenceExtrapolator {
    override fun findSequenceForFiles(fileData: List<FileMetaData>): ExtrapolationResult {
        fileData.forEach { fileMetaData -> fileMetaData.sequence = getDateFor(fileMetaData.file.toPath()) }
        fileData.forEach { fileMetaData -> fileMetaData.proposedNames[0] }
        return ExtrapolationResult(ArrayList(fileData).sortedBy { fileMetaData -> fileMetaData.sequence })
    }


    private fun getDateFor(path: Path): String {
        val dateTime = MediaMetadataClient.fetchTakenDate(path)
        return DateTimeFormatter.ofPattern(pattern).format(dateTime)
    }
}