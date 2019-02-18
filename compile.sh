#!/bin/sh
# If you plan on actually using this, please don't put random .java or .class files not belonging to the thing.

# Compile Java code to .class files
javac src/team/tilde/architector4/img2bmp/*.java
# Move .class files to bin directory
mv src/team/tilde/architector4/img2bmp/*.class bin/team/tilde/architector4/img2bmp/
# Go to that bin directory
cd bin
# Compile .jar file
jar -cfe ../img2bmp.jar team/tilde/architector4/img2bmp/IMG2BMP team/tilde/architector4/img2bmp/*.class
# Make that .jar file executable
chmod +x ../img2bmp.jar
