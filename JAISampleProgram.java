import java.io.IOException;

import javax.media.jai.*;
import javax.media.jai.widget.ScrollingImagePanel;

import com.sun.media.jai.codec.FileSeekableStream;
import java.awt.image.renderable.ParameterBlock;
import java.awt.*;
import com.sun.medialib.mlib.Image;

public class JAISampleProgram 
{
	public static void main(String args[])
	{
		FileSeekableStream fi = null;
		try
		{
			/*create a stream on the input file specified.*/
			System.out.println(args[1]);
			fi = new FileSeekableStream(args[1]);
		}
		catch(IOException ex)
		{
			System.out.println("Error opening the image");
			System.exit(0);
		}
		if (Image.isAvailable()) {
			System.out.println("ImageIO native impl. seems to be available");
		} else {
			System.out.println("ImageIO native impl. does NOT seem to be available");			
		}
		/** 
		 * Create an operator to decode the image file
		 */ 
		RenderedOp image1 = JAI.create("stream",fi);
		/*For now, think that RenderedOp class represents our image as an object. We are creating our image from a "stream" fi.*/

		Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
		/* we are using Bilinear Interpolation */

		/* we now prepare the parameters that are going to be applied on the image1 */
		ParameterBlock params=new ParameterBlock();
		params.addSource(image1); // first specify the image
		params.add(0.1f); // X - scaling
		params.add(0.1f); // Y - scaling factor
		params.add(0.0f); // X - translate
		params.add(0.0f); //Y- translate

		// our second image is created by "scale"-ing based on the params we pass.
		RenderedOp image2=JAI.create("scale",params);

		//rest of the code is AWT window to display the image we created.

		int width = image2.getWidth();
		int height = image2.getHeight();

		ScrollingImagePanel panel=new ScrollingImagePanel(image2,width,height);
		Frame window=new Frame("JAI Sample Program");
		window.add(panel);
		window.pack();
		window.show(); 

	}

}
