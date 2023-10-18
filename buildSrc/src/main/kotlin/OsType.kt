import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem

enum class OsName { WINDOWS, MAC, LINUX, UNKNOWN }
enum class OsArch { X86_32, X86_64, ARM64, UNKNOWN }
data class OsType(val name: OsName, val arch: OsArch)

val Project.currentOsType: OsType get() {
    val gradleOs = OperatingSystem.current()
    val osName = when {
        gradleOs.isMacOsX -> OsName.MAC
        gradleOs.isWindows -> OsName.WINDOWS
        gradleOs.isLinux -> OsName.LINUX
        else -> OsName.UNKNOWN
    }

    val osArch = when (providers.systemProperty("sun.arch.data.model").forUseAtConfigurationTime().get()) {
        "32" -> OsArch.X86_32
        "64" -> when (providers.systemProperty("os.arch").forUseAtConfigurationTime().get().lowercase()) {
            "aarch64" -> OsArch.ARM64
            else -> OsArch.X86_64
        }
        else -> OsArch.UNKNOWN
    }

    return OsType(osName, osArch)
}
