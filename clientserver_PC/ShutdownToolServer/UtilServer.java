import java.net.*;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;


class UtilServer
{

	private static final String SHUTDOWN = "shutdown -t 0 -s -f";
	private static final String RESTART = "shutdown -t 0 -r -f";
	private static final String SERVER_CLOSE = "gameover";
	private boolean listening;
	private static final int PORT = 8472;
	ServerSocket serverSocket;

	public UtilServer(int p)
	{
		try
		{
			serverSocket = new ServerSocket(p);
			listening = true;
			logMessage(currentTime()+ ": Server started on port " + p);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String currentTime()
		{

			String timeStamp = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date());
			return timeStamp;
	}

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

	public void serve()
	{

		try
		{
			while(listening)
			{
				Socket connection = serverSocket.accept();
				Runnable connectionHandler = new ConnectionHandler(connection);
				new Thread(connectionHandler).start();
			}

			serverSocket.close();
			//restartServer();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void restartServer()
	{
		UtilServer server = new UtilServer(PORT);
		server.serve();
	}

	public static void main(String[] args)
	{
		UtilServer server = new UtilServer(PORT);
		server.serve();
	}

	public void executeShutdown()
	{
		try
		{
			Runtime.getRuntime().exec(SHUTDOWN);
		}
		catch(Exception io)
		{
			System.out.println("Failed");
		}
	}

	public void executeRestart()
	{
		try
		{
			Runtime.getRuntime().exec(RESTART);
		}
		catch(Exception io)
		{
			System.out.println("Failed");
		}
	}
}