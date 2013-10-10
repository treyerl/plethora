package plethora.core;

import java.util.Iterator;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PConstants;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

public class Ple_Camera {

	PApplet p5;
	public Vec3D cLoc;
	public Vec3D tLoc;
	public Vec3D up;

	public Spline3D path1;
	public Spline3D path2;

	/**
	 * 
	 * @param _p5
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 */
	public Ple_Camera(PApplet _p5, float x1, float y1, float z1, float x2, float y2, float z2){
		p5 = _p5;

		up = new Vec3D(0,0,-1);
		cLoc = new Vec3D (x1,y1,z1);
		tLoc = new Vec3D (x2, y2, z2);

		path1 = new Spline3D();
		path2 = new Spline3D();
	}


	/**
	 * 
	 */
	public void update(){
		p5.camera(cLoc.x, cLoc.y, cLoc.z,  tLoc.x, tLoc.y, tLoc.z, up.x,up.y,up.z);
	}
	

	/**
	 * 
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 */
	public void moveStraightCamera(float speedX, float speedY, float speedZ){
		moveStraight(cLoc, speedX,  speedY,  speedZ);
	}


	/**
	 * 
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 */
	public void moveStraightTarget(float speedX, float speedY, float speedZ){
		moveStraight(tLoc, speedX,  speedY,  speedZ);
	}

	/**
	 * 
	 * @param v
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 */
	public void moveStraight(Vec3D v, float speedX, float speedY, float speedZ){
		v.x += speedX;
		v.y += speedY;
		v.z += speedZ;
	}


	/**
	 * 
	 * @param radius
	 * @param speed
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 */
	public void moveCircleCamera(float radius, float speed, float centerX, float centerY, float centerZ){
		moveCircle(cLoc, radius,  speed,  centerX,  centerY,  centerZ);
	}

	/**
	 * 
	 * @param radius
	 * @param speed
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 */
	public void moveCircleTarget(float radius, float speed, float centerX, float centerY, float centerZ){
		moveCircle(tLoc, radius,  speed,  centerX,  centerY,  centerZ);
	}

	/**
	 * 
	 * @param v
	 * @param radius
	 * @param speed
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 */
	public void moveCircle(Vec3D v, float radius, float speed, float centerX, float centerY, float centerZ){
		v.x = (PApplet.cos(p5.frameCount * speed) * radius) + centerX; 
		v.y = (PApplet.sin(p5.frameCount * speed) * radius) + centerY; 
		v.z = centerZ;
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateXCamera(float amplitud, float period){
		cLoc.x += moveOcilating(amplitud,  period);
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateYCamera(float amplitud, float period){
		cLoc.y += moveOcilating(amplitud,  period);
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateYtarget(float amplitud, float period){
		tLoc.y += moveOcilating(amplitud,  period);
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateXtarget(float amplitud, float period){
		tLoc.x += moveOcilating( amplitud,  period);
	}



	/**
	 * 
	 * @param v
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public float moveOcilating(float amplitud, float period){	 
		return amplitud * PApplet.cos(PConstants.TWO_PI * p5.frameCount / period) ;
	}
	
	/**
	 * 
	 * @param time
	 * @param smooth
	 */
	public void camaraFollowPath(float time, boolean smooth){
		Vec3D v = followPath(path1, time, smooth);
		
		cLoc.x = v.x;
		cLoc.y = v.y;
		cLoc.z = v.z;
	}
	
	/**
	 * 
	 * @param time
	 * @param smooth
	 */
	public void targetFollowPath(float time, boolean smooth){
		Vec3D v = followPath(path2, time, smooth);
		
		tLoc.x = v.x;
		tLoc.y = v.y;
		tLoc.z = v.z;
	}


	/**
	 * 
	 * @param sp
	 * @param time
	 */
	public Vec3D followPath(Spline3D sp, float time, boolean smooth){

		Spline3D pathsp = new Spline3D();
		Vec3D locInSpline = new Vec3D();
		if(sp.pointList.size() > 1){

			if(smooth){
				pathsp = smoothPath(sp, 8);
			}else{
				pathsp = sp;
			}
			
			float param = (float) p5.frameCount/time;
			if (param > 1) param = 1;

			locInSpline = ptOnParam(pathsp, param);

		}
		return locInSpline;
	}

	/**
	 * 
	 * @param sp
	 * @param RES
	 * @return
	 */
	public Spline3D smoothPath(Spline3D sp, int RES){

		Spline3D nuSp = new Spline3D ();	
		for(Iterator <Vec3D> i = sp.computeVertices(RES).iterator(); i.hasNext();) {
			Vec3D p =(Vec3D)i.next();
			nuSp.add(p);
		}
		return nuSp;
	}


	/**
	 * 
	 * @param v
	 */
	public void addPointToCameraPath(Vec3D v){
		path1.add(v);
	}

	/**
	 * 
	 * @param v
	 */
	public void addPointToTargetPath(Vec3D v){
		path2.add(v);
	}


	/**
	 * 
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












}
