package team.tilde.architector4.img2bmp;

// This contains a function used to do the full IMG>BMP conversion, and another that
// is a wrapper for the first one except it outputs readable text of what went wrong.

import java.io.File;

public class IOStuff{

	public static void saveBytes(String path,byte[] in) throws java.io.IOException{
			java.io.FileOutputStream stream=new java.io.FileOutputStream(path);
			try{
				stream.write(in);
			}finally{
				stream.close();
			}
	}


	public static byte convertImage(String inPath,String out, boolean overwrite,GUIStuff gui){
		
		File in = new File(inPath);
		
		if(!in.exists()) return 1;
		// Input image doesn't exist
		
		if(in.isDirectory())
			return 2; 
		// Is directory. Look in IMG2BMP.java from line 89 to see how to handle them.
		
		java.awt.image.BufferedImage inImage;
		
		try{
			inImage = javax.imageio.ImageIO.read(in);
		}catch(Exception e){
			System.out.println(e);
			return 3; //Failed reading image
		}

		File outfile = new File(out);
		
		if(outfile.exists())
			if(!overwrite) 
				return 4;
		// File already exists
			else if(!outfile.delete())
				return 5;
		// Failed deleting old file
		
		try{
			try{
				java
				.nio
				.file
				.Files
				.createDirectories(outfile.getParentFile().toPath());
			}catch(Exception e){}
			// Either the folder to the output already exists,
			// or the next catch block is appropriate for that.
			ConvertStuff.toBMP(inImage,out,gui);

		}catch(Exception e){
			return 6;
			//Failed saving image
		}
			
		return 0;
		// All is well.
	}

	public static String convertImageHumanized(
		String inPath,
		String out,
		boolean overwrite,
		boolean commandline,
		GUIStuff gui
	){
		
		
		byte result = convertImage(inPath,out,overwrite,gui);
		switch(result){
			case 0: return null;

			case 1: return inPath+": Input image doesn't exist!";

			case 2: return inPath+": Is a directory!";

			case 3: return inPath+": Failed to load the image! Unsupported format?";

			case 4: return out+": Image already exists!"
			       +(commandline?" Set tag -o to overwrite instead.":"");

			case 5: return inPath+": Failed overwriting old file! No permission?";	

			case 6: return out+": Failed writing image! No space? No permission?";

			default: return "WIBBLY WOBBLY IS MY CODE, SOMETHING BROKE HORRENDOUSLY.";
		}
	}
	

	public static String switchExtension(File in,String newExt){
		String name = in.getName();
		if(name.lastIndexOf('.')!=-1) name = name.substring(0,name.lastIndexOf("."));
		
		return (in.getParent()==null ? "": in.getParent())
		       	+ File.separator + name + "." + newExt;
		
	}
}


