# Requirements

  + Java JDK 8 (Oracle, OpenJDK)
  + Python 3
  + [Maven](https://maven.apache.org/)
  + [Yosys](http://www.clifford.at/yosys/)
    - Linux: check your package manager
    - Mac OSX: `brew install yosys`
    - Windows:
      * download yosys-win32-mxebin-0.8.zip here: <http://www.clifford.at/yosys/download.html>
      * unzip anywhere, add the folder containing yosys.exe to `%Path%`
  + [Graphviz](http://www.graphviz.org/)
    - Linux: check your package manager
    - Mac OSX: `brew install graphviz`
    - Windows:
      * download and install latest msi, e.g. graphviz-2.38.msi
      * add path to dot.exe, e.g. `C:\Program Files (x86)\Graphvix2.38\bin`, to `%Path%`

# Installation

## Manual

Clone the repository.

    git clone https://github.com/CIDARLAB/Cello2.git

Build.

    cd Cello2/cello2
    mvn clean install

Test.

    cd target
    ./run.py -e DNACompiler -j "-Xms2G -Xmx5G" -a " \
    -targetDataFile ../../sample-input/DNACompiler/and/Eco1C1G1T1-synbiohub.UCF.json \
    -inputNetlist ../../sample-input/DNACompiler/and/and.v \
    -options ../../sample-input/DNACompiler/and/options.csv \
    -outputDir /path/to/output/dir
