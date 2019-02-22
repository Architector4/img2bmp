#!/bin/sh
# If you plan on actually using this, please don't put random .java or .class files not belonging to the thing.

packname="team/tilde/architector4/img2bmp"

# Compile Java code to .class files
javac src/$packname/*.java &&
# Move .class files to bin directory
mv src/$packname/*.class bin/$packname/ &&
# Go to that bin directory
cd bin &&
# Compile .jar file
jar -cfe ../img2bmp.jar $packname/IMG2BMP $packname/*.class &&
# Make that .jar file executable
chmod +x ../img2bmp.jar
