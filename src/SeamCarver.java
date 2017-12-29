import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
	private Picture pic;
	private int w, h;
	private double[][] energyArray;
		
	public SeamCarver(Picture picture) {                // create a seam carver object based on the given picture
		pic = new Picture(picture);
		w = pic.width();
		h = pic.height();
		
		createEnergyArray();
	}
	
	private void createEnergyArray() {
		energyArray = new double[w][h];
		for(int i=0;i<w;i++) {
			for(int j=0;j<h;j++) {
				if(i==0 || i==w-1 || j==0 || j==h-1) {
					energyArray[i][j] = 1000;
				}
				else {
					double SquareDeltaX = getSquareDeltaX(i,j);
					double SquareDeltaY = getSquareDeltaY(i,j);
					energyArray[i][j] = Math.sqrt(SquareDeltaX+SquareDeltaY);
				}
			}
		}
		
	}
	
	private double getSquareDeltaX(int x, int y) {
		Color left = pic.get(x-1, y);
		Color right = pic.get(x+1, y);
		int redDiffer = left.getRed()-right.getRed();
		int greenDiffer = left.getGreen()-right.getGreen();
		int blueDiffer = left.getBlue()-right.getBlue();
		
		return redDiffer*redDiffer + greenDiffer*greenDiffer + blueDiffer*blueDiffer;
	}
	
	private double getSquareDeltaY(int x, int y) {
		Color upper = pic.get(x, y-1);
		Color bottom = pic.get(x, y+1);
		int redDiffer = upper.getRed()-bottom.getRed();
		int greenDiffer = upper.getGreen()-bottom.getGreen();
		int blueDiffer = upper.getBlue()-bottom.getBlue();
		
		return redDiffer*redDiffer + greenDiffer*greenDiffer + blueDiffer*blueDiffer;
	}
	
	public Picture picture() {                          // current picture
		return new Picture(pic);
	}
	
	public int width() {                            // width of current picture
		return w;
	}	
		
	public int height() {                          // height of current picture
		return h;
	}	
		
	public  double energy(int x, int y) {               // energy of pixel at column x and row y
		if(x<0 || x>=w || y<0 || y>=h) { 
			throw new IllegalArgumentException();
		}
		else{
			return energyArray[x][y];
		}
			
	}	
	
	public int[] findHorizontalSeam() {               // sequence of indices for horizontal seam
		double[][] backup = energyArray;
		energyArray = transpose(energyArray);
		int temp = w;
		w = h;
		h = temp;
		int[] res = findVerticalSeam();
		energyArray = backup;
		h = w;
		w = temp;
		return res;
	}
	
	private double[][] transpose(double[][] energyArray) {
		double[][] transpose = new double[h][w];
		for(int i=0;i<w;i++) {
			for(int j=0;j<h;j++) {
				transpose[j][i] = energyArray[i][j];
			}
		}
		return transpose;
	}
	
    public int[] findVerticalSeam() {                 // sequence of indices for vertical seam
    		
    		double[][] distTo = new double[w][h];
    		int[][] pathTo = new int[w][h];
    		
    		for(int i=0;i<w;i++) {
    			for(int j=0;j<h;j++) {
    				if(j==0) distTo[i][j] = 1000;
    				else distTo[i][j] = Double.MAX_VALUE;
    			}
    		}
    		
    		// relax
    		for(int j=0;j<=h-2;j++) {
    			for(int i=0;i<w;i++) {
    				
    				// relax bottom-left [i-1][j+1]
    				if( i-1>=0 && distTo[i][j]+energyArray[i-1][j+1]<distTo[i-1][j+1] ) {
    					distTo[i-1][j+1] = distTo[i][j]+energyArray[i-1][j+1];
    					pathTo[i-1][j+1] = i;
    				}
    				// relax bottom-middle [i][j+1]
    				if( distTo[i][j]+energyArray[i][j+1]<distTo[i][j+1] ) {
    					distTo[i][j+1] = distTo[i][j]+energyArray[i][j+1];
    					pathTo[i][j+1] = i;
    				}
    				// relax bottom-right [i+1][j+1]
    				if( i+1<w && distTo[i][j]+energyArray[i+1][j+1]<distTo[i+1][j+1] ) {
    					distTo[i+1][j+1] = distTo[i][j]+energyArray[i+1][j+1];
    					pathTo[i+1][j+1] = i;
    				}
    			}
    		}
    		// find the min path
    		int[] res = new int[h];   
    		res[h-1] = 0;
    		double min = distTo[0][h-1];
    		for(int i=1;i<w;i++) {
    			if( distTo[i][h-1]<min ) {
    				min = distTo[i][h-1];
    				res[h-1] = i;
    			}
    		}
    		for(int i=h-2;i>=0;i--) {
    			res[i] = pathTo[res[i+1]][i+1];
    		}
    		return res;
    }
    
    public void removeHorizontalSeam(int[] seam) {   // remove horizontal seam from current picture
    		if(seam==null || h==1 || seam.length!=w) throw new IllegalArgumentException();
    		for(int i=1;i<w;i++) {
    			if( Math.abs(seam[i]-seam[i-1])>1 )
    				throw new IllegalArgumentException();
    		}
    		
    		Picture updatedPic = new Picture( w,h-1 );
    		for(int i=0;i<w;i++) {
    			for(int j=0;j<seam[i];j++) {
    				updatedPic.set(i, j, pic.get(i, j));
    			}
    		}
    		for(int i=0;i<w;i++) {
    			for(int j=seam[i]+1;j<h;j++) {
    				updatedPic.set(i,j-1,pic.get(i, j));
    			}
    		}
    		
    		pic = updatedPic;
    		w = pic.width();
    		h = pic.height();
    		
    		createEnergyArray();
    		
	}
    public void removeVerticalSeam(int[] seam) {     // remove vertical seam from current picture
    		if(seam==null || w==1 || seam.length!=h) throw new IllegalArgumentException();
    		for(int i=1;i<h;i++) {
    			if( Math.abs(seam[i]-seam[i-1])>1 )
    				throw new IllegalArgumentException();
    		}
    		
    		Picture updatedPic = new Picture( w-1,h );
    		for(int j=0;j<h;j++) {
    			for(int i=0;i<seam[j];i++) {
    				updatedPic.set(i, j, pic.get(i, j));
    			}
    		}
    		for(int j=0;j<h;j++) {
    			for(int i=seam[j]+1;i<w;i++) {
    				updatedPic.set(i-1,j,pic.get(i, j));
    			}
    		}
    		
    		pic = updatedPic;
    		w = pic.width();
    		h = pic.height();
    		
    		createEnergyArray();
    }
    
}
