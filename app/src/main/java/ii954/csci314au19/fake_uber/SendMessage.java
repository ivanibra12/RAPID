package ii954.csci314au19.fake_uber;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendMessage extends AsyncTask<String,Void,Void>
{

    Socket socket;
    PrintWriter printWriter;

    String publicIP="203.10.91.76";
    String localIP="10.14.147.11";
    String localip = "10.14.81.177";
    int portNum = 1234;

    @Override
    protected Void doInBackground(String... voids) {

        String message = voids[0];

        try
        {
            socket = new Socket (UserMainActivity.serverIPaddress,portNum);

            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(message);
            outputStream.flush();
            outputStream.close();

            socket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
