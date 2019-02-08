package team.tilde.architector4.img2bmp;

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
		
		if(!in.exists()) return 1; //Input image doesn't exist
		
		if(in.isDirectory()){
			return 2; //Is directory. Look in IMG2BMP.java from line 89 to see how to handle them.
		}
		
		
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
				return 4; //File already exists

		if(outfile.exists()&&!outfile.delete())
			return 5; //Failed deleting old file
		
		try{
			try{
				java.nio.file.Files.createDirectories(outfile.getParentFile().toPath());
			}catch(Exception e){} // Either it already exists, or the next catch's gonna handle that
			ConvertStuff.toBMP(inImage,out,gui);
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			return 6; //Failed saving image
		}
			
		return 0;
	}

	public static String convertImageHumanized(
		String inPath,
		String out,
		boolean overwrite,
		boolean commandline,
		GUIStuff gui
	){
		
		
		byte result = convertImage(inPath,out,overwrite,gui);
			 if(result==1)
			return inPath+": Input image doesn't exist!";
		else if(result==2)
			return inPath+": Is a directory!";
		else if(result==3)
			return inPath+": Failed to load the image! Unsupported format?";
		else if(result==4)
			return out+": Image already exists!"
			+(commandline?" Set tag -o to overwrite instead.":"");
		else if(result==5)
			return inPath+": Failed overwriting old file! No permission?";	
		else if(result==6)
			return out+": Failed writing image! No space? No permission?";

			return null;
	}
	

	public static String switchExtension(File in,String newExt){
		String name = in.getName();
		if(name.lastIndexOf('.')!=-1) name = name.substring(0,name.lastIndexOf("."));
		
		return (in.getParent()==null ? "": in.getParent()) + File.separator + name + "." + newExt;
		
	}
	
	
	//static byte convertFolder(String inPath,String out,GUIStuff printTo){
	//	printTo.println("xd");
	//	return -1;}
}


