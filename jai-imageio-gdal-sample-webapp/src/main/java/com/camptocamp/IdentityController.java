package com.camptocamp;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/identity.json")
public class IdentityController {

	// A simple controller which returns JSON
	// with a possible HTTP error code given as argument
	@RequestMapping(method = RequestMethod.GET)
	public void handle(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "code", defaultValue = "200") int code,
			@RequestParam(value = "message", defaultValue = "identity") String message)
			throws IOException {
		Writer r = resp.getWriter();
		try {
			resp.setStatus(code);		
			JSONObject ret = new JSONObject();
			ret.put("message", message);
			r.write(ret.toString(4));
		}
		finally {
			r.close();
		}
	}
}
