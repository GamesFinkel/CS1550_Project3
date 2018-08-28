class vmsim
{
  public static void main(String[] args)
  {
    if(args.length<5 || args.length>7)
    {
      System.out.println("Incorrect number of arguments.\nGiven - "+args.length+"\nQuitting.");
      System.exit(0);
    }
    if(!args[0].equals("-n"))
    {
      System.out.println("Incorrect first argument.\nGiven - "+args[0]+"\nNeeded - '-n'\nQuitting.");
      System.exit(0);
    }
    int frames = 0;
    try
    {
      frames = Integer.parseInt(args[1]);
    }
    catch(Exception e)
    {
      System.out.println("Incorrect second argument.\nGiven - "+args[1]+"\nNeeded - Number of frames\nQuitting.");
      System.exit(0);
    }
    if(!args[2].equals("-a"))
    {
      System.out.println("Incorrect third argument.\nGiven - "+args[2]+"\nNeeded - '-a'\nQuitting.");
      System.exit(0);
    }
    String alg = args[3];
    if(!alg.equals("opt")&&!alg.equals("clock")&&!alg.equals("fifo")&&!alg.equals("nru"))
    {
      System.out.println("Incorrect fourth argument.\nGiven - "+args[3]+"\nNeeded - <opt|clock|fifo|nru>\nQuitting.");
      System.exit(0);
    }
    Algorithms algSim = new Algorithms(frames);
    int refresh = 0;
    if(alg.equals("nru"))
    {
      if(args.length != 7)
      {
        System.out.println("Incorrect number of arguments.\nGiven - "+args.length+"\nNeeded - 7\nQuitting.");
        System.exit(0);
      }
      if(!args[4].equals("-r"))
      {
        System.out.println("Incorrect fifth argument.\nGiven - "+args[4]+"\nNeeded - '-r'\nQuitting.");
        System.exit(0);
      }
      try
      {
        refresh = Integer.parseInt(args[5]);
      }
      catch(Exception e)
      {
        System.out.println("Incorrect sixth argument.\nGiven - "+args[5]+"\nNeeded - Refresh rate\nQuitting.");
        System.exit(0);
      }
    }
    else if(args.length != 5)
    {
      System.out.println("Incorrect number of arguments.\nGiven - "+args.length+"\nNeeded - 5\nQuitting.");
      System.exit(0);
    }
    if(alg.equals("opt"))
      algSim.opt(args[4]);
    else if(alg.equals("clock"))
      algSim.clock(args[4]);
    else if(alg.equals("fifo"))
      algSim.fifo(args[4]);
    else if(alg.equals("nru"))
      algSim.nru(refresh, args[6]);
    else
    {
        System.out.println("Illegal algorithm.\nQuitting.");
        System.exit(0);
    }
  }
}
