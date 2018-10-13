#!/usr/bin/env python

"""run.py
"""
import os
import sys
import argparse

#####################################
# 		Main
#####################################
def main(arguments):
	# parse arguments
	parser = argparse.ArgumentParser(
		description=__doc__,
		formatter_class=argparse.RawDescriptionHelpFormatter)
	parser.add_argument('-j','--jvm', help="Java Virtual Machine (VM) arguments", required=False)
	parser.add_argument('-d','--debug', help="Invoke debugger", required=False, action="store_true")
	parser.add_argument('-e','--executable', help="Executable", required=True)
	parser.add_argument('-a','--exec_args', help="Executable Arguments", required=False)
	args = parser.parse_args(arguments)

	#####################################
	# 	Get Command Line Arguments
	#####################################
	# jvm
	jvm = args.jvm
	# debug
	debug = args.debug
	# executable
	executable = args.executable
	# exec_args
	exec_args = args.exec_args

	#####################################
	# 	Get Jar
	#####################################
	classpath = ""
	for root, dirs, files in os.walk("./"):
		for file in files:
			if file.endswith(".jar"):
				classpath = os.path.join(root, file) + ":" + classpath
	classpath += "./"

	#####################################
	# 	Run
	#####################################
	cmd = ""
	if (debug):
		cmd += "jdb "
	else:
		cmd += "java "
	if (jvm != None):
		cmd += jvm
	cmd += " -classpath "
	cmd += classpath
	cmd += " "
	cmd += executable
	cmd += ".runtime.Main "
	if (exec_args != None):
		cmd += exec_args
	print ("Executing: " + cmd)
	os.system(cmd)

#####################################
# 		Entry
#####################################
if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
