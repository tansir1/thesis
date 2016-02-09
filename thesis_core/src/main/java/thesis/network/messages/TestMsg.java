package thesis.network.messages;

import java.nio.ByteBuffer;

public class TestMsg extends InfrastructureMsg
{
   private int data[];

   public TestMsg()
   {
      super(InfrastructureMsgType.Test);
   }

   public void setData(int data[])
   {
      this.data = new int[data.length];
      System.arraycopy(data, 0, this.data, 0, data.length);
   }

   public int[] getData()
   {
      return data;
   }

   @Override
   public void encodeData(ByteBuffer buf)
   {
      buf.putInt(data.length);
      for(int i=0; i<data.length; ++i)
      {
         buf.putInt(data[i]);
      }
   }

   @Override
   public void decodeData(ByteBuffer buf)
   {
      int numDataBytes = buf.getInt();
      data = new int[numDataBytes];
      for(int i=0; i<numDataBytes; ++i)
      {
         data[i] = buf.getInt();
      }
   }

   @Override
   public long getEncodedSize()
   {
      return (data.length + 1) * Integer.BYTES;
   }

}
