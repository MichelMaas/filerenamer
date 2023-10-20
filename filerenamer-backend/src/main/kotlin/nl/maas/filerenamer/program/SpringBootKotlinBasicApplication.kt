package nl.maas.filerenamer.program

import nl.maas.filerenamer.domain.ExtrapolationFailure
import nl.maas.filerenamer.domain.ExtrapolationFailures.FailureType
import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.domain.enums.SEQUENCE
import nl.maas.filerenamer.extrapolation.FileMetaData
import nl.maas.filerenamer.extrapolation.NewNameExtrapolator
import nl.maas.filerenamer.extrapolation.SequenceExtrapolator
import nl.maas.filerenamer.io.FileHandler
import nl.maas.filerenamer.io.RenameOrder
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.File

@SpringBootApplication
class SpringBootKotlinBasicApplication

fun main(args: Array<String>) {
//    runApplication<SpringBootKotlinBasicApplication>(*args)
    var path = if (args.isEmpty()) requestPath() else args[0]
    var dry = dryRun()
    val filesIn = FileHandler().searchFilesIn(path)
    var sequenceExtrapolator = requestSequenceExtrapolator()
    for (key in filesIn.keys) {
        processFiles(filesIn, key, sequenceExtrapolator, dry)
    }
}

private fun processFiles(
    filesIn: Map<File, List<File>>,
    key: File,
    sequenceExtrapolator: SequenceExtrapolator,
    dryRun: Boolean
) {
    val fileData = filesIn.get(key)!!.map { file -> FileMetaData(file) }
    val sequenceForFiles = sequenceExtrapolator.findSequenceForFiles(fileData)
    processFailedSequences(sequenceForFiles)
    NewNameExtrapolator().determineNameForFiles(sequenceForFiles.files)
    if (dryRun) {
        fileData.filter { fileMetaData ->
            !fileMetaData.newName.isNullOrBlank() && !fileMetaData.newName.equals(
                fileMetaData.file.name
            )
        }.forEach { println("::${it.name} -> ${it.newName}") }
    } else {
        FileHandler().renameFiles(*fileData.filter { fileMetaData ->
            !fileMetaData.newName.isNullOrBlank() && !fileMetaData.newName.equals(
                fileMetaData.file.name
            )
        }.map { fileMetaData -> RenameOrder(fileMetaData.file, fileMetaData.newName!!) }.toTypedArray())
    }
}

private fun processFailedSequences(result: ExtrapolationResult) {
    val options = result.failures.getTypedFailures<Int>()
    options.keys.filter { key -> FailureType.TooMany.equals(options[key]!!.toTypedArray()[0].type) }
        .forEach { key -> requestManualSequence(options[key]!!) }
    options.keys.filter { key -> FailureType.None.equals(options[key]!!.toTypedArray()[0].type) }
        .forEach { key -> warnNotFound(key, options[key]!!) }
}

fun warnNotFound(folder: String, failures: Set<ExtrapolationFailure<Int>>) {
    failures.forEach { failure -> println("Voor map ${folder} is volgnummer ${failure.sequence} niet gevonden.") }
}

fun requestManualSequence(failures: Set<ExtrapolationFailure<Int>>) {

    failures.forEach {
        val nr = it.sequence
        val options = it.failed
        for (i in 0..options!!.size - 1) {
            println("${i}: ${options[i].name}")
        }
        print("Maak een keuze van 0 - ${options.size - 1}: ")
        val readLine = readLine()!!.toInt()
        options[readLine].sequence = nr.toString()
    }
}

private fun requestSequenceExtrapolator(): SequenceExtrapolator {
    print("Do the files you wish to rename have a (N)umerical sequence, or should they be sequenced by (D)ate, (T)ime or Time(s)tamp?: ")
    val sequence = SEQUENCE.forValue(readLine()!!)
    var sequenceExtrapolator = SequenceExtrapolator.instance(sequence)
    return sequenceExtrapolator
}

private fun requestPath(): String {
    println("Path is required. Please provide a path: ")
    return readLine()!!
}

private fun dryRun(): Boolean {
    println("Do you want a dry run? enter 'dry' in order to run dry: ")
    return if ("dry".equals(readLine())) true else false
}
