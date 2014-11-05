package com.camptocamp;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("isClassAvailable.json")
public class ClassPathController {

	@RequestMapping(method = RequestMethod.GET)
	public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JSONObject info = new JSONObject();
		Writer ret = resp.getWriter();
		String className = req.getParameter("class");
		
		info.put("class", className);
		
		try {
			Class.forName(className);
			info.put("status", "available");
		} catch (Throwable e) {
			info.put("status", "unavailable");
			info.put("reason", e.getMessage());
		} finally {
			ret.write(info.toString(4));
			if (ret != null)
				ret.close();
		}
	}


}