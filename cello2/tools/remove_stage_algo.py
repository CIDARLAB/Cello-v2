#!/usr/bin/env python

"""add_stage_algo.py
"""

from __future__ import print_function
import os
import sys
import argparse
import csv
import json
import shutil

#####################################
#	isEmpty
#####################################
def isEmpty(value):
	rtn = not value
	return rtn

#####################################
#	getSourcePath
#####################################
def getSourcePath(Directory, StageName):
	return os.path.join(Directory,"src",StageName)

#####################################
#	getResourcePath
#####################################
def getResourcePath(Directory, StageName):
	return os.path.join(Directory,"src","resources-"+StageName)

#####################################
#	getSampleInputPath
#####################################
def getSampleInputPath(Directory, Name):
	return os.path.join(Directory,"sample-input",Name)

#####################################
#	getResultsPath
#####################################
def getResultsPath(Directory, Name):
	return os.path.join(Directory,"src","results","results",Name)

#####################################
#	getDataPath
#####################################
def getDataPath(Directory, Name):
	return os.path.join(Directory,"src","data","data",Name)

#####################################
#	getConstraintPath
#####################################
def getConstraintPath(Directory, Name):
	return os.path.join(Directory,"src","constraint","constraint",Name)

#####################################
#	getAlgorithmPath
#####################################
def getAlgorithmPath(Directory, StageName):
	return os.path.join(getResourcePath(Directory, StageName),"algorithms")

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
	# ProjectDirectory
	@property
	def ProjectDirectory(self):
		return self.__ProjectDirectory

	# ProjectDirectory
	@ProjectDirectory.setter
	def ProjectDirectory(self, ProjectDirectory):
		if (ProjectDirectory != None):
			self.__ProjectDirectory = ProjectDirectory

	# isValid
	def isValid(self):
		rtn = True
		if (self.ProjectDirectory == ""):
			rtn = False
		if (not os.path.isdir(self.ProjectDirectory)):
			rtn = False
		return rtn

#####################################
#	Algo Class
#####################################
class Algo(object):

	# constructor
	def __init__(self):
		self.Name = ""

	# Name
	@property
	def Name(self):
		return self.__Name

	# Name
	@Name.setter
	def Name(self, Name):
		if (Name != None):
			self.__Name = Name

#####################################
#	Stage Class
#####################################
class Stage(object):

	# constructor
	def __init__(self):
		self.Name = ""

	# Name
	@property
	def Name(self):
		return self.__Name

	# Name
	@Name.setter
	def Name(self, Name):
		if (Name != None):
			self.__Name = Name

#####################################
#	Application Class
#####################################
class Application(object):

	# constructor
	def __init__(self):
		self.__Names = []

	# TODO: make private
	# Names
	@property
	def Names(self):
		return self.__Names

	# Names
	#@Names.setter
	#def Names(self, Names):
	#	if (Names != None):
	#		self.__Names = Names

	# Names
	def addName(self, Name):
		if (Name != None):
			self.Names.append(Name)

	def getNameAtIdx(self, index):
		rtn = None
		if ((0 <= index) and (index < self.getNumNames())):
			rtn = self.Names[index]
		return rtn

	def getNumNames(self):
		return len(self.Names)

#####################################
#	Remove path to classpath
#####################################
def removeFromClasspath(env, stage):
	print("-------------------------------")
	print("		Updating classpath")
	print("-------------------------------")
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	classpath = os.path.join(ProjectDirectory,".classpath")
	if isfile(classpath):
		print("Classpath file found!")
		# parse classpath
		fileInfo = open(classpath, "r")
		fileTemp = []
		found = False
		# read
		for line in fileInfo:
			# not found class path
			if (not ((line.strip().find("kind=\"src\" path=\"src/" + StageName + "\"/>"))) != -1):
				fileTemp.append(line)
			else:
				print("Removing classpath for Stage: " + StageName + "!")
		fileInfo.close
		# write
		fileInfo = open(classpath, "w")
		for line in fileTemp:
			fileInfo.write(line)
		fileInfo.close
	else:
		print("Classpath file not found!")
	print("-------------------------------")

#####################################
#	removeStageDirectories
#####################################
def removeStageDirectories(env, stage):
	print("Removing stage")
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	# remove source directory
	final_path = getSourcePath(ProjectDirectory,StageName)
	rmdir(final_path)
	# remove resource directories
	final_path = os.path.join(ProjectDirectory,"src","resources-"+StageName)
	rmdir(final_path)
	# remove sample-input directories
	final_path = getSampleInputPath(ProjectDirectory,StageName)
	rmdir(final_path)
	# remove results directories
	final_path = getResultsPath(ProjectDirectory,StageName)
	rmdir(final_path)
	# create data directories
	final_path = getDataPath(ProjectDirectory,StageName)
	rmdir(final_path)
	# create constraint directories
	final_path = getConstraintPath(ProjectDirectory,StageName)
	rmdir(final_path)

#####################################
# 		Stage
#####################################
def doStage(env, stage, algo):
	print("-------------------------------")
	print("            Stage")
	print("-------------------------------")
	#####################################
	# 		stageExists
	#####################################
	stageExists = False
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	src_path = getSourcePath(ProjectDirectory, StageName)
	if isdir(src_path):
		stageExists = True
		print ("Removing " + StageName + ".")
	else:
		print (StageName + " not found.")
	if stageExists == True:
		#####################################
		# 		classpath
		#####################################
		removeFromClasspath(env, stage)
		#####################################
		#	RemoveStageDirectories
		#####################################
		removeStageDirectories(env, stage)
	return stageExists

#####################################
# 		removeAlgo
#####################################
def removeAlgo(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	print("-------------------------------")
	print("		Removing algo")
	print("-------------------------------")
	# remove algo directory
	algo_path = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm",AlgorithmName)
	rmdir(algo_path)

#####################################
# 	Update Algorithm Factory File
#####################################
def updateAlgorithmFactoryFile(env, stage, algo, FilePath):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	for root, dirs, files in os.walk(FilePath):
		if (root == FilePath): # top level directory
			for filename in files:
				if filename.find("Factory.java") > -1:
					AFFilePath = os.path.join(FilePath, filename)
					print("Updating Algorithm Factory File: " + AFFilePath)
					AFFile = open(AFFilePath, "r")
					newFile = []
					skip = 0
					for line in AFFile:
						if line.find("import "+StageName+".algorithm."+AlgorithmName+".") > -1:
							print("Removing import in Algorithm Factory.")
							skip = 1
						if line.find("if (name.equals(\""+AlgorithmName+"\")){\n") > -1:
							skip = 3
						if skip == 0:
							newFile.append(line);
						else:
							skip = skip - 1
					AFFile.close()
					AFFile = open(AFFilePath, "w")
					for line in newFile:
						AFFile.write(line)
					AFFile.close()

#####################################
# 	Update Algorithm Factory
#####################################
def updateAlgorithmFactory(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	print("Updating Algorithm Factory")
	# new file path
	AFFilePath = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm")
	updateAlgorithmFactoryFile(env, stage, algo, AFFilePath)
	AFFilePath = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm","data")
	updateAlgorithmFactoryFile(env, stage, algo, AFFilePath)

#####################################
# 	Remove Algorithm Resource
#####################################
def removeResourceAlgo(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	# new file path
	JSONFilePath = getAlgorithmPath(ProjectDirectory,StageName)
	JSONFilePath = os.path.join(JSONFilePath,AlgorithmName)
	rmdir(JSONFilePath)

#####################################
# 		Algorithm
#####################################
def doAlgo(env, stage, algo):
	print("-------------------------------")
	print("            Algorithm")
	print("-------------------------------")
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	#####################################
	# 		algoExists
	#####################################
	algoExists = False
	algo_path = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm",AlgorithmName)
	if os.path.isdir(algo_path):
		algoExists = True
		print ("Removing Algorithm " + AlgorithmName + ".")
	else:
		print (AlgorithmName + " not found.")
	## remove algo
	if algoExists == True:
		removeAlgo(env, stage, algo)
		updateAlgorithmFactory(env, stage, algo)
		removeResourceAlgo(env, stage, algo)
	return algoExists

#####################################
# 		addStageToJSON
#####################################
def removeStageFromJSON(env, stage, algo, appName):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	JSONFilePath = getResourcePath(ProjectDirectory, appName)
	JSONFilePath = os.path.join(JSONFilePath, "Configuration.json")
	AlgoName = algo.Name
	if (isfile(JSONFilePath)):
		print("Removing Stage From JSON")
		# load json
		data = json.load(open(JSONFilePath))
		if "stages" in data:
			# add new stage
			stages = data["stages"]
			new_stages = []
			for stage in stages:
				if (isEmpty(AlgoName) and (stage["name"] != StageName)):
					new_stages.append(stage)
				elif ((not isEmpty(AlgoName)) and ((stage["name"] != StageName) or ((stage["name"] == StageName) and (stage["algorithm_name"] != AlgorithmName)))):
					new_stages.append(stage)
			data["stages"] = new_stages
			# write file
			with open(JSONFilePath, 'w') as outfile:
				json.dump(data, outfile, indent=4)

#####################################
# 	removeImportFromApplication
#####################################
def removeImportFromApplication(env, stage, algo, appName):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	JAVAFilePath = getSourcePath(ProjectDirectory, appName)
	JAVAFilePath = os.path.join(JAVAFilePath, appName, "runtime", "Main.java")
	if (isfile(JAVAFilePath)) and (isEmpty(AlgorithmName)):
		print("Removing Import from JAVA")
		# parse Main.java (Application)
		fileInfo = open(JAVAFilePath, "r")
		fileTemp = []
		startSearch = False
		# read
		for line in fileInfo:
			found = False
			# stop search after class declaration
			if (line.strip().find("public") > -1) and (line.strip().find("class") > -1) and (line.strip().find("Main") > -1):
				startSearch = False
			if (startSearch) and (line.strip().find("import") > -1) and (line.strip().find(StageName + ".runtime.") > -1):
				found = True
				print("Found Import In JAVA")
				print("Removing...")
			# start search after package header
			# append import
			if (line.strip().find("package") > -1) and (line.strip().find(appName + ".runtime") > -1):
				startSearch = True
			if (not found):
				fileTemp.append(line)
		fileInfo.close
		# write
		fileInfo = open(JAVAFilePath, "w")
		for line in fileTemp:
			fileInfo.write(line)
		fileInfo.close
	print("-------------------------------")

#####################################
# 	printApplicationInfo
#####################################
def printApplicationInfo(env, stage, algo, app, templateFilePath):
	StageName = stage.Name
	AlgorithmName = algo.Name
	# new file path
	templateFile = open(templateFilePath, "r")
	countLines = 0
	for line in templateFile:
		newLine = ""
		if (line.find("##NONCE##21##APPLIST##21##NONCE") > -1):
			numAppNames = app.getNumNames()
			for idx in range(0,numAppNames):
				name = app.getNameAtIdx(idx)
				newLine = newLine + line.replace("##NONCE##21##APPLIST##21##NONCE", name)
				
		else:	
			newLine = line
			newLine = line.replace("##NONCE##21##STAGENAME##21##NONCE", StageName)
			newLine = newLine.replace("##NONCE##21##ALGONAME##21##NONCE",AlgorithmName)
		print(newLine, end='')
		countLines = countLines + 1
	if countLines > 0:
		print()
	templateFile.close()

#####################################
# 		Application
#####################################
def doApplication(env, stage, algo, app):
	TemplateDirectory = env.TemplateDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	numNames = app.getNumNames()
	for idx in range(0,numNames):
		name = app.getNameAtIdx(idx)
		print("-------------------------------")
		print("            Application")
		print("-------------------------------")
		print("Attempting Application: " + name)
		# removeStageFromJSON
		removeStageFromJSON(env, stage, algo, name)
		# removeImportFromApplication
		removeImportFromApplication(env, stage, algo, name)
	# remove info
	AlgorithmName = algo.Name
	if (isEmpty(AlgorithmName)):
		# new file path
		TemplatePath = os.path.join(TemplateDirectory,"application","remove","TEMPLATEJAVA")
		printApplicationInfo(env, stage, algo, app, TemplatePath)

#####################################
# 		Main
#####################################
def main(arguments):
	# parse arguments
	parser = argparse.ArgumentParser(
		description=__doc__,
		formatter_class=argparse.RawDescriptionHelpFormatter)
	parser.add_argument('-p','--projectDir', help="Project Directory", required=True)
	parser.add_argument('-s','--stageName', help="Stage Name", required=True)
	parser.add_argument('-a','--algoName', help="Algorithm Name", required=False)
	parser.add_argument('-e','--appNames', help="Application Name(s)", nargs='*', metavar=('Name'), required=True)
	args = parser.parse_args(arguments)

	#####################################
	# 	Get Command Line Arguments
	#####################################
	# ProjectDirectory
	ProjectDirectory = args.projectDir
	# TemplateDirectory
	TemplateDirectory = os.path.join(ProjectDirectory,"tools","templates")
	# StageName
	StageName = args.stageName
	# AlgoName
	AlgoName = args.algoName
	# Names
	AppNames = args.appNames

	#####################################
	# 		Env
	#####################################
	env = Env()
	env.ProjectDirectory = os.path.join(ProjectDirectory,"")
	env.TemplateDirectory = os.path.join(TemplateDirectory,"")

	#####################################
	# 		stage
	#####################################
	stage = Stage()
	stage.Name = StageName

	#####################################
	# 		algo
	#####################################
	algo = Algo()
	algo.Name = AlgoName

	#####################################
	# 		app
	#####################################
	app = Application()
	for name in AppNames:
		app.addName(name)

	#####################################
	# 		Exist
	#####################################
	algoExist = False

	#####################################
	# 		Remove Stage
	#####################################
	if (isEmpty(AlgoName)):
		doStage(env, stage, algo)
	else:
	#####################################
	# 		Remove Algo
	#####################################
		algoExist=doAlgo(env, stage, algo)

	#####################################
	# 		Application
	#####################################
	if (isEmpty(AlgoName) or ((not isEmpty(AlgoName)) and (algoExist == True))):
		doApplication(env, stage, algo, app)

	

#####################################
# 		Entry
#####################################
if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
