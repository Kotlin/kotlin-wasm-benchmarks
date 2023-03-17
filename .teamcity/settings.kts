import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.CustomChart
import jetbrains.buildServer.configs.kotlin.CustomChart.*
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.buildSteps.sshExec
import jetbrains.buildServer.configs.kotlin.buildTypeChartsOrder
import jetbrains.buildServer.configs.kotlin.buildTypeCustomChart
import jetbrains.buildServer.configs.kotlin.triggers.ScheduleTrigger
import jetbrains.buildServer.configs.kotlin.triggers.schedule

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
            ChartData("microBenchmarks.AbstractMethodBenchmark.sortStrings", 0.004296, 0.093322),
            ChartData("microBenchmarks.AbstractMethodBenchmark.sortStringsWithComparator", 0.004601, 0.051093),
            ChartData("microBenchmarks.AllocationBenchmark.allocateObjects", 0.003045, 0.105222),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_BimorphicCallsite", 0.516935, 4.249114),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_MonomorphicCallsite", 0.222925, 1.82325),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_TrimorphicCallsite", 0.860814, 5.804693),
            ChartData("microBenchmarks.CallsBenchmark.finalMethodCall", 0.218762, 0.723033),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_BimorphicCallsite", 0.521681, 4.184401),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_HexamorphicCallsite", 2.4502, 7.741301),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_MonomorphicCallsite", 0.2228, 1.825578),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_TrimorphicCallsite", 0.804868, 5.169638),
            ChartData("microBenchmarks.CallsBenchmark.parameterBoxUnboxFolding", 0.223546, 10.175508),
            ChartData("microBenchmarks.CallsBenchmark.returnBoxUnboxFolding", 0.223404, 10.398075),
            ChartData("microBenchmarks.CastsBenchmark.classCast", 0.618504, 8.476322),
            ChartData("microBenchmarks.CastsBenchmark.interfaceCast", 0.819089, 19.767113),
            ChartData("microBenchmarks.ChainableBenchmark.testChainable", 2.529316, 8.396412),
            ChartData("microBenchmarks.ClassArrayBenchmark.copy", 0.01938, 0.110684),
            ChartData("microBenchmarks.ClassArrayBenchmark.copyManual", 0.03514, 0.27191),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFiltered", 0.1371, 1.611573),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFilteredLocal", 0.135087, 1.639276),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFilteredManual", 0.136639, 1.618545),
            ChartData("microBenchmarks.ClassArrayBenchmark.filter", 0.137679, 1.618862),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndCount", 0.138192, 1.679387),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndMap", 0.180347, 1.747847),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndMapManual", 0.18309, 1.770491),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterManual", 0.142471, 1.68473),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateArray", 0.001472, 0.022985),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateArrayAndFill", 1.138767, 6.246597),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateList", 9.0E-6, 0.022284),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateListAndFill", 1.215085, 7.410483),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateListAndWrite", 0.031594, 0.260952),
            ChartData("microBenchmarks.ClassBaselineBenchmark.consume", 0.604184, 8.197946),
            ChartData("microBenchmarks.ClassBaselineBenchmark.consumeField", 0.032434, 0.066963),
            ChartData("microBenchmarks.ClassListBenchmark.copy", 0.003543, 0.075892),
            ChartData("microBenchmarks.ClassListBenchmark.copyManual", 0.046038, 0.343039),
            ChartData("microBenchmarks.ClassListBenchmark.countFiltered", 0.20427, 2.252577),
            ChartData("microBenchmarks.ClassListBenchmark.countFilteredManual", 0.200807, 2.21118),
            ChartData("microBenchmarks.ClassListBenchmark.countWithLambda", 0.021837, 0.163211),
            ChartData("microBenchmarks.ClassListBenchmark.filter", 0.204686, 2.092672),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndCount", 0.203807, 2.176354),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndCountWithLambda", 0.029844, 0.298603),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMap", 0.242848, 2.026739),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapManual", 0.241822, 2.177247),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapWithLambda", 0.216516, 3.780123),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapWithLambdaAsSequence", 0.182636, 4.936653),
            ChartData("microBenchmarks.ClassListBenchmark.filterManual", 0.207051, 2.314347),
            ChartData("microBenchmarks.ClassListBenchmark.filterWithLambda", 0.031125, 0.303742),
            ChartData("microBenchmarks.ClassListBenchmark.mapWithLambda", 0.204304, 8.326984),
            ChartData("microBenchmarks.ClassListBenchmark.reduce", 0.20828, 2.384716),
            ChartData("microBenchmarks.ClassStreamBenchmark.copy", 0.080571, 0.541559),
            ChartData("microBenchmarks.ClassStreamBenchmark.copyManual", 0.065851, 0.55749),
            ChartData("microBenchmarks.ClassStreamBenchmark.countFiltered", 0.201265, 1.54876),
            ChartData("microBenchmarks.ClassStreamBenchmark.countFilteredManual", 0.201744, 1.546578),
            ChartData("microBenchmarks.ClassStreamBenchmark.filter", 0.231814, 2.157831),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndCount", 0.236804, 2.60725),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndMap", 0.28203, 2.581105),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndMapManual", 0.237692, 2.367455),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterManual", 0.203767, 1.583718),
            ChartData("microBenchmarks.ClassStreamBenchmark.reduce", 0.21078, 1.554846),
            ChartData("microBenchmarks.CompanionObjectBenchmark.invokeRegularFunction", 5.0E-6, 1.9E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testEightOfEight", 4.0E-6, 1.4E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testFourOfFour", 4.0E-6, 1.4E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfEight", 4.0E-6, 2.9E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfFour", 4.0E-6, 2.6E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfTwo", 4.0E-6, 1.8E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testTwoOfTwo", 4.0E-6, 1.4E-5),
            ChartData("microBenchmarks.ElvisBenchmark.testCompositeElvis", 0.073013, 0.638222),
            ChartData("microBenchmarks.ElvisBenchmark.testElvis", 0.015648, 0.081857),
            ChartData("microBenchmarks.EulerBenchmark.problem1", 0.007542, 0.0131),
            ChartData("microBenchmarks.EulerBenchmark.problem14", 0.696686, 1.974038),
            ChartData("microBenchmarks.EulerBenchmark.problem14full", 1.766333, 7.191757),
            ChartData("microBenchmarks.EulerBenchmark.problem1bySequence", 0.017061, 0.157541),
            ChartData("microBenchmarks.EulerBenchmark.problem2", 1.89E-4, 0.001442),
            ChartData("microBenchmarks.EulerBenchmark.problem8", 0.018482, 1.73747),
            ChartData("microBenchmarks.EulerBenchmark.problem9", 0.531933, 278.166477),
            ChartData("microBenchmarks.FibonacciBenchmark.calc", 0.00216, 0.145647),
            ChartData("microBenchmarks.FibonacciBenchmark.calcClassic", 0.001981, 0.142797),
            ChartData("microBenchmarks.FibonacciBenchmark.calcWithProgression", 0.002231, 0.143389),
            ChartData("microBenchmarks.ForLoopsBenchmark.arrayIndicesLoop", 0.006034, 0.269059),
            ChartData("microBenchmarks.ForLoopsBenchmark.arrayLoop", 0.00574, 0.271962),
            ChartData("microBenchmarks.ForLoopsBenchmark.charArrayIndicesLoop", 0.003638, 0.27184),
            ChartData("microBenchmarks.ForLoopsBenchmark.charArrayLoop", 0.003427, 0.25864),
            ChartData("microBenchmarks.ForLoopsBenchmark.floatArrayIndicesLoop", 0.004093, 0.018124),
            ChartData("microBenchmarks.ForLoopsBenchmark.floatArrayLoop", 0.004109, 0.016256),
            ChartData("microBenchmarks.ForLoopsBenchmark.intArrayIndicesLoop", 0.003582, 0.267478),
            ChartData("microBenchmarks.ForLoopsBenchmark.intArrayLoop", 0.003453, 0.270903),
            ChartData("microBenchmarks.ForLoopsBenchmark.stringIndicesLoop", 0.016325, 1.169965),
            ChartData("microBenchmarks.ForLoopsBenchmark.stringLoop", 0.01483, 1.110931),
            ChartData("microBenchmarks.ForLoopsBenchmark.uIntArrayIndicesLoop", 0.003995, 0.492422),
            ChartData("microBenchmarks.ForLoopsBenchmark.uIntArrayLoop", 0.00382, 0.509864),
            ChartData("microBenchmarks.ForLoopsBenchmark.uLongArrayIndicesLoop", 0.004022, 0.226767),
            ChartData("microBenchmarks.ForLoopsBenchmark.uLongArrayLoop", 0.003646, 0.24659),
            ChartData("microBenchmarks.ForLoopsBenchmark.uShortArrayIndicesLoop", 0.003921, 0.484415),
            ChartData("microBenchmarks.ForLoopsBenchmark.uShortArrayLoop", 0.004156, 0.515403),
            ChartData("microBenchmarks.InheritanceBenchmark.baseCalls", 0.231484, 10.773668),
            ChartData("microBenchmarks.InlineBenchmark.calculate", 0.001979, 0.009509),
            ChartData("microBenchmarks.InlineBenchmark.calculateGeneric", 0.002048, 0.074989),
            ChartData("microBenchmarks.InlineBenchmark.calculateGenericInline", 0.002019, 0.101031),
            ChartData("microBenchmarks.InlineBenchmark.calculateInline", 0.00198, 0.008288),
            ChartData("microBenchmarks.IntArrayBenchmark.copy", 0.03553, 0.465919),
            ChartData("microBenchmarks.IntArrayBenchmark.copyManual", 0.035361, 0.404037),
            ChartData("microBenchmarks.IntArrayBenchmark.countFiltered", 0.132576, 2.607377),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredLocal", 0.13066, 2.742446),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredManual", 0.12981, 2.658626),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredPrime", 0.077706, 0.15115),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredPrimeManual", 0.077497, 0.150689),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSome", 0.010081, 0.026548),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSomeLocal", 0.010144, 0.026907),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSomeManual", 0.010153, 0.026428),
            ChartData("microBenchmarks.IntArrayBenchmark.filter", 0.134231, 3.099216),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndCount", 0.136903, 2.534036),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndMap", 0.139359, 2.525728),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndMapManual", 0.139935, 2.51572),
            ChartData("microBenchmarks.IntArrayBenchmark.filterManual", 0.13929, 3.071303),
            ChartData("microBenchmarks.IntArrayBenchmark.filterPrime", 0.087707, 0.180071),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSome", 0.020876, 0.15451),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSomeAndCount", 0.01856, 0.181441),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSomeManual", 0.020901, 0.18705),
            ChartData("microBenchmarks.IntArrayBenchmark.reduce", 0.135818, 3.233851),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateArray", 0.001223, 0.011495),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateArrayAndFill", 0.003903, 0.020948),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateList", 9.0E-6, 0.022588),
            ChartData("microBenchmarks.IntBaselineBenchmark.consume", 0.006589, 0.033566),
            ChartData("microBenchmarks.IntListBenchmark.copy", 0.003382, 0.076826),
            ChartData("microBenchmarks.IntListBenchmark.copyManual", 0.115399, 0.453833),
            ChartData("microBenchmarks.IntListBenchmark.countFiltered", 0.197878, 3.355449),
            ChartData("microBenchmarks.IntListBenchmark.countFilteredLocal", 0.200835, 3.409357),
            ChartData("microBenchmarks.IntListBenchmark.countFilteredManual", 0.193956, 3.431383),
            ChartData("microBenchmarks.IntListBenchmark.filter", 0.198856, 3.392056),
            ChartData("microBenchmarks.IntListBenchmark.filterAndCount", 0.197541, 3.286897),
            ChartData("microBenchmarks.IntListBenchmark.filterAndMap", 0.201157, 2.534774),
            ChartData("microBenchmarks.IntListBenchmark.filterAndMapManual", 0.197174, 2.573629),
            ChartData("microBenchmarks.IntListBenchmark.filterManual", 0.197665, 2.593142),
            ChartData("microBenchmarks.IntListBenchmark.reduce", 0.20055, 2.551741),
            ChartData("microBenchmarks.IntStreamBenchmark.copyManual", 0.06794, 1.057276),
            ChartData("microBenchmarks.IntStreamBenchmark.countFiltered", 0.146021, 3.396157),
            ChartData("microBenchmarks.IntStreamBenchmark.countFilteredLocal", 0.144308, 3.212663),
            ChartData("microBenchmarks.IntStreamBenchmark.countFilteredManual", 0.145633, 3.233648),
            ChartData("microBenchmarks.IntStreamBenchmark.filter", 0.194359, 2.816037),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndCount", 0.202421, 2.846143),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndMap", 0.198268, 2.846359),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndMapManual", 0.147856, 2.905209),
            ChartData("microBenchmarks.IntStreamBenchmark.filterManual", 0.137216, 2.88073),
            ChartData("microBenchmarks.IntStreamBenchmark.reduce", 0.148225, 3.580071),
            ChartData("microBenchmarks.LambdaBenchmark.capturingLambda", 0.002269, 0.004106),
            ChartData("microBenchmarks.LambdaBenchmark.capturingLambdaNoInline", 0.002482, 0.288118),
            ChartData("microBenchmarks.LambdaBenchmark.methodReference", 0.002269, 0.007008),
            ChartData("microBenchmarks.LambdaBenchmark.methodReferenceNoInline", 0.048937, 0.27296),
            ChartData("microBenchmarks.LambdaBenchmark.mutatingLambda", 0.003071, 0.009677),
            ChartData("microBenchmarks.LambdaBenchmark.mutatingLambdaNoInline", 0.055343, 0.286434),
            ChartData("microBenchmarks.LambdaBenchmark.noncapturingLambda", 0.002285, 0.006158),
            ChartData("microBenchmarks.LambdaBenchmark.noncapturingLambdaNoInline", 0.006158, 0.262496),
            ChartData("microBenchmarks.LocalObjectsBenchmark.localArray", 4.4E-5, 5.08E-4),
            ChartData("microBenchmarks.LoopBenchmark.arrayForeachLoop", 0.010965, 0.058614),
            ChartData("microBenchmarks.LoopBenchmark.arrayIndexLoop", 0.037902, 0.061216),
            ChartData("microBenchmarks.LoopBenchmark.arrayListForeachLoop", 0.041206, 0.194167),
            ChartData("microBenchmarks.LoopBenchmark.arrayListLoop", 0.039517, 0.221894),
            ChartData("microBenchmarks.LoopBenchmark.arrayLoop", 0.034532, 0.067191),
            ChartData("microBenchmarks.LoopBenchmark.arrayWhileLoop", 0.037118, 0.062276),
            ChartData("microBenchmarks.LoopBenchmark.rangeLoop", 0.00641, 0.033282),
            ChartData("microBenchmarks.MatrixMapBenchmark.add", 0.73937, 2.574497),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeEightArgsWithNullCheck", 5.0E-6, 3.3E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeEightArgsWithoutNullCheck", 5.0E-6, 3.3E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeOneArgWithNullCheck", 4.0E-6, 1.5E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeOneArgWithoutNullCheck", 4.0E-6, 1.5E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeTwoArgsWithNullCheck", 4.0E-6, 1.7E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeTwoArgsWithoutNullCheck", 4.0E-6, 1.7E-5),
            ChartData("microBenchmarks.PrimeListBenchmark.calcDirect", 0.169078, 0.602257),
            ChartData("microBenchmarks.SingletonBenchmark.access", 0.008433, 0.039789),
            ChartData("microBenchmarks.StringBenchmark.stringBuilderConcat", 0.056059, 0.173243),
            ChartData("microBenchmarks.StringBenchmark.stringBuilderConcatNullable", 0.056527, 0.177189),
            ChartData("microBenchmarks.SwitchBenchmark.testConstSwitch", 0.014023, 0.073338),
            ChartData("microBenchmarks.SwitchBenchmark.testDenseEnumsSwitch", 0.015042, 0.529299),
            ChartData("microBenchmarks.SwitchBenchmark.testDenseIntSwitch", 0.012155, 0.074601),
            ChartData("microBenchmarks.SwitchBenchmark.testEnumsSwitch", 0.023154, 0.835461),
            ChartData("microBenchmarks.SwitchBenchmark.testObjConstSwitch", 0.026573, 0.299899),
            ChartData("microBenchmarks.SwitchBenchmark.testSealedWhenSwitch", 0.02757, 0.137494),
            ChartData("microBenchmarks.SwitchBenchmark.testSparseIntSwitch", 0.015731, 0.076939),
            ChartData("microBenchmarks.SwitchBenchmark.testStringsDifficultSwitch", 0.547553, 2.144848),
            ChartData("microBenchmarks.SwitchBenchmark.testStringsSwitch", 0.123474, 0.364305),
            ChartData("microBenchmarks.SwitchBenchmark.testVarSwitch", 0.042184, 0.429333),
            ChartData("microBenchmarks.WithIndiciesBenchmark.withIndicies", 0.176796, 1.84859),
            ChartData("microBenchmarks.WithIndiciesBenchmark.withIndiciesManual", 0.176171, 2.230921),
            ChartData("microBenchmarks.JsInteropBenchmark.externInteropIn", 0.024173, 0.027004),
            ChartData("microBenchmarks.JsInteropBenchmark.externInteropOut", 0.223163, 0.5765),
            ChartData("microBenchmarks.JsInteropBenchmark.intInteropIn", 0.020426, 0.027476),
            ChartData("microBenchmarks.JsInteropBenchmark.intInteropOut", 0.02717, 0.060068),
            ChartData("microBenchmarks.JsInteropBenchmark.simpleInterop", 0.020911, 0.025738),
            ChartData("microBenchmarks.JsInteropBenchmark.stringInteropIn", 1.031434, 3.734923),
            ChartData("microBenchmarks.JsInteropBenchmark.stringInteropOut", 3.740371, 6.148096),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNotNull", 0.023815, 0.026907),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNotNull2Params", 0.026328, 0.034296),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNull", 0.022056, 0.026746),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropOutNotNull", 0.224732, 0.522615),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropOutNull", 0.081835, 0.132942),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNotNull", 0.08689, 0.273614),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNotNull2Params", 0.173904, 0.541266),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNull", 0.028475, 0.032965),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropOutNotNull", 0.102058, 0.524797),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropOutNull", 0.037273, 0.070018),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNotNull", 1.101302, 3.952321),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNotNull2Params", 2.307973, 7.729818),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNull", 0.026822, 0.030977),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropOutNotNull", 3.785138, 5.953479),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropOutNull", 0.033726, 0.089432),
            ChartData("microBenchmarks.ArrayCopyBenchmark.copyInSameArray", 1249.635, 14420.531968),
            ChartData("microBenchmarks.BunnymarkBenchmark.testBunnymark", 220.556, 2777.935872),
            ChartData("microBenchmarks.CoordinatesSolverBenchmark.solve", 257.991, 1105.374976),
            ChartData("microBenchmarks.EulerBenchmark.problem4", 128.247, 2200.87168),
            ChartData("microBenchmarks.FibonacciBenchmark.calcSquare", 20.088, 10199.017984),
            ChartData("microBenchmarks.LinkedListWithAtomicsBenchmark.ensureNext", 57.828, 721.58976),
            ChartData("microBenchmarks.PrimeListBenchmark.calcEratosthenes", 188.057, 1154.817024),
            ChartData("microBenchmarks.StringBenchmark.stringConcat", 0.02176, 0.129024),
            ChartData("microBenchmarks.StringBenchmark.stringConcatNullable", 0.055, 0.408064),
            ChartData("microBenchmarks.StringBenchmark.summarizeSplittedCsv", 0.834761, 24.391168),
            ChartData("microBenchmarks.superslow.GraphSolverBenchmark.solve", 29.462, 231.367936),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.cd", 107.537, 388.54784),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.havlak", 535.615, 3171.720192),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.json", 0.818285, 2.958848),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.mandelbrot", 58.770176, 69.478912),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.nBody", 9.472, 21.59488),
            ChartData("macroBenchmarks.MacroBenchmarksFast.bounce", 0.007755, 0.038521),
            ChartData("macroBenchmarks.MacroBenchmarksFast.list", 0.009167, 0.058716),
            ChartData("macroBenchmarks.MacroBenchmarksFast.permute", 0.018269, 0.064893),
            ChartData("macroBenchmarks.MacroBenchmarksFast.queens", 0.02338, 0.050275),
            ChartData("macroBenchmarks.MacroBenchmarksFast.sieve", 0.011086, 0.131027),
            ChartData("macroBenchmarks.MacroBenchmarksFast.storage", 0.076329, 0.472184),
            ChartData("macroBenchmarks.MacroBenchmarksFast.towers", 0.040125, 0.121282),
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
                    Serie(title = "js_v8", key = SeriesKey("js_$name")),
                    Serie(title = "js_sm", key = SeriesKey("jsShell_js_$name")),
                    Serie(title = "wasm_v8", key = SeriesKey("wasm_$name")),
                    Serie(title = "wasm_sm", key = SeriesKey("jsShell_wasm_$name")),
                    Serie(title = "wasmOpt_v8", key = SeriesKey("wasmOpt_$name")),
                    Serie(title = "wasmOpt_sm", key = SeriesKey("jsShell_wasmOpt_$name")),
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
    publishArtifacts = PublishMode.NORMALLY_FINISHED

    params {
        param("kotlin-version", "%dep.Kotlin_KotlinDev_CompilerDistAndMavenArtifacts.build.number%")
    }

    triggers {
        schedule {
            schedulingPolicy = daily {
                hour = 0
            }
            branchFilter = "+:<default>"
            triggerBuild = onWatchedBuildChange {
                buildType = "Kotlin_KotlinDev_CompilerDistAndMavenArtifacts"
                watchedBuildRule = ScheduleTrigger.WatchedBuildRule.LAST_SUCCESSFUL
                promoteWatchedBuild = false
            }
            withPendingChangesOnly = false
            param("cronExpression_hour", "1")
        }
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            scriptContent = "find ./kotlin-compiler -type f"
        }

        gradle {
            name = "clean"
            tasks = ":clean"
            gradleParams = "-Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "wasmBenchmark_v8"
            tasks = ":wasmFastMicroBenchmark :wasmSlowMicroBenchmark :wasmFastMacroBenchmark :wasmSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
        }
        gradle {
            name = "wasmBenchmark_sm"
            tasks = ":jsShell_wasmFastMicroBenchmark :jsShell_wasmSlowMicroBenchmark :jsShell_wasmFastMacroBenchmark :jsShell_wasmSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "wasmOptBenchmark_v8"
            tasks = ":wasmOptFastMicroBenchmark :wasmOptSlowMicroBenchmark :wasmOptFastMacroBenchmark :wasmOptSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
        }
        gradle {
            name = "wasmOptBenchmark_sm"
            tasks = ":jsShell_wasmOptFastMicroBenchmark :jsShell_wasmOptSlowMicroBenchmark :jsShell_wasmOptFastMacroBenchmark :jsShell_wasmOptSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "jsBenchmark_v8"
            tasks = ":jsFastMicroBenchmark :jsSlowMicroBenchmark :jsFastMacroBenchmark :jsSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
        }
        gradle {
            name = "jsBenchmark_sm"
            tasks = ":jsShell_jsFastMicroBenchmark :jsShell_jsSlowMicroBenchmark :jsShell_jsFastMacroBenchmark :jsShell_jsSlowMacroBenchmark"
            gradleParams = "--rerun-tasks -Pkotlin_version=%kotlin-version%"
        }
        gradle {
            name = "reportAllTargetsToTC"
            tasks = ":reportAllTargetsToTC"
            gradleParams = "-Pkotlin_version=%kotlin-version%"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
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
