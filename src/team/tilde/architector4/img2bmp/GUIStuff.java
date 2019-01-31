package team.tilde.architector4.img2bmp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class GUIStuff extends JPanel implements ActionListener{

	private static final long serialVersionUID=674585998342284178L;
	//I'll be honest, I don't know what this serial thing is, but, eh. 
	//One Eclipse warning more, one Eclipse warning less. lol
	//...Also, shouldn't this be named SERIAL_VERSION_UID, considering it's final static whatever?

	File[] input;
	File inputRoot;
	float process;
	JButton selectButton;
	JButton saveButton;
	JScrollPane textAreaScroll;
	volatile JTextArea textArea;
	JFileChooser fileChooserSelect;
	JFileChooser fileChooserSave;

	JFrame frame;
	
	volatile boolean busy = false;


	public void initGUI(){
		// frame.getContentPane().add(startButton, BorderLayout.LINE_END);
		// GUIStuff pane = new GUIStuff();

		selectButton=new JButton("Select image");
		selectButton.setToolTipText("Select images or folders to be converted into .bmp files.");
		//selectButton.setActionCommand("selectButton");
		selectButton.setMargin(new Insets(8,0,8,0));
		selectButton.addActionListener(this);

		saveButton=new JButton("Save as BMP");
		saveButton.setEnabled(false);
		saveButton.setToolTipText("Save the selection as .bmp files somewhere.");
		//saveButton.setActionCommand("saveButton");
		saveButton.setMargin(new Insets(8,0,8,0));
		saveButton.addActionListener(this);

		JPanel pane=new JPanel();
		pane.setLayout(new GridBagLayout());

		GridBagConstraints c=new GridBagConstraints();
		c.weightx=0.5;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridx=c.gridy=0;
		pane.add(selectButton,c);
		c.gridx=1;
		pane.add(saveButton,c);

		//		c.fill= GridBagConstraints.SOUTH; 
		//		c.ipady = 100;
		c.gridwidth=2;
		c.gridx=0;
		c.gridy=1;
		// pane.add(image,c);


		//image=new JLabel("");
		//image.setEnabled(false);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setMargin(new Insets(5,5,5,5));

		textAreaScroll = new JScrollPane(textArea);
		//textAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		print(IMG2BMP.credit);
		println(IMG2BMP.purpose);
		println("WARNING: This overwrites files in the output!");
		println("Make sure to export into a new folder!");


		frame = new JFrame("IMG2BMP");

		frame.add(pane,BorderLayout.NORTH);

		//frame.add(image,BorderLayout.CENTER);
		frame.add(textAreaScroll,BorderLayout.CENTER);

		// frame.pack();
		frame.setSize(500,250);
		frame.setMinimumSize(new Dimension(300,100));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);




		fileChooserSelect=new JFileChooser();
		fileChooserSelect.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooserSelect.setMultiSelectionEnabled(true);
		fileChooserSelect.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooserSelect.setFileFilter(new ImageFilter());
		fileChooserSelect.setAcceptAllFileFilterUsed(false);

		fileChooserSave=new JFileChooser();
		fileChooserSave.setCurrentDirectory(new File(System.getProperty("user.dir")));
		fileChooserSave.setFileFilter(new BMPFilter());


	}

	public void actionPerformed(ActionEvent e){
		if(e.getSource()==selectButton){
			fileChooserSelect.setCurrentDirectory(fileChooserSave.getCurrentDirectory());
			final int returnVal=fileChooserSelect.showDialog(this,"Select");
			if(returnVal==JFileChooser.APPROVE_OPTION){

				input=fileChooserSelect.getSelectedFiles(); //USER CAN CHOOSE NONEXISTENT FILES O_O
				inputRoot=fileChooserSelect.getCurrentDirectory();

				fileChooserSave.setCurrentDirectory(inputRoot);

				if(input.length==1){
					try{
						println("Selected "
										+(input[0].isDirectory()?"folder ":"file ")
										+input[0].getAbsolutePath());


						saveButton.setEnabled(true);

						//System.out.println(input[0].getAbsolutePath());
						//Hmm, ImageIO understands more image formats than ImageIcon...
						//image.setIcon(new ImageIcon(ImageIO.read(input[0])));
						//image.setText("");
					}catch(Exception e1){
						//image.setIcon(null);
						//image.setText("Failed loading image "+input[0].getAbsolutePath());
						println("We had an error thinking about that 1 file: "+e1.toString());
					}
				}else if(input.length!=0){
					//image.setIcon(null);
					//image.setText("Multiple files selected");
					println("Selected:");
					for(File i:input){
						println("  "+i.getAbsolutePath());
						saveButton.setEnabled(true);
					}
				}
			}
			//try{ //I forgot where that is from but that looks scary.
			//// javax.swing.SwingUtilities.invokeLater(new Runnable() {
			//// 	
			//// });
			//// (new Thread(new JFrameTest())).start();
			//	new Task().execute();
			//}catch(Exception e2){
			//}
		} else if(e.getSource()==saveButton){
			new Task().execute();
		}
	}

	class Task extends SwingWorker<Void,Void>{

		@Override
		protected Void doInBackground() throws Exception{
			//bun();
			convertStuff();

			return null;
		}
	}

	public void bun(){
		try{

			selectButton.setText("OW");
			Thread.sleep(2000);
			selectButton.setText("oww...");

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void print(String text){
		if(text!=null){
			textArea.append(text);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}
	public void println(String text){
		if(text!=null) print(text+"\r\n");
	}


	public class ImageFilter extends FileFilter{
		//True if the input file exists, an image or a folder.
		public boolean accept(File f){
			if(f.isDirectory()) return true;

			String extension = getExtension(f);           
			if(extension.equals("tiff"	)) 	return true;  
			if(extension.equals("tif"	)) 	return true; 
			if(extension.equals("gif"	)) 	return true; 
			if(extension.equals("jpeg"	)) 	return true;  
			if(extension.equals("jpg"	)) 	return true;  
			if(extension.equals("png"	)) 	return true;
			if(extension.equals("bmp"	)) 	return true; 
			return false;
		}
		
		public String getDescription(){
			return "Images (.bmp, .png, .jpg, .jpeg, .gif, .tif, .tiff)";
		}
	}

	public class DirectoryFilter extends FileFilter{
		public boolean accept(File f){
			if(f.isDirectory()) return true;
			return false;
		}
		public String getDescription(){
			return "Folder";
		}
	}
	public class BMPFilter extends FileFilter{
		public boolean accept(File f){
			if(getExtension(f).equals("bmp")) return true;
			return false;
		}
		public String getDescription(){
			return "Image (.bmp)";
		}
	}



	public static String getExtension(File f){
		//Get name, trim it to the last dot, to lowercase.
		//Sorry, I like one-liners lol
		return f.getName().substring(f.getName().lastIndexOf('.')+1).toLowerCase();
	}


	// private String spookystring(int len){
	// String out="";
	// for(int i=0;i<len;i++){
	// out+=Character.toString((char)new Random().nextInt(500));
	// }
	// return out;
	// }

	public void convertStuff(){

		selectButton.setEnabled(false);	
		saveButton.setEnabled(false);


		fileChooserSave.removeChoosableFileFilter(new BMPFilter());
		fileChooserSave.removeChoosableFileFilter(new DirectoryFilter());

		fileChooserSave.setCurrentDirectory(fileChooserSelect.getCurrentDirectory());
		boolean multi = false;
		if(input.length>1||input[0].isDirectory()) multi = true;
		fileChooserSave.setFileFilter(multi?new DirectoryFilter():new BMPFilter());
		fileChooserSave.setFileSelectionMode(
				multi?JFileChooser.DIRECTORIES_ONLY:JFileChooser.FILES_ONLY);
		int returnVal = fileChooserSave.showDialog(this,multi?"Save to":"Save as");
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File out = fileChooserSave.getSelectedFile();
			fileChooserSelect.setCurrentDirectory(fileChooserSave.getCurrentDirectory());

			//try{
			if(!out.isDirectory()&&!multi){
				//Single file save...
				println("Converting "+input[0].getAbsolutePath()+"...");
				String result = IOStuff.convertImageHumanized(
						input[0].getAbsolutePath()
						,IOStuff.switchExtension(out,"bmp")
						,true,false);
				
				println(result!=null?result:"Done!");
			}else{
				//Multi file save...
				for(File i:input){
					java.util.ArrayList<Job> jobs = jobUtils.convertFolder(
							inputRoot
							,i
							,out
							,(FileFilter)new ImageFilter());
					
					for(Job u:jobs){
						println("Converting "+u.inFile.getAbsolutePath()+"...");
						String result = IOStuff.convertImageHumanized(
								u.inFile.getAbsolutePath()
								,IOStuff.switchExtension(u.outFile,"bmp")
								,true,false);
						
						println(result);
					}
				}
				println("Done!");
			}
			//}catch(IOException e1){
			//	println("Failed doing a thing! Permission error? "+e1);
			//}
		}
		selectButton.setEnabled(true);	
		saveButton.setEnabled(true);
	}

}
