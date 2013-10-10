//FUTURE LOCATION AND NORMAL TO LINE

import processing.opengl.*;

import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

Ple_Util pu;
Ple_Terrain pTerr;
Ple_Terrain pTerr2;

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

  //b = loadImage("test.jpg");

  sp2 = new Spline3D();
  for (int i = 0; i < 20; i ++) {
    Vec3D v = new Vec3D(-600 + (1200/20*i), random(-50, 50), random(-50, 50));
    sp2.add(v);
  }


  Vec3D origin = new Vec3D(-600, -150, -150);
  Vec3D origin2 = new Vec3D(-600, -150, 20);
  pTerr = new Ple_Terrain(this, origin, 400, 400, 5, 5);
  pTerr2 = new Ple_Terrain(this, origin2, 100, 100, 20, 20);
  // pTerr.noiseHeight(0, 100);
  float [][] heights = pTerr.loadImageToBuffer("1492634_8b.jpg");
  pTerr.loadBufferasHeight(heights, 0  , 100);
  pTerr2.noiseHeight(20, 60);

  boids = new ArrayList <Ple_Agent>();

  buildAgents(100);
}

void draw() {
  background(0);
  lights();
  buildBox(DIMX, DIMY, DIMZ);
  //runAgents();

  stroke(0, 255, 0);
  strokeWeight(2);
  //pTerr.display();
  stroke(255,90);
  strokeWeight(1);
  //pTerr.drawLines(true, true,false);
  stroke(160,40);
  pTerr2.drawLines(true, true,false);

  float [][] heights = pTerr.loadImageToBuffer("1492634_8b.jpg");
  //float [][] angles = pTerr.calcSteepnessMap();
   //pTerr.drawDataMap(angles, 0,0.8);	
   
   pTerr.drawDataMap(heights, 0,255,255,255);
  //pTerr.drawLines(true, false, false);
  //Vec3D [][] vField = pTerr.vectorField2D(angles, 0, TWO_PI);
  stroke(255, 66, 100);
  //pTerr.drawVectorField (vField, -7);
}

