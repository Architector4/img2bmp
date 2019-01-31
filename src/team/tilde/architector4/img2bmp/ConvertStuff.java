package team.tilde.architector4.img2bmp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ConvertStuff{
	
	public static short[][][] imgTo2D(BufferedImage image){
		// Adapted from https://stackoverflow.com/a/9470843/9925382
		//ACTUALLY SCREW THAT APADTION, THE FREAKING BYTE ARRAY
		//GAVE ME 1 BYTE PER PIXEL AND PULLING USING GETRGB
		//TURNED OUT TO BE MORE EFFICIENT ANYWAYS AND NOT BUGGY
		//SO UH YEAH, CREDIT WHERE CREDIT'S DUE, BUT JEEZ
		// ** end of rant **

		
		//final byte[] pixels=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		
		final int width=image.getWidth();
		final int height=image.getHeight();

		//final boolean hasAlphaChannel=image.getAlphaRaster()!=null;

		short[][][] result=new short[height][width][3];
		//final int pixelLength=hasAlphaChannel?4:1;
		//final int pixelLength=pixels.length/width/height;
		//for(int pixel=0,row=0,col=0;pixel<pixels.length-2;pixel+=pixelLength){
		for(int pixel=0,row=0,col=0;pixel<width*height;pixel++){
			// int argb = 0;
			// argb += -16777216; // 255 alpha
			// argb += ((int) pixels[pixel] & 0xff); // blue
			// argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
			// argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
			// result[row][col] = argb;
			//try{
			//result[row][col][0]=unsignbyte(pixels[pixel+min(2,pixelLength-1)]);//&0xff);
			//result[row][col][1]=unsignbyte(pixels[pixel+min(1,pixelLength-1)]);//&0xff);
			//result[row][col][2]=unsignbyte(pixels[pixel]);//&0xff);
			final int color = image.getRGB(col,row);
			result[row][col][0] = (short)((color>>16)&0xFF);
			result[row][col][1] = (short)((color>>8)&0xFF);
			result[row][col][2] = (short)((color)&0xFF);
			//}catch(Exception e){System.out.println("fuck "+pixels.length+" "+pixel);}
			col++;
			if(col==width){
				col=0;
				row++;
			}
		}
		
		return result;
	}

	public static byte[] toBMP(short[][][] in) throws Exception{
		// This is based on this:
		// http://www.dragonwins.com/domains/getteched/bmp/bmpfileformat.htm
		
		
		ByteArrayOutputStream out=new ByteArrayOutputStream();
			out.write(66); 					// 0 "B" in "BM" header.
			out.write(77); 					// 1 "M" in "BM" header.
			out.write(toByte(0)); 			// 2 File size. We are going to fill this later.
			out.write(toByte(0)); 			// 6 Reserved.
			out.write(toByte(1024+54)); 	// 10 Pixel data start offset.
			out.write(toByte(40)); 			// 14 Header size.
			out.write(toByte(in[0].length));// 18 Width.
			out.write(toByte(in.length)); 	// 22 Height.
			out.write(toByte((short)1)); 	// 26 Amount of images in this image. ._.
			out.write(toByte((short)8)); 	// 28 Bits per pixel. This does 8, so let's do 8.
			out.write(toByte(0)); 			// 30 Compression type. I don't like compression.
			out.write(toByte(0)); 			// 34 Image size. Matters only if compressed, so meh.
			out.write(toByte(0)); 			// 38 Preferred X printing resolution.
			out.write(toByte(0)); 			// 42 Preferred Y printing resolution.
			out.write(toByte(0)); 			// 46 Number of used Color Map entries.
			out.write(toByte(0)); 			// 50 Number of significant Color Map entries.
			out.write(Palette.PALETTE); 	// 54-1024 Palette.

											//1078+ Image data
			for(int i=0;i<(in[0].length+4)*in.length;i++){
				//+4 because the format uses 4 padding bytes after each row of pixels
				short[] pixel;
				try{
					pixel=in
							[(in.length-1-bity(i,in[0].length))]
							[bitx(i,in[0].length)]; // Grab matching image pixel
				}catch(Exception e){
					pixel=new short[]{0,0,0};
				}
				//if(i<1700) System.out.println(pixel[0]+" "+pixel[1]+" "+pixel[2]);
				// This next bit of code grabs the color from the input image and compares it to
				// all colors in the palette until it finds the one that is closest, and assigns
				// that.
				
				if(bitgood(i,in[0].length)){ 

					int best=0; // Best color for the pixel
					float diffbest=-1; //How good best color matches
					for(int u=0;u<255;u++){ // Iterate through all palette colors
						int diff= //How good current color matches
						Math.abs(unsignbyte(Palette.PALETTE[2+u*4])-pixel[0])
						+Math.abs(unsignbyte(Palette.PALETTE[1+u*4])-pixel[1])
						+Math.abs(unsignbyte(Palette.PALETTE[0+u*4])-pixel[2]);
						if(diff==0){ //0 means perfect - screw other colors.
							break;
						}
						if(diff<diffbest||diffbest==-1){ // If this palette color's better
							diffbest=diff;
							best=u;
						}
					}
					out.write((byte)best);
				}else{
					// If it's transparent just use first color in the palette
					// (which is R G B 255 0 255 in the included palette).
					// Or if it's a spacing byte then also don't bother.
					out.write(0);
				}
			}


		byte[] output=out.toByteArray();
		int length=out.size();
		out=null;

		output[2]=toByte(length-54)[0]; // 2 File size.
		output[3]=toByte(length-54)[1]; // 3 File size.
		output[4]=toByte(length-54)[2]; // 4 File size.
		output[5]=toByte(length-54)[3]; // 5 File size.
		// System.out.println(length-54);

		return output;
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

	int bitpos(int x,int y,int width){ 
		// Converts input X Y coordinates into a byte number
		return x+y*(bitoffset(width));
	}

	static int bitx(int in,int width){ 
		// Converts a byte number into an X coordinate
		return in%(bitoffset(width));
	}

	static int bity(int in,int width){ 
		// Converts a byte number into an Y coordinate
		return in/(bitoffset(width));
	}

	static boolean bitgood(int in,int width){ 
		// Can I write in this byte, or it's one of
		// those 4 spacing bytes at the end of each horizontal line?
		return in%(bitoffset(width))<width;
	}

	static int bitoffset(int width){ 
		// To account for these 4 additional 
		// spacing bytes at the end of each horizontal line.
		return (((width-1)/4)+1)*4;
		//0 1 2 3 4 5 6 7 8 9  10 11 12 13...
		//is
		//4 4 4 4 4 8 8 8 8 12 12 12 12 16...
	}

	static short unsignbyte(byte in){
		return (short)(in&0x7F+(in<0?128:0));
	}
	static int min(int a,int b){
		return a<b?a:b;
	}
	static int max(int a,int b){
		return a>b?a:b;
	}
}
