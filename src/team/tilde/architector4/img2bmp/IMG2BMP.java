package team.tilde.architector4.img2bmp;

// This contains the main function that is run when you launch the application.

import java.io.File;

public class IMG2BMP{

	static final String credit =
		"IMG2BMP Converter\r\n"
		+"By Architector #4, 2019\r\n";
	static final String purpose =
		"Converts images to BMP format with a specific palette.\r\n"
		+"Made as a tool to make valid sprites for \"Cortex Command\" videogame.\r\n";

	static final String usage=
		"Usage: img2bmp [-ho] input [output]\r\n"
		+"Converts input image to an 8-bit BMP file with custom palette\r\n"
		+"If no output is specified, the image is saved with the input's name, "
		+"but with .bmp extension.\r\n\r\n"
		+"Tags:\r\n"
		+"-h: Print this help and exit\r\n"
		+"-o: Overwrite output if exists\r\n";

	static final String helpMessage=
		credit
		+purpose
		+usage;
	public static final void main(String[] args){

		String input=null;
		String output=null;
		boolean outputOverwrite=false;
		
		if(args.length>0){
			for(String i:args){
				if(i.charAt(0)=='-'){
					if(i.charAt(1)=='-'){
						if(i.equals("--help")){
							System.out.println(helpMessage);
							return;
						}
					}else{
						for(int u=1;u<i.length();u++){
							switch(i.charAt(u)){
								case 'h':
									System.out.println(helpMessage);
									return;
								case 'o':
									outputOverwrite=true;
									break;
								default:
									System.err.println(
											i.charAt(u)
											+": Unknown argument.");
							}
						}
					}
				}else{
					if(input==null){
						input=i;
						continue;
					}
					if(output==null){
						output=i;
						continue;
					}
					System.err.println("Too many arguments supplied!");
					return;
				}
			}
		}
		if(input==null){
			System.err.println("No input specified - opening GUI...\r\n"
					+ "For command-line operation, use -h or --help tag.");
			try{
				new GUIStuff().initGUI();
			}catch(java.awt.HeadlessException e){
				System.err.println("Actually, not opening GUI. Headless or whatever detected.");
			}
			return;
		}

		if(output==null){
			output=IOStuff.switchExtension(new File(input),"bmp");
		}
		

		// Handling a directory...
		if(new File(input).isDirectory()){
			java.util.ArrayList<Job> jobs = jobUtils.convertFolder(
					new File(input)
					,new File(input)
					,new File(output)
					,(javax.swing.filechooser.FileFilter)new GUIStuff.ImageFilter());

			for(Job u:jobs){
				String result = IOStuff.convertImageHumanized(
						u.inFile.getAbsolutePath()
						,IOStuff.switchExtension(u.outFile,"bmp")
						,outputOverwrite,true,null);

				if(result!=null)
					System.err.println(result);
			}

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
