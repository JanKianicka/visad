package visad;

import java.rmi.RemoteException;

public class MessageEvent
{
  public static final int ID_GENERIC = 1;

  private int id, originator;
  private String str;
  private RemoteData data;

  /**
   * Create a message event with the given ID
   *
   * @param id Message ID
   */
  public MessageEvent(int id)
  {
    this(id, -1, null, null);
  }

  /**
   * Create a message event with the given string
   *
   * @param str Message <tt>String</tt>
   */
  public MessageEvent(String str)
  {
    this(ID_GENERIC, -1, str, null);
  }

  /**
   * Create a message event with the given data
   *
   * @param data Message <tt>Data</tt>
   */
  public MessageEvent(RemoteData data)
  {
    this(ID_GENERIC, -1, null, data);
  }

  /**
   * Create a message event with the given ID and string
   *
   * @param id Message ID
   * @param str Message <tt>String</tt>
   */
  public MessageEvent(int id, String str)
  {
    this(id, -1, str, null);
  }

  /**
   * Create a message event with the given ID and data
   *
   * @param id Message ID
   * @param data Message <tt>Data</tt>
   */
  public MessageEvent(int id, RemoteData data)
  {
    this(id, -1, null, data);
  }

  /**
   * Create a message event with the given string and data
   *
   * @param str Message <tt>String</tt>
   * @param data Message <tt>Data</tt>
   */
  public MessageEvent(String str, RemoteData data)
  {
    this(ID_GENERIC, -1, str, data);
  }


  /**
   * Create a message event with the given ID, string and data
   *
   * @param id Message ID
   * @param str Message <tt>String</tt>
   * @param data Message <tt>Data</tt>
   */
  public MessageEvent(int id, String str, RemoteData data)
  {
    this(id, -1, str, data);
  }

  /**
   * Create a message event with the given IDs, string and data
   *
   * @param id Message ID
   * @param originator Originator ID.
   * @param str Message <tt>String</tt>
   * @param data Message <tt>Data</tt>
   */
  public MessageEvent(int id, int originator, String str, RemoteData data)
  {
    this.id = id;
    this.originator = originator;
    this.str = str;
    this.data = data;
  }

  /**
   * Get this message's ID.
   *
   * @return The message ID.
   */
  public int getId() { return id; }

  /**
   * Get the ID of the originator of this message.
   *
   * @return The originator's ID.
   */
  public int getOriginatorId() { return originator; }

  /**
   * Get the string associated with this message.
   *
   * @return The message string.
   */
  public String getString() { return str; }

  /**
   * Get the data associated with this message.
   *
   * @return The message <tt>Data</tt>.
   */
  public RemoteData getData() { return data; }

  public String toString()
  {
    StringBuffer buf = new StringBuffer(getClass().getName());
    buf.append('[');
    if (id != ID_GENERIC) {
      buf.append("id=");
      buf.append(id);
      buf.append(',');
    }
    if (originator != -1) {
      buf.append("originator=");
      buf.append(originator);
      buf.append(',');
    }
    if (str != null) {
      buf.append("str=\"");
      buf.append(str);
      buf.append("\",");
    }
    if (data != null) {
      buf.append("data=");
      try {
        buf.append(data.local());
      } catch(Exception e) {
        buf.append('?');
        buf.append(e.getMessage());
        buf.append('?');
      }
      buf.append(',');
    }
    buf.setLength(buf.length()-1);
    buf.append(']');
    return buf.toString();
  }
}