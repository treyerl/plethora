//FUTURE LOCATION AND NORMAL TO LINE

import processing.opengl.*;

import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

Ple_Util pu;
Ple_Terrain pTerr;

Spline3D sp2;

PeasyCam cam;

float DIMX = 1200;
float DIMY = 300;
float DIMZ = 300;

void setup() {
  size(1200, 600, OPENGL);
  //size(1200, 600, P3D);
  smooth();
  cam = new PeasyCam(this, 100);

  pu = new Ple_Util(this);



  sp2 = new Spline3D();
  for (int i = 0; i < 20; i ++) {
    Vec3D v = new Vec3D(-600 + (1200/20*i), random(-50, 50), random(-50, 50));
    sp2.add(v);
  }


  Vec3D origin = new Vec3D(-600, -150, -150);
  pTerr = new Ple_Terrain(this, origin, 120, 30, 10, 10);
  pTerr.noiseHeight(0, 100);

  boids = new ArrayList <Ple_Agent>();

  buildAgents(100);
}

void draw() {
  background(0);
  buildBox(DIMX, DIMY, DIMZ);
  runAgents();

  stroke(0, 255, 0);
  strokeWeight(2);
  pTerr.display();
  strokeWeight(1);
  
  float [][] angles = pTerr.calcSteepnessMap();
 // pTerr.drawDataMap(angles, 0,0.8);	
  //pTerr.drawLines(true, false, false);
  Vec3D [][] vField = pTerr.vectorField2D(angles, 0,2);
  stroke(255, 66, 100);
  pTerr.drawVectorField (vField, -7);

}

