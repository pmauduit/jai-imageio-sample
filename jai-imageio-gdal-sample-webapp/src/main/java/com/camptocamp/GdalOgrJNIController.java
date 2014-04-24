package com.camptocamp;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.gdal.gdal.gdal;
import org.gdal.ogr.ogr;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.ogr.OGRDataStoreFactory;
import org.geotools.data.ogr.jni.JniOGRDataStoreFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sun.corba.se.impl.copyobject.JavaStreamObjectCopierImpl;
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
		r.put("GeoTools", new JSONObject().put("datastores", gtDatastores));
		
		// OGR
		JSONObject ogrStatus = new JSONObject();
		try {
			// should make a native call
			ogr.RegisterAll();
			JSONArray availableOGRDrivers = new JSONArray();
			for (int i = 0; i < ogr.GetDriverCount(); i++) {
				availableOGRDrivers.put(ogr.GetDriver(i).getName());
			}
			ogrStatus.put("status", "available").put("drivers", availableOGRDrivers);
		} catch (Throwable e) {
			ogrStatus.put("status", "unavailable").put("reason", e.toString());
		}
		
		// now try via GeoTools classes
		try {
			OGRDataStoreFactory f = new JniOGRDataStoreFactory();
			if (f.isAvailable()) {
				ogrStatus.put("via-geotools", "available");
			} else {
				ogrStatus.put("via-geotools", "available");
			}
		} catch (Throwable e) {
			ogrStatus.put("via-geotools", new JSONObject().
					put("status", "unavailable").
					put("reason", e.getMessage()));
		}	
		r.put("OGR", ogrStatus);

		// GDAL
		JSONObject gdalStatus = new JSONObject();
		try {
			// should make a native call
			gdal.AllRegister();
			JSONArray availableGDALDrivers = new JSONArray();
			for (int i = 0 ; i < gdal.GetDriverCount(); i++) {
				availableGDALDrivers.put(gdal.GetDriver(i).getShortName());
			}
			gdalStatus.put("status", "available").put("drivers", availableGDALDrivers);
		} catch (Throwable e) {
			gdalStatus.put("status", "unavailable").put("reason", e.toString());
		}
		// via GeoTools
		try {
		 if (GDALUtilities.isGDALAvailable()) {
			 gdalStatus.put("via-geotools", new JSONObject().put("status", "available"));
		 } else {
			 gdalStatus.put("via-geotools", new JSONObject().put("status", "unavailable"));
		 }
		} catch (Throwable e) {
			 gdalStatus.put("via-geotools", new JSONObject().put("status", "unavailable")
					 .put("reason", e.getMessage()));
		}
		r.put("GDAL", gdalStatus);
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
				info.put("jai-imageio", new JSONObject().put("status",
						Image.isAvailable() ? "available" : "unavailable"));

			} catch (NoClassDefFoundError e) {
				info.put(
						"jai-imageio",
						new JSONObject().put("status", "unavailable").put(
								"reason",
								"Unable to find class com.sun.medialib.mlib.Image, this is likely due to "
										+ "a classpath misconfiguration."));

			}
			// TurboJPEG
			try {
				Class c = Class
						.forName("org.libjpegturbo.turbojpeg.TJCompressor");
				c.newInstance();
			} catch (NoClassDefFoundError e) {
				info.put(
						"libturbojpeg",
						new JSONObject()
								.put("status", "unavailable")
								.put("reason",
										"Unable to find class org.libjpegturbo.turbojpeg.TJCompressor, this is likely due to "
												+ "a classpath misconfiguration."));
			} catch (ClassNotFoundException e) {
				info.put(
						"libturbojpeg",
						new JSONObject()
								.put("status", "unavailable")
								.put("reason",
										"Unable to find class org.libjpegturbo.turbojpeg.TJCompressor, this is likely due to "
												+ "a classpath misconfiguration."));
			} catch (UnsatisfiedLinkError e) {
				info.put(
						"libturbojpeg",
						new JSONObject()
								.put("status", "unavailable")
								.put("reason",
										"Class found but references unreachable native code. Probably a mismatch between "
												+ "bindings and native library version."));
			} catch (Throwable e) {
				info.put(
						"libturbojpeg",
						new JSONObject().put("status", "unavailable").put(
								"reason", e.getMessage()));
			}

		} catch (Throwable e) {
			info.put("unhandled_exception", e.getMessage());
		} finally {
			ret.write(info.toString(2));
			if (ret != null)
				ret.close();
		}
	}

}
