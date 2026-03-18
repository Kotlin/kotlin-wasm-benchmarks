// Seed "some" entry point as a requirement of Wasm Edge
@OptIn(ExperimentalWasmInterop::class)
@WasmExport
fun entryPointStub(): Unit = Unit

// Seems to be a bug in kx.benchmarks plugin which do not compile entryPointStub
@Suppress("DEPRECATION")
@ExperimentalStdlibApi
@EagerInitialization
private val stubReference: Unit = run {
    entryPointStub()
}