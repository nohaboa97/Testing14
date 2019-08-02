package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;

public class Inventory implements IInventory, IRecipeHelperPopulator {
   private final int slotsCount;
   private final NonNullList<ItemStack> inventoryContents;
   private List<IInventoryChangedListener> listeners;

   public Inventory(int p_i50397_1_) {
      this.slotsCount = p_i50397_1_;
      this.inventoryContents = NonNullList.withSize(p_i50397_1_, ItemStack.EMPTY);
   }

   public Inventory(ItemStack... p_i50398_1_) {
      this.slotsCount = p_i50398_1_.length;
      this.inventoryContents = NonNullList.from(ItemStack.EMPTY, p_i50398_1_);
   }

   /**
    * Add a listener that will be notified when any item in this inventory is modified.
    */
   public void addListener(IInventoryChangedListener listener) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(listener);
   }

   /**
    * removes the specified IInvBasic from receiving further change notices
    */
   public void removeListener(IInventoryChangedListener listener) {
      this.listeners.remove(listener);
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventoryContents.size() ? this.inventoryContents.get(index) : ItemStack.EMPTY;
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, index, count);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack addItem(ItemStack stack) {
      ItemStack itemstack = stack.copy();

      for(int i = 0; i < this.slotsCount; ++i) {
         ItemStack itemstack1 = this.getStackInSlot(i);
         if (itemstack1.isEmpty()) {
            this.setInventorySlotContents(i, itemstack);
            this.markDirty();
            return ItemStack.EMPTY;
         }

         if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
            int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
            int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());
            if (k > 0) {
               itemstack1.grow(k);
               itemstack.shrink(k);
               if (itemstack.isEmpty()) {
                  this.markDirty();
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (itemstack.getCount() != stack.getCount()) {
         this.markDirty();
      }

      return itemstack;
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      ItemStack itemstack = this.inventoryContents.get(index);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.inventoryContents.set(index, ItemStack.EMPTY);
         return itemstack;
      }
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventoryContents.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.slotsCount;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.inventoryContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
      if (this.listeners != null) {
         for(IInventoryChangedListener iinventorychangedlistener : this.listeners) {
            iinventorychangedlistener.onInventoryChanged(this);
         }
      }

   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      return true;
   }

   public void clear() {
      this.inventoryContents.clear();
   }

   public void fillStackedContents(RecipeItemHelper helper) {
      for(ItemStack itemstack : this.inventoryContents) {
         helper.accountStack(itemstack);
      }

   }
}