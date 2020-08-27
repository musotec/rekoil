package tech.muso.rekoil.core

private const val DEBUG = false

internal object Log {

    private const val ANSI_RESET = "\u001B[0m"
    private const val ANSI_BLACK = "\u001B[30m"
    private const val ANSI_RED = "\u001B[31m"
    private const val ANSI_GREEN = "\u001B[32m"
    private const val ANSI_YELLOW = "\u001B[33m"
    private const val ANSI_BLUE = "\u001B[34m"
    private const val ANSI_PURPLE = "\u001B[35m"
    private const val ANSI_CYAN = "\u001B[36m"
    private const val ANSI_WHITE = "\u001B[37m"

    const val tag = "rekoil"

    inline fun d(message: Any?) {
        printdbg("${ANSI_BLUE}Log.d/$tag\t$message$ANSI_RESET")
    }

    inline fun i(message: Any?) {
        printdbg("${ANSI_GREEN}Log.i/$tag\t$message$ANSI_RESET")
    }

    inline fun w(message: Any?) {
        printdbg("${ANSI_YELLOW}Log.w/$tag\t$message$ANSI_RESET")
    }

    inline fun e(message: Any?) {
        printdbg("${ANSI_RED}Log.e/$tag\t$message$ANSI_RESET")
    }

    inline fun v(message: Any?) {
        printdbg("${ANSI_WHITE}Log.v/$tag\t$message$ANSI_RESET")
    }

    internal inline fun printdbg(message: Any?) {
        if (DEBUG) println(message)
    }

    val prnt = if (DEBUG) ::printdbg else {}

    inline fun a(message: Any?) = d(message)
    inline fun s(message: Any?) = i(message)
    inline fun r(message: Any?) = e(message)
}