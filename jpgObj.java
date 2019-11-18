import java.awt.image.BufferedImage;
import java.io.Serializable;

public class JpgObj implements Serializable
{
	private static int imageWidth;
	private static int imageHeight;
	private static float ratio;
	private static int[] Y;
	private static int[][] yMatrix;
	private static int[] U;
	private static int[][] uMatrix;
	private static int[] V;
	private static int[][] vMatrix;
	private static java.io.File file;
	private static BufferedImage image;
	
	public JpgObj()
	{
		
	}
	
	/**
	 * This function sets the width of the image obj
	 * @param width The number of pixels the image is wide
	 */
	public void setImageWidth(int width)
	{
		imageWidth = width;
	}
	
	/**
	 * This function gets the width of the image obj
	 */
	public int getImageWidth()
	{
		return imageWidth;
	}
	
	/**
	 * This function sets the height of the image obj
	 * @param height The number of pixels the image is high
	 */
	public void setImageHeight(int height)
	{
		imageHeight = height;
	}
	
	/**
	 * This function gets the height of the image obj
	 */
	public int getImageHeight()
	{
		return imageHeight;
	}
	
	/**
	 * This function sets the Y array
	 * @param array The contents of Y
	 */
	public void setY(int[] array)
	{
		Y = array;
	}
	
	/**
	 * This function gets the Y array
	 */
	public int[] getY()
	{
		return Y;
	}
	
	/**
	 * This function sets the U array
	 * @param array The contents of U
	 */
	public void setU(int[] array)
	{
		U = array;
	}
	
	/**
	 * This function gets the U array
	 */
	public int[] getU()
	{
		return U;
	}
	
	/**
	 * This function sets the V array
	 * @param array The contents of V
	 */
	public void setV(int[] array)
	{
		V = array;
	}
	
	/**
	 * This function gets the V array
	 */
	public int[] getV()
	{
		return V;
	}
	
	/**
	 * This function sets the Y matrix
	 * @param array The contents of matrix
	 */
	public void setYMatrix(int[][] array)
	{
		yMatrix = array;
	}
	
	/**
	 * This function gets the matrix
	 */
	public int[][] getYMatrix()
	{
		return yMatrix;
	}
	
	/**
	 * This function sets the U matrix
	 * @param array The contents of matrix
	 */
	public void setUMatrix(int[][] array)
	{
		uMatrix = array;
	}
	
	/**
	 * This function gets the matrix
	 */
	public int[][] getUMatrix()
	{
		return uMatrix;
	}

	/**
	 * This function sets the V matrix
	 * @param array The contents of matrix
	 */
	public void setVMatrix(int[][] array)
	{
		vMatrix = array;
	}
	
	/**
	 * This function gets the matrix
	 */
	public int[][] getVMatrix()
	{
		return vMatrix;
	}
	
	/**
	 * This function sets the file
	 * @param f The file
	 */
	public void setFile(java.io.File f)
	{
		file = f;
	}
	
	/**
	 * This function gets the file
	 */
	public java.io.File getFile()
	{
		return file;
	}
	
	/**
	 * This function sets the stored image file
	 * @param img The image to store
	 */
	public void setImage(BufferedImage img)
	{
		image = img;
	}
	
	/**
	 * This function gets the stored image file
	 */
	public BufferedImage getImage()
	{
		return image;
	}
	
	/**
	 * This function gets the image scale ratio
	 */
	public float getRatio()
	{
		return ratio;
	}
	
	/**
	 * This function sets the image scale ratio
	 * @param r The ratio
	 */
	public void setRatio(float r)
	{
		ratio = r;
	}
}