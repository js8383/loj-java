import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.sql.Timestamp;

import java.util.Map;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class Coordinator extends Verticle {

	// Default mode: Strongly consistent. Possible values are "strong" and
	// "causal"
	private static String consistencyType = "strong";

	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances
	 */
	private static final String dataCenter1 = "ec2-52-4-252-64.compute-1.amazonaws.com";
	private static final String dataCenter2 = "ec2-52-5-128-3.compute-1.amazonaws.com";
	private static final String dataCenter3 = "ec2-52-4-209-62.compute-1.amazonaws.com";

	private class PriorityRequest implements Comparable<PriorityRequest> {
		private String timestamp;
		private String key;
		private String request;

		public PriorityRequest(String iTimestamp, String iKey, String iRequest) {
			timestamp = iTimestamp;
			key = iKey;
			request = iRequest;
		}

		public String getTimeStamp() {
			return timestamp;
		}

		public String getKey() {
			return key;
		}

		public String getRequest() {
			return request;
		}

		@Override
		public int compareTo(PriorityRequest p) {
			return timestamp.compareTo(p.timestamp);
			// return indicator < 0 ? -1 : indicator > 0 ? 1 : 0;
		}

		public boolean fastEqual(String iTimestamp, String iKey, String iRequest) {
			return timestamp.equals(iTimestamp) && key.equals(iKey) && request.equals(iRequest);
		}
	}

	private static final Map<String, PriorityQueue<PriorityRequest>> queueMapAll = new HashMap<String, PriorityQueue<PriorityRequest>>();
	private static final Map<String, PriorityQueue<PriorityRequest>> queueMapD1 = new HashMap<String, PriorityQueue<PriorityRequest>>();
	private static final Map<String, PriorityQueue<PriorityRequest>> queueMapD2 = new HashMap<String, PriorityQueue<PriorityRequest>>();
	private static final Map<String, PriorityQueue<PriorityRequest>> queueMapD3 = new HashMap<String, PriorityQueue<PriorityRequest>>();

	@Override
	public void start() {
		// DO NOT MODIFY THIS
		KeyValueLib.dataCenters.put(dataCenter1, 1);
		KeyValueLib.dataCenters.put(dataCenter2, 2);
		KeyValueLib.dataCenters.put(dataCenter3, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String value = map.get("value");
				// You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System.currentTimeMillis()
						+ TimeZone.getTimeZone("EST").getRawOffset()).toString();

				System.out.println("Put: " + key + "," + value + "," + timestamp);
				Thread t = new Thread(new Runnable() {
					public void run() {
						// TODO: Write code for PUT operation here.
						// Each PUT operation is handled in a different thread.
						// Highly recommended that you make use of helper
						// functions.

						/* ------------------------------------------------- */
						boolean first = false;
						boolean added = false;

						if (consistencyType.equals("strong")) {
							final PriorityQueue<PriorityRequest> queueAll;
							synchronized (queueMapAll) {
								if (!queueMapAll.containsKey(key)) {
									queueMapAll.put(key, new PriorityQueue<PriorityRequest>());
								}
								queueAll = queueMapAll.get(key);
							}

							while (true) {
								synchronized (queueAll) {
									if (!added) {
										queueAll.add(new PriorityRequest(timestamp, key, "Put"));
										added = true;
									}

									if (queueAll.peek().fastEqual(timestamp, key, "Put")) {
										first = true;
									} else {
										first = false;
									}
								}

								if (first) {
									try {
										KeyValueLib.PUT(dataCenter1, key, value);
										KeyValueLib.PUT(dataCenter2, key, value);
										KeyValueLib.PUT(dataCenter3, key, value);
										System.out.println("Put sucessful: " + key + "," + value);

									} catch (IOException e) {
										// do something
									}

									synchronized (queueAll) {
										queueAll.remove();
									}

									break;

								}

							}

							/* ------------------------------------------------- */
						} else if (consistencyType.equals("causal")) {

							Thread t1 = new Thread(new Runnable() {
								public void run() {
									boolean firstD1 = false;
									boolean addedD1 = false;
									final PriorityQueue<PriorityRequest> queueD1;
									synchronized (queueMapD1) {
										if (!queueMapD1.containsKey(key)) {
											queueMapD1.put(key, new PriorityQueue<PriorityRequest>());
										}
										queueD1 = queueMapD1.get(key);
									}
									while (true) {
										synchronized (queueD1) {
											if (!addedD1) {
												queueD1.add(new PriorityRequest(timestamp, key, "Put"));
												addedD1 = true;
											}

											if (queueD1.peek().fastEqual(timestamp, key, "Put")) {
												firstD1 = true;
											}
										}

										if (firstD1) {
											try {
												KeyValueLib.PUT(dataCenter1, key, value);
											} catch (IOException e) {
												// do something
											}
											synchronized (queueD1) {
												queueD1.remove();
											}
											break;
										}
									}
								}

							});

							Thread t2 = new Thread(new Runnable() {
								public void run() {
									boolean firstD2 = false;
									boolean addedD2 = false;
									final PriorityQueue<PriorityRequest> queueD2;
									synchronized (queueMapD2) {
										if (!queueMapD2.containsKey(key)) {
											queueMapD2.put(key, new PriorityQueue<PriorityRequest>());
										}
										queueD2 = queueMapD2.get(key);
									}
									while (true) {
										synchronized (queueD2) {
											if (!addedD2) {
												queueD2.add(new PriorityRequest(timestamp, key, "Put"));
												addedD2 = true;
											}

											if (queueD2.peek().fastEqual(timestamp, key, "Put")) {
												firstD2 = true;
											}
										}

										if (firstD2) {
											try {
												KeyValueLib.PUT(dataCenter2, key, value);
											} catch (IOException e) {
												// do something
											}
											synchronized (queueD2) {
												queueD2.remove();
											}
											break;
										}
									}
								}

							});

							Thread t3 = new Thread(new Runnable() {

								public void run() {
									boolean firstD3 = false;
									boolean addedD3 = false;
									final PriorityQueue<PriorityRequest> queueD3;
									synchronized (queueMapD3) {
										if (!queueMapD3.containsKey(key)) {
											queueMapD3.put(key, new PriorityQueue<PriorityRequest>());
										}
										queueD3 = queueMapD3.get(key);
									}
									while (true) {
										synchronized (queueD3) {
											if (!addedD3) {
												queueD3.add(new PriorityRequest(timestamp, key, "Put"));
												addedD3 = true;
											}

											if (queueD3.peek().fastEqual(timestamp, key, "Put")) {
												firstD3 = true;
											}
										}

										if (firstD3) {
											try {
												KeyValueLib.PUT(dataCenter3, key, value);
											} catch (IOException e) {
												// do something
											}
											synchronized (queueD3) {
												queueD3.remove();
											}
											break;
										}
									}
								}

							});

							t1.start();
							t2.start();
							t3.start();
						}
					}
				});
				t.start();
				req.response().end(); // Do not remove this
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String loc = map.get("loc");

				// You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System.currentTimeMillis()
						+ TimeZone.getTimeZone("EST").getRawOffset()).toString();

				System.out.println("Get: " + key + "," + loc + "," + timestamp);

				Thread t = new Thread(new Runnable() {
					public void run() {
						// TODO: Write code for GET operation here.
						// Each GET operation is handled in a different thread.
						// Highly recommended that you make use of helper
						// functions.

						/* ------------------------------------------------- */
						String result = "0";
						boolean first = false;
						boolean added = false;

						final PriorityQueue<PriorityRequest> queueAll;
						synchronized (queueMapAll) {
							if (!queueMapAll.containsKey(key)) {
								queueMapAll.put(key, new PriorityQueue<PriorityRequest>());
							}
							queueAll = queueMapAll.get(key);
						}

						if (consistencyType.equals("strong")) {
							while (true) {

								synchronized (queueAll) {
									if (!added) {
										queueAll.add(new PriorityRequest(timestamp, key, "get"));
										added = true;
									}

									if (queueAll.peek().getRequest().equals("get")) {
										first = true;
									} else {
										first = false;
									}
								}

								if (first) {

									try {
										if (loc.equals("1")) {
											result = KeyValueLib.GET(dataCenter1, key);
										} else if (loc.equals("2")) {
											result = KeyValueLib.GET(dataCenter2, key);
										} else if (loc.equals("3")) {
											result = KeyValueLib.GET(dataCenter3, key);
										}

										System.out.println("Get result: " + key + "," + result);

									} catch (IOException e) {
										// do something
									}

									synchronized (queueAll) {
										queueAll.remove();
									}

									break;
								}

							}
						} else if (consistencyType.equals("causal")) {
							try {
								if (loc.equals("1")) {
									result = KeyValueLib.GET(dataCenter1, key);
								} else if (loc.equals("2")) {
									result = KeyValueLib.GET(dataCenter2, key);
								} else if (loc.equals("3")) {
									result = KeyValueLib.GET(dataCenter3, key);
								}

								System.out.println("Get result: " + key + "," + result);

							} catch (IOException e) {
								// do something
							}
						}
						req.response().end(result);
					}
				});
				t.start();
			}
		});

		routeMatcher.get("/consistency", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				consistencyType = map.get("consistency");
				// This endpoint will be used by the auto-grader to set the
				// consistency type that your key-value store has to support.
				// You can initialize/re-initialize the required data structures
				// here
				queueMapAll.clear();
				queueMapD1.clear();
				queueMapD2.clear();
				queueMapD3.clear();

				req.response().end();
			}
		});

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type", "text/html");
				String response = "Not found.";
				req.response().putHeader("Content-Length", String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(8080);
	}
}
