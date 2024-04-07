package googol;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {

  private IUrlQueue urlQueue;
  private IStorageBarrel storageBarrel;

  public Downloader(IUrlQueue urlQueue, IStorageBarrel storageBarrel) {
    this.urlQueue = urlQueue;
    this.storageBarrel = storageBarrel;
  }

  public void start() throws RemoteException {
    String url = getUrl();

    System.out.println("First url: " + url);

    while (true) {
      System.out.println("Downloading " + url);
      try {
        Document doc = Jsoup.connect(url).get();
        updateUrlQueue(doc);
        updateStorageBarrels(doc, url);
      } catch (MalformedURLException e) {
        System.out.println("Url invalida: " + url);
      } catch (Exception e) {
        System.out.println("Erro ao acessar: " + url);
        urlQueue.addUrlFirst(url);
      }

      url = getUrl();

      System.out.println("Next url: " + url);

      Scanner in = new Scanner(System.in);
      // in.nextLine();
    }
  }

  private String getUrl() throws RemoteException {
    String nextUrl;

    nextUrl = urlQueue.getNextUrl();

    if (nextUrl == null) {
      System.out.println("0 urls encontradas...");
      System.out.println("Tentando novamente!");
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      return getUrl();
    }

    return nextUrl;
  }

  private void updateStorageBarrels(Document doc, String url) {

    StringTokenizer tokens = new StringTokenizer(doc.text());
    Elements links = doc.select("a[href]");

    Set<String> words = new HashSet<String>();

    while (tokens.hasMoreElements()) {
      String formattedToken = tokens.nextToken().toLowerCase().replaceAll("\\p{Punct}", "");
      // System.out.println(tokens.nextToken().toLowerCase());
      if (formattedToken.length() > 3) {
        words.add(formattedToken);
      }
    }

    try {
      System.out.println("Found " + words.size() + " words");
      storageBarrel.updateStorageBarrels(words, url);
      storageBarrel.addReferencedUrls(links, url);
    } catch (Exception e) {
      System.out.println("Error updating storage barrels");
      System.err.println(e);
    }
  }

  private void updateUrlQueue(Document doc) {
    Elements links = doc.select("a[href]");
    List<String> newUrls = new LinkedList<String>();

    try {
      System.out.println("Total pages to search " + urlQueue.size());
      System.out.println("Found " + links.size() + " links");
      for (Element link : links)
        newUrls.add(link.attr("abs:href"));
      urlQueue.addUrls(newUrls);
    } catch (Exception e) {
      System.out.println("Error updating url queue");
      System.err.println(e);
    }
  }

}
