package com.david.bank;


import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * entry point of all other microservice(API) of this application
 *
 * eg.
 *     [
 *         {
 *             "rel": "self",
 *             "href": "http://localhost:3000/",
 *             "type": "application/json"
 *         },
 *     ]
 */
@Path("api/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class EntryResource {

	protected enum ContentType {
		APPLICATION_JSON("application/json");

		private String val;

		ContentType(String val) {
			this.val = val;
		}

		@Override
		public String toString() {
			return this.val;
		}
	}


	public static class EntryPoint implements Serializable {
		public String rel;
		public String href;
		public ContentType type = ContentType.APPLICATION_JSON;
	}

	public List<EntryPoint> index() {
		List result = new ArrayList<>();
		return result;
	}
}
