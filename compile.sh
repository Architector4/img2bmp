javac src/team/tilde/architector4/img2bmp/*.java
mv src/team/tilde/architector4/img2bmp/*.class bin/team/tilde/architector4/img2bmp/
cd bin
jar -cfe ../img2bmp.jar team/tilde/architector4/img2bmp/IMG2BMP team/tilde/architector4/img2bmp/*.class
chmod +x ../img2bmp.jar
