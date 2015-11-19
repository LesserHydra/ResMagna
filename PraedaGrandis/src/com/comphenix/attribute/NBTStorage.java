package com.comphenix.attribute;

import org.bukkit.inventory.ItemStack;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.NbtList;

public class NBTStorage
{
    private ItemStack target;
    private NbtList storage;
    private final String storageName;
    
    private NBTStorage(ItemStack target, String storageName) {
    	this.target = NbtFactory.getCraftItemStack(target);
        //this.target = Preconditions.checkNotNull(target, "target cannot be NULL");
        this.storageName = storageName;
        loadStorage(false);
    }
    
    /**
     * Load the NBT list from the TAG compound.
     * @param createIfMissing - create the list if its missing.
     */
    private void loadStorage(boolean createIfMissing) {
    	if (this.storage == null) {
            NbtCompound nbt = NbtFactory.fromItemTag(this.target, createIfMissing);
            if (nbt != null) {
            	this.storage = nbt.getList("CustomStorage", createIfMissing);
            }
    	}
    }
    
    /**
     * Construct a new attribute storage system.
     * <p>
     * The key must be the same in order to retrieve the same data.
     * @param target - the item stack where the data will be stored.
     * @param uniqueKey - the unique key used to retrieve the correct data.
     */
    public static NBTStorage newTarget(ItemStack target, String uniqueKey) {
        return new NBTStorage(target, uniqueKey);
    }
    
    /**
     * Retrieve the data stored in the item.
     * @param defaultValue - the default value to return if no data can be found.
     * @return The stored data, or defaultValue if not found.
     */
    public String getString(String defaultValue) {
        NbtCompound current = getCompound(storageName);
        return current != null ? current.getString("Data", defaultValue) : defaultValue;
    }
    
    /**
     * Retrieve the data stored in the item.
     * @param defaultValue - the default value to return if no data can be found.
     * @return The stored data, or defaultValue if not found.
     */
    public Integer getInteger(Integer defaultValue) {
        NbtCompound current = getCompound(storageName);
        return current != null ? current.getInteger("Data", defaultValue) : defaultValue;
    }
    
    /**
     * Retrieve the data stored in the item.
     * @param defaultValue - the default value to return if no data can be found.
     * @return The stored data, or defaultValue if not found.
     */
    public Double getDouble(Double defaultValue) {
        NbtCompound current = getCompound(storageName);
        return current != null ? current.getDouble("Data", defaultValue) : defaultValue;
    }
    
    /**
     * Determine if we are storing any data.
     * @return TRUE if we are, FALSE otherwise.
     */
    public boolean hasData() {
    	return getCompound(storageName) != null;
    }
    
    /**
     * Set the data stored in the attributes.
     * @param data - the data.
     */
    public void setData(Object data) {
    	loadStorage(true);
    	NbtCompound current = getCompound(storageName);
        if (current == null) {
        	NbtCompound newCompound = NbtFactory.createCompound();
        	newCompound.put("Name", storageName);
        	newCompound.put("Data", data);
            storage.add(newCompound);
        } else {
            current.put("Data", data);
        }
    }
    
    /**
     * Retrieve the target stack. May have been changed.
     * @return The target stack.
     */
    public ItemStack getTarget() {
        return target;
    }
    
    /**
     * Retrieve a compound by name.
     * @param name - the name to search for.
     * @return The first attribute associated with this name, or NULL.
     */
    private NbtCompound getCompound(String name) {
    	if (storage != null) {
    		for (Object obj : storage) {
    			NbtCompound compound = (NbtCompound) obj;
    			if (name.equals(compound.getString("Name", null))) {
    				return compound;
    			}
    		}
    	}
    	return null;
    }
}
