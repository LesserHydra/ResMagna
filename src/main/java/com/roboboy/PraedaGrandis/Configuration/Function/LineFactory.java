package com.roboboy.PraedaGrandis.Configuration.Function;

import com.roboboy.PraedaGrandis.Abilities.Ability;
import com.roboboy.PraedaGrandis.Abilities.AbilityFactory;
import com.roboboy.PraedaGrandis.Abilities.Conditions.Condition;
import com.roboboy.PraedaGrandis.Abilities.Conditions.ConditionFactory;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineFactory {
	
	public static @Nonnull GrandFunction parseAndLinkLines(@Nonnull Collection<String> lines) {
		return lines.stream()
				.sequential()
				.collect(LineFactory::new, LineFactory::parseLine, LineFactory::combine)
				.finish();
	}
	
	
	//(\w+)\s*(.*)
	private static final Pattern lineTypePattern = Pattern.compile("(\\w+)\\s*(.*)");
	//label\s+(\w+)
	private static final Pattern labelLinePattern = Pattern.compile("label\\s+(\\w+)");
	//jump\s+(\w+)
	private static final Pattern jumpLinePattern = Pattern.compile("jump\\s+(\\w+)");
	//jumpif\s+(\w+)\s+(.+)
	private static final Pattern jumpIfLinePattern = Pattern.compile("jumpif\\s+(\\w+)\\s+(.+)");
	//(\w+[^\@]*(?<!\s))\s*(\@.+)?
	private static final Pattern abilityLinePattern = Pattern.compile("(\\w+[^@]*(?<!\\s))\\s*(@.+)?");
	//delay\s+(\d+)
	private static final Pattern delayLinePattern = Pattern.compile("delay\\s+(\\d+)");
	
	private FunctionLine first;
	private FunctionLine last;
	private final Map<String, GrandFunction> labels = new HashMap<>();
	private final List<Pair<String, Jump>> jumps = new LinkedList<>();
	
	private LineFactory() {}
	
	private void parseLine(String lineString) {
		String l_lineString = lineString.trim().toLowerCase();
		
		//Match
		Matcher typeMatcher = lineTypePattern.matcher(l_lineString);
		if (!typeMatcher.matches()) {
			GrandLogger.log("Invalid line format: ", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + l_lineString, LogType.CONFIG_ERRORS);
			return;
		}
		
		String typeString = typeMatcher.group(1);
		switch (typeString) {
			case "label": parseLabelLine(l_lineString);
				break;
			case "jump": parseJumpLine(l_lineString);
				break;
			case "jumpif": parseJumpIfLine(l_lineString);
				break;
			case "delay": parseDelayLine(l_lineString);
				break;
			default: parseAbilityLine(l_lineString);
				break;
		}
	}
	
	private LineFactory combine(LineFactory other) {
		//Other is empty
		if (other.first == null) return this;
		//This is empty
		else if (first == null) return other;
		
		//Combine
		last.linkNext(other.first);
		last = other.last;
		labels.putAll(other.labels);
		jumps.addAll(other.jumps);
		return this;
	}
	
	private @Nonnull GrandFunction finish() {
		jumps.forEach(this::finishJump);
		return (first == null ? (target) -> {} : first);
	}
	
	private void finishJump(Pair<String, Jump> jump) {
		GrandFunction label = labels.get(jump.getLeft());
		if (label == null) {
			GrandLogger.log("Requested label not found: " + jump.getLeft(), LogType.CONFIG_ERRORS);
			return;
		}
		jump.getRight().linkJump(label);
	}
	
	private void parseLabelLine(String l_lineString) {
		//Match
		Matcher matcher = labelLinePattern.matcher(l_lineString);
		if (!matcher.matches()) {
			GrandLogger.log("Invalid label format: " + l_lineString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Expected: LABEL <label>", LogType.CONFIG_ERRORS);
			return;
		}
		
		//Prepare for labeling next line
		FunctionLine labelLine = new FunctionLine();
		labels.put(matcher.group(1), labelLine);
		addLine(labelLine);
	}
	
	private void parseJumpLine(String l_lineString) {
		//Match
		Matcher matcher = jumpLinePattern.matcher(l_lineString);
		if (!matcher.matches()) {
			GrandLogger.log("Invalid jump format: " + l_lineString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Expected: JUMP <label>", LogType.CONFIG_ERRORS);
			return;
		}
		
		//Result
		JumpLine jumpLine = new JumpLine();
		jumps.add(new ImmutablePair<>(matcher.group(1), jumpLine));
		addLine(jumpLine);
	}
	
	private void parseJumpIfLine(String l_lineString) {
		//Match
		Matcher matcher = jumpIfLinePattern.matcher(l_lineString);
		if (!matcher.matches()) {
			GrandLogger.log("Invalid conditional jump format: " + l_lineString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Expected: JUMPIF <label> <condition> [targeter]", LogType.CONFIG_ERRORS);
			return;
		}
		
		//Get component strings
		String labelName = matcher.group(1);
		String conditionString = matcher.group(2);
		
		//Parse condition, check for fail
		Condition condition = ConditionFactory.build(conditionString);
		if (condition == null) {
			GrandLogger.log("  In line: " + l_lineString, LogType.CONFIG_ERRORS);
			return;
		}
		
		//Result
		JumpIfLine jumpLine = new JumpIfLine(condition);
		jumps.add(new ImmutablePair<>(labelName, jumpLine));
		addLine(jumpLine);
	}
	
	private void parseDelayLine(String l_lineString) {
		//Check validity
		Matcher matcher = delayLinePattern.matcher(l_lineString);
		if (!matcher.matches()) {
			GrandLogger.log("Invalid delay format: " + l_lineString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Expected: DELAY <length>", LogType.CONFIG_ERRORS);
			return;
		}
		
		//Result
		addLine(new DelayLine(Long.parseLong(matcher.group(1))));
	}
	
	private void parseAbilityLine(String l_lineString) {
		//Pull out groupings
		GroupingParser groupingParser = new GroupingParser(l_lineString);
		
		//Match
		Matcher matcher = abilityLinePattern.matcher(groupingParser.getSimplifiedString());
		if (!matcher.matches()) {
			GrandLogger.log("Invalid ability format: " + l_lineString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Expected: <name> [args] [targeter]", LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + groupingParser.getSimplifiedString(), LogType.CONFIG_ERRORS);
			return;
		}
		
		//Get component strings
		String abilityString = groupingParser.readdAllGroupings(matcher.group(1));
		String targeterString = groupingParser.readdAllGroupings(matcher.group(2));
		
		//Null ability or targeter indicates parsing error
		//TODO: expand errors, like in JUMPIF
		Ability ability = AbilityFactory.build(abilityString);
		Targeter targeter = TargeterFactory.build(targeterString);
		if (ability != null && targeter != null) addLine(new AbilityLine(ability, targeter));
	}
	
	private void addLine(FunctionLine functionLine) {
		//First line
		if (first == null) {
			first = functionLine;
			last = functionLine;
			return;
		}
		
		//Next line
		last.linkNext(functionLine);
		last = functionLine;
	}
	
}
