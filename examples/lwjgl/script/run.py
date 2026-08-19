#! /usr/bin/env python3
import argparse, glob, os, platform, subprocess, sys
sys.path.append(os.path.normpath(os.path.dirname(__file__) + '/../../../script'))
import common, build, build_utils

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--jwm-version', default=None)
  parser.add_argument('--lwjgl-version', default='3.4.1')
  args = parser.parse_args()

  if not args.jwm_version:
    build.main()

  os.chdir(common.basedir + '/examples/lwjgl')

  # Javac
  if (build_utils.arch == 'x64'):
    lwjgl_classifier = 'natives-' + build_utils.system
  else:
    lwjgl_classifier = 'natives-' + build_utils.system + '-' + build_utils.arch

  classpath = common.deps_compile() + [
    build_utils.fetch_maven('org.lwjgl', 'lwjgl', args.lwjgl_version),
    build_utils.fetch_maven('org.lwjgl', 'lwjgl-opengl', args.lwjgl_version),
    build_utils.fetch_maven('org.lwjgl', 'lwjgl', args.lwjgl_version, classifier=lwjgl_classifier),
    build_utils.fetch_maven('org.lwjgl', 'lwjgl-opengl', args.lwjgl_version, classifier=lwjgl_classifier)
  ]

  if args.jwm_version:
    classpath += [
      build_utils.fetch_maven('io.github.humbleui', 'jwm', args.jwm_version)
    ]
  else:
    classpath += [
      '../../target/classes',
      build_utils.system + '/build'
    ]

  sources = build_utils.files(f'java/**/*.java')
  build_utils.javac(sources, f'target/classes', classpath = classpath, release='22')
  
  # run
  subprocess.check_call([
    'java',
    '--class-path', build_utils.classpath_join(classpath + [f'target/classes']),
    '-Djava.awt.headless=true',
    '-enableassertions',
    '-enablesystemassertions',
    '-Dfile.encoding=UTF-8',
    *(['--enable-native-access=ALL-UNNAMED'] if build_utils.jdk_version()[0] >= 24 else []),
    '-Xcheck:jni',
    'io.github.humbleui.jwm.examples.Example'
  ])

  return 0

if __name__ == '__main__':
  sys.exit(main())
