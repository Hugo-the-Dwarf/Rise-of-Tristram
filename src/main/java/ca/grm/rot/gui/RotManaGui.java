package ca.grm.rot.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import ca.grm.rot.Rot;
import ca.grm.rot.libs.ExtendPlayer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RotManaGui extends Gui {
	private Minecraft						mc;
	private static final ResourceLocation	texturepath	= new ResourceLocation(
																Rot.MODID
																		+ ":textures/gui/mana_bar_final.png");

	private int								barW		= 55, barH = 9;

	public RotManaGui(Minecraft mc) {
		super();
		this.mc = mc;
	}

	@SubscribeEvent(
			priority = EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if (event.isCancelable() || (event.type != ElementType.EXPERIENCE)) { return; }
		ExtendPlayer props = ExtendPlayer.get(this.mc.thePlayer);
		
		if ((props == null) || (props.getMaxMana() == 0)) { return; }

		int xPos = 3, yPos = 3;
		this.mc.getTextureManager().bindTexture(texturepath);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		/**
		 * Draw the background bar which contains a transparent section; note
		 * the new size
		 */
		drawTexturedModalRect(xPos, yPos, 0, 0, this.barW, this.barH);
		
		int manabarwidth = (int) (((float) props.getCurrentMana() / props.getMaxMana()) * this.barW);

		drawTexturedModalRect(xPos, yPos, 0, this.barH, manabarwidth, this.barH);
		String s = (int) props.getCurrentMana() + "/" + (int) props.getMaxMana();

		yPos += this.barH + 2;
		this.mc.fontRenderer.drawString(s, xPos + 1, yPos, 0);
		this.mc.fontRenderer.drawString(s, xPos - 1, yPos, 0);
		this.mc.fontRenderer.drawString(s, xPos, yPos + 1, 0);
		this.mc.fontRenderer.drawString(s, xPos, yPos - 1, 0);
		this.mc.fontRenderer.drawString(s, xPos, yPos, 0x0097d9);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
	}
}
