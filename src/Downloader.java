public class Downloader {

  public static void main(String[] args) throws Exception {
    System.out.println("Hello, World!");

    // IStorageBarrel barrel = new LocalBarrel();
    IStorageBarrel barrel = new StorageBarrel();
    String url = barrel.getNextUrl();
    System.out.println("First url: " + url);

    // while (url != null) {
    // System.out.println("Downloading " + url);
    // List<String> newUrls = new LinkedList<String>();

    // Document doc = Jsoup.connect(url).get();
    // StringTokenizer tokens = new StringTokenizer(doc.text());
    // int countTokens = 0;
    // // while (tokens.hasMoreElements() && countTokens++ < 100)
    // // System.out.println(tokens.nextToken().toLowerCase());

    // Elements links = doc.select("a[href]");
    // System.out.println("Total pages to search " + barrel.size());
    // System.out.println("Found " + links.size() + " links");
    // for (Element link : links)
    // newUrls.add(link.attr("abs:href"));
    // // System.out.println(link.text() + "\n" + link.attr("abs:href") + "\n");

    // barrel.addUrls(newUrls);

    // Scanner in = new Scanner(System.in);

    // in.nextLine();

    // url = barrel.getNextUrl();
    // }

  }

}
