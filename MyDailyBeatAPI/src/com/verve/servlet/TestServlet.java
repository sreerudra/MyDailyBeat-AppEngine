package com.verve.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class TestServlet extends com.google.api.server.spi.SystemServiceServlet {

	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	/**
	 * 
	 */
	private static final long serialVersionUID = -9197262624523117473L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		//super.doPost(req, res);
		Map<String, List<BlobKey>> map = blobstoreService.getUploads(req);
		if (map != null) {
			List<BlobKey> blobs = map.get("file");
			BlobKey blobKey = blobs.get(0);

			ImagesService imagesService = ImagesServiceFactory.getImagesService();
			ServingUrlOptions servingOptions = ServingUrlOptions.Builder
					.withBlobKey(blobKey);

			String servingUrl = imagesService.getServingUrl(servingOptions);

			res.setStatus(HttpServletResponse.SC_OK);
			res.setContentType("application/json");

			try {
				JSONObject json = new JSONObject();
				json.put("servingUrl", servingUrl);
				json.put("blobKey", blobKey.getKeyString());

				PrintWriter out = res.getWriter();
				out.print(json.toString());
				out.flush();
				out.close();
			} catch (Exception e) {

			}
		} else {
			throw new IOException ("map is null");
		}
		
	}

}
