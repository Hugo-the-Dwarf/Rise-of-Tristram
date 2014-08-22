package ee.rot;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ee.rot.blocks.RotBlocks;
import ee.rot.blocks.TileEntityMagicBase;
import ee.rot.comms.BaseBuilderPacket;
import ee.rot.comms.CommonProxy;
import ee.rot.comms.TextPacket;
import ee.rot.events.RotEventHandler;
import ee.rot.events.RotStandardEventHandler;
import ee.rot.gui.GuiHandler;
import ee.rot.gui.RotManaGui;
import ee.rot.gui.RotStamGui;
import ee.rot.items.RotItems;

@Mod(modid = Rot.MODID, version = Rot.VERSION, name= Rot.MODNAME)

public class Rot {
	
	@Instance(value = "RoT")
	public static Rot instance;
	
	// Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="ee.rot.comms.ClientProxy", serverSide="ee.rot.comms.CommonProxy")
    public static CommonProxy proxy;
    
    //@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {"rot"}, packetHandler = OpenGuiPacket.class)
	
	public static final String MODID = "RoT";
	public static final String MODNAME = "Rise of Tristram";
    public static final String VERSION = "1.0";   
    
    //Packets
    public static SimpleNetworkWrapper net;
    private int packetId = 0;   
    
    // Sending packets:
    /*
    MyMod.network.sendToServer(new MyMessage("foobar"));
    MyMod.network.sendTo(new SomeMessage(), somePlayer);
    */
    
    public static CreativeTabs tabRoT = new CreativeTabsRoT("RoT");
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {  	
    	net = NetworkRegistry.INSTANCE.newSimpleChannel("rpcee");
    	net.registerMessage(TextPacket.TextPacketHandler.class, TextPacket.class, packetId++, Side.SERVER);
    	net.registerMessage(BaseBuilderPacket.BaseBuilderPacketHandler.class, BaseBuilderPacket.class, packetId++, Side.SERVER);
    	
		GameRegistry.registerTileEntity(TileEntityMagicBase.class, "itemGenRot"); //This needs to be renamed, but this and it's related objects are under heavy construction
    	
    	RotBlocks.init();
    	RotBlocks.registerBlocks();
    	
    	RotItems.init();
    	RotItems.registerItems();
    	
    	RotRecipes.init();    
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new RotStandardEventHandler());
    	MinecraftForge.EVENT_BUS.register(new RotEventHandler());
    	if (FMLCommonHandler.instance().getEffectiveSide().isClient())MinecraftForge.EVENT_BUS.register(new RotManaGui(Minecraft.getMinecraft()));
    	if (FMLCommonHandler.instance().getEffectiveSide().isClient())MinecraftForge.EVENT_BUS.register(new RotStamGui(Minecraft.getMinecraft()));
    }
}
