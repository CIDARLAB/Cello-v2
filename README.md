[![Build Status](https://travis-ci.org/CIDARLAB/Cello-v2.svg?branch=develop)](https://travis-ci.org/CIDARLAB/Cello-v2)

# Requirements

  + When building from source:
    - Java JDK 8 ([Oracle JDK](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html) or [OpenJDK](https://adoptopenjdk.net/))
    - [Maven](https://maven.apache.org/)
  + When using a JAR file from the releases page:
    - Java JRE 8 ([Oracle JRE](https://www.oracle.com/java/technologies/javase-jre8-downloads.html)) or Java JDK 8 (see above)
  + [Python 3](https://www.python.org/downloads/)
  + [Yosys](http://www.clifford.at/yosys/)
    - Linux: check your package manager
    - Mac OSX: `brew install yosys`
    - Windows:
      * download yosys-win32-mxebin-0.9.zip here: <http://www.clifford.at/yosys/download.html>
      * unzip anywhere, add the folder containing yosys.exe to `%Path%`
  + [Graphviz](http://www.graphviz.org/)
    - Linux: check your package manager
    - Mac OSX: `brew install graphviz`
    - Windows:
      * download and install the [latest executable package](https://graphviz.gitlab.io/_pages/Download/Download_windows.html), e.g. graphviz-2.38.msi
      * add path to dot.exe, e.g. `C:\Program Files (x86)\Graphvix2.38\bin`, to `%Path%`
  + [dnaplotlib](https://github.com/VoigtLab/dnaplotlib)
    - `pip install dnaplotlib`
  + (Optional) [pycello-v2](https://github.com/CIDARLAB/pycello-v2)
    - To support experimental feature: RNAseq profile generation.
    - `pip install git+https://github.com/CIDARLAB/pycello2`

# Installation

## Manual

Clone the repository.

    git clone https://github.com/CIDARLAB/Cello-v2.git

Build.

    cd Cello-v2/cello
    mvn clean package

Test.

    cd target
	java -jar cello-dnacompiler-2.0.0-SNAPSHOT-jar-with-dependencies.jar
    -userConstraintsFile ../../sample-input/ucf/Eco/Eco1C1G1T1.UCF.json \
	-inputSensorFile ../../sample-input/inout/Eco/Eco1C1G1T1.input.json \
	-outputDeviceFile ../../sample-input/inout/Eco/Eco1C1G1T1.output.json \
    -inputNetlist ../../sample-input/DNACompiler/primitives/and.v \
    -options ../../sample-input/DNACompiler/primitives/options.csv \
    -outputDir /path/to/output/dir \
	-pythonEnv /path/to/python
