package bot

import bwapi.Game
import bwapi.Unit as Unit

class ScvBot(
    private val game: Game
) {
    fun gatherMineral() {
        game.allUnits
            .filter { it.type.isWorker && it.isIdle }
            .forEach { scv ->
                val minerals = game.neutral().units
                    .filter { it.type.isMineralField}
                    .sortedBy { scv.getDistance(game.getClosestUnit(it.position)) }
                scv.gather(minerals.first(), false)
                return@forEach
            }
    }
}
