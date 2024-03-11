import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.List;

public class StorageBarrel implements IStorageBarrel {

  private String HOST_NAME = "224.3.2.1";
  private InetAddress mcastaddr;
  private int PORT = 4321;

  private String typeGetNext = "GET_NEXT_URL";
  private String msgGetNext = "TYPE | " + typeGetNext;

  private MulticastSocket socket;

  public StorageBarrel() throws IOException {
    // super("StorabeBarrel " + (long) (Math.random() * 1000));
    // this.start();

    socket = new MulticastSocket(PORT);
    mcastaddr = InetAddress.getByName(HOST_NAME);
    socket.joinGroup(new InetSocketAddress(mcastaddr, 0), NetworkInterface.getByIndex(0));
  }

  @Override
  public String getNextUrl() {
    byte[] buffer = msgGetNext.getBytes();

    try {
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, mcastaddr, PORT);
      socket.send(packet);

      return receiveNextUrl();

    } catch (IOException e) {
      System.err.println("Error on getNextUrl: " + e.getMessage());
      return "";
    }
  }

  private String receiveNextUrl() throws IOException {
    byte[] buffer = new byte[1024];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    String newUrl = "";

    try {
      while (newUrl.isEmpty()) {
        socket.receive(packet);
        String res = new String(packet.getData());
        String resFormat = "TYPE | RES; " + typeGetNext;
        if (res.startsWith(resFormat)) {
          newUrl = res.substring(resFormat.length() + 1).trim();
          break;
        }
      }

      return newUrl;
    } catch (IOException e) {
      System.err.println("Error receiving next url: " + e.getMessage());
      throw e;
    }
  }

  @Override
  public int size() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'size'");
  }

  @Override
  public void addUrls(List<String> urls) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'addUrls'");
  }

}
