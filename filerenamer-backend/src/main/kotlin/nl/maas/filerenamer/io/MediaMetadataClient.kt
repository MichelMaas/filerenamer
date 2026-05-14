package nl.maas.filerenamer.io

import com.drew.imaging.FileType
import com.drew.imaging.FileTypeDetector
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.ExifSubIFDDirectory
import com.drew.metadata.exif.GpsDirectory
import net.bramp.ffmpeg.FFprobe
import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.io.path.absolutePathString

object MediaMetadataClient {

    private const val FFMPEG_LOCATION_TAG = "location"
    private const val QUICKTIME_LOCATION_TAG = "com.apple.quicktime.location.ISO6709"

    fun fetchTakenDate(path: Path): LocalDateTime {
        return when {
            isImage(path) -> fetchImageTakenDate(path) ?: fetchCreateDate(path)
            isVideoFile(path) -> fetchVideoTakenDate(path) ?: fetchCreateDate(path)
            else -> fetchCreateDate(path)
        }
    }

    fun fetchLocation(path: Path): Map<String, Double> {
        return when {
            isImage(path) -> fetchImageLocation(path)
            isVideoFile(path) -> fetchVideoLocation(path)
            else -> emptyMap()
        }
    }

    private fun fetchImageTakenDate(path: Path): LocalDateTime? {
        return try {
            val metadata = ImageMetadataReader.readMetadata(path.toFile())
            val exifSubIfd = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
            val dateOriginal = exifSubIfd?.dateOriginal ?: return null

            LocalDateTime.ofInstant(dateOriginal.toInstant(), ZoneId.systemDefault())
        } catch (ex: Exception) {
            null
        }
    }

    private fun fetchImageLocation(path: Path): Map<String, Double> {
        return try {
            val metadata = ImageMetadataReader.readMetadata(path.toFile())
            val gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory::class.java)
            val geoLocation = gpsDirectory?.geoLocation ?: return emptyMap()

            mapOf(
                "lat" to geoLocation.latitude,
                "long" to geoLocation.longitude
            )
        } catch (ex: Exception) {
            emptyMap()
        }
    }

    private fun fetchVideoTakenDate(path: Path): LocalDateTime? {
        return try {
            val result = ffprobe(path)

            val formatCreationTime = result.format?.tags?.get("creation_time")
            if (formatCreationTime != null) {
                parseFfprobeDate(formatCreationTime)?.let { return it }
            }

            result.streams
                ?.asSequence()
                ?.mapNotNull { stream -> stream.tags?.get("creation_time") }
                ?.mapNotNull { parseFfprobeDate(it) }
                ?.firstOrNull()
        } catch (ex: Exception) {
            null
        }
    }

    private fun fetchVideoLocation(path: Path): Map<String, Double> {
        return try {
            val result = ffprobe(path)

            val formatLocation = result.format?.tags?.get(FFMPEG_LOCATION_TAG)
                ?: result.format?.tags?.get(QUICKTIME_LOCATION_TAG)

            if (formatLocation != null) {
                parseIso6709Location(formatLocation)?.let { return it }
            }

            result.streams
                ?.asSequence()
                ?.mapNotNull { stream ->
                    stream.tags?.get(FFMPEG_LOCATION_TAG)
                        ?: stream.tags?.get(QUICKTIME_LOCATION_TAG)
                }
                ?.mapNotNull { parseIso6709Location(it) }
                ?.firstOrNull()
                ?: emptyMap()
        } catch (ex: Exception) {
            emptyMap()
        }
    }

    private fun parseIso6709Location(value: String): Map<String, Double>? {
        val regex = Regex("""^([+-]\d+(?:\.\d+)?)([+-]\d+(?:\.\d+)?)([+-]\d+(?:\.\d+)?)?/?$""")
        val match = regex.matchEntire(value.trim()) ?: return null

        val latitude = match.groupValues[1].toDoubleOrNull() ?: return null
        val longitude = match.groupValues[2].toDoubleOrNull() ?: return null

        return mapOf(
            "lat" to latitude,
            "long" to longitude
        )
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

    private fun fetchCreateDate(path: Path): LocalDateTime {
        val attr = Files.readAttributes(path, BasicFileAttributes::class.java)
        return LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault())
    }

    private fun isImage(path: Path): Boolean {
        return try {
            BufferedInputStream(path.toFile().inputStream()).use { input ->
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
        } catch (ex: Exception) {
            false
        }
    }

    private fun isVideoFile(path: Path): Boolean {
        if (!path.toFile().isFile || !path.toFile().canRead()) {
            return false
        }

        return try {
            val result = ffprobe(path)

            result.streams?.any { stream ->
                stream.codec_type?.name.equals("VIDEO", ignoreCase = true)
            } ?: false
        } catch (ex: Exception) {
            false
        }
    }

    private fun ffprobe(path: Path) = FFprobe("ffprobe").probe(path.absolutePathString())
}