package plethora.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.IGeometryAttribute;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.PointMaterial;
import ch.fhnw.util.Pair;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

/**
 * Terrain Class based on a grid condition. It contains a data buffer that allows to store different layers of data
 * obtained from images or other sources. This data can be red by Agents (Ple_Agents) or used for height or color.
 * The class is mainly a grid structure for calculations like vector fields (fluids) or path-finding.
 * 
 * Written my Jose Sanchez - 2011
 * for feedback please contact me at: jomasan@gmail.com
 * 
 * @author jomasan
 *
 */

public class Ple_Terrain {
	
	public static class CellCoordinate {
		public final int x, y;
		public CellCoordinate(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	public static class IndexBox {
		public final int minX, minY, maxX, maxY;
		public IndexBox(int minX, int minY, int maxX, int maxY){
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}
	}
	
	IController controller;

	public final int COLS, ROWS;
	public final float cellSizeX, cellSizeY;

	private final IMesh mesh;
	private BoundingBox crop;
	private IMesh points;
	private final List<Integer>[][] zIndices;
	private List<Integer>[][] vColors, linePointZIndices;
	private IMesh lines;
	
	public Ple_Terrain(IController controller){
		this(controller, new ColorMaterial(RGBA.WHITE), 100, 100, 1, 1);
	}
	
	public static Ple_Terrain blankVertices(IController controller, int COLS, int ROWS, float cellSizeX, float cellSizeY){
		float[] vcols = new float[COLS*ROWS*2*3*4];
		for (int i = 0; i < vcols.length; i++) vcols[i] = 1.0f;
		return new Ple_Terrain(controller, vcols, COLS, ROWS, cellSizeX, cellSizeY);
	}

	/**
	 * Creates an IMesh the points of which are in arranged in a 2D grid. Their z and color value can be modified
	 * using the according methods.
	 * 
	 * @param controller IController
	 * @param COLS int
	 * @param ROWS int
	 * @param cellSizeX float
	 * @param cellSizeY float
	 */

	@SuppressWarnings("unchecked")
	public Ple_Terrain(IController controller, IMaterial material, int COLS, int ROWS, float cellSizeX, float cellSizeY){
		this.controller = controller;
		this.COLS = COLS;
		this.ROWS = ROWS;
		this.cellSizeX = cellSizeX;
		this.cellSizeY = cellSizeY;
		zIndices = new LinkedList[ROWS][COLS];
		defineGridIndices(zIndices, 3);
		IGeometry g = DefaultGeometry.createV(Vec3.toArray(createTriangles()));
		mesh = new DefaultMesh(Primitive.TRIANGLES, material, g);
	}
	
	
	@SuppressWarnings("unchecked")
	public Ple_Terrain(IController controller, float[] vertexColors, int COLS, int ROWS, float cellSizeX, float cellSizeY){
		this.controller = controller;
		this.COLS = COLS;
		this.ROWS = ROWS;
		this.cellSizeX = cellSizeX;
		this.cellSizeY = cellSizeY;
		
		zIndices = new LinkedList[ROWS][COLS];
		vColors = new LinkedList[ROWS][COLS];
		
		defineGridIndices(zIndices, 3);
		defineGridIndices(vColors, 4);
		IGeometry g = DefaultGeometry.createVC(Vec3.toArray(createTriangles()), vertexColors);
		mesh = new DefaultMesh(Primitive.TRIANGLES, new ColorMaterial(RGBA.WHITE), g);
	}
	
	/**register the z-coordinates that belong to a grid node
	 * @param i List&lt;Integer&gt;[y][x], indices per grid_node_[y][x], 2 in the corners and at the edges, else 6 
	 * @param n coordinate count, typically 3
	 */
	private void defineGridIndices(List<Integer>[][] i, int n){
		for (int y = 0; y < ROWS-1; y++){
			for (int x = 0; x < COLS-1; x++){
				// x, y
				i[y][x]    .add((y     * 2 * COLS +  x    * 2)     * n + n - 1);
				i[y][x]    .add((y     * 2 * COLS +  x    * 2 + 3) * n + n - 1);
				// x + 1, y
				i[y][x+1]  .add((y     * 2 * COLS + (x+1) * 2)     * n + n - 1);
				// x, y + 1
				i[y+1][x]  .add(((y+1) * 2 * COLS +  x    * 2 + 3) * n + n - 1);
				// x + 1, y + 1
				i[y+1][x+1].add(((y+1) * 2 * COLS + (x+1) * 2)     * n + n - 1);
				i[y+1][x+1].add(((y+1) * 2 * COLS + (x+1) * 2 + 3) * n + n - 1);
			}
		}
	}
	
	private List<Vec3> createTriangles(){
		List<Vec3> triangles = new LinkedList<>();
		for (int y = 0; y < ROWS-1; y++){
			for (int x = 0; x < COLS-1; x++){
				float X = x * cellSizeX;
				float Y = y * cellSizeY;
				// triangle 1, do not close it
				// o
				// |
				// |
				// o - - o
				triangles.add(new Vec3(X, Y , 0));
				triangles.add(new Vec3(X, Y + cellSizeY, 0));
				triangles.add(new Vec3(X + cellSizeX, Y + cellSizeY, 0));
				// triangle 2, do not close it
				// o  o
				//  \ |
				//   \|
				//    o
				triangles.add(new Vec3(X, Y, 0));
				triangles.add(new Vec3(X + cellSizeX, Y + cellSizeY, 0));
				triangles.add(new Vec3(X + cellSizeX, Y, 0));
			}
		}
		return triangles;
	}
	
	/**register the z-coordinates that belong to a line grid node
	 * @param i List&lt;Integer&gt;[y][x], indices per grid_node_[y][x], 2 in the corners, 3 at the edges, else 4 
	 * @param n coordinate count, typically 3
	 */
	private List<Vec3> createLines(List<Integer>[][] i, int n){
		float[] d = mesh.getGeometry().getData()[0];
		List<Integer>[][] z = zIndices;
		List<Vec3> lines = new LinkedList<>();
		for (int y = 0; y < ROWS; y++){
			for (int x = 0; x < COLS; x++){
				boolean lowerEnd = y == ROWS-1, rightEnd = x == COLS-1; 
				if (lowerEnd && rightEnd) break;
				if (!rightEnd){
					// x, y
					i[y][x]  .add((y     * 2 * COLS + x     * 2)     * n + n - 1);
					// x + 1, y
					i[y][x+1].add((y     * 2 * COLS + (x+1) * 2)     * n + n - 1);
					lines.add(new Vec3(x    *cellSizeX, y*cellSizeY, d[z[y][x  ].get(0)]));
					lines.add(new Vec3((x+1)*cellSizeX, y*cellSizeY, d[z[y][x+1].get(0)]));
				}
				if (!lowerEnd){
					// x, y
					i[y][x]  .add((y     * 2 * COLS + x     * 2 + 2) * n + n - 1);
					// x, y + 1
					i[y+1][x].add(((y+1) * 2 * COLS + x     * 2 + 2) * n + n - 1);
					lines.add(new Vec3(x    *cellSizeX, y*cellSizeY, d[z[y][x  ].get(0)]));
					lines.add(new Vec3(x*cellSizeX, (y+1)*cellSizeY, d[z[y+1][x].get(0)]));
				}
			}
		}
		return lines;
	}
	
	/**
	 * define cropping thresholds
	 * @param cX
	 * @param cY
	 * @param cZ
	 * @param cX2
	 * @param cY2
	 * @param cZ2
	 */
	public void crop(float cX, float cY, float cZ, float cX2, float cY2, float cZ2){
		crop = new BoundingBox();
		crop.add(new float[]{cX, cY, cZ, cX2, cY2, cZ2});
	}


	/**
	 * set the height of a point to a value
	 * @param col
	 * @param row
	 * @param z
	 */
	public void setPointZ(int row, int col, float z){
		if (MathUtilities.isInRange(col, 0, COLS - 1) &&
			MathUtilities.isInRange(row, 0, ROWS - 1)){
			setPointZ_(col, row, z);
		}
	}
	
	private void setPointZ_(int row, int col, float z){
		float[] v = mesh.getGeometry().getData()[0];
		for (int i: zIndices[row][col]) v[i] = z;
		
		if (points != null){
			float[] d = points.getGeometry().getData()[0];
			d[(row*COLS+col)*3+2] = z;
		}
		if (lines != null){
			float[] d = lines.getGeometry().getData()[0];
			for(int i: linePointZIndices[row][col]) d[i] = z;
		}
	}
	
	public void updateBoundingBoxPointZ(int row, int col, float z){
		if (crop != null) {
			float x = col*cellSizeX;
			float y = row*cellSizeY;
			if (crop.contains2D(x, y))
				crop.add(x, y, z);
		}
	}
	
	public void setPointColor(int row, int col, RGBA color){
		IGeometryAttribute[] attributes = mesh.getGeometry().getAttributes();
		int colorMapIndex = Arrays.asList(attributes).indexOf(DefaultGeometry.COLOR_ARRAY);
		if (colorMapIndex == -1) 
			throw new IllegalStateException("Must initialize terrain with vertex colors to set point colors");
		if (MathUtilities.isInRange(col, 0, COLS - 1) &&
			MathUtilities.isInRange(row, 0, ROWS - 1)){
			setPointColor_(row, col, color, colorMapIndex);
		}
	}
	
	private void setPointColor_(int row, int col, RGBA color, int colorMapIndex){
		float[] vcols = mesh.getGeometry().getData()[colorMapIndex];
		for (int i: vColors[row][col]) {
			vcols[i-3] = color.r;
			vcols[i-2] = color.g;
			vcols[i-1] = color.b;
			vcols[i] = color.a;
		}
	}

	/**
	 * add some height to a point
	 * @param col
	 * @param row
	 * @param z
	 */
	public void addPointZ(int row, int col, float z){
		if (MathUtilities.isInRange(col, 0, COLS - 1) &&
			MathUtilities.isInRange(row, 0, ROWS - 1)){
			addPointZ_(col, row, z);
		}
	}
	
	private void addPointZ_(int row, int col, float z){
		for (int i: zIndices[row][col])
			mesh.getGeometry().getData()[0][i] += z;
		if (points != null){
			float[] d = points.getGeometry().getData()[0];
			d[(row*COLS+col)*3+2] += z;
		}
		if (lines != null){
			float[] d = lines.getGeometry().getData()[0];
			for(int i: linePointZIndices[row][col]) d[i] += z;
		}
	}
	
	public boolean hasCrop(){
		return crop != null;
	}
	
	public BoundingBox releaseCrop(){
		BoundingBox b = crop;
		crop = null;
		return b;
	}
	
	
	@SuppressWarnings("unchecked")
	public Pair<Integer, Integer>[][] getCroppedIndices(){
		IndexBox b = getIndexBox();
		if ((b.minY < ROWS && b.minX < COLS) || (b.maxY < ROWS && b.maxX < COLS))
			return IntStream.rangeClosed(b.minY, b.maxY)
							.mapToObj(y -> IntStream.rangeClosed(b.minX, b.maxX)
									.mapToObj(x -> new Pair<Integer, Integer>(x, y))
									.toArray())
							.toArray(Pair[][]::new);
		return new Pair[0][0];
	}
	
	public IndexBox getIndexBox(){
		return getIndexBox(crop.getMinX(), crop.getMinY(), crop.getMaxX(), crop.getMaxY());
	}
	
	public IndexBox getIndexBox(float fMinX, float fMinY, float fMaxX, float fMaxY){
		int minX = Math.max((int) (fMinX/cellSizeX), 0);
		int maxX = Math.min((int) Math.ceil(fMaxX/cellSizeX), COLS);
		int minY = Math.max((int) (fMinY/cellSizeY), 0);
		int maxY = Math.min((int) Math.ceil(fMaxY/cellSizeY), ROWS);
		return new IndexBox(minX, minY, maxX, maxY);
	}
	
	public IMesh getPoints(){
		if (points == null){
			float[] d = mesh.getGeometry().getData()[0];
			float[] p = new float[ROWS*COLS*3];
			int c = 0;
			for (int y = 0; y < ROWS; y++){
				for (int x = 0; x < COLS; x++){
					int i = zIndices[y][x].get(0);
					p[c++] = d[i-2];
					p[c++] = d[i-1];
					p[c++] = d[i];
				}
			}
			points = new DefaultMesh(Primitive.POINTS, new PointMaterial(RGBA.BLUE, 10), DefaultGeometry.createV(p), Queue.OVERLAY);
		}
		return points;
	}
	
	public Vec3 getPoint(int row, int col){
		float[] d = mesh.getGeometry().getData()[0];
		int i = zIndices[row][col].get(0);
		return new Vec3(d[i-2], d[i-1], d[i]);
	}

	/**
	 * calculates if point is in cropping thresholds
	 * @param v
	 * @return
	 */
	public boolean checkCrop (Vec3 v){
		return crop.contains(v);
	}
	
	public void setZNoise(float zMin, float zMax){
		forEachNode((x, y) -> setPointZ_(y, x, MathUtilities.random(zMin, zMax)));
	}
	
	public void addZNoise(float zMin, float zMax){
		forEachNode((x, y) -> addPointZ_(y, x, MathUtilities.random(zMin, zMax)));
	}
	
	public void forEachNode(BiConsumer<Integer, Integer> bc){
		int yTop = ROWS, xTop = COLS, y = 0, x = 0;
		if (crop != null){
			IndexBox b = getIndexBox();
			x = b.minX;
			y = b.minY;
			xTop = b.maxX;
			yTop = b.maxY;
		}
		for (; y < yTop; y++){
			for (; x < xTop; x++){
				bc.accept(x, y);
			}
		}
	}
	
	public IMesh getLines(){
		return getLines(new ColorMaterial(RGBA.BLACK));
	}
	
	@SuppressWarnings("unchecked")
	public IMesh getLines(IMaterial material){
		if (lines == null){
			linePointZIndices = new List[ROWS][COLS];
			IGeometry g = DefaultGeometry.createV(Vec3.toArray(createLines(linePointZIndices, 3)));
			lines = new DefaultMesh(Primitive.LINES, material, g, IMesh.Queue.OVERLAY, IMesh.NO_FLAGS);
		}
		return lines;
	}

	/**
	 * increase or decrease the values of a data-map gradually. (good for stigmergy)
	 * @param data
	 * @param value
	 * @param upperThreshold
	 * @param lowerThreshold
	 * @return
	 */
	public float [][] fadeDataMap(float [][] data, float value, float upperThreshold, float lowerThreshold){
		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {

				data[i][j] += value;

				if(data[i][j] >= upperThreshold){
					data[i][j] = upperThreshold;
				}
				if(data[i][j] <= lowerThreshold){
					data[i][j] = lowerThreshold;
				}
			}
		}

		return data;
	}
	
	// TODO add image as texture - why loading data map from / Vertex colors from image?

	/**
	 * create heights from data-map
	 * @param data float[][]
	 * @param min float
	 * @param max float
	 */
	public void loadBufferasHeight(float [][] data, float min, float max){
		float[] d = mesh.getGeometry().getData()[0];
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				float height = MathUtilities.map(data[i][j], 0, 255, min, max);
				for(int idx: zIndices[j][i]) d[idx] = height;
			}
		}
	}

	/**
	 * generates a data-map of the angle of inclination
	 * @param from
	 * @param to
	 * @return
	 */
	public float[][] calcSteepnessMap(){

		float[][] fieldAngles = new float[COLS-1][ROWS-1];
		float[] d = mesh.getGeometry().getData()[0];
		for (int i = 0; i < COLS-1; i++) {
			for (int j = 0; j < ROWS-1; j++) {
				int I = zIndices[i][j].get(0);
				Vec3 p1 = new Vec3(d[I-2], d[I-1], d[I]);
				int J = zIndices[i+1][j+1].get(0);
				Vec3 p3 = new Vec3(d[J-2], d[J-1], d[J]);

				fieldAngles[i][j] = pitchFromAToB(p1,p3);

			}
		}
		return fieldAngles;
	}


	/**
	 * calculates angles of inclination
	 * @param a
	 * @param b
	 * @return
	 */
	public float pitchFromAToB(Vec3 a, Vec3 b){
		return b.subtract(a).angle(new Vec3(b.x,b.y, a.z).subtract(a));
	}

	public CellCoordinate getCellIndex(Vec3 v){
		// vector in terrain coordinate space
		Vec3 tv = v.subtract(mesh.getPosition());
		// constrain x and y indices to COLS/ROWS
		int x = (int) MathUtilities.clamp(tv.x / cellSizeX, 0, COLS-1);
		int y = (int) MathUtilities.clamp(tv.y / cellSizeY, 0, ROWS-1);
		return new CellCoordinate(x, y);
	}
	
	

}
