package googol;

import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

public class LocalQueue implements IUrlQueue {

  private Deque<String> urls = new LinkedBlockingDeque<String>();
  private Set<String> knownUrls = new HashSet<>();

  public LocalQueue() {
    urls.add("http://www.yahoo.com");
    // urls.add("http://www.uc.pt");
    // urls.add("http://www.bing.com");
  }

  public int size() {
    return urls.size();
  }

  public String getNextUrl() {
    return urls.poll();
  }

  public void addUrls(List<String> newUrls) {
    for (String url : newUrls) {
      if (knownUrls.add(url)) {
        urls.add(url);
      }
    }
  }

  public void addUrlFirst(String url) {
    urls.addFirst(url);
  }

}
