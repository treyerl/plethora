import controlP5.*;

import processing.opengl.*;

import plethora.core.*;
import toxi.geom.*;
import peasy.*;

ArrayList <Ple_Agent> boids;

ControlP5 controlP5;
PMatrix3D currCameraMatrix;
PGraphics3D g3; 

PeasyCam cam;

float DIMX = 1200;
float DIMY = 300;
float DIMZ = 300;

int sArray = 20;

 //-------------------------------------------------------------------------------
float DES_SEP;
float DES_COH;
float DES_ALI;

float SEP_SCALE;
float COH_SCALE;
float ALI_SCALE;

int TAIL;
 //-------------------------------------------------------------------------------


void setup() {
  size(1200, 600, OPENGL);
  smooth();

  //initialize camera
  cam = new PeasyCam(this, 100);

  g3 = (PGraphics3D)g;
  //-------------------------------------------------------------------------------
  //initialize interface
  controlP5 = new ControlP5(this);
  controlP5.setColorBackground(color(255, 255, 255, 50)); 
  controlP5.setColorForeground(color(255, 255, 255, 100));
  controlP5.setColorActive(color(255, 255, 255, 200));
  controlP5.setAutoDraw(false);
  //-------------------------------------------------------------------------------
  //initialize interface:
  uiElements();
  //-------------------------------------------------------------------------------
  //initialize agent collection
  boids = new ArrayList <Ple_Agent>();


  //-------------------------------------------------------------------------------
  //BUID AGENTS:
  int pop = 600;
  for (int i = 0; i < pop; i++) {
    Vec3D v = new Vec3D (random(-DIMX/2, DIMX/2), random(-DIMY/2, DIMY/2), random(-DIMZ/2, DIMZ/2));
    Ple_Agent pa = new Ple_Agent(this, v);

    //-------------------------------------------------------------------------------
    //SETUP METHODS:
    Vec3D initialVelocity = new Vec3D (random(-1, 1), random(-1, 1), random(-1, 1));
    pa.setVelocity(initialVelocity);
    pa.initTail(30);
    boids.add(pa);
    //-------------------------------------------------------------------------------
  }
  //-------------------------------------------------------------------------------
}


//LOOP:
void draw() {
  background(0);
  buildBox(DIMX, DIMY, DIMZ);

  hint(ENABLE_DEPTH_TEST);
  cam.setActive(true);

  for (Ple_Agent pa : boids) {

    //-------------------------------------------------------------------------------
    //AGENT METHODS:
    pa.flock(boids, DES_SEP, DES_COH*DES_COH, DES_ALI *DES_ALI, SEP_SCALE, COH_SCALE, ALI_SCALE);
    //pa.flock(boids, 40, 80*80, 50 *50, 1.5, 1, 2);
    pa.update();
    pa.wrapSpace(DIMX/2, DIMY/2, DIMZ/2);


    pa.updateTail(TAIL);

    stroke(0, 255, 169);
    strokeWeight(2);
    pa.displayTailPoints(255, 0, 0, 255, 0, 255, 100, 100, 1, 10);

    strokeWeight(3);
    stroke(255, 0, 0);
    pa.displayPoint();

    strokeWeight(1);
    stroke(255);
    pa.displayDir(pa.vel.magnitude()*5);

    //-------------------------------------------------------------------------------
  }

  //-------------------------------------------------------------------------------
  //DRAW 2D INFO - SLIDERS
  hint(DISABLE_DEPTH_TEST);
  gui();
  //-------------------------------------------------------------------------------
}

//-------------------------------------------------------------------------------
// FUNCTIONS:
//-------------------------------------------------------------------------------
//BUILD SLIDERS HERE:
void uiElements() {
  buildFSlider("DES_SEP", 0, 200, 40);
  buildFSlider("DES_COH", 0, 200, 80);
  buildFSlider("DES_ALI", 0, 200, 50);
  
  sArray += 20;
  
  buildFSlider("SEP_SCALE", 0, 5, 1);
  buildFSlider("COH_SCALE", 0, 5, 1);
  buildFSlider("ALI_SCALE", 0, 5, 1);
  
  sArray += 20;
  
  buildFSlider("TAIL", 1, 20, 8);
}
//-------------------------------------------------------------------------------
//DONT WORRY ABOUT THIS:
void gui() {
  currCameraMatrix = new PMatrix3D(g3.camera);
  camera();
  controlP5.draw();
  g3.camera = currCameraMatrix;
}

void buildBox(float x, float y, float z) {
  noFill();
  stroke(100);
  strokeWeight(1);
  pushMatrix();
  scale(x, y, z);
  box(1);
  popMatrix();
}

void controlEvent(ControlEvent theEvent) {
  cam.setActive(false);
}

void buildFSlider(String name, float min, float max, float def) {
  Controller s1 = controlP5.addSlider(name, min, max, def, 20, sArray, 100, 10);
  sArray += 20;
  s1.setId(1);
  s1.setValue(def);
}

void buildISlider(String name, int min, int max, int def) {
  Controller s1 = controlP5.addSlider(name, min, max, def, 20, sArray, 100, 10);
  sArray += 20;
  s1.setId(1);
  s1.setValue(def);
}

