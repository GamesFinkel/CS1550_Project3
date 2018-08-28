class PTE
{
  private boolean valid;
  private boolean referenced;
  private boolean dirty;
  private int index;
  private int frame;

  public PTE()
  {
    valid = false;
    referenced = false;
    dirty = false;
    index = -1;
    frame = -1;
  }

  public boolean isValid()
  {
    return valid;
  }
  public boolean isReferenced()
  {
    return referenced;
  }
  public boolean isDirty()
  {
    return dirty;
  }
  public int getIndex()
  {
    return index;
  }
  public int getFrame()
  {
    return frame;
  }
  public void setValid(boolean v)
  {
    valid = v;
  }
  public void setDirty(boolean d)
  {
    dirty = d;
  }
  public void setReferenced(boolean r)
  {
    referenced = r;
  }
  public void setIndex(int i)
  {
    index = i;
  }
  public void setFrame(int f)
  {
    frame = f;
  }
}
