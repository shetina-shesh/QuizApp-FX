package Server;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection (Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendString(String message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
        }
    }

    public String receiveString() throws IOException, ClassNotFoundException {
        synchronized(in) {
            return (String) in.readObject();
        }
    }

    public void sendFile(File file) throws IOException {
        synchronized (out) {
            out.writeObject(file);
        }
    }

    public File receiveFile() throws IOException, ClassNotFoundException {
        synchronized(in) {
            return (File) in.readObject();
        }
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    public void close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }


}