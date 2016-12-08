package com.lesserhydra.resmagna.nms;

import net.minecraft.server.v1_11_R1.EntityArrow;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftArrow;

public class NMSEntity {
	
	public static void setArrowPickup(org.bukkit.entity.Arrow arrow, boolean value) {
		CraftArrow craftArrow = (CraftArrow) arrow;
		EntityArrow nmsArrow = craftArrow.getHandle();
		nmsArrow.fromPlayer = value ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
	}
	
}
