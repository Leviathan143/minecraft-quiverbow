package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.SoulShot;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SoulCairn extends _WeaponBase
{
    public SoulCairn()
    {
	super("soul_cairn", 1);
	this.setCreativeTab(CreativeTabs.TOOLS); // Tool, so on the tool tab
	this.Cooldown = 20;
	setFiringBehaviour(new SingleShotFiringBehaviour<SoulCairn>(this, (world, weaponStack, entity, data) -> new SoulShot(world, entity, (float) this.Speed))
	{
	    @Override
	    public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand) 
	    { 
		entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 2); // A sacrifice in blood
		super.fire(stack, world, entity, hand);
	    }
	});
    }

    @Override
    public void doFireFX(World world, Entity entity)
    {
	entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0F, 2.0F);
	entity.playSound(SoundEvents.BLOCK_NOTE_BASS, 1.0F, 0.4F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
	this.Speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
		.getDouble();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 4)", 4)
		.getInt();

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. This is easily abusable.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Soul Cairn (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "e e", "epe", "oto", 'o',
		    Blocks.OBSIDIAN, 'e', Blocks.END_STONE, 't', Blocks.TRIPWIRE_HOOK, 'p', Blocks.PISTON);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Reload with 1 diamond
	GameRegistry.addShapelessRecipe(new ItemStack(this), Items.DIAMOND,
		Helper.createEmptyWeaponOrAmmoStack(this, 1));
    }
}
