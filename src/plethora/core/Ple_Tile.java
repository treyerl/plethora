package plethora.core;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;

public class Ple_Tile {

	PApplet p5;

	public Vec3D a;
	public Vec3D b;
	public Vec3D c;
	public Vec3D d;

	public int recursion;

	public float weight01;
	public float weight02;
	public float weight03;
	public float weight04;

	public float height;
	public float heightScale;

	public int count = 0;

	public int maxRecursion;

	public boolean quad1;
	public boolean quad2;
	public boolean quad3;
	public boolean quad4;
	
	TriangleMesh tileMesh = new TriangleMesh("tile");

	public Ple_Tile(PApplet _p5, Vec3D _a, Vec3D _b, Vec3D _c, Vec3D _d){
		p5 = _p5;

		a = _a;
		b = _b;
		c = _c;
		d = _d;

		recursion = 2;

		weight01 = 0.5f;
		weight02 = 0.5f;
		weight03 = 0.5f;
		weight04 = 0.5f;

		height = 1;
		heightScale = 1;

		maxRecursion = 4;

		quad1 = true;
		quad2 = true;
		quad3 = true;
		quad4 = true;
		
		
	}

	/**
	 * 
	 * @param value1
	 * @param value2
	 * @param value3
	 * @param value4
	 */
	public void setQuadBooleans(boolean value1, boolean value2, boolean value3, boolean value4){
		quad1 = value1;
		quad2 = value2;
		quad3 = value3;
		quad4 = value4;
	}

	/**
	 * 
	 * @param value
	 */
	public void setMaxRecursion(int value){
		maxRecursion = value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setRecursion(int value){
		recursion = value;
	}

	/**
	 * 
	 */
	public void update(){
		tileMesh.clear();
		
		if(recursion > maxRecursion){
			recursion = maxRecursion;
		}

		if(recursion > 0){
			subdivide(a,b,c,d,recursion);
		}else{
			drawPoly(a,b,c,d);
			tMeshFromQuad(a,b,c,d);
		}

	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param iterations
	 */
	public void subdivide(Vec3D a, Vec3D b,Vec3D c,Vec3D d,int iterations){

		
		
		if(iterations > 0){

			Vec3D nor = Normal(a,b,d, height*heightScale);


			Vec3D ave = averageVectors(a, b, c, d, nor);

			Vec3D mpt1 = midPts(a,b, weight01);
			Vec3D mpt2 = midPts(b,c, weight02);
			Vec3D mpt3 = midPts(c,d, weight03);
			Vec3D mpt4 = midPts(d,a, weight04);

			if(quad1){
				subdivide(a,mpt1,ave,mpt4,iterations-1);
			}else{
				drawPoly(a,mpt1,ave,mpt4);
				tMeshFromQuad(a,mpt1,ave,mpt4);
			}

			if(quad2){
				subdivide(b,mpt2,ave,mpt1,iterations-1);
			}else{
				drawPoly(b,mpt2,ave,mpt1);
				tMeshFromQuad(b,mpt2,ave,mpt1);
			}

			if(quad3){
				subdivide(c,mpt3,ave,mpt2,iterations-1);
			}else{
				drawPoly(c,mpt3,ave,mpt2);
				tMeshFromQuad(c,mpt3,ave,mpt2);
			}

			if(quad4){
				subdivide(d,mpt4,ave,mpt3,iterations-1);
			}else{
				drawPoly(d,mpt4,ave,mpt3);
				tMeshFromQuad(d,mpt4,ave,mpt3);
			}

		}else{

			drawPoly(a,b,c,d);
			tMeshFromQuad(a,b,c,d);
		}
	}
	
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param v4
	 */
	public void tMeshFromQuad(Vec3D v1, Vec3D v2, Vec3D v3, Vec3D v4){
		tileMesh.addFace(v1, v2, v3);
		tileMesh.addFace(v1, v3, v4);
	}

	/**
	 * 
	 * @param vec1
	 * @param vec2
	 * @param weight
	 * @return
	 */
	public Vec3D midPts(Vec3D vec1, Vec3D vec2, float weight){  
		float distance = vec1.distanceTo(vec2);
		Vec3D pt = vec2.copy();
		pt.subSelf(vec1);
		pt.normalize();
		pt.scaleSelf(distance*weight);
		pt.addSelf(vec1);

		//p5.strokeWeight(4);
		//p5.stroke(0,255,0);
		//point(pt.x,pt.y,pt.z);
		return pt;
	}

	/**
	 * 
	 * @param vec1
	 * @param vec2
	 * @param vec3
	 * @param vec4
	 * @param nor
	 * @return
	 */
	public Vec3D averageVectors (Vec3D vec1,Vec3D vec2, Vec3D vec3, Vec3D vec4, Vec3D nor){
		Vec3D pt = new Vec3D(0,0,0);
		pt.addSelf(vec1);
		pt.addSelf(vec2);
		pt.addSelf(vec3);
		pt.addSelf(vec4);
		pt.scaleSelf(0.25f);
		pt.addSelf(nor);
		//p5.strokeWeight(10);
		//p5.stroke(0,255,255);
		//point(pt.x,pt.y,pt.z);
		return pt;
	}

	/**
	 * 
	 * @param vec1
	 * @param vec2
	 * @param vec3
	 * @param norScale
	 * @return
	 */
	public Vec3D Normal (Vec3D vec1,Vec3D vec2, Vec3D vec3, float norScale){

		float distance = vec2.distanceTo(vec3);

		Vec3D pVec1 = vec1.copy();
		pVec1.subSelf(vec2);
		pVec1.normalize();
		Vec3D pVec2 = vec1.copy();
		pVec2.subSelf(vec3);
		pVec2.normalize();

		Vec3D pt = pVec1.cross(pVec2);
		pt.normalize();
		pt.scaleSelf(distance*norScale);
		//pt.plus(vec1);

		return pt;
	}

	/**
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param v4
	 */
	public void drawPoly(Vec3D v1, Vec3D v2, Vec3D v3, Vec3D v4){
		p5.beginShape();
		vex(v1);
		vex(v2);
		vex(v3);
		//vex(v4);
		vex(v1);
		p5.endShape();

		p5.beginShape();
		vex(v1);
		//vex(v2);
		vex(v3);
		vex(v4);
		vex(v1);
		p5.endShape();
	}

	/**
	 * 
	 */
	public void drawPoly(){
		p5.beginShape();
		vex(a);
		vex(b);
		vex(c);
		vex(d);
		vex(a);
		p5.endShape();
	}

	/**
	 * 
	 * @param v
	 */
	public void vex(Vec3D v){
		p5.vertex(v.x, v.y, v.z);
	}




}
