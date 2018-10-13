#!/usr/bin/env python

"""instantiate_poros.py
"""

from __future__ import print_function
import os
import sys
import argparse
import shutil
import xml.etree.ElementTree as ET

#####################################
#	isEmpty
#####################################
def isEmpty(value):
	rtn = not value
	return rtn

#####################################
#	makedir
#####################################
def makedir(Directory):
	if not os.path.isdir(Directory):
		os.makedirs (Directory)
		print("Creating " + Directory)

#####################################
#	rmdir
#####################################
def rmdir(Directory):
	if isdir(Directory):
		shutil.rmtree(Directory)
		print("Removing " + Directory)

#####################################
#	isdir
#####################################
def isdir(Directory):
	return os.path.isdir(Directory)

#####################################
#	isfile
#####################################
def isfile(FilePath):
	return os.path.isfile(FilePath)

#####################################
#	Environment Class
#####################################
class Env(object):

	# constructor
	def __init__(self):
		self.ProjectDirectory = ""
		self.PorosDirectory = ""
		self.ProjectName = ""
		self.Source = False

	# ProjectDirectory
	@property
	def ProjectDirectory(self):
		return self.__ProjectDirectory

	# ProjectDirectory
	@ProjectDirectory.setter
	def ProjectDirectory(self, ProjectDirectory):
		if (ProjectDirectory != None):
			self.__ProjectDirectory = ProjectDirectory

	# PorosDirectory
	@property
	def PorosDirectory(self):
		return self.__PorosDirectory

	# PorosDirectory
	@PorosDirectory.setter
	def PorosDirectory(self, PorosDirectory):
		if (PorosDirectory != None):
			self.__PorosDirectory = PorosDirectory

	# Source
	@property
	def Source(self):
		return self.__Source

	# Source
	@Source.setter
	def Source(self, Source):
		if (Source != None):
			self.__Source = Source

	# ProjectName
	@property
	def ProjectName(self):
		return self.__ProjectName

	# ProjectName
	@ProjectName.setter
	def ProjectName(self, ProjectName):
		if (ProjectName != None):
			self.__ProjectName = ProjectName

	# isValid
	def isValid(self):
		rtn = True
		if (self.ProjectDirectory == ""):
			rtn = False
		if (self.PorosDirectory == ""):
			rtn = False
		if (self.ProjectName == ""):
			rtn = False
		if (not os.path.isdir(self.PorosDirectory)):
			rtn = False
		return rtn
	
#####################################
# copy
#####################################
def updateDir(env):
	ProjectName = env.ProjectName
	ProjectDirectory = env.ProjectDirectory
	buildXML = os.path.join(ProjectDirectory,"build.xml")
	projectXML = os.path.join(ProjectDirectory,".project")
	print("Updating directory: " + ProjectDirectory)
	# update file
	print("Updating file: " + buildXML)
	File = open(buildXML, "r")
	newFile = []
	for line in File:
		if line.find("<project basedir=\".\" default=\"build\" name=\"") > -1:
			newFile.append("<project basedir=\".\" default=\"build\" name=\"" + ProjectName + "\">\n")
		elif line.find("windowtitle=\"Poros API\">") > -1:
			newFile.append(line.replace("Poros", ProjectName))
		elif line.find("<doctitle><![CDATA[<h1>Poros</h1>]]>") > -1:
			newFile.append(line.replace("Poros", ProjectName))
		else:
			newFile.append(line);
	File.close()
	File = open(buildXML, "w")
	for line in newFile:
		File.write(line)
	File.close()
	# update file
	print("Updating file: " + projectXML)
	File = open(projectXML, "r")
	newFile = []
	for line in File:
		if line.find("<name>Poros</name>") > -1:
			newFile.append("\t<name>" + ProjectName + "</name>\n")
		else:
			newFile.append(line);
	File.close()
	File = open(projectXML, "w")
	for line in newFile:
		File.write(line)
	File.close()
	
#####################################
# copy
#####################################
def copy(env):
	destinationDir = env.ProjectDirectory
	sourceDir = env.PorosDirectory
	Source = env.Source
	do = False
	# source directory exist
	if (isdir(sourceDir)):
		# source only
		if (Source):
			if (not isdir(destinationDir)):
				makedir(destinationDir)
			destinationDir = os.path.join(destinationDir,"src")
			sourceDir = os.path.join(sourceDir,"src")
			if (isdir(destinationDir)):
				rmdir(destinationDir)
		# copy / copy source
		if (not isdir(destinationDir)):
			shutil.copytree(sourceDir, destinationDir)
			print("Copying from: " + sourceDir + " to directory: " + destinationDir)
			if (not Source):
				updateDir(env)
				printInfo(env)
		else:
			print("Error copying, ensure that the project directory does not exist.")
	
#####################################
# 	printInfo
#####################################
def printInfo(env):
	ProjectName = env.ProjectName
	PorosDirectory = env.PorosDirectory
	TemplatePath = os.path.join(PorosDirectory,"tools","templates","instantiate_poros","TEMPLATE")
	# new file path
	templateFile = open(TemplatePath, "r")
	countLines = 0
	for line in templateFile:
		newLine = ""
		newLine = line.replace("##NONCE##21##PROJECTNAME##21##NONCE", ProjectName)
		print(newLine, end='')
		countLines = countLines + 1
	if countLines > 0:
		print()
	templateFile.close()

#####################################
# 		Main
#####################################
def main(arguments):
	# parse arguments
	parser = argparse.ArgumentParser(
		description=__doc__,
		formatter_class=argparse.RawDescriptionHelpFormatter)
	parser.add_argument('-p','--projectDir', help="Project Directory", required=True)
	parser.add_argument('-f','--porusDir', help="Poros Directory", required=True)
	parser.add_argument('-n','--projectName', help="Project Name", required=True)
	parser.add_argument('-s','--source', help="Source only", required=False, action='store_true')
	args = parser.parse_args(arguments)

	#####################################
	# 	Get Command Line Arguments
	#####################################
	# ProjectName
	ProjectName = args.projectName
	# ProjectDirectory
	ProjectDirectory = args.projectDir
	# TemplateDirectory
	PorosDirectory = args.porusDir
	# Source
	Source = args.source

	#####################################
	# 		Env
	#####################################
	env = Env()
	env.ProjectDirectory = os.path.join(ProjectDirectory,"")
	env.PorosDirectory = os.path.join(PorosDirectory,"")
	env.Source = Source
	env.ProjectName = ProjectName.replace(' ', '')

	#####################################
	# 		copy
	#####################################
	if (env.isValid()):
		copy(env)

#####################################
# 		Entry
#####################################
if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
