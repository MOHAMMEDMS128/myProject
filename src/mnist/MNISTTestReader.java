package mnist;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
// this class to test the provided dataset from http://yann.lecun.com/exdb/mnist/
public class MNISTTestReader {
	
	
	
	DataInputStream label_data_stream = null;//input the label_data_stream variable and assign it null/0/nothing
	DataInputStream image_data_stream = null;//input the image_data_stream variable and assign it to null/0/nothing
	
	//direction for files that hold the MNIST database
	String train_label_filename;
	String train_image_filename;
	
	static ArrayList<int[][]> image_list;//ArrayList of int that called image_list
	ArrayList<Integer> label_list;

	
	public MNISTTestReader(String labelPath, String imagePath) {
		train_label_filename = labelPath;//train_label_filename Variable is assigned to the label path string
		train_image_filename = imagePath;
		
		image_list = new ArrayList<int[][]>();
		label_list = new ArrayList<Integer>();

		
		
	}
	
	public static ArrayList<int[][]> getImages() {//access content of the MMNIST Data
		
		return image_list;
	}
	
public ArrayList<Integer> getlabels() {//access content of the MMNIST Data
		
		return label_list;
	}
	//add function to get labels
	
	
	
	public void readMNISTdata() throws IOException{//read content of the MNISTdata
	
		
		try {
			label_data_stream = new DataInputStream(new FileInputStream(train_label_filename));//new varibale that contain the MNIST LABEL DIRECTION
			image_data_stream = new DataInputStream(new FileInputStream(train_image_filename));	
		
			int startcode_image = image_data_stream.readInt();
			int startcode_label = label_data_stream.readInt();
			
			System.out.println("start code: images = " + startcode_image +
					" startcode labels = "+ startcode_label );
			
			int number_of_labels = label_data_stream.readInt();
			int number_of_images = image_data_stream.readInt();
			
			System.out.println("number of labels: " + number_of_labels + " number of images: "
					+ number_of_images);
			
			int image_height = image_data_stream.readInt();
			int image_width = image_data_stream.readInt();
			
			System.out.println("image size:" + image_width +  " x " + image_height);
	
			
			//byte array for labels
			byte[] label_data = new byte[number_of_labels];//byte array that holds 60k of label data
			
			//byte array for images
			int image_size = image_height * image_width;
			byte[] image_data = new byte[image_size * number_of_images];//A byte array that hold 60k of images data
			
			//read all labels
			label_data_stream.read(label_data);
			
			//read all images
			image_data_stream.read(image_data);
			
			
			int[][] image;
			//Access selected images and labels.
			for (int i=0; i <number_of_images; i++) 
			{
				int label = label_data[i];
				//System.out.println(label);
				
				image = new int [image_width][image_height];
				
				for(int row = 0; row < image_height; row++) {
				for(int col = 0; col <image_width; col++) {
					
					image[row][col] = image_data[(i*image_size)+((row*image_width)+ col)];
				}
				label_list.add(label);
				image_list.add(image);
			}

			}
			
		}catch(FileNotFoundException e) {//Catch the Errors
			System.out.print("Application Cannot find file " + e.toString());
			
			
		 
		}
		
		
		

		
	}
		
		
}
