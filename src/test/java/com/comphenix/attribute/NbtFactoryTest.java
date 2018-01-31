// The MIT License (MIT)
//
// Copyright (c) 2015 Kristian Stangeland
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
// files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
// modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the 
// Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
// ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.comphenix.attribute;

import com.comphenix.attribute.mocking.BukkitInitialization;
import com.lesserhydra.bukkitutil.InventoryUtil;
import com.lesserhydra.bukkitutil.nbt.NbtCompound;
import com.lesserhydra.bukkitutil.nbt.NbtFactory;
import com.lesserhydra.bukkitutil.nbt.NbtList;
import com.lesserhydra.bukkitutil.nbt.NbtType;
import com.lesserhydra.bukkitutil.nbt.volatilecode.CraftNbtCompound;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest({CraftItemFactory.class, CraftServer.class})
@PowerMockIgnore("javax.management.*")
public class NbtFactoryTest {
	@BeforeClass
	public static void initializeMeta() {
		BukkitInitialization.initializeItemMeta();
	}
	
	@Test
	public void testCompound() {
		NbtCompound compound = createTestCompound();
		
		// Simple test
		verifyCompound(compound);
	}
	
	private void verifyCompound(NbtCompound compound) {
		// Verify the NBT content
		assertEquals(NBTTagCompound.class, ((CraftNbtCompound)compound).getHandle().getClass());
		assertEquals(2009, compound.getInteger("released"));
		assertEquals("Minecraft", compound.getString("game"));
		//assertEquals(Arrays.asList(1, 2, 3), compound.getList("list", NbtType.INT, false));
		//assertEquals("Markus Persson", compound.getPath("author.name"));
		//assertEquals("Kristian Stangeland", compound.getPath("fan.name"));
		
		//assertNull("Missing root path was not NULL", compound.getPath("missing.test"));
	}
	
	private NbtCompound createTestCompound() {
		// Use NbtFactory.fromCompound(obj); to load from a NBTCompound class.
		NbtCompound compound = NbtFactory.makeCompound();
		NbtCompound author = NbtFactory.makeCompound();
		 
		compound.set("released", 2009);
		compound.set("game", "Minecraft");
		compound.set("author", author);
		compound.set("bytes", new byte[] { 1, 2, 3 });
		compound.set("integers", new int[] { 1, 2, 3});
		
		//compound.put("list", NbtFactory.createList(1, 2, 3));
		NbtList list = compound.getList("list", NbtType.INT, true);
		list.add(1);
		list.add(2);
		list.add(3);

		author.set("name", "Markus Persson");
		
		//compound.putPath("fan.name", "Kristian Stangeland");
		NbtCompound fan = compound.getCompound("fan", true);
		fan.set("name", "Kristian Stangeland");
		
		return compound;
	}
	
	/*@Test
	public void testSaving() throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final NbtCompound compound = createTestCompound();
		
		// Save the compound with compression
	    compound.saveTo(new ByteSink() {
	    	public OutputStream openStream() throws IOException {
	    		return output;
	    	}
		}, StreamOptions.GZIP_COMPRESSION);
	     
	    // Load the compound
	    NbtCompound loaded = NbtFactory.fromStream(
	    		ByteSource.wrap(output.toByteArray()), StreamOptions.GZIP_COMPRESSION);
	    verifyCompound(loaded);
	}*/
	
	@Test
	public void testItemMeta() {
	    ItemStack stack = InventoryUtil.getCraftItemStack(new ItemStack(Material.GOLD_AXE));
	    NbtCompound other = InventoryUtil.getItemTag(stack, true);
	    
	    // Do whatever
		//other.putPath("display.Name", "New display");
		NbtCompound display = other.getCompound("display", true);
		display.set("Name", "New display");
		
		//other.putPath("display.Lore", NbtFactory.createList("Line 1", "Line 2"));
		NbtList loreList = display.getList("Lore", NbtType.STRING, true);
		loreList.add("Line 1");
		loreList.add("Line 2");
		
	    ItemMeta meta = stack.getItemMeta();
	    assertEquals("New display", meta.getDisplayName());
	    assertEquals(Arrays.asList("Line 1", "Line 2"), meta.getLore());
	}
}
