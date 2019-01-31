# img2bmp
A Java tool that converts images to 8-bit BMP images compatible with Cortex Command videogame by using the color palette that the game uses.

img2bmp.jar is the compiled file ready to use.

### Usage:

`img2bmp [-ho] input [output]`
`-h` tag prints help and info.
`-o` tag allows overwriting existing files.
If no output is specified, the result is saved alongside input image with .bmp extension.
If the original file has .bmp extension by itself, it will give the "file exists" error.

If no parameters are specified, it will open a GUI with 2 buttons and a log text box.

### Inspired by this guide in the game's forums:
https://steamcommunity.com/sharedfiles/filedetails/?id=233129307


_(also this is my first github repository, so sorry if this isn't really professional enough or something lol)_
