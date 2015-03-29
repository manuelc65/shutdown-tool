import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.*;

class ClientConnection
{
	Socket socket;
	boolean isConnected = false;
	int _PORT;
	String _IP;
	String _MESSAGE;

	public ClientConnection(int port, String ip, String message)
	{
		_PORT = port;
		_IP = ip;
		_MESSAGE = message;

		try
		{
			InetAddress server = InetAddress.getByName(_IP);
			socket = new Socket(server, _PORT);
			isConnected = true;
		}
		catch(UnknownHostException uhe)
		{
				uhe.printStackTrace();
				System.out.println("Unable to contact server.");
		}
		catch(IOException ioe)
		{
				ioe.printStackTrace();
				System.out.println("IO Exception");
		}

	}

	public void setMessage(String m)
	{
		_MESSAGE = m;
	}

	public String getMessage()
	{
		return _MESSAGE;
	}

	public void sendMessage()
	{
		try
		{
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			out.println(_MESSAGE);
		}
		catch(IOException ioe)
		{
			System.out.println("IO Exception");
		}
	}
}