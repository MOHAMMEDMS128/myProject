package mnist;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import java.io.File;
import javafx.scene.control.Button;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Toolkit;

import javafx.embed.swing.SwingFXUtils;
import java.io.FileInputStream;
import java.nio.file.Paths;

import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.event.EventHandler;
import javax.imageio.ImageIO;
import javafx.stage.Stage;
import javafx.stage.Popup;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Main extends Application {

	public static final int IMG_HEIGHT = 28;
	public static final int IMG_WIDTH = 28;
	private static final int k = 115;
	public static final String DATASET_LOC = "MNIST/jpg";
	private static KNN knn;
	private static ReadImage r;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// title of the application
		primaryStage.setTitle("Hand Written Digits Recognition");
		
		// initiate vertical box to add buttons
	    VBox root = new VBox();
	    root.setPadding(new Insets(10, 50, 50, 50));
	    root.setSpacing(15);
		
		// to browse file
		final FileChooser fileChooser = new FileChooser();
		
		// text to display predicted result
		Text text = new Text();
		text.setX(30);
		text.setY(150);
				
		// button for browsing image
		Button button1 = new Button("Browse Image");
		// action handler for button 1
		button1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
					// open the images dataset that placed in jpg folder
				
				String currentPath = Paths.get("MNIST/jpg").toAbsolutePath().normalize().toString();
				fileChooser.setInitialDirectory(new File(currentPath));
				
				// show browsing panel
				File file = fileChooser.showOpenDialog(primaryStage);
				
				
				// image view object to display image on canvas
				ImageView imageView = new ImageView();
				
				if (file != null) {
					// read image and get pixel values
					int[] data = r.readImage(file.getAbsolutePath());
					
					if (data == null)
						text.setText("Unable to read from this location");
					else {
						// get predicted value from knn
						int result = knn.predict(data);
						// set text to display
						text.setText("Predicted value : " + String.valueOf(result));
						// open image and show on canvas
						try {
							Image image = new Image(new FileInputStream(file.getAbsolutePath()));
							imageView = new ImageView(image);
						} catch (Exception ee) {
							text.setText("Unable to read from this location");
						}
						// create pop-up and display image on that pop-up
						Popup pp = new Popup();
						pp.getContent().add(imageView);
						pp.getContent().add(text);
						pp.show(primaryStage);	
						pp.setAutoHide(true);
						pp.setOnAutoHide(null);
					}
				}
			}
		});

		Button button2 = new Button("Draw A Digit");
		button2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				Popup pp = new Popup();
				Canvas canvas = new Canvas(300, 300);
				GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
				graphicsContext.setLineWidth(28.0);
				graphicsContext.setFill(Color.BLACK);
				graphicsContext.fillRect(0, 0, 300, 300);
				graphicsContext.setStroke(Color.WHITE);
				
				pp.getContent().add(canvas);
				pp.show(primaryStage);
				
				// event handler when mouse is pressed
				canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						graphicsContext.beginPath();
						graphicsContext.moveTo(event.getX(), event.getY());
						graphicsContext.stroke();

					}
				});

				// event handler when mouse is dragged
				canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						graphicsContext.lineTo(event.getX(), event.getY());
						graphicsContext.stroke();
						graphicsContext.closePath();
						graphicsContext.beginPath();
						graphicsContext.moveTo(event.getX(), event.getY());
					}
				});

				// event handler when mouse key is released
				canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						graphicsContext.lineTo(event.getX(), event.getY());
						graphicsContext.stroke();
						graphicsContext.closePath();
						try {
							// write drawing to a file
							WritableImage writableImage = new WritableImage((int) canvas.getWidth(),
									(int) canvas.getHeight());
							canvas.snapshot(null, writableImage);
							// convert image to gray-scale and resize image
							BufferedImage scaledImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
							java.awt.Image tmp =  SwingFXUtils.fromFXImage(writableImage, null)
									.getScaledInstance(28, 28, java.awt.Image.SCALE_SMOOTH);
							File outputfile = new File("image.jpg");
							// save scaled image to disk
							Graphics graphics = scaledImg.getGraphics();
							graphics.drawImage(tmp, 0, 0, null);
							graphics.dispose();
							ImageIO.write(scaledImg, "jpg", outputfile);
							pp.hide();
							
							// read image from disk
							int[] data = r.readImage(outputfile.getAbsolutePath());
							ImageView imageView = new ImageView();
							if (data == null)
								text.setText("Unable to read from this location");
							else {
								int result = knn.predict(data);
								text.setText("Predicted value : " + String.valueOf(result));
								try {
									Image image = new Image(new FileInputStream(outputfile.getAbsolutePath()));
									imageView = new ImageView(image);
								} catch (Exception ee) {
									text.setText("Unable to read from this location");
								}
								Popup pp = new Popup();
								pp.getContent().add(imageView);
								pp.getContent().add(text);
								pp.show(primaryStage);
								pp.setAutoHide(true);
								pp.setOnAutoHide(null);
							}

						} catch (Exception ex) {
							text.setText("unable to process canvas ouput");
						}
					}
				});
			}
		});

		root.getChildren().add(button1);
		root.getChildren().add(button2);

		Scene scene = new Scene(root, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		r = new ReadImage();
		knn = new KNN(DATASET_LOC, k, IMG_HEIGHT, IMG_WIDTH);
		knn.readDataset();
		Application.launch(args);
	}
}
