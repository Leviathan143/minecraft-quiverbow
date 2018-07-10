package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;

import net.minecraft.entity.projectile.EntityArrow;

public class CrossbowCompact extends WeaponCrossbow
{
	public CrossbowCompact()
	{
		super("compact_crossbow", 1, (world, weaponStack, entity, data, properties) ->
		{
			EntityArrow entityarrow = Helper.createArrow(world, entity);

			// Random Damage
			int dmg_range = properties.getDamageMin() - properties.getDamageMin(); // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1);// Range will be between
														// 0
			// and 10
			dmg += properties.getDamageMin(); // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			entityarrow.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, properties.getProjectileSpeed(), 0.5F);
			entityarrow.setDamage(dmg);
			entityarrow.setKnockbackStrength(properties.getKnockback());
			
			return entityarrow;
		});
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(14).maximumDamage(20).projectileSpeed(2.5F).knockback(2)
				.mobUsable().build();
	}
}