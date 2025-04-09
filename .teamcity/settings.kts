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
            ChartData("microBenchmarks.AbstractMethodBenchmark.sortStrings", 0.0030260312315851506, 0.03312900770186335),
            ChartData("microBenchmarks.AbstractMethodBenchmark.sortStringsWithComparator", 0.0034127482335440684, 0.029031913300492613),
            ChartData("microBenchmarks.AllocationBenchmark.allocateObjects", 0.0023563198624247635, 0.048708049999999996),
            ChartData("microBenchmarks.ArithmeticBenchmark.division", 0.016631654676259, 0.2553815999999999),
            ChartData("microBenchmarks.ArithmeticBenchmark.division_constant", 0.05710252427184465, 0.3592874124293784),
            ChartData("microBenchmarks.ArithmeticBenchmark.reminder", 0.03696533815987934, 0.3049871578947365),
            ChartData("microBenchmarks.ArithmeticBenchmark.reminder_constant", 0.02400295225806451, 0.8160458666666667),
            ChartData("microBenchmarks.BoxingBenchmark.booleanTypeBoxing", 0.0019997030173183354, 0.014477787481804949),
            ChartData("microBenchmarks.BoxingBenchmark.integerTypeBoxing", 0.0020240029867613822, 0.04026368740955137),
            ChartData("microBenchmarks.BoxingBenchmark.integerTypeVarClosure", 0.01260383631713555, 0.6683967072463768),
            ChartData("microBenchmarks.BoxingBenchmark.referenceTypeVarClosure", 0.012576981982524858, 0.8646224842105263),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_BimorphicCallsite", 0.4060599999999999, 4.6664917333333324),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_MonomorphicCallsite", 0.22177181818181818, 1.7908771310344829),
            ChartData("microBenchmarks.CallsBenchmark.classOpenMethodCall_TrimorphicCallsite", 0.829875, 5.56261888),
            ChartData("microBenchmarks.CallsBenchmark.finalMethodCall", 0.2037113924050633, 0.7526588235294117),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_BimorphicCallsite", 1.2379888888888888, 5.0420326399999995),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_HexamorphicCallsite", 2.8618875, 7.789860571428572),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_MonomorphicCallsite", 0.2487110063157895, 4.962615854545454),
            ChartData("microBenchmarks.CallsBenchmark.interfaceMethodCall_TrimorphicCallsite", 0.8723354833333333, 5.713470577777779),
            ChartData("microBenchmarks.CallsBenchmark.parameterBoxUnboxFolding", 0.22491089108910894, 5.17700096),
            ChartData("microBenchmarks.CallsBenchmark.returnBoxUnboxFolding", 0.20132712550607285, 5.03389696),
            ChartData("microBenchmarks.CastsBenchmark.classCast", 0.6920705882352941, 8.4342016),
            ChartData("microBenchmarks.CastsBenchmark.interfaceCast", 4.473450000000001, 11.443712),
            ChartData("microBenchmarks.ChainableBenchmark.testChainable", 3.052541176470588, 19.695428266666664),
            ChartData("microBenchmarks.ClassArrayBenchmark.copy", 0.014175198070961076, 0.1719570101010101),
            ChartData("microBenchmarks.ClassArrayBenchmark.copyManual", 0.05513249032258065, 0.44586262456140346),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFiltered", 0.15563561371428572, 1.3921190956521738),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFilteredLocal", 0.15475527857142857, 1.3845930666666668),
            ChartData("microBenchmarks.ClassArrayBenchmark.countFilteredManual", 0.1563354683908046, 1.40577792),
            ChartData("microBenchmarks.ClassArrayBenchmark.filter", 0.15965207931034484, 1.4048417684210526),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndCount", 0.15832009892473117, 1.3954981647058822),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndMap", 0.14216874424242423, 1.4773504000000002),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterAndMapManual", 0.16667983663663663, 1.4528060235294116),
            ChartData("microBenchmarks.ClassArrayBenchmark.filterManual", 0.1586507120879121, 1.4134549942857144),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateArray", 8.06958669833729E-4, 0.01987262138269739),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateArrayAndFill", 0.6924526315789473, 2.915511717647059),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateList", 1.1675343281115925E-5, 0.025928502052785924),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateListAndFill", 0.7511380281690141, 3.8978592),
            ChartData("microBenchmarks.ClassBaselineBenchmark.allocateListAndWrite", 0.044305500982318266, 0.4118119939849624),
            ChartData("microBenchmarks.ClassBaselineBenchmark.consume", 0.404444262295082, 2.9808600615384617),
            ChartData("microBenchmarks.ClassBaselineBenchmark.consumeField", 0.016806369554875977, 0.07162909681159421),
            ChartData("microBenchmarks.ClassListBenchmark.copy", 0.003162765610337111, 0.237426688),
            ChartData("microBenchmarks.ClassListBenchmark.copyManual", 0.05876494597839136, 0.6696214683544305),
            ChartData("microBenchmarks.ClassListBenchmark.countFiltered", 0.17231914525547443, 1.72147712),
            ChartData("microBenchmarks.ClassListBenchmark.countFilteredManual", 0.2521402963235294, 1.6581197575757578),
            ChartData("microBenchmarks.ClassListBenchmark.countWithLambda", 0.016286803519061584, 0.21382201690140845),
            ChartData("microBenchmarks.ClassListBenchmark.filter", 0.2503058209386281, 1.6841447225806452),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndCount", 0.24897977811320754, 1.7280578064516132),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndCountWithLambda", 0.048502874132804755, 0.543327744),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMap", 0.17889792706766916, 1.803138648275862),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapManual", 0.2653442996282528, 1.7559675586206898),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapWithLambda", 0.1978732340425532, 3.2303473777777776),
            ChartData("microBenchmarks.ClassListBenchmark.filterAndMapWithLambdaAsSequence", 0.1590063393939394, 3.2321293473684207),
            ChartData("microBenchmarks.ClassListBenchmark.filterManual", 0.25310282857142863, 1.7314122322580645),
            ChartData("microBenchmarks.ClassListBenchmark.filterWithLambda", 0.04766294820717131, 0.5594953142857142),
            ChartData("microBenchmarks.ClassListBenchmark.mapWithLambda", 0.2723801548387097, 5.285124654545454),
            ChartData("microBenchmarks.ClassListBenchmark.reduce", 0.25471795144927534, 2.0277148903225806),
            ChartData("microBenchmarks.ClassStreamBenchmark.copy", 0.10920226244343892, 0.8746559015384616),
            ChartData("microBenchmarks.ClassStreamBenchmark.copyManual", 0.0811447481228669, 0.8796349046153846),
            ChartData("microBenchmarks.ClassStreamBenchmark.countFiltered", 0.2519587479553903, 1.6033400470588237),
            ChartData("microBenchmarks.ClassStreamBenchmark.countFilteredManual", 0.25152689347826085, 1.6020660705882352),
            ChartData("microBenchmarks.ClassStreamBenchmark.filter", 0.2075810927272727, 1.9487044266666669),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndCount", 0.20636960698689957, 1.92389632),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndMap", 0.21783705178571428, 1.9234742857142855),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterAndMapManual", 0.2579120671480144, 1.7127535999999999),
            ChartData("microBenchmarks.ClassStreamBenchmark.filterManual", 0.25039898136200717, 1.6722544),
            ChartData("microBenchmarks.ClassStreamBenchmark.reduce", 0.24774954042553193, 1.6106416),
            ChartData("microBenchmarks.CompanionObjectBenchmark.invokeRegularFunction", 3.938830514443314E-6, 2.6240857145698097E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testEightOfEight", 3.8000105662660073E-6, 2.7810742619326313E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testFourOfFour", 3.864913320601253E-6, 2.4115121069437833E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfEight", 4.627349582513845E-6, 2.2197894964544284E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfFour", 4.526382529338689E-6, 2.1486173280020372E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testOneOfTwo", 4.108102246814754E-6, 2.0933608191853384E-5),
            ChartData("microBenchmarks.DefaultArgumentBenchmark.testTwoOfTwo", 3.733231663503422E-6, 2.067817263364665E-5),
            ChartData("microBenchmarks.ElvisBenchmark.testCompositeElvis", 0.05147185877466252, 0.2942961202312139),
            ChartData("microBenchmarks.ElvisBenchmark.testElvis", 0.014741448382126349, 0.11302675041322315),
            ChartData("microBenchmarks.EulerBenchmark.problem1", 0.007414355285428527, 0.013195556571428572),
            ChartData("microBenchmarks.EulerBenchmark.problem14", 0.4633818181818182, 2.9408223999999996),
            ChartData("microBenchmarks.EulerBenchmark.problem14full", 1.0847906976744184, 9.445193142857141),
            ChartData("microBenchmarks.EulerBenchmark.problem1bySequence", 0.013842077955271564, 0.25284096),
            ChartData("microBenchmarks.EulerBenchmark.problem2", 8.587874608700589E-5, 0.001617547372546655),
            ChartData("microBenchmarks.EulerBenchmark.problem8", 0.016766917293233083, 2.0726027130434783),
            ChartData("microBenchmarks.EulerBenchmark.problem9", 0.3791740458015267, 362.46604800000006),
            ChartData("microBenchmarks.FibonacciBenchmark.calc", 0.0021416099284667757, 0.1471847619047619),
            ChartData("microBenchmarks.FibonacciBenchmark.calcClassic", 0.001978154963482802, 0.14658469647058822),
            ChartData("microBenchmarks.FibonacciBenchmark.calcWithProgression", 0.001990776042307219, 0.14613437151335312),
            ChartData("microBenchmarks.ForLoopsBenchmark.arrayIndicesLoop", 0.004257862956156471, 0.23277620512820513),
            ChartData("microBenchmarks.ForLoopsBenchmark.arrayLoop", 0.00470679089424647, 0.2410733898989899),
            ChartData("microBenchmarks.ForLoopsBenchmark.charArrayIndicesLoop", 0.0032375717911045814, 0.23843357486910993),
            ChartData("microBenchmarks.ForLoopsBenchmark.charArrayLoop", 0.003472042595879312, 0.23413376000000002),
            ChartData("microBenchmarks.ForLoopsBenchmark.floatArrayIndicesLoop", 0.003946963962481487, 0.02257036091205212),
            ChartData("microBenchmarks.ForLoopsBenchmark.floatArrayLoop", 0.00408442853490156, 0.01686088205128205),
            ChartData("microBenchmarks.ForLoopsBenchmark.intArrayIndicesLoop", 0.003232133624975144, 0.23358810928961748),
            ChartData("microBenchmarks.ForLoopsBenchmark.intArrayLoop", 0.003424540663723116, 0.2385907135678392),
            ChartData("microBenchmarks.ForLoopsBenchmark.stringIndicesLoop", 0.01779784145887607, 0.7620356196721312),
            ChartData("microBenchmarks.ForLoopsBenchmark.stringLoop", 0.013315743991358356, 0.7190614646153846),
            ChartData("microBenchmarks.ForLoopsBenchmark.uIntArrayIndicesLoop", 0.0023792836113837095, 0.46928012549019604),
            ChartData("microBenchmarks.ForLoopsBenchmark.uIntArrayLoop", 0.0029639955357142855, 0.5100318279569892),
            ChartData("microBenchmarks.ForLoopsBenchmark.uLongArrayIndicesLoop", 0.0024470464135021098, 0.23265926213592233),
            ChartData("microBenchmarks.ForLoopsBenchmark.uLongArrayLoop", 0.0033235093578236974, 0.3087599908571429),
            ChartData("microBenchmarks.ForLoopsBenchmark.uShortArrayIndicesLoop", 0.002945443311708372, 0.47973469090909093),
            ChartData("microBenchmarks.ForLoopsBenchmark.uShortArrayLoop", 0.020949002217294902, 0.5163111489361702),
            ChartData("microBenchmarks.InheritanceBenchmark.baseCalls", 0.20137246963562752, 16.2091264),
            ChartData("microBenchmarks.InlineBenchmark.calculate", 0.0012711968665387347, 0.008526024191279888),
            ChartData("microBenchmarks.InlineBenchmark.calculateGeneric", 0.0012713453334720632, 0.07400210638930163),
            ChartData("microBenchmarks.InlineBenchmark.calculateGenericInline", 0.001269879518072289, 0.0743753696969697),
            ChartData("microBenchmarks.InlineBenchmark.calculateInline", 0.001275638690878818, 0.008504632967032966),
            ChartData("microBenchmarks.IntArrayBenchmark.copy", 0.054571423503325944, 0.5640325333333334),
            ChartData("microBenchmarks.IntArrayBenchmark.copyManual", 0.05390871011235955, 0.5766783999999999),
            ChartData("microBenchmarks.IntArrayBenchmark.countFiltered", 0.15458105139664807, 3.274507377777778),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredLocal", 0.15563680867208673, 3.1171907368421055),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredManual", 0.15613689770992367, 3.2658972444444445),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredPrime", 0.06121992528019926, 0.20096132720306512),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredPrimeManual", 0.06131025, 0.19857795181644358),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSome", 0.010017422759483119, 0.03575635957842529),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSomeLocal", 0.010000138373983739, 0.02734610731707317),
            ChartData("microBenchmarks.IntArrayBenchmark.countFilteredSomeManual", 0.010000047197400488, 0.027351498507462692),
            ChartData("microBenchmarks.IntArrayBenchmark.filter", 0.15384472328767124, 3.2201642666666666),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndCount", 0.15523230051282053, 3.2516579555555554),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndMap", 0.12503166117021278, 3.2660878222222225),
            ChartData("microBenchmarks.IntArrayBenchmark.filterAndMapManual", 0.15331409189189188, 3.3012224000000003),
            ChartData("microBenchmarks.IntArrayBenchmark.filterManual", 0.15470468455284553, 3.202710755555555),
            ChartData("microBenchmarks.IntArrayBenchmark.filterPrime", 0.06663929058663029, 0.20808643445378153),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSome", 0.023628566565349545, 0.2005805070422535),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSomeAndCount", 0.023762830957230145, 0.2038485942857143),
            ChartData("microBenchmarks.IntArrayBenchmark.filterSomeManual", 0.023360640628066733, 0.20801962968197882),
            ChartData("microBenchmarks.IntArrayBenchmark.reduce", 0.1573679625698324, 3.5041247999999996),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateArray", 0.0017531532795510348, 0.00984907331670823),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateArrayAndFill", 0.0044548027615507165, 0.02435989249011858),
            ChartData("microBenchmarks.IntBaselineBenchmark.allocateList", 1.1792560799023916E-5, 0.03843437965635739),
            ChartData("microBenchmarks.IntBaselineBenchmark.consume", 0.004700580107583589, 0.057846016875712655),
            ChartData("microBenchmarks.IntListBenchmark.copy", 0.003136590723612622, 0.2499525097345133),
            ChartData("microBenchmarks.IntListBenchmark.copyManual", 0.07031870060606062, 0.8195995042253521),
            ChartData("microBenchmarks.IntListBenchmark.countFiltered", 0.17067354782608696, 3.623500800000001),
            ChartData("microBenchmarks.IntListBenchmark.countFilteredLocal", 0.2473429164835165, 3.081447905882353),
            ChartData("microBenchmarks.IntListBenchmark.countFilteredManual", 0.24786549275362318, 3.5485093647058834),
            ChartData("microBenchmarks.IntListBenchmark.filter", 0.2505716129032258, 3.485759247058823),
            ChartData("microBenchmarks.IntListBenchmark.filterAndCount", 0.24861198141263943, 3.565278870588235),
            ChartData("microBenchmarks.IntListBenchmark.filterAndMap", 0.17374724943820224, 3.5636495058823536),
            ChartData("microBenchmarks.IntListBenchmark.filterAndMapManual", 0.25077879267399267, 3.5163617882352947),
            ChartData("microBenchmarks.IntListBenchmark.filterManual", 0.24814221376811596, 3.5466527999999995),
            ChartData("microBenchmarks.IntListBenchmark.reduce", 0.2501102523636364, 3.6589343999999997),
            ChartData("microBenchmarks.IntStreamBenchmark.copyManual", 0.07037698972099853, 1.0487141587301587),
            ChartData("microBenchmarks.IntStreamBenchmark.countFiltered", 0.16686053229974157, 3.5025344000000005),
            ChartData("microBenchmarks.IntStreamBenchmark.countFilteredLocal", 0.16465102486772487, 3.113987011764706),
            ChartData("microBenchmarks.IntStreamBenchmark.countFilteredManual", 0.16722959144385027, 3.1944342588235295),
            ChartData("microBenchmarks.IntStreamBenchmark.filter", 0.16739963596491228, 3.6499040000000007),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndCount", 0.16770122803030302, 3.7938400000000003),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndMap", 0.16533053559322033, 3.6937088000000005),
            ChartData("microBenchmarks.IntStreamBenchmark.filterAndMapManual", 0.16562926276595746, 3.5029895529411768),
            ChartData("microBenchmarks.IntStreamBenchmark.filterManual", 0.16684752559366753, 3.5329204705882353),
            ChartData("microBenchmarks.IntStreamBenchmark.reduce", 0.1698082714285714, 3.7131231999999996),
            ChartData("microBenchmarks.LambdaBenchmark.capturingLambda", 0.0021318890021849966, 0.1830531072),
            ChartData("microBenchmarks.LambdaBenchmark.capturingLambdaNoInline", 0.00214504203125, 0.19592385511811022),
            ChartData("microBenchmarks.LambdaBenchmark.methodReference", 0.0021284911323071107, 0.18047422256809337),
            ChartData("microBenchmarks.LambdaBenchmark.methodReferenceNoInline", 0.002150567854183927, 0.20923024905660376),
            ChartData("microBenchmarks.LambdaBenchmark.mutatingLambda", 0.002150813967885471, 0.007682628210146018),
            ChartData("microBenchmarks.LambdaBenchmark.mutatingLambdaNoInline", 0.0021862922544583444, 0.08410973658536586),
            ChartData("microBenchmarks.LambdaBenchmark.noncapturingLambda", 0.00216923164248856, 0.18169665084745762),
            ChartData("microBenchmarks.LambdaBenchmark.noncapturingLambdaNoInline", 0.002131019352001173, 0.21465641290322585),
            ChartData("microBenchmarks.LocalObjectsBenchmark.localArray", 3.781726901274512E-5, 5.090190878225734E-4),
            ChartData("microBenchmarks.LoopBenchmark.arrayForeachLoop", 0.009796766803278689, 0.1155515501133787),
            ChartData("microBenchmarks.LoopBenchmark.arrayIndexLoop", 0.03413522491349481, 0.12710071794871797),
            ChartData("microBenchmarks.LoopBenchmark.arrayListForeachLoop", 0.03983060897435898, 0.30785598060606056),
            ChartData("microBenchmarks.LoopBenchmark.arrayListLoop", 0.039912730184147316, 0.2997057192546584),
            ChartData("microBenchmarks.LoopBenchmark.arrayLoop", 0.03010946180048662, 0.1143650909090909),
            ChartData("microBenchmarks.LoopBenchmark.arrayWhileLoop", 0.03354142372881356, 0.12670185316455695),
            ChartData("microBenchmarks.LoopBenchmark.rangeLoop", 0.004823362831858408, 0.05735582857142857),
            ChartData("microBenchmarks.MatrixMapBenchmark.add", 0.5831860465116281, 2.7034112),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeEightArgsWithNullCheck", 6.108190896978944E-6, 7.18358411973915E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeEightArgsWithoutNullCheck", 6.0615855600985496E-6, 7.134158538573887E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeOneArgWithNullCheck", 4.145075240429945E-6, 5.206592162121889E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeOneArgWithoutNullCheck", 4.109202306864443E-6, 4.981884756555963E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeTwoArgsWithNullCheck", 4.3756630676690445E-6, 5.0743120199484614E-5),
            ChartData("microBenchmarks.ParameterNotNullAssertionBenchmark.invokeTwoArgsWithoutNullCheck", 4.306419602779574E-6, 5.295693430340011E-5),
            ChartData("microBenchmarks.PrimeListBenchmark.calcDirect", 0.12282340425531915, 1.0307907918367347),
            ChartData("microBenchmarks.SingletonBenchmark.access", 0.006486550827746613, 0.06871243174603174),
            ChartData("microBenchmarks.StringBenchmark.stringBuilderConcat", 0.025627267119062307, 0.29187801980198025),
            ChartData("microBenchmarks.StringBenchmark.stringBuilderConcatNullable", 0.025845023948908995, 0.29895391179487174),
            ChartData("microBenchmarks.SwitchBenchmark.testConstSwitch", 0.01152999313658202, 0.15116653051359516),
            ChartData("microBenchmarks.SwitchBenchmark.testDenseEnumsSwitch", 0.014830586135078847, 0.1654225188571429),
            ChartData("microBenchmarks.SwitchBenchmark.testDenseIntSwitch", 0.011617034256217738, 0.1516379834920635),
            ChartData("microBenchmarks.SwitchBenchmark.testEnumsSwitch", 0.0199374539500614, 0.19100207940074906),
            ChartData("microBenchmarks.SwitchBenchmark.testObjConstSwitch", 0.011545843439911798, 0.15825121337047351),
            ChartData("microBenchmarks.SwitchBenchmark.testSealedWhenSwitch", 0.021691152815013402, 0.1361231448275862),
            ChartData("microBenchmarks.SwitchBenchmark.testSparseIntSwitch", 0.014084878979343862, 0.1508127288888889),
            ChartData("microBenchmarks.SwitchBenchmark.testStringsDifficultSwitch", 0.8682522947368423, 16.116258133333336),
            ChartData("microBenchmarks.SwitchBenchmark.testStringsSwitch", 0.08173194444444444, 2.6853753263157896),
            ChartData("microBenchmarks.SwitchBenchmark.testVarSwitch", 0.097378515625, 0.47058548771929826),
            ChartData("microBenchmarks.WithIndiciesBenchmark.withIndicies", 0.25802524206349203, 1.93807872),
            ChartData("microBenchmarks.WithIndiciesBenchmark.withIndiciesManual", 0.25629071544117643, 1.8253343999999998),
            ChartData("microBenchmarks.MultiFunctionInterfaceBenchmark.interfaceFunctionCall", 0.9918842105263158, 371.89918720000003),
            ChartData("microBenchmarks.JsInteropBenchmark.externInteropIn", 0.02555942340093604, 0.030390881823635273),
            ChartData("microBenchmarks.JsInteropBenchmark.externInteropOut", 0.24535106382978725, 0.4198955389830509),
            ChartData("microBenchmarks.JsInteropBenchmark.intInteropIn", 0.02048738947368421, 0.03248891769280622),
            ChartData("microBenchmarks.JsInteropBenchmark.intInteropOut", 0.02804708931383577, 0.09651209499072357),
            ChartData("microBenchmarks.JsInteropBenchmark.simpleInterop", 0.018259305231689088, 0.03024639423076923),
            ChartData("microBenchmarks.JsInteropBenchmark.stringInteropIn", 0.9004407407407407, 2.635086147368421),
            ChartData("microBenchmarks.JsInteropBenchmark.stringInteropOut", 3.084011764705882, 6.2335487999999994),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNotNull", 0.02608398540906722, 0.030091332169866196),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNotNull2Params", 0.028427758420441346, 0.036303820761762515),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropInNull", 0.022502207879924956, 0.03015824835032993),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropOutNotNull", 0.2511038674033149, 0.41390080000000007),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.externInteropOutNull", 0.09765269461077843, 0.1788782962457338),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNotNull", 0.1228474074074074, 0.1525527689552239),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNotNull2Params", 0.21160168067226892, 0.2835690057142857),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropInNull", 0.03041708094327597, 0.03890935236707939),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropOutNotNull", 0.10421505376344084, 0.325816576),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.intInteropOutNull", 0.04266373056994818, 0.1192421308056872),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNotNull", 1.0001428571428572, 2.723492977777778),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNotNull2Params", 2.0080400000000003, 5.41420032),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropInNull", 0.031340012602394454, 0.03683256470588235),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropOutNotNull", 3.1118375, 5.865697280000001),
            ChartData("microBenchmarks.JsInteropNullableBenchmark.stringInteropOutNull", 0.08620265677179964, 0.1645091137254902),
            ChartData("microBenchmarks.ArrayCopyBenchmark.copyInSameArray", 1682.044046, 10054.492928),
            ChartData("microBenchmarks.BunnymarkBenchmark.testBunnymark", 272.956, 2716.898304),
            ChartData("microBenchmarks.CoordinatesSolverBenchmark.solve", 184.234, 1290.608128),
            ChartData("microBenchmarks.EulerBenchmark.problem4", 107.195, 1453.712896),
            ChartData("microBenchmarks.FibonacciBenchmark.calcSquare", 19.727, 11078.83392),
            ChartData("microBenchmarks.LinkedListWithAtomicsBenchmark.ensureNext", 55.841, 680.537088),
            ChartData("microBenchmarks.PrimeListBenchmark.calcEratosthenes", 188.528, 1086.460928),
            ChartData("microBenchmarks.StringBenchmark.stringConcat", 0.024064, 2.29),
            ChartData("microBenchmarks.StringBenchmark.stringConcatNullable", 0.055, 0.34816),
            ChartData("microBenchmarks.StringBenchmark.summarizeSplittedCsv", 0.874716, 16.963072),
            ChartData("microBenchmarks.superslow.GraphSolverBenchmark.solve", 23.183, 257.82784),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.cd", 90.635, 398.208768),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.coroutineIteration", 20.97, 138.573824),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.coroutineRecursion", 218.361, 1897.807872),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.havlak", 479.632, 2656.258048),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.json", 0.372775, 5.001216),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.mandelbrot", 57.562, 71.501824),
            ChartData("macroBenchmarks.MacroBenchmarksSlow.nBody", 9.157, 90.672896),
            ChartData("macroBenchmarks.MacroBenchmarksFast.bounce", 0.007467796610169492, 0.07440216025236594),
            ChartData("macroBenchmarks.MacroBenchmarksFast.list", 0.007799776714513556, 0.06484432717678101),
            ChartData("macroBenchmarks.MacroBenchmarksFast.permute", 0.018304088397790054, 0.13176345989847715),
            ChartData("macroBenchmarks.MacroBenchmarksFast.queens", 0.010217034700315457, 0.10022037378435518),
            ChartData("macroBenchmarks.MacroBenchmarksFast.sieve", 0.007571020282728947, 0.0432363220083682),
            ChartData("macroBenchmarks.MacroBenchmarksFast.storage", 0.04234022770398482, 0.2032727690607735),
            ChartData("macroBenchmarks.MacroBenchmarksFast.towers", 0.027075424192665575, 0.21718118400000003),
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
    publishArtifacts = PublishMode.SUCCESSFUL

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
