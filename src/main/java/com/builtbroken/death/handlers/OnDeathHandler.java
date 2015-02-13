package com.builtbroken.death.handlers;

import com.builtbroken.death.events.DestroyItemEvent;
import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.recipe.ItemStackWrapper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;

/**
 * Created by robert on 2/13/2015.
 */
public class OnDeathHandler
{
    public static HashMap<ItemStackWrapper, ItemStack> fire_damage_result = new HashMap();

    //Called first to handle and trigger sub events
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        //Make sure we are a player and that the world doesn't allow players to keep there items
        if (event.entity instanceof EntityPlayer && !event.entity.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            //Scan threw inventory looking for items to destroy remove, or lose if the player dies
            int tnt_count = 0;
            for (int slot = 0; slot < ((EntityPlayer) event.entity).inventory.mainInventory.length; slot++)
            {
                ItemStack stack = ((EntityPlayer) event.entity).inventory.mainInventory[slot];
                if(stack != null)
                {
                    //TODO handle other explosives
                    if (stack.getItem() == Items.tnt_minecart || stack.getItem() == Item.getItemFromBlock(Blocks.tnt))
                    {
                        tnt_count += stack.stackSize;
                        ((EntityPlayer) event.entity).inventory.mainInventory[slot] = null;
                    }
                    else
                    {
                        ((EntityPlayer) event.entity).inventory.mainInventory[slot] = handleItem(new ItemStackWrapper(stack), event.source);
                    }
                }
            }
            //TODO add progress delay to lure in looters before blowing up
            if(tnt_count > 0)
            {
                tnt_count = Math.max(tnt_count, 20);
                IExplosiveHandler ex = ExplosiveRegistry.get("TNT");
                if(ex != null)
                {
                    ExplosiveRegistry.triggerExplosive(new Location(event.entity), ex, new TriggerCause.TriggerCauseEntity(event.entity), tnt_count, null);
                }
            }
        }
    }

    /**
     * Called to damage an item
     * @param stack
     * @param source
     * @return
     */
    public ItemStack handleItem(ItemStackWrapper stack, DamageSource source)
    {
        if(source == DamageSource.inFire || source.isFireDamage())
        {
            if(fire_damage_result.containsKey(stack))
            {
                return fire_damage_result.get(stack);
            }
        }
        if(stack.itemStack != null)
        {
            DestroyItemEvent event = new DestroyItemEvent(stack.itemStack, source);
            MinecraftForge.EVENT_BUS.post(event);
            return event.item;
        }
        return stack.itemStack;
    }
}
