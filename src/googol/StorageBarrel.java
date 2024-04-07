package googol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StorageBarrel implements IStorageBarrel {

  private final Integer BUFFER_SIZE = 1024;

  private int portSend;
  private InetAddress group;
  private MulticastSocket socket;

  private UUID downloaderId;
  private int messageId = 0;

  private ConcurrentHashMap<Integer, String> messageBuffer = new ConcurrentHashMap<Integer, String>();

  StorageBarrel(String hostName, int portSend, int portRetrieve) throws IOException {

    this.portSend = portSend;
    socket = new MulticastSocket(portRetrieve);
    group = InetAddress.getByName(hostName);
    this.downloaderId = UUID.randomUUID();

    listenRetrieve();
  }

  public void listenRetrieve() {

    new Thread() {
      public void run() {
        try {
          // System.out.println("Listening on port: " + PORT_RETRIEVE);
          NetworkInterface networkInterface = NetworkInterface.getByIndex(0);
          InetSocketAddress socketAddress = new InetSocketAddress(group, 0);

          socket.joinGroup(socketAddress, networkInterface);

          byte[] buffer = new byte[BUFFER_SIZE];

          while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String recivedMessage = new String(packet.getData());
            String[] parts = recivedMessage.split(";");
            String type = parts[0].split("\\|")[1].trim();

            if (type.equals("RETRIEVE")) {
              String[] identifier = parts[1].split("\\|");
              Integer requestedMessageId = Integer.parseInt(identifier[1].trim());

              System.out.println("Retrieve message id: " + requestedMessageId);
              System.out.println("Message buffer: " + messageBuffer.keySet());

              if (UUID.fromString(identifier[0].trim()).equals(downloaderId)) {
                String retrieveMessage = messageBuffer.get(requestedMessageId);

                byte[] retrieveBuffer = retrieveMessage.getBytes();
                DatagramPacket retrievePacket = new DatagramPacket(retrieveBuffer, retrieveBuffer.length, group,
                    portSend);
                socket.send(retrievePacket);
              }
            }
          }
        } catch (NullPointerException e) {
          // TODO: Send NEED-SYNC message
          System.out.println("MENSAGEM FORA DO BUFFER");
        } catch (Exception e) {
          System.out.println("Error on listenRetrieve: " + e.getMessage());
        }
      }
    }.start();

  }

  @Override
  public void updateStorageBarrels(Set<String> words, String url) throws IOException {
    String message = createMulticastMessage("WORD_LIST") + ";URL|" + url + ";" + " word_COUNT|" + words.size();

    int buffer_size = 0;
    int id = 0;

    for (String word : words) {
      String newWords = id + "|" + word;

      if (buffer_size + newWords.getBytes().length > BUFFER_SIZE) {
        sendMulticast(message);

        // messageBuffer.put(messageId, message);
        // if (messageBuffer.size() > 25) {
        // messageBuffer.keySet().stream().sorted().limit(5).forEach(key -> {
        // messageBuffer.remove(key);
        // });
        // }
        // messageId++;
        message = createMulticastMessage("WORD_LIST") + ";URL|" + url + ";" + " word_COUNT|" + words.size() + ";"
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
    String message = createMulticastMessage("REFERENCED_URLS") + "; URL|" + url + ";" + " urls_COUNT|"
        + links.size();

    int buffer_size = 0;

    int id = 0;

    for (Element link : links) {
      String newLinks = id + "|" + link.attr("abs:href");

      if (buffer_size + newLinks.getBytes().length > BUFFER_SIZE) {
        sendMulticast(message);
        message = createMulticastMessage("REFERENCED_URLS") + "; URL|" + url + ";" + "urls_COUNT|" + links.size()
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
    return downloaderId.toString() + "|" + messageId + ";TYPE|" + type;
  }

  private void sendMulticast(String message) throws IOException {
    messageBuffer.put(messageId, message);
    byte[] buffer = message.getBytes();
    // System.out.println("\tTotal bytes: " + buffer.length);
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, portSend);
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    System.out.println(
        "\t ->Sending message: " + messageId + " at " + timestamp.getTime());
    socket.send(packet);
    messageId++;

    if (messageBuffer.keySet().size() > 25) {
      messageBuffer.keySet().stream().sorted().limit(5).forEach(key -> {
        messageBuffer.remove(key);
      });
    }
    // Scanner in = new Scanner(System.in);
    // in.nextLine();
  }

}
