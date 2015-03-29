import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


class ConnectionHandler implements Runnable
{
	public void logMessage(String s)
	{
		try
		{
			File file = new File("logs/log.txt");
			if(!file.exists())
			{
				file.createNewFile();
			}

			String separator = System.getProperty("line.separator");
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(s);
			bw.append(separator);
			bw.close();
			System.out.println(s);
		}
		catch(IOException e)
		{
			System.out.println("You done gone fucked up");
		}
		//write to log

	}

	public String currentTime()
	{
		String timeStamp = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date());
		return timeStamp;
	}

	Socket socket;
	String ip;
	public ConnectionHandler(Socket s)
	{
		socket = s;
	}
	public void run()
	{
		try
		{
			ip = socket.getInetAddress().getHostAddress();
			logMessage(currentTime()+": Device connected at " + ip);

			PrintWriter out;
			BufferedReader in;
			try
			{
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				out.println("signal");
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String input;
				if((input = in.readLine()) != null)
				{
					if(input.equals("ack"))
					{
						logMessage(currentTime()+": Client acknowledged connection");
					}
					else
					{
						socket.close();
					}
				}

			}
			catch(IOException e)
			{
				logMessage(currentTime()+": Unable to send verification to client at " + ip);
			}


			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String command;
			boolean waiting = true;
			//boolean listening = true;
			do
			{
				command = br.readLine();
				if(command.equals("shutdown"))
				{
					logMessage(currentTime()+": Shutdown order received.");
					//executeShutdown()
				}
				else if(command.equals("restart"))
				{
					logMessage(currentTime()+": Restart order received.");
					//executeRestart();
				}
				else if(command.equals("gameover"))
				{
					logMessage(currentTime()+": Exit Server order received.");
					//socket.close();
					waiting = false;
				}
				else
				{
					logMessage(currentTime()+": Unrecognised command received - SOCKET CLOSED.");
					waiting = false;
				}
			}while(waiting);
		}
		catch(Exception e)
		{
			logMessage(currentTime()+": Device disconnected from " +ip);
		}
	}
}