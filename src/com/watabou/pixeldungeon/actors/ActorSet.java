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
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.buffs.Buff;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class ActorSet implements Bundlable {

    private static HashSet<Actor> actors = new HashSet<Actor>();
	private static SparseArray<Actor> ids = new SparseArray<Actor>();
	private static Char[] chars = new Char[Level.LENGTH];

    private static float now = 0;

    public static HashSet<Actor> getActors() {
        return actors;
    }
    
    public static Char[] getChars() {
        return chars;
    }

    public static void clear() {
		
		now = 0;
        Arrays.fill( chars, null );

		actors.clear();
		
		ids.clear();
	}

	public static void add( Actor actor ) {
		add( actor, now );
	}
	
	public static void addDelayed( Actor actor, float delay ) {
		add( actor, now + delay );
	}
	
	private static void add( Actor actor, float time ) {
		
		if (actors.contains( actor )) {
			return;
		}
		
		if (actor.getId() > 0) {
			ids.put( actor.getId(),  actor );
		}
		
		actors.add( actor );
		actor.setTime(actor.getTime() + time) ;
		actor.onAdd();
		
		if (actor instanceof Char) {
			Char ch = (Char)actor;
			chars[ch.pos] = ch;
			for (Buff buff : ch.buffs()) {
				actors.add( buff );
				buff.onAdd();
			}
		}
	}
	
	public static void remove( Actor actor ) {
		
		if (actor != null) {
			actors.remove( actor );
			actor.onRemove();
			
			if (actor.getId() > 0) {
				ids.remove( actor.getId() );
			}
		}
	}
	
    public static Actor findById( int id ) {
		return ids.get( id );
	}
	
    public static Char findChar( int pos ) {
		return chars[pos];
	}

}