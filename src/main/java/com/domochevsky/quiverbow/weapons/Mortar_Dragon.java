package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.Sabot_Rocket;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Mortar_Dragon extends _WeaponBase
{
	public Mortar_Dragon() { super("dragon_mortar", 8); }


	

	private int FireDur;
	private double ExplosionSize;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/DragonMortar");
	}


	@Override
	public IIcon getIconFromDamage(int meta)	// This is for inventory display. Comes in with metadata. Only gets called on client side
	{
		return this.Icon; 	// Full, default
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		// Good to go (already verified)
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		// Firing
		Sabot_Rocket projectile = new Sabot_Rocket(world, entity, (float) this.Speed);
		projectile.damage = dmg;
		projectile.fireDuration = this.FireDur;
		projectile.explosionSize = this.ExplosionSize;

		world.spawnEntityInWorld(projectile); 	// Firing!

		// SFX
		world.playSoundAtEntity(entity, "tile.piston.out", 1.0F, 2.0F);
		world.playSoundAtEntity(entity, "fireworks.launch", 1.0F, 0.5F);

		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 11, (byte) 1);

		// The parent function will take care of ammo and cooldown
		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{
		world.playSoundAtEntity(entity, "random.click", 0.6F, 2.0F);
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What damage are my rockets dealing, at least? (default 4)", 4).getInt();
		this.DmgMax = config.get(this.name, "What damage are my rockets dealing, tops? (default 6)", 6).getInt();

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3).getInt();

		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.FireDur = config.get(this.name, "How long is what I hit on fire? (default 6s)", 6).getInt();
		this.ExplosionSize = config.get(this.name, "How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0).getDouble();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Dragon Mortar (Empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ipi", "isr", "tsf",
					't', Blocks.tripwire_hook,
					'i', Items.iron_ingot,
					's', Blocks.sticky_piston,
					'p', Blocks.piston,
					'r', Items.repeater,
					'f', Items.flint_and_steel
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack ammo = Helper.getAmmoStack(RocketBundle.class, 0);
		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(ammo.getItem(), 1));
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		return "MortarDragon";	// Regular
	}
}
