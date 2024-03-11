import java.io.IOException;
import java.util.List;

public interface IStorageBarrel {

  public String getNextUrl() throws IOException;

  public int size();

  public void addUrls(List<String> urls);

}
