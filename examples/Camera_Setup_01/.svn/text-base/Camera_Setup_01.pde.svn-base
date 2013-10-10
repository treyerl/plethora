import peasy.*;
import processing.opengl.*;
import toxi.geom.*;
import toxi.util.*;
import plethora.core.*;


//1 declare plethora camara
Ple_Camara pCam;

void setup(){
  size(800,600, OPENGL);
  
  //2 initialize plethora camara (this, x,y,z,    x,y,z);
  pCam = new Ple_Camara(this, 500,500,500    ,0,0,0);
}


void draw(){
  background(0);
  
  //3 update camara Position
  pCam.update();
  //call come of the camera functionallity
  pCam.moveStraightCamera(1,0,0);
  
  
  noFill();
  stroke(255);
  box(600);
}
