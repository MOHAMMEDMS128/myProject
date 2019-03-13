package mnist;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class KNN {

	private String directory;
	private int datasetSize;
	private int imgHeight;
	private int imgWidth;
	private ReadImage rImg;
	private ArrayList<int[]> trainingData;
	private ArrayList<Integer> labels;
	private int k;

	KNN(String tdirec, int k, int height, int width) {
		directory = tdirec;
		this.k = k;
		datasetSize = 0;
		labels = new ArrayList<Integer>();;
		trainingData = new ArrayList<int []>();
		rImg = new ReadImage();
		imgHeight = height;
		imgWidth = width;
	}

	/***
	 * function to read dataset from directory, folders inside directory have data,
	 * folder name is label of all images inside that folder
	 * 
	 * @throws Exception
	 */
	public void readDataset() {
		// read all folders inside directory
		File folders = new File(directory);
		File[] listOfFolders = folders.listFiles();

		try {
			for (File folder : listOfFolders) {
				// check whether file we are reading is directory or not
				if (folder.isDirectory()) {
					
					// parse folder name to int because folder name is label for
					// all images inside that folder
					int labl = Integer.parseInt(folder.getName());
					
					// get all files inside a folder
					File files = new File(folder.getAbsolutePath());
					File[] listOfFiles = files.listFiles();
					
					// read image check img height and width if match then add to
					// dataset and increase dataset size
					for (File file : listOfFiles) {
						if (file.isFile()) {
							int[] img = rImg.readImage(file.getAbsolutePath());
							int iH = rImg.getHeight();
							int iW = rImg.getWidth();
							if (iH != imgHeight || iW != imgWidth)
								continue;
							datasetSize += 1;
							trainingData.add(img);
							labels.add(labl);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.print(e.getMessage());
			System.exit(1);
		}
	}
	
	/***
	 * function to predict label of test image, test image dimensions should
	 * match training image dimension otherwise it will return -1
	 * @param testData
	 * @return image label 
	 */
	public int predict(int[] testData) {
		
		// check whether test image dimension match training set images
		if (imgHeight * imgWidth != testData.length)
			return -1;
		
		// store distance value of test image from training data images
		double[][] distances = new double[datasetSize][2];
		for (int i = 0; i < datasetSize; i++) {
			double distance = eDistance(testData, trainingData.get(i));
			distances[i][0] = distance;
			distances[i][1] = (double)labels.get(i);
		}
				
		// create vote array initially all votes are 0
		int[] vote = new int[10];
		for (int i = 0; i < 10; i++)
			vote[i] = 0;
		
		// sort distances by euclidean distance
		Arrays.sort(distances, new Comparator<double[]>() {
			@Override
			public int compare(double[] a, double[] b) {
				if (a[0] > b[0])
					return 1;
				else
					return -1;
			}
		});
				
		// count votes of top k members
		for(int i = 0;i < k;i++) 
			vote[(int) Math.round(distances[i][1])]++;
		
		// find value with maximum votes
		int max = -1;
		for(int i = 0;i < 10;i++) {
			if(vote[i] > max)
				max = i;
		}
		return max;
	}
	
	// measure euclidean distance between two arrays
	double eDistance(int[] testData, int[] trainData) {
		double sum = 0.0;
		for(int i = 0;i < testData.length;i++) {
			double diff = Math.abs(testData[i] - trainData[i]);
			double sq = Math.pow(diff, 2.0);
			sum = sum + sq;
		}
		return Math.sqrt(sum);
	}

}