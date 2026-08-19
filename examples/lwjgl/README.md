# JWM example using LWJGL with OpenGL

JWM example working with [LWJGL](https://www.lwjgl.org/) as graphics-drawing library using OpenGL.

This example shows a rotating square at the center of the main window. Its GL code is adapted from
[SwtDemo.java](https://github.com/LWJGL/lwjgl3-demos/blob/main/src/org/lwjgl/demo/opengl/swt/SwtDemo.java)
demo program from the [lwjgl3-demos](https://github.com/LWJGL/lwjgl3-demos) project.

Includes the `script/run.py` script to compile/run this specific example program, which requires
the LWJGL library as dependencies to be downloaded from Maven repository.

By default, the `script/run.py` script uses the locally-compiled JWM classes as dependencies.
To specify a particular JWM version published in Maven repository, use the optional
`--jwm-version` argument, e.g. `./script/run.py --jwm-version 0.4.25`
