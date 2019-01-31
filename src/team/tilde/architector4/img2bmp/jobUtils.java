package team.tilde.architector4.img2bmp;
import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

public class jobUtils{

	public static String getRelativePath(File of,File to){
		//"of" is for example /home/user/thing.png
		//"to" is for example /home/
		//And it returns user/thing.png
		if(!to.isDirectory()) to = to.getParentFile();

		String ofP = of.getAbsolutePath();
		String toP = to.getAbsolutePath();
		
		if(ofP.length()<toP.length()) return null;
		
		
		if(
			!ofP.substring(0,toP.length())
			.equals(toP)
		) return null;
		
		
		return ofP.substring(toP.length()+1);
	}
	public static File[] getConformingFiles(File in,boolean recursive,FileFilter filter){
		File[] medium = {in};
		return getConformingFiles(medium,recursive,filter);
	}
	
	public static File[] getConformingFiles(File[] in,boolean recursive,FileFilter filter){
			
		ArrayList<File> result = new ArrayList<File>();
		for(File i:in){
			if(i.isDirectory()){
				if(recursive)
					add(result,getConformingFiles(i.listFiles(),true,filter));
			} 
			else if(filter.accept(i)) 
				result.add(i);
		}
		
		return (File[])result.toArray();
	}
	
	static ArrayList<File> add(ArrayList<File> in,File[] thing){
		for(File i:thing)
			in.add(i);
		return in;
	}
	
	public static ArrayList<Job> convertFolder(File anchor,File inPath,File out,FileFilter match){
			final String outPath = 
			out.getAbsolutePath()
			+File.separator;
			//+anchor.getName()
			//+File.separator;
			
			//Files.createDirectories(Paths.get(outPath));
			
			if(!inPath.isDirectory()){
				if(!match.accept(inPath)) return null;
				ArrayList<Job> outJob = new ArrayList<Job>();
				outJob.add(
						new Job(
								inPath
								,new File(outPath+jobUtils.getRelativePath(inPath,anchor)))
						);
				return outJob;
			}
			
			final File[] files = inPath.listFiles();
			ArrayList<Job> result = new ArrayList<Job>();
			for(File i:files){
				ArrayList<Job> thisResult = convertFolder(anchor,i,out,match);
				if(thisResult!=null) for(Job u:thisResult) result.add(u);
			}
			return result;
		
		
	}
	

	
	
}

class Job{
	final File inFile;
	final File outFile;
	Job(File in, File out){
		inFile = in;
		outFile = out;
	}
	public String toString(){
		return inFile.getAbsolutePath()+": "+outFile.getAbsolutePath();
	}
}
