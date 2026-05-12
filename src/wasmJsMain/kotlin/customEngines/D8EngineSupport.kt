@file:OptIn(ExperimentalWasmJsInterop::class)

package customEngines

private fun d8Arguments(): String =
    js("globalThis.arguments.join(' ')")

internal object D8EngineSupport : StandaloneJsVmSupport() {
    override fun arguments(): Array<out String> =
        d8Arguments().split(' ').toTypedArray()

    override fun isSupported(): Boolean = isD8Engine()
}

private fun isD8Engine(): Boolean =
    js("typeof d8 !== 'undefined'")

