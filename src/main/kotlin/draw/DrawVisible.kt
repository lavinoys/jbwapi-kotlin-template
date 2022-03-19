package draw

import bwapi.Game
import bwapi.UnitType
import bwapi.Unit as Unit

class DrawVisible(
    private val game: Game
) {

    fun worker(myUnit: Unit) {
        if (myUnit.type.isWorker) {
            drawCommon(myUnit)
            /*if (myUnit.type.isWorker) {
                game.drawLineMap(
                    myUnit.position.getX(),
                    myUnit.position.getY(),
                    myUnit.orderTargetPosition.getX(),
                    myUnit.orderTargetPosition.getY(),
                    Color.Black
                )
            }*/
        }
    }

    fun supplyDepot(myUnit: Unit) {
        if (myUnit.type == UnitType.Terran_Supply_Depot) {
            drawCommon(myUnit)
        }
    }

    private fun drawCommon(myUnit: Unit) {
        game.drawTextMap(
            myUnit.position.getX(),
            myUnit.position.getY(),
            "TilePos: ${myUnit.tilePosition} Pos: ${myUnit.position} isCompleted: ${myUnit.isCompleted}"
        )
    }
}
