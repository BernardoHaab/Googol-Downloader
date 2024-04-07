package googol;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class App {

  private static String queueHost;
  private static int queuePort;
  private static String queueRegistryName;

  private static String barrelHost;
  private static int barrelPortSend;
  private static int barrelPortRetrieve;

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.out.println("Usage: java App <properties file>");
        return;
      }

      String fileName = args[0];

      readFileProperties(fileName);

      IUrlQueue queue = getUrlQueueInstance();
      IStorageBarrel barrel = new StorageBarrel(barrelHost, barrelPortSend, barrelPortRetrieve);
      Downloader downloader = new Downloader(queue, barrel);

      downloader.start();
    } catch (Exception e) {
      System.out.println("Error on main: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static IUrlQueue getUrlQueueInstance() {
    try {
      return (IUrlQueue) LocateRegistry.getRegistry(queueHost, queuePort).lookup(queueRegistryName);
    } catch (Exception e) {
      System.out.println("Erro ao conectar ao servidor!");
      System.out.println("Tentando novamente...");
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      return getUrlQueueInstance();
    }
  }

  private static void readFileProperties(String fileName) {
    File propFile = new File(fileName);

    try {
      Scanner myReader = new Scanner(propFile);
      while (myReader.hasNextLine()) {
        String line = myReader.nextLine();
        System.out.println(line);

        String[] parts = line.split(";");

        switch (parts[0]) {
          case "urlQueue":
            queueHost = parts[1];
            queuePort = Integer.parseInt(parts[2]);
            queueRegistryName = parts[3];
            break;
          case "multicast":
            barrelHost = parts[1];
            barrelPortSend = Integer.parseInt(parts[2]);
            barrelPortRetrieve = Integer.parseInt(parts[3]);
            break;

          default:
            break;
        }

      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Arquivo de propriedades não encontrado.");
      e.printStackTrace();
    } catch (IndexOutOfBoundsException e) {
      System.out.println("Arquivo de propriedades mal formatado.");
      e.printStackTrace();
    }
  }

}
