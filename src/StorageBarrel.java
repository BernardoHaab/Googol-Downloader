import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StorageBarrel implements IStorageBarrel {

  private final Integer BUFFER_SIZE = 1024;
  private final String HOST_NAME = "224.3.2.1";
  private final int PORT = 4321;
  private final int PORT_RETRIEVE = 4322;

  private InetAddress group;
  private MulticastSocket socket;

  private UUID downloaderId;
  private int messageId = 0;

  private ConcurrentHashMap<Integer, String> messageBuffer = new ConcurrentHashMap<Integer, String>();

  StorageBarrel() throws IOException {

    socket = new MulticastSocket(PORT_RETRIEVE);
    group = InetAddress.getByName(HOST_NAME);
    this.downloaderId = UUID.randomUUID();

    listenRetrieve();
  }

  public void listenRetrieve() {

    new Thread() {
      public void run() {
        try {
          System.out.println("Listening on port: " + PORT_RETRIEVE);
          NetworkInterface networkInterface = NetworkInterface.getByIndex(0);
          InetSocketAddress socketAddress = new InetSocketAddress(group, 0);

          socket.joinGroup(socketAddress, networkInterface);

          byte[] buffer = new byte[1024];

          while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String recivedMessage = new String(packet.getData());
            String[] parts = recivedMessage.split(";");
            String type = parts[0].split("\\|")[1].trim();

            if (type.equals("RETRIEVE")) {
              String[] identifier = parts[1].split("\\|");
              Integer requestedMessageId = Integer.parseInt(identifier[1].trim());

              if (UUID.fromString(identifier[0].trim()).equals(downloaderId)) {
                String retrieveMessage = messageBuffer.get(requestedMessageId);

                byte[] retrieveBuffer = retrieveMessage.getBytes();
                DatagramPacket retrievePacket = new DatagramPacket(retrieveBuffer, retrieveBuffer.length, group, PORT);
                socket.send(retrievePacket);
              }
            }
          }
        } catch (Exception e) {
          System.out.println("Error on listenRetrieve: " + e.getMessage());
        }
      }
    }.start();

  }

  @Override
  public void updateStorageBarrels(Set<String> words, String url) throws IOException {
    String message = createMulticastMessage("WORD_LIST") + ";URL | " + url + ";" + " word_COUNT | " + words.size();

    int buffer_size = 0;
    int id = 0;

    for (String word : words) {
      String newWords = id + " | " + word;

      if (buffer_size + newWords.getBytes().length > BUFFER_SIZE) {
        sendMulticast(message);
        message = createMulticastMessage("WORD_LIST") + ";URL | " + url + ";" + " word_COUNT | " + words.size() + ";"
            + newWords;
      } else {
        message += ";" + newWords;
      }

      buffer_size = message.getBytes().length;
      id++;
    }

    if (buffer_size > 0) {
      sendMulticast(message);
    }

  }

  @Override
  public void addReferencedUrls(Elements links, String url) throws IOException {
    String message = createMulticastMessage("REFERENCED_URLS") + "; URL | " + url + ";" + " urls_COUNT | "
        + links.size();

    int buffer_size = 0;

    int id = 0;

    for (Element link : links) {
      String newLinks = id + " | " + link.attr("abs:href");

      if (buffer_size + newLinks.getBytes().length > BUFFER_SIZE) {
        sendMulticast(message);
        message = createMulticastMessage("REFERENCED_URLS") + "; URL | " + url + ";" + "urls_COUNT | " + links.size()
            + ";" + newLinks;
      } else {
        message += ";" + newLinks;
      }

      buffer_size = message.getBytes().length;
      id++;
    }

    if (buffer_size > 0) {
      sendMulticast(message);
    }
  }

  private String createMulticastMessage(String type) {
    return downloaderId.toString() + " | " + messageId + ";TYPE | " + type;
  }

  private void sendMulticast(String message) throws IOException {
    messageBuffer.put(messageId, message);
    byte[] buffer = message.getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
    socket.send(packet);
    messageId++;

    if (messageBuffer.size() > 10) {
      messageBuffer.keySet().stream().sorted().limit(5).forEach(key -> {
        messageBuffer.remove(key);
      });
    }
  }

}
