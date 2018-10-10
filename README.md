# Clone

- Download & install [git-lfs](https://git-lfs.github.com/)
- Initialize git-lfs

      git lfs install

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

    cd $dir
	LD_LIBRARY_PATH="resources-partitioning/algorithms/GPCC_SUGARM_BASE/linux64" \
	java -cp "./*" org.cellocad.cello2.DNACompiler.runtime.Main \
	-targetDataFile ../../sample-input/DNACompiler/and/Eco1C1G1T1-synbiohub.UCF.json \
	-inputNetlist ../../sample-input/DNACompiler/and/and.v \
	-options ../../sample-input/DNACompiler/and/options.csv \
	-outputDir /path/to/output/dir
