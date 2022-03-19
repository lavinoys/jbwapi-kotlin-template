import bot.CommandCenterBot
import listener.CustomListener

fun main() {
    CustomListener(
        CommandCenterBot()
    ).start()
}
