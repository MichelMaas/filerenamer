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
        val sequenceMap = HashMap<Int, List<FileMetaData>>()
        var extrapolationFailures = ExtrapolationFailures();
        val warnings = ArrayList<Warning>();
        determineBestSequences(result, fileData, sequenceMap)

        val failedToDetermine = sequenceMap.keys.filter { i -> sequenceMap.get(i)?.size ?: 0 != 1 }

        if (failedToDetermine.isNotEmpty()) {
            for (nr in failedToDetermine) {
                val options = sequenceMap[nr]
                if (options?.isEmpty() ?: true) {
                    if (nr >= result.sequence.first() && nr <= result.sequence.last()) {
                        val message =
                            "Er is geen geldige optie gevonden voor volgnummer ${nr}. Dit nummer wordt genegeerd"
                        sequenceMap[result.sequence.first()]?.let { opt ->
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
                    extrapolationFailures.add(sequenceMap[result.sequence.first()]!![0].dirName, extrapolationFailure)
                }
            }
        }

        sequenceMap.keys.forEach { i -> sequenceMap.get(i)?.firstOrNull()?.sequence = i.toString() }
        result.failures.addAll<Int>(extrapolationFailures.getTypedFailures())
        return result
    }

    private fun determineBestSequences(
        result: ExtrapolationResult,
        fileData: List<FileMetaData>,
        sequenceMap: HashMap<Int, List<FileMetaData>>
    ) {
        result.sequence.forEachIndexed { index, i ->
            val list = mutableListOf<FileMetaData>()
            if (fileData[index].hasPotentialSequenceFor(i)) {
                list.add(fileData[index])
            } else {
                fileData.filter { fileMetaData -> fileMetaData.hasPotentialSequenceFor(i) }
            }
            sequenceMap.put(i, list)
        }

        sequenceMap.keys.filter { i -> sequenceMap.get(i)?.size ?: 0 > 1 }.forEach {
            sequenceMap.replace(
                it,
                sequenceMap.get(it)!!.filter { ms ->
                    sequenceMap.filter { os -> os.value.size == 1 }.none { os -> os.value.contains(ms) }
                })
        }
    }


    private fun determineSequencePositions(result: ExtrapolationResult): ExtrapolationResult {
        val files = result.files
        result.sequence =
            findSequence(files.flatMap { it.extrapolatedName.map { it.toIntOrNull() }.filterNotNull() }, files.size)
        files.forEach { fileData ->
            fileData.potentialSequenceNumbers =
                "[0-9]+".toRegex().findAll(fileData.name).map { it.value }
                    .map { it.toIntOrNull() }.filterNotNull()
                    .filter { result.sequence.contains(it) }.map { it.toString() }
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

    private fun findSequence(numbers: List<Int>, sequenceSize: Int): List<Int> {
        val sequenceStart =
            numbers.firstOrNull { numbers.contains(it + sequenceSize - 1) }
        return if (sequenceStart == null) (1..sequenceSize).toList() else (sequenceStart..(sequenceStart + sequenceSize - 1)).toList()
    }

}