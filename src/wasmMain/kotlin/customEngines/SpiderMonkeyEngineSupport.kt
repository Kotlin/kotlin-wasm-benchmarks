@file:OptIn(ExperimentalWasmJsInterop::class)

package customEngines

private fun spiderMonkeyArguments(): String =
    js("globalThis.scriptArgs.join(' ')")

internal object SpiderMonkeyEngineSupport : StandaloneJsVmSupport() {
    override fun arguments(): Array<out String> =
        spiderMonkeyArguments().split(' ').toTypedArray()

    override fun isSupported(): Boolean = isSpiderMonkeyEngine()
}

private fun isSpiderMonkeyEngine(): Boolean =
    js("typeof(globalThis.inIon) !== 'undefined' || typeof(globalThis.isIon) !== 'undefined'")