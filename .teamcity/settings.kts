import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.CustomChart
import jetbrains.buildServer.configs.kotlin.CustomChart.*
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildTypeChartsOrder
import jetbrains.buildServer.configs.kotlin.buildTypeCustomChart

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

data class ChartData(val name: String, val min: Double, val max: Double)

project {

    buildType(Kotlin_Benchmarks_Wasm_Main)

    features {
        val benchmarks = listOf(
            ChartData("microBenchmarks.AbstractMethodBenchmark.sortStrings", 0.004158, 0.083292),
            ChartData("microBenchmarks.AbstractMethodBenchmark.sortStringsWithComparator", 0.004808, 0.031633),
            ChartData("microBenchmarks.AllocationBenchmark.allocateObjects", 0.003036, 0.00959),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_BimorphicCallsite", 0.565376, 0.936653),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_MonomorphicCallsite", 0.230023, 0.773971),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_TrimorphicCallsite", 0.883971, 1.358206),
            ChartData("microBenchmarks.CallsBenchmark.finalMethodCall", 0.214969, 0.232936),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_BimorphicCallsite", 0.573501, 0.903024),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_HexamorphicCallsite", 2.3294, 7.144875),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_MonomorphicCallsite", 0.231375, 0.756533),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_TrimorphicCallsite", 0.810217, 1.342327),
            ChartData("microBenchmarks.CallsBenchmark.parameterBoxUnboxFolding", 0.227294, 1.725281),
            ChartData("microBenchmarks.CallsBenchmark.returnBoxUnboxFolding", 0.222907, 2.254436),
            ChartData("microBenchmarks.CastsBenchmark.classCast", 2.025872, 6.578314),
            ChartData("microBenchmarks.CastsBenchmark.interfaceCast", 1.34679, 10.94384),
            ChartData("microBenchmarks.ChainableBenchmark.testChainable", 3.420543, 5.199822),
            ChartData("microBenchmarks.ClassArrayBenchmark.copy", 0.02136, 0.077186),
            ChartData("microBenchmarks.ClassArrayBenchmark.copyManual", 0.055036, 0.057419),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFiltered", 0.127834, 0.205637),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFilteredLocal", 0.12922, 0.206489),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFilteredManual", 0.128687, 0.208198),
            ChartData("microBenchmarks.ClassArrayBenchmark.filter", 0.129422, 0.19447),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndCount", 0.128613, 0.194902),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndMap", 0.160553, 0.227151),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndMapManual", 0.160734, 0.243413),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterManual", 0.128486, 0.195136),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateArray", 0.005462, 0.024617),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateArrayAndFill", 1.22995, 2.20293),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateList", 8.0E-6, 0.001528),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateListAndFill", 1.270758, 2.21767),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateListAndWrite", 0.049725, 0.057555),
            ChartData("microBenchmarks.ClassBaselineBenchmark.consume", 0.518808, 1.7155),
            ChartData("microBenchmarks.ClassBaselineBenchmark.consumeField", 0.032844, 0.038023),
            ChartData("microBenchmarks.ClassListBenchmark.copy", 0.00156, 0.023986),
            ChartData("microBenchmarks.ClassListBenchmark.copyManual", 0.064772, 0.126714),
            ChartData("microBenchmarks.ClassListBenchmark.countFiltered", 0.199481, 0.208011),
            ChartData("microBenchmarks.ClassListBenchmark.countFilteredManual", 0.19173, 0.212404),
            ChartData("microBenchmarks.ClassListBenchmark.countWithLambda", 0.017844, 0.07515),
            ChartData("microBenchmarks.ClassListBenchmark.filter", 0.197451, 0.218171),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndCount", 0.197896, 0.214657),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndCountWithLambda", 0.043588, 0.098693),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMap", 0.211343, 0.239453),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapManual", 0.226581, 0.254252),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapWithLambda", 0.210834, 0.928846),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapWithLambdaAsSequence", 0.161431, 1.047563),
            ChartData("microBenchmarks.ClassListBenchmark.filterManual", 0.194556, 0.212977),
            ChartData("microBenchmarks.ClassListBenchmark.filterWithLambda", 0.044008, 0.098595),
            ChartData("microBenchmarks.ClassListBenchmark.mapWithLambda", 0.197678, 1.997187),
            ChartData("microBenchmarks.ClassListBenchmark.reduce", 0.19578, 0.218289),
            ChartData("microBenchmarks.ClassStreamBenchmark.copy", 0.101518, 0.137289),
            ChartData("microBenchmarks.ClassStreamBenchmark.copyManual", 0.078899, 0.128514),
            ChartData("microBenchmarks.ClassStreamBenchmark.countFiltered", 0.198814, 0.22457),
            ChartData("microBenchmarks.ClassStreamBenchmark.countFilteredManual", 0.197512, 0.224093),
            ChartData("microBenchmarks.ClassStreamBenchmark.filter", 0.216605, 0.299186),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndCount", 0.217073, 0.294657),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndMap", 0.253367, 0.300665),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndMapManual", 0.21175, 0.240141),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterManual", 0.202399, 0.216027),
            ChartData("microBenchmarks.ClassStreamBenchmark.reduce", 0.197109, 0.219049),
            ChartData("microBenchmarks.CompanionObjectBenchmark.invokeRegularFunction", 4.0E-6, 6.0E-6),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testEightOfEight", 4.0E-6, 5.0E-6),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testFourOfFour", 4.0E-6, 5.0E-6),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfEight", 5.0E-6, 6.0E-6),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfFour", 5.0E-6, 5.0E-6),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfTwo", 4.0E-6, 5.0E-6),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testTwoOfTwo", 4.0E-6, 5.0E-6),
            ChartData("microBenchmarks.ElvisBenchmark.testCompositeElvis", 0.054961, 0.084355),
            ChartData("microBenchmarks.ElvisBenchmark.testElvis", 0.018423, 0.018954),
            ChartData("microBenchmarks.EulerBenchmark.problem1", 0.007295, 0.010111),
            ChartData("microBenchmarks.EulerBenchmark.problem14", 0.540237, 0.649295),
            ChartData("microBenchmarks.EulerBenchmark.problem14full", 1.717929, 2.856435),
            ChartData("microBenchmarks.EulerBenchmark.problem1bySequence", 0.016745, 0.036231),
            ChartData("microBenchmarks.EulerBenchmark.problem2", 1.74E-4, 3.25E-4),
            ChartData("microBenchmarks.EulerBenchmark.problem8", 0.027135, 0.605962),
            ChartData("microBenchmarks.EulerBenchmark.problem9", 0.522783, 100.5592),
            ChartData("microBenchmarks.FibonacciBenchmark.calc", 0.002205, 0.055703),
            ChartData("microBenchmarks.FibonacciBenchmark.calcClassic", 0.002218, 0.055576),
            ChartData("microBenchmarks.FibonacciBenchmark.calcWithProgression", 0.002216, 0.055623),
            ChartData("microBenchmarks.ForLoopsBenchmark.arrayIndicesLoop", 0.008572, 0.064925),
            ChartData("microBenchmarks.ForLoopsBenchmark.arrayLoop", 0.005349, 0.056777),
            ChartData("microBenchmarks.ForLoopsBenchmark.charArrayIndicesLoop", 0.00606, 0.062405),
            ChartData("microBenchmarks.ForLoopsBenchmark.charArrayLoop", 0.003466, 0.054377),
            ChartData("microBenchmarks.ForLoopsBenchmark.floatArrayIndicesLoop", 0.004143, 0.006389),
            ChartData("microBenchmarks.ForLoopsBenchmark.floatArrayLoop", 0.004037, 0.004201),
            ChartData("microBenchmarks.ForLoopsBenchmark.intArrayIndicesLoop", 0.005933, 0.060647),
            ChartData("microBenchmarks.ForLoopsBenchmark.intArrayLoop", 0.003488, 0.057505),
            ChartData("microBenchmarks.ForLoopsBenchmark.stringIndicesLoop", 0.020737, 0.257982),
            ChartData("microBenchmarks.ForLoopsBenchmark.stringLoop", 0.015295, 0.264816),
            ChartData("microBenchmarks.ForLoopsBenchmark.uIntArrayIndicesLoop", 0.006526, 0.122758),
            ChartData("microBenchmarks.ForLoopsBenchmark.uIntArrayLoop", 0.027891, 0.108692),
            ChartData("microBenchmarks.ForLoopsBenchmark.uLongArrayIndicesLoop", 0.00637, 0.069327),
            ChartData("microBenchmarks.ForLoopsBenchmark.uLongArrayLoop", 0.026072, 0.062344),
            ChartData("microBenchmarks.ForLoopsBenchmark.uShortArrayIndicesLoop", 0.006473, 0.121833),
            ChartData("microBenchmarks.ForLoopsBenchmark.uShortArrayLoop", 0.027925, 0.133756),
            ChartData("microBenchmarks.InheritanceBenchmark.baseCalls", 0.224597, 2.635),
            ChartData("microBenchmarks.InlineBenchmark.calculate", 0.002186, 0.002381),
            ChartData("microBenchmarks.InlineBenchmark.calculateGeneric", 0.003129, 0.042794),
            ChartData("microBenchmarks.InlineBenchmark.calculateGenericInline", 0.01922, 0.020626),
            ChartData("microBenchmarks.InlineBenchmark.calculateInline", 0.002206, 0.00238),
            ChartData("microBenchmarks.IntArrayBenchmark.copy", 0.058647, 0.264354),
            ChartData("microBenchmarks.IntArrayBenchmark.copyManual", 0.059078, 0.264346),
            ChartData("microBenchmarks.IntArrayBenchmark.countFiltered", 0.121585, 0.501591),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredLocal", 0.12072, 0.50013),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredManual", 0.12097, 0.510702),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredPrime", 0.075053, 0.090303),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredPrimeManual", 0.07553, 0.089295),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSome", 0.009743, 0.016151),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSomeLocal", 0.009944, 0.016168),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSomeManual", 0.009819, 0.016114),
            ChartData("microBenchmarks.IntArrayBenchmark.filter", 0.119769, 0.507171),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndCount", 0.119783, 0.553018),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndMap", 0.122548, 0.530307),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndMapManual", 0.116833, 0.519063),
            ChartData("microBenchmarks.IntArrayBenchmark.filterManual", 0.119128, 0.511854),
            ChartData("microBenchmarks.IntArrayBenchmark.filterPrime", 0.085296, 0.097803),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSome", 0.026828, 0.038322),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSomeAndCount", 0.026011, 0.03201),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSomeManual", 0.027131, 0.037976),
            ChartData("microBenchmarks.IntArrayBenchmark.reduce", 0.123325, 0.513717),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateArray", 0.001248, 0.00776),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateArrayAndFill", 0.004859, 0.011482),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateList", 7.0E-6, 0.001664),
            ChartData("microBenchmarks.IntBaselineBenchmark.consume", 0.008536, 0.010956),
            ChartData("microBenchmarks.IntListBenchmark.copy", 0.00147, 0.028311),
            ChartData("microBenchmarks.IntListBenchmark.copyManual", 0.097433, 0.284862),
            ChartData("microBenchmarks.IntListBenchmark.countFiltered", 0.164258, 0.508767),
            ChartData("microBenchmarks.IntListBenchmark.countFilteredLocal", 0.166051, 0.511459),
            ChartData("microBenchmarks.IntListBenchmark.countFilteredManual", 0.168584, 0.53306),
            ChartData("microBenchmarks.IntListBenchmark.filter", 0.163194, 0.526931),
            ChartData("microBenchmarks.IntListBenchmark.filterAndCount", 0.164048, 0.552007),
            ChartData("microBenchmarks.IntListBenchmark.filterAndMap", 0.170924, 0.534162),
            ChartData("microBenchmarks.IntListBenchmark.filterAndMapManual", 0.181955, 0.52594),
            ChartData("microBenchmarks.IntListBenchmark.filterManual", 0.168348, 0.536107),
            ChartData("microBenchmarks.IntListBenchmark.reduce", 0.160728, 0.513221),
            ChartData("microBenchmarks.IntStreamBenchmark.copyManual", 0.060397, 0.499538),
            ChartData("microBenchmarks.IntStreamBenchmark.countFiltered", 0.121538, 0.518838),
            ChartData("microBenchmarks.IntStreamBenchmark.countFilteredLocal", 0.122505, 0.513319),
            ChartData("microBenchmarks.IntStreamBenchmark.countFilteredManual", 0.119804, 0.516893),
            ChartData("microBenchmarks.IntStreamBenchmark.filter", 0.163139, 0.629047),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndCount", 0.164787, 0.63207),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndMap", 0.162601, 0.633959),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndMapManual", 0.12232, 0.521732),
            ChartData("microBenchmarks.IntStreamBenchmark.filterManual", 0.121247, 0.520806),
            ChartData("microBenchmarks.IntStreamBenchmark.reduce", 0.121595, 0.515877),
            ChartData("microBenchmarks.LambdaBenchmark.capturingLambda", 0.002271, 0.004103),
            ChartData("microBenchmarks.LambdaBenchmark.capturingLambdaNoInline", 0.004033, 0.044465),
            ChartData("microBenchmarks.LambdaBenchmark.methodReference", 0.002285, 0.006037),
            ChartData("microBenchmarks.LambdaBenchmark.methodReferenceNoInline", 0.00231, 0.05396),
            ChartData("microBenchmarks.LambdaBenchmark.mutatingLambda", 0.003452, 0.008047),
            ChartData("microBenchmarks.LambdaBenchmark.mutatingLambdaNoInline", 0.01006, 0.063529),
            ChartData("microBenchmarks.LambdaBenchmark.noncapturingLambda", 0.002312, 0.006044),
            ChartData("microBenchmarks.LambdaBenchmark.noncapturingLambdaNoInline", 0.002311, 0.054665),
            ChartData("microBenchmarks.LocalObjectsBenchmark.localArray", 4.5E-5, 0.001185),
            ChartData("microBenchmarks.LoopBenchmark.arrayForeachLoop", 0.034402, 0.047414),
            ChartData("microBenchmarks.LoopBenchmark.arrayIndexLoop", 0.039646, 0.045226),
            ChartData("microBenchmarks.LoopBenchmark.arrayListForeachLoop", 0.056908, 0.099275),
            ChartData("microBenchmarks.LoopBenchmark.arrayListLoop", 0.05454, 0.09968),
            ChartData("microBenchmarks.LoopBenchmark.arrayLoop", 0.034992, 0.043954),
            ChartData("microBenchmarks.LoopBenchmark.arrayWhileLoop", 0.039196, 0.045174),
            ChartData("microBenchmarks.LoopBenchmark.rangeLoop", 0.008249, 0.010968),
            ChartData("microBenchmarks.MatrixMapBenchmark.add", 0.724108, 1.111569),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeEightArgsWithNullCheck", 6.0E-6, 8.0E-6),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeEightArgsWithoutNullCheck", 6.0E-6, 8.0E-6),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeOneArgWithNullCheck", 4.0E-6, 5.0E-6),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeOneArgWithoutNullCheck", 4.0E-6, 5.0E-6),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeTwoArgsWithNullCheck", 5.0E-6, 5.0E-6),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeTwoArgsWithoutNullCheck", 5.0E-6, 5.0E-6),
            ChartData("microBenchmarks.PrimeListBenchmark.calcDirect", 0.212103, 0.244514),
            ChartData("microBenchmarks.SingletonBenchmark.access", 0.011242, 0.018743),
            ChartData("microBenchmarks.StringBenchmark.stringBuilderConcat", 0.056002, 0.096165),
            ChartData("microBenchmarks.StringBenchmark.stringBuilderConcatNullable", 0.057837, 0.0994),
            ChartData("microBenchmarks.SwitchBenchmark.testConstSwitch", 0.014072, 0.015624),
            ChartData("microBenchmarks.SwitchBenchmark.testDenseEnumsSwitch", 0.016947, 0.158635),
            ChartData("microBenchmarks.SwitchBenchmark.testDenseIntSwitch", 0.013674, 0.015382),
            ChartData("microBenchmarks.SwitchBenchmark.testEnumsSwitch", 0.02142, 0.249357),
            ChartData("microBenchmarks.SwitchBenchmark.testObjConstSwitch", 0.060868, 0.074404),
            ChartData("microBenchmarks.SwitchBenchmark.testSealedWhenSwitch", 0.030073, 0.070891),
            ChartData("microBenchmarks.SwitchBenchmark.testSparseIntSwitch", 0.015558, 0.020356),
            ChartData("microBenchmarks.SwitchBenchmark.testStringsDifficultSwitch", 0.524093, 1.9206),
            ChartData("microBenchmarks.SwitchBenchmark.testStringsSwitch", 0.088956, 0.349811),
            ChartData("microBenchmarks.SwitchBenchmark.testVarSwitch", 0.050362, 0.246124),
            ChartData("microBenchmarks.WithIndiciesBenchmark.withIndicies", 0.18319, 0.218509),
            ChartData("microBenchmarks.WithIndiciesBenchmark.withIndiciesManual", 0.189201, 0.212434),
            ChartData("microBenchmarks.JsInteropBenchmark.externInteropIn", 0.030971, 0.031139),
            ChartData("microBenchmarks.JsInteropBenchmark.externInteropOut", 0.302434, 0.303771),
            ChartData("microBenchmarks.JsInteropBenchmark.intInteropIn", 0.029385, 0.030942),
            ChartData("microBenchmarks.JsInteropBenchmark.intInteropOut", 0.035285, 0.036841),
            ChartData("microBenchmarks.JsInteropBenchmark.simpleInterop", 0.030183, 0.03045),
            ChartData("microBenchmarks.JsInteropBenchmark.stringInteropIn", 1.188557, 1.219722),
            ChartData("microBenchmarks.JsInteropBenchmark.stringInteropOut", 3.765429, 3.827077),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNotNull", 0.031565, 0.033379),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNotNull2Params", 0.030335, 0.032437),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNull", 0.03154, 0.032467),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropOutNotNull", 0.300136, 0.311626),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropOutNull", 0.09679, 0.101568),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNotNull", 0.100132, 0.104282),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNotNull2Params", 0.201913, 0.209712),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNull", 0.034287, 0.03774),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropOutNotNull", 0.110512, 0.11692),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropOutNull", 0.042219, 0.043719),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNotNull", 1.303474, 1.321778),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNotNull2Params", 2.643642, 2.731356),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNull", 0.033318, 0.036314),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropOutNotNull", 3.756557, 3.808),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropOutNull", 0.03854, 0.040078),
            ChartData("microBenchmarks.ArrayCopyBenchmark.copyInSameArray", 1784.405, 1883.592),
            ChartData("microBenchmarks.BunnymarkBenchmark.testBunnymark", 251.812, 1048.241),
            ChartData("microBenchmarks.CoordinatesSolverBenchmark.solve", 293.495, 411.137),
            ChartData("microBenchmarks.EulerBenchmark.problem4", 133.464, 1018.256),
            ChartData("microBenchmarks.FibonacciBenchmark.calcSquare", 19.709, 3169.778),
            ChartData("microBenchmarks.LinkedListWithAtomicsBenchmark.ensureNext", 65.83, 152.143),
            ChartData("microBenchmarks.PrimeListBenchmark.calcEratosthenes", 277.594, 300.244),
            ChartData("microBenchmarks.StringBenchmark.stringConcat", 0.022, 0.05),
            ChartData("microBenchmarks.StringBenchmark.stringConcatNullable", 0.088, 0.116),
            ChartData("microBenchmarks.StringBenchmark.summarizeSplittedCsv", 0.826, 6.452),
            ChartData("microBenchmarks.superslow.GraphSolverBenchmark.solve", 36.386, 136.856),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.cd", 140.076, 175.153),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.havlak", 790.726, 1060.437),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.json", 0.491, 1.329),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.mandelbrot", 60.286, 61.615),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.nBody", 9.771, 12.49),
            ChartData("macroBenchmarks.MacroBenchmarksFast.bounce", 0.008595, 0.013741),
            ChartData("macroBenchmarks.MacroBenchmarksFast.list", 0.008779, 0.024113),
            ChartData("macroBenchmarks.MacroBenchmarksFast.permute", 0.011556, 0.027345),
            ChartData("macroBenchmarks.MacroBenchmarksFast.queens", 0.016625, 0.025346),
            ChartData("macroBenchmarks.MacroBenchmarksFast.sieve", 0.007972, 0.029829),
            ChartData("macroBenchmarks.MacroBenchmarksFast.storage", 0.076505, 0.181701),
            ChartData("macroBenchmarks.MacroBenchmarksFast.towers", 0.029814, 0.049001),
        )

        fun String.toId() = "id_benchmark_$this"

        fun addBenchmarkChart(name: String, min: Double, max: Double) {
            val margins = (max - min)
            buildTypeCustomChart {
                id = name.toId()
                title = name
                seriesTitle = "Serie"
                format = CustomChart.Format.TEXT
                series = listOf(
                    Serie(title = "js", key = SeriesKey("js_$name")),
                    Serie(title = "wasm", key = SeriesKey("wasm_$name")),
                    Serie(title = "wasmOpt", key = SeriesKey("wasmOpt_$name")),
                )
                param("properties.axis.y.min", (min - margins).coerceAtLeast(-margins / 10.0).toString())
                param("properties.axis.y.type", "default")
                param("properties.axis.y.max", (max + margins).toString())
            }
        }

        for (benchmark in benchmarks) {
            addBenchmarkChart(benchmark.name, benchmark.min, benchmark.max)
        }

        addBenchmarkChart("bundleSize", 0.0, 21403263.0)

        buildTypeChartsOrder {
            id = "PROJECT_EXT_2453"
            order = benchmarks.map { it.name.toId() }
        }
    }
}

object Kotlin_Benchmarks_Wasm_Main : BuildType({
    id("Main")
    name = "Main"

    artifactRules = "build/reports/benchmarks => reports"
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        param("kotlin-version", "%dep.Kotlin_KotlinDev_CompilerDistAndMavenArtifacts.build.number%")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            name = "clean"
            tasks = ":clean"
            gradleParams = "-Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "wasmBenchmark"
            tasks = ":wasmFastMicroBenchmark :wasmSlowMicroBenchmark :wasmFastMacroBenchmark :wasmSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "wasmOptBenchmark"
            tasks = ":wasmOptFastMicroBenchmark :wasmOptSlowMicroBenchmark :wasmOptFastMacroBenchmark :wasmOptSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "jsBenchmark"
            tasks = ":jsFastMicroBenchmark :jsSlowMicroBenchmark :jsFastMacroBenchmark :jsSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "reportAllTargetsToTC"
            tasks = ":reportAllTargetsToTC"
            gradleParams = "-Pkotlin_version=%kotlin-version%"
        }
    }

    dependencies {
        artifacts(AbsoluteId("Kotlin_KotlinDev_CompilerDistAndMavenArtifacts")) {
            buildRule = lastSuccessful()
            artifactRules = "maven.zip!**=>kotlin-compiler"
        }
    }

    requirements {
        equals("teamcity.agent.name", "kotlin-linux-x64-wasm-js-perf-munit788")
    }

    cleanup {
        keepRule {
            id = "Keep Artifacts for the 5 years"
            dataToKeep = artifacts()
            keepAtLeast = days(5 * 365)
            applyToBuilds {
                inBranches {
                    branchFilter = patterns(
                        "+:<default>"
                    )
                }
            }
        }
    }
})
