import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.activation.MimetypesFileTypeMap;

public class JavaWebServer {

	private static final int NUMBER_OF_THREADS = 100;
	private static final Executor THREAD_POOL = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(8080);

		// waits for a connection
		while (true) {
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					HandleRequest(connection);
				}
			};
			THREAD_POOL.execute(task);

		}

	}

	private static void HandleRequest(Socket s) {
		BufferedReader in;
		PrintWriter out;
		String request;
		String responseFile;

		try {
			String webServerAddress = s.getInetAddress().toString();
			System.out.println("New Connection:" + webServerAddress);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));

			request = in.readLine();
			System.out.println("--- Client request: " + request);
			while (in.ready()) {
				System.out.println(in.readLine());

			}
			out = new PrintWriter(s.getOutputStream(), true);

			// System.out.println(new String(in.readLine()));
			out.println("HTTP/1.0 200");
			if (request.contains("GET")) {
				String route = request.substring(request.indexOf('/'), request.indexOf(" H"));
				if (route.equals("/")) {
					route = "/index.html";
					System.out.println(route);
				} else if (route.equals("/test")) {
					route = "/test.html";
				}
				responseFile = route.substring(1);

				String mimeType = null;
				int index = responseFile.lastIndexOf('.');
				if (index == -1) {
					mimeType = "application/octet-stream";
				} else {
					String fileExtension = responseFile.substring(index).toLowerCase();

					// Try common types first
					if (fileExtension.equals(".html")) {
						mimeType = "text/html";
					} else if (fileExtension.equals(".css")) {
						mimeType = "text/css";
					} else if (fileExtension.equals(".js")) {
						mimeType = "application/javascript";
					} else if (fileExtension.equals(".gif")) {
						mimeType = "image/gif";
					} else if (fileExtension.equals(".png")) {
						mimeType = "image/png";
					} else if (fileExtension.equals(".txt")) {
						mimeType = "text/plain";
					} else if (fileExtension.equals(".xml")) {
						mimeType = "application/xml";
					} else if (fileExtension.equals(".json")) {
						mimeType = "application/json";
					} else {
						MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
						mimeType = mimeTypesMap.getContentType(responseFile);
					}
				}

				// System.out.println("mimeType " + mimeType);
				out.println("Content-type: " + mimeType);

				// Converts file to a string
				String response = new String(Files.readAllBytes(Paths.get(responseFile))).replaceAll("ipHeRe",
						webServerAddress.substring(1));
				out.println("Server-name: myserver");
				out.println("Content-length: " + response.length());
				out.println("");
				out.println(response);
			}

			out.flush();
			out.close();
			s.close();
		} catch (IOException e) {
			System.out.println("Failed respond to client request: " + e.getMessage());
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

}