package plethora.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import processing.core.PApplet;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

public class Ple_Util {

	PApplet p5;

	public Ple_Util(PApplet _p5){
		p5 = _p5;
	}
	
	/**
	 * NOT YET TESTED - INTERPOLATION BETWEEN SPLINES
	 * @param sp1
	 * @param sp2
	 * @param numOfSplines
	 * @return
	 */
	ArrayList <Spline3D> interpolateSplines(Spline3D sp1, Spline3D sp2, int numOfSplines, boolean close){

		ArrayList <Spline3D> sps = new ArrayList<Spline3D> ();
		
		for(int i = 0; i < numOfSplines; i++){

			Spline3D isp = new Spline3D();

			for(int j = 0; j < sp1.pointList.size(); j++){

				Vec3D v1 = sp1.pointList.get(j);
				Vec3D v2 = sp2.pointList.get(j);
				Vec3D dif = v1.sub(v2);
				float d = dif.magnitude();
				dif.normalize();
				dif.scaleSelf(d/(numOfSplines-1)*i);
				dif.addSelf(v2);

				//----------------------------------------
				p5.stroke(255);
				p5.strokeWeight(1);
				vPoint(dif);
				//-----------------------------------------

				isp.add(dif);
			}

			Vec3D v1 = sp1.pointList.get(0);
			Vec3D v2 = sp2.pointList.get(0);
			Vec3D dif = v1.sub(v2);
			float d = dif.magnitude();
			dif.normalize();
			dif.scaleSelf(d/(numOfSplines-1)*i);
			dif.addSelf(v2);

			//OPEN OR CLOSE:
			if(close)sps.add(isp);
			//isp.add(dif);
			//----------
		}
		return sps;
	}

	/**
	 * 
	 * @param v
	 */
	void vPoint(Vec3D v){
		p5.point(v.x,v.y,v.z);	
	}
	
	/**
	 * NOT YET TESTED
	 * @param sp
	 * @param num
	 * @return
	 */
	ArrayList <Vec3D> divideNumSegments (Spline3D sp, int num){
		ArrayList <Vec3D> pts = new ArrayList <Vec3D>();
		float tlen = 0;
		
		int count = sp.pointList.size()-1;
		
		float [] aculengs = new float[count];
		float [] lengs = new float[count];
		
		for(int i = 1; i < sp.pointList.size(); i++){
			Vec3D v = sp.pointList.get(i);
			Vec3D bef = sp.pointList.get(i-1);

			Vec3D dif = v.sub(bef);
			float d = dif.magnitude();

			tlen += d;

			lengs[i-1] = d;
			aculengs[i-1] = tlen;
		}

		for(int i  = 0; i < num; i++){
			float linearPos = tlen/(num-1) * i;
			int segment = 0;
			float loclen = linearPos;

			//determine in what segment u are:
			for(int j = 0; j < count; j++){
				if (linearPos > aculengs[j]){
					segment = j+1;
				}
			}
			//determine your distance in your own segment:
			for(int k = 0; k < segment; k++){
				loclen -= lengs[k];
			}

			Vec3D v1 = new Vec3D();
			Vec3D bef1 = new Vec3D();

			if(segment+1 < sp.pointList.size()){//!!!!!!!!!!!!!!!!!!!!dude!

				v1 = sp.pointList.get(segment+1); ///////+!1???????????
				bef1 = sp.pointList.get(segment);

				Vec3D dif = v1.sub(bef1);

				dif.normalize();
				dif.scaleSelf(loclen);

				//ADD PT TO ARRAYLIST
				pts.add(dif);

				dif.addSelf(bef1);

				//if(segment == 0){p5.stroke(255,0,0);}
				//if(segment == 1){p5.stroke(0,255,0);}
				//if(segment == 2){p5.stroke(0,0,255);}
				//if(segment == 3){p5.stroke(255,0,255);}
				//p5.strokeWeight(3);

				//vPoint(dif);
			}
		}
		return pts;
	}
	
	/**
	 * 
	 * @param sp
	 */
	public void drawSpline(Spline3D sp){
		p5.stroke(255,0,0);
		p5.strokeWeight(1);
		p5.beginShape();
		for(Vec3D v : sp.pointList){
			vex(v);
		}
		p5.endShape();	
	}

	/**
	 * 
	 * @param v
	 */
	public void vex(Vec3D v){
		p5.vertex(v.x,v.y,v.z);
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 */
	public void vLine(Vec3D v1, Vec3D v2){
		p5.line(v1.x,v1.y,v1.z, v2.x, v2.y, v2.z);
	}
	
	/**
	 * NOT YET TESTED
	 * @param p1
	 * @param p2
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Vec3D localPt(Vec3D p1, Vec3D p2,float x, float y , float z){
		float distance = p1.distanceTo(p2);

		Vec3D result = new Vec3D(0,0,0);

		//axis = x , rot = y, up = z
		Vec3D axis = p2.sub(p1);
		axis.normalize();
		axis.scaleSelf(distance * x);

		Vec3D rot = p2.sub(p1);
		rot.normalize();
		rot.rotateZ(PApplet.PI/2);
		rot.scaleSelf(distance * y);
		//rot.scaleSelf(y);

		Vec3D up = new Vec3D(0,0,z);

		result.addSelf(axis);
		result.addSelf(rot);
		result.addSelf(up);
		result.addSelf(p1);
		return result;
	}
	
	
	/**
	 * NOT YET TESTED
	 * @param sp
	 * @param param
	 * @return
	 */
	public Vec3D ptOnParam (Spline3D sp, float param) {
		float tlen = 0;
		int count = sp.pointList.size()-1;
		float [] aculengs = new float[count];
		float [] lengs = new float[count];
		for(int i = 1; i < sp.pointList.size(); i++){
			Vec3D v = sp.pointList.get(i);
			Vec3D bef = sp.pointList.get(i-1);
			Vec3D dif = v.sub(bef);
			float d = dif.magnitude();
			tlen += d;
			lengs[i-1] = d;
			aculengs[i-1] = tlen;
		}

		float linearPos = tlen*param;
		int segment = 0;
		float loclen = linearPos;

		//determine in what segment u are:
		for(int j = 0; j < count; j++){
			if (linearPos > aculengs[j]){
				segment = j+1;
			}
		}
		//determine your distance in your own segment:
		for(int k = 0; k < segment; k++){
			loclen -= lengs[k];
		}

		Vec3D v1 = sp.pointList.get(segment+1);
		Vec3D bef1 = sp.pointList.get(segment);

		Vec3D dif = v1.sub(bef1);

		dif.normalize();
		dif.scaleSelf(loclen);

		dif.addSelf(bef1);

		//vPoint(dif);

		return dif;
	}


	/**
	 * 
	 * @param boids
	 * @param filename
	 */
	public void createLocationFile (ArrayList <Ple_Agent> boids, String filename, String sepSymbol, boolean block){

		PrintWriter output = p5.createWriter("data/loc" + filename + p5.frameCount + ".csv"); 

		PApplet.println("creating csv file");

		for (Ple_Agent pa : boids) {
			if(!block){
				output.println(pa.loc.x + "," + pa.loc.y + "," + pa.loc.z);
			}else{
				output.print(pa.loc.x + "," + pa.loc.y + "," + pa.loc.z + sepSymbol);
			}
		}

		output.flush();
		output.close();

		PApplet.println("data/loc" + filename + p5.frameCount + ".csv" + " created.");
	}
	
	/**
	 * 
	 * @param boids
	 * @param filename
	 */
	public void createTrailFile (ArrayList <Ple_Agent> boids, String filename){

		PrintWriter output = p5.createWriter("data/loc" + filename + p5.frameCount + ".csv"); 

		PApplet.println("creating csv file");

		int count = 0;
		int count2 = 0;
		
		for (Ple_Agent pa : boids) {
			output.println("agent");
			for(Vec3D v: pa.trail){
				if(v != null){
				//output.println(v.x + "," + v.y + "," + v.z + "," + count + "," + count2);
				output.println(v.x + "," + v.y + "," + v.z);
				count2 ++;
				}
			}
			count ++;
			count2 = 0;
		}

		output.flush();
		output.close();

		PApplet.println("data/loc" + filename + p5.frameCount + ".csv" + " created.");
	}
	
	
	/**
	 * 
	 * @param boids
	 * @param filename
	 */
	public void createAnchoredData (ArrayList <Ple_Agent> boids, String filename){

		PrintWriter output = p5.createWriter("data/loc" + filename + p5.frameCount + ".csv"); 
		PrintWriter output2 = p5.createWriter("data/vels" + filename + p5.frameCount + ".csv"); 
		PrintWriter output3 = p5.createWriter("data/anchor" + filename + p5.frameCount + ".csv"); 

		PApplet.println("creating csv file");

		for (Ple_Agent pa : boids) {
			if(pa.lock){
				output.println(pa.loc.x + "," + pa.loc.y + "," + pa.loc.z);
				output2.println(pa.vel.x + "," + pa.vel.y + "," + pa.vel.z);
				output3.println(pa.anchor.x + "," + pa.anchor.y + "," + pa.anchor.z);
			}
			
		}

		output.flush();
		output.close();
		
		output2.flush();
		output2.close();
		
		output3.flush();
		output3.close();

		PApplet.println("data/loc" + filename + p5.frameCount + ".csv" + " created.");
	}
	
	
	
	public void createTailFile (ArrayList <Ple_Agent> boids, String filename){

		PrintWriter output = p5.createWriter("data/tailLocs" + filename + p5.frameCount + ".csv"); 
		PrintWriter output2 = p5.createWriter("data/index" + filename + p5.frameCount + ".csv");
		PrintWriter output3 = p5.createWriter("data/vels" + filename + p5.frameCount + ".csv");
		PrintWriter output4 = p5.createWriter("data/tailIndex" + filename + p5.frameCount + ".csv");

		PApplet.println("creating csv file");

		int agentId = 0;
		
		for (Ple_Agent pa : boids) {
			for(int i = 0; i < pa.tail.length; i ++){	
			
				//output.println(pa.loc.x + "," + pa.loc.y + "," + pa.loc.z);
				output.println(pa.tail[i].x + "," + pa.tail[i].y + "," + pa.tail[i].z);
				output2.println(agentId);
				output3.println(pa.vel.x + "," + pa.vel.y + "," + pa.vel.z);
				output4.println(i);
				
			}
			agentId++;
			
				//output.print(pa.loc.x + "," + pa.loc.y + "," + pa.loc.z + sepSymbol);
			
		}

		output.flush();
		output.close();
		
		output2.flush();
		output2.close();
		
		output3.flush();
		output3.close();
		
		output4.flush();
		output4.close();

		PApplet.println("data/loc" + filename + p5.frameCount + ".csv" + " created.");
	}



	/**
	 * 
	 * @param boids
	 * @param filename
	 */
	public void createVelocityFile (ArrayList <Ple_Agent> boids, String filename, String sepSymbol, boolean block){

		PrintWriter output = p5.createWriter("data/vel" + filename + p5.frameCount + ".csv"); 

		PApplet.println("creating csv file");

		for (Ple_Agent pa : boids) {
			if(!block){
				output.println(pa.vel.x + "," + pa.vel.y + "," + pa.vel.z);
			}else{
				output.print(pa.vel.x + "," + pa.vel.y + "," + pa.vel.z  + sepSymbol);
			}
		}

		output.flush();
		output.close();

		PApplet.println("data/vel" + filename + p5.frameCount + ".csv" + " created.");
	}
}
