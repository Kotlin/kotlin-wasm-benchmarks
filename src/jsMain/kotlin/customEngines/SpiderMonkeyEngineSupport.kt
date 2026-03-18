package customEngines

private external fun print(text: String)

internal object SpiderMonkeyEngineSupport : StandaloneJsVmSupport() {
    override fun writeFile(path: String, content: String) {
        //WORKAROUND: In StandaloneJsVMs cannot write into files, this format will be parsed on gradle plugin side
        print("<FILE:$path>$content<ENDFILE>")
    }

    override fun arguments(): Array<out String> {
        val arguments = js("globalThis.scriptArgs.join(' ')") as String
        return arguments.split(' ').toTypedArray()
    }

    override fun isSupported(): Boolean = isSpiderMonkeyEngine()
}

private fun isSpiderMonkeyEngine(): Boolean =
    js("typeof(globalThis.inIon) !== 'undefined' || typeof(globalThis.isIon) !== 'undefined'") as Boolean