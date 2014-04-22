package com.camptocamp;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/native.json")
public class NativeLibAvailableController {

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

	@RequestMapping(method = RequestMethod.GET)
	public void handle(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		JSONObject info = new JSONObject();
		Writer ret = resp.getWriter();
		try {

			// native libs
			String [] libraries = ClassScope.getLoadedLibraries(ClassLoader
					.getSystemClassLoader());
			JSONArray libr = new JSONArray();
			for (String lib : libraries) {
				libr.put(lib);
			}
			info.put("loaded_native_libraries", libr);
			
			// current available packages
			JSONArray packs = new JSONArray();
			for (Package p : Package.getPackages()) {
				packs.put(p.getName());
			}
			info.put("known_packages", packs);

		} catch (Exception e) {
			info.put("exception", e.getMessage());
		} finally {
			ret.write(info.toString(4));
			if (ret != null)
				ret.close();
		}
	}
}
