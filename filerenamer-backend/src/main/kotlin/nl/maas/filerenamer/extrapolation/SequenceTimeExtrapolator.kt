package nl.maas.filerenamer.extrapolation

import nl.maas.filerenamer.domain.ExtrapolationResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SequenceTimeExtrapolator : SequenceExtrapolator {
    override fun findSequenceForFiles(fileData: List<FileMetaData>): ExtrapolationResult {
        fileData.forEach { fileMetaData -> fileMetaData.sequence = getDateFor(fileMetaData.file.toPath()) }

        return ExtrapolationResult(ArrayList(fileData).sortedBy { fileMetaData -> fileMetaData.sequence })
    }

    private fun getDateFor(path: Path): String {
        var attr = Files.readAttributes(path, BasicFileAttributes::class.java)
        val dateTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("HHmmssS").format(dateTime)
    }
}