package com.roboboy.PraedaGrandis.Configuration;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public enum LocationComponentType
{
	X_ABSOLUTE			("X=") {
		@Override public void modify(Location location, double amount) {
			location.setX(amount);
	}},
	Y_ABSOLUTE			("Y=") {
		@Override public void modify(Location location, double amount) {
			location.setY(amount);
	}},
	Z_ABSOLUTE			("Z=") {
		@Override public void modify(Location location, double amount) {
			location.setZ(amount);
	}},
	X_RELATIVE			("X") {
		@Override public void modify(Location location, double amount) {
			location.add(amount, 0D, 0D);
	}},
	Y_RELATIVE			("Y") {
		@Override public void modify(Location location, double amount) {
			location.add(0D, amount, 0D);
	}},
	Z_RELATIVE			("Z") {
		@Override public void modify(Location location, double amount) {
			location.add(0D, 0D, amount);
	}},
	FACING				("F") {
		@Override public void modify(Location location, double amount) {
			Vector forward = location.getDirection();
			location.add(forward.multiply(amount));
	}},
	FACING_UP			("U") {
		@Override public void modify(Location location, double amount) {
			Vector forward = location.getDirection();
			Vector right = new Vector(-forward.getZ(), 0, forward.getX());
			Vector up = right.crossProduct(forward);
			up.normalize();
			location.add(up.multiply(amount));
	}},
	FACING_RIGHT		("R") {
		@Override public void modify(Location location, double amount) {
			Vector forward = location.getDirection();
			Vector right = new Vector(-forward.getZ(), 0, forward.getX());
			right.normalize().multiply(amount);
			location.add(right.multiply(amount));
	}},
	FACING_HORIZONTAL	("FH") {
		@Override public void modify(Location location, double amount) {
			Vector hForward = location.getDirection().setY(0);
			hForward.normalize();
			location.add(hForward.multiply(amount));
	}};
	
	private final String identifier;
	
	private LocationComponentType(String identifier) {
		this.identifier = identifier;
	}
	
	public abstract void modify(Location location, double amount);
	
	static public LocationComponentType fromString(String typeString) {
		typeString = typeString.toUpperCase();
		for (LocationComponentType type : values()) {
			if (type.identifier.equals(typeString));
		}
		return null;
	}
}
