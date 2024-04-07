package googol;

import java.io.IOException;
import java.util.Set;

import org.jsoup.select.Elements;

public interface IStorageBarrel {

  public void updateStorageBarrels(Set<String> words, String url) throws IOException;

  public void addReferencedUrls(Elements urls, String url) throws IOException;

}
