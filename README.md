# img2bmp
A Java tool that converts images to 8-bit BMP images compatible with Cortex Command videogame by using the color palette that the game uses.

Use "Releases" tab on the right to download a usable version.

### Usage:

Run the jar file either directly by clicking on it, or via the command line:

`java -jar img2bmp.jar [-h] [-o] INPUTFILE OUTPUTFILE`

`-h` - print help and info and exit.

`-o` - allow overwriting existing files.

If no output file is specified, the result is saved alongside input image with .bmp extension.

If the original file has .bmp extension by itself and `-o` is not specified, it will give the "file exists" error.

If no parameters are specified, a GUI is opened.

### Inspired by this guide in the game's forums:
https://steamcommunity.com/sharedfiles/filedetails/?id=233129307

---

Please note that this is my first GitHub repository, and this code is really old, amateur and possibly quite messy. But it does the job though - the images produced by it seem to work just fine in Cortex Command.
