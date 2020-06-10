package tech.muso.rekoil.core

internal const val DEBUG = false

internal fun printdbg(message: Any?) {
    if (DEBUG) println(message)
}