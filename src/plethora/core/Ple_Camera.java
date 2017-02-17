package plethora.core;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.path.CatmullRomSpline;
import ch.fhnw.util.math.path.IPath;

public class Ple_Camera {

	IController controller;
	private Vec3 cLoc;
	private Vec3 tLoc;
	private Vec3 up;

	public IPath cameraPath;
	public IPath targetPath;

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
	public Ple_Camera(IController controller, float x1, float y1, float z1, float x2, float y2, float z2){
		this.controller = controller;

		up = new Vec3(0,0,-1);
		cLoc = new Vec3(x1,y1,z1);
		tLoc = new Vec3(x2, y2, z2);
	}


	/**
	 * 
	 */
	public void update(){
//		p5.camera(cLoc.x, cLoc.y, cLoc.z,  tLoc.x, tLoc.y, tLoc.z, up.x,up.y,up.z);
		IView view = controller.getCurrentView();
		if (view != null) {
			ICamera cam = controller.getCamera(view);
			cam.setPosition(cLoc);
			cam.setTarget(tLoc);
			cam.setUp(up);
		}
	}
	

	/**
	 * 
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 */
	public void moveStraightCamera(float speedX, float speedY, float speedZ){
		cLoc = moveStraight(cLoc, speedX,  speedY,  speedZ);
	}


	/**
	 * 
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 */
	public void moveStraightTarget(float speedX, float speedY, float speedZ){
		tLoc = moveStraight(tLoc, speedX,  speedY,  speedZ);
	}

	/**
	 * 
	 * @param v
	 * @param speedX
	 * @param speedY
	 * @param speedZ
	 */
	public Vec3 moveStraight(Vec3 v, float speedX, float speedY, float speedZ){
		return new Vec3(v.x + speedX, v.y + speedY, v.z + speedZ);
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
		cLoc = moveCircle(cLoc, radius,  speed,  centerX,  centerY,  centerZ);
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
		tLoc = moveCircle(tLoc, radius,  speed,  centerX,  centerY,  centerZ);
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
	public Vec3 moveCircle(Vec3 v, float radius, float speed, float centerX, float centerY, float centerZ){
		double frameCount = controller.getScheduler().getTime();
		double x = (Math.cos(frameCount * speed) * radius) + centerX; 
		double y = (Math.sin(frameCount * speed) * radius) + centerY; 
		return new Vec3(x, y, centerZ);
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateXCamera(float amplitud, float period){
		cLoc = new Vec3(cLoc.x + moveOcilating(amplitud,  period), cLoc.y, cLoc.z);
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateYCamera(float amplitud, float period){
		cLoc = new Vec3(cLoc.x, cLoc.y + moveOcilating(amplitud,  period), cLoc.z);
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateYtarget(float amplitud, float period){
		tLoc = new Vec3(tLoc.x, tLoc.y + moveOcilating(amplitud,  period), tLoc.z);
	}

	/**
	 * 
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public void ocilateXtarget(float amplitud, float period){
		tLoc = new Vec3(tLoc.x + moveOcilating( amplitud,  period), tLoc.y, tLoc.z);
	}



	/**
	 * 
	 * @param v
	 * @param amplitud
	 * @param period
	 * @param speed
	 */
	public float moveOcilating(float amplitud, float period){	
		double frameCount = controller.getScheduler().getTime();
		return (float) (amplitud * Math.cos(Math.PI * 2 * frameCount / period)) ;
	}
	
	public Ple_Camera setCameraPath(IPath path){
		cameraPath = path;
		return this;
	}
	
	/**
	 * 
	 * @param time
	 * @param smooth
	 */
	public void camaraFollowPath(float time){
		cLoc = followPath(cameraPath, time);
	}
	
	public Ple_Camera setTargetPath(IPath path){
		targetPath = path;
		return this;
	}
	
	/**
	 * 
	 * @param time
	 * @param smooth
	 */
	public void targetFollowPath(float time){
		tLoc = followPath(targetPath, time);
	}


	/**
	 * 
	 * @param path
	 * @param time
	 */
	public Vec3 followPath(IPath path, float time){
		if(path.getNumNodes() > 1){		
			double frameCount = controller.getScheduler().getTime();
			float param = (float) frameCount/time;
			if (param > 1) param = 1;
			return path.position(param);
		}
		return Vec3.ZERO;
	}

	/**
	 * 
	 * @param sp
	 * @param param
	 * @return
	 */
	public Vec3 ptOnParam (CatmullRomSpline sp, float param) {
		return sp.position(param);
	}












}
