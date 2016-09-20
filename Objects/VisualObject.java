package Objects;

import java.util.ArrayList;

import Commands.Command;
import Utils.OsuUtils;



// A visual object contains multiple commands
public abstract class VisualObject {
	private final String nl = OsuUtils.nl;
	
	protected Object type;
	private Layer layer; 
	protected Origin origin; //  where on the image should osu! consider that image's origin (coordinate) to be
	protected String filepath;
	// x and y are in terms of hit object and not the SB coordinate
	// auto convert to SB coordinate in constructor
	protected int x; 
	protected int y;
	protected ArrayList<Command> commands;
	
	public VisualObject(ObjectType type, Layer layer, String filePath , int x , int y){
		initializeParameters(type, layer,Origin.Centre, filePath , x ,y);
	}
	
	public VisualObject(ObjectType type, Layer layer,Origin origin, String filePath , int x , int y){
		initializeParameters(type, layer,origin, filePath , x ,y);
	}
	
	private void initializeParameters(ObjectType type, Layer layer,Origin origin, String filePath , int x , int y){
		this.x = OsuUtils.hitObjectXToStoryboardX(x);
		this.y = OsuUtils.hitObjectYToStoryboardY(y);
		this.type = type;
		this.setLayer(layer);
		this.origin = origin;
		this.filepath = filePath;
		commands = new ArrayList<>();
	}
	
	public void add(Command c){
		commands.add(c);
	}
	
	public void remove(Command c){
		commands.remove(c);
	}
	
	public long getStartTime(){
		long startTime=Long.MAX_VALUE;
		if (commands.size()>0){
			for (Command c : commands){
				if (c.getStartTime()<startTime){
					startTime = c.getStartTime();
				}
			}
		}
		return startTime;
	}
	
	public long getEndTime(){
		long endTime=Long.MIN_VALUE;
		if (commands.size()>0){
			for (Command c : commands){
				if (c.getEndTime()>endTime){
					endTime = c.getEndTime();
				}
			}
		}
		return endTime;
	}
	
	public abstract String getHeader();
	public abstract VisualObject Clone();
		
	protected void addQuotesToFilePath(){
		filepath =  "\"" + filepath + "\"";
	}
	
	public void sortByStartTime(){
		commands.sort(Command.StartTimeComparator);
	}
	
	public void sortByEndTime(){
		commands.sort(Command.EndTimeComparator);
	}
	
	public boolean isSprite(){
		return type.equals(ObjectType.Sprite);
	}
	
	public String toString(){
		commands.sort(Command.StartTimeComparator);
		String output = getHeader() + nl;
		for (Command c: commands){
			output += c.toString() + nl;
		}
		return output;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setXY(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}
}
