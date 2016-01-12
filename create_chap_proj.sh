#####################################################
# Prompt user to create a chapter and project name
#####################################################

# Chapter name
echo "Please enter the chapter name directory (ie. chap01):"
read chapname

# Project name
echo "Please enter the project name directory (ie. MyProject):"
read projname

# Java Main class name
echo "Please enter the Java class name (ie. MyExample):"
read javamain

# If chapter directory not already created.
if [ ! -e $chapname ]; then
   echo "Creating $chapname "
   mkdir -p $chapname
fi
###### FIX THE PRO
# Error out if project already exists
if [ -e $chapname/$projname ]; then
   echo "Error $chapname/$projname already exists"
   exit 1
else
   mkdir -p $chapname/$projname

   # Preserve chapX project
   cp -R chapX/projectX/* $chapname/$projname
  
   # Create a project using the java main class.
   #mv "$chapname/projectX" "$chapname/$projname"
   sed -i '' "s/ProjectX/$javamain/" "$chapname/$projname/build.gradle"
   sed -i '' "s/ProjectX/$projname/" "$chapname/$projname/settings.gradle"

   mv "$chapname/$projname/src/main/java/com/jfxbe/ProjectX.java" "$chapname/$projname/src/main/java/com/jfxbe/$javamain.java"
   sed -i '' "s/ProjectX/$javamain/" "$chapname/$projname/src/main/java/com/jfxbe/$javamain.java"
fi  
