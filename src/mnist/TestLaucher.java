package mnist;

import java.io.IOException;

public class TestLaucher {

		public static void main(String[] args) throws IOException {
			

			MNISTTestReader reader = new MNISTTestReader("MNIST/train-labels.idx1-ubyte", "MNIST/train-images.idx3-ubyte");
			reader.readMNISTdata();
			
			//reader.getImages();
			//reader.getlabels();
			
		}

	}
