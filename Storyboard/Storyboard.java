package Storyboard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;





import java.util.Properties;

import Commands.Fade;
import effects.Effects;
import Objects.Layer;
import Objects.VisualObject;
import Utils.OsuUtils;
import Utils.Sample;

// Size = 640x480
public class Storyboard {
	private final static String nl = OsuUtils.nl;
	private static final String propertyName = "StoryboardConfig.properties";
	private File osuFile;
	private File outputFile; // OSB file or OSU file
	private String defaultPath = OsuUtils.defaultOsuPath;
	private ArrayList<VisualObject> background = new ArrayList<>();
	private ArrayList<VisualObject> SB_background = new ArrayList<>();
	private ArrayList<VisualObject> SB_fail = new ArrayList<>();
	private ArrayList<VisualObject> SB_pass = new ArrayList<>();
	private ArrayList<VisualObject> SB_foreground = new ArrayList<>();
	private ArrayList<Sample> SB_Samples = new ArrayList<>();

	public Storyboard(boolean isDifficultySpecific){
		readFromProperty(OsuUtils.startPath);
		osuFile = OsuUtils.getOsuFile(defaultPath);
		writeToProperty(OsuUtils.startPath);
		//System.out.println(osuFile);
		if (isDifficultySpecific==false){
			outputFile = getSBFile();
		} else{
			outputFile = osuFile;
		}
		//System.out.println(outputFile);
	}
	
	public Storyboard(boolean isDifficultySpecific, File file){
		readFromProperty(OsuUtils.startPath);
		osuFile = file;
		writeToProperty(OsuUtils.startPath);
		//System.out.println(osuFile);
		if (isDifficultySpecific==false){
			outputFile = getSBFile();
		} else{
			outputFile = osuFile;
		}
		//System.out.println(outputFile);
	}
	
	private void readFromProperty(String path) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			String propertyPath = path + "\\" + propertyName;
			File f = new File(propertyPath);
			f.createNewFile();
			if (f.exists() && f.isFile()){
				input = new FileInputStream(propertyPath);
				prop.load(input);
				if (prop.getProperty("Path")!=null){
					defaultPath = prop.getProperty("Path");
				}
				input.close();
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void disableBG(VisualObject bg){
		bg.setLayer(Layer.Background);
		bg.add(new Fade(0,0,0,0));
		add(bg);
	}
	
private void writeToProperty(String path) {
	Properties prop = new Properties();
	OutputStream output = null;
	try {
		String propertyPath = path + "\\" + propertyName;
		File f = new File(propertyPath);
		if (!f.exists()){
			f.createNewFile();
		}
		FileInputStream input = new FileInputStream(propertyPath);
		prop.load(input);
		prop.setProperty("Path",osuFile.getParent());
		input.close();
		// save properties to project root folder
		output = new FileOutputStream(propertyPath);
		prop.store(output, null);
		output.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
	
	public void add(VisualObject o){
		if (o.getLayer() == Layer.Video){
			background.add(o.Clone());
		} else if (o.getLayer() == Layer.Background){
			SB_background.add(o.Clone());
		} else if (o.getLayer() == Layer.Foreground){
			SB_foreground.add(o.Clone());
		} else if (o.getLayer() == Layer.Fail){
			SB_fail.add(o.Clone());
		} else if (o.getLayer() == Layer.Pass){
			SB_pass.add(o.Clone());
		}
		
	}

	public void add(Effects e){
		ArrayList<VisualObject> objects = e.getObjects();
		for (VisualObject o : objects){
			if (o.getLayer() == Layer.Video){
				background.add(o.Clone());
			} else if (o.getLayer() == Layer.Background){
				SB_background.add(o.Clone());
			} else if (o.getLayer() == Layer.Foreground){
				SB_foreground.add(o.Clone());
			} else if (o.getLayer() == Layer.Fail){
				SB_fail.add(o.Clone());
			} else if (o.getLayer() == Layer.Pass){
				SB_pass.add(o.Clone());
			}
		}
		for (Sample s : e.getSamples()){
			SB_Samples.add(s.clone());
		}
	}
	
	public void remove(VisualObject o){
		if (o.getLayer() == Layer.Video){
			background.remove(o);
		} else if (o.getLayer() == Layer.Background){
			SB_background.remove(o);
		} else if (o.getLayer() == Layer.Foreground){
			SB_foreground.remove(o);
		} else if (o.getLayer() == Layer.Fail){
			SB_fail.add(o.Clone());
		} else if (o.getLayer() == Layer.Pass){
			SB_pass.add(o.Clone());
		}
	}
	
	public void remove(Effects e){
		ArrayList<VisualObject> objects = e.getObjects();
		for (VisualObject o : objects){
			if (o.getLayer() == Layer.Video){
				background.remove(o);
			} else if (o.getLayer() == Layer.Background){
				SB_background.remove(o);
			} else if (o.getLayer() == Layer.Foreground){
				SB_foreground.remove(o);
			} else if (o.getLayer() == Layer.Fail){
				SB_fail.add(o.Clone());
			} else if (o.getLayer() == Layer.Pass){
				SB_pass.add(o.Clone());
			}
			
		}
	}
	
	public void sortAllEffects(){
		for (VisualObject o : background){
			o.sortByStartTime();
		}
		for (VisualObject o : SB_background){
			o.sortByStartTime();
		}
		for (VisualObject o : SB_foreground){
			o.sortByStartTime();
		}
		for (VisualObject o : SB_fail){
			o.sortByStartTime();
		}
		for (VisualObject o : SB_pass){
			o.sortByStartTime();
		}
	}
	
	private File getSBFile(){
		File output;
		String osuName = osuFile.getName();
		String osb = osuName.substring(0, osuName.indexOf(" [")) + ".osb";
		output = new File(osuFile.getParent() + "\\" + osb);
		return output;
	}
	
	public String toString(){
		String text = "[Events]" +nl;
		text+= "//Background and Video events" + nl;
		text += getLayerInfo(background);
		text+= "//Storyboard Layer 0 (Background)" + nl;
		text += getLayerInfo(SB_background);
		text+= "//Storyboard Layer 1 (Fail)" + nl;
		text += getLayerInfo(SB_fail);
		text+= "//Storyboard Layer 2 (Pass)" + nl;
		text += getLayerInfo(SB_pass);
		text+= "//Storyboard Layer 3 (Foreground)" + nl;
		text += getLayerInfo(SB_foreground);
		text+= "//Storyboard Sound Samples" + nl;
		text += OsuUtils.convertALtoString(SB_Samples);
		return text;
	}
	
	private String getLayerInfo(ArrayList<VisualObject> layer){
		String output = "";
		if (layer.isEmpty()){
			return output;
		}
		for (VisualObject o : layer){
			output += o.toString() + nl;
		}
		return output;
	}
	
	public void exportSB(){
		try {
			outputFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OsuUtils.exportBeatmap(outputFile, toString());
	}
	
	public void exportSB(String subFolderName){
		File output = null;
		try {
			String osuName = osuFile.getName();
			String osb = osuName.substring(0, osuName.indexOf(" [")) + ".osb";
			new File(subFolderName).mkdir();
			output = new File(subFolderName + "\\" + osb);
			output.createNewFile();
		} catch (IOException e) {
			System.out.println(output.getAbsolutePath());
			e.printStackTrace();
			System.exit(-1);
		}
		OsuUtils.exportBeatmap(output, toString());
	}

	public File getOsuFile() {
		return osuFile;
	}

	public void setOsuFile(File osuFile) {
		this.osuFile = osuFile;
	}
	
}
