package listener

import bot.ScvBot
import bwapi.*
import bwapi.Unit
import tile.CustomAnalyseTile

class CustomListener: DefaultBWListener() {
    private val bwClient = BWClient(this)
    private lateinit var game: Game
    private lateinit var customAnalyseTile: CustomAnalyseTile
    private lateinit var scvBot: ScvBot

    fun start() {
        bwClient.startGame()
    }

    override fun onStart() {
        game = bwClient.game
        game.setLocalSpeed(35)//게임 속도 30이 기본, 토너먼트에선 20
        customAnalyseTile = CustomAnalyseTile(game)
        scvBot = ScvBot(game)
    }

    override fun onFrame() {
        val self: Player = game.self()
        game.drawTextScreen(10, 10, "Playing as ${self.name} - ${self.race}")

        scvBot.gatherMineral()

        /*val myUnits: List<Unit> = game.allUnits
        myUnits.forEach { myUnit ->
            if (myUnit.type == UnitType.Terran_Command_Center && self.minerals() >= 50) {
                myUnit.train(UnitType.Terran_SCV)
            }

            if (myUnit.type.isWorker && myUnit.isIdle) {
                if ((self.supplyTotal() - self.supplyUsed() < 2) && self.minerals() >= 100) {
                    val buildTile: TilePosition? = customAnalyseTile.getBuildTile(myUnit, UnitType.Terran_Supply_Depot, self.startLocation)
                    buildTile.let {
                        myUnit.build(UnitType.Terran_Supply_Depot, buildTile)
                    }
                }

                if (!myUnit.isConstructing) {
                    var closestMineral: Unit? = null


                    //find the closest mineral
                    for (neutralUnit in game.neutral().units) {
                        game.neutral().units.forEach { neutral ->
                            if (neutral.type.isMineralField) {
                                if (closestMineral == null ||
                                    myUnit.getDistance(neutral) < myUnit.getDistance(closestMineral)) {
                                    closestMineral = neutral
                                }
                            }
                        }
                    }
                    //if a mineral patch was found, send the worker to gather it
                    closestMineral.let {
                        myUnit.gather(closestMineral, false)
                    }
                }
            }

            if (myUnit.type == UnitType.Terran_SCV) {
                game.drawTextMap(
                    myUnit.position.getX(),
                    myUnit.position.getY(),
                    "TilePos: ${myUnit.tilePosition} Pos: ${myUnit.position}"
                )
                game.drawLineMap(
                    myUnit.position.getX(),
                    myUnit.position.getY(),
                    myUnit.orderTargetPosition.getX(),
                    myUnit.orderTargetPosition.getY(),
                    Color.Black
                )
            }
        }*/
    }
}
