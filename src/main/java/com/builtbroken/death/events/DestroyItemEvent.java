package com.builtbroken.death.events;

import com.builtbroken.mc.core.content.damage.DamageSources;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import java.awt.event.ItemEvent;

/** Call when an item is destroyed by a damage source. Normally called
 * when a player dies.
 *
 * Created by robert on 2/13/2015.
 */
public class DestroyItemEvent extends Event
{
    //Feel free to change this item
    public ItemStack item;
    public final DamageSource source;

    public DestroyItemEvent(ItemStack item, DamageSource source)
    {
        this.item = item;
        this.source = source;
    }
}
