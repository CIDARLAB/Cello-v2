[![Build Status](https://travis-ci.org/CIDARLAB/Cello-v2.svg?branch=develop)](https://travis-ci.org/CIDARLAB/Cello-v2)

# Introduction

This is the repository for Cello v2, the successor of the Cello genetic circuit design software. This repository hosts the core of Cello v2, a command line tool that implements the circuit design routines. If you are looking for the web application, it is hosted at [CIDARLAB/Cello-v2-webapp][github-webapp].

# Installation

## Options

You have a few options to install Cello:

1. Get the Docker image from Docker hub and run it.
2. Install the runtime dependencies, download a JAR file from the releases page of this repository, run the JAR file.
3. Install the developer dependencies, clone the contents of this repository, and build the webapp from source.

The procedure for each option is described in the sections below.

In the future, Cello will be hosted with a GUI on [cellocad.org](http://cellocad.org). For now, [the original version of Cello](https://github.com/CIDARLAB/cello) remains hosted there. The webapp for Cello-v2 is [in development](https://github.com/CIDARLAB/Cello-v2-webapp).

## (Option 1) Docker

Download and install Docker.

  + Linux: check your package manager / distribution instructions.
  + Mac: [Instructions](https://docs.docker.com/docker-for-mac/install/)
  + Windows: [Instructions](https://docs.docker.com/docker-for-windows/install/) (Windows 10) / [Instructions](https://docs.docker.com/toolbox/toolbox_install_windows/) (Windows 7/8)

Pull the image:

    docker pull cidarlab/cello-dnacompiler:latest

Run the image, replacing fields surrounded by `<>` with files or directories appropriate for your use case:

    docker run --rm -i \
    -v <ABSOLUTE_PATH_TO_LOCAL_INPUT_DIRECTORY>:/root/input \
    -v <ABSOLUTE_PATH_TO_LOCAL_OUTPUT_DIRECTORY>:/root/output \
    -t cidarlab/cello-dnacompiler:latest \
    java -classpath /root/app.jar org.cellocad.v2.DNACompiler.runtime.Main \
    -inputNetlist /root/input/<VERILOG_FILE_IN_INPUT_DIRECTORY> \
    -options /root/input/<OPTIONS_FILE_IN_INPUT_DIRECTORY> \
    -userConstraintsFile /root/input/<UCF_IN_INPUT_DIRECTORY> \
    -inputSensorFile /root/input/<INPUT_SENSOR_FILE_IN_INPUT_DIRECTORY> \
    -outputDeviceFile /root/input/<OUTPUT_DEVICE_FILE_IN_INPUT_DIRECTORY> \
    -pythonEnv python \
    -outputDir /root/output

See the `sample-input` directory in this repository for example Verilog files, UCFs, and option files. An example invocation with all fields completed (omitting the options switch, thus using defaults) might be:

    docker run --rm -i \
    -v /home/foobar/input:/root/input \
    -v /home/foobar/output:/root/output \
    -t cidarlab/cello-dnacompiler:latest \
    java -classpath /root/app.jar org.cellocad.v2.DNACompiler.runtime.Main \
    -inputNetlist /root/input/and.v \
    -userConstraintsFile /root/input/Eco1C1G1T1.UCF.json \
    -inputSensorFile /root/input/Eco1C1G1T1.input.json \
    -outputDeviceFile /root/input/Eco1C1G1T1.output.json \
    -pythonEnv python \
    -outputDir /root/output

After execution, check the output directory for generated files.

<a id="install-option-2"></a>
## (Option 2) Prepackaged JAR file

### Runtime dependencies

  + Java JRE 8 ([Oracle JRE](https://www.oracle.com/java/technologies/javase-jre8-downloads.html)) or Java JDK 8 (see above)
  + [Python 3](https://www.python.org/downloads/)
  + [Yosys](http://www.clifford.at/yosys/)
    - Linux: check your package manager
    - Mac OSX: via [Homebrew][homebrew]: `brew install yosys`
    - Windows:
      * download yosys-win32-mxebin-0.9.zip here: <http://www.clifford.at/yosys/download.html>
      * unzip anywhere, add the folder containing yosys.exe to `%Path%`
  + [Graphviz](http://www.graphviz.org/)
    - Linux: check your package manager
    - Mac OSX: via [Homebrew][homebrew]: `brew install graphviz`
    - Windows:
      * download and install the [latest executable package](https://graphviz.gitlab.io/_pages/Download/Download_windows.html), e.g. graphviz-2.38.msi
      * add path to dot.exe, e.g. `C:\Program Files (x86)\Graphvix2.38\bin`, to `%Path%`
  + [dnaplotlib](https://github.com/VoigtLab/dnaplotlib)
    - `pip install dnaplotlib`
  + (Optional) [pycello-v2](https://github.com/CIDARLAB/pycello-v2)
    - To support experimental feature: RNAseq profile generation.
    - `pip install git+https://github.com/CIDARLAB/pycello-v2`
  + *The latest JAR from the releases page of this repository, or the latest snapshot from [Sonatype](https://oss.sonatype.org/#nexus-search;quick~cello-dnacompiler).*

<a id="install-option-2-execution"></a>
### Execution

Replace fields surrounded by `<>` with files or directories appropriate for your use case:

    java -classpath <JAR_FILE> org.cellocad.v2.DNACompiler.runtime.Main \
    -inputNetlist <PATH_TO_VERILOG_FILE> \
    -options <PATH_TO_OPTIONS_FILE> \
    -userConstraintsFile <PATH_TO_UCF> \
    -inputSensorFile <PATH_TO_INPUT_SENSOR_FILE> \
    -outputDeviceFile <PATH_TO_OUTPUT_DEVICE_FILE> \
    -pythonEnv <ABSOLUTE_PATH_TO_PYTHON_OR_NAME_OF_EXECUTABLE_IN_ENVIRONMENT_PATH> \
    -outputDir <PATH_TO_OUTPUT_DIRECTORY>

See the `sample-input` directory in this repository for example Verilog files, UCFs, and option files. An example invocation with all fields completed (omitting the options switch, thus using defaults) might be:

    java -classpath cello-dnacompiler-2.0.0-SNAPSHOT-jar-with-dependencies.jar org.cellocad.v2.DNACompiler.runtime.Main \
    -inputNetlist and.v \
    -userConstraintsFile Eco1C1G1T1.UCF.json \
    -inputSensorFile Eco1C1G1T1.input.json \
    -outputDeviceFile Eco1C1G1T1.output.json \
    -pythonEnv python \
    -outputDir /home/cello_user/output

## (Option 3) Building from source

### Runtime dependencies

  + Java JDK 8 (Oracle, OpenJDK)
  + *All other dependencies from [(Option 2)](#install-option-2) above.*

1. Clone the repository:

        git clone --recurse-submodules https://github.com/CIDARLAB/Cello-v2.git

2. Build & package:

        cd Cello-v2/cello
        mvn clean package

### Example execution

First go to the `target` directory:

    cd cello-dnacompiler/target

Then proceed as in the [Execution section from (Option 2)](#install-option-2-execution) above.

# Development

## Code style

This project uses the [Maven Checkstyle Plugin][maven-checkstyle-plugin] with a slightly relaxed version of `google_checks.xml` to enforce code style. If the code does not match the style requirements, maven will fail during the `validate` phase. If using Eclipse, you can use the [Eclipse Checkstyle Plugin][eclipse-checkstyle-plugin] to show warnings when code does not meet the style requirements. You can also automatically format written code with the [`google-java-format`][google-java-format], which can be used from the command line, or as a plugin in Eclipse or JetBrains IDEs.

## Deployment

### Sonatype OSS

  + `mvn clean deploy -Pdeploy`
  + See configuration in the parent `pom.xml`.

### Docker Hub

  + Docker images are built and deployed automatically via Travis CI. See `.travis.yml`.
  + Manual:

        mvn docker:build
		mvn docker:push

[github-webapp]: https://github.com/CIDARLAB/Cello-v2-webapp
[homebrew]: https://brew.sh/
[maven-checkstyle-plugin]: https://maven.apache.org/plugins/maven-checkstyle-plugin/index.html
[eclipse-checkstyle-plugin]: https://checkstyle.org/eclipse-cs/#!/
[google-java-format]: https://github.com/google/google-java-format
