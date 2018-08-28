import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
class RefreshTester
{
  public static void main(String[] args) throws IOException
  {
    for(int frames = 8 ; frames < 128;frames*=2)
    {
      String[] results = new String[100];
      int z = 0;
      for(int refresh = 3;refresh<103;refresh++)
      {
        Algorithms all = new Algorithms(frames);
        int faults = all.nru(refresh,"bzip.trace");
        results[z] = faults+"";
        z++;
      }
      BufferedWriter writer = new BufferedWriter(new FileWriter("refresh_disk_bzip_"+frames+".txt",true));
      for(int start = 0;start < results.length;start++)
      {
        writer.write(results[start]);
        writer.newLine();
      }
      writer.close();
    }
  }
}
