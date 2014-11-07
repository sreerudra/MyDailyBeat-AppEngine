package com.verve.api;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

@Api(name = "mydailybeat", version = "v1")
public class MyDailyBeatAPI {

	public static DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();
	public static BlobstoreService blobstore = BlobstoreServiceFactory
			.getBlobstoreService();

	@ApiMethod(name = "users.joingroup", path = "users/groups/join", httpMethod = HttpMethod.POST)
	public BooleanResponse joinGroup(JoinGroupPostObject postObj) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, postObj.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, postObj.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		Query q2 = new Query("GroupsList");
		List<Entity> groupsList = (List<Entity>) datastore.prepare(q2).asList(
				FetchOptions.Builder.withDefaults());
		ArrayList<Integer> groups = (ArrayList<Integer>) s
				.getProperty("groups");

		if (groups == null) {
			groups = new ArrayList<Integer>();
		}

		for (int i = 0; i < groupsList.size(); i++) {
			if (((String) groupsList.get(i).getProperty("groupName"))
					.equalsIgnoreCase(postObj.groupName)) {
				if (!groups.contains((int) (((Long) groupsList.get(i)
						.getProperty("id")).longValue()))) {
					groups.add((int) (((Long) groupsList.get(i).getProperty(
							"id")).longValue()));
				} else {
					return BooleanResponse.createResponse(false,
							"Already a member of this group");
				}

			}
		}

		s.setProperty("groups", groups);
		datastore.put(s);

		return BooleanResponse.createResponse(true);
	}

	@ApiMethod(name = "groups.create", path = "groups/create", httpMethod = HttpMethod.POST)
	public BooleanResponse createGroup(CreateGroupPostObject postObj) {
		Query q = new Query("GroupsList");
		List<Entity> groupsList = (List<Entity>) datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		if (groupsList == null) {
			groupsList = new ArrayList<Entity>();
		}

		if (groupsList.size() > 0) {
			ArrayList<Integer> idList = new ArrayList<Integer>();
			for (int i = 0; i < groupsList.size(); ++i) {
				idList.add((int) (((Long) groupsList.get(i).getProperty("id"))
						.longValue()));
			}
			Collections.sort(idList);
			Group.ID_START = idList.get(idList.size() - 1);
		} else {
			Group.ID_START = 0;
		}

		Query q2 = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, postObj.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, postObj.password);
		List<Entity> results = datastore.prepare(q2).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		ArrayList<Integer> groups = (ArrayList<Integer>) s
				.getProperty("groups");

		if (groups == null) {
			groups = new ArrayList<Integer>();
		}

		for (int i = 0; i < groupsList.size(); i++) {
			if (((String) groupsList.get(i).getProperty("groupName"))
					.equalsIgnoreCase(postObj.groupName)) {
				return BooleanResponse.createResponse(false,
						"Group already exists");
			}
		}
		Group g = new Group(postObj.groupName, postObj.screenName);
		Entity newGroup = new Entity("GroupsList");
		newGroup.setProperty("groupName", g.groupName);
		newGroup.setProperty("adminScreenName", g.adminScreenName);
		newGroup.setProperty("id", g.id);
		datastore.put(newGroup);
		return this.joinGroup(new JoinGroupPostObject(postObj.groupName,
				postObj.screenName, postObj.password));

	}

	@ApiMethod(name = "groups.get", path = "groups/get", httpMethod = HttpMethod.GET)
	public ArrayList<Group> getGroupsForUser(
			@Named("screen_name") String screenName,
			@Named("password") String password) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName).addFilter("password",
				Query.FilterOperator.EQUAL, password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		ArrayList<Long> groups = (ArrayList<Long>) s.getProperty("groups");
		Query q2 = new Query("GroupsList");
		List<Entity> groupsList = (List<Entity>) datastore.prepare(q2).asList(
				FetchOptions.Builder.withDefaults());
		ArrayList<Group> subList = new ArrayList<Group>();
		for (int i = 0; i < groups.size(); i++) {
			for (int j = 0; j < groupsList.size(); j++) {
				Long value1 = (Long) groupsList.get(j).getProperty("id");
				Long value2 = groups.get(i).longValue();
				if (value1.equals(value2)) {
					Group g = new Group((String) groupsList.get(j).getProperty(
							"groupName"), (String) groupsList.get(j)
							.getProperty("adminScreenName"),
							(int) (((Long) groupsList.get(j).getProperty("id"))
									.longValue()), (String) groupsList.get(j)
									.getProperty("blobKey"),
							(String) groupsList.get(j)
									.getProperty("servingURL"));
					subList.add(g);
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

	@ApiMethod(name = "users.standard.edit", path = "users/edit", httpMethod = HttpMethod.POST)
	public BooleanResponse editStandardUser(VerveStandardUser info) {

		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, info.screenName).addFilter(
				"password", Query.FilterOperator.EQUAL, info.password);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);
		s.setProperty("name", info.name);
		if (!info.email.equalsIgnoreCase((String) s.getProperty("email"))) {
			s.setProperty("email", info.email);
			s.setProperty("md5key", Constants.generateMD5(info.email));
			s.setProperty("verified", Boolean.FALSE);
		}

		s.setProperty("mobile", info.mobile);
		s.setProperty("zipcode", info.zipcode);
		s.setProperty("birth_month", info.birth_month);
		s.setProperty("birth_year", info.birth_year);

		try {

			datastore.put(s);

		} catch (Exception e) {
			return BooleanResponse.createResponse(false);
		}

		if (!((Boolean) s.getProperty("verified"))) {
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
	public BooleanResponse saveProfileBlobKeyAndServingURL(
			ProfilePictureUploadData picData) {
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
	public ProfilePictureRetrievalResponse retrieveProfileBlobKeyAndServingURL(
			@Named("screen_name") String screenName) {
		Query q = new Query("StandardUser").addFilter("screenName",
				Query.FilterOperator.EQUAL, screenName);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		Entity s = results.get(0);

		EmbeddedEntity pictureData = (EmbeddedEntity) s
				.getProperty("Profile Picture Data");
		ProfilePictureRetrievalResponse data = new ProfilePictureRetrievalResponse();
		if (pictureData != null) {
			data.screenName = screenName;

			data.blobKey = (String) pictureData.getProperty("blobKey");
			data.servingURL = (String) pictureData.getProperty("servingURL");
		}

		return data;
	}

	@ApiMethod(name = "groups.blobkey.save", path = "groups/blobkey/save", httpMethod = HttpMethod.POST)
	public BooleanResponse saveGroupBlobKeyAndServingURL(
			GroupPictureUploadData picData) {
		Query q2 = new Query("GroupsList");
		List<Entity> groupsList = (List<Entity>) datastore.prepare(q2).asList(
				FetchOptions.Builder.withDefaults());
		Entity e = new Entity("GroupsList");
		for (int i = 0; i < groupsList.size(); ++i) {
			Entity e1 = groupsList.get(i);
			if (picData.id == ((Long) e1.getProperty("id")).intValue()) {
				e = e1;
				break;
			}
		}

		EmbeddedEntity pictureData = new EmbeddedEntity();
		pictureData.setProperty("blobKey", picData.blobKey);
		pictureData.setProperty("servingURL", picData.servingURL);

		e.setProperty("Picture Data", pictureData);

		datastore.put(e);

		return BooleanResponse.createResponse(true);
	}

	@ApiMethod(name = "groups.blobkey.retrieve", path = "groups/blobkey/retrieve", httpMethod = HttpMethod.GET)
	public GroupPictureUploadData retrieveGroupBlobKeyAndServingURL(
			@Named("id") int id) {
		Query q2 = new Query("GroupsList");
		List<Entity> groupsList = (List<Entity>) datastore.prepare(q2).asList(
				FetchOptions.Builder.withDefaults());
		Entity e = new Entity("GroupsList");
		for (int i = 0; i < groupsList.size(); ++i) {
			Entity e1 = groupsList.get(i);
			if (id == ((Long) e1.getProperty("id")).intValue()) {
				e = e1;
				break;
			}
		}

		EmbeddedEntity pictureData = (EmbeddedEntity) e
				.getProperty("Picture Data");
		GroupPictureUploadData data = new GroupPictureUploadData();

		if (pictureData != null) {
			data.id = id;

			data.blobKey = (String) pictureData.getProperty("blobKey");
			data.servingURL = (String) pictureData.getProperty("servingURL");
		}

		return data;
	}

	@ApiMethod(name = "groups.post", path = "groups/post", httpMethod = HttpMethod.POST)
	public BooleanResponse writePost(WritePostRequestObject obj) {
		Query q = new Query("Post");
		List<Entity> postList = (List<Entity>) datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		if (postList == null) {
			postList = new ArrayList<Entity>();
		}

		if (postList.size() > 0) {
			ArrayList<Integer> idList = new ArrayList<Integer>();
			for (int i = 0; i < postList.size(); ++i) {
				idList.add((int) (((Long) postList.get(i)
						.getProperty("post_id")).longValue()));
			}
			Collections.sort(idList);
			Post.ID_START = idList.get(idList.size() - 1);
		} else {
			Post.ID_START = 0;
		}
		Post p = new Post(obj.postText, obj.blobKey, obj.servingURL,
				obj.userScreenName, obj.when);
		Entity e = new Entity("Post");
		e.setProperty("postText", obj.postText);
		e.setProperty("blobKey", obj.blobKey);
		e.setProperty("servingURL", obj.servingURL);
		e.setProperty("userScreenName", obj.userScreenName);
		e.setProperty("when", obj.when);
		e.setProperty("post_id", p.id);

		Entity f = new Entity("PostList");
		f.setProperty("userScreenName", obj.userScreenName);
		f.setProperty("post_id", p.id);
		f.setProperty("group_id", obj.id);

		datastore.put(e);
		datastore.put(f);

		return BooleanResponse.createResponse(true);

	}

	@ApiMethod(name = "groups.posts.get", path = "groups/posts/get", httpMethod = HttpMethod.GET)
	public ArrayList<Post> getPostsForGroupWithId(@Named("id") int id) {
		Query q = new Query("PostList").addFilter("group_id",
				Query.FilterOperator.EQUAL, id);
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		ArrayList<Post> posts = new ArrayList<Post>();
		for (int i = 0; i < results.size(); ++i) {
			Entity postListEntry = results.get(i);
			Query q2 = new Query("Post").addFilter("post_id",
					FilterOperator.EQUAL, postListEntry.getProperty("post_id"));
			Entity post = datastore.prepare(q2).asSingleEntity();
			Post p = new Post(
					((Long) postListEntry.getProperty("post_id")).intValue(),
					(String) post.getProperty("postText"),
					(String) post.getProperty("blobKey"),
					(String) post.getProperty("servingURL"),
					(String) post.getProperty("userScreenName"),
					((Long) post.getProperty("when")).intValue());
			posts.add(p);
		}

		return posts;
	}

	@ApiMethod(name = "groups.posts.delete", path = "groups/posts/delete", httpMethod = HttpMethod.GET)
	public BooleanResponse deletePost(@Named("id") int id) {
		Query q = new Query("PostList").addFilter("post_id",
				Query.FilterOperator.EQUAL, id);
		Entity e = datastore.prepare(q).asSingleEntity();
		datastore.delete(e.getKey());
		Query q2 = new Query("Post").addFilter("post_id", FilterOperator.EQUAL,
				id);
		Entity post = datastore.prepare(q2).asSingleEntity();
		if (post.getProperty("blobKey") != null) {
			BlobKey imgKey = new BlobKey((String) post.getProperty("blobKey"));
			blobstore.delete(imgKey);
		}
		datastore.delete(post.getKey());

		return BooleanResponse.createResponse(true);
	}

	@ApiMethod(name = "groups.delete", path = "groups/delete", httpMethod = HttpMethod.GET)
	public BooleanResponse deleteGroup(@Named("id") int id) {
		Query groupsQuery = new Query("GroupsList").addFilter("id",
				FilterOperator.EQUAL, id);
		Entity group = datastore.prepare(groupsQuery).asSingleEntity();
		EmbeddedEntity pictureData = (EmbeddedEntity) group
				.getProperty("Picture Data");
		if (pictureData != null) {
			BlobKey imgKey = new BlobKey(
					(String) pictureData.getProperty("blobKey"));
			blobstore.delete(imgKey);
		}
		datastore.delete(group.getKey());

		Query usersQuery = new Query("StandardUser");
		List<Entity> users = (List<Entity>) datastore.prepare(usersQuery)
				.asList(FetchOptions.Builder.withDefaults());

		for (Entity s : users) {
			ArrayList<Long> groups = (ArrayList<Long>) s.getProperty("groups");
			Long group_id = new Long(id);
			int index = Collections.binarySearch(groups, group_id);
			if (index >= 0) {
				groups.remove(index);
			} else {
				return BooleanResponse.createResponse(false);
			}
			s.setProperty("groups", groups);
			datastore.put(s);
		}
		
		Query q2 = new Query("PostList").addFilter("group_id",
				Query.FilterOperator.EQUAL, id);
		List<Entity> it = datastore.prepare(q2).asList(FetchOptions.Builder.withDefaults());
		for (Entity post : it) {
			int post_id = ((Long) post.getProperty("post_id")).intValue();
			this.deletePost(post_id);
		}

		return BooleanResponse.createResponse(true);
	}
	
	@ApiMethod(name = "users.search.screenname", path = "users/search/screenName", httpMethod = HttpMethod.GET)
	public List<VerveStandardUser> getUsersWithScreenNameContainingString(@Named("query") String screenName, @Named("sort_order") int sort_order) {
		Query q;
		if (sort_order == 0) {
			q = new Query("StandardUser").addSort("screenName", SortDirection.ASCENDING);
		} else {
			q = new Query("StandardUser").addSort("screenName", SortDirection.DESCENDING);
		}
		
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		ArrayList<VerveStandardUser> users = new ArrayList<VerveStandardUser>();
		for (Entity entry : results) {
			if (((String) entry.getProperty("screenName")).toLowerCase().contains(screenName.toLowerCase())) {
				users.add(this.getUserInfo(((String) entry.getProperty("screenName")), ((String) entry.getProperty("password"))));
			}
		}
		
		return users;
	}
	
	@ApiMethod(name = "users.search.name", path = "users/search/name", httpMethod = HttpMethod.GET)
	public List<VerveStandardUser> getUsersWithNameContainingString(@Named("query") String name, @Named("sort_order") int sort_order) {
		Query q;
		if (sort_order == 0) {
			q = new Query("StandardUser").addSort("name", SortDirection.ASCENDING);
		} else {
			q = new Query("StandardUser").addSort("name", SortDirection.DESCENDING);
		}
		
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		ArrayList<VerveStandardUser> users = new ArrayList<VerveStandardUser>();
		for (Entity entry : results) {
			if (((String) entry.getProperty("name")).toLowerCase().contains(name.toLowerCase())) {
				users.add(this.getUserInfo(((String) entry.getProperty("screenName")), ((String) entry.getProperty("password"))));
			}
		}
		
		return users;
	}
	
	@ApiMethod(name = "users.search.email", path = "users/search/email", httpMethod = HttpMethod.GET)
	public List<VerveStandardUser> getUsersWithEmailContainingString(@Named("query") String email, @Named("sort_order") int sort_order) {
		Query q;
		if (sort_order == 0) {
			q = new Query("StandardUser").addSort("email", SortDirection.ASCENDING);
		} else {
			q = new Query("StandardUser").addSort("email", SortDirection.DESCENDING);
		}
		
		List<Entity> results = datastore.prepare(q).asList(
				FetchOptions.Builder.withDefaults());
		ArrayList<VerveStandardUser> users = new ArrayList<VerveStandardUser>();
		for (Entity entry : results) {
			if (((String) entry.getProperty("email")).toLowerCase().contains(email.toLowerCase())) {
				users.add(this.getUserInfo(((String) entry.getProperty("screenName")), ((String) entry.getProperty("password"))));
			}
		}
		
		return users;
	}
	
}
