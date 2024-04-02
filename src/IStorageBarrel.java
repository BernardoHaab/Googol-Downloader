import java.io.IOException;
import java.util.Set;

public interface IStorageBarrel {

  public void updateStorageBarrels(Set<String> words, String url) throws IOException;

}
