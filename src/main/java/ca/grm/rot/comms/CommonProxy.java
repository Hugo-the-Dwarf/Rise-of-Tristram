package ca.grm.rot.comms;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy
{
	// /** Used to store IExtendedEntityProperties data temporarily between
	// player death and respawn */
	// private static final Map<String, NBTTagCompound> extendedEntityData = new
	// HashMap<String, NBTTagCompound>();

	public int addArmor(String armor)
	{
		return 0;
	}

	public void handleClassMessage(ClassResponsePacket message, MessageContext ctx)
	{
	}

	public void handleProfessionMessage(ProfessionResponsePacket message, MessageContext ctx)
	{
	}

	public void handleGoldMessage(GoldResponsePacket message, MessageContext ctx)
	{
	}

	public void handleMobData(MobResponsePacket message, MessageContext ctx)
	{
	}

	public void handleVillagerData(VillagerResponsePacket message,
			MessageContext ctx) {}
	
	public void registerKeyBindings()
	{
	}

	public void registerRenderers()
	{

	}

	public void updatePlayer(EntityPlayer player)
	{

	}



}
