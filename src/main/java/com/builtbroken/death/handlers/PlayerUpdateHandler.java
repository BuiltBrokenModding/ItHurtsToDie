package com.builtbroken.death.handlers;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import com.builtbroken.mc.prefab.recipe.ItemStackWrapper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Created by robert on 2/13/2015.
 */
public class PlayerUpdateHandler
{
    @SubscribeEvent
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(event.entity instanceof EntityPlayer && !event.entity.worldObj.isRemote && event.entity.worldObj.getWorldInfo().getWorldTime() % 10 == 0)
        {
            if(event.entity.isBurning() && event.entity.worldObj.rand.nextBoolean())
            {
                int tnt_count = 0;
                for (int slot = 0; slot < ((EntityPlayer) event.entity).inventory.mainInventory.length; slot++)
                {
                    ItemStack stack = ((EntityPlayer) event.entity).inventory.mainInventory[slot];
                    //TODO handle other explosives
                    if(stack.getItem() == Items.tnt_minecart || stack.getItem() == Item.getItemFromBlock(Blocks.tnt))
                    {
                        tnt_count += stack.stackSize;
                        ((EntityPlayer) event.entity).inventory.mainInventory[slot] = null;
                    }
                    else if(stack.getItem() == Items.gunpowder)
                    {
                        tnt_count += stack.stackSize / 5;
                        ((EntityPlayer) event.entity).inventory.mainInventory[slot] = null;
                    }
                }
                //TODO add progress delay to lure in looters before blowing up
                if(tnt_count > 0)
                {
                    tnt_count = Math.max(tnt_count, 20);
                    IExplosiveHandler ex = ExplosiveRegistry.get("tnt");
                    if(ex != null)
                    {
                        ExplosiveRegistry.triggerExplosive(new Location(event.entity), ex, new TriggerCause.TriggerCauseEntity(event.entity), tnt_count, null);
                    }
                }
            }
        }
    }
}
