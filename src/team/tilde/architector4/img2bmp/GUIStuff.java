package team.tilde.architector4.img2bmp;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

/**
 * Class containing the graphical user interface of this converter.
 */
public class GUIStuff implements ActionListener{

	/** Contains all input files chosen by the user. */
	File[] input;
	/** Root folder which contains all the input files. */
	File inputRoot;
	/** Button that, when pressed, opens selection
	 * dialog to select files for conversion. */
	JButton selectButton;
	/** Button that, when pressed, opens selection
	 * dialog to select destination of conversion results. */
	JButton saveButton;
	/** Scroll pane used to scroll the text area. */
	JScrollPane textAreaScroll;
	/** Text area containing operation log and other information. */
	volatile JTextArea textArea;
	/** File chooser used to choose files for conversion. */
	JFileChooser fileChooserSelect;
	/** File chooser used to choose destination of conversion results. */
	JFileChooser fileChooserSave;
	/** Frame that contains all the GUI elements. */
	JFrame frame;

	/** True if conversion is happening. */
	volatile boolean busy = false;

	/**
	 * Puts interface elements into a window and then spawns it
	 */
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
		saveButton.setToolTipText("Save the selection as .bmp file(s) somewhere.");
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

	/**
	 * Is triggered when a button is pressed.
	 */
	public void actionPerformed(ActionEvent e){ //A button was pressed!
		if(e.getSource()==selectButton){
			fileChooserSelect.setCurrentDirectory(fileChooserSave.getCurrentDirectory());
			final int returnVal=fileChooserSelect.showDialog(frame,"Select");
			if(returnVal==JFileChooser.APPROVE_OPTION){

				input=fileChooserSelect.getSelectedFiles();
				inputRoot=fileChooserSelect.getCurrentDirectory();

				fileChooserSave.setCurrentDirectory(inputRoot);

				if(input.length==1){
					println("Selected "
							+(input[0].isDirectory()?"folder ":"file ")
							+input[0].getAbsolutePath());
				}else if(input.length!=0){
					//image.setIcon(null);
					//image.setText("Multiple files selected");
					println("Selected:");
					for(File i:input){
						println("  "+i.getAbsolutePath());
					}
				}
				saveButton.setEnabled(true);
			}
		} else if(e.getSource()==saveButton){
			//Basically starts function convertStuff() which is below.
			new Task().execute();
		}
	}

	/**
	 * Class that launches the conversion process on a separate thread.
	 */
	class Task extends SwingWorker<Void,Void>{
		protected Void doInBackground() throws Exception{
			convertStuff();
			return null;
		}
	}
	
	/**
	 * Prints text into the GUI's text field.
	 * @param text Text to be written
	 */
	public void print(String text){
		if(text!=null){
			textArea.append(text);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}

	/**
	 * Replaces the last line in GUI's text field.
	 * @param text Text that the line is replaced with.
	 */
	public void replaceLastLine(String text){
		try{
			textArea.replaceRange(
					text,
					textArea.getLineStartOffset(textArea.getLineCount()-1),
					textArea.getLineEndOffset(textArea.getLineCount()-1)
					);
			
		}catch(BadLocationException e){
			// This shouldn't happen unless the class is modified.
		}
	}

	/**
	 * Prints text into the GUI's text field and adds a newline.
	 * @param text Text to be written
	 */
	public void println(String text){
		if(text!=null) print(text+"\r\n");
	}

	/**
	 * Class that extends {@link FileFilter}
	 * and contains the check that returns <code>true</code>
	 * for any image files.
	 * 
	 * @see FileFilter
	 */
	public static class ImageFilter extends FileFilter{
		//True if the input file is an image or a folder.
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
	/**
	 * Class that extends {@link FileFilter}
	 * and contains the check that returns <code>true</code>
	 * for any directories.
	 * 
	 * @see FileFilter
	 */
	public class DirectoryFilter extends FileFilter{
		//True if the input is a folder
		public boolean accept(File f){
			if(f.isDirectory()) return true;
			return false;
		}
		public String getDescription(){
			return "Folder";
		}
	}
	/**
	 * Class that extends {@link FileFilter}
	 * and contains the check that returns <code>true</code>
	 * for BMP files.
	 * 
	 * @see FileFilter
	 */
	public class BMPFilter extends FileFilter{
		public boolean accept(File f){
			if(getExtension(f).equals("bmp")) return true;
			return false;
		}
		public String getDescription(){
			return "Image (.bmp)";
		}
	}



	/**
	 * Gets the extension of the file - part of its name after the period.
	 * @param f File whose extension is needed.
	 */
	public static String getExtension(File f){
		//Get name, trim it to the last dot, to lowercase.
		//Sorry, I like one-liners lol
		return f.getName().substring(f.getName().lastIndexOf('.')+1).toLowerCase();
	}


	/**
	 * Performs conversion on items stores in variables of this class.
	 */
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
		int returnVal = fileChooserSave.showDialog(frame,multi?"Save to":"Save as");
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
						,true,false,this);

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
								,true,false,this);

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
