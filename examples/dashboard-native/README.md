# Gradle build scripts  for `dashboard` example

This directory contains Gradle build scripts for the `dashboard` example application,
especially to produce a native image of the application using the
[GraalVM](https://www.graalvm.org/latest/reference-manual/native-image/) tool.

The scripts assume you already have the [Gradle](https://gradle.org/releases/) build tool
installed and is available in the path. Standard Gradle commands can then be used to build the
`dashboard` application, such as:

* `gradle run`: compile and run the application
* `gradle jar`: generate the application's `jar` file in `build/libs/` directory
* etc.

By default, the locally-compiled JWM classes are used as dependencies to compile the `dashboard`
application. To use a published JWM version in Maven repository, specify the `-PjwmVersion=...`
option in the Gradle commands, e.g.

* `gradle run -PjwmVersion=0.4.25`: compile and run the application with JWM `0.4.25` version
  published in Maven repository

All of the above build commands would work on all platforms supported by JWM.

Especially the scripts make use of the latest
[Gradle native image](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
plugin to generate the `dashboard` native executable for current platform, as detailed below.

## Building `dashboard` native image

### Prerequisites

A GraalVM JDK 25+ distribution is required to be installed and is available in the system, 
e.g. from [Oracle](https://www.graalvm.org/downloads/) release, or from a
[GraalVM Community](https://github.com/graalvm/graalvm-ce-builds/releases) distribution
or from a [Liberica Native Image Kit](https://bell-sw.com/liberica-native-image-kit/) release,
with the JAVA_HOME environment variable pointing to the installation and its `bin` directory be
added to the path. More detailed installing info for various OSes is
[here](https://www.graalvm.org/latest/getting-started/#installing).

A C compiler for the current platform is also required to produce the native image executable.
Detailed setup info is available [here](https://www.graalvm.org/latest/reference-manual/native-image/#prerequisites).

### Building native image

First, the
[native image metadata](https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/)
for the `dashboard` application need be generated, by running the application with the `-Pagent` option:
```
gradle run -Pagent
```
Use and interact with the running `dashboard` application as per normal, then close it.

Then generate its native image with the command:
```
gradle nativeCompile
```
The resulting native image will be produced in the `build/native/nativeCompile/` directory,
and can be executed directly:
```
./build/native/nativeCompile/dashboard-native
```
or in Windows:
```
build\native\nativeCompile\dashboard-native.exe
```

### Supported platforms

The `dashboard` native images can be produced on platforms supported by GraalVM, namely:

- Linux `x64/arm64`,
- MacOS `x64/arm64` (but the `x64` support is removed in GraalVM 25.0.2+),
- Windows `x64`.
