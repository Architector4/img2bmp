package team.tilde.architector4.img2bmp;

import java.io.File;

public class IMG2BMP{

	static final String credit =
			"IMG2BMP Converter\r\n"
					+"By Architector #4, 2019\r\n";
	static final String purpose =
			"Converts images to BMP format with a specific palette.\r\n"
					+"Made as a tool to make valid sprites for \"Cortex Command\" videogame.\r\n";

	static final String helpMessage=
			credit
			+purpose
			+"Usage: img2bmp [-ho] input [output]\r\n"
			+"Converts input image to an 8-bit BMP file with custom palette\r\n"
			+"If no output is specified, the image is saved with the input's name, "
			+ "but with .bmp extension.\r\n\r\n"
			+"Tags:\r\n"+"-h: Print this help and exit\r\n"
			+"-o: Overwrite output if exists\r\n";

	public static final void main(String[] args){
		// String workdir = System.getProperty("user.dir");

		// System.out.println("Current working directory is "+workdir);

		String input="";
		String output="";
		boolean outputAuto=true;
		boolean outputOverwrite=false;
		// for(int i=0;i<args.length;i++){
		// System.out.println(args[i]);
		// }
		if(args.length>0){
			try{
				int argsoffset=0;
				boolean argsoffseted=false;
				while(!argsoffseted){
					if(args[argsoffset].charAt(0)=='-'){

						if(args[argsoffset].charAt(1)=='-'){
							if(args[argsoffset].equals("--help")){
								System.out.println(helpMessage);
							}
						}else{
							java.util.stream.IntStream chars=args[argsoffset].substring(1).chars();
							for(int i:chars.toArray()){

								if((char)i=='h'){
									System.out.println(helpMessage);
									return;
								}else if((char)i=='o'){
									outputOverwrite=true;
								}else{
									System.err.println("Unknown argument "+(char)i);
								}
							}
							argsoffset++;
						}
					}else{
						argsoffseted=true;
					}
				}
				input=args[argsoffset+0];
				if(args.length>argsoffset+1){
					output=args[argsoffset+1];
					outputAuto=false;
				}
			}catch(Exception e){
				System.err.println("Failed parsing arguments! See -h or --help for syntax.");
				return;
			}
		}else{
			System.err.println("No arguments - opening GUI...\r\n"
					+ "For command-line operation, use -h or --help tag.");
			new GUIStuff().initGUI();
			return;
		}

		if(outputAuto){
			output=IOStuff.switchExtension(new File(input),"bmp");
		}

		if(new File(input).isDirectory()){
			java.util.ArrayList<Job> jobs = jobUtils.convertFolder(
					new File(input)
					,new File(input)
					,new File(output)
					,(javax.swing.filechooser.FileFilter)new GUIStuff.ImageFilter());

			for(Job u:jobs){
				//				System.out.println("Converting "+u.inFile.getAbsolutePath()+"...");
				String result = IOStuff.convertImageHumanized(
						u.inFile.getAbsolutePath()
						,IOStuff.switchExtension(u.outFile,"bmp")
						,outputOverwrite,true,null);

				if(result!=null)
					System.err.println(result);
			}
			//			System.out.println("Done!");

		}else{
			final String result=IOStuff.convertImageHumanized(
					input,
					output,
					outputOverwrite,
					true,null);
			if(result!=null)
				System.err.println(result);
		}
		return;
	}
}








