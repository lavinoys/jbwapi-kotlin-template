package listener

import bot.CommandCenterBot
import bot.ScvBot
import bwapi.*
import bwapi.Unit as Unit
import draw.DrawVisible
import tile.CustomAnalyseTile
import util.CodeUtils

class CustomListener(
    private val commandCenterBot: CommandCenterBot
): DefaultBWListener() {
    private val bwClient = BWClient(this)
    private val codeUtils = CodeUtils()
    private lateinit var game: Game
    private lateinit var customAnalyseTile: CustomAnalyseTile
    private lateinit var scvBot: ScvBot
    private lateinit var drawVisible: DrawVisible
    private var buildingScv: Unit? = null

    fun start() {
        bwClient.startGame()
    }

    override fun onStart() {
        game = bwClient.game
        game.setLocalSpeed(35)//게임 속도 30이 기본, 토너먼트에선 20
        customAnalyseTile = CustomAnalyseTile(game)
        scvBot = ScvBot(game)
        drawVisible = DrawVisible(game)
    }

    override fun onFrame() {
        val self: Player = game.self()
        game.drawTextScreen(10, 10, "Playing as ${self.name} - ${self.race}")
        game.drawTextScreen(10, 20, "supplyTotal : ${self.supplyTotal()}, supplyUsed : ${self.supplyUsed()}")

        game.allUnits
            .filter { it.isCompleted }
            .forEach { myUnit ->
                drawVisible.worker(myUnit)
                drawVisible.supplyDepot(myUnit)
                commandCenterBot.makeScv(myUnit, self)
                scvBot.gatherMineral(myUnit)
        }

        if (buildingScv == null
            && self.supplyTotal()-self.supplyUsed() < 6
            && self.minerals() >= 100) {
            buildingScv = scvBot.selectBuildSupplyDepotScv()
        } else {
            buildingScv?.let {
                scvBot.buildSupplyDepot(it, self, customAnalyseTile)
            }
        }
    }


    override fun onUnitComplete(unit: Unit?) {
        unit?.let {
            if (it.type == UnitType.Terran_Supply_Depot && it.isCompleted) {
                buildingScv = null
            }
        }

    }
}
