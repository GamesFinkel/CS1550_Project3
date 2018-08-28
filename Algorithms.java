import java.util.Scanner;
import java.io.File;
import java.util.LinkedList;
import java.util.ArrayList;
class Algorithms
{
  public int frames;
  public final int PAGE_SIZE = (int)Math.pow(2,12);
  public Algorithms(int f)
  {
    frames = f;
  }
  //
  // OPT Algorithm
  //
  // OPT works by keeping track of all requests in the future and eviciting the page we will not need for the longest time.
  // OPT has the lowest page fault rate possible.
  // However, OPT can not be realistically implemented as it requires knowledge of the future.
  public void opt(String file)
  {
    int accesses = 0;
    int faults = 0;
    int writes = 0;
    int activeFrames = 0;
    File f = new File(file);
    Scanner sc = new Scanner(System.in);
    try
    {
      sc = new Scanner(f);
    }
    catch(Exception e)
    {
      System.out.println("File error.\nQuitting");
      System.exit(0);
    }
    PTE[] RAM_PageTable = createRAM();
    PTE[] PageTable = createPageTable();
    ArrayList<LinkedList<Integer>> future = new ArrayList<LinkedList<Integer>>(PageTable.length);
    for(int createList = 0;createList<PageTable.length;createList++)
    {
      future.add(createList,new LinkedList<Integer>());
    }
    int time = 0;
    while(sc.hasNextLine())
    {
      String inp = sc.nextLine();
      String[] splitInput = inp.split(" ");
      long address = Long.decode("0x"+splitInput[0]);
      int page = (int)(address/PAGE_SIZE);
      if(page<0||page>=PageTable.length)
      {
        System.out.println("Error: Attempting to write to page outside bounds.\nQuitting.");
        System.exit(0);
      }
      future.get(page).add(time);
      time++;
    }
    try
    {
      sc = new Scanner(f);
    }
    catch(Exception e)
    {
      System.out.println("File error.\nQuitting");
      System.exit(0);
    }
    while(sc.hasNextLine())
    {
      accesses++;
      String inp = sc.nextLine();
      String[] splitInput = inp.split(" ");
      char row = splitInput[1].charAt(0);
      long address = Long.decode("0x"+splitInput[0]);
      int page = (int)(address/PAGE_SIZE);
      if(page<0||page>=PageTable.length)
      {
        System.out.println("Error: Attempting to write to page outside bounds.\nQuitting.");
        System.exit(0);
      }
      if(PageTable[page].isValid())
      {
        if(row == 'W')
          RAM_PageTable[PageTable[page].getFrame()].setDirty(true);
        System.out.println("hit");
      }
      else
      {
        faults++;
        if(activeFrames < frames)
        {
          PageTable[page].setValid(true);
          PageTable[page].setFrame(activeFrames);
          if(row == 'W')
            PageTable[page].setDirty(true);
          RAM_PageTable[activeFrames] = PageTable[page];
          System.out.println("page fault - no eviction");
          activeFrames++;
        }
        else
        {
          int toRemove = -1;
          for(int start = 0;start < frames;start++)
          {
            if(future.get(RAM_PageTable[start].getIndex()).peek() == null)
            {
              toRemove = start;
              break;
            }
            else
            {
              if(toRemove == -1)
              {
                toRemove = start;
              }
              else
              {
                if((int)future.get(RAM_PageTable[toRemove].getIndex()).peek() < (int)future.get(RAM_PageTable[start].getIndex()).peek())
                  toRemove = start;
              }
            }
          }
          if(RAM_PageTable[toRemove].isDirty())
          {
            writes++;
            System.out.println("page fault - evict dirty");
          }
          else
          {
            System.out.println("page fault - evict clean");
          }
          int temp_index = RAM_PageTable[toRemove].getIndex();
          PageTable[temp_index].setDirty(false);
          PageTable[temp_index].setValid(false);
          PageTable[temp_index].setFrame(-1);
          RAM_PageTable[toRemove] = PageTable[page];
          if(row == 'W')
            RAM_PageTable[toRemove].setDirty(true);
          RAM_PageTable[toRemove].setValid(true);
          RAM_PageTable[toRemove].setFrame(toRemove);
          RAM_PageTable[toRemove].setReferenced(true);
        }
      }
      future.get(page).remove();
    }
    output("OPT",accesses,faults,writes);
  }

  //
  // Clock Algorithm
  //
  // Clock works by giving all pages in RAM a second-chance at residency.
  // Whenever an eviction must occur, we check our pointer for the page at that frame.
  // If the page is unreferenced, we evict it.
  // If the page is referenced, we mark it as unreferenced, then increment our pointer until we find an unreferenced page.
  // If all pages were initially referenced, the first page we looked at, which will be the first unreferenced page, gets evicted.
  // Anytime a page is used, the referenced bit is reset to true
  public void clock(String file)
  {
    int accesses = 0;
    int faults = 0;
    int writes = 0;
    int ClockPoint = 0;
    File f = new File(file);
    Scanner sc = new Scanner(System.in);
    try
    {
      sc = new Scanner(f);
    }
    catch(Exception e)
    {
      System.out.println("File error.\nQuitting");
      System.exit(0);
    }
    PTE[] RAM_PageTable = createRAM();
    PTE[] PageTable = createPageTable();
    while(sc.hasNextLine())
    {
      accesses++;
      String inp = sc.nextLine();
      String[] splitInput = inp.split(" ");
      char row = splitInput[1].charAt(0);
      long address = Long.decode("0x"+splitInput[0]);
      int page = (int)(address/PAGE_SIZE);
      if(page<0||page>=PageTable.length)
      {
        System.out.println("Error: Attempting to write to page outside bounds.\nQuitting.");
        System.exit(0);
      }
      if(PageTable[page].isValid())
      {
        if(row == 'W')
          RAM_PageTable[PageTable[page].getFrame()].setDirty(true);
        RAM_PageTable[PageTable[page].getFrame()].setReferenced(true);
        System.out.println("hit");
      }
      else
      {
        faults++;
        if(RAM_PageTable[ClockPoint].getIndex() == -1)
        {
          PageTable[page].setValid(true);
          PageTable[page].setFrame(ClockPoint);
          if(row == 'W')
          PageTable[page].setDirty(true);
          PageTable[page].setReferenced(true);
          RAM_PageTable[ClockPoint] = PageTable[page];
          System.out.println("page fault - no eviction");
          ClockPoint = (ClockPoint + 1)%frames;
        }
        else
        {
          for(;;)
          {
            if(!RAM_PageTable[ClockPoint].isReferenced())
              break;
            else RAM_PageTable[ClockPoint].setReferenced(false);
            ClockPoint = (ClockPoint + 1)%frames;
          }
          if(RAM_PageTable[ClockPoint].isDirty())
            {
              writes++;
              System.out.println("page fault - evict dirty");
            }
          else
          {
            System.out.println("page fault - evict clean");
          }
          int temp_index = RAM_PageTable[ClockPoint].getIndex();
          PageTable[temp_index].setDirty(false);
          PageTable[temp_index].setValid(false);
          PageTable[temp_index].setFrame(-1);
          RAM_PageTable[ClockPoint] = PageTable[page];
          if(row == 'W')
            RAM_PageTable[ClockPoint].setDirty(true);
          RAM_PageTable[ClockPoint].setValid(true);
          RAM_PageTable[ClockPoint].setFrame(ClockPoint);
          RAM_PageTable[ClockPoint].setReferenced(true);
          ClockPoint = (ClockPoint + 1)%frames;
        }
      }
    }
    output("Clock",accesses,faults,writes);
  }

  //
  // FIFO Algorithm
  //
  // FIFO works by tracking the page that has been in RAM the longest and evicting it.
  // This can be done by simply keeping track of the next frame in need of eviction.
  // Anytime a page fault occurs and eviction is needed, we evict from the tracked location then increment.
  public void fifo(String file)
  {
    int accesses = 0;
    int faults = 0;
    int writes = 0;
    int toGo = 0;
    File f = new File(file);
    Scanner sc = new Scanner(System.in);
    try
    {
      sc = new Scanner(f);
    }
    catch(Exception e)
    {
      System.out.println("File error.\nQuitting");
      System.exit(0);
    }
    PTE[] RAM_PageTable = createRAM();
    PTE[] PageTable = createPageTable();
    while(sc.hasNextLine())
    {
      accesses++;
      String inp = sc.nextLine();
      String[] splitInput = inp.split(" ");
      char row = splitInput[1].charAt(0);
      long address = Long.decode("0x"+splitInput[0]);
      int page = (int)(address/PAGE_SIZE);
      if(page<0||page>=PageTable.length)
      {
        System.out.println("Error: Attempting to write to page outside bounds.\nQuitting.");
        System.exit(0);
      }
      if(PageTable[page].isValid())
      {
        if(row == 'W')
        RAM_PageTable[PageTable[page].getFrame()].setDirty(true);
        System.out.println("hit");
      }
      else
      {
        if(RAM_PageTable[toGo].getIndex() == -1)
        {
          PageTable[page].setValid(true);
          PageTable[page].setFrame(toGo);
          if(row == 'W')
          PageTable[page].setDirty(true);
          RAM_PageTable[toGo] = PageTable[page];
          System.out.println("page fault - no eviction");
        }
        else
        {
          if(RAM_PageTable[toGo].isDirty())
            {
              writes++;
              System.out.println("page fault - evict dirty");
            }
          else
          {
            System.out.println("page fault - evict clean");
          }
          int temp_index = RAM_PageTable[toGo].getIndex();
          PageTable[temp_index].setDirty(false);
          PageTable[temp_index].setValid(false);
          PageTable[temp_index].setFrame(-1);
          RAM_PageTable[toGo] = PageTable[page];
          if(row == 'W')
            RAM_PageTable[toGo].setDirty(true);
          RAM_PageTable[toGo].setValid(true);
          RAM_PageTable[toGo].setFrame(toGo);
        }
        faults++;
        toGo = (toGo + 1)%frames;
      }
    }
    output("FIFO",accesses,faults,writes);
  }

//
// NRU Algorithm
//
// NRU needs a refresh rate
// NRU works by finding the lowest priority page in RAM and evicting it.
// Priority goes:
// Highest    Dirty and Referenced
//            Clean and Referenced
//            Dirty and Unreferenced
// Lowest     Clean and Unreferenced
// After every refresh iterations, all referenced bits are set to false
  public void nru(int refresh, String file)
  {
    int accesses = 0;
    int faults = 0;
    int writes = 0;
    int activeFrames = 0;

    File f = new File(file);
    Scanner sc = new Scanner(System.in);
    try
    {
      sc = new Scanner(f);
    }
    catch(Exception e)
    {
      System.out.println("File error.\nQuitting");
      System.exit(0);
    }

    PTE[] RAM_PageTable = createRAM();
    PTE[] PageTable = createPageTable();

    while(sc.hasNextLine())
    {
      accesses++;
      if(accesses%refresh==0)
      {
        RAM_PageTable = clearReferenced(RAM_PageTable);
      }
      String inp = sc.nextLine();
      String[] splitInput = inp.split(" ");
      char row = splitInput[1].charAt(0);
      long address = Long.decode("0x"+splitInput[0]);
      int page = (int)(address/PAGE_SIZE);
      if(page<0||page>=PageTable.length)
      {
        System.out.println("Error: Attempting to write to page outside bounds.\nQuitting.");
        System.exit(0);
      }
      if(PageTable[page].isValid())
      {
        if(row == 'W')
        RAM_PageTable[PageTable[page].getFrame()].setDirty(true);
        RAM_PageTable[PageTable[page].getFrame()].setReferenced(true);
        System.out.println("hit");
      }
      else
      {
        faults++;
        if(activeFrames < frames)
        {
          PageTable[page].setValid(true);
          PageTable[page].setFrame(activeFrames);
          if(row == 'W')
            PageTable[page].setDirty(true);
          PageTable[page].setReferenced(true);
          RAM_PageTable[activeFrames] = PageTable[page];
          System.out.println("page fault - no eviction");
          activeFrames++;
        }
        else
        {
          int toRemove = -1;
          boolean rem_ref = true;
          boolean rem_dirty = true;

          for(int start_index = 0;start_index < frames;start_index++)
          {
            boolean cur_ref = RAM_PageTable[start_index].isReferenced();
            boolean cur_dirty = RAM_PageTable[start_index].isDirty();
            if(!cur_dirty&&!cur_ref)
            {
              toRemove = start_index;
              break;
            }
            else if(!cur_ref)
            {
              if(rem_ref)
              {
                toRemove = start_index;
                rem_ref = false;
                rem_dirty = true;
              }
            }
            else if(!cur_dirty)
            {
              if(rem_ref && rem_dirty)
              {
                toRemove = start_index;
                rem_dirty = false;
              }
            }
            else
            {
              if(toRemove==-1)
              {
                toRemove = start_index;
              }
            }
          }
          if(RAM_PageTable[toRemove].isDirty())
            {
              writes++;
              System.out.println("page fault - evict dirty");
            }
          else
          {
            System.out.println("page fault - evict clean");
          }
          int temp_index = RAM_PageTable[toRemove].getIndex();
          PageTable[temp_index].setDirty(false);
          PageTable[temp_index].setValid(false);
          PageTable[temp_index].setFrame(-1);
          RAM_PageTable[toRemove] = PageTable[page];
          if(row == 'W')
            RAM_PageTable[toRemove].setDirty(true);
          RAM_PageTable[toRemove].setValid(true);
          RAM_PageTable[toRemove].setFrame(toRemove);
          RAM_PageTable[toRemove].setReferenced(true);
        }
      }
    }
    output("NRU",accesses,faults,writes);
  }
  // Output our findings
  public void output(String alg, int memacc, int fault, int writes)
  {
    System.out.println("Algorithm: "+alg);
    System.out.println("Number of frames:\t"+frames);
    System.out.println("Total memory accesses:\t"+memacc);
    System.out.println("Total page faults:\t"+fault);
    System.out.println("Total writes to disk:\t"+writes);
  }
  // Creates a blank array full of Page Table Entries to use as our Page Table
  public PTE[] createPageTable()
  {
    PTE[] pageTable = new PTE[(int)Math.pow(2,20)];
    for(int x = 0 ; x < pageTable.length ; x++)
    {
      PTE temp = new PTE();
      temp.setIndex(x);
      pageTable[x] = temp;
    }
    return pageTable;
  }
  // Creates a blank array full of Page Table Entries to use as RAM
  public PTE[] createRAM()
  {
    PTE[] RAM = new PTE[frames];
    for(int x = 0 ; x < RAM.length ; x++)
    {
      PTE temp = new PTE();
      temp.setFrame(x);
      RAM[x] = temp;
    }
    return RAM;
  }
  // Clears all referenced bits in RAM for NRU
  public PTE[] clearReferenced(PTE[] RAM)
  {
    for(int temp = 0;temp<frames;temp++)
    {
      RAM[temp].setReferenced(false);
    }
    return RAM;
  }
}
