package com.github.haretaro.weakpingwo.role;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.Topic;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;

public class Werewolf extends AbstractWerewolf {
	
	private GameInfo gameInfo;
	private Agent me;
	private Agent requestedTarget;
	private long readTalkNumber = 0;
	private long readWhisperNumber = 0;
	private int talkCount = 0;

	@Override
	public Agent attack() {
		if(requestedTarget != null){
			return requestedTarget;
		}else{
			return getWhiteAgents().get(0);
		}
	}

	@Override
	public void dayStart() {
		requestedTarget = null;
		talkCount = 0;
	}

	@Override
	public void finish() {
	}
	
	private List<Agent> getWolfAgents(){
		Map<Agent,Role> roleMap = gameInfo.getRoleMap();
		return roleMap.keySet()
				.stream()
				.filter(a->roleMap.get(a)==Role.WEREWOLF)
				.collect(Collectors.toList());
	}
	
	private List<Agent> getWhiteAgents(){
		List<Agent> wolvs = getWolfAgents();
		List<Agent> whites = gameInfo.getAgentList();
		whites.removeAll(wolvs);
		System.out.println(whites);
		return whites;
	}
	
	@Override
	public void initialize(GameInfo gameInfo, GameSetting gameSetting){
		this.gameInfo = gameInfo;
		me = gameInfo.getAgent();
	}

	@Override
	public String talk() {
		if(gameInfo.getDay() == 1 && talkCount == 1){
			String comingoutTalk = TemplateTalkFactory.comingout(me,Role.MEDIUM);
			return comingoutTalk;
		}
		
		if(gameInfo.getDay() == 0 && talkCount == 1){
			return TemplateTalkFactory.estimate(me, Role.WEREWOLF);
		}
		
		if(talkCount == 1 && gameInfo.getDay() > 0){
			Agent hangman = gameInfo.getExecutedAgent();
			Species species = Species.HUMAN;
			if(getWolfAgents().contains(hangman)){
				species = Species.WEREWOLF;
			}
			String mediumResult = TemplateTalkFactory.inquested(hangman, species);
			return mediumResult;
		}
		
		return Talk.OVER;
	}
	
	@Override
	public void update(GameInfo gameInfo){
		this.gameInfo = gameInfo;
		talkCount ++;
		
		List<Talk> talkList = gameInfo.getTalkList();
		talkList.stream().skip(readTalkNumber)
		.forEach(talk -> {
			
		});
		readTalkNumber = talkList.size();
		
		List<Talk> whisperList = gameInfo.getWhisperList();
		whisperList.stream().skip(readWhisperNumber)
		.forEach(whisper -> {
			Utterance utterance = new Utterance(whisper.getContent());
			
			if(utterance.getTopic() == Topic.ATTACK || utterance.getTopic() == Topic.VOTE){
				if(requestedTarget == null){
					requestedTarget = utterance.getTarget();
				}
			}
		});
		readWhisperNumber = whisperList.size();
	}

	@Override
	public Agent vote() {
		if(requestedTarget != null){
			return requestedTarget;
		}else{
			return getWhiteAgents().get(0);
		}
	}

	@Override
	public String whisper() {
		if(requestedTarget == null){
			String talk = TemplateWhisperFactory.attack(getWhiteAgents().get(0));
			return talk;
		}
		return Talk.OVER;
	}

}
