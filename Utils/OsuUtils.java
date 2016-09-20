package Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class OsuUtils {
	
	//Constants
	public final static String nl = System.getProperty("line.separator");
	public final static String format = "%.4f";
	private static int SUPPORTED_OSU_FILE_VERSION=14;
	private static int SUPPORTED_PLAY_MODE = 3; // Osu!mania only
	public final static String defaultOsuPath = "C:/Program Files (x86)/osu!/Songs";
	
	public static String convertALtoString(ArrayList<?> al){
		String output="";
		for (Object o : al){
			output+=o.toString() + nl;
		}
		return output;
	}
	
	public static String characterToUnicode(char ch){
		return Integer.toHexString(ch | 0x10000).substring(1);
	}
	
	public static int characterToUnicodeInt(char ch){
		String hex = characterToUnicode(ch);
		return Integer.decode("0x"+hex);
	}
	
	public static int storyboardXToHitObjectX(int x){
		return x - 63;
	}
	
	public static int hitObjectXToStoryboardX(int x){
		return x + 63;
	}
	
	public static int storyboardYToHitObjectY(int y){
		return y - 71;
	}
	
	public static int hitObjectYToStoryboardY(int y){
		return y + 71;
	}
	
	public static double degreesToRadian(double degrees){
		return (degrees/180.0) * Math.PI; 
	}
	
	public static boolean isCharacterJapanese(char ch) {
		if (characterToUnicodeInt(ch)<=9835){
			return false;
		}
		
	    return true;
	}
	
	public static ArrayList<Long> getDistinctStartTime(ArrayList<HitObject> hitObjects, ArrayList<HitObject> hitObjects2 ){
		ArrayList<Long> output = new ArrayList<Long>();
		long t = -1;
		for (HitObject ho : hitObjects){
			long startTime = ho.getStartTime();
			if (startTime != t){
				t = startTime;
				output.add(t);
			}
		}
		t = -1;
		for (HitObject ho : hitObjects2){
			long startTime = ho.getStartTime();
			if (startTime != t){
				t = startTime;
				if (!output.contains(t)){
					output.add(t);
				}

			}
		}
		return output;
	}
	
	
	public static ArrayList<Sample> getSamples(File f) throws Exception{
		ArrayList<Sample> output = new ArrayList<Sample>();               
		if (f == null || !(f.exists())){
			// error reading file
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
		    String line;
		    while ((line = br.readLine()) != null) {      
		       // read line by line
		    	if (line.contains("Sample,")){
	    			String[] parts = line.split(",");
					long startTime = Long.parseLong(parts[1]);
					String hs = parts[3];
					int vol = Integer.parseInt(parts[4]);
					Sample s = new Sample(startTime,hs,vol);
					output.add(s);
		    }
		}
		}
		return output;                 
	}
	
	public static int getMode(File f) throws Exception{
		int mode = -1;
		System.out.println(f.getAbsolutePath());
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
		    String line;
		    while ((line = br.readLine()) != null) {      
		       // read line by line
		    	if (line.contains("Mode: ")){
					mode = Integer.parseInt(line.substring(6));		
		    	}	
		    }
		} catch (Exception e){
			e.printStackTrace();
		}
		return mode;
	}
	
	public static String[] getAllInfo(File f) throws Exception{
		String[] output = new String[4];
		String generalInfo="";
		String sampleInfo="";
		String timingInfo="";
		String hitObjectsInfo="";
		if (f == null || !(f.exists())){
			// error reading file
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
		    String line;
		    int sectionID =0;
		    while ((line = br.readLine()) != null) {      
		       // read line by line
		    	switch(sectionID){
				case 0:
					// General stuff
					
					if (line.contains("Storyboard Sound Samples")){
						sectionID=1;
						sampleInfo += line + nl;
					} else if (line.contains("osu file format v")){
						int version = Integer.parseInt(line.substring(17));
						if (version!=SUPPORTED_OSU_FILE_VERSION){
							String errMsg = "The currently supported osu file version is "+SUPPORTED_OSU_FILE_VERSION +nl;
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);
						}
					}
					else if (line.contains("Mode :")){
						int mode = Integer.parseInt(line.substring(6));
						if (mode!=SUPPORTED_PLAY_MODE){
							String errMsg = "The currently supported mode is mania";
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);            
						}
					} 
					generalInfo += line + nl;
					break;
				case 1:
					// Samples
					if (line.equals("[TimingPoints]")){
						sectionID=2;
						timingInfo += line + nl;
					} else{
						sampleInfo += line + nl;
					} 
					break;
					
				case 2: 
					// timing points
					if (line.contains("[HitObjects]")){
						hitObjectsInfo += line + nl;
						sectionID=3;
					}
					timingInfo += line +nl;
					break;
					
				case 3:
					// Hit Objects
					hitObjectsInfo += line + nl;
					break;
				}
		    }
		}
		output[0]=generalInfo;
		output[1]=sampleInfo;
		output[2]=timingInfo;
		output[3]=hitObjectsInfo;
		return output;
	}
	
	
	public static File getOsuFile(){
		File f = null;
		FileFilter filter = new FileNameExtensionFilter("OSU file", "osu");
 	   	final JFileChooser jFileChooser1 = new javax.swing.JFileChooser(defaultOsuPath);
        jFileChooser1.addChoosableFileFilter(filter);
        jFileChooser1.setFileFilter(filter);
        // Open details
        Action details = jFileChooser1.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);
        // Sort by date modified
        JTable table = SwingUtils.getDescendantsOfType(JTable.class, jFileChooser1).get(0);
        table.getRowSorter().toggleSortOrder(3);
        table.getRowSorter().toggleSortOrder(3);
        int returnVal = jFileChooser1.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION){
     	   f = jFileChooser1.getSelectedFile();
        }
		return f;
	}
	
	public static ArrayList<Timing> getRedTimingPoints(File f) throws Exception{
		ArrayList<Timing> output = new ArrayList<Timing>();
		if (f == null || !(f.exists())){
			// error reading file
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
		    String line;
		    int sectionID =0;
		    while ((line = br.readLine()) != null) {      
		       // read line by line
		    	switch(sectionID){
				case 0:
					// General stuff
					
					if (line.equals("[TimingPoints]")){
						sectionID=1;
					} else if (line.contains("osu file format v")){
						int version = Integer.parseInt(line.substring(17));
						if (version!=SUPPORTED_OSU_FILE_VERSION){
							String errMsg = "The currently supported osu file version is "+SUPPORTED_OSU_FILE_VERSION +nl;
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);
						}
					}
					else if (line.contains("Mode:")){
						int mode = Integer.parseInt(line.substring(6));
						if (mode!=SUPPORTED_PLAY_MODE){
							String errMsg = "The currently supported mode is mania";
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);            
						}
					} 
					break;
				case 1:
					// timing points
					if (line.contains("[HitObjects]") ){
						sectionID=2;
					} else if (!line.equals("")){
						String[] parts = line.split(",");
						if (parts[0].contains(".")){
							parts[0] = parts[0].substring(0,parts[0].indexOf('.'));
						}
						int isInherited = Integer.parseInt(parts[6]);
						if (isInherited == 1){
							long offset = Long.parseLong(parts[0]);
							float mspb = Float.parseFloat(parts[1]);
							int meter = Integer.parseInt(parts[2]);
							int sampleSet = Integer.parseInt(parts[3]);
							int setID = Integer.parseInt(parts[4]);
							int volume = Integer.parseInt(parts[5]);

							int isKiai = Integer.parseInt(parts[7]);
							Timing timing = new Timing(offset,mspb,meter,sampleSet,setID,volume,isInherited,isKiai);
							output.add(timing);
						}
						
					}
					 
					break;
					
		    	}
		    }
		}

		return output;
	}
	
	public static ArrayList<Timing> getTimingPoints(File f) throws Exception{
		ArrayList<Timing> output = new ArrayList<Timing>();
		if (f == null || !(f.exists())){
			// error reading file
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
		    String line;
		    int sectionID =0;
		    while ((line = br.readLine()) != null) {      
		       // read line by line
		    	switch(sectionID){
				case 0:
					// General stuff
					
					if (line.equals("[TimingPoints]")){
						sectionID=1;
					} else if (line.contains("osu file format v")){
						int version = Integer.parseInt(line.substring(17));
						if (version!=SUPPORTED_OSU_FILE_VERSION){
							String errMsg = "The currently supported osu file version is "+SUPPORTED_OSU_FILE_VERSION +nl;
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);
						}
					}
					else if (line.contains("Mode:")){
						int mode = Integer.parseInt(line.substring(6));
						if (mode!=SUPPORTED_PLAY_MODE){
							String errMsg = "The currently supported mode is mania";
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);            
						}
					} 
					break;
				case 1:
					// timing points
					if (line.contains("[HitObjects]") ){
						sectionID=2;
					} else if (!line.equals("")){
						String[] parts = line.split(",");
						if (parts[0].contains(".")){
							parts[0] = parts[0].substring(0,parts[0].indexOf('.'));
						}
						long offset = Long.parseLong(parts[0]);
						float mspb = Float.parseFloat(parts[1]);
						int meter = Integer.parseInt(parts[2]);
						int sampleSet = Integer.parseInt(parts[3]);
						int setID = Integer.parseInt(parts[4]);
						int volume = Integer.parseInt(parts[5]);
						int isInherited = Integer.parseInt(parts[6]);
						int isKiai = Integer.parseInt(parts[7]);
						Timing timing = new Timing(offset,mspb,meter,sampleSet,setID,volume,isInherited,isKiai);
						output.add(timing);
					}
					 
					break;
					
		    	}
		    }
		}

		return output;
	}
	
	public static ArrayList<HitObject> getChordByTime(ArrayList<HitObject> hitObjects, long startTime){
		ArrayList<HitObject> output = new ArrayList<HitObject>();
		for (HitObject ho : hitObjects){
			if (ho.getStartTime()==startTime){
				output.add(ho.clone());
			}
		}
		output = sortChordByHSType(output);
		return output;
	}
	
	public static ArrayList<HitObject> sortChordByHSType(ArrayList<HitObject> hitObjects){
		ArrayList<HitObject> output = new ArrayList<HitObject>();
		ArrayList<HitObject> wavHS = new ArrayList<HitObject>();
		ArrayList<HitObject> defaultHS = new ArrayList<HitObject>();
		for (HitObject ho : hitObjects){
			if (ho.isWAV_HS()){
				wavHS.add(ho.clone());
			} else{
				if (ho.getWhistleFinishClap()!=0){
					output.add(ho.clone());
				}else{
					defaultHS.add(ho);
				}

			}
		}
		for (HitObject wav : wavHS){
			output.add(wav);
		}
		for (HitObject d : defaultHS){
			output.add(d);
		}
		return output;
	}
	
	public static int getChordSizeForTime(ArrayList<HitObject> hitObjects, long startTime){
		int size = 0;
		for (HitObject ho : hitObjects){
			if (ho.getStartTime()==startTime){
				size++;
			}
		}
		return size;
	}
	
	public static int getDefaultHSChordSizeForTime(ArrayList<HitObject> hitObjects, long startTime){
		int size = 0;
		for (HitObject ho : hitObjects){
			if (ho.getStartTime()==startTime && ho.getWhistleFinishClap()!= 0 && !ho.isWAV_HS()){
				size++;
			}
		}
		return size;
	}
	
	public static void exportBeatmap(File file, String outputText){
		BufferedWriter writer = null;

		try {
			// create a temporary file
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			writer.write(outputText);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.flush();
				writer.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static ArrayList<HitObject> setTimingForHitObjects(ArrayList<Timing> timingPoints, ArrayList<HitObject> input ){
		ArrayList<HitObject> output = new ArrayList<>();
		for (HitObject ho : input){
			ho = setTimingForHitObject(timingPoints, ho);
			output.add(ho.clone());
		}
		return output;
		
	}
	
	public static HitObject copyHS(HitObject input, HitObject output, ArrayList<Long> tps){
		long time = input.getStartTime();
		if (!tps.contains(time)){
			tps.add(time);
		}
		return copyHS(input,output);
	}
	
	
	private static HitObject copyHS(HitObject input, HitObject output){
		HitObject ho = output.clone();
		ho.setHitSound(input.getHitSound());
		ho.setVolume(input.getVolume());
		ho.setWhislteFinishClap(input.getWhistleFinishClap());
		ho.setSetID(input.getSetID());
		ho.setNormalSoftDrum(input.getNormalSoftDrum());
		return ho;
	}
	
	// only for default HS
	private static HitObject setTimingForHitObject(Timing tp, HitObject input){
		HitObject output = input.clone();
		if (output.getVolume()==0){
			output.setVolume(tp.getVolume());
			output.setIsDefaultHS(true);
		} else{
			output.setIsDefaultHS(false);
		}
		output.setNormalSoftDrum(tp.getSampleSet());
		output.setSetID(tp.getSetID());
		return output;
	}
	
	private static HitObject setTimingForHitObject(ArrayList<Timing> timingPoints, HitObject input ){
		HitObject output = null;
		long t = input.getStartTime();
		if (timingPoints.size()>1){
			for (int i = 0; i<timingPoints.size()-1;i++){
				Timing tp1 = timingPoints.get(i);
				long t1 = tp1.getOffset();
				Timing tp2 = timingPoints.get(i+1);
				long t2 = tp2.getOffset();
				
				if (t==t1){
					output = setTimingForHitObject(tp1,input);
					return output;
				} else if (t==t2){
					output = setTimingForHitObject(tp2,input);
					return output;
				} else if (t1<t && t<t2){
					output = setTimingForHitObject(tp1,input);
					return output;
				}
				
			}
		} else if (timingPoints.size()==1){
			Timing tp = timingPoints.get(0);
			output = setTimingForHitObject(tp,input);
		}
		output = setTimingForHitObject(timingPoints.get(timingPoints.size()-1),input);
		return output;
	}
	
	public static ArrayList<HitObject> getListOfHSHitObjects(File f) throws Exception{
		ArrayList<HitObject> output = new ArrayList<>();
		if (f == null || !(f.exists())){
			// error reading file
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
		    String line;
		    int sectionID =0;
		    while ((line = br.readLine()) != null) {      
		       // read line by line
		    	switch(sectionID){
				case 0:
					// General stuff
					
					if (line.contains("[HitObjects]")){
						sectionID=1;
					} else if (line.contains("osu file format v")){
						int version = Integer.parseInt(line.substring(17));
						if (version!=SUPPORTED_OSU_FILE_VERSION){
							String errMsg = "The currently supported osu file version is "+SUPPORTED_OSU_FILE_VERSION +nl;
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);
						}
					}
					else if (line.contains("Mode :")){
						int mode = Integer.parseInt(line.substring(6));
						if (mode!=SUPPORTED_PLAY_MODE){
							String errMsg = "The currently supported mode is mania";
							JOptionPane.showMessageDialog(null, errMsg);
							System.exit(-1);            
						}
					} 
					

					break;
				case 1:
					// Hit OBject
					if (line!=null && line.contains(",")){
						HitObject ho  = null;
						String[] parts = line.split(Pattern.quote(","));
						int x = Integer.parseInt(parts[0]);
						long t = Long.parseLong(parts[2]);
						int type = Integer.parseInt(parts[3]);
						int HS_Type = Integer.parseInt(parts[4]);
						String full_hs = parts[5];
						int index = parts[5].indexOf(':');
						String hs = parts[5].substring(index+1, parts[5].length());
						if (type == 128){
							// change LN to short note
							long endLN = Long.parseLong(parts[5].substring(0, index));
							int volume = getVolumeFromFullHitSoundString(hs);
							String wav = getWavNameFromFullHitSoundString(hs);
							ho  = new HitObject(x, t,HS_Type, volume,endLN, wav);
						} else{
							int volume = getVolumeFromFullHitSoundString(full_hs);
							String wav = getWavNameFromFullHitSoundString(full_hs);
							ho  = new HitObject(x, t,HS_Type, volume, wav);
						}
						output.add(ho);
					} 
					break;
					
				}
		    }
		}
		ArrayList<Timing> timingPoints = getTimingPoints(f);
		output = setTimingForHitObjects(timingPoints, output);
		return output;
	}
	
	private static int getVolumeFromFullHitSoundString(String hs){
		int vol = 0;
		for  (int i = 0 ; i< 3;i++){
			int index = hs.indexOf(':');
			hs = hs.substring(index+1,hs.length());
		}
		vol = Integer.parseInt(hs.substring(0,hs.indexOf(':')));
		return vol;
	}
	
	private static String getWavNameFromFullHitSoundString(String hs){
		String output = "";
		for  (int i = 0 ; i< 3;i++){
			int index = hs.indexOf(':');
			hs = hs.substring(index+1,hs.length());
		}
		output = hs.substring(hs.indexOf(':')+1,hs.length());
		return output;
	}

}

