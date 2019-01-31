package team.tilde.architector4.img2bmp;
import java.io.File;
import java.io.FileOutputStream;

public class IOStuff{

	public static void saveBytes(String path,byte[] in) throws java.io.IOException{
		// String[] path2=ConvertStuff.split(path,'.');
		// path2[path2.length-1]="";
		// if(ConvertStuff.split(path,'.').length>1){path=ConvertStuff.join(path2,".");}
		// path+="bmp";
			FileOutputStream stream=new FileOutputStream(path);
			try{
				stream.write(in);
			}finally{
				stream.close();
			}
	}


	public static byte convertImage(String inPath,String out, boolean overwrite){
		
		File in = new File(inPath);
		
		if(!in.exists()) return 1; //Input image doesn't exist
		
		if(in.isDirectory()){
			return 2; //Is directory - not implemented yet.
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
		
		byte[] bytes;
		try{
			bytes = ConvertStuff.toBMP(ConvertStuff.imgTo2D(inImage));
		}catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			return 5; //Failed converting image
		}
		try{
			if(outfile.exists()&&!outfile.delete())
				return 6; //Failed deleting old file
			
			try{
				java.nio.file.Files.createDirectories(outfile.getParentFile().toPath());
			}catch(Exception e){}
			
			IOStuff.saveBytes(out,bytes);
			return 0; //Success
		}catch(Exception e){
			System.out.println(e);
			return 7; //Failed saving image
		}
	}

	public static String convertImageHumanized(
		String inPath,
		String out,
		boolean overwrite,
		boolean commandline
	){
		
		
		byte result = convertImage(inPath,out,overwrite);
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
			return inPath+": Failed converting image! Out of memory?";	
		else if(result==6)
			return out+": Failed overwriting old file! No permission?";
		else if(result==7)
			return out+": Failed writing image! Do you have access to the output path/file?";

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


