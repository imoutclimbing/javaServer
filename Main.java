
import java.io.*;
import java.lang.*;
import java.net.*;

/**
*		
*
* <b><big>General Info</big></b><br>
* Author: Douglas Dunlap<br>
* Email: ddunlap3@mscd.edu<br>
* Program: #4 - Server<br>
* Version: 2<br>
* Written For: MSCD CSS4727 / Summer 2004<br>
* Written With: J2SE 1.4.0_01<br>
* Development Environment: jGrasp 1.5.0<br>
* Compiler: javac included with J2SE 1.4.0_01<br>
* Platform: IBM 600e (Intel PII-400Mhz), Microsoft Windows XP Professional<br>
* Original Write: 8/1/2004<br>
* Last Mod: 8/4/04<br><br><br>
*
* <b><big>Statement of Purpose</big></b><br> 
* This program creates a web server that takes two arguments from the command line:<br>
* the port to bind to and a directory to serve files from.  The server will accept<br>
* a single GET request, serve it, and then quit.  The program will also print the<br>
* results of the attempt to the screen.<br><br><br>
*
* <b><big>Description of Input and Output</big></b><br>
* <i>Input</i>: Two command-line arguments: A valid port and directory to serve files from.<br>
* <i>Output</i>: A file to a socket (file = GET command from client). Message to screen.<br><br><br>
* 
* <b><big>How to Use</big></b><br>
* 1) Save this file as 'Main.java'.<br>
* 2) Compile the source code with 'javac Main.java'.<br>
* 3) Run by typing 'java Main (port) (directory to serve from)'<br><br><br>
* 
* <b><big>Assumptions on Expected Data</big></b><br>
* Only valid ports and directories. Anything else will throw an exception.<br>
* Port must be entered (since two args are possible); however, a directory<br>
* is not necessary. The default path is currently '.'.<br><br><br>
*
* <b><big>Exceptions</big></b><br>
*
* <i>FileNotFoundException</i> 
* <ul>
* <li>FileInputStream Class</li>
* 	<ul>
* 	<li>FileInputStream(String name) - if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.</li>
* 	</ul>
* </ul>
*
* <i>NumberFormatException</i>
* <ul>
* <li>Integer Class</li>
* 	<ul>
* 	<li>parstInt() - if the string does not contain a parsable integer.</li>
* 	</ul>
* </ul>
*
* <i>IllegalBlockingModeException</i>
* <ul>
* <li>ServerSocket Class</li>
* 	<ul>
* 	<li>accept() - if this socket has an associated channel, and the channel is in non-blocking mode.</li>
* 	</ul>
* </ul>
*
* <i>SocketTimeoutException</i>
* <ul>
* <li>ServerSocket Class</li>
* 	<ul>
* 	<li>accept() - if a timeout was previously set with setSoTimeout and the timeout has been reached.</li>
* 	</ul>
* </ul>
*
* <i>SecurityException</i>
* <ul>
* <li>FileInputStream Class</li>
* 	<ul>
* 	<li>FileInputStream(String name) - .</li>
* 	</ul>
* <li>ServerSocket Class</li>
* 	<ul>
* 	<li>ServerSocket(port) - if a security manager exists and its checkListen method doesn't allow the operation.</li>
*  <li>accept() - if a security manager exists and its checkListen method doesn't allow the operation.</li>
* 	</ul>
* </ul>
*
* <i>IOException</i>     done
* <ul>
* <li>FileInputStream Class</li>
* 	<ul>
* 	<li>read() - if an I/O error occurs.</li>
* 	</ul>
* <li>BufferedInputStream Class</li>
* 	<ul>
* 	<li>read() - if an I/O error occurs.</li>
* 	</ul>
* <li>BufferedOutputStream Class</li>
* 	<ul>
* 	<li>write() - if an I/O error occurs.</li>
*  <li>flush() - if an I/O error occurs.</li>
* 	</ul>
* <li>Socket Class</li>
* 	<ul>
* 	<li>close() - if an I/O error occurs.</li>
* 	</ul>
* <li>ServerSocket Class</li>
* 	<ul>
* 	<li>ServerSocket(port) - if an I/O error occurs.</li>
*  <li>accept() - if an I/O error occurs when waiting for a connection.</li>
* 	</ul>
* </ul><br>
* 
* <b><big>Test Design</big></b><br>
* <small><table border="border">
* <tr><th>Test Case</th>
* <th>Expected Result</th>
* <th>Misc</th></tr>
*
* <tr><td>Valid port, valid path</td>	
* <td>OK</td>
* <td>All info included</td></tr>
*
* <tr><td>Negative port, valid path</td>
* <td>Message & quit</td>
* <td>Port must be in 0 to 65535 range</td></tr>	 
*
* <tr><td>No port, valid path</td>
* <td>Exception</td>
* <td>Port is required.</td></tr>
*
* <tr><td>Valid port, no path</td>
* <td>OK</td>
* <td>'.' was used as default directoryt to serve from</td></tr></table></small><br><br><br>
*
* <b><big>Method Descriptions</big></b><br>
* Main - operation core of program<br><br><br>
* 
* <b><big>Bibliography</big></b><br>
* RFC 2616 - HTTP/1.1<br>
* Java Software Solutions - 3rd, Lewis and Loftus, Addison Wesley, 2003<br>
* HTML & XHTML: The Definitive Guide - 5th, Musciano & Kennedy, O'Reilly, 2002<br>
* Java Network Programming (including source exapmles) - 2nd, Harold, O'Reilly, 2000<br>
*
*
*/ 

public class Main
{
		// Purpose: This class creates a basic text-only web server.
		// Author: Douglas Dunlap
		// Email: ddunlap3@mscd.edu
		// Date Written: 8/1/04
		// Last Mod: 8/4/04
		// Version:
		// Exceptions:

	public static void main (String args[])
	{
			// Purpose: the Main method is the operational core of the program
			// Author: Douglas Dunlap
			// Email: ddunlap3@mscd.edu
			// Date Written: 8/1/04
			// Last Mod: 8/4/04
			// Version:
			// Exceptions:	

		// create variables & objects  --------------------------------------------------
			// general variables
			int port = 0;
			String path;
			String request = "";  // entire GET command from client
			String requestArray[];  // array for the parts of the request after being split
			String fileRequested;  // file to be served to client
			String z; // misc variable
			// sockets
			ServerSocket theServerSocket = null;
			Socket theSocket = null;
			// input
			BufferedInputStream bis = null;
			FileInputStream fis = null;
			// output
			BufferedOutputStream bos = null;


		try  // deal with port
		{
			port = Integer.parseInt(args[0]);  // create int for port number
		
			if (port < 0 || port >= 65535)  // if port is <0 or >65535, err&stop
			{
				System.out.println();
				System.out.println();
				System.out.println("An attempt had a invalid port.");
				System.out.println();
				System.out.println();
				System.exit(0);
			}
		}
		catch (NumberFormatException e)
		{
			System.out.println(e);
			System.exit(1);
		}		

		if (args.length < 2)  // check to make sure there are two args
		{
			path = ".";
		}
		else
		{
			path = args[1];
		}

		// set up the sockets  ----------------------------------------------------------
		try
		{
			theServerSocket = new ServerSocket(port);  // make a ServerSocket to listen on args[0]
		}
		catch (IOException e)
		{
			System.out.println (e);
			System.exit(1);
		}

		try  // create the socket and get the request
		{
			theSocket = theServerSocket.accept();  // open the socket
			bis = new BufferedInputStream(theSocket.getInputStream());  // create a stream for the request
			bos = new BufferedOutputStream(theSocket.getOutputStream());  // create a stream for the file
		}
		catch (SocketTimeoutException e)
		{
			System.out.println(e);
			System.exit(1);
		}		
		catch (IOException e)
		{
			System.out.println(e);
			System.exit(1);
		}

		// now that the socket is created, get the request from the client  -------------
		int c;

		while (true)
		{
			try
			{
				c = bis.read();  // read

				if (c == '\r' || c == '\n' || c == -1)  // test for end of 1st line
				{
					break;
				}

				request += ((char) c);  // append what was read
			}
			catch (IOException e)
			{
				System.out.println (e);
				System.exit(1);
			}
		}

		// print the requested file and message  ----------------------------------------
		requestArray = request.split(" ");
		fileRequested = requestArray[1];
		
		try  // build a FileInputStream around the path (args[1]) & file from GET command 
		{
			fis = new FileInputStream(path + fileRequested);
		}
		catch (FileNotFoundException e)
		{
			System.out.println();
			System.out.println("404 -- File Not Found");
			
			try
			{
				bos.write("404 -- File Not Found\r\n\r\n".getBytes());
			}
			catch (IOException f)
			{
				System.out.println (f);
				System.exit(1);
			}

			System.out.println();
			System.exit(1);
		}
		
		try
		{
			System.out.println();  // send an OK msg
			System.out.println("200 -- OK / Request Successful");
			bos.write("200 -- OK / Request Successful\r\n\r\n".getBytes());
			System.out.println();
		}
		catch (IOException e)
		{
			System.out.println (e);
			System.exit(1);
		}

		while (true)
		{
			try
			{
				int i = fis.read();
				if (i == -1) break;
				bos.write (i);
			}
			catch (IOException e)
			{
				System.out.println (e);
				System.exit(1);
			}
		}

		try
		{
			bos.flush();
			theSocket.close();
			System.exit(0);
		}
		catch (IOException e)
		{
			System.out.println (e);
			System.exit(1);
		}
					

	}  // method end
}  // class end