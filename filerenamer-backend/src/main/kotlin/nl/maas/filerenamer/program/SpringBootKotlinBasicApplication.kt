package nl.maas.filerenamer.program

import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.extrapolation.NewNameExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator.Companion.SEQUENCE.Companion.forValue
import nl.maas.filerenamer.extrapolation.SequenceNumberExtrapolator
import nl.maas.filerenamer.io.FileHandler
import nl.maas.filerenamer.io.RenameOrder
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.File

@SpringBootApplication
class SpringBootKotlinBasicApplication

fun main(args: Array<String>) {
//    runApplication<SpringBootKotlinBasicApplication>(*args)
    var path = if (args.isEmpty()) requestPath() else args[0]
    val filesIn = FileHandler().searchFilesIn(path)
    var sequenceExtrapolator = requestSequenceExtrapolator()
    for (key in filesIn.keys) {
        processFiles(filesIn, key, sequenceExtrapolator)
    }
}

private fun processFiles(filesIn: Map<File, List<File>>, key: File, sequenceExtrapolator: SequenceExtrapolator) {
    val fileData = filesIn.get(key)!!.map { file -> FileMetaData(file) }
    sequenceExtrapolator.findSequenceForFiles(fileData)
    NewNameExtrapolator().determineNameForFiles(fileData)
    FileHandler().saveFiles(*fileData.filter { fileMetaData -> !fileMetaData.newName.isNullOrBlank() && !fileMetaData.newName.equals(fileMetaData.file.name) }.map { fileMetaData -> RenameOrder(fileMetaData.file, fileMetaData.newName!!) }.toTypedArray())
}

private fun requestSequenceExtrapolator(): SequenceExtrapolator {
    print("Do the files you wish to rename have a (N)umerical sequence, or should they be sequenced by (D)ate?: ")
    val sequence = forValue(readLine()!!)
    var sequenceExtrapolator = SequenceExtrapolator.instance(sequence)
    return sequenceExtrapolator
}

private fun requestPath(): String {
    println("Path is required. Please provide a path: ")
    return readLine()!!
}
