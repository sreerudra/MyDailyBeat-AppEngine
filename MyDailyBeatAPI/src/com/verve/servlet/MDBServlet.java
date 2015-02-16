package com.verve.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.server.spi.SystemServiceServlet;

public class MDBServlet extends SystemServiceServlet {
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
		if (req.getRequestURI().contains("verify")) {
			// verify
			System.out.println("Verifying");
			resp.setStatus(307); //this makes the redirection keep your requesting method as is.
			resp.addHeader("Location", URLEncoder.encode("http://www.google.com", "UTF-8"));
		} else if (req.getRequestURI().contains("accept")) {
			//accept info
			System.out.println("Accept");
			resp.setStatus(307); //this makes the redirection keep your requesting method as is.
			resp.addHeader("Location", URLEncoder.encode("http://www.yahoo.com", "UTF-8"));
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
		if (req.getRequestURI().contains("verify")) {
			// verify
			System.out.println("Verifying");
			resp.setStatus(307); //this makes the redirection keep your requesting method as is.
			resp.addHeader("Location", URLEncoder.encode("http://www.google.com", "UTF-8"));
		} else if (req.getRequestURI().contains("accept")) {
			//accept info
			System.out.println("Accept");
			resp.setStatus(307); //this makes the redirection keep your requesting method as is.
			resp.addHeader("Location", URLEncoder.encode("http://www.yahoo.com", "UTF-8"));
		}
		
	}

}
