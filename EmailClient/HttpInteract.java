/*************************************
 * Filename: HttpInteract.java
 * Names: Alexander Finnigan
 * Student-ID: 201084157
 * Date: 25/10/17
 *************************************/

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Class for downloading one object from http server.
 *
 */
public class HttpInteract {
	private String host;
	private String path;
	private String requestMessage;
	
		
	private static final int HTTP_PORT = 80;
	private static final String CRLF = "\r\n";
	private static final int BUF_SIZE = 4096; 
	private static final int MAX_OBJECT_SIZE = 102400;

 	/* Create a HttpInteract object. */
	public HttpInteract(String url) {
		
		/* Split the "URL" into "host name" and "path name", and
		 * set host and path class variables. 
		 * if URL is only a host name, use "/" as path 
		 */	
		
		/* Fill in */	
		try {
			URL aURL = new URL("file://"+url);
			host = aURL.getHost();
			path = aURL.getPath();
		} catch (Exception e) {
			System.out.println(e);
		}
	
		/* Construct requestMessage, add a header line so that
		 * server closes connection after one response. */		
	
		/* Fill in */
		requestMessage = "GET "+path+" HTTP/1.1"+CRLF
		                    +"Host: "+host+CRLF
							+"User-Agent: Firefox/3.6.10"+CRLF
							+"Accept: text/html,application/xhtml+xml"+CRLF
							+"Accept-Language: en-us,en;q=0.5"+CRLF
							+"Accept-Encoding: gzip,deflate"+CRLF
							+"Accept-Charset: ISO-8859-1,utf-8;q=0.7"+CRLF
							+"Keep-Alive: 115"+CRLF
							+"Connection: keep-alive"+CRLF
							+CRLF;
		return;
	}	
	
	
	/* Send Http request, parse response and return requested object 
	 * as a String (if no errors), 
	 * otherwise return meaningful error message. 
	 * Don't catch Exceptions. EmailClient will handle them. */		
	public String send() throws IOException {
		
		/* buffer to read object in 4kB chunks */
		char[] buf = new char[BUF_SIZE];

		/* Maximum size of object is 100kB, which should be enough for most objects. 
		 * Change constant if you need more. */		
		char[] body = new char[MAX_OBJECT_SIZE];
		
		String statusLine="";	// status line
		int status;		// status code
		String headers="";	// headers
		int bodyLength=-1;	// lenghth of body
				
		String[] tmp;
		
		/* The socket to the server */
		Socket connection;
		
		/* Streams for reading from and writing to socket */
		BufferedReader fromServer;
		DataOutputStream toServer;
		
		System.out.println("Connecting server: " +host+CRLF);
		
		/* Connect to http server on port 80.
		 * Assign input and output streams to connection. */		
		connection = new Socket(host, HTTP_PORT);
		fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		toServer = new DataOutputStream(connection.getOutputStream());
		
		System.out.println("Send request:\n" + requestMessage);


		/* Send requestMessage to http server */
		/* Fill in */
		try {
			toServer.writeBytes(requestMessage);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
		
		/* Read the status line from response message */
		statusLine= fromServer.readLine();
		System.out.println("Status Line:\n"+statusLine+CRLF);
		
		/* Extract status code from status line. If status code is not 200,
		 * close connection and return an error message. 
		 * Do NOT throw an exception */		
		/* Fill in */
		status = 0;
		if (statusLine.startsWith("HTTP/1.1 200 OK")){
			status = 200;
		}	
		if (status != 200){
			connection.close();
			System.out.println("200 reply not received from server.");
		}
		

		/* Read header lines from response message, convert to a string, 
 		 * and assign to "headers" variable. 
		 * Recall that an empty line indicates end of headers.
		 * Extract length  from "Content-Length:" (or "Content-length:") 
		 * header line, if present, and assign to "bodyLength" variable. 
		*/
		/* Fill in */ 		// requires about 10 lines of code
		headers = fromServer.readLine()+CRLF;
		headers += fromServer.readLine()+CRLF;
		headers += fromServer.readLine()+CRLF;
		headers += fromServer.readLine()+CRLF;
		headers += fromServer.readLine()+CRLF;
		headers += fromServer.readLine()+CRLF;
		String[] header = headers.split(": ");
		String contentLength = header[header.length - 1];
		headers += fromServer.readLine()+CRLF;
		headers += fromServer.readLine()+CRLF;
		headers += fromServer.readLine()+CRLF;
		System.out.println("Header:\n"+headers+CRLF);
		bodyLength = Integer.parseInt(contentLength.trim());
		

		/* If object is larger than MAX_OBJECT_SIZE, close the connection and 
		 * return meaningful message. */
		if (bodyLength > MAX_OBJECT_SIZE) {
			connection.close();
			return("Object is larger than the maximum object size. Object size is: "+bodyLength);
		}
					    
		/* Read the body in chunks of BUF_SIZE using buf[] and copy the chunk
		 * into body[]. Stop when either we have
		 * read Content-Length bytes or when the connection is
		 * closed (when there is no Content-Length in the response). 
		 * Use one of the read() methods of BufferedReader here, NOT readLine().
		 * Also make sure not to read more than MAX_OBJECT_SIZE characters.
		 */				
		int bytesRead = 0;
		/* Fill in */   // Requires 10-20 lines of code
		int i = 0;
		try{
			while (bytesRead <= bodyLength){
				while (i < BUF_SIZE && i <= bodyLength){
					buf[i] = (char) fromServer.read();
					i++;
				}
				body[bytesRead] = buf[bytesRead];
				bytesRead++;
			}
		} catch (Exception e){
			System.out.println(e);
		}
		

		/* At this points body[] should hold to body of the downloaded object and 
		 * bytesRead should hold the number of bytes read from the BufferedReader
		 */
		
		/* Close connection and return object as String. */
		System.out.println("Done reading file. Closing connection.");
		connection.close();
		return(new String(body, 0, bytesRead));
	}
}