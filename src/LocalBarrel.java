import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

public class LocalBarrel implements IStorageBarrel {

  private Deque<String> urlQueue = new LinkedBlockingDeque<String>();
  private Set<String> knownUrls = new HashSet<>();

  public LocalBarrel() {
    urlQueue.add("https://www.uc.pt/");
  }

  public int size() {
    return urlQueue.size();
  }

  public String getNextUrl() {
    return urlQueue.poll();
  }

  public void addUrls(List<String> urls) {
    for (String url : urls) {
      if (knownUrls.add(url)) {
        urlQueue.add(url);
      }
    }
  }

}
