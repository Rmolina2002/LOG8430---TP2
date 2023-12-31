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
package com.watabou.pixeldungeon.items.armor.glyphs;

import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.items.armor.Armor;
import com.watabou.pixeldungeon.items.armor.Armor.Glyph;
import com.watabou.pixeldungeon.items.wands.WandOfBlink;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.sprites.ItemSprite;
import com.watabou.pixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Displacement extends Glyph {

	private static final String TXT_DISPLACEMENT	= "%s of displacement";
	
	private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x66AAFF );

	@Override
	public int proc( Armor armor, Char attacker, Char defender, int damage ) {
		if (Dungeon.bossLevel()) {
			return damage;
		}
		int nTries = calculateTries(armor);
		performTries(nTries, defender);
		return damage;
	}
	public void performTries(int nTries, Char defender){
		for (int i=0; i < nTries; i++) {
			int pos = Random.Int( Level.LENGTH );
			if (isBlinkPossible(pos)) {
				performBlink(defender, pos);
				break;
			}
		}
	}
	public boolean isBlinkPossible(int pos){
		return Dungeon.visible[pos] && Level.passable[pos] && Actor.findChar( pos ) == null;
	}
	public void performBlink(Char defender, int pos){
		WandOfBlink.appear( defender, pos );
		Dungeon.level.press( pos, defender );
		Dungeon.observe();
	}
	public int calculateTries(Armor armor){
		int level = armor.effectiveLevel();
		return (level < 0 ? 1 : level + 1) * 5;
	}


	@Override
	public String name( String weaponName) {
		return String.format( TXT_DISPLACEMENT, weaponName );
	}

	@Override
	public Glowing glowing() {
		return BLUE;
	}
}
