package plethora.core;

import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.util.FileSequenceDescriptor;
import toxi.util.FileUtils;

public class Ple_Image {

	PApplet p5;
	FileSequenceDescriptor fsd;
	Iterator <String> images;

	int vCol;
	int vRow;
	String fPath;
	float iScale;

	float y = 0;
	float x = 0;

	Vec3D loc = new Vec3D();

	PImage img;
	
	int alpha = 255;

	public Ple_Image(PApplet _p5, String _fPath, Vec3D _loc, int _vCol, int _vRow, float _iScale){
		p5 = _p5;

		fPath = _fPath;
		loc = _loc;
		vCol = _vCol;
		vRow = _vRow;
		iScale = _iScale;


		fsd = FileUtils.getFileSequenceDescriptorFor(p5.dataPath("") + fPath);
		images=fsd.iterator();

		String imgPath=(String)images.next();
		img = p5.loadImage(imgPath);
	}

	/**
	 * This function will call both displayPoints and updateImageSeq, is a shortcut to make functional.
	 */
	public void run() {
		updateImageSeq();
		displayPoints(3,100,0,0,0);
	}
	
	public void setAlpha(int v){
		alpha = v;
	}


	/**
	 * This Function allows to update the image sequence. This will make the videos to Play.
	 */
	public void updateImageSeq() {
		if (!images.hasNext()) {
			images=fsd.iterator();
		}

		String imgPath = (String)images.next();
		img = p5.loadImage(imgPath);

	}

	/**
	 * *K
	 * Use this Function to display the image as a Solid
	 * @param rotX - This is the rotation in X
	 * @param rotY - This is the Rotation in Y
	 * @param rotZ - This is the Rotation in Z
	 */
	public void displayImage(float rotX,  float rotY, float rotZ) {
		renderImage(img, loc, vCol *iScale, vRow *iScale, 255, alpha, rotX, rotY, rotZ);
	}

	/**
	 * *K
	 * Use this Function to display the image as Points. 
	 * @param thickness - This is the Thickness of the Points, the 'strokeWeight'
	 * @param alpha - This is the transparency of the points
	 * @param rotX - This is the rotation in X
	 * @param rotY - This is the Rotation in Y
	 * @param rotZ - This is the Rotation in Z
	 */
	public  void displayPoints(int thickness, int alpha, float rotX,  float rotY, float rotZ) {

		int colCount = 0;
		int rowCount = 0;

		//p5.pushMatrix();
		//rotate(45,0,0);
		for (int i = 0; i < vCol * vRow; i+= 1) {

			int col = img.pixels[i];
			p5.stroke(col,alpha);
			p5.strokeWeight(thickness);
			//int y = int(brightness(col)/40);
			y = 0;
			//float y = (red(col))*0.3;
			//line(colCount *10 + loc.x, y+ loc.y, rowCount *10+ loc.z, colCount *10 + loc.x, loc.y, rowCount *10+ loc.z);

			p5.pushMatrix();
			p5.translate(loc.x,loc.y,loc.z);
			rotate(rotX,rotY,rotZ);
			p5.point(0 + (colCount * iScale), y + 0, 0 - (rowCount * iScale));
			//p5.point(loc.x + (colCount * iScale), y + loc.y,  loc.z - (rowCount * iScale));
			p5.popMatrix();

			//pushMatrix();
			//translate(colCount *2 + loc.x, y+ loc.y, rowCount *2 + loc.z);
			//rotateX(PI/2);
			//noStroke();
			//fill(col);
			//rect(0,0,1,1);
			//popMatrix();

			colCount++;

			if (colCount == vCol) {
				rowCount++;
				colCount = 0;
			}
		}
		//p5.popMatrix();
	}

	/**
	 * rotation function, call inside pushMatrix() , for private use.
	 * @param inX
	 * @param inY
	 * @param inZ
	 */
	public void rotate(float inX, float inY, float inZ){		
		p5.rotateX(PApplet.radians(inX));
		p5.rotateY(PApplet.radians(inY));
		p5.rotateZ(PApplet.radians(inZ));
	}

	/**
	 * This Function allows to call an image using scale and location, for private use.
	 * @param img
	 * @param _loc
	 * @param _diamX
	 * @param _diamY
	 * @param _col
	 * @param _alpha
	 */
	public void renderImage(PImage img, Vec3D _loc, float _diamX, float _diamY, int _col, float _alpha, float rotX,  float rotY, float rotZ ) {
		//float rots[] = cam.getRotations();
		//float camPos[] = cam.getPosition();

		///Vec3D cP = new Vec3D(camPos[0], camPos[1], camPos[2]);

		//float dist = cP.distanceTo(_loc);
		//float coef = map(dist, 700, 0, 0.2f, 1);
		//if(coef < 0.7f) coef = 0.7f;
		//if (coef > 1.0f) coef = 1.0f;
		//if(dist < 300.0f) coef = 1.0f;

		//_diam *= coef;
		p5.noStroke();

		p5.pushMatrix();
		p5.translate( _loc.x, _loc.y, _loc.z );

		//rotateX((float) (rots[0]));

		//p5.rotateX((float) (-PConstants.PI/2));
		
		rotate(rotX,rotY,rotZ);
		rotate(-90,0,0);
		

		//rotateY((float) (rots[1]));
		//rotateZ((float) (rots[2]));

		//p5.tint(p5.red(_col), p5.green(_col), p5.blue(_col), _alpha);
		p5.tint(_col, _alpha);

		//tint(coef, _alpha);
		//p5.imageMode(PConstants.CENTER);
		p5.image(img, 0, 0, _diamX, _diamY);
		p5.popMatrix();
	}

























}
