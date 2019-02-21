# Requirements

  + [Yosys](http://www.clifford.at/yosys/)

# Clone

    git clone $repo $dir

# Build

    cd $dir/cello2
    mvn clean install

# Test DNACompiler application

    cd $dir/cello2/target
    ./run.py -e DNACompiler -j "-Xms2G -Xmx5G" -a " \
    -targetDataFile ../../sample-input/DNACompiler/and/Eco1C1G1T1-synbiohub.UCF.json \
    -inputNetlist ../../sample-input/DNACompiler/and/and.v \
    -options ../../sample-input/DNACompiler/and/options.csv \
    -outputDir /path/to/output/dir
