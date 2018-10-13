#!/usr/bin/env python

"""add_remove_application.py
"""

from __future__ import print_function
import os
import sys
import argparse
import shutil

#####################################
#	getSourcePath
#####################################
def getSourcePath(Directory, Name):
	return os.path.join(Directory,"src",Name)

#####################################
#	getResourcePath
#####################################
def getResourcePath(Directory, Name):
	return os.path.join(Directory,"src","resources-"+Name)

#####################################
#	getSampleInputPath
#####################################
def getSampleInputPath(Directory, Name):
	return os.path.join(Directory,"sample-input",Name)

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
		self.Remove = False

	# ProjectDirectory
	@property
	def ProjectDirectory(self):
		return self.__ProjectDirectory

	# ProjectDirectory
	@ProjectDirectory.setter
	def ProjectDirectory(self, ProjectDirectory):
		if (ProjectDirectory != None):
			self.__ProjectDirectory = ProjectDirectory

	# Remove
	@property
	def Remove(self):
		return self.__Remove

	# Remove
	@Remove.setter
	def Remove(self, Remove):
		if (Remove != None):
			self.__Remove = Remove

	# isValid
	def isValid(self):
		rtn = True
		if (self.ProjectDirectory == ""):
			rtn = False
		if (not os.path.isdir(self.ProjectDirectory)):
			rtn = False
		return rtn

#####################################
#	Application Class
#####################################
class Application(object):

	# constructor
	def __init__(self):
		self.Name = ""
		self.AuthorName = "Vincent Mirian"

	# Name
	@property
	def Name(self):
		return self.__Name

	# Name
	@Name.setter
	def Name(self, Name):
		if (Name != None):
			self.__Name = Name

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
		if (self.Name == ""):
			rtn = False
		return rtn
		
#####################################
# 		createDirectories
#####################################
def createDirectories(env, app):
	print("Creating application directory")
	ProjectDirectory = env.ProjectDirectory
	AppName = app.Name
	# create source directory
	final_path = getSourcePath(ProjectDirectory,AppName)
	makedir(final_path)
	# create resource directories
	final_path = getResourcePath(ProjectDirectory,AppName)
	makedir(final_path)
	final_path = os.path.join(getResourcePath(ProjectDirectory,AppName),"executable")
	makedir(final_path)
	final_path = os.path.join(getResourcePath(ProjectDirectory,AppName),"executable","scripts")
	makedir(final_path)
	final_path = os.path.join(getResourcePath(ProjectDirectory,AppName),"executable","applications")
	makedir(final_path)
	# create sample-input directories
	final_path = getSampleInputPath(ProjectDirectory,AppName)
	makedir(final_path)

#####################################
#	Add path to classpath
#####################################
def addToClasspath(env, app):
	print("-------------------------------")
	print("		Updating classpath")
	print("-------------------------------")
	ProjectDirectory = env.ProjectDirectory
	AppName = app.Name
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
			if line.strip().find("kind=\"src\" path=\"src/" + AppName + "\"/>") != -1:
				found = True
			# append if not found
			if ((line.strip() == "</classpath>") and (found == False)):
				print("Appending classpath for Application: " + AppName + "!")
				fileTemp.append("\t<classpathentry kind=\"src\" path=\"src/" + AppName + "\"/>\n")
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
#	Copy File for App
#####################################
def copyStageFileAndReplace(env, app, NewFilePath, TemplateFilePath):
	AppName = app.Name
	AuthorName = app.AuthorName
	print ("Copying " + TemplateFilePath + " to " + NewFilePath)
	# copy file
	templateFile = open(TemplateFilePath, "r")
	newFile = open(NewFilePath, "w")
	for line in templateFile:
		newLine = line.replace("##NONCE##21##APPNAME##21##NONCE", AppName)
		newLine = newLine.replace("##NONCE##21##AUTHORNAME##21##NONCE",AuthorName)
		newFile.write(newLine)
	templateFile.close()
	newFile.close()

#####################################
#	Copy Source Files for App
#####################################
def copySourceFileForApp(env, app):
	AppName = app.Name
	ProjectDirectory = env.ProjectDirectory
	src_path = os.path.join(getSourcePath(ProjectDirectory,AppName),AppName)
	makedir(src_path)
	template_path = os.path.join(ProjectDirectory,"tools","templates","application","src")
	# fix reference
	template_path = os.path.join(template_path,"")
	for (dirpath, dirnames, filenames) in os.walk(template_path):
		# relative path to template file
		relativepath = os.path.relpath(dirpath,template_path)
		for files in filenames:
			# template path
			templatePath = os.path.join(template_path,relativepath,files)
			# new file path
			newFilePath = os.path.join(src_path,relativepath)
			# mkdir
			makedir(newFilePath)
			# add filename
			if files != 'Main.java':
				newFilePath = os.path.join(newFilePath,AppName+files)
			else:
				newFilePath = os.path.join(newFilePath,files)
			# copy file
			copyStageFileAndReplace(env, app, newFilePath, templatePath)

#####################################
#	Copy Resource Files for App
#####################################
def copyResourceFileForApp(env, app):
	AppName = app.Name
	ProjectDirectory = env.ProjectDirectory
	src_path = os.path.join(getResourcePath(ProjectDirectory,AppName))
	makedir(src_path)
	template_path = os.path.join(ProjectDirectory,"tools","templates","application","resource")
	# fix reference
	template_path = os.path.join(template_path,"")
	for (dirpath, dirnames, filenames) in os.walk(template_path):
		# relative path to template file
		relativepath = os.path.relpath(dirpath,template_path)
		for files in filenames:
			# template path
			templatePath = os.path.join(template_path,relativepath,files)
			# new file path
			newFilePath = os.path.join(src_path,relativepath)
			# mkdir
			makedir(newFilePath)
			# add filename
			newFilePath = os.path.join(newFilePath,files)
			# copy file
			copyStageFileAndReplace(env, app, newFilePath, templatePath)

#####################################
#	Copy Files for App
#####################################
def copyFilesForApp(env, app):
	copySourceFileForApp(env, app)
	copyResourceFileForApp(env, app)

#####################################
# 		createApp
#####################################
def createApp(env, app):
	createDirectories(env, app)
	addToClasspath(env, app)
	copyFilesForApp(env, app)

#####################################
#	Remove path to classpath
#####################################
def removeFromClasspath(env, app):
	print("-------------------------------")
	print("		Updating classpath")
	print("-------------------------------")
	ProjectDirectory = env.ProjectDirectory
	AppName = app.Name
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
			if (not ((line.strip().find("kind=\"src\" path=\"src/" + AppName + "\"/>"))) != -1):
				fileTemp.append(line)
			else:
				print("Removing classpath for App: " + AppName + "!")
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
# 		removeApp
#####################################
def removeApp(env, app):
	ProjectDirectory = env.ProjectDirectory
	AppName = app.Name
	print("Removing application: " + AppName)
	# remove source directory
	final_path = getSourcePath(ProjectDirectory,AppName)
	rmdir(final_path)
	# remove resource directories
	final_path = getResourcePath(ProjectDirectory,AppName)
	rmdir(final_path)
	# remove sample-input directories
	final_path = getSampleInputPath(ProjectDirectory,AppName)
	rmdir(final_path)
	# update classpath
	removeFromClasspath(env, app)

#####################################
# 		Main
#####################################
def main(arguments):
	# parse arguments
	parser = argparse.ArgumentParser(
		description=__doc__,
		formatter_class=argparse.RawDescriptionHelpFormatter)
	parser.add_argument('-p','--projectDir', help="Project Directory", required=True)
	parser.add_argument('-a','--appName', help="Application Name", required=True)
	parser.add_argument('-w','--authorName', help="Author(s) Name", required=False)
	parser.add_argument('-r','--remove', help="Remove", required=False, action='store_true')
	args = parser.parse_args(arguments)

	#####################################
	# 	Get Command Line Arguments
	#####################################
	# ProjectDirectory
	ProjectDirectory = args.projectDir
	# get AppName
	AppName = args.appName
	# get AuthorName
	AuthorName = args.authorName
	# get Remove
	Remove = args.remove

	#####################################
	# 		Env
	#####################################
	env = Env()
	env.ProjectDirectory = os.path.join(ProjectDirectory,"")
	env.Remove = Remove

	#####################################
	# 		Application
	#####################################
	app = Application()
	app.Name = AppName
	app.AuthorName = AuthorName

	#####################################
	# 		Execute
	#####################################
	if env.isValid() and app.isValid():
		if (not Remove):
			createApp(env, app)
		else:
			removeApp(env, app)
	else:
		print("Error occured")
		sys.exit()

#####################################
# 		Entry
#####################################
if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
