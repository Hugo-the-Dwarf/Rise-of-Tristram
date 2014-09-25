package ca.grm.rot.comms;

import ca.grm.rot.blocks.TileEntityBaseNode;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BaseNodeRequestPacket implements IMessage {
	public static class BaseNodeRequestPacketHandler implements IMessageHandler<BaseNodeRequestPacket, IMessage> {
		
		@Override
		public IMessage onMessage(BaseNodeRequestPacket message, MessageContext ctx) {
			TileEntityBaseNode te;
			// 0 = add, 1 = clear, 2 = start
			switch (message.actionType) {
				case 0 :
					te = (TileEntityBaseNode) ctx.getServerHandler().playerEntity
							.getEntityWorld().getTileEntity(message.xTe, message.yTe,
									message.zTe);
					te.addLocation(message.xB, message.yB, message.zB,
							Block.getBlockById(message.blockId));
					break;
				case 1 :
					te = (TileEntityBaseNode) ctx.getServerHandler().playerEntity
							.getEntityWorld().getTileEntity(message.xTe, message.yTe,
									message.zTe);
					te.clearLocations();
					break;
				case 2 :
					te = (TileEntityBaseNode) ctx.getServerHandler().playerEntity
							.getEntityWorld().getTileEntity(message.xTe, message.yTe,
									message.zTe);
					te.startBuilding();
					break;
				case 3 :
					te = (TileEntityBaseNode) ctx.getServerHandler().playerEntity
							.getEntityWorld().getTileEntity(message.xTe, message.yTe,
									message.zTe);
					te.updateClient(ctx.getServerHandler().playerEntity);
					break;
			}
			return null;
		}
		
	}
	
	public int	actionType;	// 0 is add, 1 is clear, 2 is build
	public int	xTe, yTe, zTe;
	public int	xB, yB, zB;

	public int	blockId;

	public BaseNodeRequestPacket() {

	}
	
	public BaseNodeRequestPacket(int action, int xTe, int yTe, int zTe, int xB, int yB,
			int zB, int blockId) {
		this.actionType = action;
		this.xTe = xTe;
		this.yTe = yTe;
		this.zTe = zTe;
		this.xB = xB;
		this.yB = yB;
		this.zB = zB;
		this.blockId = blockId;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.actionType = buf.readInt();
		this.xTe = buf.readInt();
		this.yTe = buf.readInt();
		this.zTe = buf.readInt();
		this.xB = buf.readInt();
		this.yB = buf.readInt();
		this.zB = buf.readInt();
		this.blockId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.actionType);
		buf.writeInt(this.xTe);
		buf.writeInt(this.yTe);
		buf.writeInt(this.zTe);
		buf.writeInt(this.xB);
		buf.writeInt(this.yB);
		buf.writeInt(this.zB);
		buf.writeInt(this.blockId);
	}
	
}
