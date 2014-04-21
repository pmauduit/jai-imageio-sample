package com.camptocamp.jnistandalone;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.media.jai.*;
import javax.media.jai.widget.ScrollingImagePanel;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.ogr.OGRDataStoreFactory;
import org.geotools.data.ogr.jni.JniOGRDataStoreFactory;

import com.sun.media.jai.codec.FileSeekableStream;

import java.awt.image.renderable.ParameterBlock;
import java.awt.*;

import com.sun.medialib.mlib.Image;

public class Standalone {

	public static void GeoToolsGdalOgrStatus() {
		// GeoTools

		Iterator<DataStoreFactorySpi> dtf = DataStoreFinder.getAllDataStores();
		System.out.println("Available GeoTools datastores:");
		while (dtf.hasNext()) {
			DataStoreFactorySpi dsfspi = dtf.next();
			System.out.println("\t" + dsfspi.getDisplayName());
		}

		// OGR
		try {
			Class.forName("org.gdal.ogr.ogr");

			OGRDataStoreFactory f = new JniOGRDataStoreFactory();

			if (f.isAvailable()) {
				System.out
						.println("JNI OGR datastorefactory available. Listing drivers:");
				for (String s : f.getAvailableDrivers()) {
					System.out.println("\t" + s);
				}
			}
			// Unavailable
			else {
				System.out
						.println("JNI OGR datastorefactory reported UNAVAILABLE");
			}
			// Exception raised
		} catch (Throwable e) {
			System.out.println("JNI OGR datastorefactory UNAVAILABLE, reason: "
					+ e.getMessage());
		}

		// GDAL
		try {
			if (GDALUtilities.isGDALAvailable()) {
				System.out
						.println("GDAL utilities are reported GDAL as available.");
			} else {
				System.out
						.println("GDAL utilities reported GDAL as UNAVAILABLE.");
			}
		} catch (Throwable e) {
			System.out
					.println("GDAL utilities reported GDAL as UNAVAILABLE, reason "
							+ e.getMessage());
		}
	}

	public static void main(String args[]) {
		FileSeekableStream fi = null;
		try {
			/* create a stream on the input file specified. */
			System.out.println("file.png");
			fi = new FileSeekableStream("file.png");
		} catch (IOException ex) {
			System.out.println("Error opening the image");
			System.exit(0);
		}
		if (Image.isAvailable()) {
			System.out.println("ImageIO native impl. seems to be available");
		} else {
			System.out
					.println("ImageIO native impl. does NOT seem to be available");
		}
		/**
		 * Create an operator to decode the image file
		 */
		RenderedOp image1 = JAI.create("stream", fi);
		/*
		 * For now, think that RenderedOp class represents our image as an
		 * object. We are creating our image from a "stream" fi.
		 */

		Interpolation interp = Interpolation
				.getInstance(Interpolation.INTERP_BILINEAR);
		/* we are using Bilinear Interpolation */

		/*
		 * we now prepare the parameters that are going to be applied on the
		 * image1
		 */
		ParameterBlock params = new ParameterBlock();
		params.addSource(image1); // first specify the image
		params.add(0.1f); // X - scaling
		params.add(0.1f); // Y - scaling factor
		params.add(0.0f); // X - translate
		params.add(0.0f); // Y- translate

		// our second image is created by "scale"-ing based on the params we
		// pass.
		RenderedOp image2 = JAI.create("scale", params);

		// rest of the code is AWT window to display the image we created.

		int width = image2.getWidth() + 20;
		int height = image2.getHeight() + 20;

		ScrollingImagePanel panel = new ScrollingImagePanel(image2, width,
				height);
		Frame window = new Frame("JAI Sample Program");
		window.add(panel);
		window.pack();
		window.show();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		window.dispose();

		GeoToolsGdalOgrStatus();
		try {
			System.out.println("Loaded native libraries before ending:");
			String[] lib = ClassScope.getLoadedLibraries(ClassLoader
					.getSystemClassLoader());
			for (String l : lib) {
				System.out.println("\t" + l);
			}
		} catch (Exception e) {
		}
	}

	static class ClassScope {
		private static java.lang.reflect.Field LIBRARIES = null;
		static {
			try {
				LIBRARIES = ClassLoader.class
						.getDeclaredField("loadedLibraryNames");
				LIBRARIES.setAccessible(true);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static String[] getLoadedLibraries(final ClassLoader loader)
				throws Exception {
			final Vector<String> libraries = (Vector<String>) LIBRARIES
					.get(loader);
			return libraries.toArray(new String[] {});
		}
	}

}
