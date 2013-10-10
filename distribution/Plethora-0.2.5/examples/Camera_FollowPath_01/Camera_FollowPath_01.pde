import peasy.*;
import processing.opengl.*;
import toxi.geom.*;
import toxi.util.*;
import plethora.core.*;


//1 declare plethora camara
Ple_Camera pCam;

void setup() {
  size(800, 600, OPENGL);

  //2 initialize plethora camara (this, x,y,z,    x,y,z);
  pCam = new Ple_Camera(this, 500, 500, 500, 0, 0, 0);


  //build the camera path, repeat for each point
  pCam.addPointToCameraPath(  new Vec3D  (-2000, 0, 0)  );
  pCam.addPointToCameraPath(  new Vec3D  (-2000, 2000, 0)  );
  pCam.addPointToCameraPath(  new Vec3D  (2000, 2000, 0)  );
}


void draw() {
  background(235);

  //3 update camara Position
  pCam.update();
  //if the path has some points, you can follow the path, camera or target separetly
  //true or false make the path smooth or straight
  pCam.camaraFollowPath(500, true);


  noFill();
  stroke(0);
  box(600);
}

