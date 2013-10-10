import peasy.*;
import processing.opengl.*;
import toxi.geom.*;
import toxi.util.*;
import plethora.core.*;


//1 declare plethora camara
Ple_Camera pCam;

void setup(){
  size(800,600, OPENGL);
  
  //2 initialize plethora camara (this, x,y,z,    x,y,z);
  pCam = new Ple_Camera(this,   500,500,0,        0,0,0);
}


void draw(){
  background(235);
  
  //3 update camara Position
  pCam.update();
  //call come of the camera functionallity
  pCam.moveStraightCamera(1,0,0);
  
  
  noFill();
  stroke(0);
  box(600);
}
