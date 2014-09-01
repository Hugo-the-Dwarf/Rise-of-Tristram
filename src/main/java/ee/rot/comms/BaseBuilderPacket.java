package ee.rot.comms;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ee.rot.blocks.RotBlocks;
import ee.rot.blocks.TileEntityMagicBase;

public class BaseBuilderPacket implements IMessage 
{
	
	public String msg;	
	
	public BaseBuilderPacket()
	{
		
	}
	
	public BaseBuilderPacket(String msg)
	{
		this.msg = msg;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		msg = ByteBufUtils.readUTF8String(buf); // this class is very useful in general for writing more complex objects
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		 ByteBufUtils.writeUTF8String(buf, msg);
	}
	
	public static class BaseBuilderPacketHandler implements IMessageHandler<BaseBuilderPacket, IMessage> 
	{

		@Override
		public IMessage onMessage(BaseBuilderPacket message, MessageContext ctx) 
		{
			String[] messagePieces = message.msg.split(";");
			String[] teCoords = messagePieces[1].split(","); 
			int x,y,z;
			TileEntityMagicBase te;
			// 0 = add, 1 = clear, 2 = start, 3 = set grid sizes
			switch (Integer.parseInt(messagePieces[0]))
			{
			
				case 0:
					x = Integer.parseInt(teCoords[0]);
					y = Integer.parseInt(teCoords[1]);
					z = Integer.parseInt(teCoords[2]);
					te = (TileEntityMagicBase)ctx.getServerHandler().playerEntity.getEntityWorld().getTileEntity(x, y, z);
					for (int mp = 2; mp < messagePieces.length; mp++)
					{
						String[] lCoords = messagePieces[mp].split(",");
						Block block = null;
						x = Integer.parseInt(lCoords[0]);
						y = Integer.parseInt(lCoords[1]);
						z = Integer.parseInt(lCoords[2]);
						for (int bt = 0; bt < RotBlocks.blockTypes.length;bt++)
						{
							if (lCoords[3].equals(RotBlocks.blockTypes[bt]))
							{
								block = RotBlocks.blockTypeObjects[bt];
								break;
							}
						}						
						if (block == null)block = Blocks.dirt;
						te.addLocation(x, y, z, block);
					}
					break;
				case 1:
					teCoords = messagePieces[1].split(",");
					x = Integer.parseInt(teCoords[0]);
					y = Integer.parseInt(teCoords[1]);
					z = Integer.parseInt(teCoords[2]);
					te = (TileEntityMagicBase)ctx.getServerHandler().playerEntity.getEntityWorld().getTileEntity(x, y, z);
					te.clearLocations();
					break;
				case 2:
					teCoords = messagePieces[1].split(",");
					x = Integer.parseInt(teCoords[0]);
					y = Integer.parseInt(teCoords[1]);
					z = Integer.parseInt(teCoords[2]);
					te = (TileEntityMagicBase)ctx.getServerHandler().playerEntity.getEntityWorld().getTileEntity(x, y, z);
					te.startBuilding();
					break;
			}
			return null;
		}

	}

}
