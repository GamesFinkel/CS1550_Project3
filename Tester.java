import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
class Tester
{
  public static void main(String[] args) throws IOException
  {
    int[] frameSize = {8,16,32,64};
    String[] files = {"bzip.trace","swim.trace","gcc.trace"};
    String[] results = new String[12];
    int z = 0;
    for(int y = 0;y<files.length;y++)
    {
      for(int x = 0;x<frameSize.length;x++)
      {
        Algorithms all = new Algorithms(frameSize[x]);
        int faults = all.opt(files[y]);
        results[z] = "OPT - "+files[y]+" - "+frameSize[x]+" frames - "+faults+" faults";
        z++;
      }
    }
    BufferedWriter writer = new BufferedWriter(new FileWriter("faults_opt.txt",true));
    for(int start = 0;start < 12;start++)
    {
      writer.write(results[start]);
      writer.newLine();
    }
    writer.close();
  }
}
