package com.camptocamp;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.ogr.OGRDataStoreFactory;
import org.geotools.data.ogr.jni.JniOGRDataStoreFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sun.medialib.mlib.Image;


@Controller
@RequestMapping("/info.json")
public class GdalOgrJNIController {

	
	static Logger log = Logger.getLogger(GdalOgrJNIController.class.getName());
	
	protected JSONObject getGDALOGRStatus() {
		JSONObject r = new JSONObject();

		// Available GeoTools datastores
		JSONArray gtDatastores = new JSONArray();
		Iterator<DataStoreFactorySpi> dtf = DataStoreFinder.getAllDataStores();
		while (dtf.hasNext()) {
			DataStoreFactorySpi dsfspi = dtf.next();
			gtDatastores.put(dsfspi.getDisplayName());
		}
		r.put("datastores", gtDatastores);
		// OGR
		try {
			Class.forName("org.gdal.ogr.ogr");

			OGRDataStoreFactory f = new JniOGRDataStoreFactory();

			// Next step will call loadLibrary("gdaljni") ... Not good.
			// This will arise in GeoTools library.

			if (f.isAvailable()) {
				JSONArray availableOGRDrivers = new JSONArray();

				for (String s : f.getAvailableDrivers()) {
					availableOGRDrivers.put(s);
				}
				r.put("OGR",
						new JSONObject().put("status", "available").put(
								"drivers", availableOGRDrivers));

			}
			// Unavailable
			else {
				r.put("OGR",
						new JSONObject().put("status", "unavailable").put(
								"reason", "JniOGR reported as unavailable (JniOGRDataStoreFactory.doIsAvailable() throwed "
										+ "something or returned false, presumably native library not found in the java.library.path "
										+ "or attempted to be loaded twice."));
			}
			// Exception raised
		} catch (ClassNotFoundException e){
			r.put("OGR",
					new JSONObject().put("status", "unavailable").put(
							"reason", "Unable to resolve class org.gdal.ogr.ogr. Probable misconfiguration of the classpath."));			
		} catch (Throwable e) {
			r.put("OGR",
					new JSONObject().put("status", "unavailable").put("reason",
							e.toString()));
		}

		// GDAL
		try {
			// Same remark: Call to loadLibrary("gdaljni") initiated 
			// by imageIO-Ext library ("vanilla" version, my fork on github
			// avoids this).
			if (GDALUtilities.isGDALAvailable()) {
				r.put("GDAL", new JSONObject().put("status", "available"));
			} else {
				r.put("GDAL", new JSONObject().put("status", "unavailable"));
			}
		} catch (NoClassDefFoundError e) {
			r.put("GDAL", new JSONObject().put("status", "unavailable")
					.put("reason", "Unable to find class org.gdal.gdal.gdal, this is likely due to "
							+ "a classpath misconfiguration."));
			
		}

		return r;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void handle(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PrintWriter ret = null;
		JSONObject info = new JSONObject();
		try {
			ret = resp.getWriter();
			
			// Add GeoTools informations
			info.put("geotools", getGDALOGRStatus());
			// JAI
			try {
			info.put("jai-imageio",
					new JSONObject().put("status", Image.isAvailable() ? "available" : "unavailable" ));

			} catch (NoClassDefFoundError e) {
				info.put("jai-imageio",
						new JSONObject().put("status", "unavailable" ).put("reason",
								"Unable to find class com.sun.medialib.mlib.Image, this is likely due to "
										+ "a classpath misconfiguration."));
				
			}
			// TurboJPEG
			try {
				Class c = Class
						.forName("org.libjpegturbo.turbojpeg.TJCompressor");
				c.newInstance();
			} catch (NoClassDefFoundError e) {
				info.put("libturbojpeg",
						new JSONObject().put("status", "unavailable").put("reason", 
								"Unable to find class org.libjpegturbo.turbojpeg.TJCompressor, this is likely due to "
										+ "a classpath misconfiguration."));
			} catch (ClassNotFoundException e) {
				info.put("libturbojpeg",
				new JSONObject().put("status", "unavailable").put("reason", 
						"Unable to find class org.libjpegturbo.turbojpeg.TJCompressor, this is likely due to "
								+ "a classpath misconfiguration."));				
			} catch (UnsatisfiedLinkError e) {
				info.put("libturbojpeg",
				new JSONObject().put("status", "unavailable").put("reason", 
						"Class found but references unreachable native code. Probably a mismatch between "
						+ "bindings and native library version."));					
			} catch (Throwable e) {
				info.put("libturbojpeg",
						new JSONObject().put("status", "unavailable").put("reason", e.getMessage()));
			}

		} catch (Throwable e) {
			info.put("unhandled_exception", e.getMessage());
		} 
		finally {
			ret.write(info.toString(2));
			if (ret != null)
				ret.close();
		}
	}

}
