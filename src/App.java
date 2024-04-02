public class App {

  public static void main(String[] args) {
    try {
      IUrlQueue queue = new LocalQueue();
      // IUrlQueue queue = (IUrlQueue)
      // LocateRegistry.getRegistry(6666).lookup("urlQueue");
      IStorageBarrel barrel = new StorageBarrel();
      Downloader downloader = new Downloader(queue, barrel);

      // String[] urls = { "http://www.yahoo.com" };

      // queue.addUrls(new LinkedList<String>(java.util.Arrays.asList(urls)));

      downloader.start();
    } catch (Exception e) {
      System.out.println("Error on main: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
