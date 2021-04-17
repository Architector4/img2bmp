# img2bmp
A Java tool that converts images to 8-bit BMP images compatible with Cortex Command videogame by using the color palette that the game uses.

img2bmp.jar is the compiled file ready to use.

### Usage:

`img2bmp [-ho] input [output]`

`-h` tag prints help and info.

`-o` tag allows overwriting existing files.

If no output is specified, the result is saved alongside input image with .bmp extension.

If the original file has .bmp extension by itself and `-o` is not specified, it will give the "file exists" error.


If no parameters are specified, it will open a GUI with 2 buttons and a log text box.

### Inspired by this guide in the game's forums:
https://steamcommunity.com/sharedfiles/filedetails/?id=233129307

---

Please note that this is my first GitHub repository, and this code is really old, amateur and possibly quite messy. But it does the job though - the images produced by it seem to work just fine in Cortex Command.
