package team.tilde.architector4.img2bmp;

import java.text.NumberFormat;

/**
 * Contains the function that performs the conversion.
 * It also contains a bunch of functions needed for that function.
 */
public class ConvertStuff{

	/**
	 * Converts an image to an 8-bit BMP and saves it to a file.
	 * <p>
	 * It uses a palette defined in {@link BMPData} class
	 * to encode the colors.
	 * 
	 * @param	in		Image object that would be converted
	 * @param	outPath	Path to the output file, where the result will be written
	 * @param	gui		Reference to a object of class <code>GUIStuff</code>
	 * 					to report progress to it
	 * 
	 * @throws java.io.IOException	Unable to create/write to output file.
	 */
	public static void toBMP(
			java.awt.image.BufferedImage in,
			String outPath,
			GUIStuff gui) 
		throws java.io.IOException {
			// If you are using this elsewhere, you can pass null as "gui" argument instead.
			// It's to give feedback if the conversion takes a long time.
			// Yeah, I know, meh coding practices. Please tell me how to make this better!
			// And if you are actually using this function elsewhere, 
			// PLEASE TELL ME I WANT TO KNOW OwO

			// This is based on this:
			// http://www.dragonwins.com/domains/getteched/bmp/bmpfileformat.htm

			// This is going to have a lot of obvious comments.
			// I wrote them in case me in the future gets lost in this. lol

			// Just for the progress number.
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);



			final int width=in.getWidth();
			final int height=in.getHeight();


			java.io.FileOutputStream out = new java.io.FileOutputStream(outPath);

			try{
				out.write(BMPData.HEADER_1);				// 0-1
				out.write(toByte(widthoffset(width)*height+1078));	// 2 File size
				out.write(BMPData.HEADER_2);				// 6-17
				out.write(toByte(width));				// 18 Width
				out.write(toByte(height));				// 22 Height
				out.write(BMPData.HEADER_3);				// 26-53
				out.write(BMPData.PALETTE);				// 54-1024 Palette


				long time = 0;


				for(int i=0;i<widthoffset(width)*height;i++){		// 1078+ Image data

					//Progress feedback in GUI.
					if(gui!=null&&time+100<System.currentTimeMillis()){
						time = System.currentTimeMillis();
						gui.replaceLastLine(
								"Progress: "
								+nf.format(
									(double)i
									/(widthoffset(width)*height)
									*100)
								+"%"
								);
					}

					if(bytegood(i,width)){ // If it's not a spacing byte
						out.write((byte)bestPaletteColor(
									in.getRGB(
										bytex(i,width)
										,height-1-bytey(i,width)
										)
									,BMPData.PALETTE));
					}else{
						// Or if it's a spacing byte then don't bother.
						out.write(0);
					}
				}

			}catch(java.io.IOException e){
				out.close();
				if(gui!=null) gui.replaceLastLine("");
				throw e;
				// I'd use "Finally", but that won't allow me to throw this exception.
			}

			out.close();
			if(gui!=null) gui.replaceLastLine("");
		}

	/**
	 * Finds color in <code>palette</code> that is closest to <code>color</code>.<br>
	 * <b>Note</b> that alpha channel is ignored.<br>
	 * <b>Note</b> that color and palette have different input color orders.
	 * 
	 * @param color		Input color, encoded in an <code>int</code> of format
	 * 					<code>0xRRGGBBAA</code>.
	 * @param palette	Input palette, consisting of 4 bytes per color
	 * 					in order of blue, green, red, alpha.
	 * 
	 */
	public static int bestPaletteColor(int color,byte[] palette){

		//final int[] pixel={
		//	((color>>16)&0xFF),		// R
		//	((color>>8 )&0xFF),		// G
		//	((color    )&0xFF),		// B
		//	((color>>24)&0xFF)		// A
		//};

		if(((color>>24)&0xFF)!=255) return 0;
		// Is transparent

		int best=0; 
		// Best color for the pixel
		double diffbest=-1.0;
		//How good best color matches

		for(int u=1;u<palette.length/4;u++){ // Iterate through all palette colors

			final double diff = colorDistance(
					unsignbyte (palette[2+u*4])	// R
					,unsignbyte(palette[1+u*4])	// G
					,unsignbyte(palette[0+u*4])	// B
					,((color>>16)&0xFF)		// R
					,((color>>8 )&0xFF)		// G
					,( color     &0xFF)		// B
					);

			if(diff<diffbest||diffbest==-1.0){ // If this color's better
				diffbest=diff;
				best=u;
				if(diff==0.0d) return best; // Can't get any better than that!
			}
		}

		return best;
	} 


	/**
	 * Converts an <code>int</code> into 4 bytes packed as an array.
	 * @param a <code>int</code> to be converted into an array. 
	 */
	static byte[] toByte(int a){
		return new byte[]{
			(byte)((a)&0xFF)
				,(byte)((a>>8)&0xFF)
				,(byte)((a>>16)&0xFF)
				,(byte)(a>>24&0xFF)
		};
	}
	/**
	 * Converts an <code>short</code> into 2 bytes packed as an array.
	 * @param a <code>short</code> to be converted into an array. 
	 */
	static byte[] toByte(short a){ 
		return new byte[]{
			(byte)((a)&0xFF)
				,(byte)((a>>8)&0xFF)
		};
	}
	/**
	 * Returns number of a byte corresponding to the pixel on input coordinates.
	 * @param x X position of the pixel
	 * @param y Y position of the pixel
	 * @param width Width of the image
	 */
	static int bytepos(int x,int y,int width){ 
		// Converts input X Y coordinates into a byte number
		return x+y*(widthoffset(width));
	}

	/**
	 * Returns X coordinate of the pixel that this byte corresponds to.
	 * @param in Byte number
	 * @param width Width of the image
	 */
	static int bytex(int in,int width){ 
		// Converts a byte number into an X coordinate
		return in%(widthoffset(width));
	}
	/**
	 * Returns Y coordinate of the pixel that this byte corresponds to.
	 * @param in Byte number
	 * @param width Width of the image
	 */
	static int bytey(int in,int width){ 
		// Converts a byte number into an Y coordinate
		return in/(widthoffset(width));
	}
	/**
	 * Returns <code>true</code> if this byte is not a spacing byte.<br>
	 * This is used to not write into a spacing byte, of which there are some
	 * for each pixel row in the BMP image format.
	 * @param in Byte number
	 * @param width Width of the image
	 */
	static boolean bytegood(int in,int width){ 
		// Can I write in this byte, or it's one of
		// those spacing bytes at the end of each horizontal line?
		return in%(widthoffset(width))<width;
	}

	/**
	 * Returns the amount of bytes a single pixel row takes in a
	 * BMP image with specified width. <br>
	 * @param width Width of the image
	 */
	static int widthoffset(int width){
		//Figures width in bytes of 1 row of pixels.
		return (((width-1)/4)+1)*4;
		//0 1 2 3 4 5 6 7 8 9  10 11 12 13...
		//is
		//4 4 4 4 4 8 8 8 8 12 12 12 12 16...
	}

	/**
	 * Converts the input byte into a number as if it were an unsigned byte.
	 * @param in Byte to be converted.
	 */
	static int unsignbyte(byte in){
		//Removes minus from byte.
		return in<0?  0xFF+in  :  in&0x7F;
	}
	/**
	 * Returns the smaller number out of two.
	 */
	static int min(int a,int b){
		//Returns smaller of 2 values.
		return a<b?a:b;
	}
	/**
	 * Returns the bigger number out of two.
	 */
	static int max(int a,int b){
		//Returns bigger of 2 values.
		return a>b?a:b;
	}

	// Next function is yanked from:
	// https://stackoverflow.com/a/52562886/9925382
	// I'm too lazy to do that Lab color space comparison thing. :v
	// double maxColDist = 764.8339663572415;
	/**
	 * Measures difference between two colors. <br>
	 * The measurement is done in RGB color space,
	 * so it might not be as accurate.
	 * <p>
	 * The function is taken from here:
	 * <a href="https://stackoverflow.com/a/52562886/9925382">
	 * https://stackoverflow.com/a/52562886/9925382</a>
	 * @param red1 Red part of the first color
	 * @param green1 Green part of the first color
	 * @param blue1 Blue part of the first color
	 * @param red2 Red part of the second color
	 * @param green2 Green part of the second color
	 * @param blue2 Blue part of the second color
	 */
	public static double colorDistance(
			int red1
			,int green1
			,int blue1
			,int red2
			,int green2
			,int blue2)
	{
		if(red1==red2&&green1==green2&&blue1==blue2){
			return 0.0d; // They are identical
		}
		double rmean = ( red1 + red2 )/2;
		int r = red1 - red2;
		int g = green1 - green2;
		int b = blue1 - blue2;
		double weightR = 2 + rmean/256;
		double weightG = 4.0;
		double weightB = 2 + (255-rmean)/256;
		return Math.sqrt(weightR*r*r + weightG*g*g + weightB*b*b);
	}



}
