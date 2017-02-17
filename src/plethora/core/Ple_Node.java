package plethora.core;

import ch.fhnw.util.math.Vec3;

public class Ple_Node {

	public Vec3 loc;
	public int xPos;
	public int yPos;
	
	public float data = 0;
	
	/**
	 * Simple vector class that allows to correlate data and vector location
	 * @param _loc
	 */
	public Ple_Node(Vec3 _loc){	
		loc = _loc;	
	}
	/**
	 * 
	 * @param i
	 * @param j
	 */
	public void setIndex(int i, int j){
		xPos = i;
		yPos = j;
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setData(float value){
		data = value;
	}
}
