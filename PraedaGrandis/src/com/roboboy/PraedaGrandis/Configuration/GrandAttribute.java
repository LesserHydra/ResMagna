package com.roboboy.PraedaGrandis.Configuration;

import com.comphenix.attribute.Attributes.Attribute;
import com.comphenix.attribute.Attributes.AttributeType;
import com.comphenix.attribute.Attributes.Operation;

public class GrandAttribute
{
	final private String 		name;
	final private AttributeType type;
	final private Operation 	operation;
	final private double 		amount;
	
	GrandAttribute(ConfigString attString)
	{
		//TODO: Custom attributes
		
		if (attString.size() == 3)
		{
			name = attString.get(0);
			
			//AttributeType
			//type = null;
			switch (name)
			{
			case "attackdamage":
				type = AttributeType.GENERIC_ATTACK_DAMAGE;
				break;
			case "followrange":
				type = AttributeType.GENERIC_FOLLOW_RANGE;
				break;
			case "knockbackresistance":
				type = AttributeType.GENERIC_KNOCKBACK_RESISTANCE;
				break;
			case "maxhealth":
				type = AttributeType.GENERIC_MAX_HEALTH;
				break;
			case "movementspeed":
				type = AttributeType.GENERIC_MOVEMENT_SPEED;
				break;
			default:
				//TODO: Error handling
				//TODO: Debug
				type = null;
				break;
			}
			
			//Opperation and amount
			//operation = null;
			if (attString.get(2).contains("%"))
			{
				double preamount = Double.valueOf(attString.get(2).replace('%', ' ')) / 100;
				
				if (attString.get(1).equals("+")) {
					amount = preamount;
					operation = Operation.ADD_PERCENTAGE;
				}
				else if (attString.get(1).equals("-")) {
					amount = -preamount;
					operation = Operation.ADD_PERCENTAGE;
				}
				else if (attString.get(1).equals("*")) {
					amount = preamount - 1;
					operation = Operation.MULTIPLY_PERCENTAGE;
				}
				else if (attString.get(1).equals("/")) {
					amount = 1/preamount - 1;
					operation = Operation.MULTIPLY_PERCENTAGE;
				}
				else {
					amount = 0;
					operation = null;
				}
			}
			else
			{
				double preamount = Double.valueOf(attString.get(2));
				if (attString.get(1).equals("+")) {
					amount = preamount;
					operation = Operation.ADD_NUMBER;
				}
				else if (attString.get(1).equals("-")) {
					amount = -preamount;
					operation = Operation.ADD_NUMBER;
				}
				else if (attString.get(1).equals("*")) {
					amount = preamount - 1;
					operation = Operation.MULTIPLY_PERCENTAGE;
				}
				else if (attString.get(1).equals("/")) {
					amount = 1/preamount - 1;
					operation = Operation.MULTIPLY_PERCENTAGE;
				}
				else {
					amount = 0;
					operation = null;
				}
			}
			
			//Add attribute
			if (type == null || operation == null) {
				//TODO: Error handling
			}
		}
		else {
			//TODO: Error handling
			name = null;
			type = null;
			operation = null;
			amount = 0;
		}
	}
	
	public Attribute build() {
		return Attribute.newBuilder().name(name).type(type).operation(operation).amount(amount).build();
	}
}
