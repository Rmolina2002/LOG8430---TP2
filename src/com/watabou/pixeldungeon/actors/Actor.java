/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.actors;

import java.util.Arrays;
import java.util.HashSet;

import android.util.SparseArray;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.Statistics;
import com.watabou.pixeldungeon.actors.blobs.Blob;
import com.watabou.pixeldungeon.actors.ActorSet;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Actor implements Bundlable {
	
	public static final float TICK	= 1f;

	private float time;
	
	private int id = 0;
	
	protected abstract boolean act();
	
	protected void spend( float time ) {
		this.time += time;
	}
	
	protected void postpone( float time ) {
		if (this.time < now + time) {
			this.time = now + time;
		}
	}
	
	protected float cooldown() {
		return time - now;
	}
	
	protected void diactivate() {
		time = Float.MAX_VALUE;
	}
	
	protected void onAdd() {}
	
	protected void onRemove() {}
	
	private static final String TIME	= "time";
	private static final String ID		= "id";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( TIME, time );
		bundle.put( ID, id );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		time = bundle.getFloat( TIME );
		id = bundle.getInt( ID );
	}
	
	public float getTime() {
		return this.time;	
	}

	public void setTime( float time ) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public int id() {
		if (id > 0) {
			return id;
		} else {
			int max = 0;
			for (Actor a : ActorSet.getActors()) {
				if (a.id > max) {
					max = a.id;
				}
			}
			return (id = max + 1);
		}
	}
	
	// **********************
	// *** Static members ***
	
	private static ActorSet all;
	private static Actor current;
	
	
	private static float now = 0;
	
	
	public static void clear() {
		ActorSet.clear();
	}
	
	public static void fixTime() {
		
		if (Dungeon.hero != null && ActorSet.getActors().contains( Dungeon.hero )) {
			Statistics.duration += now;
		}
		
		float min = Float.MAX_VALUE;
		for (Actor a : ActorSet.getActors()) {
			if (a.time < min) {
				min = a.time;
			}
		}
		for (Actor a : ActorSet.getActors()) {
			a.time -= min;
		}
		now = 0;
	}
	
	public static void init() {
		
		ActorSet.addDelayed( Dungeon.hero, -Float.MIN_VALUE );
		
		for (Mob mob : Dungeon.level.mobs) {
			ActorSet.add( mob );
		}
		
		for (Blob blob : Dungeon.level.blobs.values()) {
			ActorSet.add( blob );
		}
		
		current = null;
	}
	
	public static void occupyCell( Char ch ) {
		ActorSet.getChars()[ch.pos] = ch;
	}
	
	public static void freeCell( int pos ) {
		ActorSet.getChars()[pos] = null;
	}
	
	/*protected*/public void next() {
		if (current == this) {
			current = null;
		}
	}
	
	public static void process() {
		
		if (current != null) {
			return;
		}
	
		boolean doNext;

		do {
			now = Float.MAX_VALUE;
			current = null;
			
			Arrays.fill( ActorSet.getChars(), null );
			
			for (Actor actor : ActorSet.getActors()) {
				if (actor.time < now) {
					now = actor.time;
					current = actor;
				}
				
				if (actor instanceof Char) {
					Char ch = (Char)actor;
					ActorSet.getChars()[ch.pos] = ch;
				}
			}

			if (current != null) {
				
				if (current instanceof Char && ((Char)current).sprite.isMoving) {
					// If it's character's turn to act, but its sprite 
					// is moving, wait till the movement is over
					current = null;
					break;
				}
				
				doNext = current.act();
				if (doNext && !Dungeon.hero.isAlive()) {
					doNext = false;
					current = null;
				}
			} else {
				doNext = false;
			}
			
		} while (doNext);
	}



}
