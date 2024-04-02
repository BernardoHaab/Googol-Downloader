import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Set;

public class StorageBarrel implements IStorageBarrel {

  private final Integer BUFFER_SIZE = 1024;
  private final String HOST_NAME = "224.3.2.1";
  private final int PORT = 4321;

  private InetAddress group;
  private MulticastSocket socket;

  StorageBarrel() throws IOException {
    // super("StorageBarrel");
    // // StorageBarrel server = new StorageBarrel();
    // start();

    socket = new MulticastSocket();
    group = InetAddress.getByName(HOST_NAME);
    // socket.joinGroup(new InetSocketAddress(mcastaddr, 0),
    // NetworkInterface.getByIndex(0));
  }

  @Override
  public void updateStorageBarrels(Set<String> words, String url) throws IOException {
    // String message = "TESTE";
    // byte[] buffer = message.getBytes();

    String message = "TYPE | WORD_LIST; URL | " + url + ";" + " word_COUNT | " + words.size() + ";";

    int buffer_size = 0;

    int id = 0;

    for (String word : words) {
      String newWords = id + " | " + word + ";";

      if (buffer_size + newWords.getBytes().length > BUFFER_SIZE) {
        byte[] buffer = message.getBytes();
        System.out.println("---------LIMITE----------");
        System.out.println("Sending: " + message);
        System.out.println("buffer size: " + buffer.length);
        System.out.println("buffer size: " + buffer_size);
        DatagramPacket packet = new DatagramPacket(buffer, buffer_size, group, PORT);
        socket.send(packet);
        message = "TYPE | WORD_LIST; URL | " + url + ";" + " word_COUNT | " + words.size() + ";" + newWords;
      } else {
        message += newWords;
      }

      buffer_size = message.getBytes().length;
      id++;
    }

    if (buffer_size > 0) {
      byte[] buffer = message.getBytes();

      System.out.println("\n");

      System.out.println("---------FINAL----------");
      System.out.println("Sending: " + message);
      System.out.println("buffer size: " + buffer.length);
      System.out.println("buffer size: " + buffer_size);
      DatagramPacket packet = new DatagramPacket(buffer, buffer_size, group, PORT);
      socket.send(packet);
    }

  }

}
