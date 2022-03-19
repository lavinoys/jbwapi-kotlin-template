package tile

import bwapi.*
import bwapi.Unit

class CustomAnalyseTile(
    private val game: Game
) {
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
