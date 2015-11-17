package com.github.haretaro.weakpingwo.role;

import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

import com.github.haretaro.pingwo.role.PingwoVillager;

public class WeakPingwo extends AbstractRoleAssignPlayer{
	
	public WeakPingwo(){
		setVillagerPlayer(new PingwoVillager());
		setWerewolfPlayer(new Werewolf());
	}

	@Override
	public String getName() {
		return "弱い平兀";
	}

}
