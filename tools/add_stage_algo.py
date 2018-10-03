#!/usr/bin/env python

"""add_stage_algo.py
"""

from __future__ import print_function
import os
import sys
import argparse
import csv
import json

#####################################
#	validateParamType
#####################################
def validateParamType(paramType):
	rtn = False
	paramType = paramType.lower()
	if (paramType == 'boolean'):
		rtn = True
	if (paramType == 'byte'):
		rtn = True
	if (paramType == 'char'):
		rtn = True
	if (paramType == 'short'):
		rtn = True
	if (paramType == 'int'):
		rtn = True
	if (paramType == 'long'):
		rtn = True
	if (paramType == 'float'):
		rtn = True
	if (paramType == 'double'):
		rtn = True
	if (paramType == 'string'):
		rtn = True
	return rtn

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
		self.TemplateDirectory = ""
		self.InputFilename = ""
		self.AuthorName = "Vincent Mirian"

	# ProjectDirectory
	@property
	def ProjectDirectory(self):
		return self.__ProjectDirectory

	# ProjectDirectory
	@ProjectDirectory.setter
	def ProjectDirectory(self, ProjectDirectory):
		if (ProjectDirectory != None):
			self.__ProjectDirectory = ProjectDirectory

	# TemplateDirectory
	@property
	def TemplateDirectory(self):
		return self.__TemplateDirectory

	# TemplateDirectory
	@TemplateDirectory.setter
	def TemplateDirectory(self, TemplateDirectory):
		if (TemplateDirectory != None):
			self.__TemplateDirectory = TemplateDirectory

	# Filename
	@property
	def Filename(self):
		return self.__Filename

	# Filename
	@Filename.setter
	def Filename(self, Filename):
		if (Filename != None):
			self.__Filename = Filename

	# AuthorName
	@property
	def AuthorName(self):
		return self.__AuthorName

	# AuthorName
	@AuthorName.setter
	def AuthorName(self, AuthorName):
		if (AuthorName != None):
			self.__AuthorName = AuthorName

	# isValid
	def isValid(self):
		rtn = True
		if (self.ProjectDirectory == ""):
			rtn = False
		if (self.TemplateDirectory == ""):
			rtn = False
		if (self.Filename == ""):
			rtn = False
		if (not os.path.isdir(self.ProjectDirectory)):
			rtn = False
		if (not os.path.isdir(self.TemplateDirectory)):
			rtn = False
		if (not os.path.isfile(self.Filename)):
			rtn = False
		return rtn

#####################################
#	Algo Class
#####################################
class Algo(object):

	# constructor
	def __init__(self):
		self.Name = ""
		self.Extends = "" 
		self.__Params = []

	# Name
	@property
	def Name(self):
		return self.__Name

	# Name
	@Name.setter
	def Name(self, Name):
		if (Name != None):
			self.__Name = Name

	# Extends
	@property
	def Extends(self):
		return self.__Extends

	# Extends
	@Extends.setter
	def Extends(self, Extends):
		if (Extends != None):
			self.__Extends = Extends

	# TODO: make private
	# Params
	@property
	def Params(self):
		return self.__Params

	# Params
	#@Extends.setter
	#def Params(self, Params):
	#	if (Params != None):
	#		self.__Params = Params

	# Params
	def addParam(self, param):
		if (param != None):
			if (isinstance(param, Param)):
				self.Params.append(param)

	def getParamAtIdx(self, index):
		rtn = None
		if ((0 <= index) and (index < self.getNumParams())):
			rtn = self.Params[index]
		return rtn

	def getNumParams(self):
		return len(self.Params)

	def getNumAddedParams(self):
		rtn = 0
		for idx in range(0, self.getNumParams()):
			param = self.getParamAtIdx(idx)
			if not isEmpty(param.Added):
				rtn = rtn + 1
		return rtn

#####################################
#	Stage Class
#####################################
class Stage(object):

	# constructor
	def __init__(self):
		self.Name = ""
		self.Prefix = ""

	# Name
	@property
	def Name(self):
		return self.__Name

	# Name
	@Name.setter
	def Name(self, Name):
		if (Name != None):
			self.__Name = Name

	# Prefix
	@property
	def Prefix(self):
		return self.__Prefix

	# Prefix
	@Prefix.setter
	def Prefix(self, Prefix):
		if (Prefix != None):
			self.__Prefix = Prefix

#####################################
#	Param Class
#####################################
class Param(object):

	# constructor
	def __init__(self):
		self.Name = ""
		self.Type = ""
		self.Value = ""
		self.Added = ""

	# Name
	@property
	def Name(self):
		return self.__Name

	# Name
	@Name.setter
	def Name(self, Name):
		if (Name != None):
			self.__Name = Name

	# Type
	@property
	def Type(self):
		return self.__Type

	# Type
	@Type.setter
	def Type(self, Type):
		if (Type != None):
			self.__Type = Type

	# Value
	@property
	def Value(self):
		return self.__Value

	# Value
	@Value.setter
	def Value(self, Value):
		if (Value != None):
			self.__Value = Value

	# Added
	@property
	def Added(self):
		return self.__Added

	# Added
	@Added.setter
	def Added(self, Added):
		if (Added != None):
			self.__Added = Added

	#####################################
	#	isParamTypeValid
	#####################################
	def isParamTypeValid(self):
		rtn = False
		paramType = self.Type
		paramType = paramType.lower()
		if (paramType == 'boolean'):
			rtn = True
		if (paramType == 'byte'):
			rtn = True
		if (paramType == 'char'):
			rtn = True
		if (paramType == 'short'):
			rtn = True
		if (paramType == 'int'):
			rtn = True
		if (paramType == 'long'):
			rtn = True
		if (paramType == 'float'):
			rtn = True
		if (paramType == 'double'):
			rtn = True
		if (paramType == 'string'):
			rtn = True
		return rtn

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
# 		Read CSV
#####################################
def readCSVInputFile(env, stage, algo, app):
	print("-------------------------------")
	print("            Read CSV")
	print("-------------------------------")
	# parse CSV
	with open(env.Filename) as csvfile:
		csvreader = csv.reader(csvfile, delimiter=',')
		for i, row in enumerate(csvreader):
			if (len(row) < 2):
				continue;
			name = row[0]
			value = row[1]
			# get StagePrefix
			if name == "StagePrefix":
				print("Found StagePrefix")
				stage.Prefix = value
			# get StageName
			elif name == "StageName":
				print("Found StageName")
				stage.Name = value
			# get AlgorithmName
			elif name == "AlgorithmName":
				print("Found AlgorithmName")
				algo.Name = value
			# get AlgorithmExtends
			elif name == "AlgorithmExtends":
				print("Found AlgorithmExtends")
				algo.Extends = value
			# get AuthorName
			elif name == "AuthorName":
				print("Found AuthorName")
				env.AuthorName = value
			# get ApplicationNames
			elif name == "ApplicationNames":
				print("Found ApplicationNames")
				for entry in row:
					if ((entry != name)):
						app.addName(entry)
			else:
				# get param type
				if (len(row) < 3):
					continue;
				paramType = row[2]
				# get param added
				paramAddedFlag = ""
				if (len(row) > 3):
					paramAddedFlag = row[3]
				# is parameter type valid
				paramType = paramType.lower()
				if (validateParamType(paramType)):
					if (paramType == 'string'):
						paramtype = "String"
					param = Param()
					param.Name = name
					param.Type = paramType
					param.Value = value
					param.Added = paramAddedFlag
					algo.addParam(param)
					print ("Found parameter: " + name)
				else:
					print (name + " has an invalid type.")

	

#####################################
#	Add path to classpath
#####################################
def addToClasspath(env, stage):
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
			# found class path
			if line.strip().find("kind=\"src\" path=\"src/" + StageName + "\"/>") != -1:
				found = True
			# append if not found
			if ((line.strip() == "</classpath>") and (found == False)):
				print("Appending classpath for Stage: " + StageName + "!")
				fileTemp.append("\t<classpathentry kind=\"src\" path=\"src/" + StageName + "\"/>\n")
			fileTemp.append(line)
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
#	CreateStageDirectories
#####################################
def createStageDirectories(env, stage):
	print("Creating stage")
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	# create source directory
	final_path = getSourcePath(ProjectDirectory,StageName)
	makedir(final_path)
	# create resource directories
	final_path = os.path.join(ProjectDirectory,"src","resources-"+StageName)
	makedir(final_path)
	final_path = os.path.join(ProjectDirectory,"src","resources-"+StageName,"executable")
	makedir(final_path)
	final_path = os.path.join(ProjectDirectory,"src","resources-"+StageName,"executable","scripts")
	makedir(final_path)
	final_path = os.path.join(ProjectDirectory,"src","resources-"+StageName,"executable","applications")
	makedir(final_path)
	final_path = getAlgorithmPath(ProjectDirectory,StageName)
	makedir(final_path)
	# create sample-input directories
	final_path = getSampleInputPath(ProjectDirectory,StageName)
	makedir(final_path)
	# create results directories
	final_path = getResultsPath(ProjectDirectory,StageName)
	makedir(final_path)
	# create data directories
	final_path = getDataPath(ProjectDirectory,StageName)
	makedir(final_path)
	# create constraint directories
	final_path = getConstraintPath(ProjectDirectory,StageName)
	makedir(final_path)

#####################################
#	Copy Stage Files And Replace
#####################################
def copyStageFilesAndReplace(env, stage, NewFilePath, TemplateFilePath):
	StageName = stage.Name
	StagePrefix = stage.Prefix
	print ("Copying " + TemplateFilePath + " to " + NewFilePath)
	# copy file
	templateFile = open(TemplateFilePath, "r")
	newFile = open(NewFilePath, "w")
	for line in templateFile:
		newLine = line.replace("##NONCE##21##STAGENAME##21##NONCE", StageName)
		newLine = newLine.replace("##NONCE##21##STAGEPREFIX##21##NONCE",StagePrefix)
		newFile.write(newLine)
	templateFile.close()
	newFile.close()

#####################################
#	Copy Stage Files
#####################################
def copyStageFiles(env, stage):
	StageName = stage.Name
	StagePrefix = stage.Prefix
	ProjectDirectory = env.ProjectDirectory
	TemplateDirectory = env.TemplateDirectory
	TemplateDirectory = os.path.join(TemplateDirectory,"stage")
	print("Copying Stage Files")
	files = []
	# fix reference
	TemplateDirectory = os.path.join(TemplateDirectory,"")
	for (dirpath, dirnames, filenames) in os.walk(TemplateDirectory):
		# relative path to template file
		relativepath = os.path.relpath(dirpath,TemplateDirectory)
		for files in filenames:
			# template path
			templatePath = os.path.join(TemplateDirectory,relativepath,files)
			# new file path
			newFilePath = os.path.join(getSourcePath(ProjectDirectory, StageName),StageName,relativepath)
			# mkdir
			makedir(newFilePath)
			# add filename
			if files != 'Main.java':
				newFilePath = os.path.join(newFilePath,StagePrefix+files)
			else:
				newFilePath = os.path.join(newFilePath,files)
			# copy file
			copyStageFilesAndReplace(env, stage, newFilePath, templatePath)

#####################################
#	Copy Results Files
#####################################
def copyResultsFiles(env, stage):
	StageName = stage.Name
	StagePrefix = stage.Prefix
	ProjectDirectory = env.ProjectDirectory
	TemplateDirectory = env.TemplateDirectory
	print("Copying Results Files")
	# template path
	templatePath = os.path.join(TemplateDirectory,"stage_results","Results.java")
	# new file path
	newFilePath = os.path.join(getResultsPath(ProjectDirectory, StageName),StagePrefix+"Results.java")
	# copy file
	copyStageFilesAndReplace(env, stage, newFilePath, templatePath)

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
		print ("Stage " + StageName + " exists.")
	else:
		print ("Adding stage " + StageName + ".")
	if stageExists == False:
		#####################################
		# 		classpath
		#####################################
		addToClasspath(env, stage)
		#####################################
		#	CreateStageDirectories
		#####################################
		createStageDirectories(env, stage)
		#####################################
		#	copyStageFiles
		#####################################
		copyStageFiles(env, stage)
		#####################################
		#	copyResultsFiles
		#####################################
		copyResultsFiles(env, stage)
	return stageExists

#####################################
# 		createAlgo
#####################################
def createAlgo(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	print("-------------------------------")
	print("		Creating algo")
	print("-------------------------------")
	# create algo directory
	algo_path = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm",AlgorithmName)
	makedir(algo_path)
	# create algo data directory
	algo_path = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm",AlgorithmName, "data")
	makedir(algo_path)

#####################################
# Copy Algorithm File And Replace
#####################################
def copyAlgoFileAndReplace(env, stage, algo, NewFilePath, TemplateFilePath, isData):
	StageName = stage.Name
	StagePrefix = stage.Prefix
	AlgorithmName = algo.Name
	AlgorithmExtends = algo.Extends
	print ("Copying " + TemplateFilePath + " to " + NewFilePath)
	AlgorithmExtendsIsEmpty = isEmpty(AlgorithmExtends)
	# copy file
	templateFile = open(TemplateFilePath, "r")
	newFile = open(NewFilePath, "w")
	for line in templateFile:
		# set parameter values
		if line.find("##NONCE##21##SETPARAMETERVALUES##21##NONCE") > -1:
			numParam = algo.getNumParams()
			numAddedParam = algo.getNumAddedParams()
			if ((numParam > 0) and (AlgorithmExtendsIsEmpty)) or (not(AlgorithmExtendsIsEmpty) and (numAddedParam > 0)):
				newFile.write("\t\tBoolean present = true;\n")
			for idx in range(0,numParam):
				param = algo.getParamAtIdx(idx)
				paramName = param.Name
				paramType = param.Type.title()
				paramValue = param.Value
				paramAddedFlag = param.Added
				if (not AlgorithmExtendsIsEmpty) and (not paramAddedFlag):
					continue
				newFile.write("\t\tpresent = this.getAlgorithmProfile().get"+paramType+"Parameter(\"" + paramName + "\").getFirst();\n")
				newFile.write("\t\tif (present) {\n")
				newFile.write("\t\t\tthis.set" + paramName+"(this.getAlgorithmProfile().get"+paramType+"Parameter(\"" + paramName + "\").getSecond());\n")
				newFile.write("\t\t}\n")
		# get/set parameter
		elif line.find("##NONCE##21##PARAMETERGETTERSETTER##21##NONCE") > -1:
			for idx in range(0,numParam):
				param = algo.getParamAtIdx(idx)
				paramName = param.Name
				paramType = param.Type
				if (paramType == 'string'):
					paramType = paramType.title()
				paramValue = param.Value
				paramAddedFlag = param.Added
				if (not AlgorithmExtendsIsEmpty) and (not paramAddedFlag):
					continue
				newFile.write("\t/**\n")
				newFile.write("\t * Setter for <i>" + paramName +"</i>\n")
				newFile.write("\t * @param value the value to set <i>" + paramName +"</i>\n")
				newFile.write("\t*/\n")
				newFile.write("\tprotected void set" + paramName + "(final " + paramType + " value){\n")
				newFile.write("\t\tthis." + paramName +" = value;\n")
				newFile.write("\t}\n\n")
				newFile.write("\t/**\n")
				newFile.write("\t * Getter for <i>" + paramName +"</i>\n")
				newFile.write("\t * @return value of <i>" + paramName +"</i>\n")
				newFile.write("\t*/\n")
				newFile.write("\tprotected " + paramType + " get" + paramName +"(){\n")
				newFile.write("\t\treturn this." + paramName +";\n")
				newFile.write("\t}\n\n")
				newFile.write("\tprivate " + paramType + " " + paramName + ";\n\n")
		# other
		else:
			newLine = line
			# Extends
			if newLine.find("##NONCE##21##EXTENDS##21##NONCE") > -1:
				if isData == False:
					if AlgorithmExtendsIsEmpty:
						newLine = newLine.replace("##NONCE##21##EXTENDS##21##NONCE", StagePrefix + "Algorithm")
					else:
						if newLine.find("import ") > -1:
							newLine = newLine.replace("##NONCE##21##EXTENDS##21##NONCE", AlgorithmExtends + "." + AlgorithmExtends)
						else:
							newLine = newLine.replace("##NONCE##21##EXTENDS##21##NONCE", AlgorithmExtends)
				elif isData == True:
					if AlgorithmExtendsIsEmpty:
						if newLine.find("import ") > -1:
							newLine = newLine.replace("##NONCE##21##EXTENDS##21##NONCE", "data." + StagePrefix)
						else:
							newLine = newLine.replace("##NONCE##21##EXTENDS##21##NONCE", StagePrefix)
					else:
						if newLine.find("import ") > -1:
							newLine = newLine.replace("##NONCE##21##EXTENDS##21##NONCE", AlgorithmExtends + ".data." + AlgorithmExtends)
						else:
							newLine = newLine.replace("##NONCE##21##EXTENDS##21##NONCE", AlgorithmExtends)
			#MACRO REPLACEMENT
			newLine = newLine.replace("##NONCE##21##AUTHORNAME##21##NONCE", env.AuthorName)
			newLine = newLine.replace("##NONCE##21##STAGENAME##21##NONCE", StageName)
			newLine = newLine.replace("##NONCE##21##STAGEPREFIX##21##NONCE",StagePrefix)
			newLine = newLine.replace("##NONCE##21##ALGONAME##21##NONCE",AlgorithmName)
			newFile.write(newLine)
	templateFile.close()
	newFile.close()
			
#####################################
# 	Copy Algorithm File
#####################################
def copyAlgorithmFile(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	TemplateDirectory = env.TemplateDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	AlgorithmExtends = algo.Extends
	print("-------------------------------")
	print("Copying Algorithm Files")
	print("-------------------------------")
	# template file
	TemplateFilePath = os.path.join(TemplateDirectory,"algorithm")
	if (isEmpty(AlgorithmExtends)):
		TemplateFilePath = os.path.join(TemplateFilePath,"TEMPLATE.java")
	else:
		TemplateFilePath = os.path.join(TemplateFilePath,"TEMPLATEEXTENDS.java")
	# exit if file not present
	if not isfile(TemplateFilePath):
		sys.exit()
	# new file path
	NewFilePath = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm",AlgorithmName,AlgorithmName+".java")
	# copy file
	copyAlgoFileAndReplace(env, stage, algo, NewFilePath, TemplateFilePath, False)
	# template reference
	TemplateFilePath = os.path.join(TemplateDirectory,"algorithm")
	for (dirpath, dirnames, filenames) in os.walk(TemplateFilePath):
		# relative path to template file
		relativepath = os.path.relpath(dirpath,TemplateFilePath)
		# skip if files in root directory
		if (relativepath == "."):
			continue
		for files in filenames:
			# template path
			templatePath = os.path.join(TemplateFilePath,relativepath,files)
			# new file path
			newFilePath = os.path.join(getSourcePath(ProjectDirectory, StageName),StageName,"algorithm",AlgorithmName,relativepath)
			# mkdir
			makedir(newFilePath)
			# add filename
			newFilePath = os.path.join(newFilePath,AlgorithmName+files)
			# copy file
			copyAlgoFileAndReplace(env, stage, algo, newFilePath, templatePath, True)

#####################################
# 	Update Algorithm Factory
#####################################
def updateAlgorithmFactory(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	StagePrefix = stage.Prefix
	AlgorithmName = algo.Name
	print("Updating Algorithm Factory")
	# new file path
	AFFilePath = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm",StagePrefix+"AlgorithmFactory.java")
	print("Updating Algorithm Factory: " + AFFilePath)
	# update file
	AFFile = open(AFFilePath, "r")
	newFile = []
	for line in AFFile:
		newFile.append(line);
		if line.find("common.algorithm.AlgorithmFactory;") > -1:
			newFile.append("import "+StageName+".algorithm."+AlgorithmName+"."+AlgorithmName+";\n");
		if line.find("rtn = null") > -1:
			newFile.append("\t\tif (name.equals(\""+AlgorithmName+"\")){\n");
			newFile.append("\t\t\trtn = new " + AlgorithmName + "();\n");
			newFile.append("\t\t}\n");
	AFFile.close()
	AFFile = open(AFFilePath, "w")
	for line in newFile:
		AFFile.write(line)
	AFFile.close()


#####################################
# Update Algorithm Data Factory File
#####################################
def updateAlgorithmDataFactoryFile(env, stage, algo, filePath, NetlistType):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	StagePrefix = stage.Prefix
	AlgorithmName = algo.Name
	print("Updating Algorithm Data Factory: " + filePath)
	# update file
	File = open(filePath, "r")
	newFile = []
	for line in File:
		newFile.append(line);
		if line.find("common.algorithm.data.Netlist") > -1:
			newFile.append("import "+StageName+".algorithm."+AlgorithmName+".data."+AlgorithmName+NetlistType+"Data;\n");
		if line.find("rtn = null") > -1:
			newFile.append("\t\tif (name.equals(\""+AlgorithmName+"\")){\n");
			newFile.append("\t\t\trtn = new " + AlgorithmName + NetlistType+ "Data();\n");
			newFile.append("\t\t}\n");
	File.close()
	File = open(filePath, "w")
	for line in newFile:
		File.write(line)
	File.close()

#####################################
# 	Update Algorithm Data Factory
#####################################
def updateAlgorithmDataFactory(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	StagePrefix = stage.Prefix
	AlgorithmName = algo.Name
	print("Updating Algorithm Data Factory")
	# new file path
	File = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm","data",StagePrefix+"NetlistDataFactory.java")
	print (File)
	updateAlgorithmDataFactoryFile(env, stage, algo, File, "Netlist")
	File = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm","data",StagePrefix+"NetlistEdgeDataFactory.java")
	updateAlgorithmDataFactoryFile(env, stage, algo, File, "NetlistEdge")
	File = os.path.join(getSourcePath(ProjectDirectory,StageName),StageName,"algorithm","data",StagePrefix+"NetlistNodeDataFactory.java")
	updateAlgorithmDataFactoryFile(env, stage, algo, File, "NetlistNode")

#####################################
# 	Write Algorithm JSON
#####################################
def writeAlgoJSON(env, stage, algo):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	# new file path
	JSONFilePath = getAlgorithmPath(ProjectDirectory,StageName)
	JSONFilePath = os.path.join(JSONFilePath,AlgorithmName)
	makedir(JSONFilePath)
	JSONFilePath = os.path.join(JSONFilePath,AlgorithmName+".json")
	# update file
	print("Write Algorithm JSON")
	JSONFile = open(JSONFilePath, "w")
	# header
	JSONFile.write("{\n")
	JSONFile.write("\t\"name\": \""+AlgorithmName+"\",\n")
	JSONFile.write("\t\"parameters\":\n\t[\n")
	numParam = algo.getNumParams()
	for idx in range(0,numParam):
		param = algo.getParamAtIdx(idx)
		paramName = param.Name
		paramType = param.Type
		paramValue = param.Value
		JSONFile.write("\t\t{\n")
		JSONFile.write("\t\t\t\"name\" : \"" + paramName + "\",\n")
		JSONFile.write("\t\t\t\"type\" : \"" + paramType + "\",\n")
		JSONFile.write("\t\t\t\"value\": ")
		if (paramType == "char") or (paramType == "string"):
			JSONFile.write("\"")
		if (paramType == "boolean"):
			paramValue = paramValue.lower()
		if (paramType == "char"):
			if (len(paramValue) > 0):
				paramValue = paramValue[0]
		if (paramType == "float") or (paramType == "double"):
			if (paramValue.find(".") < 0):
				paramValue = paramValue + ".0"
		JSONFile.write(paramValue)
		if (paramType == "char") or (paramType == "string"):
			JSONFile.write("\"")
		JSONFile.write("\n")
		JSONFile.write("\t\t},\n")		
	JSONFile.write("\t]\n")
	JSONFile.write("}")
	JSONFile.close()

#####################################
# 	Write Algorithm CSV
#####################################
def writeAlgoCSV(env, stage, algo, app):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	StagePrefix = stage.Prefix
	AlgorithmName = algo.Name
	# new file path
	CSVFilePath = getAlgorithmPath(ProjectDirectory,StageName)
	CSVFilePath = os.path.join(CSVFilePath,AlgorithmName)
	makedir(CSVFilePath)
	CSVFilePath = os.path.join(CSVFilePath,AlgorithmName+".csv")
	# write file
	print("Write Algorithm CSV")
	csvfile = open(CSVFilePath, "w")
	# write
	csvfile.write("AuthorName,,\n")
	# application names
	csvfile.write("ApplicationNames,")
	numAppNames = app.getNumNames()
	for idx in range(0,numAppNames):
		name = app.getNameAtIdx(idx)
		csvfile.write(name + ",")
	csvfile.write("\n")
	# stage
	csvfile.write("StagePrefix," + StagePrefix + "," +"\n")
	csvfile.write("StageName," + StageName + "," +"\n")
	# algo
	csvfile.write("AlgorithmName," + "" + "," +"\n")
	csvfile.write("AlgorithmExtends," + AlgorithmName + "," +"\n")
	numParam = algo.getNumParams()
	for idx in range(0,numParam):
		param = algo.getParamAtIdx(idx)
		paramName = param.Name
		paramType = param.Type
		paramValue = param.Value
		if paramType == 'string':
			paramValue = "\"" + paramValue + "\"";
		csvfile.write(paramName + "," + paramValue + "," + paramType + "," +"\n")
	csvfile.close()

#####################################
# 		Algorithm
#####################################
def doAlgo(env, stage, algo, app):
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
		print ("Algorithm " + AlgorithmName + " exists.")
	else:
		print ("Adding Algorithm " + AlgorithmName + ".")
	## create algo
	if algoExists == False:
		createAlgo(env, stage, algo)
		copyAlgorithmFile(env, stage, algo)
		updateAlgorithmFactory(env, stage, algo)
		updateAlgorithmDataFactory(env, stage, algo)
		writeAlgoJSON(env, stage, algo)
		writeAlgoCSV(env, stage, algo, app)
	return algoExists

#####################################
# 		addStageToJSON
#####################################
def addStageToJSON(env, stage, algo, appName):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	AlgorithmName = algo.Name
	JSONFilePath = getResourcePath(ProjectDirectory, appName)
	JSONFilePath = os.path.join(JSONFilePath, "Configuration.json")
	if (isfile(JSONFilePath)):
		print("Adding Stage To JSON")
		# load json
		data = json.load(open(JSONFilePath))
		if "stages" in data:
			# add new stage
			stages = data["stages"]
			stages.append({u'name': StageName, u'algorithm_name':AlgorithmName})
			# write file
			with open(JSONFilePath, 'w') as outfile:
				json.dump(data, outfile, indent=4)

#####################################
# 	addImportToApplication
#####################################
def addImportToApplication(env, stage, algo, appName):
	ProjectDirectory = env.ProjectDirectory
	StageName = stage.Name
	StagePrefix = stage.Prefix
	AlgorithmName = algo.Name
	JAVAFilePath = getSourcePath(ProjectDirectory, appName)
	JAVAFilePath = os.path.join(JAVAFilePath, appName, "runtime", "Main.java")
	if (isfile(JAVAFilePath)):
		print("Adding Import To JAVA")
		# parse Main.java (Application)
		fileInfo = open(JAVAFilePath, "r")
		fileTemp = []
		startSearch = False
		found = False
		counter = 0
		index = 0
		# read
		for line in fileInfo:
			# stop search after class declaration
			if (line.strip().find("public") > -1) and (line.strip().find("class") > -1) and (line.strip().find("Main") > -1):
				startSearch = False
			if (startSearch) and (line.strip().find("import") > -1) and (line.strip().find(StageName + ".runtime." + StagePrefix + "RuntimeObject;") > -1):
				found = True
			fileTemp.append(line)
			# start search after package header
			# append import
			if (line.strip().find("package") > -1) and (line.strip().find(appName + ".runtime") > -1):
				index = counter + 1
				startSearch = True
				fileTemp.append("import " + StageName + ".runtime." + StagePrefix + "RuntimeObject;\n")
			counter = counter + 1
		if found:
			del fileTemp[index]
		fileInfo.close
		# write
		fileInfo = open(JAVAFilePath, "w")
		for line in fileTemp:
			fileInfo.write(line)
		fileInfo.close
	else:
		print("Application " + appName + " not found!")
	print("-------------------------------")


#####################################
# 	printApplicationInfo
#####################################
def printApplicationInfo(env, stage, algo, app, templateFilePath):
	StageName = stage.Name
	StagePrefix = stage.Prefix
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
			newLine = newLine.replace("##NONCE##21##STAGEPREFIX##21##NONCE",StagePrefix)
			newLine = newLine.replace("##NONCE##21##ALGONAME##21##NONCE",AlgorithmName)
		print(newLine, end='')
		countLines = countLines + 1
	if countLines > 0:
		print()
	templateFile.close()

#####################################
# 		Application
#####################################
def doApplication(env, stage, algo, app, stageExists, algoExists):
	print("-------------------------------")
	print("            Application")
	print("-------------------------------")
	TemplateDirectory = env.TemplateDirectory
	if stageExists == False:
		numAppNames = app.getNumNames()
		for idx in range(0,numAppNames):
			name = app.getNameAtIdx(idx)
			print("Looking at application: " + name)
			# addStageToJSON
			addStageToJSON(env, stage, algo, name)
			# addImportToApplication
			addImportToApplication(env, stage, algo, name)
		# new file path
		TemplatePath = os.path.join(TemplateDirectory,"application","add","TEMPLATENOTEXIST")
		printApplicationInfo(env, stage, algo, app, TemplatePath)
	else:
		# new file path
		TemplatePath = os.path.join(TemplateDirectory,"application","add","TEMPLATEEXIST")
		printApplicationInfo(env, stage, algo, app, TemplatePath)
	TemplatePath = os.path.join(TemplateDirectory,"application","add","TEMPLATEJAVA")
	printApplicationInfo(env, stage, algo, app, TemplatePath)
		
#####################################
# 		Main
#####################################
def main(arguments):
	# parse arguments
	parser = argparse.ArgumentParser(
		description=__doc__,
		formatter_class=argparse.RawDescriptionHelpFormatter)
	parser.add_argument('-i','--infile', help="Input file", required=True)
	parser.add_argument('-p','--projectDir', help="Project Directory", required=True)
	args = parser.parse_args(arguments)

	#####################################
	# 	Get Command Line Arguments
	#####################################
	# get Filename
	Filename = args.infile
	# ProjectDirectory
	ProjectDirectory = args.projectDir

	#####################################
	# 		Env
	#####################################
	env = Env()
	env.Filename = Filename
	env.ProjectDirectory = os.path.join(ProjectDirectory,"")
	env.TemplateDirectory = os.path.join(ProjectDirectory,"tools","templates")

	#####################################
	# 		stage
	#####################################
	stage = Stage()

	#####################################
	# 		algo
	#####################################
	algo = Algo()

	#####################################
	# 		app
	#####################################
	app = Application()

	#####################################
	# 		Read CSV
	#####################################
	if env.isValid():
		readCSVInputFile(env, stage, algo, app)
	else:
		print("Error occured")
		sys.exit()

	#####################################
	# 		Create Stage
	#####################################
	stageExists = doStage(env, stage, algo)

	#####################################
	# 		Create Algo
	#####################################
	algoExists = doAlgo(env, stage, algo, app)

	#####################################
	# 		Application
	#####################################
	doApplication(env, stage, algo, app, stageExists, algoExists)

#####################################
# 		Entry
#####################################
if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
