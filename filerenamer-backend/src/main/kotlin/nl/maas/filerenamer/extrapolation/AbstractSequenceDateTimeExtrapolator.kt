package nl.maas.filerenamer.extrapolation

import com.drew.imaging.FileType
import com.drew.imaging.FileTypeDetector
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifSubIFDDirectory
import net.bramp.ffmpeg.FFprobe
import nl.maas.filerenamer.domain.ExtrapolationResult
import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.io.path.absolutePathString

abstract class AbstractSequenceDateTimeExtrapolator(val pattern: String) : SequenceExtrapolator {
    override fun findSequenceForFiles(fileData: List<FileMetaData>): ExtrapolationResult {
        fileData.forEach { fileMetaData -> fileMetaData.sequence = getDateFor(fileMetaData.file.toPath()) }
        fileData.forEach { fileMetaData -> fileMetaData.proposedNames[0] }
        return ExtrapolationResult(ArrayList(fileData).sortedBy { fileMetaData -> fileMetaData.sequence })
    }

    private fun getCreateDateFrom(path: Path): LocalDateTime {
        var attr = Files.readAttributes(path, BasicFileAttributes::class.java)
        return LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
    }

    private fun isImage(path: Path): Boolean {
        var isimage = false
        try {
            isimage = BufferedInputStream(path.toFile().inputStream()).use { input ->
                when (FileTypeDetector.detectFileType(input)) {
                    FileType.Jpeg,
                    FileType.Png,
                    FileType.Gif,
                    FileType.Bmp,
                    FileType.WebP,
                    FileType.Tiff,
                    FileType.Psd,
                    FileType.Ico,
                    FileType.Pcx,
                    FileType.Raf,
                    FileType.Arw,
                    FileType.Crw,
                    FileType.Cr2,
                    FileType.Nef,
                    FileType.Orf,
                    FileType.Rw2 -> true

                    else -> false
                }
            }
        } catch (e: Exception) {
            isimage = false
        }
        return isimage
    }

    private fun fetchTakenTime(path: Path): LocalDateTime {
        val metaData = ImageMetadataReader.readMetadata(path.toFile())
        val exifSubIfd = metaData.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)

        return LocalDateTime.ofInstant(exifSubIfd.dateOriginal.toInstant(), ZoneId.systemDefault())
    }

    private fun getVideoDateWithFfprobe(path: Path): LocalDateTime {
        val ffprobe = FFprobe("ffprobe")
        val result = ffprobe.probe(path.absolutePathString())

        val formatCreationTime = result.format?.tags?.get("creation_time")
        if (formatCreationTime != null) {
            return parseFfprobeDate(formatCreationTime)!!
        }

        return result.streams
            ?.asSequence()
            ?.mapNotNull { stream -> stream.tags?.get("creation_time") }
            ?.mapNotNull { parseFfprobeDate(it) }
            ?.firstOrNull()
            ?: getCreateDateFrom(path)
    }

    private fun parseFfprobeDate(value: String): LocalDateTime? {
        return try {
            OffsetDateTime.parse(value)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (ex: Exception) {
            null
        }
    }

    private fun isVideoFile(path: Path): Boolean {
        if (!path.toFile().isFile || !path.toFile().canRead()) {
            return false
        }

        return try {
            val ffprobe = FFprobe("ffprobe")
            val result = ffprobe.probe(path.absolutePathString())

            result.streams?.any { stream ->
                stream.codec_type?.name.equals("VIDEO", ignoreCase = true)
            } ?: false
        } catch (ex: Exception) {
            false
        }
    }

    private fun getDateFor(path: Path): String {
        val dateTime: LocalDateTime
        if (isImage(path)) {
            dateTime = fetchTakenTime(path)
        } else if (isVideoFile(path)) {
            dateTime = getVideoDateWithFfprobe(path)
        } else {
            dateTime = getCreateDateFrom(path)
        }
        return DateTimeFormatter.ofPattern(pattern).format(dateTime)
    }
}