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
- Window > Preferences > Java > Build Path > User Libraries
- Import > Browse... > `$dir/Cello/Cello.userlibraries`
- Ok > Apply & Close

## Build

- Navigate to Cello project
- Navigate to build.xml, right-click, Run As > Ant Build
- Right-click on Cello project, Refresh

## Run/Debug configuration

- Run > Run Configurations...
- Java Application (right click) > New
- Main tab
    - Project: Cello
    - Main class: `DNACompiler.runtime.Main`
- Arguments tab
    - Program arguments:

          -inputNetlist "${project_loc}/sample-input/DNACompiler/adder/adder.v"
          -targetDataFile "${project_loc}/sample-input/DNACompiler/adder/Eco1C1G1T1.UCF.json"
          -options "${project_loc}/sample-input/DNACompiler/adder/options.csv"
          -netlistConstraintFile "${project_loc}/sample-input/DNACompiler/adder/adder_netlistconstraints.json"
          -outputDir "${project_loc}/sample-output/adder"
- Environment tab
    - New...
    - Name: `LD_LIBRARY_PATH`
    - Value:

          ${project_loc}/src/resources-partitioning/algorithms/GPCC_SCIP/linux64:${project_loc}/src/resources-partitioning/algorithms/GPCC_SUGARM_BASE/linux64

- Apply
- May need to invoke the Ant build script once, then right click on the project, Refresh

# Command line

## Build

    cd $dir
    ant

## Test DNACompiler application

    cd $dir/exec
    ./run.py -e DNACompiler -j "-Xms2G -Xmx5G" -a " \
    -inputNetlist ../sample-input/DNACompiler/rule30/rule30.v \
    -targetDataFile ../sample-input/DNACompiler/rule30/Eco1C1G1T1-synbiohub.UCF.json \
    -options ../sample-input/DNACompiler/rule30/options.csv \
    -outputDir /path/to/outputdir"

## Test export stage

    cd $dir/exec
    ./run.py -e export -j "-Xms2G -Xmx5G" -a " \
    -inputNetlist ../sample-input/export/placement.json \
    -targetDataFile ../sample-input/export/Eco1C1G1T0-synbiohub.UCF.json \
    -options ../sample-input/export/options.csv \
    -outputDir /path/to/outputdir \
    -algoName SBOL"
