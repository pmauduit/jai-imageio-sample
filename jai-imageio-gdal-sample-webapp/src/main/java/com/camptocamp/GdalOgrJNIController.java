package com.camptocamp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.ogr.OGRDataStoreFactory;
import org.geotools.data.ogr.jni.JniOGRDataStoreFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.log4j.Logger;

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
		// OGR / GeoTools
		try {
			OGRDataStoreFactory f = new JniOGRDataStoreFactory();

			if (f.isAvailable()) {
				JSONArray availableOGRDrivers = new JSONArray();
			
				for (String s : f.getAvailableDrivers()) {
					availableOGRDrivers.put(s);
				}
				r.put("OGR", new JSONObject().put("status", "available").
						put("drivers", availableOGRDrivers));
			
			}
			// Unavailable
			else {
				r.put("OGR", new JSONObject().put("status", "unavailable").
						put("reason", "Probable misconfiguration"));
			}
		// Exception raised
		} catch (Throwable e) {
			r.put("OGR", new JSONObject().put("status", "unavailable").
					put("reason", e.toString()));
		}

		
		return r;
	}
	
	
    @RequestMapping(method = RequestMethod.GET)
	public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		PrintWriter ret = null;
		JSONObject info = new JSONObject();
		try {
			ret = resp.getWriter();
			
			// Add GeoTools informations
			info.put("geotools", getGDALOGRStatus());
			
			info.put("status", "OK");
		} finally {
			ret.write(info.toString(4));
			if (ret != null)
				ret.close();
		}
	}
	
	
}