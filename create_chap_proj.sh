#####################################################
# Prompt user to create a chapter and project name
#####################################################
# Chapter name
echo "Please enter the chapter name directory (ie. chap01):"
read chapname

# Error out if project already exists
if [ -e $chapname/src ]; then
   echo "Error $chapname already exists"
   exit 1
else
   # Java Main class name
   echo "Please enter a Java class name containing the main method (ie. MyExample) [Simple name]:"
   read javamain

   mkdir -p $chapname/src
   mkdir -p $chapname/resources

   # Preserve chapX project
   cp -R chapX/* $chapname
  
   # Create a file using the java main class.
   mv "$chapname/src/com/jfxbe/ProjectX.java" "$chapname/src/com/jfxbe/$javamain.java"
   sed -i '' "s/ProjectX/$javamain/" "$chapname/src/com/jfxbe/$javamain.java"
   
   # Change the package shell script. Renames the jar file as the chapter name.
   sed -i '' "s/chapX/$chapname/" "$chapname/package"
   
   # Change the README.txt
   sed -i '' "s/ProjectX/$javamain/" "$chapname/README.txt"
   sed -i '' "s/chapX/$chapname/" "$chapname/README.txt"
fi  
