package com.domochevsky.quiverbow.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class InventoryHelper
{
    public static boolean hasItem(EntityPlayer player, Item item, int amount)
    {
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == item && stack.getCount() >= amount) return true;
        }
        return false;
    }

    public static boolean hasBlock(EntityPlayer player, Block block, int amount)
    {
        return hasItem(player, Item.getItemFromBlock(block), amount);
    }

    public static boolean hasIngredient(EntityPlayer player, Ingredient ingredient, int amount)
    {
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (ingredient.apply(stack) && stack.getCount() >= amount)
                return true;
        }
        return false;
    }

    // Omnomnom
    public static boolean consumeItem(EntityPlayer player, Item item, int amount)
    {
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() == item)
            {
                if (stack.getCount() - amount < 0)
                {
                    return false;
                }
                else
                {
                    stack.shrink(amount);
                    if (stack.getCount() <= 0) player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
                    return true;
                }
            }
        }
        return false;
    }

    // Omnomnom 2: Electric Boogaloo
    public static boolean consumeBlock(EntityPlayer player, Block block, int amount)
    {
        return consumeItem(player, Item.getItemFromBlock(block), amount);
    }

    public static boolean consumeIngredient(EntityPlayer player, Ingredient ingredient, int amount)
    {
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (ingredient.apply(stack))
            {
                if (stack.getCount() - amount < 0)
                    return false;
                else
                {
                    stack.shrink(amount);
                    return true;
                }
            }
        }
        return false;
    }

    // Utils for interacting with and checking both hands
    public static ItemStack findItemInHands(EntityLivingBase entity, Item item)
    {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();

        if (mainHand.getItem() == item) return mainHand;
        else if (offHand.getItem() == item) return offHand;
        else return ItemStack.EMPTY;
    }

    public static ItemStack findItemInHandsByClass(EntityLivingBase entity, Class<?> clazz)
    {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();

        if (clazz.isInstance(mainHand.getItem())) return mainHand;
        else if (clazz.isInstance(offHand.getItem())) return offHand;
        else return ItemStack.EMPTY;
    }
}
