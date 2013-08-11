package com.nsx.ageoftower.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.nsx.ageoftower.utils.Level;

//-- class permettant l'enregistrement de l'�tat du jeu selon un profile choisi
public class ProfileStateHolder {
	private String _profileName;
	private ArrayList<Level> _lvlList;

	public ProfileStateHolder(){
	} 
	
	public String getProfileName() {
		return _profileName;
	}

	public void setProfileName(String _profileName) {
		this._profileName = _profileName;
	}

	public ArrayList<Level> getLvlList() {
		return _lvlList;
	}

	public void setLvlList(ArrayList<Level> _lvlList) {
		this._lvlList = _lvlList;
	}
	
	public ProfileStateHolder(String name){	
		_profileName = name;
		_lvlList = new ArrayList<Level>();
		_lvlList.add(new Level("level1",false)); 
		_lvlList.add(new Level("level2",true));
		_lvlList.add(new Level("level3",true));
		_lvlList.add(new Level("level4",true));
		_lvlList.add(new Level("level5",true));
		_lvlList.add(new Level("level6",true));
		_lvlList.add(new Level("level7",true));
		_lvlList.add(new Level("level8",true));
	}

	public void unlockNextLevel(String name) {
		int i=0;
		while(i<_lvlList.size() && !_lvlList.get(i).getName().equals(name)){
			i++;
		}
		if(i<_lvlList.size())
			_lvlList.get(i+1).unlock();
		this.save();
	}

	private void save() {
		FileHandle pFileHandle = Gdx.files.local("data/Profiles/"+_profileName+".prf");
		Json json = new Json();
		String text = json.prettyPrint(this);
		pFileHandle.writeString(text, false);		
	}
}
