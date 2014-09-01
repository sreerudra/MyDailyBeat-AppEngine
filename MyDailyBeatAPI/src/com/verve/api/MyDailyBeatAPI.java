package com.verve.api;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

@Api(name = "mydailybeat", version = "v1")
public class MyDailyBeatAPI {

	public static DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	public static BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();

	@ApiMethod(name = "groups.createmasterlist", path="groups/createmasterlist", httpMethod = HttpMethod.GET)
	public BooleanResponse createMasterList() {
		Query q = new Query("GroupsList");
		if (datastore.prepare(q).asSingleEntity() == null) {
			Entity s = new Entity("GroupsList");
			ArrayList<Group> groups = new ArrayList<Group>();
			s.setProperty("list", groups);
			s.setProperty("max_id", 1);
			datastore.put(s);
		}
		
		return BooleanResponse.createResponse(true);
	}
	
	@ApiMethod(name = "users.joingroup", path = "users/groups/join", httpMethod = HttpMethod.POST)
	public BooleanResponse joinGroup(JoinGroupPostObject postObj) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, postObj.screenName).addFilter("password",
				Query.FilterOperator.EQUAL, postObj.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		Query q2 = new Query("GroupsList");
		Entity groupsList = datastore.prepare(q2).asSingleEntity();
		ArrayList<Group> list = (ArrayList<Group>) groupsList.getProperty("list");
		ArrayList<Integer> groups = (ArrayList<Integer>) s.getProperty("groups");
		
		for (int i = 0 ; i < list.size() ; i++) {
			if (list.get(i).groupName.equalsIgnoreCase(postObj.groupName)) {
				groups.add(list.get(i).id);
			}
		}
		
		return BooleanResponse.createResponse(true);
	}
	
	@ApiMethod(name="groups.create", path="groups/create", httpMethod = HttpMethod.POST)
	public BooleanResponse createGroup(CreateGroupPostObject postObj) {
		Query q = new Query("GroupsList");
		Entity groupsList = datastore.prepare(q).asSingleEntity();
		int max_id = ((Integer) groupsList.getProperty("max_id")).intValue();
		Group.ID_START = max_id;
		ArrayList<Group> list = (ArrayList<Group>) groupsList.getProperty("list");
		list.add(new Group(postObj.groupName, postObj.screenName));
		return BooleanResponse.createResponse(true);
		
	}
	
	@ApiMethod(name="groups.get", path="groups/get", httpMethod=HttpMethod.GET)
	public ArrayList<Group> getGroupsForUser(@Named("screen_name") String screenName,
			@Named("password") String password) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		ArrayList<Integer> groups = (ArrayList<Integer>) s.getProperty("groups");
		Query q2 = new Query("GroupsList");
		Entity groupsList = datastore.prepare(q2).asSingleEntity();
		ArrayList<Group> list = (ArrayList<Group>) groupsList.getProperty("list");
		ArrayList<Group> subList = new ArrayList<Group>();
		for (int i = 0 ; i < groups.size() ; i++) {
			for (int j = 0 ; j < list.size() ; j++) {
				if (list.get(j).id == groups.get(i)) {
					subList.add(list.get(j));
				}
			}
		}
		
		return subList;
	}
	
	@ApiMethod(name = "users.standard.create", path = "users/register", httpMethod = HttpMethod.POST)
	public BooleanResponse createStandardUser(VerveStandardUser info) {

		Entity s = new Entity("StandardUser");
		s.setProperty("name", info.name);
		s.setProperty("email", info.email);
		s.setProperty("password", info.password);
		s.setProperty("screenName", info.screenName);
		s.setProperty("mobile", info.mobile);
		s.setProperty("zipcode", info.zipcode);
		s.setProperty("birth_month", info.birth_month);
		s.setProperty("birth_year", info.birth_year);
		s.setProperty("md5key", Constants.generateMD5(info.email));
		s.setProperty("verified", Boolean.FALSE);
		ArrayList<Integer> groups = new ArrayList<Integer>();
		s.setProperty("groups", groups);

		try {

			datastore.put(s);

		} catch (Exception e) {
			return BooleanResponse.createResponse(false);
		}

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String md5 = (String) s.getProperty("md5key");

		String msgBody = "Hi "
				+ info.name
				+ " !\n\nPlease verify the accuracy of your account information by going to the following link:\n\nhttps://1-dot-mydailybeat-api.appspot.com/_ah/api/mydailybeat/v1/verify?hash="
				+ md5;

		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(Constants.DEV_EMAIL,
					"MyDailyBeat Admin"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					info.email, info.name));
			msg.setSubject(Constants.DO_NOT_REPLY_PREFIX + "Activate your "
					+ Constants.DOMAIN_URL + " account!");
			msg.setText(msgBody);
			Transport.send(msg);

		} catch (AddressException e) {
			return BooleanResponse.createResponse(false);
		} catch (MessagingException e) {
			return BooleanResponse.createResponse(false);
		} catch (UnsupportedEncodingException e) {
			return BooleanResponse.createResponse(false);
		}

		return BooleanResponse.createResponse(true);

	}

	@SuppressWarnings("deprecation")
	@ApiMethod(name = "users.standard.getinfo", path = "users/getInfo", httpMethod = HttpMethod.GET)
	public VerveStandardUser getUserInfo(
			@Named("screen_name") String screenName,
			@Named("password") String password) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		return new VerveStandardUser((String) s.getProperty("name"),
				(String) s.getProperty("email"),
				(String) s.getProperty("password"),
				(String) s.getProperty("screenName"),
				(String) s.getProperty("mobile"),
				(String) s.getProperty("zipcode"),
				(String) s.getProperty("birth_month"),
				(long) s.getProperty("birth_year"));
	}

	@ApiMethod(name = "users.standard.create.verifyinfo", path = "verify", httpMethod = HttpMethod.GET)
	public VerveStandardUser verifyUserInfo(@Named("hash") String hash) {

		@SuppressWarnings("deprecation")
		Query q = new Query("StandardUser").addFilter("md5key",
				Query.FilterOperator.EQUAL, hash);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		s.setProperty("verified", Boolean.TRUE);
		datastore.put(s);
		return new VerveStandardUser((String) s.getProperty("name"),
				(String) s.getProperty("email"),
				(String) s.getProperty("password"),
				(String) s.getProperty("screenName"),
				(String) s.getProperty("mobile"),
				(String) s.getProperty("zipcode"),
				(String) s.getProperty("birth_month"),
				(long) s.getProperty("birth_year"));

	}

	@ApiMethod(name = "users.prefs.relationship.retrieve", path = "users/prefs/relationship/retrieve", httpMethod = HttpMethod.GET)
	public PrefsList retrieveRelationshipPrefs(
			@Named("screen_name") String screenName,
			@Named("password") String password) throws Exception {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		if (s != null) {
			PrefsList prefs = new PrefsList();
			ArrayList<Object> prefsList = new ArrayList<Object>();
			prefs.screenName = screenName;
			prefs.password = password;
			EmbeddedEntity relationshipPrefs = (EmbeddedEntity) s
					.getProperty("relationshipPrefs");
			if (relationshipPrefs != null) {
				EmbeddedEntity sexualPref = (EmbeddedEntity) relationshipPrefs
						.getProperty("Sexual Preference");
				if (sexualPref != null) {
					RadioButtonPreference orientation = new RadioButtonPreference();
					orientation.strings = (ArrayList<String>) sexualPref
							.getProperty("strings");
					orientation.index = ((Long) sexualPref.getProperty("index"))
							.intValue();
					if (orientation.strings != null) {
						IntegerPreference age = new IntegerPreference();
						age.data = ((Long) relationshipPrefs.getProperty("Age"))
								.intValue();
						prefsList.add(orientation);
						prefsList.add(age);
						prefs.prefs = prefsList;

						return prefs;
					}
					throw new Exception("orientation.strings is null");
				}
				throw new Exception("sexualPref is null");
			}
			throw new Exception("relationshipPrefs is null");
		}
		throw new Exception("Entity s is null");

	}

	@ApiMethod(name = "users.prefs.relationship.save", path = "users/prefs/relationship/save", httpMethod = HttpMethod.POST)
	public BooleanResponse saveRelationshipPrefs(PrefsList prefs) {

		EmbeddedEntity embedded = new EmbeddedEntity();

		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, prefs.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, prefs.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);

		EmbeddedEntity sexualpref = new EmbeddedEntity();
		Map pref = (LinkedHashMap) prefs.prefs.get(0);
		sexualpref.setProperty("strings",
				(ArrayList<String>) pref.get("strings"));
		sexualpref.setProperty("index", (int) pref.get("index"));

		embedded.setProperty("Sexual Preference", sexualpref);
		embedded.setProperty("Age", (int) prefs.prefs.get(1));
		s.setProperty("relationshipPrefs", embedded);
		datastore.put(s);

		return BooleanResponse.createResponse(true);
	}

	@ApiMethod(name = "users.prefs.hobby.retrieve", path = "users/prefs/hobby/retrieve", httpMethod = HttpMethod.GET)
	public CheckBoxPreference retrieveHobbyPrefs(
			@Named("screen_name") String screenName,
			@Named("password") String password) throws Exception {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		if (s != null) {
			CheckBoxPreference prefs = new CheckBoxPreference();
			prefs.screenName = screenName;
			prefs.password = password;
			EmbeddedEntity hobbyPrefs = (EmbeddedEntity) s
					.getProperty("hobbyPrefs");
			if (hobbyPrefs != null) {
				EmbeddedEntity interests = (EmbeddedEntity) hobbyPrefs
						.getProperty("Interests");
				if (interests != null) {
					ArrayList<String> options = (ArrayList<String>) interests
							.getProperty("options");
					ArrayList<Boolean> selected = (ArrayList<Boolean>) interests
							.getProperty("selected");
					if (options != null) {
						prefs.options = options;
						prefs.selected = selected;

						return prefs;
					}
					throw new Exception("options is null");
				}
				throw new Exception("interests is null");
			}
			throw new Exception("hobbyPrefs is null");
		}
		throw new Exception("Entity s is null");

	}

	@ApiMethod(name = "users.prefs.hobby.save", path = "users/prefs/hobby/save", httpMethod = HttpMethod.POST)
	public BooleanResponse saveHobbyPrefs(CheckBoxPreference prefs) {

		EmbeddedEntity embedded = new EmbeddedEntity();

		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, prefs.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, prefs.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);

		EmbeddedEntity interests = new EmbeddedEntity();
		interests.setProperty("options", prefs.options);
		interests.setProperty("selected", prefs.selected);

		embedded.setProperty("Interests", interests);

		s.setProperty("hobbyPrefs", embedded);
		datastore.put(s);

		return BooleanResponse.createResponse(true);
	}

	@ApiMethod(name = "users.prefs.fling.retrieve", path = "users/prefs/fling/retrieve", httpMethod = HttpMethod.GET)
	public PrefsList retrieveFlingPrefs(
			@Named("screen_name") String screenName,
			@Named("password") String password) throws Exception {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		if (s != null) {
			PrefsList prefs = new PrefsList();
			ArrayList<Object> prefsList = new ArrayList<Object>();
			prefs.screenName = screenName;
			prefs.password = password;
			EmbeddedEntity relationshipPrefs = (EmbeddedEntity) s
					.getProperty("flingPrefs");
			if (relationshipPrefs != null) {
				EmbeddedEntity sexualPref = (EmbeddedEntity) relationshipPrefs
						.getProperty("Sexual Preference");
				if (sexualPref != null) {
					RadioButtonPreference orientation = new RadioButtonPreference();
					orientation.strings = (ArrayList<String>) sexualPref
							.getProperty("strings");
					orientation.index = ((Long) sexualPref.getProperty("index"))
							.intValue();
					if (orientation.strings != null) {
						IntegerPreference age = new IntegerPreference();
						age.data = ((Long) relationshipPrefs.getProperty("Age"))
								.intValue();
						prefsList.add(orientation);
						prefsList.add(age);
						prefs.prefs = prefsList;

						return prefs;
					}
					throw new Exception("orientation.strings is null");
				}
				throw new Exception("sexualPref is null");
			}
			throw new Exception("flingPrefs is null");
		}
		throw new Exception("Entity s is null");

	}

	@ApiMethod(name = "users.prefs.fling.save", path = "users/prefs/fling/save", httpMethod = HttpMethod.POST)
	public BooleanResponse saveFlingPrefs(PrefsList prefs) {

		EmbeddedEntity embedded = new EmbeddedEntity();

		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, prefs.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, prefs.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);

		EmbeddedEntity sexualpref = new EmbeddedEntity();
		Map pref = (LinkedHashMap) prefs.prefs.get(0);
		sexualpref.setProperty("strings",
				(ArrayList<String>) pref.get("strings"));
		sexualpref.setProperty("index", (int) pref.get("index"));

		embedded.setProperty("Sexual Preference", sexualpref);
		embedded.setProperty("Age", (int) prefs.prefs.get(1));
		s.setProperty("flingPrefs", embedded);
		datastore.put(s);

		return BooleanResponse.createResponse(true);
	}
	@ApiMethod(name = "users.prefs.volunteering.retrieve", path = "users/prefs/volunteering/retrieve", httpMethod = HttpMethod.GET)
	public CheckBoxPreference retrieveVolunteeringPrefs(
			@Named("screen_name") String screenName,
			@Named("password") String password) throws Exception {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		if (s != null) {
			CheckBoxPreference prefs = new CheckBoxPreference();
			prefs.screenName = screenName;
			prefs.password = password;
			EmbeddedEntity hobbyPrefs = (EmbeddedEntity) s
					.getProperty("volunteeringPrefs");
			if (hobbyPrefs != null) {
				EmbeddedEntity interests = (EmbeddedEntity) hobbyPrefs
						.getProperty("Location");
				if (interests != null) {
					ArrayList<String> options = (ArrayList<String>) interests
							.getProperty("options");
					ArrayList<Boolean> selected = (ArrayList<Boolean>) interests
							.getProperty("selected");
					if (options != null) {
						prefs.options = options;
						prefs.selected = selected;

						return prefs;
					}
					throw new Exception("options is null");
				}
				throw new Exception("location is null");
			}
			throw new Exception("volunteeringPrefs is null");
		}
		throw new Exception("Entity s is null");

	}

	@ApiMethod(name = "users.prefs.volunteering.save", path = "users/prefs/volunteering/save", httpMethod = HttpMethod.POST)
	public BooleanResponse saveVolunteeringPrefs(CheckBoxPreference prefs) {

		EmbeddedEntity embedded = new EmbeddedEntity();

		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, prefs.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, prefs.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);

		EmbeddedEntity interests = new EmbeddedEntity();
		interests.setProperty("options", prefs.options);
		interests.setProperty("selected", prefs.selected);

		embedded.setProperty("Location", interests);

		s.setProperty("volunteeringPrefs", embedded);
		datastore.put(s);

		return BooleanResponse.createResponse(true);
	}

	@ApiMethod(name = "users.prefs.social.retrieve", path = "users/prefs/social/retrieve", httpMethod = HttpMethod.GET)
	public CheckBoxPreference retrieveSocialPrefs(
			@Named("screen_name") String screenName,
			@Named("password") String password) throws Exception {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		if (s != null) {
			CheckBoxPreference prefs = new CheckBoxPreference();
			prefs.screenName = screenName;
			prefs.password = password;
			EmbeddedEntity hobbyPrefs = (EmbeddedEntity) s
					.getProperty("socialPrefs");
			if (hobbyPrefs != null) {
				EmbeddedEntity interests = (EmbeddedEntity) hobbyPrefs
						.getProperty("Interests");
				if (interests != null) {
					ArrayList<String> options = (ArrayList<String>) interests
							.getProperty("options");
					ArrayList<Boolean> selected = (ArrayList<Boolean>) interests
							.getProperty("selected");
					if (options != null) {
						prefs.options = options;
						prefs.selected = selected;

						return prefs;
					}
					throw new Exception("options is null");
				}
				throw new Exception("interests is null");
			}
			throw new Exception("socialPrefs is null");
		}
		throw new Exception("Entity s is null");

	}

	@ApiMethod(name = "users.prefs.social.save", path = "users/prefs/social/save", httpMethod = HttpMethod.POST)
	public BooleanResponse saveSocialPrefs(CheckBoxPreference prefs) {

		EmbeddedEntity embedded = new EmbeddedEntity();

		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, prefs.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, prefs.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);

		EmbeddedEntity interests = new EmbeddedEntity();
		interests.setProperty("options", prefs.options);
		interests.setProperty("selected", prefs.selected);

		embedded.setProperty("Interests", interests);

		s.setProperty("socialPrefs", embedded);
		datastore.put(s);

		return BooleanResponse.createResponse(true);
	}
	
	@ApiMethod(name = "users.getuploadurl", path = "users/getuploadurl", httpMethod = HttpMethod.GET)
	public StringResponse getUploadURL() {
		StringResponse resp = new StringResponse();
		resp.response = blobstore.createUploadUrl("/upload");
		return resp;
	}
	
	@ApiMethod(name = "users.profile.blobkey.save", path = "users/profile/blobkey/save", httpMethod = HttpMethod.POST)
	public BooleanResponse saveBlobKeyAndServingURL(PictureUploadData picData) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, picData.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, picData.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		
		EmbeddedEntity pictureData = new EmbeddedEntity();
		pictureData.setProperty("blobKey", picData.blobKey);
		pictureData.setProperty("servingURL", picData.servingURL);
		
		s.setProperty("Profile Picture Data", pictureData);
		
		datastore.put(s);

		return BooleanResponse.createResponse(true);
	}
	
	@ApiMethod(name = "users.profile.blobkey.retrieve", path = "users/profile/blobkey/retrieve", httpMethod = HttpMethod.GET)
	public PictureUploadData retrieveBlobKeyAndServingURL(@Named("screen_name") String screenName,
			@Named("password") String password) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		
		EmbeddedEntity pictureData = (EmbeddedEntity) s.getProperty("Profile Picture Data");
		PictureUploadData data  = new PictureUploadData();
		data.screenName = screenName;
		data.password = password;
		
		data.blobKey = (String) pictureData.getProperty("blobKey");
		data.servingURL = (String) pictureData.getProperty("servingURL");
		
		return data;
	}

}
