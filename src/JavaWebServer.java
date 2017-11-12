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

	private static final int fNumberOfThreads = 100;
	private static final Executor fThreadPool = Executors.newFixedThreadPool(fNumberOfThreads);

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
			fThreadPool.execute(task);
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

			out = new PrintWriter(s.getOutputStream(), true);
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
				/*
				if (responseFile.contains(".js")) {
					out.println("Content-type:  text/javascript");
				} else if (responseFile.contains(".css")) {
					out.println("Content-type: text/css");
				} else if (responseFile.contains(".html")) {
					out.println("Content-type: text/html");
				} else {
					out.println("Content-type: application/octet-stream");
				}
				*/
				out.println((new MimetypesFileTypeMap().getContentType(responseFile)));
		
				// Converts file to a string
				String response = new String(Files.readAllBytes(Paths.get(responseFile)));
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