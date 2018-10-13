# Clone

- Clone

      git clone $repo $dir; cd $dir

# Eclipse

## Import

- File > Open Projects from File System...
- Directory...
- `$dir` (where you cloned the repository)

## Build

- Navigate to cello2 project, right-click, Run As > Maven install

# Command line

## Build

    cd $dir
    mvn clean install

## Test DNACompiler application

    cd $dir/cello2/target
	./run.py -e DNACompiler -j "-Xms2G -Xmx5G" -a " \
	-targetDataFile ../../sample-input/DNACompiler/and/Eco1C1G1T1-synbiohub.UCF.json \
    -inputNetlist ../../sample-input/DNACompiler/and/and.v \
    -options ../../sample-input/DNACompiler/and/options.csv \
    -outputDir /path/to/output/dir
	
