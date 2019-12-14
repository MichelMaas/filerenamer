package nl.maas.filerenamer.extrapolation

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SequenceDateExtrapolator : SequenceExtrapolator {
    override fun findSequenceForFiles(fileData: List<FileMetaData>) {
        fileData.forEach { fileMetaData -> fileMetaData.sequence = getDateFor(fileMetaData.file.toPath()) }
    }

    private fun getDateFor(path: Path): String {
        var attr = Files.readAttributes(path, BasicFileAttributes::class.java)
        val dateTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(dateTime)
    }
}