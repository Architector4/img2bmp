package team.tilde.architector4.img2bmp;

import java.io.File;

/**
 * This contains a function used to do the full file>conversion>file pipeline for a single file.
 * <br>It also contains a function used to switch file extension.
 */
public class IOStuff{

	public static byte convertImage(String inPath,String out, boolean overwrite,GUIStuff gui){
		
		File in = new File(inPath);
		
		if(!in.exists()) return 1;
		// Input image doesn't exist
		
		if(in.isDirectory())
			return 2; 
		// Is a directory. Look in IMG2BMP.java from line 90 to see how to handle them.
		
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

			default: return "The thing returned an unknown result. I screwed up. Let me know.";
		}
	}
	

	public static String switchExtension(File in,String newExt){
		String name = in.getName();
		if(name.lastIndexOf('.')!=-1) name = name.substring(0,name.lastIndexOf("."));
		
		return (in.getParent()==null ? "": in.getParent())
		       	+ File.separator + name + "." + newExt;
		
	}
}
