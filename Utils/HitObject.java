package Utils;
import java.util.Comparator;

public class HitObject {
	// Instance Variables
	private int type; // 1=circle 128=LN
	private long endLN;
	private int xposition;
	private final int ypos = 192;
	private long startTime;
	private String hitSound;
	private int volume;
	private int column;
	private boolean isDefaultHS;
	private int whistle_finish_clap=0; 	// 2 = whistle, 4 = finish, 8 = clap
	private int setID=0;	//X in hit-hitnormalX.wav
	private int normal_Soft_Drum=1; // 1 = normal, 2 = soft, 3 = drum
	private int addition=1; //1 = hit, 2= soft, 3=drum
	private String nl = OsuUtils.nl;
	//64,192,708,  1,    2,      3        :2       :0    :0: 
	//x, y  ,t,  type,whistle,   sampleset:addition:setID:volume: 
	//                drum-softwhistle.wav          auto :auto:
	
	public HitObject(long t, int col){
		setStartTime(t);
		setColumn(col);
	}
	
	/**
	 * Constructor for a single note
	 * 
	 * @param xpos
	 *            x-position of hit object, max 512
	 * @param t
	 *            start time of hit object
	 * @param vol
	 *            volume from 0 to 100
	 * @param hitSound
	 *            filename of hitsound, in .wav format
	 */
	public HitObject(int xpos, long t, int vol, String hitSound) {
		xposition = xpos;
		setStartTime(t);
		type = 1;
		endLN = 0;
		setVolume(vol);
		this.setHitSound(hitSound);
	}
	
	public HitObject(int xpos, long t,int whistle_finish_clap ,int vol, String hitSound) {
		xposition = xpos;
		setStartTime(t);
		type = 1;
		endLN = 0;
		this.whistle_finish_clap=whistle_finish_clap;
		setVolume(vol);
		this.setHitSound(hitSound);
	}

	/**
	 * Constructor for a LN
	 * 
	 * @param xpos
	 *            x-position of hit object, max 512
	 * @param t
	 *            start time of hit object
	 * @param volume
	 *            volume from 0 to 100
	 * @param end
	 *            end time of hit object
	 * @param hitSound
	 *            filename of hitsound, in .wav format
	 */
	public HitObject(int xpos, long t, int volume, long end, String hitSound) {
		type = 128;
		xposition = xpos;
		setStartTime(t);
		endLN = end;
		this.setVolume(volume);
		this.setHitSound(hitSound);
	}

	public HitObject(int xpos, long t,int HS_Set, int volume, long end, String hitSound) {
		type = 128;
		xposition = xpos;
		setStartTime(t);
		whistle_finish_clap = HS_Set;
		endLN = end;
		this.setVolume(volume);
		this.setHitSound(hitSound);
	}
	
	public void setIsDefaultHS(boolean input){
		isDefaultHS = input;
	}
	
	public boolean getIsDefaultHS(){
		return isDefaultHS;
	}
	
	public int getType() {
		return type;
	}
	
	public int getWhistleFinishClap (){
		return whistle_finish_clap;
	}
	
	public int getNormalSoftDrum(){
		return normal_Soft_Drum;
	}
	
	public void setNormalSoftDrum(int v){
		normal_Soft_Drum=v;
	}

	public void convertColumnIDtoXpos(int KeyCount){
		double columnWidth = 512.0/KeyCount;
		xposition = (int) Math.round(getColumn() * columnWidth) + 10;
	}
	
	public boolean isWAV_HS(){
		if (hitSound.contains("wav")){
			return true;
		}
		return false;
	}
	
	public void setWhislteFinishClap(int type){
		whistle_finish_clap = type;
	}
	
	public int getSetID(){
		return setID;
	}
	
	public void setSetID(int set){
		setID = set;
	}
	
	public HitObject clone(){
		HitObject ho = null;
		if (endLN!=0  && endLN>startTime){
			// LN
			ho = new HitObject(xposition,startTime,whistle_finish_clap,volume,endLN,hitSound);
		} else {
			// Short note
			ho =  new HitObject(xposition,startTime,whistle_finish_clap,volume,hitSound);
		}
		ho.setSetID(setID);
		ho.setNormalSoftDrum(normal_Soft_Drum);
		ho.setAddition(addition);
		return ho;
	}
	
	public int getAddition(){
		return addition;
	}
	
	public void setAddition(int v){
		addition=v;
	}
	
	public Sample toSample(){
			if (!hitSound.contains(".wav")){
				switch(normal_Soft_Drum){
				case 1:
					hitSound = "normal-";
					break;
					
				case 2:
					hitSound = "soft-";
					break;
					
				case 3:
					hitSound = "drum-";
					
					break;
				}
				
				switch(addition){
				case 1:
					hitSound+="hit";
					break;
				case 2:
					hitSound+="soft";
					break;
				case 3:
					hitSound+="drum";
					break;
				}
				
				switch(whistle_finish_clap){
				case 0:
					hitSound+="normal";
					break;
				case 2:
					hitSound+="whistle";
					break;
				case 4:
					hitSound+="finish";
					break;
				case 8:
					hitSound+="clap";
				}
				
				String id = "";
				if (setID>1){
					id+=setID;
				}
				hitSound+=id+".wav";
			}
			Sample s = new Sample(startTime,hitSound,volume);
			s.addQuotesToHS();
			return s;
	}
	
	
	
	@Override
	public String toString() {
		if (type==1) {
			// for a single note
			return "" + xposition + "," + ypos + "," + getStartTime() + "," + 1
					+ "," + whistle_finish_clap + "," + "0:0:0:" + getVolume() + ":" + getHitSound() + nl;
		}
		// for a LN
		return "" + xposition + "," + ypos + "," + getStartTime() + "," + 128 + ","
				+ whistle_finish_clap + "," + endLN + ":0:0:0:" + getVolume() + ":" + getHitSound() + nl;
	}
	
	public static Comparator<HitObject> StartTimeComparator = new Comparator<HitObject>() {
		@Override
		public int compare(HitObject ho1, HitObject ho2) {
			long t1 = ho1.startTime;
			long t2 = ho2.startTime;
			/* For ascending order */
			return (int) (t1 - t2);
		}
	};

	
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getHitSound() {
		return hitSound;
	}

	public void setHitSound(String hitSound) {
		this.hitSound = hitSound;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

}
