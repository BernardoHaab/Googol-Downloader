import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    socket = new MulticastSocket(PORT);
    group = InetAddress.getByName(HOST_NAME);

    // run();
    // listenRetrieve();
  }

  // public void listenRetrieve() {

  // new Thread() {
  // public void run() {
  // try {
  // NetworkInterface networkInterface = NetworkInterface.getByIndex(0);
  // // InetAddress mcastaddr = InetAddress.getByName(HOST_NAME);

  // InetSocketAddress socketAddress = new InetSocketAddress(group, 0);

  // socket.joinGroup(socketAddress, networkInterface);

  // byte[] buffer = new byte[1024];

  // System.out.println("Port: " + socket.getLocalPort());

  // while (true) {
  // DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
  // socket.receive(packet);

  // String message = new String(packet.getData(), 0, packet.getLength());
  // String type = message.split(";")[0].split(" | ")[1];

  // if (type.equals("RETRIEVE")) {

  // }

  // }

  // } catch (Exception e) {
  // System.out.println("Error on listenRetrieve: " + e.getMessage());
  // }
  // }
  // }.start();

  // }

  @Override
  public void updateStorageBarrels(Set<String> words, String url) throws IOException {
    String message = "TYPE | WORD_LIST; URL | " + url + ";" + " word_COUNT | " + words.size() + ";";

    int buffer_size = 0;

    int id = 0;

    for (String word : words) {
      String newWords = id + " | " + word + ";";

      if (buffer_size + newWords.getBytes().length > BUFFER_SIZE) {
        byte[] buffer = message.getBytes();
        System.out.println("---------LIMITE----------");
        // System.out.println("Sending: " + message);
        // System.out.println("buffer size: " + buffer.length);
        // System.out.println("buffer size: " + buffer_size);
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
      // System.out.println("Sending: " + message);
      // System.out.println("buffer size: " + buffer.length);
      // System.out.println("buffer size: " + buffer_size);
      DatagramPacket packet = new DatagramPacket(buffer, buffer_size, group, PORT);
      socket.send(packet);
    }

  }

  @Override
  public void addReferencedUrls(Elements links, String url) throws IOException {
    String message = "TYPE | REFERENCED_URLS; URL | " + url + ";" + " urls_COUNT | " + links.size() + ";";

    int buffer_size = 0;

    int id = 0;

    for (Element link : links) {
      String newLinks = id + " | " + link.attr("abs:href") + ";";

      if (buffer_size + newLinks.getBytes().length > BUFFER_SIZE) {
        byte[] buffer = message.getBytes();
        System.out.println("---------LIMITE----------");
        System.out.println("Sending: " + message);
        // System.out.println("buffer size: " + buffer.length);
        // System.out.println("buffer size: " + buffer_size);
        DatagramPacket packet = new DatagramPacket(buffer, buffer_size, group, PORT);
        socket.send(packet);
        message = "TYPE | REFERENCED_URLS; URL | " + url + ";" + "urls_COUNT | " + links.size() + ";" + newLinks;
      } else {
        message += newLinks;
      }

      buffer_size = message.getBytes().length;
      id++;
    }

    if (buffer_size > 0) {
      byte[] buffer = message.getBytes();

      System.out.println("\n");

      System.out.println("---------FINAL----------");
      // System.out.println("Sending: " + message);
      // System.out.println("buffer size: " + buffer.length);
      // System.out.println("buffer size: " + buffer_size);
      DatagramPacket packet = new DatagramPacket(buffer, buffer_size, group, PORT);
      socket.send(packet);
    }
  }

}
