package team.tilde.architector4.img2bmp;

// This contains the function that does actual conversion.
// It takes a BufferedImage, and writes the result as a file to the outPath.

import java.text.NumberFormat;

public class ConvertStuff{


	public static void toBMP(
			java.awt.image.BufferedImage in,
			String outPath,
			GUIStuff gui) 
			throws java.io.FileNotFoundException,java.io.IOException {
		// If you are using this elsewhere, you can pass null as "gui" argument instead.
		// It's to give feedback if the conversion takes a long time.
		// Yeah, I know, meh coding practices. Please tell me how to make this better!
		// And if you are actually using this function elsewhere, PLEASE TELL ME I WANT TO KNOW OwO

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
						final int color = 
							in.getRGB(
									bytex(i,width)
									,height-1-bytey(i,width)
								 );
						out.write((byte)bestPaletteColor(color,BMPData.PALETTE));
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

	// This next bit of code grabs the color from the input image and compares it to
	// all colors in the palette until it finds the one that is closest, and assigns that. 
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

		for(int u=1;u<256;u++){ // Iterate through all palette colors

			final double diff = colorDistance(
					unsignbyte (palette[2+u*4])
					,unsignbyte(palette[1+u*4])
					,unsignbyte(palette[0+u*4])
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



	static byte[] toByte(int a){ 
		// Converts an int into 4 bytes packed into an array
		return new byte[]{
			(byte)((a)&0xFF)
				,(byte)((a>>8)&0xFF)
				,(byte)((a>>16)&0xFF)
				,(byte)(a>>24&0xFF)
		};
	}

	static byte[] toByte(short a){ 
		// Converts a short into 2 bytes packed into an array
		return new byte[]{
			(byte)((a)&0xFF)
				,(byte)((a>>8)&0xFF)
		};
	}

	int bytepos(int x,int y,int width){ 
		// Converts input X Y coordinates into a byte number
		return x+y*(widthoffset(width));
	}

	static int bytex(int in,int width){ 
		// Converts a byte number into an X coordinate
		return in%(widthoffset(width));
	}

	static int bytey(int in,int width){ 
		// Converts a byte number into an Y coordinate
		return in/(widthoffset(width));
	}

	static boolean bytegood(int in,int width){ 
		// Can I write in this byte, or it's one of
		// those spacing bytes at the end of each horizontal line?
		return in%(widthoffset(width))<width;
	}

	static int widthoffset(int width){
		//Figures width in bytes of 1 row of pixels.
		return (((width-1)/4)+1)*4;
		//0 1 2 3 4 5 6 7 8 9  10 11 12 13...
		//is
		//4 4 4 4 4 8 8 8 8 12 12 12 12 16...
	}

	static int unsignbyte(byte in){
		//Removes minus from byte.
		return in<0?  0xFF+in  :  in&0x7F;
	}
	static int min(int a,int b){
		//Returns smaller of 2 values.
		return a<b?a:b;
	}
	static int max(int a,int b){
		//Returns bigger of 2 values.
		return a>b?a:b;
	}

	// Next function is yanked from:
	// https://stackoverflow.com/a/52562886/9925382
	// I'm too lazy to do that Lab color space comparison thing. :v
	// double maxColDist = 764.8339663572415;
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
