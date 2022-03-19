package bot

import bwapi.Player
import bwapi.UnitType
import bwapi.Unit as Unit

class CommandCenterBot {

    fun makeScv(myUnit: Unit, self: Player) {
        if (myUnit.type == UnitType.Terran_Command_Center
            && myUnit.canTrain()
            && !myUnit.isTraining
            && self.supplyTotal() - self.supplyUsed() > 2
            && self.minerals() >= 50) {

            myUnit.train(UnitType.Terran_SCV)
        }
    }
}
