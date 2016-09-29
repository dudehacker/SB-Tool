package Commands;

import Utils.OsuUtils;

public class VectorScale extends Command {
	private double startScaleX;
	private double startScaleY;
	private double endScaleX;
	private double endScaleY;

	public VectorScale(long startT, long endT) {
		super(CommandName.VectorScale, 0, startT, endT);
		setParameters(1, 1, 1, 1);
	}
	
	public VectorScale(int easing, long startT, long endT) {
		super(CommandName.VectorScale, easing, startT, endT);
		setParameters(1, 1, 1, 1);
	}
	
	public VectorScale(long startT, long endT, double start, double end) {
		super(CommandName.VectorScale, 0, startT, endT);
		setParameters(start, start, end, end);
	}
	
	public VectorScale(int easing, long startT, long endT, double start, double end) {
		super(CommandName.VectorScale, easing, startT, endT);
		setParameters(start, start, end, end);
	}
	
	public VectorScale(int easing, long startT, long endT,double startScaleX, double startScaleY, double endScaleX, double endScaleY) {
		super(CommandName.VectorScale, easing, startT, endT);
		setParameters(startScaleX, startScaleY, endScaleX, endScaleY);
	}
	
	public VectorScale(long startT, long endT,double startScaleX, double startScaleY, double endScaleX, double endScaleY) {
		super(CommandName.VectorScale, 0, startT, endT);
		setParameters(startScaleX, startScaleY, endScaleX, endScaleY);
	}

	private void setParameters(double startScaleX, double startScaleY, double endScaleX, double endScaleY){
		this.startScaleX = startScaleX;
		this.startScaleY = startScaleY;
		this.endScaleX = endScaleX;
		this.endScaleY = endScaleY;
	}
	
	@Override
	public Command clone() {
		return new VectorScale(easing, startTime,endTime,startScaleX,startScaleY,endScaleX,endScaleY);
	}

	@Override
	public String toString() {
		String startX = OsuUtils.formatDoubleToString(startScaleX);
		String endX = OsuUtils.formatDoubleToString(endScaleX);
		String startY = OsuUtils.formatDoubleToString(startScaleY);
		String endY = OsuUtils.formatDoubleToString(endScaleY);
		String y = OsuUtils.formatDoubleToString(endScaleY-startScaleY);
		String x = OsuUtils.formatDoubleToString(endScaleX-startScaleX); 
		if (x.equals(y)){
			if (startX.equals(endX)){
				if (startX.equals("1")){
					return "";
				}
				return " S,"+easing+","+startTime+","+endTime+","+startX;
			}
			return " S,"+easing+","+startTime+","+endTime+","+startX+","+endX;
		}
		return " S,"+easing+","+startTime+","+endTime+","+startX+","+startY+","+endX+","+endY;
	}

	public double getStartScaleX() {
		return startScaleX;
	}

	public void setStartScaleX(double startScaleX) {
		this.startScaleX = startScaleX;
	}

	public double getStartScaleY() {
		return startScaleY;
	}

	public void setStartScaleY(double startScaleY) {
		this.startScaleY = startScaleY;
	}

	public double getEndScaleX() {
		return endScaleX;
	}

	public void setEndScaleX(double endScaleX) {
		this.endScaleX = endScaleX;
	}

	public double getEndScaleY() {
		return endScaleY;
	}

	public void setEndScaleY(double endScaleY) {
		this.endScaleY = endScaleY;
	}

}
