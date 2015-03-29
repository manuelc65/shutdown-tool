import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class ShutdownGUI extends JFrame implements ActionListener
{
	JButton restartButton;
	JButton shutdownButton;
	ClientConnection msg = new ClientConnection(8472,"192.168.1.100",null);

	public ShutdownGUI()
	{
		super("Shutdown Program");
		Container c = getContentPane();
		JPanel buttonPanel = new JPanel();
		restartButton = new JButton("Restart");
		restartButton.addActionListener(this);
		shutdownButton = new JButton("Shutdown");
		shutdownButton.addActionListener(this);
		buttonPanel.add(restartButton);
		buttonPanel.add(shutdownButton);
		c.add(buttonPanel);
		setSize(300,300);
		setResizable(false);
		setLocation(500,200);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(restartButton))
		{
			msg.setMessage("restart");
			msg.sendMessage();

		}
		if(e.getSource().equals(shutdownButton))
		{
			msg.setMessage("shutdown");
			msg.sendMessage();

		}
	}

	public void executeShutdown()
	{
		try
		{

			Runtime.getRuntime().exec("shutdown -t 0 -s -f");
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
			Runtime.getRuntime().exec("shutdown -t 0 -r -f");
		}
		catch(Exception io)
		{
			System.out.println("Failed");
		}
	}
}