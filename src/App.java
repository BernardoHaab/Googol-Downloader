import java.rmi.registry.LocateRegistry;

public class App {

  public static void main(String[] args) {
    try {
      // IUrlQueue queue = new LocalQueue();
      IUrlQueue queue = (IUrlQueue) LocateRegistry.getRegistry(6666).lookup("urlQueue");
      IStorageBarrel barrel = new StorageBarrel();
      Downloader downloader = new Downloader(queue, barrel);

      downloader.start();
    } catch (Exception e) {
      System.out.println("Error on main: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
