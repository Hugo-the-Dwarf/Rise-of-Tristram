package ee.rot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import ee.rot.comms.CommonProxy;

public class ExtendPlayerRot implements IExtendedEntityProperties 
{

	/*
	Here I create a constant EXT_PROP_NAME for this class of properties
	You need a unique name for every instance of IExtendedEntityProperties
	you make, and doing it at the top of each class as a constant makes
	it very easy to organize and avoid typos. It's easiest to keep the same
	constant name in every class, as it will be distinguished by the class
	name: ExtendedPlayer.EXT_PROP_NAME vs. ExtendedEntity.EXT_PROP_NAME
	
	Note that a single entity can have multiple extended properties, so each
	property should have a unique name. Try to come up with something more
	unique than the tutorial example.
	*/
	public final static String EXT_PROP_NAME = "EEPlayerRot";

	// I always include the entity to which the properties belong for easy access
	// It's final because we won't be changing which player it is
	private final EntityPlayer player;
	
	private enum playerClass
	{
		NOCLASS("Peasent",0,0,0,0,0,200f,200f),
		FIGHTER("Fighter",3,1,-3,1,-2,50f,350f),
		MAGE("Mage",-2,0,5,-2,-1,350f,50f),
		TANK("Tank",1,-2,-2,4,-1,25f,375f),
		BUILDER("Builder",-1,3,-3,2,-1,200f,200f),
		RANGER("Ranger",-3,2,-1,-1,3,25f,375f),
		THIEF("Thief",-2,4,-1,-2,1,125f,275f);
		
		private int str,agi,inte,vit,dex;
		private float maxMana, maxStam;
		private String className;
		
		private playerClass(String name, int str, int agi,int inte, int vit, int dex,float maxMana, float maxStam)
		{
			this.className = name;
			this.str = str;
			this.agi = agi;
			this.inte = inte;
			this.vit = vit;
			this.dex = dex;
			this.maxMana = maxMana;
			this.maxStam = maxStam;
		}
		
		public String getClassName() {return className;}
		public int getStr() {return str;}
		public int getAgi() {return agi;}
		public int getInte() {return inte;}
		public int getVit() {return vit;}
		public int getDex() {return dex;}
		public float getMaxMana() {return maxMana;}
		public float getMaxStam() {return maxStam;}
		
		public static playerClass getClass(String className)
		{
			for (playerClass pc : playerClass.values())
			{
				if (pc.getClassName().equals(className))
				{
					return pc;
				}
			}
			return NOCLASS;
		}
		
		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("Class: " + className);
			sb.append(" Strength Modifer: " + str);
			sb.append(" Agility Modifer: " + agi);
			sb.append(" Intelligence Modifer: " + inte);
			sb.append(" Vitality Modifer: " + vit);
			sb.append(" Dexterity Modifer: " + dex);
			sb.append(" Base Mana: " + maxMana);
			sb.append(" Base Stamina: " + maxStam);
			return sb.toString();
		}
	}
	
	private playerClass currentClass;

	// Declare other variables you want to add here

	private float currentMana, maxMana, 
					currentStam, maxStam;
	private int strength, agility, intelligence, dexterity, vitality;

	/*
	The default constructor takes no arguments, but I put in the Entity
	so I can initialize the above variable 'player'
	
	Also, it's best to initialize any other variables you may have added,
	just like in any constructor.
	*/
	
	public static final int MANA_WATCHER = 20;
	public static final int STAM_WATCHER = 21;
	
	public ExtendPlayerRot(EntityPlayer player)
	{
		this.player = player;
		
		this.currentClass = playerClass.NOCLASS;
		this.dexterity = 0;
		this.agility = 0;
		this.intelligence = 0;
		this.strength = 0;
		this.vitality = 0;
		
		// Start with max mana. Every player starts with the same amount.
		this.currentMana = this.maxMana = 200f;
		this.currentStam = this.maxStam = 200f;
		this.player.getDataWatcher().addObject(MANA_WATCHER, this.maxMana);
		this.player.getDataWatcher().addObject(STAM_WATCHER, this.maxStam);
	}
	
	private static final String getSaveKey(EntityPlayer player) 
	{
		// no longer a username field, so use the command sender name instead:
		return player.getCommandSenderName() + ":" + EXT_PROP_NAME;
	}
	
	/**
	* Does everything I did in onLivingDeathEvent and it's static,
	* so you now only need to use the following in the above event:
	* ExtendedPlayer.saveProxyData((EntityPlayer) event.entity));
	*/
	public static void saveProxyData(EntityPlayer player) 
	{
		ExtendPlayerRot playerData = ExtendPlayerRot.get(player);
		NBTTagCompound savedData = new NBTTagCompound();
	
		playerData.saveNBTData(savedData);
		// Note that we made the CommonProxy method storeEntityData static,
		// so now we don't need an instance of CommonProxy to use it! Great!
		//CommonProxy.storeEntityData(getSaveKey(player), savedData);
	}
	
	public static final void loadProxyData(EntityPlayer player) 
	{
		ExtendPlayerRot playerData = ExtendPlayerRot.get(player);
		//NBTTagCompound savedData = CommonProxy.getEntityData(getSaveKey(player));
		//if (savedData != null) { playerData.loadNBTData(savedData); }
		// we are replacing the entire sync() method with a single line; more on packets later
		// data can by synced just by sending the appropriate packet, as everything is handled internally by the packet class
		//Rot.packetPipeline.sendTo(new SyncPlayerPropsPacket(player), (EntityPlayerMP) player);
	}

		// remove the public void sync() method; it is no longer needed

	/**
	 * Used to register these extended properties for the player during EntityConstructing event
	 * This method is for convenience only; it will make your code look nicer
	 */
	public static final void register(EntityPlayer player)
	{
		player.registerExtendedProperties(ExtendPlayerRot.EXT_PROP_NAME, new ExtendPlayerRot(player));
	}

	/**
	 * Returns ExtendedPlayer properties for player
	 * This method is for convenience only; it will make your code look nicer
	 */
	public static final ExtendPlayerRot get(EntityPlayer player)
	{
		return (ExtendPlayerRot) player.getExtendedProperties(EXT_PROP_NAME);
	}

	// Save any custom data that needs saving here
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		// We need to create a new tag compound that will save everything for our Extended Properties
		NBTTagCompound properties = new NBTTagCompound();

		// We only have 2 variables currently; save them both to the new tag
		properties.setFloat(Rot.MODID+"CurrentMana", this.player.getDataWatcher().getWatchableObjectFloat(MANA_WATCHER));
		properties.setFloat(Rot.MODID+"MaxMana", this.maxMana);
		properties.setFloat(Rot.MODID+"CurrentStam", this.player.getDataWatcher().getWatchableObjectFloat(STAM_WATCHER));
		properties.setFloat(Rot.MODID+"MaxStam", this.maxStam);
		
		properties.setInteger(Rot.MODID+"Strength", this.strength);
		properties.setInteger(Rot.MODID+"Dexterity", this.dexterity);
		properties.setInteger(Rot.MODID+"Intelligence", this.intelligence);
		properties.setInteger(Rot.MODID+"Agility", this.agility);
		properties.setInteger(Rot.MODID+"Vitality", this.vitality);
		
		properties.setString(Rot.MODID+"Class", currentClass.getClassName());

		// Now add our custom tag to the player's tag with a unique name (our property's name)
		// This will allow you to save multiple types of properties and distinguish between them
		// If you only have one type, it isn't as important, but it will still avoid conflicts between
		// your tag names and vanilla tag names. For instance, if you add some "Items" tag,
		// that will conflict with vanilla. Not good. So just use a unique tag name.
		compound.setTag(EXT_PROP_NAME, properties);

	}

	// Load whatever data you saved
	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		// Here we fetch the unique tag compound we set for this class of Extended Properties
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		// Get our data from the custom tag compound
		this.strength = properties.getInteger(Rot.MODID+"Strength");
		this.vitality = properties.getInteger(Rot.MODID+"Vitality");
		this.dexterity = properties.getInteger(Rot.MODID+"Dexterity");
		this.intelligence = properties.getInteger(Rot.MODID+"Intelligence");
		this.agility = properties.getInteger(Rot.MODID+"Agility");
		
		this.player.getDataWatcher().updateObject(MANA_WATCHER, properties.getFloat(Rot.MODID+"CurrentMana"));
		this.maxMana = properties.getFloat(Rot.MODID+"MaxMana");
		this.player.getDataWatcher().updateObject(STAM_WATCHER, properties.getFloat(Rot.MODID+"CurrentStam"));
		this.maxStam = properties.getFloat(Rot.MODID+"MaxStam");
		
		this.currentClass = playerClass.getClass(properties.getString(Rot.MODID+"Class"));
		// Just so you know it's working, add this line:
		//System.out.println("[TUT PROPS] Mana from NBT: " + this.currentMana + "/" + this.maxMana);
	}

	/*
	I personally have yet to find a use for this method. If you know of any,
	please let me know and I'll add it in! 
	*/
	@Override
	public void init(Entity entity, World world){}

	/*
	That's it for the IExtendedEntityProperties methods, but we need to add
	a few of our own in order to interact with our new variables. For now,
	let's make one method to consume mana and one to replenish it.
	 */

	/**
	 * Returns true if the amount of mana was consumed or false
	 * if the player's current mana was insufficient
	 */
	// This method gets a little messier, unfortunately, due to the unwieldy length of getting information
	// from DataWatcher vs. referencing a local variable, so we'll create a local variable instead
	public final boolean consumeMana(float amount)
	{
		// This variable makes it easier to write the rest of the method
		float mana = this.player.getDataWatcher().getWatchableObjectFloat(MANA_WATCHER);
	
		// These two lines are the same as before
		boolean sufficient = amount <= mana;
		//mana -= (amount < mana ? amount : mana);
		if(sufficient)
		{
			mana -= amount;
			// Update the data watcher object with the new value
			this.player.getDataWatcher().updateObject(MANA_WATCHER, mana);
		}
	
		// note that we no longer need to call 'sync()' to update the client
	
		return sufficient;
	}
	
	public final boolean consumeStam(float amount)
	{
		// This variable makes it easier to write the rest of the method
		float stam = this.player.getDataWatcher().getWatchableObjectFloat(STAM_WATCHER);
	
		// These two lines are the same as before
		boolean sufficient = amount <= stam;
		//mana -= (amount < mana ? amount : mana);
		if(sufficient)
		{
			stam -= amount;
			// Update the data watcher object with the new value
			this.player.getDataWatcher().updateObject(STAM_WATCHER, stam);
		}
	
		// note that we no longer need to call 'sync()' to update the client
	
		return sufficient;
	}

	/**
	 * Simple method sets current mana to max mana
	 */
	public void replenishMana()
	{
		this.player.getDataWatcher().updateObject(MANA_WATCHER, this.maxMana);
	}
	
	public void replenishStam()
	{
		this.player.getDataWatcher().updateObject(STAM_WATCHER, this.maxStam);
	}
	
	public void regenMana(float amount)
	{
		float mana = this.player.getDataWatcher().getWatchableObjectFloat(MANA_WATCHER);
		
		if (mana != this.maxMana)
		{
			mana += amount;
			if (mana > this.maxMana)this.player.getDataWatcher().updateObject(MANA_WATCHER, this.maxMana);
			else this.player.getDataWatcher().updateObject(MANA_WATCHER, mana);
		}
		/*else if (mana > this.maxMana)
		{
			mana = this.maxMana;
		}*/
			
	}
	
	public void regenStam(float amount)
	{
		float stam = this.player.getDataWatcher().getWatchableObjectFloat(STAM_WATCHER);
		
		if (stam != this.maxStam)
		{
			stam += amount;
			if (stam > this.maxStam)this.player.getDataWatcher().updateObject(STAM_WATCHER, this.maxStam);
			else this.player.getDataWatcher().updateObject(STAM_WATCHER, stam);
		}
	}
	
	public float getCurrentMana()
	{
		return this.player.getDataWatcher().getWatchableObjectFloat(MANA_WATCHER);
	}
	
	public float getCurrentStam()
	{
		return this.player.getDataWatcher().getWatchableObjectFloat(STAM_WATCHER);
	}

	public float getMaxMana() 
	{
		return this.maxMana;
	}
	
	public float getMaxStam() 
	{
		return this.maxStam;
	}

	public void setMaxMana(float readFloat) 
	{
		this.maxMana = readFloat + ((currentClass == playerClass.MAGE ? 45 : 20) * this.intelligence);		
	}
	
	public void setMaxStam(float readFloat) 
	{
		this.maxStam = readFloat + (15 * this.strength) + (25 * this.vitality);		
	}

	public void setCurrentMana(float readFloat) {
		this.player.getDataWatcher().updateObject(MANA_WATCHER, (readFloat < this.maxMana ? readFloat : this.maxMana));		
	}
	
	public void setCurrentStam(float readFloat) {
		this.player.getDataWatcher().updateObject(STAM_WATCHER, (readFloat < this.maxStam ? readFloat : this.maxStam));		
	}
	
	public boolean needsMana()
	{
		if (getCurrentMana() < maxMana)return true;
		else return false;
	}
	
	public boolean needsStam()
	{
		if (getCurrentStam() < maxStam)return true;
		else return false;
	}
	
	public void setStrength(int value)
	{
		this.strength = MathHelper.clamp_int(value + currentClass.getStr(), -20, 20);
		setMaxStam(currentClass.getMaxStam());
	}
	
	public void setVitality(int value)
	{
		this.vitality = MathHelper.clamp_int(value + currentClass.getVit(), -20, 20);
		setMaxStam(currentClass.getMaxStam());
	}
	
	public void setDexterity(int value)
	{
		this.dexterity = MathHelper.clamp_int(value + currentClass.getDex(), -20, 20);
	}
	
	
	public void setIntelligence(int value)
	{
		this.intelligence = MathHelper.clamp_int(value + currentClass.getInte(), -20, 20);
		setMaxMana(currentClass.getMaxMana());
	}
	public void setAgility(int value)
	{
		this.agility = MathHelper.clamp_int(value + currentClass.getAgi(), -20, 20);
		if (player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() == 2)
		{
			player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2);
			System.out.println("set speed");			
		}
		//System.out.println(player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
	}
	
	public int getStrength()
	{
		return this.strength;
	}
	
	public int getVitality()
	{
		return this.vitality;
	}
	
	public int getDexterity()
	{
		return this.dexterity;
	}
	
	public int getAgility()
	{
		return this.agility;
	}
	
	public int getIntelligence()
	{
		return this.intelligence;
	}
}
