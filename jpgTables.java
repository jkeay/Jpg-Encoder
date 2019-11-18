import java.awt.*;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;

public class JPEGWindow extends JFrame
{
	// Variables
	private static JPEGWindow window;
	private final int WINDOW_WIDTH = 700;
	private final int WINDOW_HEIGHT = 500;
	private final int height = 256;
	private final int width = 256;
	private static JPanel bottomPanel;
	private static JPanel centerPanel;
	private static JPanel topPanel;
	private JButton openButton;
	private JButton saveButton;
	private JButton yuvButton;
	private JButton chromaButton;
	private JButton dctButton;
	private JButton quantButton;
	private JButton quantInvButton;
	private JButton invdctButton;
	private JButton rgbButton;
	private JLabel text;
	private JLabel imageLabel;
	private JPanel titlePanel;
	private JTable tableA;
	private JTable tableB;
	BufferedImage image = null;
	BufferedImage editedImage = null;
	BufferedImage cube = null;
	java.io.File file;
	public static int imageWidth;
	public static int imageHeight;
	public static int[] Y;
	public static int[][] yMatrix;
	public static int[] U;
	public static int[][] uMatrix;
	public static int[] V;
	public static int[][] vMatrix;
	JpgObj img = new JpgObj();
	JpgTables tables = new JpgTables();
	
	public static void main(String args[])
	{
		window = new JPEGWindow();
	}
	
	public JPEGWindow()
	{
		setTitle("JPEG Encoder");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel innerPanel = new JPanel();
		setLayout(new GridLayout(2,0));
		
		innerPanel.setLayout(new BorderLayout());
		String[][] dataA = new String[8][8];
		String[][] dataB = new String[8][8];
		String[][] dataNames = new String[8][8];
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				dataA[i][j] = "0";
				dataB[i][j] = tables.getLumTable()[i][j] + "";
				dataNames[i][j] = " ";
			}
		}
		
		tableA = new JTable(dataA, dataNames);
		tableA.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableA.setTableHeader(null);
		tableA.setGridColor(Color.BLACK);
		tableA.setEnabled(false);
		
		tableB = new JTable(dataB, dataNames);
		tableB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableB.setTableHeader(null);
		tableB.setGridColor(Color.BLACK);
		tableB.setEnabled(false);
		
		for(int i=0;i<8;i++)
		{
			tableA.getColumnModel().getColumn(i).setPreferredWidth(30);
			tableB.getColumnModel().getColumn(i).setPreferredWidth(30);
		}
		
		JPanel grids = new JPanel();
		grids.setLayout(new GridLayout(0,2));
		JScrollPane scrollA = new JScrollPane(tableA);
		JScrollPane scrollB = new JScrollPane(tableB);
		
		grids.add(scrollA);
		grids.add(scrollB);
		add(innerPanel);
		add(grids);
		
		bottomPanel = buildBottomPanel();
		innerPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		centerPanel = buildPanel();
		centerPanel.setLayout(new BorderLayout());
		titlePanel = new JPanel();
		centerPanel.add(titlePanel, BorderLayout.NORTH);
		innerPanel.add(centerPanel, BorderLayout.CENTER);
		
		topPanel = buildPanel();
		topPanel.setBackground(Color.WHITE);
		text = new JLabel("JPEG Encoder");
		topPanel.add(new JLabel(""), BorderLayout.CENTER);
		topPanel.add(text, BorderLayout.EAST);
		text.setAlignmentX(RIGHT_ALIGNMENT);
		innerPanel.add( topPanel, BorderLayout.NORTH);
		
		setVisible(true);
	}
	
	public JPanel buildPanel()
	{
		JPanel panel = new JPanel();
		return panel;
	}
	
	public JPanel buildBottomPanel()
	{
		JPanel panel = new JPanel();
		
		openButton = new JButton("Open");
		openButton.setActionCommand("open");
		openButton.addActionListener(new ButtonListener());
		saveButton = new JButton("Save");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(new ButtonListener());
		yuvButton = new JButton("YUV");
		yuvButton.setActionCommand("yuv");
		yuvButton.addActionListener(new ButtonListener());
		chromaButton = new JButton("4:2:0");
		chromaButton.setActionCommand("chroma");
		chromaButton.addActionListener(new ButtonListener());
		dctButton = new JButton("DCT");
		dctButton.setActionCommand("dct");
		dctButton.addActionListener(new ButtonListener());
		quantButton = new JButton("Quantization");
		quantButton.setActionCommand("quant");
		quantButton.addActionListener(new ButtonListener());
		quantInvButton = new JButton("Inv Quantization");
		quantInvButton.setActionCommand("quantInv");
		quantInvButton.addActionListener(new ButtonListener());
		invdctButton = new JButton("Inverse DCT");
		invdctButton.setActionCommand("invdct");
		invdctButton.addActionListener(new ButtonListener());
		rgbButton = new JButton("RGB");
		rgbButton.setActionCommand("rgb");
		rgbButton.addActionListener(new ButtonListener());
		
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.WHITE);
		
		panel.add(openButton);
		panel.add(saveButton);
		panel.add(new JLabel("               ")); // spacer
		panel.add(yuvButton);
		panel.add(chromaButton);
		panel.add(dctButton);
		panel.add(quantButton);
		panel.add(quantInvButton);
		panel.add(invdctButton);
		panel.add(rgbButton);
		
		yuvButton.setEnabled(false);
		chromaButton.setEnabled(false);
		dctButton.setEnabled(false);
		quantButton.setEnabled(false);
		quantInvButton.setEnabled(false);
		invdctButton.setEnabled(false);
		rgbButton.setEnabled(false);
		
		return panel;
	}
	
	private class MouseClickListener implements MouseListener
	{
		public void mouseClicked(MouseEvent e) 
		{
			int x = e.getX();
			int y = e.getY();
			int yBorder = (imageLabel.getHeight() - img.getImageHeight())/2;
			int xBorder = (imageLabel.getWidth() - img.getImageWidth())/2;
			y -= yBorder;
			x -= xBorder;
			
			if(x >= 0 && x < img.getImageWidth() && y >= 0 && y < img.getImageHeight())
			{
				tables.setX(x);
				tables.setY(y);
			
				x = (int)(x / img.getRatio());
				y = (int)(y / img.getRatio());
			
				for(int i=0;i<8;i++)
				{
					for(int j=0;j<8;j++)
						tableA.getModel().setValueAt(""+yMatrix[y+i][x+j], i, j);
				}
			}
			
			text.setText("Selected 8x8 Cube");
		}

		public void mousePressed(MouseEvent e) 
		{
		}

		public void mouseReleased(MouseEvent e) 
		{
		}

		public void mouseEntered(MouseEvent e)
		{	
		}

		public void mouseExited(MouseEvent e) 
		{
		}
	}
	
	private class ButtonListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("open"))
			{
				try
				{
					readImage();
				}
				catch(IOException error)
				{
					
				}
			}
			else if(e.getActionCommand().equals("save"))
			{
				text.setText("Image Saved");
				try 
				{
					saveImage();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			else if(e.getActionCommand().equals("yuv"))
			{
				text.setText("Image Converted to YUV");
				convertYUV();
			}
			else if(e.getActionCommand().equals("chroma"))
			{
				text.setText("4:2:0 Chroma Subsampling Performed");
				try 
				{
					chromaSubSampling();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				
				chromaButton.setEnabled(false);
				dctButton.setEnabled(true);
			}
			else if(e.getActionCommand().equals("dct"))
			{
				text.setText("DCT Table Performed");
				try 
				{
					DCT();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			else if(e.getActionCommand().equals("quant"))
			{
				int selected = -1;
				String[] choices = {"Standard", "Low Constant", "High Constant", "High"};
				selected = JOptionPane.showOptionDialog(null, "Choose a Table", "Quantization", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				tables.setSelectedTable(selected);
				
				for(int i=0;i<8;i++)
				{
					for(int j=0;j<8;j++)
						tableB.setValueAt(""+tables.getLumTable()[i][j], i, j);
				}
				
				try 
				{
					quantization();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				
				text.setText("Quantization Performed " + tables.getChosenTable());
			}
			else if(e.getActionCommand().equals("quantInv"))
			{
				text.setText("Inv Quantization Performed");
				try 
				{
					quantizationInv();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			else if(e.getActionCommand().equals("invdct"))
			{
				text.setText("Inverse DCT Performed");
				try
				{
					inverseDCT();
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
			else if(e.getActionCommand().equals("rgb"))
			{
				text.setText("Image Converted back to RGB");
				try 
				{
					convertRGB();
				} catch (IOException e1) 
				{
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void readImage() throws IOException
	{
		JFileChooser chooser = new JFileChooser();
		
		try
		{
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				if(image != null)
				{
					centerPanel.removeAll();
					centerPanel.updateUI();
				}
				
				java.io.File file = chooser.getSelectedFile();
				img.setFile(file);
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				image = ImageIO.read(file);
				float ratio = image.getWidth() * 256/image.getHeight();
				img.setRatio(ratio/image.getWidth());
				editedImage = image;
				imageWidth = (int)(ratio);
				imageHeight = 256;
				
				img.setImageWidth(imageWidth);
				img.setImageHeight(imageHeight);
				img.setImage(editedImage);
				
				imageLabel = new JLabel(new ImageIcon(editedImage.getScaledInstance(imageWidth, 256, 0)));
				window.centerPanel.add(new JLabel(new ImageIcon(image.getScaledInstance(imageWidth, 256, 0))), BorderLayout.WEST);
				
				window.centerPanel.add(imageLabel, BorderLayout.EAST);
				window.pack();
				text.setText("Opened: " + file.getName());
				titlePanel.add(new JLabel("Original                                            "), BorderLayout.WEST);
				titlePanel.add(new JLabel("                                            Original"), BorderLayout.EAST);
				yuvButton.setEnabled(true);
			}
		}
		catch(IOException e)
		{
			System.out.println("Error:" + e);
		}
	}
	
	public void saveImage() throws IOException
	{
		JFileChooser save = new JFileChooser();
		int saveValue = save.showSaveDialog(null);
        if(saveValue == JFileChooser.APPROVE_OPTION)
        {
            try 
            {
            	ImageIO.write(img.getImage(), "jpg", save.getSelectedFile());
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
	}
	
	public void convertYUV()
	{		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int Y[] = new int[width * height];
		int U[] = new int[width * height];
		int V[] = new int[width * height];
    	int values[] = new int[width * height];
    	PixelGrabber grabber = new PixelGrabber(editedImage.getSource(), 0, 0, width, height, values, 0, width);
        
    	try
    	{
    		grabber.grabPixels();
        } 
    	catch (InterruptedException e)
    	{
    	}
        
    	int R, G, B;
    	
    	for(int i=0;i<values.length;i++)
    	{
    		R = (values[i] & 0x00ff0000) >> 16;
        	G = (values[i] & 0x0000ff00) >> 8;
        	B = values[i] & 0x000000ff;
    		Y[i] = (int)((0.299 * R) + (0.587 * G) + (0.114 * B));
    		U[i] = (int)((-0.14713 * R) + (-0.28886 * G) + (0.436 * B));
    		V[i] = (int)((0.615 * R) + (-0.51499 * G) + (-0.10001 * B));
    	}
    	   	
    	yMatrix = makeMatrix(Y, yMatrix);
    	uMatrix = makeMatrix(U, uMatrix);
    	vMatrix = makeMatrix(V, vMatrix);
    	
    	img.setY(Y);
    	img.setU(U);
    	img.setV(V);
    	img.setYMatrix(yMatrix);
    	img.setUMatrix(uMatrix);
    	img.setVMatrix(vMatrix);
    	    	
        editedImage = new BufferedImage(img.getImage().getWidth(), img.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster imgRaster = (WritableRaster) editedImage.getData();
    	imgRaster.setPixels(0, 0, img.getImage().getWidth(), img.getImage().getHeight(), Y);
    	editedImage.setData(imgRaster);
    	img.setImage(editedImage);
    	centerPanel.remove(imageLabel);
    	imageLabel = new JLabel(new ImageIcon(editedImage.getScaledInstance(img.getImageWidth(), 256, 0)));
    	imageLabel.addMouseListener(new MouseClickListener());
    	centerPanel.add(imageLabel);
		titlePanel.removeAll();
		titlePanel.add(new JLabel("Original                          "), BorderLayout.WEST);
		titlePanel.add(new JLabel("                                            Y DCT Greyscaled"), BorderLayout.EAST);
		yuvButton.setEnabled(false);
		chromaButton.setEnabled(true);
	}
	
	public void chromaSubSampling() throws IOException
	{
		int blocksWide = (int)(image.getWidth() / 8)*8;
		int blocksHigh = (int)(image.getHeight() / 8)*8;
		
		for(int i=0;i<blocksHigh;i+=2)
		{
			for(int j=0;j<blocksWide;j+=2)
			{
				for(int k=0;k<2;k++)
				{
					for(int l=0;l<2;l++)
					{
						img.getUMatrix()[i+k][j+l] = img.getUMatrix()[i][j];
						img.getVMatrix()[i+k][j+l] = img.getVMatrix()[i][j];
					}
				}
			}
		}
		
		img.setY(breakMatrix(img.getY(), img.getYMatrix()));
		img.setU(breakMatrix(img.getU(), img.getUMatrix()));
		img.setV(breakMatrix(img.getV(), img.getVMatrix()));
		
		chromaButton.setEnabled(false);
		dctButton.setEnabled(true);
	}
	
	public int[][] makeMatrix( int[] array, int[][] matrix)
	{
		int index = 0;
		matrix = new int[img.getImage().getHeight()][img.getImage().getWidth()];
		for(int i=0;i<img.getImage().getHeight();i++)
		{
			for(int j=0;j<img.getImage().getWidth();j++)
			{
				matrix[i][j] = array[index];
				index++;
			}
		}
		return matrix;
	}
	
	public int[] breakMatrix( int[] array, int[][] matrix)
	{
		int index = 0;
		array = new int[img.getImage().getWidth() * img.getImage().getHeight()];
		for(int i=0;i<img.getImage().getHeight();i++)
		{
			for(int j=0;j<img.getImage().getWidth();j++)
			{
				array[index] = matrix[i][j];
				index++;
			}
		}
		return array;
	}
	
	public void convertRGB() throws IOException
	{
		int[] rgb = new int[img.getImage().getHeight() * img.getImage().getWidth()];
		int[] R = new int[img.getImage().getHeight() * img.getImage().getWidth()];
		int[] G = new int[img.getImage().getHeight() * img.getImage().getWidth()];
		int[] B = new int[img.getImage().getHeight() * img.getImage().getWidth()];
		
		editedImage = new BufferedImage(img.getImage().getWidth(), img.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
		editedImage = ImageIO.read(img.getFile());
		int w = 0;
		int h = 0;
		
		for(int i=0;i<img.getImage().getHeight() * img.getImage().getWidth();i++)
		{
			R[i] = (int)(img.getY()[i] + (1.13983*img.getV()[i]));
			G[i] = (int)(img.getY()[i] - (0.39465*img.getU()[i]) - (0.58060*img.getV()[i]));
			B[i] = (int)(img.getY()[i] + (2.03211*img.getU()[i]));
			
			rgb[i] = (R[i] << 16) | (G[i] << 8) | (B[i]<< 0);
			
			if(h >= img.getImage().getHeight()-1 && w >= img.getImage().getWidth()-1)
				break;
			
			editedImage.setRGB(w, h, rgb[i]);
			
			if(w >= img.getImage().getWidth()-1)
			{
				w = 0;
				h++;
			}
			else
				w++;
		}
		
		window.centerPanel.removeAll();
		window.centerPanel.validate();
		window.centerPanel.repaint();
		
		image = new BufferedImage(img.getImage().getWidth(), img.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
		image = ImageIO.read(img.getFile());
		window.centerPanel.add(new JLabel(new ImageIcon(image.getScaledInstance(img.getImageWidth(), 256, 0))), BorderLayout.WEST);
		
		img.setImage(editedImage);
		centerPanel.remove(imageLabel);
    	imageLabel = new JLabel(new ImageIcon(editedImage.getScaledInstance(imageWidth, 256, 0)));
    	imageLabel.addMouseListener(new MouseClickListener());
    	centerPanel.add(imageLabel);
		titlePanel.removeAll();
		titlePanel.add(new JLabel("Original                          "), BorderLayout.WEST);
		titlePanel.add(new JLabel("                                            Y DCT Grayscaled"), BorderLayout.EAST);
		rgbButton.setEnabled(false);
	}
	
	public void DCT() throws IOException
	{
		int blocksWide = (int)(img.getImage().getWidth() / 8)*8;
		int blocksHigh = (int)(img.getImage().getHeight() / 8)*8;
		
		for(int i=0;i<blocksHigh;i+=8)
		{
			for(int j=0;j<blocksWide;j+=8)
			{
				int[][] yBlock = new int[8][8];
				int[][] uBlock = new int[8][8];
				int[][] vBlock = new int[8][8];
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						yBlock[k][l] = img.getYMatrix()[i+k][j+l];
						uBlock[k][l] = img.getUMatrix()[i+k][j+l];
						vBlock[k][l] = img.getVMatrix()[i+k][j+l];
					}
				}

				yBlock = matrixMul(yBlock, false); // T * Y
				yBlock = matrixMul(yBlock, true); // Y * T^T
				
				uBlock = matrixMul(uBlock, false); // T * U
				uBlock = matrixMul(uBlock, true); // U * T^T
				
				vBlock = matrixMul(vBlock, false); // T * V
				vBlock = matrixMul(vBlock, true); // V * T^T
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						img.getYMatrix()[i+k][j+l] = yBlock[k][l];
						img.getUMatrix()[i+k][j+l] = uBlock[k][l];
						img.getVMatrix()[i+k][j+l] = vBlock[k][l];
					}
				}
			}
		}
		
		img.setY(breakMatrix(img.getY(), img.getYMatrix()));
		img.setU(breakMatrix(img.getU(), img.getUMatrix()));
		img.setV(breakMatrix(img.getV(), img.getVMatrix()));
		
		window.centerPanel.removeAll();
		window.centerPanel.validate();
		window.centerPanel.repaint();
		
		image = new BufferedImage(img.getImage().getWidth(), img.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
		image = ImageIO.read(img.getFile());
		window.centerPanel.add(new JLabel(new ImageIcon(image.getScaledInstance(img.getImageWidth(), 256, 0))), BorderLayout.WEST);
		
		editedImage = new BufferedImage(img.getImage().getWidth(), img.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster imgRaster = (WritableRaster) editedImage.getData();
    	imgRaster.setPixels(0, 0, image.getWidth(), image.getHeight(), img.getY());
    	editedImage.setData(imgRaster);
    	img.setImage(editedImage);
    	centerPanel.remove(imageLabel);
    	imageLabel = new JLabel(new ImageIcon(editedImage.getScaledInstance(imageWidth, 256, 0)));
    	imageLabel.addMouseListener(new MouseClickListener());
    	centerPanel.add(imageLabel);
		titlePanel.removeAll();
		titlePanel.add(new JLabel("Original                          "), BorderLayout.WEST);
		titlePanel.add(new JLabel("                                            Y DCT Grayscaled"), BorderLayout.EAST);
		dctButton.setEnabled(false);
		quantButton.setEnabled(true);
	
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				tableA.getModel().setValueAt(""+yMatrix[tables.getY()+i][tables.getX()+j], i, j);
		}
	}
	
	public int[][] matrixMul(int[][] A, boolean transpose)
	{
		int[][] B = new int[8][8];
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				B[i][j] = 0;
		}
		
		if(transpose)
		{
			for(int i=0;i<8;i++) 
			{
				for(int j=0;j<8;j++) 
				{
					for(int k=0;k<8;k++)
						B[i][j] += (int)((double)(A[i][k]) * tables.getDCTTransposeTable()[k][j]);
				}
			}
		}
		else
		{
			for(int i=0;i<8;i++) 
			{
				for(int j=0;j<8;j++) 
				{
					for(int k=0;k<8;k++)
						B[i][j] += (int)(tables.getDCTTable()[i][k] * (double)(A[k][j]));
				}
			}
		}
		return B;
	}
	
	public int[][] matrixMulInv(int[][] A, boolean transpose)
	{
		int[][] C = new int[8][8];
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				C[i][j] = 0;
		}
		
		if(transpose)
		{
			for(int i=0;i<8;i++) 
			{
				for(int j=0;j<8;j++) 
				{
					for(int k=0;k<8;k++)
						C[i][j] += (int)((double)A[i][k] * tables.getDCTTable()[k][j]);
				}
			}
		}
		else
		{
			for(int i=0;i<8;i++) 
			{
				for(int j=0;j<8;j++) 
				{
					for(int k=0;k<8;k++)
						C[i][j] += (int)(tables.getDCTTransposeTable()[i][k] * (double)A[k][j]);
				}
			}
		}
		return C;
	}
	
	public int[][] matrixDiv(int[][] A, int[][] B)
	{
		for(int i=0;i<8;i++) 
		{
            for(int j=0;j<8;j++) 
            {
               A[i][j] = (int)(A[i][j] / B[i][j]);
            }
        }
		
		return A;
	}

	public int[][] matrixMult(int[][] A, int[][] B)
	{
		for(int i=0;i<8;i++) 
		{
            for(int j=0;j<8;j++) 
            {
               A[i][j] = (int)(A[i][j] * B[i][j]);
            }
        }
		
		return A;
	}
	
	public void quantization() throws IOException
	{
		int blocksWide = (int)(img.getImage().getWidth() / 8)*8;
		int blocksHigh = (int)(img.getImage().getHeight() / 8)*8;
		
		for(int i=0;i<blocksHigh;i+=8)
		{
			for(int j=0;j<blocksWide;j+=8)
			{
				int[][] yBlock = new int[8][8];
				int[][] uBlock = new int[8][8];
				int[][] vBlock = new int[8][8];
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						yBlock[k][l] = img.getYMatrix()[i+k][j+l];
						uBlock[k][l] = img.getUMatrix()[i+k][j+l];
						vBlock[k][l] = img.getVMatrix()[i+k][j+l];
					}
				}
			
				yBlock = matrixDiv(yBlock, tables.getLumTable());
				uBlock = matrixDiv(uBlock, tables.getChromTable());
				vBlock = matrixDiv(vBlock, tables.getChromTable());
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						img.getYMatrix()[i+k][j+l] = yBlock[k][l];
						img.getUMatrix()[i+k][j+l] = uBlock[k][l];
						img.getVMatrix()[i+k][j+l] = vBlock[k][l];
					}
				}
			}
		}

		Y = breakMatrix(Y, yMatrix);
		U = breakMatrix(U, uMatrix);
		V = breakMatrix(V, vMatrix);
		
		text.setText(tables.getChosenTable() + " Quantization Performed");
		
		image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		image = ImageIO.read(img.getFile());
		window.centerPanel.add(new JLabel(new ImageIcon(image.getScaledInstance(img.getImageWidth(), 256, 0))), BorderLayout.WEST);
		
		editedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster imgRaster = (WritableRaster) editedImage.getData();
    	imgRaster.setPixels(0, 0, image.getWidth(), image.getHeight(), Y);
    	editedImage.setData(imgRaster);
    	img.setImage(editedImage);
    	centerPanel.remove(imageLabel);
    	imageLabel = new JLabel(new ImageIcon(editedImage.getScaledInstance(imageWidth, 256, 0)));
    	imageLabel.addMouseListener(new MouseClickListener());
    	centerPanel.add(imageLabel);
		titlePanel.removeAll();
		titlePanel.add(new JLabel("Original                          "), BorderLayout.WEST);
		titlePanel.add(new JLabel("                                            Y Quantization Greyscaled"), BorderLayout.EAST);
		quantButton.setEnabled(false);
		quantInvButton.setEnabled(true);
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				tableA.getModel().setValueAt(""+yMatrix[tables.getY()+i][tables.getX()+j], i, j);
		}
	}
	
	public void quantizationInv() throws IOException
	{
		int blocksWide = (int)(img.getImage().getWidth() / 8)*8;
		int blocksHigh = (int)(img.getImage().getHeight() / 8)*8;
		
		for(int i=0;i<blocksHigh;i+=8)
		{
			for(int j=0;j<blocksWide;j+=8)
			{
				int[][] yBlock = new int[8][8];
				int[][] uBlock = new int[8][8];
				int[][] vBlock = new int[8][8];
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						yBlock[k][l] = img.getYMatrix()[i+k][j+l];
						uBlock[k][l] = img.getUMatrix()[i+k][j+l];
						vBlock[k][l] = img.getVMatrix()[i+k][j+l];
					}
				}
			
				yBlock = matrixMult(yBlock, tables.getLumTable());
				uBlock = matrixMult(uBlock, tables.getChromTable());
				vBlock = matrixMult(vBlock, tables.getChromTable());
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						img.getYMatrix()[i+k][j+l] = yBlock[k][l];
						img.getUMatrix()[i+k][j+l] = uBlock[k][l];
						img.getVMatrix()[i+k][j+l] = vBlock[k][l];
					}
				}
			}
		}

		Y = breakMatrix(Y, yMatrix);
		U = breakMatrix(U, uMatrix);
		V = breakMatrix(V, vMatrix);
		
		image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		image = ImageIO.read(img.getFile());
		window.centerPanel.add(new JLabel(new ImageIcon(image.getScaledInstance(img.getImageWidth(), 256, 0))), BorderLayout.WEST);
		
		editedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster imgRaster = (WritableRaster) editedImage.getData();
    	imgRaster.setPixels(0, 0, image.getWidth(), image.getHeight(), Y);
    	editedImage.setData(imgRaster);
    	img.setImage(editedImage);
    	centerPanel.remove(imageLabel);
    	imageLabel = new JLabel(new ImageIcon(editedImage.getScaledInstance(imageWidth, 256, 0)));
    	imageLabel.addMouseListener(new MouseClickListener());
    	centerPanel.add(imageLabel);
		titlePanel.removeAll();
		titlePanel.add(new JLabel("Original                          "), BorderLayout.WEST);
		titlePanel.add(new JLabel("                                            Y Quantization Greyscaled"), BorderLayout.EAST);
		quantInvButton.setEnabled(false);
		invdctButton.setEnabled(true);
	
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				tableA.getModel().setValueAt(""+yMatrix[tables.getY()+i][tables.getX()+j], i, j);
		}
	}
	
	public void inverseDCT() throws IOException
	{
		int blocksWide = (int)(img.getImage().getWidth() / 8)*8;
		int blocksHigh = (int)(img.getImage().getHeight() / 8)*8;
		
		for(int i=0;i<blocksHigh;i+=8)
		{
			for(int j=0;j<blocksWide;j+=8)
			{
				int[][] yBlock = new int[8][8];
				int[][] uBlock = new int[8][8];
				int[][] vBlock = new int[8][8];
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						yBlock[k][l] = img.getYMatrix()[i+k][j+l];
						uBlock[k][l] = img.getUMatrix()[i+k][j+l];
						vBlock[k][l] = img.getVMatrix()[i+k][j+l];
					}
				}
			
				yBlock = matrixMulInv(yBlock, false); // T^T * Y
				yBlock = matrixMulInv(yBlock, true); // Y * T
				
				uBlock = matrixMulInv(uBlock, false); // T^t * U
				uBlock = matrixMulInv(uBlock, true); // U * T
				
				vBlock = matrixMulInv(vBlock, false); // T^T * V
				vBlock = matrixMulInv(vBlock, true); // V * T
				
				for(int k=0;k<8;k++)
				{
					for(int l=0;l<8;l++)
					{
						img.getYMatrix()[i+k][j+l] = yBlock[k][l];
						img.getUMatrix()[i+k][j+l] = uBlock[k][l];
						img.getVMatrix()[i+k][j+l] = vBlock[k][l];
					}
				}
			}
		}
		
		img.setY(breakMatrix(img.getY(), img.getYMatrix()));
		img.setU(breakMatrix(img.getU(), img.getUMatrix()));
		img.setV(breakMatrix(img.getV(), img.getVMatrix()));
		
		window.centerPanel.removeAll();
		window.centerPanel.validate();
		window.centerPanel.repaint();
		
		image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		image = ImageIO.read(img.getFile());
		window.centerPanel.add(new JLabel(new ImageIcon(image.getScaledInstance(img.getImageWidth(), 256, 0))), BorderLayout.WEST);
		
		editedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    	WritableRaster imgRaster = (WritableRaster) editedImage.getData();
    	imgRaster.setPixels(0, 0, image.getWidth(), image.getHeight(), img.getY());
    	editedImage.setData(imgRaster);
    	img.setImage(editedImage);
    	centerPanel.remove(imageLabel);
    	imageLabel = new JLabel(new ImageIcon(editedImage.getScaledInstance(imageWidth, 256, 0)));
    	imageLabel.addMouseListener(new MouseClickListener());
    	centerPanel.add(imageLabel);
		titlePanel.removeAll();
		titlePanel.add(new JLabel("Original                          "), BorderLayout.WEST);
		titlePanel.add(new JLabel("                                            Y Inverse DCT Grayscaled"), BorderLayout.EAST);
		invdctButton.setEnabled(false);
		rgbButton.setEnabled(true);
	
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				tableA.getModel().setValueAt(""+yMatrix[tables.getY()+i][tables.getX()+j], i, j);
		}
	}
}