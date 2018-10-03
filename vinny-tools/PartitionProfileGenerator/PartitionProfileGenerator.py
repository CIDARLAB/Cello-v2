#!/usr/bin/env python

"""add_remove_application.py
"""

from __future__ import print_function
import os
import sys
import argparse
from mercurial.revset import destination

blockEntryPrefix = "\t\t\t\t{\n\t\t\t\t\t\"name\" : \"block";
blockEntryPostfix = "\",\n\t\t\t\t\t\"capacity\":\n\t\t\t\t\t[\n\t\t\t\t\t\t\"capacity0\"\n\t\t\t\t\t],\n\t\t\t\t\t\"inout_connections_capacity\":\n\t\t\t\t\t[\n\t\t\t\t\t\t\"capacity1\"\n\t\t\t\t\t]\n\t\t\t\t},"
interBlockEntryPrefix = "\t\t\t\t{\n\t\t\t\t\t\"name\" : \"Interblock_"
interBlockEntrySource = "\",\n\t\t\t\t\t\"source\" : \"block"
interBlockEntryDestination = "\",\n\t\t\t\t\t\"destination\" : \"block"
interBlockEntryCapacity = "\",\n\t\t\t\t\t\"capacity\":\n\t\t\t\t\t[\n\t\t\t\t\t\t\"capacity"
interBlockEntryPostfix = "\"\n\t\t\t\t\t]\n\t\t\t\t},"
blockPrefix = "{\n\t\"collection\": \"PartitionProfile\",\n\t\"PartitionProfile\":\n\t{\n\t\t\"name\": \"PProfile0\",\n\t\t\"Blocks\":\n\t\t{\n\t\t\t\"Capacity_Units\":\n\t\t\t[\n\t\t\t\t\"NOT\",\n\t\t\t\t\"NOR\",\n\t\t\t\t\"OR\",\n\t\t\t\t\"Regular\"\n\t\t\t],\n\t\t\t\"Capacity\":\n\t\t\t[\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"capacity0\",\n\t\t\t\t\t\"units\":\n\t\t\t\t\t[\n\t\t\t\t\t\t\"NOT\",\n\t\t\t\t\t\t\"NOR\"\n\t\t\t\n\t\t\t\t\t],\n\t\t\t\t\t\"lower_bound\": 0,\n\t\t\t\t\t\"lower_bound_type\": \"greater_than_or_equal\",\n\t\t\t\t\t\"upper_bound\": 10,\n\t\t\t\t\t\"upper_bound_type\": \"less_than\"\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"capacity1\",\n\t\t\t\t\t\"units\":\n\t\t\t\t\t[\n\t\t\t\t\t\t\"Regular\"\n\t\t\t\t\t],\n\t\t\t\t\t\"lower_bound\": 0,\n\t\t\t\t\t\"lower_bound_type\": \"greater_than_or_equal\",\n\t\t\t\t\t\"upper_bound\": 5,\n\t\t\t\t\t\"upper_bound_type\": \"less_than\"\n\t\t\t\t}\n\t\t\t],\n\t\t\t\"Blocks\":\n\t\t\t["
interBlockPrefix = "\t\t\t]\n\t\t},\n\t\t\"InterBlocks\":\n\t\t{\n\t\t\t\"Capacity_Units\":\n\t\t\t[\n\t\t\t\t\"Regular\"\n\t\t\t],\n\t\t\t\"Capacity\":\n\t\t\t[\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"capacity0\",\n\t\t\t\t\t\"units\":\n\t\t\t\t\t[\n\t\t\t\t\t\t\"Regular\"\n\t\t\t\t\t],\n\t\t\t\t\t\"lower_bound\": 0,\n\t\t\t\t\t\"lower_bound_type\": \"greater_than_or_equal\",\n\t\t\t\t\t\"upper_bound\": 5,\n\t\t\t\t\t\"upper_bound_type\": \"less_than\"\n\t\t\t\t},\n\t\t\t\t{\n\t\t\t\t\t\"name\": \"capacity1\",\n\t\t\t\t\t\"units\":\n\t\t\t\t\t[\n\t\t\t\t\t\t\"Regular\"\n\t\t\t\t\t],\n\t\t\t\t\t\"lower_bound\": 0,\n\t\t\t\t\t\"lower_bound_type\": \"greater_than_or_equal\",\n\t\t\t\t\t\"upper_bound\": 1,\n\t\t\t\t\t\"upper_bound_type\": \"less_than\"\n\t\t\t\t}\n\t\t\t],\n\t\t\t\"InterBlocks\":\n\t\t\t["
interBlockPostfix = "\t\t\t]\n\t\t}\n\t}\n},"

def getNewLine():
    return os.linesep

def getBlockEntryPrefix():
    return "##NONCE##21##BLOCKENTRYPREFIX##21##NONCE"

def getBlockEntryPostfix():
    return "##NONCE##21##BLOCKENTRYPOSTFIX##21##NONCE"

def getInterBlockEntryPrefix():
    return "##NONCE##21##INTERBLOCKENTRYPREFIX##21##NONCE"

def getInterBlockEntryCapacity():
    return "##NONCE##21##INTERBLOCKENTRYCAPACITY##21##NONCE"

def getInterBlockEntryPostfix():
    return "##NONCE##21##INTERBLOCKENTRYPOSTFIX##21##NONCE"

def getInterBlockEntrySource():
    return "##NONCE##21##INTERBLOCKENTRYSOURCE##21##NONCE"

def getInterBlockEntryDestination():
    return "##NONCE##21##INTERBLOCKENTRYDESTINATION##21##NONCE"

def getInterBlockEntrySourceDestinationSeparator():
    return "_"

def getBlockPrefix():
    return "##NONCE##21##BLOCKPREFIX##21##NONCE"

def getBlock():
    return "##NONCE##21##BLOCK##21##NONCE"

def getInterBlockPrefix():
    return "##NONCE##21##INTERBLOCKPREFIX##21##NONCE"

def getInterBlock():
    return "##NONCE##21##INTERBLOCK##21##NONCE"

def getInterBlockPostfix():
    return "##NONCE##21##INTERBLOCKPOSTFIX##21##NONCE"

#####################################
#         Replace
#####################################
def replace(output, old, new):
    return output.replace(old,new)


#####################################
#         pipeline
#####################################
def pipeline(numRows, numColumns):
    #####################################
    #     Block/InterBlock
    #####################################
    Block = "";
    InterBlock = "";
    #####################################
    #         Execute
    #####################################
    if (numRows != 1):
        print("Error occured: number of rows should be 1 for a pipeline");
        sys.exit();
    if numRows > 0 and numColumns > 0:
        for i in range(0, numRows):
            for j in range(0, numColumns):
                if (Block):
                    Block += getNewLine();
                sourceInt = i*numColumns + j;
                source = str(sourceInt);
                Block += getBlockEntryPrefix();
                Block += source;
                Block += getBlockEntryPostfix();
                for k in range(0, numRows):
                    for l in range(0, numColumns):
                        value = 1
                        if (l == (j+1)):
                            value = 0
                        if (InterBlock):
                            InterBlock += getNewLine();
                        destinationInt = k*numColumns + l;
                        if (sourceInt == destinationInt):
                            continue
                        destination = str(destinationInt);
                        InterBlock += getInterBlockEntryPrefix();
                        InterBlock += source;
                        InterBlock += getInterBlockEntrySourceDestinationSeparator();
                        InterBlock += destination;
                        InterBlock += getInterBlockEntrySource();
                        InterBlock += source;
                        InterBlock += getInterBlockEntryDestination();
                        InterBlock += destination;
                        InterBlock += getInterBlockEntryCapacity();
                        InterBlock += str(value);
                        InterBlock += getInterBlockEntryPostfix();
    return Block, InterBlock

#####################################
#         tree
#####################################
def tree(numRows, numColumns):
    #####################################
    #     Block/InterBlock
    #####################################
    Block = "";
    InterBlock = "";
    #####################################
    #         Execute
    #####################################
    if numRows > 0 and numColumns > 0:
        for i in range(0, numRows):
            for j in range(0, numColumns):
                if (Block):
                    Block += getNewLine();
                sourceInt = i*numColumns + j;
                source = str(sourceInt);
                Block += getBlockEntryPrefix();
                Block += source;
                Block += getBlockEntryPostfix();
                for k in range(0, numRows):
                    for l in range(0, numColumns):
                        value = 1
                        if ((j == l) and ((k - i) == 1)):
                            value = 0
                        if ((k == i) and ((l - j) == 1)):
                            value = 0
                        if ((k == (i +1)) and ((l - j) == 1)):
                            value = 0
                        if (InterBlock):
                            InterBlock += getNewLine();
                        destinationInt = k*numColumns + l;
                        if (sourceInt == destinationInt):
                            continue
                        destination = str(destinationInt);
                        InterBlock += getInterBlockEntryPrefix();
                        InterBlock += source;
                        InterBlock += getInterBlockEntrySourceDestinationSeparator();
                        InterBlock += destination;
                        InterBlock += getInterBlockEntrySource();
                        InterBlock += source;
                        InterBlock += getInterBlockEntryDestination();
                        InterBlock += destination;
                        InterBlock += getInterBlockEntryCapacity();
                        InterBlock += str(value);
                        InterBlock += getInterBlockEntryPostfix();
    return Block, InterBlock


#####################################
#         Main
#####################################
def main(arguments):
    # parse arguments
    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument('-r','--numRows', help="Number of Rows", required=True)
    parser.add_argument('-c','--numColumns', help="Number of Columns", required=True)
    parser.add_argument('-t','--topology', help="Topology", required=True)
    args = parser.parse_args(arguments)

    #####################################
    #     Get Command Line Arguments
    #####################################
    # numRows
    numRowsStr = args.numRows
    numRows = int(numRowsStr)
    # get numColumns
    numColumnsStr = args.numColumns
    numColumns = int(numColumnsStr)
    # get topology
    topology = args.topology
    
    #####################################
    #     Template
    #####################################
    Output = "";
    Output += getBlockPrefix();
    Output += getNewLine()
    Output += getBlock();
    Output += getNewLine()
    Output += getInterBlockPrefix();
    Output += getNewLine()
    Output += getInterBlock();
    Output += getNewLine()
    Output += getInterBlockPostfix();
    Output += getNewLine()

    #####################################
    #     Block/InterBlock
    #####################################
    Block = "";
    InterBlock = "";
    
    #####################################
    #         Execute
    #####################################
    if topology == "pipeline":
        Block, InterBlock = pipeline(numRows, numColumns);
    elif topology == "tree":
        Block, InterBlock = tree(numRows, numColumns);
    else:
        print("Error occured");
        sys.exit();
        
    Output = replace(Output, getBlockPrefix(), blockPrefix);
    Output = replace(Output, getBlock(), Block);
    Output = replace(Output, getInterBlockPrefix(), interBlockPrefix);
    Output = replace(Output, getInterBlock(), InterBlock);
    Output = replace(Output, getInterBlockPostfix(), interBlockPostfix);
    
    Output = replace(Output, getBlockEntryPrefix(), blockEntryPrefix);
    Output = replace(Output, getBlockEntryPostfix(), blockEntryPostfix);
    
    Output = replace(Output, getInterBlockEntryPrefix(), interBlockEntryPrefix);
    Output = replace(Output, getInterBlockEntrySource(), interBlockEntrySource);
    Output = replace(Output, getInterBlockEntryDestination(), interBlockEntryDestination);
    Output = replace(Output, getInterBlockEntryCapacity(), interBlockEntryCapacity);
    Output = replace(Output, getInterBlockEntryPostfix(), interBlockEntryPostfix);
    
    print(Output);
    text_file = open("Output.json", "w")
    text_file.write(Output)
    text_file.close()

#####################################
#         Entry
#####################################
if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))