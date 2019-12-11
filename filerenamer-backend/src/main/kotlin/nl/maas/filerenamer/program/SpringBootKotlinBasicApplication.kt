package nl.maas.filerenamer.program

import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.extrapolation.NewNameExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceNumberExtrapolator
import nl.maas.filerenamer.io.FileHandler
import nl.maas.filerenamer.io.RenameOrder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootKotlinBasicApplication

fun main(args: Array<String>) {
//    runApplication<SpringBootKotlinBasicApplication>(*args)
    val filesIn = FileHandler().searchFilesIn("/shares/anime/Vinland Saga/")
//    val filesIn=FileHandler().searchFilesIn("/shares/torrents/Downloads/[AnimeRG] Death Note [Complete] [720p] [khatake2]/")
    val fileData = filesIn.get(filesIn.keys.first())!!.map { file -> FileMetaData(file) }
    SequenceNumberExtrapolator().findSequenceForFiles(fileData)
    NewNameExtrapolator().determineNameForFiles(fileData)
    FileHandler().saveFiles(*fileData.filter { fileMetaData -> !fileMetaData.newName.isNullOrBlank() }.map { fileMetaData -> RenameOrder(fileMetaData.file,fileMetaData.newName!!) }.toTypedArray())
}
