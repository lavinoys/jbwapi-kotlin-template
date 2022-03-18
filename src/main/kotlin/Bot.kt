import bwapi.*
import bwapi.Unit


class Bot : DefaultBWListener() {
    private var bwClient = BWClient(this)
    private lateinit var game: Game

    override fun onStart() {
        game = bwClient.game

        game.setLocalSpeed(35)//게임 속도 30이 기본, 토너먼트에선 20
    }

    override fun onFrame() {
        val self: Player = game.self()
        game.drawTextScreen(10, 10, "Playing as ${self.name} - ${self.race}")
        game.drawTextScreen(10, 230, "Resources: ${self.minerals()} minerals, ${self.gas()} gas")

        val myUnits: List<Unit> = game.allUnits
        myUnits.forEach { myUnit ->
            if (myUnit.type == UnitType.Terran_Command_Center && self.minerals() >= 50) {
                myUnit.train(UnitType.Terran_SCV)
            }

            if (myUnit.type.isWorker && myUnit.isIdle) {
                var closestMineral: Unit? = null

                //find the closest mineral
                for (neutralUnit in game.neutral().units) {
                    if (neutralUnit.type.isMineralField) {
                        if (closestMineral == null || myUnit.getDistance(neutralUnit) < myUnit.getDistance(
                                closestMineral
                            )
                        ) {
                            closestMineral = neutralUnit
                        }
                    }
                }

                //if a mineral patch was found, send the worker to gather it
                if (closestMineral != null) {
                    myUnit.gather(closestMineral, false)
                }
            }

            if (myUnit.type == UnitType.Terran_SCV) {
                game.drawTextMap(
                    myUnit.position.getX(),
                    myUnit.position.getY(),
                    "TilePos: ${myUnit.tilePosition} Pos: ${myUnit.position}"
                )
            }
            if (self.supplyTotal() - self.supplyUsed() < 8 && self.minerals() >= 100 && myUnit.type == UnitType.Terran_SCV) {
                if (myUnit.type == UnitType.Terran_SCV) {
                    getBuildTile(myUnit, UnitType.Terran_Supply_Depot, self.startLocation)?.let {
                        myUnit.build(UnitType.Terran_Supply_Depot, it)
                    }
                    return
                }
            }
        }
    }

    fun start() {
        bwClient.startGame()
    }

    // Returns a suitable TilePosition to build a given building type near
    // specified TilePosition aroundTile, or null if not found. (builder parameter is our worker)
    fun getBuildTile(builder: Unit, buildingType: UnitType, aroundTile: TilePosition): TilePosition? {
        val ret: TilePosition? = null
        var maxDist = 3
        val stopDist = 40

        // Refinery, Assimilator, Extractor
        if (buildingType.isRefinery) {
            for (n in game.neutral().units) {
                if (n.type === UnitType.Resource_Vespene_Geyser &&
                    Math.abs(n.tilePosition.getX() - aroundTile.getX()) < stopDist &&
                    Math.abs(n.tilePosition.getY() - aroundTile.getY()) < stopDist
                ) {
                    return n.tilePosition
                }
            }
        }
        while (maxDist < stopDist && ret == null) {
            for (i in aroundTile.getX() - maxDist..aroundTile.getX() + maxDist) {
                for (j in aroundTile.getY() - maxDist..aroundTile.getY() + maxDist) {
                    if (game.canBuildHere(TilePosition(i, j), buildingType, builder, false)) {
                        // units that are blocking the tile
                        var unitsInWay = false
                        for (u in game.allUnits) {
                            if (u.id == builder.id) continue
                            if (Math.abs(u.tilePosition.getX() - i) < 4 && Math.abs(u.tilePosition.getY() - j) < 4) unitsInWay =
                                true
                        }
                        if (!unitsInWay) {
                            return TilePosition(i, j)
                        }
                        // creep for Zerg
                        if (buildingType.requiresCreep()) {
                            var creepMissing = false
                            for (k in i..i + buildingType.tileWidth()) {
                                for (l in j..j + buildingType.tileHeight()) {
                                    if (!game.hasCreep(k, l)) creepMissing = true
                                    break
                                }
                            }
                            if (creepMissing) continue
                        }
                    }
                }
            }
            maxDist += 2
        }
        if (ret == null) game.printf("Unable to find suitable build position for $buildingType")
        return ret
    }
}

fun main() {
    val bot = Bot()
    bot.start()
}
