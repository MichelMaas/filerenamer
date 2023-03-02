package nl.maas.filerenamer.extrapolation

import nl.maas.filerenamer.domain.ExtrapolationFailure
import nl.maas.filerenamer.domain.ExtrapolationFailures
import nl.maas.filerenamer.domain.ExtrapolationResult
import nl.maas.filerenamer.domain.Warning

class SequenceNumberExtrapolator : SequenceExtrapolator {

    override fun findSequenceForFiles(fileData: List<FileMetaData>): ExtrapolationResult {
        val result = ExtrapolationResult(fileData)
        fileData.forEach { fileMetaData -> determineSequencePositions(result) }
        determineSequence(result)
        return prepareErrors(result)
    }

    private fun prepareErrors(result: ExtrapolationResult): ExtrapolationResult {
        result.files.forEach { it.potentialSequenceNumbers.add("NONE") }
        return result
    }

    private fun determineSequence(result: ExtrapolationResult): ExtrapolationResult {
        val fileData = result.files
        result.min = determineSequenceNumbers(fileData, "MIN", result)
        result.max = determineSequenceNumbers(fileData, "MAX", result)
        val sequenceMap = HashMap<Int, List<FileMetaData>>()
        var extrapolationFailures = ExtrapolationFailures();
        val warnings = ArrayList<Warning>();
        for (i in result.min..result.max) {
            val list = fileData.filter { fileMetaData -> fileMetaData.hasPotentialSequenceFor(i) }
            sequenceMap.put(i, list)
        }
        val failedToDetermine = sequenceMap.keys.filter { i -> sequenceMap.get(i)?.size ?: 0 != 1 }

        if (failedToDetermine.isNotEmpty()) {
            for (nr in failedToDetermine) {
                val options = sequenceMap[nr]
                if (options?.isEmpty() ?: true) {
                    if (nr >= result.min && nr <= result.max) {
                        val message =
                            "Er is geen geldige optie gevonden voor volgnummer ${nr}. Dit nummer wordt genegeerd"
                        sequenceMap[result.min]?.let { opt ->
                            if (opt.isNotEmpty()) {
                                opt[0]?.let {
                                    warnings.add(Warning(it.dirName, message))
                                }
                            }
                        }
                        println(message)
                    }
                    sequenceMap.remove(nr)
                } else {
                    val message =
                        "Het bepalen van een bestand voor volgnummer ${nr}."
                    println(message)
                    var extrapolationFailure =
                        ExtrapolationFailure(ExtrapolationFailures.FailureType.TooMany, nr, options!!.toMutableList())
                    extrapolationFailures.add(sequenceMap[result.min]!![0].dirName, extrapolationFailure)
                }
            }
        }

        sequenceMap.keys.forEach { i -> sequenceMap.get(i)?.firstOrNull()?.sequence = i.toString() }
        result.failures.addAll<Int>(extrapolationFailures.getTypedFailures())
        return result
    }

    private fun determineSequenceNumbers(
        fileData: List<FileMetaData>,
        edgeNumber: String,
        result: ExtrapolationResult
    ): Int {
        var numbers = fileData.flatMap { fileMetaData ->
            fileMetaData.potentialSequenceNumbers.map { it.toIntOrNull() }.filterNotNull()
        }
        var empty = false
        if (numbers.isNullOrEmpty()) {
            result.failures.add(
                fileData[0].dirName,
                ExtrapolationFailure(ExtrapolationFailures.FailureType.None, 0, fileData.toMutableList())
            )
            empty = true
        }
        var lowest = if (empty) 0 else numbers.sorted().get(0)
        var lastValid = if (empty) fileData.size else numbers.sorted()
            .reduceIndexed { index, previous, current -> if (current - previous > 10) previous else current }

        if (lastValid.equals(lowest) || (lastValid - lowest > fileData.size && fileData.none {
                it.potentialSequenceNumbers.contains(
                    lastValid.toString()
                )
            })) {
            result.failures.add(
                fileData[0].dirName,
                ExtrapolationFailure(ExtrapolationFailures.FailureType.None, 0, fileData.toMutableList())
            )
            lowest = 0;
            lastValid = fileData.size
            fileData.forEach { it.potentialSequenceNumbers = (lowest..lastValid).map { it.toString() }.toMutableSet() }
        }
        val range = lowest..lastValid
        val notPresent = range.filterNot { nr -> numbers.contains(nr) }

        return if (edgeNumber.equals("MIN")) lowest else lastValid
    }

    private fun determineSequencePositions(result: ExtrapolationResult): ExtrapolationResult {
        val files = result.files
        files.forEach { fileData ->
            fileData.potentialSequenceNumbers =
                fileData.extrapolatedName.filterIndexed { index, s ->
                    files.filterNot { f -> f.name.equals(fileData.name) }
                        .none { f -> f.extrapolatedName[index].equals(s) }
                }.map { it.toIntOrNull() }.filterNotNull().filterNot { it > files.size }.map { it.toString() }
                    .toMutableSet()
            if (fileData.potentialSequenceNumbers.isEmpty()) {
                result.failures.add(
                    fileData.dirName,
                    ExtrapolationFailure(
                        ExtrapolationFailures.FailureType.NoNumberForFile,
                        fileData.dirName,
                        0,
                        fileData
                    )
                )
            }
        }
        return result
    }


}