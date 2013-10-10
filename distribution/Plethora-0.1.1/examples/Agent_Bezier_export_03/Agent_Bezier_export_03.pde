//FUTURE LOCATION AND NORMAL TO LINE

import processing.opengl.*;
import javax.media.opengl.*;

import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

Ple_Util pu;
Ple_Terrain pTerr;

Spline3D sp2;

PeasyCam cam;

float DIMX = 1200;
float DIMY = 800;
float DIMZ = 200;

GL gl;
PGraphicsOpenGL pgl;

void setup() {
  size(1200, 600, OPENGL);
  //size(1200, 600, P3D);
  smooth();
  cam = new PeasyCam(this, 100);

  pu = new Ple_Util(this);

  //b = loadImage("test.jpg");

  sp2 = new Spline3D();
  for (int i = 0; i < 20; i ++) {
    Vec3D v = new Vec3D(-600 + (1200/20*i), random(-50, 50), random(-50, 50));
    sp2.add(v);
  }


  Vec3D origin = new Vec3D(-600, -150, -150);
  pTerr = new Ple_Terrain(this, origin, 240, 60, 5, 5);
  // pTerr.noiseHeight(0, 100);
  //float [][] heights = pTerr.loadImageToBuffer("test3.jpg");
  //pTerr.loadBufferasHeight(heights, -150  , 100);
  //pTerr.noiseHeight(0, 30);

  boids = new ArrayList <Ple_Agent>();

  buildAgents(100);

  /**
   * OPENGL initialize
   */
 // colorMode( RGB, 1.0f );
 // hint( ENABLE_OPENGL_4X_SMOOTH );
  pgl         = (PGraphicsOpenGL) g;
  gl          =  pgl.gl;
}

void draw() {
  background(0);
  println(frameCount);

  //gl.glDepthMask(false);
  gl.glEnable( GL.GL_BLEND );
  gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE );
  //hint(ENABLE_DEPTH_SORT);

  buildBox(DIMX, DIMY, DIMZ);
  runAgents();

  stroke(0, 255, 0);
  strokeWeight(2);
  //pTerr.display();
  strokeWeight(1);

  //float [][] heights = pTerr.loadImageToBuffer("test.jpg");
  //float [][] angles = pTerr.calcSteepnessMap();
  //pTerr.drawDataMap(angles, 0,0.8);	
  //pTerr.drawDataMap(heights, 0,255, 100,255);
  //pTerr.drawLines(true, false, false);
  //Vec3D [][] vField = pTerr.vectorField2D(angles, 0, TWO_PI);
  stroke(255, 66, 100);
  //pTerr.drawVectorField (vField, -7);
}


void keyPressed(){
 
 if(key == 'i'){
  pu.createTailFile(boids, "TailInfo"); 
 }
  
}


