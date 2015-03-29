import java.io.*;
import java.net.*;

public class UtilClient
{
	public static void main(String[] args)
	{
		try
		{
			Socket c = new Socket("192.168.1.100", 8472);
			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			PrintWriter pw = new PrintWriter(c.getOutputStream(), true);
			BufferedReader con = new BufferedReader(new InputStreamReader(System.in));
			do
			{
				pw.println(con.readLine());
				pw.flush();
			}
			while(!con.readLine().equals("gameover"));
			System.out.println("Connection Closed");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
