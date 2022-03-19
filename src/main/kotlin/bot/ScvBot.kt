package bot

import bwapi.Game
import bwapi.Player
import bwapi.Unit as Unit
import bwapi.UnitType
import tile.CustomAnalyseTile

class ScvBot(
    private val game: Game
) {
    fun gatherMineral(myUnit: Unit) {
        if (myUnit.type.isWorker && myUnit.isIdle) {
            val minerals = game.neutralUnits
                .filter { it.type.isMineralField }
                .sortedBy { myUnit.getDistance(game.getClosestUnit(it.position)) }
            myUnit.gather(minerals.first(), false)
        }
    }

    fun selectBuildSupplyDepotScv(): Unit =
        game.allUnits.first { it.type.isWorker && it.canBuild(UnitType.Terran_Supply_Depot) }

    fun buildSupplyDepot(
        scv: Unit,
        self: Player,
        customAnalyseTile: CustomAnalyseTile
    ): Boolean = customAnalyseTile.getBuildTile(
        scv,
        UnitType.Terran_Supply_Depot,
        self.startLocation
    )?.let {
        return scv.build(UnitType.Terran_Supply_Depot, it)
    }?: false

}
